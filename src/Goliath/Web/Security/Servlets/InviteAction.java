/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Goliath.Web.Security.Servlets;

import Goliath.Collections.List;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.Web.Constants.RequestMethod;
import Goliath.Web.Constants.RequestProtocol;

/**
 * Allows the users to be invited as a member of the system
 * @author admin
 */
public class InviteAction extends IdentityAction
{

    /**
     * Creates a new instance of SigninAction
     */
    public InviteAction()
    {
        super("invite");
    }
    
    /**
     * Allows subclassing of this action
     * @param tcValue the unique value of the action
     */
    protected InviteAction(String tcValue)
    {
        super(tcValue);
    }

    @Override
    protected boolean doAction(IHTTPRequest toRequest, IHTTPResponse toResponse, List<Throwable> toErrors)
    {
        // TODO: Implement inviting a user without email
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
        return true;
    }

    @Override
    public boolean isProtocolSupported(RequestProtocol toProtocol, IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return true;
    }
    
    @Override
    public boolean useForwarding(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return true;
    }
}