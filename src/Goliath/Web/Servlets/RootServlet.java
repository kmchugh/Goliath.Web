/* ========================================================
 * RootServlet.java
 *
 * Author:      kenmchugh
 * Created:     Mar 13, 2011, 11:54:29 AM
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

import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.Servlets.IServletConfig;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.Web.Servlets.MimeTypeManager.MimeTypeManager;
import java.io.IOException;


        
/**
 * This Servlet handles all requests at the root level /
 *
 * If a context is not handled it will eventually drop down to this level
 *
 * @see         Related Class
 * @version     1.0 Mar 13, 2011
 * @author      kenmchugh
**/
public class RootServlet extends HTTPServlet
{
    /**
     * Creates a new instance of RootServlet
     */
    public RootServlet()
    {
    }

    @Override
    public void onInit(IServletConfig toConfig) throws ServletException
    {
    }
    
    @Override
    protected void doGet(IHTTPRequest toRequest, IHTTPResponse toResponse) throws ServletException, IOException
    {
        // As this is a file request ask the mime type manager to deal with this request
        MimeTypeManager.getInstance().process(toRequest, toResponse, this);
    }

    @Override
    protected String getDefaultContext()
    {
        return "/";
    }
}
