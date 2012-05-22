/* =========================================================
 * WebContextCommand.java
 *
 * Author:      kmchugh
 * Created:     09-Apr-2008, 17:34:26
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
 * =======================================================*/

package Goliath.Web.Contexts;

import Goliath.Commands.*;
import Goliath.Applications.Application;
import Goliath.Constants.LogType;
import Goliath.Interfaces.Web.IHTMLOutputStream;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.SingletonHandler;
import Goliath.Web.Constants.RequestMethod;
import java.util.Date;


/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @param <V> 
 * @see         Related Class
 * @version     1.0 09-Apr-2008
 * @author      kmchugh
**/
public abstract class WebContextCommand<V extends java.lang.Object> extends Goliath.Commands.ContextCommand<HandleRequestCommandArgs, V>
        implements Goliath.Interfaces.Commands.IWebContextCommand<HandleRequestCommandArgs, V>

{
    private boolean m_lResponseWritten = false;
    
    
    /** Creates a new instance of WebContextCommand */
    public WebContextCommand()
    {
    }

    @Override
    public boolean allowHTTP()
    {
        return true;
    }

    @Override
    public boolean allowSSL()
    {
        return true;
    }

    @Override
    protected String onGetContext()
    {
        return this.getClass().getSimpleName();
    }




    protected final V doContext() throws Throwable
    {
        // TODO: implement keep alives
        // TODO: implement accept-language

        // Turn off compression
        getResponse().setUseCompression(false);

        // Check if we are allowed to run this context based on SSL or HTTP
        if ((getRequest().getProtocol().getValue().toLowerCase().startsWith("https") && !this.allowSSL()) || (!getRequest().getProtocol().getValue().toLowerCase().startsWith("https") && getRequest().getProtocol().getValue().toLowerCase().startsWith("http") && !this.allowHTTP()))
        {
            String lcError = (getRequest().getProtocol().getValue().toLowerCase().equals("https") ? "Not allowed to access " + this.getClass().getName() + " using SSL" : "Not allowed to access " + this.getClass().getName() + " using HTTP" );
            throw new UnsupportedOperationException(lcError);
        }

        RequestMethod loMethod = getRequest().getMethod();
        if (loMethod == RequestMethod.DELETE())
        {
            Application.getInstance().log("Executing Delete Method for Web Command " + this.getClass().getName(), LogType.TRACE());
            return onDoDeleteContext();
        }
        else if (loMethod == RequestMethod.GET())
        {
            Application.getInstance().log("Executing Get Method for Web Command " + this.getClass().getName(), LogType.TRACE());
            return onDoGetContext();
        }
        else if (loMethod == RequestMethod.HEAD())
        {
            Application.getInstance().log("Executing Head Method for Web Command " + this.getClass().getName(), LogType.TRACE());
            return onDoHeadContext();
        }
        else if (loMethod == RequestMethod.POST())
        {
            Application.getInstance().log("Executing Post Method for Web Command " + this.getClass().getName(), LogType.TRACE());
            return onDoPostContext();
        }
        else if (loMethod == RequestMethod.PUT())
        {
            Application.getInstance().log("Executing Put Method for Web Command " + this.getClass().getName(), LogType.TRACE());
            return onDoPutContext();
        }
        else if (loMethod == RequestMethod.OPTIONS())
        {
            Application.getInstance().log("Executing Put Method for Web Command " + this.getClass().getName(), LogType.TRACE());
            return onDoOptionsContext();
        }
        else
        {
            Application.getInstance().log("Executing Unknown Method for Web Command " + this.getClass().getName(), LogType.EVENT());
            return onDoUnknownContext();
        }
    }

    protected V doDefaultContext()
    {
        if (!this.hasAuthenticatedContext() || (this.hasAuthenticatedContext() && this.isAuthenticatedUser()))
        {
            return onDoDefaultContext();
        }
        return onDoUnauthorisedContext();
    }
    
    protected abstract V onDoDefaultContext();

    protected V onDoUnauthorisedContext()
    {
        Application.getInstance().log("Unauthorised context not defined for " + this.getClass().getName(), LogType.WARNING());
        return onDoDefaultContext();
    }
    
    protected V onDoGetContext()
    {
        return doDefaultContext();
    }
    protected V onDoPostContext()
    {
        return doDefaultContext();
    }
    protected V onDoDeleteContext()
    {
        return doDefaultContext();
    }
    protected V onDoHeadContext()
    {
        return doDefaultContext();
    }
    protected V onDoPutContext()
    {
        return doDefaultContext();
    }
    protected V onDoOptionsContext()
    {
        return doDefaultContext();
    }
    protected V onDoUnknownContext()
    {
        return doDefaultContext();
    }

    protected IHTTPResponse getResponse()
    {
        if (getArguments() != null)
        {
            return getArguments().getResponse();
        }
        else
        {
            throw new UnsupportedOperationException("Can not get response until execute is called and the arguments have been set");
        }
    }
    
    protected IHTTPRequest getRequest()
    {
        if (getArguments() != null)
        {
            return getArguments().getRequest();
        }
        else
        {
            throw new UnsupportedOperationException("Can not get response until execute is called and the arguments have been set");
        }
    }

    
    @Override
    public IHTMLOutputStream getStream()
    {
        return getResponse().getStream();
    }


    
    @Override
    public final void writeResponse(String tcResponse)
    {
        try
        {
            onWriteResponse(tcResponse);
            m_lResponseWritten = true;
        }
        catch(Throwable ex)
        {
            Application.getInstance().log(ex);
        }    
    }
    
    protected void onWriteResponse(String tcResponse)
    {
        getResponse().write(tcResponse);
    }
    
    @Override
    public final V doExecute() throws Throwable
    {
        
        // set the response headers
        try
        {
            return onDoExecute();
        }
        catch(Throwable ex)
        {
            // Log the error
            Application.getInstance().log(ex);
            return null;
        }
    }
    
    protected V onDoExecute() throws Throwable
    {
        setResponseHeaders();
        return doContext();
    }
    
    protected boolean isResponseWritten()
    {
        return m_lResponseWritten;
    }
    
    private void setResponseHeaders()
    {
        getResponse().setResponseHeaders("Server", SingletonHandler.getInstance(Goliath.Web.LibraryVersion.class).toString());
        getResponse().setResponseHeaders("Date", Goliath.Utilities.Date.getRFC1123Date(new Date()));

        onSetResponseHeaders();
    }
    
    /**
     * By default the response headers are set to text/html
     * override this to change the default
     */
    protected void onSetResponseHeaders()
    {
        getResponse().setResponseHeaders("Content-Type", "text/html; charset=utf-8");
    }
}
