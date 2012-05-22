/* ========================================================
 * RegisterAction.java
 *
 * Author:      admin
 * Created:     Dec 31, 2011, 6:53:43 AM
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
package Goliath.Web.Security.Servlets;

import Goliath.Applications.Application;
import Goliath.Collections.List;
import Goliath.Exceptions.InvalidOperationException;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.Security.User;
import Goliath.Web.Constants.RequestMethod;
import Goliath.Web.Constants.RequestProtocol;

/**
 * Allows a user to register with the system
 *
 * @see         Related Class
 * @version     1.0 Dec 31, 2011
 * @author      admin
 **/
public class RegisterAction extends IdentityAction
{

    /**
     * Creates a new instance of SigninAction
     */
    public RegisterAction()
    {
        super("register");
    }
    
    /**
     * Allows subclassing of this action
     * @param tcValue the unique value of the action
     */
    protected RegisterAction(String tcValue)
    {
        super(tcValue);
    }

    @Override
    protected boolean doAction(IHTTPRequest toRequest, IHTTPResponse toResponse, List<Throwable> toErrors)
    {
        // Make sure the passwords match
        String lcConfirmPassword = toRequest.getStringProperty("txtConfirmPassword");
        String lcPassword = toRequest.getStringProperty("txtPassword");
        String lcUserName = toRequest.getStringProperty("txtUserName");
        String lcEmail = toRequest.getStringProperty("txtEmail");
        String lcVerifyEmail = toRequest.getStringProperty("txtVerifyEmail");
        StringBuilder loGeneratedPassword = new StringBuilder();
        
        if (!Goliath.Utilities.isNullOrEmpty(lcEmail) && lcEmail.equalsIgnoreCase(lcVerifyEmail))
        {
            // Create the user with the email address
            Goliath.Security.Utilities.registerUserFromEmail(toRequest.getSession(), lcUserName, lcEmail, loGeneratedPassword, toErrors);
        }
        else if (!Goliath.Utilities.isNullOrEmpty(lcUserName) && !Goliath.Utilities.isNullOrEmpty(lcPassword) &&
                lcPassword.equals(lcConfirmPassword))
        {
            // Create the user with a user name and password
            Goliath.Security.Utilities.registerUserFromUserName(toRequest.getSession(), lcUserName, lcUserName, lcEmail, lcPassword, toErrors);
        }
        else
        {
            // Invalid parameters
            toErrors.add(new InvalidOperationException("Some of the required parameters are incorrect or not provided", false));
        }
        
        if (toErrors.size() == 0)
        {
            User loUser = Application.getInstance().getSecurityManager().getUser(lcUserName);
            if (!onUserRegistered(toRequest, toResponse, loUser, loGeneratedPassword.length() == 0 ? lcPassword : loGeneratedPassword.toString(), toErrors))
            {
                toRequest.getSession().unauthenticate();
                // If the hook returned false then something was wrong with the user, so delete them.
                Application.getInstance().getSecurityManager().deleteUser(loUser);
            }
        }
        
        return toErrors.size() == 0;
    }
    
    /**
     * Hook method to allow interaction with the user after it has been registered and created
     * @param toRequest the request
     * @param toResponse the response
     * @param toUser the user just created
     * @param tcPassword the password for that user
     * @return true if the user is okay, false indicates the user needs to be deleted
     */
    protected boolean onUserRegistered(IHTTPRequest toRequest, IHTTPResponse toResponse, User toUser, String tcPassword, List<Throwable> toErrors)
    {
        return true;
    }

    @Override
    public boolean allowAuthenticated(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return false;
    }
    
    @Override
    public boolean requiresAuthentication(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return false;
    }
    
    @Override
    public boolean isMethodSupported(RequestMethod toMethod, IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return toMethod.equals(RequestMethod.POST());
    }

    @Override
    public boolean isProtocolSupported(RequestProtocol toProtocol, IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return toProtocol.equals(RequestProtocol.HTTPS());
    }
    
    @Override
    public boolean useForwarding(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return true;
    }
}