/* ========================================================
 * CreateUserService.java
 *
 * Author:      manamimajumdar
 * Created:     Mar 26, 2011, 9:49:42 AM
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

import Goliath.Applications.Application;
import Goliath.Collections.PropertySet;
import Goliath.Constants.RegularExpression;
import Goliath.Web.WebServices.WebServiceServlet;
import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.Security.ISecurityManager;
import Goliath.Interfaces.Servlets.IServletConfig;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.Security.User;
import java.io.IOException;
import org.w3c.dom.Document;

/**
 * Description: the Create User web service creates a user with the specified
 * information.  This will create the user and log the current session in as the
 * new user.
 *
 * To use this service, the session must not be authenticated
 *
 * The post request data should look like one of the following:
 *
 * * EITHER *
 *  <Data>TmFycmF0aXZlIENhcHR1cmUgUHJvamVjdHRlc3R9fXx7e3Rlc3Q=</Data>
 *  The encoded data should be in the form:
 *  {
 *      DisplayName: "Test",            <!-- If not provided, email address will be used-->
 *      Email: "email@email.com",       <!-- Required-->
 *      UserName: "myUserName",         <!-- If not provided, email address will be used-->
 *      Password: "myPassword"          <!-- Required-->
 *      VerifyEmail: "email@email.com"  <!-- optional-->
 *      VerifyPassword: "myPassword"    <!-- optional-->
 *  }
 *
 *  * WHERE THE ENCODED DATA FITS THE FORM SPECIFIED, OR *
 *
 *  <DisplayName>test</DisplayName>
 *  <Email>email@email.com</Email>
 *  <UserName>myUserName</UserName>
 *  <Password>myPassword</Password>
 *  <VerifyEmail>email@email.com</VerifyEmail>
 *  <VerifyPassword>myPassword</VerifyPassword>
 *
 * This service does not accept get requests, and only aceepts HTTPS
 *
 *
 * @see         Related Class
 * @version     1.0 Mar 26, 2011
 * @author      manamimajumdar
 **/
public class CreateUserForSession extends WebServiceServlet
{

    public CreateUserForSession()
    {
    }

    @Override
    public void onInit(IServletConfig toConfig) throws ServletException
    {
        // TODO: For now, allow not HTTPS for this servlet.  Need to fix the javascript collector login before this can be reinstated
        /*
        clearSupportedMethods();
        addSupportedMethod(RequestMethod.POST());

        clearSupportedProtocols();
        addSupportedProtocol(RequestProtocol.HTTPS());
         *
         */
    }

    /**
     * Do not allow authenticated sessions here
     * @return false
     */
    @Override
    protected boolean allowAuthenticated(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return false;
    }

    @Override
    protected void doPost(IHTTPRequest toRequest, IHTTPResponse toResponse, Document toXML, StringBuilder toBuffer) throws ServletException, IOException
    {
        // TODO: A CreateUser servlet needs to be created specifically for collector
        PropertySet loLoginDetails = Goliath.Web.Security.Utilities.getLoginDetails(toXML);

        // Set up of login details
        if (loLoginDetails != null)
        {
            // If the username is not supplied, use the email address as the username
            if (Goliath.Utilities.isNullOrEmpty(loLoginDetails.<String>getProperty("UserName")))
            {
                loLoginDetails.setProperty("UserName", loLoginDetails.<String>getProperty("Email"));
            }
            else
            {
                // If the username is supplied and the email address is not, then allow creation anyway
                if (Goliath.Utilities.isNullOrEmpty(loLoginDetails.<String>getProperty("Email")))
                {
                    loLoginDetails.setProperty("Email", "anonymous@anonymous.com");
                    loLoginDetails.setProperty("VerifyEmail", loLoginDetails.<String>getProperty("Email"));
                }
            }
        }


        if (loLoginDetails != null &&
                (   !Goliath.Utilities.isNullOrEmpty(loLoginDetails.<String>getProperty("Email")) &&
                    !Goliath.Utilities.isNullOrEmpty(loLoginDetails.<String>getProperty("Password")) &&
                    !Goliath.Utilities.isNullOrEmpty(loLoginDetails.<String>getProperty("UserName")) &&
                    !Goliath.Utilities.isNullOrEmpty(loLoginDetails.<String>getProperty("DisplayName")) &&
                    loLoginDetails.<String>getProperty("Password").equalsIgnoreCase(loLoginDetails.<String>getProperty("VerifyPassword")) &&
                    loLoginDetails.<String>getProperty("Email").equalsIgnoreCase(loLoginDetails.<String>getProperty("VerifyEmail"))
                ))
        {
            // If there is not an email address supplied, then we still want to let the creation through, if there is one, then we need to confirm it is an email address
            if (!Goliath.Utilities.isNullOrEmpty(loLoginDetails.<String>getProperty("Email")) && Goliath.Utilities.getRegexMatcher(RegularExpression.EMAIL_ADDRESS().getValue(), loLoginDetails.<String>getProperty("Email")).matches())
            {
                ISecurityManager loSecurityManager = Application.getInstance().getSecurityManager();
                // Check that the user does not already exist
                String lcUserName = loLoginDetails.<String>getProperty("UserName");
                User loUser = loSecurityManager.getUser(lcUserName);
                if (loUser == null)
                {
                    try
                    {
                        loUser = loSecurityManager.createUser(lcUserName, 
                                loLoginDetails.<String>getProperty("Email"),
                                loLoginDetails.<String>getProperty("Password"),
                                loLoginDetails.<String>getProperty("DisplayName"),
                                loLoginDetails.<String>getProperty(lcUserName));

                        // Sign the user in
                        if (toRequest.getSession().authenticate(loUser, loLoginDetails.<String>getProperty("Password")))
                        {
                            // Write the user information back to the client
                            Goliath.Utilities.appendToStringBuilder(toBuffer,
                                    "<User>",

                                    "<DisplayName>",
                                    loUser.getDisplayName(),
                                    "</DisplayName>",

                                    "<UserName>",
                                    loUser.getName(),
                                    "</UserName>",

                                    "<Guid>",
                                    loUser.getGUID(),
                                    "</Guid>",
                                    
                                    "</User>");
                        }
                        else
                        {
                            addError(toResponse, new Goliath.Exceptions.Exception("User " + lcUserName + " was created, but the server was unable to authenticate session", true));
                        }
                    }
                    catch (Throwable ex)
                    {
                        addError(toResponse, ex);
                    }
                }
                else
                {
                    addError(toResponse, new Goliath.Exceptions.Exception("A user already exists with the user name " + lcUserName + ".", false));
                }
            }
            else
            {
                // We want to log this in case we have any email addresses that fail but are valid
                addError(toResponse, new Goliath.Exceptions.Exception("The value " + loLoginDetails.<String>getProperty("Email") + " does not look like an email address.", true));
            }
        }
        else
        {
            addError(toResponse, new Goliath.Exceptions.Exception("Required fields were not found in the post", true));
        }
    }
}
