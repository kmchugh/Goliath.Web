/* =========================================================
 * ServerConnection.java
 *
 * Author:      kenmchugh
 * Created:     Oct 28, 2010, 3:50:10 PM
 *
 * A server connection is the connection created between
 * the client tool and the Goliath web server, or goliath
 * compatible web server.  The connection includes the
 * server, the server ID for confirmation, the username
 * and password for the connection.
 * --------------------------------------------------------
 * <Description>
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/
package Goliath.Web;

import Goliath.Applications.Application;
import Goliath.Collections.PropertySet;
import Goliath.Constants.StringFormatType;
import Goliath.Data.BusinessObjects.DynamicDataObject;
import Goliath.Data.DataObjects.ObjectRegistry;
import Goliath.Exceptions.InvalidParameterException;
import Goliath.Exceptions.InvalidURLException;
import Goliath.Exceptions.ObjectNotCreatedException;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Web.WebServices.WebServiceRequest;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A server connection is the connection created between
 * the client tool and the Goliath web server, or goliath
 * compatible web server.
 * @author kenmchugh
 */
public class ServerConnection extends DynamicDataObject
{

    public static String getHashFromURL(String tcURL)
    {
        return Goliath.Utilities.encryptMD5(tcURL.toLowerCase());
    }

    public static String getHashFromURL(URL toURL)
    {
        return getHashFromURL(toURL.toExternalForm());
    }
    // TODO: Before saving the GUID should be updated to be a hash of the url and username
    private URL m_oServerURL;
    private URL m_oServerSSLURL;
    private String m_cServerID;
    private String m_cUserName;
    private String m_cPassword;
    private boolean m_lRememberMe;
    private String m_cName;
    private String m_cDescription;
    private String m_cWebServiceContext;
    private boolean m_lIsValidated;
    private boolean m_lIsSecureValidated;
    private int m_nTimeout;

    /**
     * Creates a new Server Connection object
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public ServerConnection()
            throws InstantiationException, IllegalAccessException
    {
        super();
        initialiseComponent();
    }

    /**
     * Creates a server connection object from the object registry object
     * @param toObjectRegistry the object to load the server connection from
     */
    public ServerConnection(ObjectRegistry toObjectRegistry)
    {
        super(toObjectRegistry);
        initialiseComponent();
    }

    /**
     * Creates a new server connection using the specified url
     * @param tcURL the url of the server connection
     */
    public ServerConnection(String tcURL)
            throws MalformedURLException
    {
        this(new URL(tcURL));
    }

    /**
     * Creates a new server connection using the specified url, if the object was loaded from the data source,
     * then this will also try to validate the object
     * @param tcURL the url of the server connection
     */
    public ServerConnection(URL toURL)
    {
        super(getHashFromURL(toURL.toExternalForm()));
        if (m_cServerID == null)
        {
            m_oServerURL = toURL;
        }
        initialiseComponent();
    }

    /**
     * Creates a new instance of the server connection with the url provided.  This also sets the serverid that was expected,
     * and validates the server against the id stored
     * @param tcURL the url of the server
     * @param tcServerID the id of the server
     * @throws MalformedURLException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public ServerConnection(String tcURL, String tcServerID)
            throws MalformedURLException
    {
        this(new URL(tcURL), tcServerID);
    }

    /**
     * Creates a new instance of the server connection with the url provided, also validates the url against the id provided
     * @param toURL the URL of the server
     * @param tcServerID the id of the server
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public ServerConnection(URL toURL, String tcServerID)
    {
        super(getHashFromURL(toURL.toExternalForm()));
        if (!m_cServerID.equals(tcServerID))
        {
            throw new ObjectNotCreatedException("The server has a different identity than the one provided, could not create this connection with id " + tcServerID);
        }
        initialiseComponent();
    }

    /**
     * Initialises the server connection
     */
    private void initialiseComponent()
    {
        // Set the default timeout to 10 seconds
        if (m_nTimeout == 0)
        {
            m_nTimeout = 10000;
        }
    }

    /**
     * Gets the context that is used for web service communication to this server
     * @return the context used for web service communication
     */
    public String getWebServiceContext()
    {
        if (m_cWebServiceContext == null)
        {
            m_cWebServiceContext = Application.getInstance().getPropertyHandlerProperty("WebServer.WebServices.DefaultURI", "/WS/");
        }
        return m_cWebServiceContext;
    }

