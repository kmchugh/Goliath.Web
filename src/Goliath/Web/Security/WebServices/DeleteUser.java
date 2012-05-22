/* ========================================================
 * DeleteUserService.java
 *
 * Author:      manamimajumdar
 * Created:     Mar 26, 2011, 3:45:34 PM
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
import Goliath.Collections.PropertySet;
import Goliath.Web.WebServices.WebServiceServlet;
import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.Servlets.IServletConfig;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.JSON.JSON;
import Goliath.Security.User;
import Goliath.Web.Constants.RequestMethod;
import Goliath.Web.Constants.RequestProtocol;
import java.io.IOException;
import org.w3c.dom.Document;

/**
 * Description: the Delete User web service will delete the specified user, the
 * user must be the user that is currently logged in (you can only delete yourself
 * through this service).  This will log the user out, delete the user, then assign
 * a new anonymous user to the session
 *
 *
 * To use this service, the session must be authenticated
 *
 * The post request data should look like one of the following:
 *
 * <User encoded="true">
 *  <Data>TmFycmF0aXZlIENhcHR1cmUgUHJvamVjdHRlc3R9fXx7e3Rlc3Q=</Data>
 * </User>
 * The encoded data should be in the form:
 * {
 *      UserName: "myUserName",     <!-- Required-->
 *      Password: "myPassword"      <!-- Required-->
 * }
 *
 * <User encoded="true">
 *  <UserName>myUserName</UserName>
 *  <Password>myPassword</Password>
 * </User>
 *
 * This service does not accept get requests, and only aceepts HTTPS
 *
 *
 * @see         Related Class
 * @version     1.0 Mar 26, 2011
 * @author      manamimajumdar
 **/
public class DeleteUser extends WebServiceServlet
{

    public DeleteUser()
    {
    }

    /**
     * This servlet requires an authenticated session
     * @return true
     */
    @Override
    protected final boolean requiresAuthentication(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return true;
    }

    @Override
    public final void onInit(IServletConfig toConfig) throws ServletException
    {
        clearSupportedMethods();
        addSupportedMethod(RequestMethod.POST());

        clearSupportedProtocols();
        addSupportedProtocol(RequestProtocol.HTTPS());
    }

    @Override
    protected void doPost(IHTTPRequest toRequest, IHTTPResponse toResponse, Document toXML, StringBuilder toBuffer) throws ServletException, IOException
    {
        PropertySet loLoginDetails = Goliath.Web.Security.Utilities.getLoginDetails(toXML);
        if (loLoginDetails != null
                && !Goliath.Utilities.isNullOrEmpty(loLoginDetails.<String>getProperty("Password")))
        {
            User loUser = toRequest.getSession().getUser();
            String lcPassword = loLoginDetails.<String>getProperty("Password");
            
            Goliath.Security.Utilities.deleteUser(toRequest.getSession(), lcPassword, new List<Throwable>());
        }

        
    }

    @Override
    protected void doPost(IHTTPRequest toRequest, IHTTPResponse toResponse, JSON toJSON, StringBuilder toBuffer) throws ServletException, IOException
    {
            User loUser = toRequest.getSession().getUser();
            String lcPassword = toJSON.get("Password").getStringValue();

            Goliath.Security.Utilities.deleteUser(toRequest.getSession(), lcPassword, new List<Throwable>());
    }

    
}
