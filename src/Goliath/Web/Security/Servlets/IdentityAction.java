package Goliath.Web.Security.Servlets;

import Goliath.Collections.List;
import Goliath.DynamicEnum;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.Web.Constants.RequestMethod;
import Goliath.Web.Constants.RequestProtocol;
import Goliath.Web.Constants.ResultCode;

/**
 * An Identity action is an action that is taken when the identity servlet
 * is used.  The action is used to process the request
 * @author kmchugh
 */
public abstract class IdentityAction extends DynamicEnum
{
    /**
     * Singleton instance for the default action
     */
    private static IdentityAction g_oDefault;
    public static IdentityAction DefaultAction()
    {
        if (g_oDefault == null)
        {
            g_oDefault = new DefaultIdentityAction();
        }
        return g_oDefault;
    }
    
    /**
     * Gets the action requested, or if the action was not a valid action, 
     * gets the default action
     * @param tcAction the action to get
     * @return the action requested, or the default action
     */
    public static IdentityAction getAction(String tcAction)
    {
        IdentityAction loAction = IdentityAction.getEnumeration(IdentityAction.class, tcAction);
        return loAction == null ? DefaultAction() : loAction;
    }
    
    /**
     * Extracts the action from a request object
     * @return the action requested by the request object
     */
    public static IdentityAction getAction(IHTTPRequest toRequest)
    {
        return getAction(toRequest.getStringProperty("action"));
    }
    
    
    
    /**
     * Not publicly creatable
     * @param tcValue the unique value of this action
     */
    protected IdentityAction(String tcValue)
    {
        super(tcValue);
    }
    
    /**
     * Hook to manipulate the process before the action occurs, returning true
     * from this function will allow the action to proceed, returning false
     * will stop the action from being attempted
     * @param toRequest the request being processed
     * @param toErrors a list of errors that should be appended to if there are any errors in this call
     * @return true to allow the action, false to cancel the action
     */
    protected boolean onBeforeAction(IHTTPRequest toRequest, List<Throwable> toErrors)
    {
        return true;
    }
    
    /**
     * Occurs when the action has been successful
     * @param toRequest the request
     * @param toResponse the response
     */
    protected void onActionSuccess(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        // There is nothing that needs to be done here by default
    }
    
    /**
     * Occurs when the action has failed
     * @param toRequest the request
     * @param toResponse the response
     */
    protected void onActionFailure(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        // If the result code is OK, then we will change it to BAD REQUEST
        if (toResponse.getResultCode().equals(ResultCode.OK()))
        {
            toResponse.setResultCode(ResultCode.BAD_REQUEST());
        }
    }
    
    /**
     * Performs the processing of this action, if there are any errors this should append errors to toErrors.
     * If this call is successful it should return true, otherwise it should return false
     * @param toRequest the request
     * @param toResponse the response
     * @param toErrors the error list
     * @return true this call was successful 
     */
    protected boolean doAction(IHTTPRequest toRequest, IHTTPResponse toResponse, List<Throwable> toErrors)
    {
        return true;
    }
    
    /**
     * Processes the request and attaches any errors that occurred to the response
     * @param toRequest the request
     * @param toResponse the response
     * @return true if this returned without adding errors to the response
     */
    public final boolean process(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        List<Throwable> loErrors = new List<Throwable>();
        
        if (onBeforeAction(toRequest, loErrors))
        {
            if (doAction(toRequest, toResponse, loErrors))
            {
                onActionSuccess(toRequest, toResponse);
            }
            else
            {
                onActionFailure(toRequest, toResponse);
            }
        }
        
        // Add the errors to the response
        for (Throwable loError : loErrors)
        {
            toResponse.addError(loError);
        }
        
        return loErrors.size() == 0;
    }
    
    /**
     * Checks if this action allows the protocol requested
     * @param toProtocol the protocol
     * @param toRequest the request
     * @param toResponse the response
     * @return true if this protocol is allowed
     */
    public abstract boolean isProtocolSupported(RequestProtocol toProtocol, IHTTPRequest toRequest, IHTTPResponse toResponse);
    
    /**
     * Checks if this action allows the specified method
     * @param toMethod the method
     * @param toRequest the request
     * @param toResponse the response
     * @return true if allowed
     */
    public abstract boolean isMethodSupported(RequestMethod toMethod, IHTTPRequest toRequest, IHTTPResponse toResponse);
    
    /**
     * Checks if this action allows an authenticated request
     * @param toRequest the request
     * @param toResponse the response
     * @return true if authenticated requests can be processed by this action
     */
    public abstract boolean allowAuthenticated(IHTTPRequest toRequest, IHTTPResponse toResponse);
    
    /**
     * Checks if this action requires that the request already be authenticated
     * @param toRequest the request
     * @param toResponse the response
     * @return true if authentication was required
     */
    public abstract boolean requiresAuthentication(IHTTPRequest toRequest, IHTTPResponse toResponse);
    
    /**
     * Checks if this action should attempt to forward to the forwarding url or the error url after completion
     * @param toRequest the request
     * @param toResponse the response
     * @return true if forwarding should be used
     */
    public abstract boolean useForwarding(IHTTPRequest toRequest, IHTTPResponse toResponse);
}