    /**
     * Sets the context that is used for web service communication to this server
     * @param tcContext the context that is used
     */
    @Goliath.Annotations.MaximumLength(length = 40)
    @Goliath.Annotations.NoNulls
    public void setWebServiceContext(String tcContext)
    {
        if ((m_cWebServiceContext == null && tcContext != null)
                || (m_cWebServiceContext != null && !m_cWebServiceContext.equalsIgnoreCase(tcContext)))
        {
            m_cWebServiceContext = tcContext;
            m_lIsValidated = false;
            m_lIsSecureValidated = false;
        }
    }

    /**
     * Return the is validated flag (currently set to true if the server is valid
     * @return is Validated flag
     */
    public boolean isURLValidated()
    {
        return m_lIsValidated;
    }

    /**
     * Set the is validated flag
     * @param tlIsValidated - set to true if the server is valid, otherwise false
     */
    @Goliath.Annotations.NotProperty
    public void setURLValidated(boolean tlIsValidated)
    {
        m_lIsValidated = tlIsValidated;
    }

    /**
     * Return the secure is validated flag (currently set to true if the secure server is valid)
     * @return is secure validated flag
     */
    public boolean isSecureURLValidated()
    {
        return m_lIsSecureValidated;
    }

    /**
     * Set the is secure validated flag
     * @param tlIsSecureValidated - set to true if the secure server is valid otherwise false
     */
    @Goliath.Annotations.NotProperty
    public void setSecureURLValidated(boolean tlIsSecureValidated)
    {
        m_lIsSecureValidated = tlIsSecureValidated;
    }

    public String getDescription()
    {
        return m_cDescription;
    }

    @Goliath.Annotations.MaximumLength(length = 500)
    public void setDescription(String tcDescription)
    {
        m_cDescription = tcDescription;
    }

    public String getName()
    {
        return m_cName;
    }

    @Goliath.Annotations.MaximumLength(length = 150)
    @Goliath.Annotations.NoNulls
    public void setName(String tcName)
    {
        m_cName = tcName;
    }

    /**
     * Gets the url to the server
     * @return the server url
     */
    public final String getServerURL()
    {
        return m_oServerURL == null ? null : m_oServerURL.toString();
    }

    /**
     * Sets teh Server URL
     * @param tcURL the url of the server
     */
    @Goliath.Annotations.MaximumLength(length = 255)
    @Goliath.Annotations.NoNulls
    public void setServerURL(String tcURL)
    {
        try
        {
            if (tcURL != null)
            {
                tcURL = tcURL.endsWith("/") ? tcURL.substring(0, tcURL.length() - 1) : tcURL;
            }
            if ((m_oServerURL == null && tcURL != null) || (m_oServerURL != null && !m_oServerURL.toExternalForm().equalsIgnoreCase(tcURL)))
            {
                m_oServerURL = new URL(tcURL);
                m_lIsValidated = false;
            }
        }
        catch (Throwable ex)
        {
            addValidationException(ex.getLocalizedMessage(), ex);
            m_oServerURL = null;
        }
    }

    /**
     * Gets the secure url to the server
     * @return the server url
     */
    public final String getServerSecureURL()
    {
        return m_oServerSSLURL == null ? null : m_oServerSSLURL.toString();
    }

    /**
     * Sets the URL to use to connect securely to the server
     * @param tcURL the url of the server
     */
    @Goliath.Annotations.MaximumLength(length = 255)
    public void setServerSecureURL(String tcURL)
    {
        try
        {
            if (tcURL != null)
            {
                tcURL = tcURL.endsWith("/") ? tcURL.substring(0, tcURL.length() - 1) : tcURL;
            }
            if ((m_oServerSSLURL == null && tcURL != null) || (m_oServerSSLURL != null && !m_oServerSSLURL.toExternalForm().equalsIgnoreCase(tcURL)))
            {
                m_oServerSSLURL = new URL(tcURL);
                m_lIsSecureValidated = false;
            }
        }
        catch (Throwable ex)
        {
            addValidationException(ex.getLocalizedMessage(), ex);
            m_oServerSSLURL = null;
        }
    }

