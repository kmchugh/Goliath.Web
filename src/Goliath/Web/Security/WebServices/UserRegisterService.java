/* ========================================================
 * UserRegisterService.java
 *
 * Author:      manamimajumdar
 * Created:     Sep 16, 2011, 6:00:57 PM
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
import Goliath.Exceptions.InvalidOperationException;
import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.Servlets.IServletConfig;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.Web.Constants.RequestMethod;
import Goliath.Web.WebServices.WebServiceServlet;
import Goliath.JSON.JSON;
import java.io.IOException;
import org.w3c.dom.Document;

/**
 * Description: the UserRegisterService web service creates a user with the specified
 *
 * To use this service, the session must not be authenticated
 *
 * The post request data should look like one of the following:
 *
 * * EITHER *
 *  <Data>TmFycmF0aXZlIENhcHR1cmUgUHJvamVjdHRlc3R9fXx7e3Rlc3Q=</Data>
 *  The encoded data should be in the form:
 *  {
 *      EmailRequired: "true",          <!-- If true, email address will be used, If false,Username/Password will be used -->
 *      DisplayName: "Test",            <!-- Optional, If provided, add the information -->
 *      Email: "email@email.com",       <!-- Required if flag is true-->
 *      UserName: "myUserName",         <!-- Required if flag is false-->
 *      Password: "myPassword"          <!-- Required if flag is false-->
 *      VerifyEmail: "email@email.com"  <!-- optional-->
 *      VerifyPassword: "myPassword"    <!-- optional-->
 *  }
 *
 *  * WHERE THE ENCODED DATA FITS THE FORM SPECIFIED, OR *
 *
 *  <EmailRequired>true</EmailRequired>
 *  <DisplayName>test</DisplayName>
 *  <Email>email@email.com</Email>
 *  <UserName>myUserName</UserName>
 *  <Password>myPassword</Password>
 *  <VerifyEmail>email@email.com</VerifyEmail>
 *  <VerifyPassword>myPassword</VerifyPassword>
 *
 * This service does not accept get requests.
 *
 *
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Sep 16, 2011
 * @author      manamimajumdar
 **/
public class UserRegisterService extends WebServiceServlet
{

    /**
     * Creates a new instance of UserRegisterService
     */
    public UserRegisterService()
    {
    }

    @Override
    public final void onInit(IServletConfig toConfig) throws ServletException
    {
        clearSupportedMethods();
        addSupportedMethod(RequestMethod.POST());

    }

    @Override
    protected boolean allowAuthenticated(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return false;
    }

    @Override
    protected void doPost(IHTTPRequest toRequest, IHTTPResponse toResponse, Document toXML, StringBuilder toBuffer) throws ServletException, IOException
    {

        if (toXML != null)
        {
            String lcEmail;
            String lcVerifyEmail;
            String lcUserName;
            String lcPassword;
            String lcVerifyPassword;
            String lcDisplayName;

            PropertySet loLoginDetails = toXML == null ? new PropertySet() : Goliath.Web.Security.Utilities.getLoginDetails(toXML);
            lcEmail = loLoginDetails.getProperty("Email");
            lcVerifyEmail = loLoginDetails.getProperty("VerifyEmail");
            lcUserName = loLoginDetails.getProperty("UserName");
            lcPassword = loLoginDetails.getProperty("Password");
            lcVerifyPassword = loLoginDetails.getProperty("VerifyPassword");
            lcDisplayName = loLoginDetails.getProperty("DisplayName");

            createUser( lcEmail, lcVerifyEmail, lcUserName, lcPassword, lcVerifyPassword, lcDisplayName, toBuffer, toRequest, toResponse);

        }

    }

    @Override
    protected void doPost(IHTTPRequest toRequest, IHTTPResponse toResponse, JSON toJSON, StringBuilder toBuffer) throws ServletException, IOException
    {

        if (toJSON != null)
        {

            String lcEmail;
            String lcVerifyEmail;
            String lcUserName;
            String lcPassword;
            String lcVerifyPassword;
            String lcDisplayName;

            lcEmail = toJSON.get("Email").getStringValue();
            lcVerifyEmail = toJSON.get("VerifyEmail").getStringValue();
            lcUserName = toJSON.get("UserName").getStringValue();
            lcPassword = toJSON.get("Password").getStringValue();
            lcVerifyPassword = toJSON.get("VerifyPassword").getStringValue();
            lcDisplayName = toJSON.get("DisplayName").getStringValue();

            createUser(lcEmail,lcVerifyEmail, lcUserName, lcPassword, lcVerifyPassword, lcDisplayName, toBuffer, toRequest, toResponse);

        }
    }

    private void createUser(String tcEmail, String tcVerifyEmail, String tcUserName, String tcPassword, String tcVerifyPassword, String tcDisplayName, StringBuilder toBuffer, IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        List<Throwable> loErrors = new List<Throwable>();
        StringBuilder loGeneratedPassword = new StringBuilder();
        
        if (!Goliath.Utilities.isNullOrEmpty(tcEmail) && tcEmail.equalsIgnoreCase(tcVerifyEmail))
        {
            // Create the user with the email address
            Goliath.Security.Utilities.registerUserFromEmail(toRequest.getSession(), tcUserName, tcEmail, loGeneratedPassword, loErrors);
        }
        else if (!Goliath.Utilities.isNullOrEmpty(tcUserName) && !Goliath.Utilities.isNullOrEmpty(tcPassword) &&
                tcPassword.equals(tcVerifyPassword))
        {
            // Create the user with a user name and password
            Goliath.Security.Utilities.registerUserFromUserName(toRequest.getSession(), tcUserName, tcUserName, tcEmail, tcPassword, loErrors);
        }
        else
        {
            // Invalid parameters
            loErrors.add(new InvalidOperationException("Some of the required parameters are incorrect or not provided", false));
        }
        
        for (Throwable loError : loErrors)
        {
            toResponse.addError(loError);
        }
    }
}
