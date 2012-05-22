/* ========================================================
 * ChangePasswordAction.java
 *
 * Author:      admin
 * Created:     Dec 31, 2011, 6:47:01 AM
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

import Goliath.Collections.List;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.Web.Constants.RequestMethod;
import Goliath.Web.Constants.RequestProtocol;

/**
 * Changes the password for the current user, the user must know the old password
 * in order to change to the new password.  The current session must be logged in already.
 *
 * The action expects three parameters, txtOldPassword which is the old password for the user,
 * and txtNewPassword which contains the new password and txtConfirmPassword which is the confirmation
 * of the new password.
 *
 * It is also possible to send this data as base64 encoded data in as the parameter
 * encodedData.  
 *
 * This Action only supports HTTPS
 *
 * @see         Related Class
 * @version     1.0 Dec 31, 2011
 * @author      admin
 **/
public class ChangePasswordAction extends IdentityAction
{

    /**
     * Creates a new instance of SigninAction
     */
    public ChangePasswordAction()
    {
        super("changePassword");
    }
    
    /**
     * Allows subclassing of this action
     * @param tcValue the unique value of the action
     */
    protected ChangePasswordAction(String tcValue)
    {
        super(tcValue);
    }

    @Override
    protected boolean doAction(IHTTPRequest toRequest, IHTTPResponse toResponse, List<Throwable> toErrors)
    {
        // Make sure the passwords match
        String lcNewPassword = toRequest.getStringProperty("txtNewPassword");
        String lcConfirmPassword = toRequest.getStringProperty("txtConfirmPassword");
        String lcOldPassword = toRequest.getStringProperty("txtPassword");
        if (lcNewPassword.equals(lcConfirmPassword))
        {
            if (!lcOldPassword.equals(lcNewPassword))
            {
                return Goliath.Security.Utilities.changeUserPassword(toRequest.getSession(),
                                                                  lcOldPassword,
                                                                  lcNewPassword,
                                                                  toErrors);
            }
            else
            {
                toResponse.addError(new Goliath.Exceptions.Exception("The old and new passwords are the same value", false));
            }
        }
        else
        {
            toResponse.addError(new Goliath.Exceptions.Exception("The passwords do not match", false));
        }
        return false;
    }

    @Override
    public boolean allowAuthenticated(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return true;
    }
    
    @Override
    public boolean requiresAuthentication(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return true;
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