    /**
     * Gets the amount of time in milliseconds this connection will wait for a response before shutting down
     * @return the connection timeout
     */
    public int getTimeout()
    {
        return m_nTimeout;
    }

    /**
     * Sets the amount of time it will take for this connection to time out if a response is not received
     * @param tnTimeout the new timeout
     */
    @Goliath.Annotations.MinimumValue(value = 0)
    @Goliath.Annotations.MaximumValue(value = 60000)
    public void setTimeout(int tnTimeout)
    {
        if (m_nTimeout != tnTimeout && tnTimeout >= 0)
        {
            m_nTimeout = tnTimeout;
        }
    }

    /**
     * Checks if this connection is supposed to remember the username and password
     * @return true if the username and password is supposed to be remembered
     */
    public boolean getRememberMe()
    {
        return m_lRememberMe;
    }

    /**
     * Sets if this connection is supposed to remember the username and password
     * @param tlRememberMe true to remember, false otherwise
     */
    public void setRememberMe(boolean tlRememberMe)
    {
        m_lRememberMe = tlRememberMe;
    }

    /**
     * gets the username that would be used for this connection
     * @return the user name for this connection
     */
    public String getUserName()
    {
        return m_cUserName;
    }

    /**
     * Sets the user name to use for this connection
     * @param tcUserName the user name for the connection
     */
    public void setUserName(String tcUserName)
    {
        m_cUserName = tcUserName;
    }

    /**
     * Sets the password for this connection
     * @param tcPassword the password for the connection
     */
    public void setPassword(String tcPassword)
    {
        m_cPassword = tcPassword;
    }

    /**
     * gets the password for this connection
     * @return the password for this connection
     */
    public String getPassword()
    {
        return m_cPassword;
    }

    /**
     * Makes a request to the server using the Request object that is provided, this will allow
     * forwarding of requests to compatible servers
     * @param toRequest the request object that is being forwarded to the server
     * @return the web service request
     * @throws InvalidURLException if the requested connection was not created
     */
    public final WebServiceRequest makeWebServiceRequest(IHTTPRequest toRequest, String tcSessionID)
            throws InvalidURLException
    {
        boolean llSecure = toRequest.isSecure();
        // Only make the request if the URL exists
        if (llSecure ? m_oServerSSLURL != null : m_oServerURL != null)
        {
            return new WebServiceRequest((llSecure ? m_oServerSSLURL : m_oServerURL).toExternalForm(), toRequest, m_nTimeout, tcSessionID);
        }
        else
        {
            // A request was attempted but the url was not available
            throw new InvalidURLException("A " + (llSecure ? "" : "non ") + "secured connection was requested but there is no available url on the server connection " + getName());
        }
    }

    /**
     * Makes a request to the specified web service for this server
     * @param tcServiceName the name of the service
     * @return the web service request
     * @throws InvalidURLException if the requested connection was not created
     */
    public final WebServiceRequest makeWebServiceRequest(String tcServiceName, String tcSessionID)
            throws InvalidURLException
    {
        return makeWebServiceRequest(tcServiceName, (String) null, false, tcSessionID);
    }

    /**
     * Makes a request to the specified web service for this server
     * @param tcServiceName the name of the service
     * @param tlSecure if true this request will be made on the secure connection
     * @return the web service request
     * @throws InvalidURLException if the requested connection was not created
     */
    public final WebServiceRequest makeWebServiceRequest(String tcServiceName, boolean tlSecure, String tcSessionID)
            throws InvalidURLException
    {
        return makeWebServiceRequest(tcServiceName, (String) null, tlSecure, tcSessionID);
    }

    /**
     * Makes a request to the specified web service for this server
     * @param tcServiceName the name of the service
     * @param tcBody the content of the request
     * @return the web service request
     * @throws InvalidURLException if the requested connection was not created
     */
    public final WebServiceRequest makeWebServiceRequest(String tcServiceName, String tcBody, String tcSessionID)
            throws InvalidURLException
    {
        return makeWebServiceRequest(tcServiceName, tcBody, false, tcSessionID);
    }

