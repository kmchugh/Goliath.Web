/* ========================================================
 * GSPMimeTypeManager.java
 *
 * Author:      admin
 * Created:     Oct 11, 2011, 6:12:12 PM
 *
 * Description
 * --------------------------------------------------------
 * General Class Description.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * ===================================================== */
package Goliath.Web.Servlets.MimeTypeManager;

import Goliath.Collections.HashTable;
import Goliath.Collections.List;
import Goliath.Collections.PropertySet;
import Goliath.Constants.MimeType;
import Goliath.Exceptions.FileNotFoundException;
import Goliath.Interfaces.Servlets.IRequestDispatcher;
import Goliath.Interfaces.Servlets.IServlet;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.UI.GSPFile;
import java.io.File;
import org.w3c.dom.Document;

/**
 * The GSP MimeTypeManager will take care of transforming .gsp file to HTML files
 * 
 * Tags that are processed by the GSPMimeTypeManager are:
 * import - inclusion of additional .xslt files
 *
 * @see         Related Class
 * @version     1.0 Oct 11, 2011
 * @author      admin
 **/
public class GSPMimeTypeManager extends MimeTypeManager
{
    private static HashTable<Goliath.IO.File, GSPFile> g_oGSPs;
    
    /**
     * Gets the gspfile for the file specified
     * @param toFile the file
     * @return the GSPFile processor object
     */
    private static GSPFile getGSPFile(Goliath.IO.File toFile)
    {
        if (g_oGSPs == null)
        {
            g_oGSPs = new HashTable<Goliath.IO.File, GSPFile>();
        }
        
        if (g_oGSPs.containsKey(toFile))
        {
            return g_oGSPs.get(toFile);
        }
        
        g_oGSPs.put(toFile, new GSPFile(toFile));
        return g_oGSPs.get(toFile);
    }
    
    /**
     * Creates a .gsp in memory from the xml provided
     * @param toXML the xml that contains the gsp data
     * @return a reference to a .gsp file
     */
    private static GSPFile createGSPFile(Document toXML)
    {
        try
        {
            return new GSPFile(toXML);
        }
        catch (FileNotFoundException ex)
        {
            return null;
        }
    }
    
    
    /**
     * This mime type manager only supports gsp files
     * @return the list of supported file types
     */
    @Override
    protected List<String> getSupportedMimeTypes()
    {
        return new List<String>(new String[]{MimeType.APPLICATION_GOLIATH_SERVER_PAGE().getValue()});
    }

    /**
     * This mime type manager supports compression
     * @return true
     */
    @Override
    public boolean supportsCompression()
    {
        return true;
    }

    @Override
    protected boolean canCache(Goliath.IO.File toFile, IHTTPResponse toResponse)
    {
        return false;
    }
    
    
    
    /**
     * This Manager will only create HTML files
     * @param toFile the file that is going to be sent to the client
     * @return the mime type to specify for the file
     */
    @Override
    public MimeType getMimeType(Goliath.IO.File toFile, IHTTPResponse toResponse)
    {
        return MimeType.TEXT_HTML();
    }
    
    /**
     * Takes a list of servlet contexts and returns a list if IServlets for processing.
     * If a Servlet is not found then it is simply not added to the list, no errors are thrown
     * @param toServletContext the List of servlet contexts to get Servlets for
     * @param toRequest the request object used to retrieve the contexts
     * @return the list of servlets, or an empty list.  This will never return null.
     */
    private List<IServlet> getServlets(List<String> toServletContext, IHTTPRequest toRequest)
    {
        List<IServlet> loReturn = new List<IServlet>();
        for (String lcClass : toServletContext)
        {
            IRequestDispatcher loServlet = toRequest.getRequestDispatcher(lcClass);
            if (loServlet != null && !loReturn.contains(loServlet.getServlet()))
            {
                loReturn.add(loServlet.getServlet());
            }
        }
        return loReturn;
    }
    
    /**
     * Processes the document passed as a GSP
     * @param toGSP the xml document containing the gsp data
     * @param toRequest the request object
     * @param toResponse the response object
     */
    public void process(Document toGSP, IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        GSPFile loGSPFile = createGSPFile(toGSP);
        process(loGSPFile, toRequest, toResponse);
    }
    
    /**
     * Processes the GSP File specified
     * @param toGSP the GSP file
     * @param toRequest the request
     * @param toResponse the response
     */
    private void process(GSPFile toGSP, IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        List<Throwable> loErrors = new List<Throwable>();
        try
        {
            // Process any servlets that are part of the GSP to extract parameter values
            List<IServlet> loServlets = getServlets(toGSP.getServlets(loErrors), toRequest);
            
            // No point in executing the servlets if there were errors
            if (loErrors.size() == 0)
            {
                for (IServlet loServlet : loServlets)
                {
                    loServlet.service(toRequest, toResponse);
                }
            }
            
            // At this point the GSP is prepared, and we have all the available parameters, so get the processed file
            PropertySet loProperties = new PropertySet(toRequest.getParameterMap());
            for (String lcProperty : toGSP.getParameters())
            {
                loProperties.setProperty(lcProperty, null);
            }
            Goliath.IO.File loProcessedFile = toGSP.getProcessedFile(Goliath.Web.Utilities.populatePropertyHash(loProperties, toRequest), loErrors);
            
            if (loErrors.size() == 0)
            {
                setDefaultHeaders(toResponse, loProcessedFile, getMimeType(loProcessedFile, toResponse));
                writeFileToResponse(loProcessedFile, toResponse);
            }
        }
        catch (Throwable ex)
        {
            loErrors.add(ex);
            writeError(ex, toResponse);
        }
        
        // If there were errors, we write them out
        for (Throwable loError : loErrors)
        {
            writeError(loError, toResponse);
        }
    }

    @Override
    public void process(File toFile, IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        try
        {
            GSPFile loGSPFile = getGSPFile(new Goliath.IO.File(toFile));
            process(loGSPFile, toRequest, toResponse);
        }
        catch (Throwable ex)
        {
            writeError(ex, toResponse);
        }
    }
    
}
