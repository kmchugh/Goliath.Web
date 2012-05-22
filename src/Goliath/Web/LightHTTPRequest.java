    /* =========================================================
 * LightHTTPRequest.java
 *
 * Author:      kmchugh
 * Created:     27-Feb-2008, 16:31:52
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
package Goliath.Web;

import Goliath.Web.Constants.RequestProtocol;
import Goliath.Web.Constants.RequestMethod;
import Goliath.Web.Constants.ContentDisposition;
import Goliath.Applications.Application;
import Goliath.Collections.HashTable;
import Goliath.Collections.List;
import Goliath.Collections.PropertySet;
import Goliath.Constants.LogType;
import Goliath.Constants.MimeType;
import Goliath.Date;
import Goliath.DynamicCode.Java;
import Goliath.Environment;
import Goliath.Exceptions.InvalidParameterException;
import Goliath.Interfaces.Servlets.IRequestDispatcher;
import Goliath.Interfaces.Servlets.IServlet;
import Goliath.Web.Servlets.RequestDispatcher;
import com.sun.net.httpserver.Headers;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;


/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 27-Feb-2008
 * @author      kmchugh
 **/
public class LightHTTPRequest extends Goliath.Object
        implements Goliath.Interfaces.Web.IHTTPRequest
{
    private static HashTable<String, Class<? extends IServlet>> g_oServlets;
    private static HashTable<String, IRequestDispatcher> g_oLoadedDispatchers;


    com.sun.net.httpserver.HttpExchange m_oExchange;
    private String m_cRequestBody;
    private PropertySet m_oPropertySet;
    private PropertySet m_oHeaders;
    private PropertySet m_oCookies;
    private Goliath.Interfaces.ISession m_oSession;
    private String m_cCharSet;
    private String m_cFile;
    private String m_cPath;
    private RequestMethod m_oMethod;
    private RequestProtocol m_oProtocol;
    private MimeType m_oContentType;
    private String m_cHTTPProtocol;
    private HashTable<String, Part> m_oParts;


    /** Creates a new instance of LightHTTPResponse
     * @param toExchange The HTTPExchange that took place
     */
    public LightHTTPRequest(com.sun.net.httpserver.HttpExchange toExchange)
    {
        m_oExchange = toExchange;

        // Make sure the name of this thread is the same name as the session thread
        // This is to ensure that calls to Application.getInstance().getCurrentSession() get the correct session
        Thread.currentThread().setName(getSession().getSessionID());

        // We are able to pick up an IP address now, so lets set it in the session
        getSession().setSessionIP(m_oExchange.getRemoteAddress().getAddress().getHostAddress());
    }

    /**
     * Gets all of the headers from the request
     * @return the headers
     */
    @Override
    public final PropertySet getHeaders()
    {
        if (m_oHeaders == null)
        {
            m_oHeaders = new PropertySet();

            Headers loHeaders = m_oExchange.getRequestHeaders();
            for (String lcHeader : loHeaders.keySet())
            {
                // TODO: Parse the header values so they are actually lists rather than strings
                List<String> loValue = new List<String>(loHeaders.get(lcHeader));
                m_oHeaders.setProperty(lcHeader, loValue);
            }
        }
        return m_oHeaders;
    }
    
    /**
     * Gets the name of the host that was browsed to
     * @return the host name
     */
    @Override
    public String getHost()
    {
        return m_oExchange.getLocalAddress().getHostName();
    }

    @Override
    public String getHeader(String tcHeader)
    {
        List<String> loValues = (List<String>)getHeaders().getProperty(tcHeader);
        return loValues != null && loValues.size() > 0 ? loValues.get(0) : null;
    }

    @Override
    public List<String> getHeaderNames()
    {
        return getHeaders().getPropertyKeys();
    }

    @Override
    public List<String> getHeaderValues(String tcHeader)
    {
        List<String> loValues = (List<String>)getHeaders().getProperty(tcHeader);
        return loValues == null ? new List<String>(0) : loValues;
    }

    /**
     * Gets the header as a date value, if the header can not be parsed, this should
     * throw an InvalidParameterException
     * @param tcHeader the header to get the value for
     * @return the value parsed as a date
     */
    public Date getDateHeader(String tcHeader)
    {
        // TODO: Implement this, and implement on the IHTTPRequest interface
        return null;
    }

    /**
     * Gets the header as an integer value, if the header can not be parsed, this should
     * throw an InvalidParameterException
     * @param tcHeader the header to get the value for
     * @return the value parsed as an integer
     */
    @Override
    public int getIntHeader(String tcHeader)
    {
        int lnReturn = 0;
        if (getHeaders().hasProperty(tcHeader))
        {
            String lcValue = getHeader(tcHeader);
            try
            {
                lnReturn = Integer.parseInt(lcValue);
            }
            catch (Throwable ex)
            {
                throw new InvalidParameterException(ex.getLocalizedMessage(), tcHeader);
            }
        }
        return lnReturn;
    }

    /**
     * Gets the session associated with the request
     * @return the session
     */
    @Override
    public synchronized final Goliath.Interfaces.ISession getSession()
    {
        if (m_oSession == null)
        {
            // First check if a session id is available
            // Here we are trying both the cookie and parameters in case the client doesn't allow cookies
            String lcSessionID = 
                    Goliath.Utilities.isNull(
                        Goliath.Utilities.isNullOrEmpty(getCookie("goliath_app_id")) ? (String)getParameter("sessionID") : getCookie("goliath_app_id"),
                        Goliath.Utilities.generateStringGUID());
            
            m_oSession = Goliath.Utilities.isNull(Application.getInstance().getSession(lcSessionID),
                                                  Application.getInstance().createSession(lcSessionID));
        }
        return m_oSession;
    }

    @Override
    public final boolean isSessionCookieSet()
    {
        return !Goliath.Utilities.isNullOrEmpty(getCookie("goliath_app_id"));
    }
    
    @Override
    public final String getHTTPProtocol()
    {
        if (m_cHTTPProtocol == null)
        {
            m_cHTTPProtocol = m_oExchange.getProtocol();
        }
        return m_cHTTPProtocol;
    }
    
    @Override
    public final RequestProtocol getProtocol()
    {
        if (m_oProtocol == null)
        {
            // TODO: Implement protocols such as file:
            // TODO: Implement this better so we are not using class names
            m_oProtocol = m_oExchange.getClass().getName().indexOf("Https") >= 0 ? RequestProtocol.HTTPS() : RequestProtocol.HTTP();
        }
        return m_oProtocol;
    }

    @Override
    public Part getPart(String tcName)
    {
        if (isMultiPart())
        {
            if (m_oParts == null)
            {
                getParts();
            }
            return m_oParts == null ? null : m_oParts.get(tcName);
        }
        return null;
    }

    @Override
    public boolean hasPart(String tcName)
    {
        return getPart(tcName) != null;
    }

    @Override
    public List<Part> getParts()
    {
        if (isMultiPart())
        {
            if (m_oParts == null)
            {
                loadParts();
                return new List<Part>(m_oParts.values());
            }
        }
        return new List<Part>(0);
    }


    private void loadParts()
    {
        if (m_oParts == null)
        {
            // Parse out the boundary
            String lcBoundary = "--" + m_oExchange.getRequestHeaders().getFirst("content-type").split("boundary=")[1];

            m_oParts = new HashTable<String, Part>();

            List<Part> loParts = MultiPartParser.parseParts(lcBoundary, m_oExchange.getRequestBody());
            for (Part loPart : loParts)
            {
                m_oParts.put(loPart.getName(), loPart);
            }
        }
    }


    
    @Override
    public final String getPreferredCharSet()
    {
        if (m_cCharSet == null)
        {
            String lcReportedSet = m_oExchange.getRequestHeaders().containsKey("Accept-Charset") ? m_oExchange.getRequestHeaders().get("Accept-Charset").get(0) : "utf-8";
            
            if (lcReportedSet == null || lcReportedSet.indexOf("utf-8") >= 0)
            {
                m_cCharSet = "utf-8";
            }
            else if(lcReportedSet != null)
            {
                m_cCharSet = lcReportedSet.split(",")[0];
            }
            else
            {
                m_cCharSet = lcReportedSet;
            }
        }
        return m_cCharSet;
    }

    @Override
    public final String getUserAgent()
    {
        java.util.List<String> loList = m_oExchange.getRequestHeaders().get("User-agent");
        return loList != null && loList.size() > 0 ? loList.get(0) : "Unknown";
    }



    /**
     * Gets the http context associated with this request
     * @return the http context
     */
    public final com.sun.net.httpserver.HttpContext getHttpContext()
    {
        return m_oExchange.getHttpContext();
    }

    /**
     * Getst the local address for the request
     * @return the local address
     */
    public final java.net.InetSocketAddress getLocalAddress()
    {
        return m_oExchange.getLocalAddress();
    }

    /**
     * Gets the remote address for the request
     * @return the remote address
     */
    @Override
    public final java.net.InetSocketAddress getRemoteAddress()
    {
        return m_oExchange.getRemoteAddress();
    }

    /**
     * Gets the method of the request
     * @return the request method
     */
    @Override
    public final RequestMethod getMethod()
    {
        if (m_oMethod == null)
        {
            m_oMethod = Goliath.DynamicEnum.getEnumeration(RequestMethod.class, m_oExchange.getRequestMethod().toUpperCase());
            if (m_oMethod == null)
            {
                Application.getInstance().log("Unknown Request Method - " + m_oExchange.getRequestMethod(), LogType.ERROR());
                return RequestMethod.UNKNOWN();
            }
        }
        return m_oMethod;
    }

    /**
     * Forces the request to use the specified method
     * @param toMethod the method to use for this request
     */
    @Override
    public void setMethod(RequestMethod toRequest)
    {
        m_oMethod = toRequest;
    }

    /**
     * Gets the body of the request
     * @return the body of the request
     */
    @Override
    public final String getBody()
    {
        if (m_cRequestBody == null)
        {
            // If this is a file upload, we will want to make sure we are streaming and storing rather than converting to a string
            m_cRequestBody = Goliath.IO.Utilities.Stream.toString(m_oExchange.getRequestBody(), getPreferredCharSet());
        }
        return m_cRequestBody;
    }

    @Override
    public final String getReferrer()
    {
        java.util.List<String> loList = m_oExchange.getRequestHeaders().get("Referer");
        return loList != null && loList.size() > 0 ? loList.get(0) : "/";
    }
    
    /**
     * Gets the length of the body
     * @return the body length
     */
    public final int getBodyLength()
    {
        return Goliath.Utilities.isNull(getBody(), "").length();
    }

    /**
     * Gets the query string from the request
     * @return the query string
     */
    @Override
    public final String getQueryString()
    {
        String lcReturn = m_oExchange.getRequestURI().getRawQuery();
        if (Goliath.Utilities.isNullOrEmpty(lcReturn))
        {
            return "";
        }
        
        // TODO: Check if this is just encode decode rather than replace
        lcReturn = lcReturn.replace('+', ' ');
        
        return lcReturn;
    }
    
    /**
     * Gets the length of the query string
     * @return the length of the query string
     */
    public final int getQueryStringLength()
    {
        return (Goliath.Utilities.isNull(m_oExchange.getRequestURI().getRawQuery(), "").length());
    }

    /**
     * Helper function to get the cookies collection, creates the collection if it does not
     * already exist
     * @return
     */
    private PropertySet getCookies()
    {
        if (m_oCookies == null)
        {
            m_oCookies = new PropertySet();
            readCookies();
        }
        return m_oCookies;
    }
    
    /**
     * Gets the value of a cookie
     * @param tcCookieName the name of the cookie to get
     * @return the value of the cookie
     */
    @Override
    public final String getCookie(String tcCookieName)
    {
        return (String)getCookies().getProperty(tcCookieName);
    }

    /**
     * Helper function to get the propety set, this will create the property set if needed
     * @return the property set
     */
    private PropertySet getPropertySet()
    {
        if (m_oPropertySet == null)
        {
            m_oPropertySet = new PropertySet();
            readProperties();
        }
        return m_oPropertySet;
    }

    /**
     * Checks if this request has the specified property set.  Parameter names are case insensitive.
     * @param tcParameterName the property to check for
     * @return true if the property is set
     */
    @Override
    public boolean hasParameter(String tcParameterName)
    {
        return getPropertySet().hasProperty(tcParameterName);
    }

    /**
     * Returns the parameter specified, if the parameter does not exist
     * then this will return null.  Parameter names are case insensitive.  If the parameter has more than one value, then
     * this method will return the first value
     * @param tcParameterName the name of the paramter to get
     * @return the parameter value, or first parameter value, or null if no parameter is set
     */
    @Override
    public String getParameter(String tcParameterName)
    {
        Object loValue = getPropertySet().getProperty(tcParameterName);
        if (loValue != null)
        {
            loValue = (Java.isEqualOrAssignable(java.util.List.class, loValue.getClass())) ? ((java.util.List)loValue).get(0) : loValue;
        }
        return (String)loValue;
    }

    /**
     * Gets the names of all the available parameters and returns them as an array
     * @return the list of all the available parameters
     */
    @Override
    public final String[] getParameterNames()
    {
        // TODO: This needs to be updated to return a List of strings after the getProperty and getParameter has been refactored
        return getPropertySet().getPropertyKeys().toArray(new String[0]);
    }

    /**
     * Gets the list of parameter values for the specified parameter.  Parameter names are case insensitive.
     * @param tcParameterName the name of the parameter to get the values for
     * @return the list of all the parameters, or an empty list if no values are available
     */
    @Override
    public final String[] getParameterValues(String tcParameterName)
    {
        // TODO: This needs to be updated to return a List of strings after the getProperty and getParameter has been refactored
        Object loValue = getPropertySet().getProperty(tcParameterName);
        if (loValue != null)
        {
            loValue = (Java.isEqualOrAssignable(java.util.List.class, loValue.getClass())) ?
                ((java.util.List)loValue).toArray(new String[((java.util.List)loValue).size()]) :
                new String[]{(String)loValue};
        }
        return loValue == null ? new String[0] : (String[])loValue;
    }

    /**
     * Sets the value of the parameter specified, overwrites any existing setting
     * @param tcPropertyName the name of the property
     * @param tcPropertyValue the value of the property
     * @return true if the property set was modified as a result of this call
     */
    @Override
    public final boolean setParameter(String tcPropertyName, String tcPropertyValue)
    {
        return getPropertySet().setProperty(tcPropertyName, tcPropertyValue);
    }

    /**
     * Adds the specifed value to the list of parameters, if the parameter already has a value, this
     * method will append the value to the list of values attributed to the parameter
     * @param tcPropertyName the name of the parameter
     * @param tcPropertyValue the value of the parameter
     * @return true if the property set was modified as a result of this call
     */
    @Override
    public final boolean addParameter(String tcPropertyName, String tcPropertyValue)
    {
        PropertySet loProperties = getPropertySet();
        if (loProperties.hasProperty(tcPropertyName))
        {
            Object loValue = loProperties.getProperty(tcPropertyName);
            if (Java.isEqualOrAssignable(java.util.List.class, loValue.getClass()))
            {
                if (((java.util.List)loValue).contains(tcPropertyValue))
                {
                    // Value is already in the list
                    return false;
                }
                else
                {
                    return ((java.util.List)loValue).add(tcPropertyValue);
                }
            }
            else
            {
                // Create the list value
                List<String> loList = new List<String>();
                loList.add((String)loValue);
                if (!loValue.equals(tcPropertyValue))
                {
                    loList.add(tcPropertyValue);
                }
                getPropertySet().setProperty(tcPropertyName, loList);
                return true;
            }
        }
        else
        {
            // The parameter does not exist, so this is the same as setting
            return setParameter(tcPropertyName, tcPropertyValue);
        }
    }



    /**
     * Clears the specified parameter from the propertyset, returns true or false
     * depending on if the property set has been changed as a result of this call, true means it has been changed
     * @param tcPropertyName the name of the property to clear
     * @return true if the property set has changed as a result of this call, false otherwise
     */
    @Override
    public final boolean clearParameter(String tcPropertyName)
    {
        return getPropertySet().clearProperty(tcPropertyName);
    }

    @Override
    public final float getFloatProperty(String tcPropertyName)
    {
        String lcValue = Goliath.Utilities.isNull(getParameter(tcPropertyName), "0");
        try
        {
            return Float.parseFloat(lcValue);
        }
        catch (Throwable ex)
        {
            throw new InvalidParameterException(ex.getLocalizedMessage(), tcPropertyName);
        }
    }


    @Override
    public final int getIntProperty(String tcPropertyName)
    {
        String lcValue = Goliath.Utilities.isNull(getParameter(tcPropertyName), "0");
        try
        {
            return Integer.parseInt(lcValue);
        }
        catch (Throwable ex)
        {
            throw new InvalidParameterException(ex.getLocalizedMessage(), tcPropertyName);
        }
    }
    
    @Override
    public final String getStringProperty(String tcPropertyName)
    {
        return Goliath.Utilities.isNull(getParameter(tcPropertyName), "");
    }
    
    /**
     * Sets a property in the request
     * @param tcPropertyName the name of the property
     * @param toValue the value to set the property to
     */
    public final void setProperty(String tcPropertyName, java.lang.Object toValue)
    {
        getPropertySet().setProperty(tcPropertyName, toValue);
    }

    /**
     * Gets all of the properties of the request
     * @return a Property bad with all the properties
     */
    public final Goliath.Interfaces.Collections.IPropertySet getProperties()
    {
        return getPropertySet();
    }

    /**
     * Reads in all the cookies and adds them to the properties
     */
    private void readCookies()
    {
        List<String> loList = this.getHeaderValues("cookie");
        if (loList.size() >= 0)
        {
            for (String lcCookieList : loList)
            {
                String[] laCookies = lcCookieList.split(";");
                for (String lcCookieValue : laCookies)
                {
                    String[] laCookie = lcCookieValue.split("=");
                    String lcName = laCookie[0];
                    String lcValue = "";
                    if (laCookie.length > 1)
                    {
                        lcValue = laCookie[1];
                    }
                    m_oCookies.setProperty(lcName, lcValue);
                }
            }
        }
    }

    /**
     * Reads in all of the properties from the request and appends them to the properties collection
     */
    private void readProperties()
    {
        m_oPropertySet.setProperty("file", getFile());
        m_oPropertySet.setProperty("path", getPath());
        
        String[] laProperties = getQueryString().split("&");
        for (String lcProperty : laProperties)
        {
            if (!Goliath.Utilities.isNullOrEmpty(lcProperty))
            {
                String[] laProperty = lcProperty.split("=", 2);
                String lcPropertyName = Goliath.Utilities.decode(laProperty[0]);
                String lcValue = "";
                if (laProperty.length > 1)
                {
                    lcValue = Goliath.Utilities.decode(laProperty[1]);
                }
                // Properties are allowed to have multiple values, if this property already exists, then change it to a list
                if (m_oPropertySet.containsKey(lcPropertyName))
                {
                    Object loValue = m_oPropertySet.get(lcPropertyName);
                    if (loValue != null && Java.isEqualOrAssignable(java.util.List.class, loValue.getClass()))
                    {
                        // If the list already contains the value, no need to add it again
                        if (!((java.util.List)loValue).contains(lcValue))
                        {
                            ((java.util.List)loValue).add(lcValue);
                        }
                    }
                    else
                    {
                        if (!loValue.equals(lcValue))
                        {
                            List<String> loList = new List<String>();
                            loList.add(loValue.toString());
                            loList.add(lcValue);
                            m_oPropertySet.setProperty(lcPropertyName, loList);
                        }
                    }
                }
                else
                {
                    m_oPropertySet.setProperty(lcPropertyName, lcValue);
                }
            }
        }

        // Only read the form properties if this was a post and of the correct type
        if (hasFormPostData())
        {
            laProperties = getBody().split("&");
            for (String lcProperty : laProperties)
            {
                if (!Goliath.Utilities.isNullOrEmpty(lcProperty))
                {
                    String[] laProperty = lcProperty.split("=", 2);
                    String lcPropertyName = Goliath.Utilities.decode(laProperty[0]);
                    String lcValue = "";
                    if (laProperty.length > 1)
                    {
                        lcValue = Goliath.Utilities.decode(laProperty[1]);
                    }
                    m_oPropertySet.setProperty(lcPropertyName, lcValue);
                }
            }
        }

        // If there is multipart and if the Content-Disposition is form-data, then they need to be included as parameters too
        if (isMultiPart())
        {
            for (Part loPart : getParts())
            {
                if (loPart.getContentDisposition() == ContentDisposition.FORM_DATA() &&
                    loPart.getContentType() == null)
                {
                    m_oPropertySet.setProperty(loPart.getName(), loPart.getContent());
                }
            }
        }
        
        // If there is a encodedData paramter, then decode it
        if (m_oPropertySet.hasProperty("encodedData"))
        {
            m_oPropertySet.merge(Utilities.parseEncodedData(m_oPropertySet.<String>getProperty("encodedData")), true);
        }
    }

    @Override
    public int getContentLength()
    {
        int lnReturn =0;
        if (getHeaders().hasProperty("Content-Length"))
        {
            try
            {
                lnReturn = getIntHeader("Content-Length");
            }
            catch (Throwable ex)
            {
                lnReturn = -1;
            }
        }
        return lnReturn;
    }



    /**
     * Gets the content type of the body of this request
     * @return the content type of the body of the request
     */
    @Override
    public final MimeType getContentType()
    {
        if (m_oContentType == null)
        {
            String lcType = this.getHeaders().hasProperty("Content-Type") ? this.getHeader("Content-Type") : MimeType.TEXT_HTML().getValue();
            if (lcType.indexOf(";") >= 0)
            {
                lcType = lcType.substring(0, lcType.indexOf(";"));
            }
            m_oContentType = MimeType.getEnumeration(MimeType.class, lcType);
        }
        return m_oContentType;
    }


    /**
     * Checks if this request has any form data
     * @return true if there is form data
     */
    @Override
    public final boolean hasFormPostData()
    {
        return getMethod() == RequestMethod.POST() && getContentType() == MimeType.APPLICATION_X_WWW_FORM_URL_ENCODED();
    }

    /**
     * Checks if this request has form multipart
     * @return
     */
    @Override
    public final boolean isMultiPart()
    {
        return getMethod() == RequestMethod.POST() && getContentType() == MimeType.MULTIPART_FORM_DATA();
    }

    /**
     * Gets the path of the request
     * @return the path
     */
    @Override
    public final String getPath()
    {
        if (m_cPath == null)
        {
            m_cPath = m_oExchange.getRequestURI().getPath();
            String lcFile = getFile();
            if (!Goliath.Utilities.isNullOrEmpty(lcFile))
            {
                m_cPath = m_cPath.substring(0, m_cPath.lastIndexOf(getFile()));
            }
        }
        return m_cPath;
    }

    /**
     * Gets the Servlet handler for the context specified, or for the class specified
     * @param tcContext the context to use to process the request
     * @return the RequestDispatcher to handle the context
     */
    @Override
    public synchronized IRequestDispatcher getRequestDispatcher(String tcPath)
    {
        // Check if this is a file, and if it exists, process it directly.
        if (!tcPath.matches(".+/$|/"))
        {
            if (new File("./" + tcPath).exists() || new File("./htdocs/" + tcPath).exists())
            {
                return getRequestDispatcher("/");
            }
        }
        
        String lcServletKey = getKeyFromContext(tcPath);
        
        // First check if the servlet already has been loaded
        if (g_oLoadedDispatchers != null && g_oLoadedDispatchers.containsKey(lcServletKey))
        {
            IRequestDispatcher loReturn = g_oLoadedDispatchers.get(lcServletKey);
            Application.getInstance().log("Using Servlet " + loReturn.getServlet().getClass().getName() + " for " + lcServletKey);
            return loReturn;
        }

        // Check if we need to load the servlets here
        if (g_oServlets == null)
        {
            loadServlets();
        }

        // Check if the servlet exists and needs to be initialised
        if (g_oServlets != null && g_oServlets.containsKey(lcServletKey))
        {
            IServlet loServlet = null;
            try
            {
                loServlet = g_oServlets.get(lcServletKey).newInstance();

                // TODO: Load up Servlet Configuration
                loServlet.init(loServlet.getServletConfig());

                if (g_oLoadedDispatchers == null)
                {
                    g_oLoadedDispatchers = new HashTable<String, IRequestDispatcher>();
                }
                g_oLoadedDispatchers.put(lcServletKey, new RequestDispatcher(loServlet));

                return g_oLoadedDispatchers.get(lcServletKey);
            }
            catch(Throwable ex)
            {
                Application.getInstance().log(ex);
            }
        }

        // If we got as far as here, no servlet was available for this request
        // Try a shorter path
        if (lcServletKey.length() > 1)
        {
            lcServletKey = lcServletKey.replaceAll("[^/]+/?$", "");
            lcServletKey = lcServletKey.substring(0, lcServletKey.lastIndexOf("/"));
            return getRequestDispatcher(lcServletKey);
        }

        Application.getInstance().log("Unable to find a Servlet for " + lcServletKey);
        // Nothing could be found
        return null;
    }


    /**
     * Loads all the servlets available
     */
    private void loadServlets()
    {
        if (g_oServlets == null)
        {
            g_oServlets = new HashTable<String, Class<? extends IServlet>>();
        }

        List<Class<IServlet>> loServlets = Application.getInstance().getObjectCache().getClasses(IServlet.class);

        for (Class<IServlet> loClass : loServlets)
        {
            try
            {
                IServlet loServlet = loClass.newInstance();
                g_oServlets.put(getKeyFromContext(loServlet.getServletConfig().getServletContext().getURLPattern()), loClass);
                // Also put the servlet class name in as a key for retrieval by class name
                g_oServlets.put(getKeyFromContext(loServlet.getClass().getName()), loClass);
            }
            catch(Throwable ex)
            {
                Application.getInstance().log(ex);
            }
        }
    }

    /**
     * Generates a key to use as a lookup from the key provided
     * @param tcPath
     * @return
     */
    private String getKeyFromContext(String tcPath)
    {
        return (tcPath.replaceAll("(?:https?://[^/]+)", "") + "/").replaceAll("//", "/").toLowerCase();
    }



    /**
     * Gets the file of the request
     * @return the file
     */
    @Override
    public final String getFile()
    {
        if (m_cFile == null)
        {
            String lcFile = m_oExchange.getRequestURI().getPath();
            lcFile = lcFile.substring(Math.max(lcFile.lastIndexOf("/"), lcFile.lastIndexOf("\\")) +1);
            m_cFile = lcFile.indexOf(".") >0 ? lcFile : "";
        }
        return m_cFile;
    }

    @Override
    public final void setPath(String tcPath)
    {
        tcPath.replaceAll("^.+://", "");
        
        if (tcPath.endsWith("/") || tcPath.endsWith(Environment.FILESEPARATOR()))
        {
            m_cPath = tcPath;
            m_cFile = "";
        }
        else
        {
            m_cPath = tcPath.indexOf("/" ) >= 0 ? tcPath.substring(0, tcPath.lastIndexOf("/")) : "/";
            m_cFile = tcPath.substring(m_cPath.lastIndexOf("/") +1);
        }
    }

    @Override
    public final URI getURI()
    {
        return m_oExchange.getRequestURI();
    }

    @Override
    public boolean isSecure()
    {
        return getProtocol() == RequestProtocol.HTTPS();
    }

    @Override
    public Map<String, java.lang.Object> getParameterMap()
    {
        return getPropertySet();
    }

    @Override
    public String getSSLFullHost()
    {
        String lcHost = getSSLHost();
        if (lcHost != null)
        {
            int lnPort = getSSLPort();
            return lcHost + (lnPort != 443 ? ":" + Integer.toString(lnPort) : "");
        }
        return null;
    }
    
    @Override
    public String getFullHost()
    {
        String lcHost = getHost();
        int lnPort = getPort();
        return lcHost + (lnPort != 80 ? ":" + Integer.toString(lnPort) : "");
    }

    @Override
    public String getHash()
    {
        return getPath().replaceAll("^.+#", "");
    }

    @Override
    public int getPort()
    {
        return Server.getServer().getAddress().getPort();
    }

    @Override
    public String getSSLHost()
    {
        InetSocketAddress loAddress = Server.getServer().getSSLAddress();
        return loAddress == null ? null : loAddress.getHostName();
    }

    @Override
    public int getSSLPort()
    {
        InetSocketAddress loAddress = Server.getServer().getSSLAddress();
        return loAddress == null ? -1 : loAddress.getPort();
    }
}
