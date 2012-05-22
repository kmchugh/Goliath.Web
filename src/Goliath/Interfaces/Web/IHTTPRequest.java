/* =========================================================
 * IHTTPRequest.java
 *
 * Author:      kmchugh
 * Created:     29-May-2008, 11:49:06
 * 
 * Description
 * --------------------------------------------------------
 * General Interface Description.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/

package Goliath.Interfaces.Web;

import Goliath.Collections.List;
import Goliath.Collections.PropertySet;
import Goliath.Constants.MimeType;
import Goliath.Interfaces.Servlets.IRequestDispatcher;
import Goliath.Web.Constants.RequestMethod;
import Goliath.Web.Constants.RequestProtocol;
import Goliath.Web.Part;
import java.net.URI;
import java.util.Map;

/**
 * Interface Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 29-May-2008
 * @author      kmchugh
**/
public interface IHTTPRequest
{
    /**
     * Gets the current session associated with the request
     * @return the session associated with the request
     */
    Goliath.Interfaces.ISession getSession();

    /**
     * Gets all of the headers from the request
     * @return the headers
     */
    PropertySet getHeaders();

    /**
     * Gets the specified header value, if there are multiple values, this
     * will return only the first value
     * @param tcHeader the header to get
     * @return the header value
     */
    String getHeader(String tcHeader);
    
    /**
     * Gets the name of the host used for this request
     * @return the host used for this request
     */
    String getHost();

    /**
     * Gets the names of all the headers set for this request
     * @return the list of headers that have been set
     */
    List<String> getHeaderNames();

    /**
     * Gets all of the values for the specified header
     * @param tcHeader the header to get the values for
     * @return the list of values, or if the header is not set, an empty list
     */
    List<String> getHeaderValues(String tcHeader);

    /**
     * Gets the header as an integer value, if the header can not be parsed, this should
     * throw an InvalidParameterException
     * @param tcHeader the header to get the value for
     * @return the value parsed as an integer
     */
    int getIntHeader(String tcHeader);

    /**
     * Gets the length in bytes of the request body.
     * @return the length of the request body, or -1 if the length is unknown
     */
    int getContentLength();
    
    /**
     * Gets the file that was requested
     * @return the file name
     */
    String getFile();

    /**
     * Gets the path of the request
     * @return the path
     */
    String getPath();
    
    /**
     * Sets the path that is being requested
     * @param tcPath the path that is being requested
     */
    void setPath(String tcPath);

    /**
     * Gets the list of parameters as they are attached to the URL
     * @return the parameters on the url
     */
    String getQueryString();



    /**
     * Gets the address of the page that the user browsed to this page from
     * @return the address of the previous page
     */
    String getReferrer();
    
    /**
     * Gets the full URI of the request
     * @return the URI of the request
     */
    URI getURI();

    /**
     * Gets the value of a cookie
     * @param tcCookieName the name of the cookie to get
     * @return the value of the cookie
     */
    String getCookie(String tcCookieName);

    /**
     * Gets an integer value for the specified property, or zero if the property
     * does not exist
     * This will throw an invalid property exception if there are any problems parsing.
     * @param tcPropertyName the name of the property to get
     * @return the integer value of the property, or zero if there are problems parsing
     */
    int getIntProperty(String tcPropertyName);
    
    /**
     * Gets an float value for the specified property, or zero if the property
     * does not exist
     * @param tcPropertyName the name of the property to get
     * @return the integer value of the property, or zero if there are problems parsing
     */
    float getFloatProperty(String tcPropertyName);
    
    /**
     * Gets a string value for the specified property, or '' if the property
     * does not exist
     * @param tcPropertyName the name of the property to get
     * @return the string value of the property, or '' if there are problems parsing
     */
    String getStringProperty(String tcPropertyName);

    /**
     * Gets the body of the request
     * @return the body of the request
     */
    String getBody();
    
    /**
     * Gets the protocol that is being used for communication
     * @return the protocol
     */
    RequestProtocol getProtocol();

    /**
     * Gets the http protocol version that is being used for communication
     * @return the protocol version
     */
    String getHTTPProtocol();
    
    /**
     * Gets the method of this request (GET, POST, etc)
     * @return the method.
     */
    RequestMethod getMethod();

    /**
     * Forces the request to use the specified method
     * @param toMethod the method to use for this request
     */
    void setMethod(RequestMethod toMethod);
    
