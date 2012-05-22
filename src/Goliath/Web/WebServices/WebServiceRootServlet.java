/* ========================================================
 * WebServiceRootServlet.java
 *
 * Author:      kenmchugh
 * Created:     Mar 14, 2011, 8:54:47 AM
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

package Goliath.Web.WebServices;

import Goliath.Applications.Application;
import Goliath.Collections.List;
import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.Web.Constants.ResultCode;
import java.io.IOException;


        
/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Mar 14, 2011
 * @author      kenmchugh
**/
public class WebServiceRootServlet extends WebServiceServlet
{
    
    // TODO: Implement a last accessed method on the servlets to monitor usage
    private static List<WebServiceServlet> g_oServlets;
    
    /**
     * Creates a new instance of WebServiceRootServlet
     */
    public WebServiceRootServlet()
    {
    }
    
    /**
     * 
     */
    private List<WebServiceServlet> getServlets()
    {
        if (g_oServlets == null)
        {
            g_oServlets = new List<WebServiceServlet>();
            
            List<Class<WebServiceServlet>> loWebServices = Application.getInstance().getObjectCache().getClasses(WebServiceServlet.class);
            for (Class<WebServiceServlet> loServletClass : loWebServices)
            {
                try
                {
                    WebServiceServlet loServlet = loServletClass.newInstance();
                    g_oServlets.add(loServlet);
                }
                catch (Throwable ex)
                {
                    Application.getInstance().log(ex);
                }
            }
        }
        return g_oServlets;
    }

    
    
    
    
    
    /*
     * for (Class<WebServiceServlet> loService : loWebServices)
        {
            Goliath.Utilities.appendToStringBuilder(toBuilder,
                    "<Service>",
                    "<Name>",
                    loService.getSimpleName(),
                    "</Name>");
            
            try
            {
                WebServiceServlet loServlet = loService.newInstance();
                Goliath.Utilities.appendToStringBuilder(toBuilder,
                        "<Description>",
                        loServlet.getServletInfo(),
                        "</Description>",
                        "<Context>",
                        loServlet.getServletConfig().getServletContext().getURLPattern(),
                        "</Context>");
            }
            catch (Throwable ex)
            {
                Application.getInstance().log(ex);
            }

            Goliath.Utilities.appendToStringBuilder(toBuilder,
                    "</Service>");
        }
        toBuilder.append("</List>");
     */
    @Override
    protected void doGet(IHTTPRequest toRequest, IHTTPResponse toResponse, StringBuilder toBuilder)
            throws ServletException, IOException
    {
        // If this path is not the root path, then there was an error and the original path could not be found.
        if (!toRequest.getPath().equalsIgnoreCase(WebServiceServlet.getContextString()))
        {
            toResponse.setResultCode(ResultCode.NOT_FOUND());
        }
        
        // Write out the list
        appendObjectToResponse(toResponse, toBuilder, getServlets());
    }

    /**
     * Gets the default context for this
     * @return
     */
    @Override
    protected String onGetDefaultContext()
    {
        return "";
    }
}
