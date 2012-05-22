/* =========================================================
 * HTTPHandler.java
 *
 * Author:      kmchugh
 * Created:     27-Feb-2008, 15:42:34
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
 * =======================================================*/
package Goliath.Web;

import Goliath.Applications.Application;
import Goliath.Collections.List;
import Goliath.Commands.ExecuteServletCommand;
import Goliath.Constants.LogType;
import Goliath.Web.Servlets.ServletCommandArgs;
import Goliath.Constants.MimeType;
import Goliath.Interfaces.Servlets.IRequestDispatcher;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 27-Feb-2008
 * @author      kmchugh
 **/
public final class LightHTTPHandler extends Goliath.Object
        implements com.sun.net.httpserver.HttpHandler
{
    private static ServerConnection g_oForwardingConnection = null;
    private static boolean g_lCheckedServerConnection;

    // TODO: This should be factored out into a proxy http handler
    private static ServerConnection getForwardingConnection()
    {
        if (!g_lCheckedServerConnection && g_oForwardingConnection == null)
        {
            g_lCheckedServerConnection = true;
            try
            {
                ServerConnection loConnection = Application.getInstance().<ServerConnection>getPropertyHandlerProperty("WebServer.ForwardingServer");
                g_oForwardingConnection = loConnection;
            }
            catch (Throwable ex)
            {
                Application.getInstance().log(ex);
            }
        }
        return g_oForwardingConnection;
    }


    /** Creates a new instance of HTTPHandler */
    public LightHTTPHandler()
    {
    }

    /**
     * Gets the list of contexts for this handler
     * @return the list of contexts that this will handle
     */
    public List<String> getContexts()
    {
        return new List<String>(new String[]{"/"});
    }

    /**
     * Handles a request from the HttpExchange
     * @param toHttpExchange the HTTPExchange object to handle
     * @throws java.io.IOException
     */
    @Override
    public final void handle(HttpExchange toHttpExchange) throws IOException
    {
        // Set up the request
        IHTTPRequest loRequest = new LightHTTPRequest(toHttpExchange);

        // Set up the response
        IHTTPResponse loResponse = new LightHTTPResponse(toHttpExchange, loRequest);
        
        // Allow the session to renew
        loRequest.getSession().renew();

        try
        {
            Application.getInstance().log("Servicing " + loRequest.getMethod().toString() +  " request for " + loRequest.getPath() +  loRequest.getFile(), LogType.DEBUG());

            IRequestDispatcher loServlet = loRequest.getRequestDispatcher(loRequest.getPath() + (loRequest.getPath().endsWith("/") ? "" : "/") + loRequest.getFile());
            if (loServlet != null)
            {
                ExecuteServletCommand loCommand = new ExecuteServletCommand(new ServletCommandArgs(loServlet.getServlet(), loRequest, loResponse), loRequest.getSession());
                Application.getInstance().log("Using Servlet " + loServlet.getServlet().getClass().getName() + " to service request for " + loRequest.getPath(), LogType.DEBUG());
            }
            else
            {
                Application.getInstance().log("Unable to get servlet for Servicing request " + loRequest.getPath());
            }
        }
        catch(Throwable loException)
        {
            loResponse.addError(loException);
            loResponse.sendResponseHeaders();
            Application.getInstance().log(loException);
        }
    }
    
    
    /**
     * Sets the response headers
     * @param loRequest
     * @param loResponse
     */
    protected void setResponseHeaders(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        // TODO: Use an enumerated type for response headers
        toResponse.setContentType(MimeType.TEXT_HTML());
    }
}
