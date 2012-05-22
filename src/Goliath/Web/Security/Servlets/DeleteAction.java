/* ========================================================
 * DeleteAction.java
 *
 * Author:      admin
 * Created:     Dec 31, 2011, 7:04:15 AM
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
public class DeleteAction extends IdentityAction
{

    /**
     * Creates a new instance of SigninAction
     */
    public DeleteAction()
    {
        super("delete");
    }
    
    /**
     * Allows subclassing of this action
     * @param tcValue the unique value of the action
     */
    protected DeleteAction(String tcValue)
    {
        super(tcValue);
    }

    @Override
    protected boolean doAction(IHTTPRequest toRequest, IHTTPResponse toResponse, List<Throwable> toErrors)
    {
        return Goliath.Security.Utilities.deleteUser(toRequest.getSession(), 
                                                    toRequest.getStringProperty("txtPassword"), 
                                                    toErrors);
    }

    @Override
    public boolean allowAuthenticated(IHTTPRequest toRequest, IHTTPResponse toResponse)
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
    public boolean requiresAuthentication(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return true;
    }
    
    @Override
    public boolean useForwarding(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return true;
    }
    
    
}