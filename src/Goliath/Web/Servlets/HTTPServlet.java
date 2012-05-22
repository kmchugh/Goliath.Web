/* ========================================================
 * HTTPServlet.java
 *
 * Author:      kenmchugh
 * Created:     Mar 13, 2011, 11:55:23 AM
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
package Goliath.Web.Servlets;

import Goliath.Applications.Application;
import Goliath.Constants.MimeType;
import Goliath.Exceptions.InvalidMethodException;
import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.Web.Constants.RequestMethod;
import Goliath.Web.Constants.ResultCode;
import java.io.IOException;
import org.w3c.dom.Document;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Mar 13, 2011
 * @author      kenmchugh
 **/
public abstract class HTTPServlet extends Servlet
{
    /**
     * Creates a new instance of HTTPServlet
     */
    public HTTPServlet()
    {
    }


    /**
     * Executes the correct function based on the method
     * @param toMethod this request is being executed for
     * @param toRequest the request to process
     * @param toResponse the response
     * @throws ServletException if there is an error
     * @throws IOException if there is a read/write error
     */
    @Override
    protected void doService(RequestMethod toMethod, IHTTPRequest toRequest, IHTTPResponse toResponse) throws ServletException, IOException
    {
        doSetup(toMethod, toRequest, toResponse);
        
        if (toMethod == RequestMethod.GET())
        {
            doGet(toRequest, toResponse);
        }
        else if (toMethod == RequestMethod.POST())
        {
            try
            {
                Document loXML = null;
                if (toRequest.getContentType() == MimeType.APPLICATION_XML())
                {
                    loXML = Goliath.XML.Utilities.toXML(toRequest.getBody());
                }
                doPost(toRequest, toResponse, loXML);
            }
            catch(Throwable ex)
            {
                Application.getInstance().log(ex);
                Application.getInstance().log(toRequest.getBody());
                addError(toResponse, ex);
            }
        }
        else if (toMethod == RequestMethod.PUT())
        {
            try
            {
                Document loXML = null;
                if (toRequest.getContentType() == MimeType.APPLICATION_XML())
                {
                    loXML = Goliath.XML.Utilities.toXML(toRequest.getBody());
                }
                doPut(toRequest, toResponse, loXML);
            }
            catch(Throwable ex)
            {
                Application.getInstance().log(ex);
                Application.getInstance().log(toRequest.getBody());
                addError(toResponse, ex);
            }
        }
        else if (toMethod == RequestMethod.DELETE())
        {
            doDelete(toRequest, toResponse);
        }
        else if (toMethod == RequestMethod.OPTIONS())
        {
            doOptions(toRequest, toResponse);
        }
        else if (toMethod == RequestMethod.HEAD())
        {
            doHead(toRequest, toResponse);
        }
        else if (toMethod == RequestMethod.TRACE())
        {
            doTrace(toRequest, toResponse);
        }
        else if (toMethod == RequestMethod.CONNECT())
        {
            doConnect(toRequest, toResponse);
        }
        else
        {
            addAllowHeaders(toResponse);
            throw new InvalidMethodException(toRequest);
        }

        doCleanup(toMethod, toRequest, toResponse);
    }

    /**
     * Executes the OPTIONS method of the web server
     * @param toRequest the request
     * @param toResponse the response
     * @throws ServletException if there is an error
     * @throws IOException if there is a read/write error
     */
    protected void doOptions(IHTTPRequest toRequest, IHTTPResponse toResponse)
            throws ServletException, IOException
    {
        addAllowHeaders(toResponse);
        toResponse.setResultCode(ResultCode.OK());
    }

    /**
     * The GET request processing, at a minimum all sub classes should override this method
     * @param toRequest the request
     * @param toResponse the response
     * @throws ServletException if there is an error
     * @throws IOException if there is a read/write error
     */
    protected void doGet(IHTTPRequest toRequest, IHTTPResponse toResponse)
            throws ServletException, IOException
    {
        
    }



    protected void doPost(IHTTPRequest toRequest, IHTTPResponse toResponse, Document toXML)
            throws ServletException, IOException
    {
        // By Default, just to the get
        doGet(toRequest, toResponse);
    }

    protected void doPut(IHTTPRequest toRequest, IHTTPResponse toResponse, Document toXML)
            throws ServletException, IOException
    {
    }

    protected void doDelete(IHTTPRequest toRequest, IHTTPResponse toResponse)
            throws ServletException, IOException
    {
    }

    protected void doHead(IHTTPRequest toRequest, IHTTPResponse toResponse)
            throws ServletException, IOException
    {
    }

    protected void doTrace(IHTTPRequest toRequest, IHTTPResponse toResponse)
            throws ServletException, IOException
    {
    }

    protected void doConnect(IHTTPRequest toRequest, IHTTPResponse toResponse)
            throws ServletException, IOException
    {
    }

    /**
     * Executed before doService, this is where any setup for the session should be done
     * @param toMethod the request method
     * @param toRequest the request
     * @param toResponse the response
     * @throws ServletException if there is an error processing the servlet
     * @throws IOException if there is an io exception while processing
     */
    protected void doSetup(RequestMethod toMethod, IHTTPRequest toRequest, IHTTPResponse toResponse)
            throws ServletException, IOException
    {

    }

    /**
     * Executed after doService, this is where any cleanup for the session should be done
     * @param toMethod the request method
     * @param toRequest the request
     * @param toResponse the response
     * @throws ServletException if there is an error processing the servlet
     * @throws IOException if there is an io exception while processing
     */
    protected void doCleanup(RequestMethod toMethod, IHTTPRequest toRequest, IHTTPResponse toResponse)
            throws ServletException, IOException
    {

    }
}