    /**
     * Makes a web request to the specified service encoding the propertyset for sending to the server
     * @param tcServiceName the name of the service
     * @param toEncodedBody the list of parameters to send to the server
     */
    public final WebServiceRequest makeWebServiceRequest(String tcServiceName, PropertySet toEncodedBody, String tcSessionID)
            throws InvalidURLException
    {
        return makeWebServiceRequest(tcServiceName, toEncodedBody, false, tcSessionID);
    }

    /**
     * Makes a web request to the specified service encoding the propertyset for sending to the server
     * @param tcServiceName the name of the service
     * @param toEncodedBody the list of parameters to send to the server
     */
    public final WebServiceRequest makeWebServiceRequest(String tcServiceName, PropertySet toEncodedBody, boolean tlSecure, String tcSessionID)
            throws InvalidURLException
    {
        // Only make the request if the URL exists
        if (tlSecure ? m_oServerSSLURL != null : m_oServerURL != null)
        {
            String lcURL = (tlSecure ? m_oServerSSLURL : m_oServerURL).toExternalForm();
            String lcContext = getWebServiceContext();

            lcURL = lcURL.endsWith("/") ? lcURL.substring(0, lcURL.length() - 1) : lcURL;
            tcServiceName = tcServiceName.startsWith("/") ? tcServiceName.substring(1) : tcServiceName;
            lcContext =
                    ((!lcContext.startsWith("/") && !lcURL.endsWith("/")) ? "/" : "")
                    + lcContext
                    + ((!lcContext.endsWith("/") && !tcServiceName.startsWith("/")) ? "/" : "");

            return new WebServiceRequest(lcURL + lcContext + tcServiceName + (tcServiceName.endsWith("/") ? "" : "/"), toEncodedBody, getTimeout(), tcSessionID);
        }
        else
        {
            // A request was attempted but the url was not available
            throw new InvalidURLException("A " + (tlSecure ? "" : "non ") + "secured connection was requested but there is no available url on the server connection " + getName());
        }
    }

    /**
     * Makes a request to the specified web service for this server
     * @param tcServiceName the name of the service
     * @param tcBody the content of the request
     * @param tlSecure if true this request will be made on the secure connection
     * @return the web service request
     * @throws InvalidURLException if the requested connection was not created
     */
    public final WebServiceRequest makeWebServiceRequest(String tcServiceName, String tcBody, boolean tlSecure, String tcSessionID)
            throws InvalidURLException
    {
        // Only make the request if the URL exists
        if (tlSecure ? m_oServerSSLURL != null : m_oServerURL != null)
        {
            String lcURL = (tlSecure ? m_oServerSSLURL : m_oServerURL).toExternalForm();
            String lcContext = getWebServiceContext();

            lcURL = lcURL.endsWith("/") ? lcURL.substring(0, lcURL.length() - 1) : lcURL;
            tcServiceName = tcServiceName.startsWith("/") ? tcServiceName.substring(1) : tcServiceName;
            lcContext =
                    ((!lcContext.startsWith("/") && !lcURL.endsWith("/")) ? "/" : "")
                    + lcContext
                    + ((!lcContext.endsWith("/") && !tcServiceName.startsWith("/")) ? "/" : "");

            return new WebServiceRequest(lcURL + lcContext + tcServiceName + (tcServiceName.endsWith("/") ? "" : "/"), tcBody, getTimeout(), tcSessionID);
        }
        else
        {
            // A request was attempted but the url was not available
            throw new InvalidURLException("A " + (tlSecure ? "" : "non ") + "secured connection was requested but there is no available url on the server connection " + getName());
        }
    }

    /**
     * Makes a request to the server using the Request object that is provided, this will allow
     * forwarding of requests to compatible servers
     * @param toRequest the request object that is being forwarded to the server
     * @return the web service request
     * @throws InvalidURLException if the requested connection was not created
     */
    public final WebRequest makeWebRequest(IHTTPRequest toRequest, String tcSessionID)
            throws InvalidURLException
    {
        boolean llSecure = toRequest.isSecure();
        // Only make the request if the URL exists
        if (llSecure ? m_oServerSSLURL != null : m_oServerURL != null)
        {
            return new WebRequest((llSecure ? m_oServerSSLURL : m_oServerURL).toExternalForm(), toRequest, m_nTimeout, tcSessionID);
        }
        else
        {
            // A request was attempted but the url was not available
            throw new InvalidURLException("A " + (llSecure ? "" : "non ") + "secured connection was requested but there is no available url on the server connection " + getName());
        }
    }

