/* ========================================================
 * IServletContext.java
 *
 * Author:      kenmchugh
 * Created:     Mar 12, 2011, 11:17:07 PM
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

package Goliath.Interfaces.Servlets;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;



/**
 * Interface Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Mar 12, 2011
 * @author      kenmchugh
**/
public interface IServletContext
{
    String getURLPattern();
    
    IServletContext getContext(String tcURIPath);

    abstract int getMajorVersion();
    abstract int getMinorVersion();
    URL getResource(String tcPath) throws MalformedURLException;
    String getMimeType(String tcFile);
    InputStream getResourceAsStream(String tcPath);
    IRequestDispatcher getRequestDispatcher(String tcURLPath);
    void log(String tcMessage);
    void log(Exception toEx, String tcMessage);
    void log(String tcMessage, Throwable toEx);
    String getRealPath(String tcPath);
    String getServerInfo();
    Object getAttribute(String tcName);
    Enumeration<String> getAttributeNames();
    void setAttribute(String tcName, Object toObject);
    void removeAttribute(String tcName);






}
