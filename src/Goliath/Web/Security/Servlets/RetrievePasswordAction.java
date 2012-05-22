/* ========================================================
 * RetrievePasswordAction.java
 *
 * Author:      admin
 * Created:     Dec 31, 2011, 6:54:10 AM
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
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.Security.User;
import Goliath.Web.Constants.RequestMethod;
import Goliath.Web.Constants.RequestProtocol;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Dec 31, 2011
 * @author      admin
 **/
public class RetrievePasswordAction extends IdentityAction
{

    /**
     * Creates a new instance of SigninAction
     */
    public RetrievePasswordAction()
    {
        super("retrievePassword");
    }
    
    /**
     * Allows subclassing of this action
     * @param tcValue the unique value of the action
     */
    protected RetrievePasswordAction(String tcValue)
    {
        super(tcValue);
    }

    @Override
    protected boolean doAction(IHTTPRequest toRequest, IHTTPResponse toResponse, List<Throwable> toErrors)
    {
        StringBuilder loRetrievedPassword = new StringBuilder();
        String lcUserName = toRequest.getStringProperty("txtUserName");
        String lcSecurityQuestion = toRequest.getStringProperty("txtQuestion");
        String lcSecurityAnswer = toRequest.getStringProperty("txtAnswer");
        
        // TODO: Implement usage of security questions if we do not have an email address
        if (Goliath.Security.Utilities.resetUserPassword(toRequest.getSession(), lcUserName, loRetrievedPassword, toErrors))
        {
            User loUser = Application.getInstance().getSecurityManager().getUser(lcUserName);
            onPasswordChanged(toRequest, toResponse, loUser, loRetrievedPassword.toString(), toErrors);
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
    protected boolean onPasswordChanged(IHTTPRequest toRequest, IHTTPResponse toResponse, User toUser, String tcPassword, List<Throwable> toErrors)
    {
        // TODO: We were able to generate a new password, now we write it back to the user
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
