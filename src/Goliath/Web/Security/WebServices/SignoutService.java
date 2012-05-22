/* ========================================================
 * LogoutService.java
 *
 * Author:      manamimajumdar
 * Created:     Mar 26, 2011, 3:38:34 PM
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
package Goliath.Web.Security.WebServices;

import Goliath.Collections.List;
import Goliath.Web.WebServices.WebServiceServlet;
import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.ISession;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.Security.User;
import java.io.IOException;

/**
 * Description: the Sign in web service logs a user in
 *
 * To use this service, the session must be authenticated
 *
 * No parameters or data is expected by this servlet
 *
 *
 * This service accepts any protocol and get and post methods as passwords are not transferred
 *
 *
 * @see         Related Class
 * @version     1.0 Mar 26, 2011
 * @author      manamimajumdar
 **/
public class SignoutService extends WebServiceServlet
{

    public SignoutService()
    {
    }

    /**
     * This requires an authenticated session
     * @return true
     */
    @Override
    protected boolean requiresAuthentication(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return true;
    }
    
    @Override
    protected void doGet(IHTTPRequest toRequest, IHTTPResponse toResponse, StringBuilder toBuffer) throws ServletException, IOException
    {
        ISession loSession = toResponse.getSession();

        if (Goliath.Security.Utilities.signoutUser(loSession, new List<Throwable>()))
        {
            User loUser = loSession.getUser();
            // Write the user information back to the client
            appendObjectToResponse(toResponse, toBuffer, loUser);
        }
    }
}
