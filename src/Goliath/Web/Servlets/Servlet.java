/* ========================================================
 * Servlet.java
 *
 * Author:      kenmchugh
 * Created:     Mar 13, 2011, 11:54:43 AM
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

package Goliath.Web.Servlets;

import Goliath.Applications.Application;
import Goliath.Collections.List;
import Goliath.Collections.PropertySet;
import Goliath.Constants.LogType;
import Goliath.Exceptions.InvalidMethodException;
import Goliath.Exceptions.InvalidOperationException;
import Goliath.Exceptions.InvalidProtocolException;
import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.Servlets.IRequestDispatcher;
import Goliath.Interfaces.Servlets.IServlet;
import Goliath.Interfaces.Servlets.IServletConfig;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.Web.Constants.RequestMethod;
import Goliath.Web.Constants.RequestProtocol;
import java.io.IOException;


        
/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Mar 13, 2011
 * @author      kenmchugh
**/
public abstract class Servlet extends Goliath.Object
        implements IServlet
{
    private IServletConfig m_oConfig;
    private List<RequestProtocol> m_oSupportedProtocols;
    private List<RequestMethod> m_oSupportedMethods;

    // TODO: Implement caching of requests and clearing of cache

    /**
     * Creates a new instance of Servlet
     */
    public Servlet()
    {
    }

    /**
     * Helper function to get the data from the  request, if the datais null or empty, an error will be added
     * @param tcProperty the property to get
     * @param tcErrorMessage the error message if the property has not been set
     * @param toRequest the request object
     * @return the value or null if the value did not exist
     */
    protected String getData(String tcProperty, String tcErrorMessage, PropertySet toEncodedData, IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        String lcReturn = toRequest.getParameter(tcProperty);
        if (toEncodedData != null && toEncodedData.hasProperty(tcProperty))
        {
            lcReturn = toEncodedData.getProperty(tcProperty);
        }
        if (Goliath.Utilities.isNullOrEmpty(lcReturn))
        {
            addError(toResponse, new Goliath.Exceptions.Exception(tcErrorMessage, false));
        }
        return lcReturn;
    }

    /**
     * Checks if this request is reporting any errors
     * @return true if there are errors for this request
     */
    public boolean hasErrors(IHTTPResponse toResponse)
    {
        return toResponse.hasErrors();
    }

    /**
     * Gets a list of errors that have occured while this command executed
     * @return a list of errors that have occurred
     */
    public Goliath.Collections.List<Throwable> getErrors(IHTTPResponse toResponse)
    {
        return toResponse.getErrors();
    }

    /**
     * Adds an exception to the error collection
     * @param toException the error to add
     * @return true if the collection was chaged due to a call to this class
     */
    protected boolean addError(IHTTPResponse toResponse, Throwable toException)
    {
        return toResponse.addError(toException);
    }

    /**
     * Clears the errors for the specified request
     * @param toRequest the request with the errors
     */
    protected void clearErrors(IHTTPResponse toResponse)
    {
        toResponse.clearErrors();
    }
    
    /**
     * Gets the list of supported methods, creates the list if it does not already exist
     * @return the list of supported methods for this servlet
     */
    private List<RequestMethod> getSupportedMethods()
    {
        if (m_oSupportedMethods == null)
        {
            m_oSupportedMethods = RequestMethod.getEnumerations(RequestMethod.class);
        }
        return m_oSupportedMethods;
    }

    /**
     * Adds a method to the supported methods
     * @param toMethod the method to add
     * @return true if the supported methods was changed as a result of this call
     */
    protected final boolean addSupportedMethod(RequestMethod toMethod)
    {
        if (!getSupportedMethods().contains(toMethod))
        {
            return m_oSupportedMethods.add(toMethod);
        }
        return false;
    }

    /**
     * Removes a supported method from the list of supported methods
     * @param toMethod the method to remove
     * @return true if the supported methods was changed as a result of this call
     */
    protected final boolean removeSupportedMethod(RequestMethod toMethod)
    {
        return getSupportedMethods().remove(toMethod);
    }

    /**
     * Helper function to check if the method is supported
     * @param toMethod the method to check
     * @return true if supported
     */
    protected boolean isMethodSupported(RequestMethod toMethod, IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return getSupportedMethods().contains(toMethod);
    }

    /**
     * Clears all the methods supported by this servlet
     * @return true if the supported methods was changed as a result of this call
     */
    protected final boolean clearSupportedMethods()
    {
        boolean llReturn = m_oSupportedMethods != null && m_oSupportedMethods.size() > 0;
        m_oSupportedMethods = new List<RequestMethod>();
        return llReturn;
    }

    /**
     * Helper function for populating the Allow response headers
     * @param toResponse the response to add the headers to
     */
    protected void addAllowHeaders(IHTTPResponse toResponse)
    {
        StringBuilder loBuilder = new StringBuilder();

        int lnCount = 0;
        for (RequestMethod loMethod : getSupportedMethods())
        {
            if (lnCount != 0)
            {
                loBuilder.append(",");
            }
            loBuilder.append(loMethod.getValue().toUpperCase());
        }
        toResponse.setResponseHeaders("Allow", loBuilder.toString());
    }

    /**
     * Gets the list of supported protocols, creates the list if it does not already exist
     * @return the list of supported protocols for this servlet
     */
    private List<RequestProtocol> getSupportedProtocols()
    {
        if (m_oSupportedProtocols == null)
        {
            m_oSupportedProtocols = RequestProtocol.getEnumerations(RequestProtocol.class);
        }
        return m_oSupportedProtocols;
    }

    /**
     * Adds a protocol to the supported protocols
     * @param toProtocol the protocol to add
     * @return true if the supported protocols was changed as a result of this call
     */
    protected final boolean addSupportedProtocol(RequestProtocol toProtocol)
    {
        if (!getSupportedProtocols().contains(toProtocol))
        {
            return m_oSupportedProtocols.add(toProtocol);
        }
        return false;
    }

    /**
     * Removes a supported protocol from the list of supported protocols
     * @param toProtocol the protocol to remove
     * @return true if the supported protocols was changed as a result of this call
     */
    protected final boolean removeSupportedProtocol(RequestProtocol toProtocol)
    {
        return getSupportedProtocols().remove(toProtocol);
    }

    /**
     * Helper function to check if the protocol is supported
     * @param toProtocol the protocol to check
     * @return true if supported
     */
    protected boolean isProtocolSupported(RequestProtocol toProtocol, IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return getSupportedProtocols().contains(toProtocol);
    }

    /**
     * Clears all the supported protocols
     * @return true if the supported protocols was changed as a result of this call
     */
    protected final boolean clearSupportedProtocols()
    {
        boolean llReturn = m_oSupportedProtocols != null && m_oSupportedProtocols.size() > 0;
        m_oSupportedProtocols = new List<RequestProtocol>();
        return llReturn;
    }

    /**
     * Removes all resources held by this servlet, called at the end of the lifetime of the servlet
     */
    @Override
    public final void destroy()
    {
        // TODO: Needs to be called when shutting down the servlet
        Application.getInstance().log("Destroying Servlet " + getClass().getName(), LogType.EVENT());
        onDestroy();
    }

    /**
     * Hook method to allow subclasses to override destroy
     */
    protected void onDestroy()
    {

    }

    /**
     * Initialises the servlet, called at the start of the lifetime of the servlet,
     * all servlet initialisation should be caught in here
     * @param toConfig
     * @throws ServletException
     */
    @Override
    public final void init(IServletConfig toConfig) throws ServletException
    {
        Application.getInstance().log("Initialising Servlet " + getClass().getName(), LogType.EVENT());
        onInit(toConfig);
    }

    /**
     * Hook method to allow subclasses to override the init
     * @param toConfig the servlet configuration
     * @throws ServletException if there are any errors in the initialisation
     */
    protected void onInit(IServletConfig toConfig)
            throws ServletException
    {
        
    }

    /**
     * Get the servlet configuration for this servlet, if the servlet has not
     * been set, this method will attempt to create it
     * @return the Servlet Configuration for this servlet
     */
    @Override
    public final IServletConfig getServletConfig()
    {
        if (m_oConfig == null)
        {
            m_oConfig = createServletConfig();
        }
        return m_oConfig;
    }

    /**
     * Creates the Servlet Configuration
     * @return
     */
    protected final IServletConfig createServletConfig()
    {
        String lcPropertyPath = "WebServer.Servlets." + getClass().getName();
        IServletConfig loConfig = Application.getInstance().<ServletConfig>getPropertyHandlerProperty(lcPropertyPath);
        if (loConfig == null)
        {
            loConfig = onCreateServletConfig();
            if (loConfig != null)
            {
                Application.getInstance().setPropertyHandlerProperty(lcPropertyPath, (ServletConfig)loConfig);
            }
        }
        return loConfig;
    }

    /**
     * Subclasses can override this to create their own config
     * @return the servlet config created
     */
    protected IServletConfig onCreateServletConfig()
    {
        IServletConfig loReturn = new ServletConfig();
        loReturn.setServletContext(getDefaultContext());
        return loReturn;
    }

    /**
     * The default context this servlet will use
     * @return the default context
     */
    protected String getDefaultContext()
    {
        return "/" + getClass().getSimpleName();
    }

    /**
     * Gets an informational string about the Servlet
     * @return the servlet information
     */
    @Override
    public String getServletInfo()
    {
        return getClass().getSimpleName();
    }
    
    /**
     * Executes the request and writes back to the response.
     * @param toRequest the request to execute
     * @param toResponse the response to return
     * @throws ServletException if there are any errors
     * @throws IOException if there are any read/write errors
     */
    @Override
    public final void service(IHTTPRequest toRequest, IHTTPResponse toResponse) throws ServletException, IOException
    {
        boolean llAuthenticated = toRequest.getSession().isAuthenticated();
        boolean llRequiresAuthentication = requiresAuthentication(toRequest, toResponse);
        boolean llAllowAuthentication = allowAuthenticated(toRequest, toResponse);
        // First check if an authenticated connection is accepted or required
        if (!((llRequiresAuthentication && !llAuthenticated) || (!llAllowAuthentication && llAuthenticated)))
        {
            RequestProtocol loProtocol = toRequest.getProtocol();

            if (isProtocolSupported(loProtocol, toRequest, toResponse))
            {
                RequestMethod loMethod = toRequest.getMethod();

                if (!isMethodSupported(loMethod, toRequest, toResponse))
                {
                    addAllowHeaders(toResponse);
                    throw new InvalidMethodException(toRequest);
                }
                
                toRequest.getSession().renew();

                doService(loMethod, toRequest, toResponse);
            }
            else
            {
                throw new InvalidProtocolException(toRequest);
            }
        }
        else
        {
            if (llRequiresAuthentication && !llAuthenticated)
            {
                addError(toResponse, new InvalidOperationException("You need to be authenticated to access " + this.getDefaultContext(), false));
            }

            if (!llAllowAuthentication && llAuthenticated)
            {
                addError(toResponse, new InvalidOperationException("You can not be authenticated to access " + this.getDefaultContext(), false));
            }

            // There was an error, so send the user to the error page
            completeServletWithDispatcher(toResponse, toRequest, null);
        }
    }

    /**
     * Helper function to send the request on to the servlet dispatcher.  If there were any errors, then the request will be sent
     * to the errorURL, otherwise it will be handled by the forwardToURL
     * @param toResponse the response for this transaction
     * @param toRequest the request for this transaction
     * @param tcMessage the message to display if a message is needed
     * @throws ServletException if there are any problems with executing the servlet required
     * @throws IOException if there are any problems with executing the servlet required
     */
    protected void completeServletWithDispatcher(IHTTPResponse toResponse, IHTTPRequest toRequest, String tcMessage)
            throws ServletException, IOException
    {
        boolean llHasErrors = hasErrors(toResponse);
        
        IRequestDispatcher loDispatcher = null;
        
        // If there are errors, redirect to the page that requested the login, otherwise redirect to the forwardToURL
        String lcPath = llHasErrors ?
                toRequest.hasParameter("errorURL") ? toRequest.getParameter("errorURL") : toRequest.getReferrer() :
                    toRequest.hasParameter("forwardToURL") ? toRequest.getParameter("forwardToURL") : toRequest.getReferrer();
        
        lcPath = lcPath.replaceAll("^\\.([^\\.]?)", toRequest.getPath() + toRequest.getFile() + "$1");

        loDispatcher = toRequest.getRequestDispatcher(lcPath);
        if (loDispatcher.getServlet() == this)
        {
            return;
        }
        
        toRequest.setPath(lcPath);
        
        // TODO: We are forcing forwarded responses to use GET, we want this to be definable per request
        toRequest.setMethod(RequestMethod.GET());

        loDispatcher.forward(toRequest, toResponse);
    }

    /**
     * Checks if this servlet must have an authenticated session to be processed
     * This should be overridden in subclasses
     * @return true if required
     */
    protected boolean requiresAuthentication(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return false;
    }

    /**
     * Checks if this servlet allows authenticated sessions to connect
     * This should be overridden in subclasses
     * @return true if allowed
     */
    protected boolean allowAuthenticated(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return true;
    }



    /**
     * Executed only if the method and protocol are allowed by this servlet
     * @param toMethod the request method
     * @param toRequest the request
     * @param toResponse the response
     * @throws ServletException if there is an error processing the servlet
     * @throws IOException if there is an io exception while processing
     */
    protected void doService(RequestMethod toMethod, IHTTPRequest toRequest, IHTTPResponse toResponse)
            throws ServletException, IOException
    {
        
    }

    /**
     * Gets the last modified time of this servlet, if not known this should return a negative number
     * @param toRequest the request getting the last modified
     * @return the last modified date of the resource in the request
     */
    protected long getLastModified(IHTTPRequest toRequest)
    {
        return -1;
    }

    @Override
    public int getMaximumPostBodySize()
    {
        // Get the global maximum size
        int lnValue = Application.getInstance().getPropertyHandlerProperty("WebServer.maximumPostSize", 2048 * 1024);
        int lnServletValue = Integer.MAX_VALUE;

        try
        {
            lnServletValue = (Integer)this.getServletConfig().getInitParameter("maximumPostSize");
        }
        catch (Throwable ex)
        {

        }

        return Math.min(lnValue, lnServletValue);
    }


}