    /**
     * Gets the user agent that has connected to the server
     * @return the user agent that is connected
     */
    String getUserAgent();
    
    /**
     * Returns true if the session cookie has been set
     * @return true if the session cookie has already been set and retrieved from the client
     */
    boolean isSessionCookieSet();

    /**
     * Returns the Character set that is preferred
     * @return A string representing the character set that should be used when writing back to the client
     */
    String getPreferredCharSet();

    /**
     * Gets the content type of the body of this request
     * @return the content type of the body of the request
     */
    MimeType getContentType();

    /**
     * Checks if this request has any form data
     * @return true if there is form data
     */
    boolean hasFormPostData();

    /**
     * Checks if this request has form multipart
     * @return
     */
    boolean isMultiPart();
    
    /**
     * Gets the Servlet handler for the context specified
     * @param tcContext the context to use to process the request
     * @return the RequestDispatcher to handle the context
     */
    IRequestDispatcher getRequestDispatcher(String tcContext);

    /**
     * Gets the remote address for the request
     * @return the remote address
     */
    java.net.InetSocketAddress getRemoteAddress();

    /**
     * Returns true if this request has been made on an ssl connection
     * @return true if secure false otherwise
     */
    boolean isSecure();

    /**
     * Gets the list of parts available to this request
     * @return the list of parts, if there are no parts, this will return an empty list
     */
    public List<Part> getParts();

    /**
     * Gets the specified part from the list of parts in this request
     * @param tcName the name of the part to get
     * @return returns the part, or null if the part does not exist
     */
    public Part getPart(String tcName);

    /**
     * Checks if the part exists in this request
     * @param tcName the name of the part
     * @return true if the part exists
     */
    public boolean hasPart(String tcName);

    /**
     * Returns the parameter specified, if the parameter does not exist,
     * then this will return null.  If the parameter has more than one value, then
     * this method will return the first value
     * @param tcParameterName the name of the paramter to get
     * @return the parameter value, or first parameter value, or null if no parameter is set
     */
    String getParameter(String tcParameterName);

    /**
     * Gets the names of all the available parameters and returns them as an array
     * @return the list of all the available parameters
     */
    String[] getParameterNames();

    /**
     * Gets the list of parameter values for the specified parameter
     * @param tcParameterName the name of the parameter to get the values for
     * @return the list of all the parameters, or an empty list if no values are available
     */
    String[] getParameterValues(String tcParameterName);

    /**
     * Checks if this request has the specified property set
     * @param tcParameterName the property to check for
     * @return true if the property is set
     */
    boolean hasParameter(String tcParameterName);

    /**
     * Clears the specified parameter from the propertyset, returns true or false
     * depending on if the property set has been changed as a result of this call, true means it has been changed
     * @param tcPropertyName the name of the property to clear
     * @return true if the property set has changed as a result of this call, false otherwise
     */
    boolean clearParameter(String tcParameterName);

    /**
     * Sets the value of the parameter specified, overwrites any existing setting
     * @param tcPropertyName the name of the property
     * @param tcPropertyValue the value of the property
     * @return true if the property set was modified as a result of this call
     */
    boolean setParameter(String tcParameterName, String tcValue);

    /**
     * Adds the specifed value to the list of parameters, if the parameter already has a value, this
     * method will append the value to the list of values attributed to the parameter
     * @param tcPropertyName the name of the parameter
     * @param tcPropertyValue the value of the parameter
     * @return true if the property set was modified as a result of this call
     */
    boolean addParameter(String tcParameterName, String tcValue);
    
    // TODO: Create javadoc comments for this method
    Map<String, Object> getParameterMap();
    
    /**
     * Gets the SSL Host name for the server this request is on
     * @return the ssl host name or null
     */
    String getSSLHost();
    
    /**
     * Gets the SSL Port of ther server this request on
     * @return the SSL Port, or -1 if there is no SSL
     */
    int getSSLPort();
    
    /**
     * Gets the full host including port of the Server
     * @return the full hostname and port
     */
    String getFullHost();
    
    /**
     * Gets the full host including port of the SSL Server
     * @return the full hostname and port
     */
    String getSSLFullHost();
    
    /**
     * Gets the port that this request is running on
     * @return the port number
     */
    int getPort();
    
    /**
     * Gets the Hash part of the URL if it exists
     * @return the hash of the url
     */
    String getHash();
}

