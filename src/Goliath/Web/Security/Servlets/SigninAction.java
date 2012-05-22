/* ========================================================
 * SigninAction.java
 *
 * Author:      admin
 * Created:     Dec 31, 2011, 6:42:35 AM
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
 * This action allows a user to sign in to the server
 *
 * @see         Related Class
 * @version     1.0 Dec 31, 2011
 * @author      admin
 **/
public class SigninAction extends IdentityAction
{

    /**
     * Creates a new instance of SigninAction
     */
    public SigninAction()
    {
        super("login");
    }
    
    /**
     * Allows subclassing of this action
     * @param tcValue the unique value of the action
     */
    protected SigninAction(String tcValue)
    {
        super(tcValue);
    }

    @Override
    protected boolean doAction(IHTTPRequest toRequest, IHTTPResponse toResponse, List<Throwable> toErrors)
    {
        return Goliath.Security.Utilities.signinUser(toRequest.getSession(), 
                                                              toRequest.getStringProperty("txtUserName"),
                                                              toRequest.getStringProperty("txtPassword"),
                                                              toErrors);
    }

    @Override
    protected void onActionFailure(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        String lcErrorURL = toRequest.getParameter("errorURL");
        toResponse.sendRedirect(lcErrorURL);
    }

    @Override
    protected void onActionSuccess(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        String lcFwdURL = toRequest.getParameter("forwardToURL");
        toResponse.sendRedirect(lcFwdURL);
    }


    @Override
    public boolean allowAuthenticated(IHTTPRequest toRequest, IHTTPResponse toResponse)
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
    public boolean requiresAuthentication(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return false;
    }
    
    @Override
    public boolean useForwarding(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return true;
    }
    
    
}