    /**
     * Makes a request to the specified url for this server
     * @param tcContextURL the url, not includeing the host of the context to get the results for
     * @return the web service request
     * @throws InvalidURLException if the requested connection was not created
     */
    public final WebRequest makeWebRequest(String tcContextURL, String tcSessionID)
            throws InvalidURLException
    {
        return makeWebRequest(tcContextURL, null, false, tcSessionID);
    }

    /**
     * Makes a request to the specified url for this server
     * @param tcContextURL the url, not includeing the host of the context to get the results for
     * @param tlSecure if true this request will be made on the secure connection
     * @return the web service request
     * @throws InvalidURLException if the requested connection was not created
     */
    public final WebRequest makeWebRequest(String tcContextURL, boolean tlSecure, String tcSessionID)
            throws InvalidURLException
    {
        return makeWebRequest(tcContextURL, null, tlSecure, tcSessionID);
    }

    /**
     * Makes a request to the specified url for this server
     * @param tcContextURL the url, not includeing the host of the context to get the results for
     * @param tcBody the content of the request
     * @return the web service request
     * @throws InvalidURLException if the requested connection was not created
     */
    public final WebRequest makeWebRequest(String tcContextURL, String tcBody, String tcSessionID)
            throws InvalidURLException
    {
        return makeWebRequest(tcContextURL, tcBody, false, tcSessionID);
    }

    /**
     * Makes a request to the specified url for this server
     * @param tcContextURL the url, not includeing the host of the context to get the results for 
     * @param tcBody the content of the request
     * @param tlSecure if true this request will be made on the secure connection
     * @return the web service request
     * @throws InvalidURLException if the requested connection was not created
     */
    public final WebRequest makeWebRequest(String tcContextURL, String tcBody, boolean tlSecure, String tcSessionID)
            throws InvalidURLException
    {
        // Only make the request if the URL exists
        if (tlSecure ? m_oServerSSLURL != null : m_oServerURL != null)
        {
            String lcURL = (tlSecure ? m_oServerSSLURL : m_oServerURL).toExternalForm();
            String lcContext = getWebServiceContext();

            lcURL = lcURL.endsWith("/") ? lcURL.substring(0, lcURL.length() - 1) : lcURL;
            tcContextURL = tcContextURL.startsWith("/") ? tcContextURL.substring(1) : tcContextURL;
            lcContext =
                    ((!lcContext.startsWith("/") && !lcURL.endsWith("/")) ? "/" : "")
                    + lcContext
                    + ((!lcContext.endsWith("/") && !tcContextURL.startsWith("/")) ? "/" : "");

            // TODO: This should be returning a WebRequest
            return new WebServiceRequest(lcURL + lcContext + tcContextURL + (tcContextURL.endsWith("/") ? "" : "/"), tcBody, getTimeout(), tcSessionID);
        }
        else
        {
            // A request was attempted but the url was not available
            throw new InvalidURLException("A " + (tlSecure ? "" : "non ") + "secured connection was requested but there is no available url on the server connection " + getName());
        }
    }

    /**
     * Hook method to allow subclasses to add custom validation rules to the class
    Ï*/
    @Override
    protected void onAddClassValidationRules()
    {
        addClassValidationRule(Goliath.Validation.Rules.ServerValidationRule.class, "ServerURL", null);
    }

    /**
     * Gets the server ID that has been set by the remote server
     * @return the id of the specified server
     */
    public final String getServerID()
    {
        return m_cServerID;
    }

    /**
     * Sets the id of the server, this can only be called if the server id is not already set, or if
     * it is being set to the same value as what it currently contains.  This is to allow the value to be set
     * the first time as well as to be set when attempting to validate the id from the remote server
     * @param tcServerID The new server ID
     */
    public final void setServerID(String tcServerID)
    {
        if (!Goliath.Utilities.isNullOrEmpty(m_cServerID) && !m_cServerID.equalsIgnoreCase(tcServerID))
        {
            throw new InvalidParameterException("Server ID is already set", "tcServerID");
        }
        m_cServerID = tcServerID;
    }

    @Override
    protected String formatString(StringFormatType toFormat)
    {
        return m_cName == null ? "" : m_cName;
    }
}
