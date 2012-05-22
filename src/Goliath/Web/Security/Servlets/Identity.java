package Goliath.Web.Security.Servlets;

import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.Web.Constants.RequestMethod;
import Goliath.Web.Constants.RequestProtocol;
import Goliath.Web.Servlets.HTTPServlet;
import java.io.IOException;
import org.w3c.dom.Document;

/**
 * Class used by the identity gsp for all identity functionality.  This class
 * executes teh Identity Actions found on the identity requests
 * @author admin
 */
public class Identity extends HTTPServlet
{
    @Override
    protected boolean isProtocolSupported(RequestProtocol toProtocol, IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return IdentityAction.getAction(toRequest).isProtocolSupported(toProtocol, toRequest, toResponse);
    }
    
    @Override
    protected boolean isMethodSupported(RequestMethod toMethod, IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return IdentityAction.getAction(toRequest).isMethodSupported(toMethod, toRequest, toResponse);
    }
    
    @Override
    protected boolean allowAuthenticated(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return IdentityAction.getAction(toRequest).allowAuthenticated(toRequest, toResponse);
    }
    
    @Override
    protected boolean requiresAuthentication(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return IdentityAction.getAction(toRequest).requiresAuthentication(toRequest, toResponse);
    }

    @Override
    protected void doGet(IHTTPRequest toRequest, IHTTPResponse toResponse) throws ServletException, IOException
    {
        // Simply forward request to do post
        doPost(toRequest, toResponse, null);
    }

    /**
     * Any of the actions could happen with post, so doGet will forward to doPost, the check for allowed methods has
     * already completed so this will be okay
     * @param toRequest the request
     * @param toResponse the response
     * @param toXML the body if sent, this may be null
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doPost(IHTTPRequest toRequest, IHTTPResponse toResponse, Document toXML) throws ServletException, IOException
    {
        // Get the action that is occuring on this request
        IdentityAction loAction = IdentityAction.getAction(toRequest);
        
        // Perform the action
        loAction.process(toRequest, toResponse);
    }
}
