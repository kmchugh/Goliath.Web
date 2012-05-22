/* ========================================================
 * ServletContext.java
 *
 * Author:      kenmchugh
 * Created:     Mar 13, 2011, 12:04:47 PM
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
import Goliath.Exceptions.FileNotFoundException;
import Goliath.Interfaces.Servlets.IRequestDispatcher;
import Goliath.Interfaces.Servlets.IServletContext;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;


        
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
public class ServletContext extends Goliath.Object
        implements IServletContext 
{
    private String m_cURLPattern;

    /**
     * Creates a new instance of ServletContext
     */
    public ServletContext(String tcURLPattern)
    {
        m_cURLPattern = tcURLPattern;
    }

    @Override
    public String getURLPattern()
    {
        return m_cURLPattern;
    }

    @Goliath.Annotations.NotProperty
    @Override
    public Object getAttribute(String tcName)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Goliath.Annotations.NotProperty
    @Override
    public Enumeration<String> getAttributeNames()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Goliath.Annotations.NotProperty
    @Override
    public IServletContext getContext(String tcURIPath)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Goliath.Annotations.NotProperty
    @Override
    public int getMajorVersion()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Goliath.Annotations.NotProperty
    @Override
    public String getMimeType(String tcFile)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Goliath.Annotations.NotProperty
    @Override
    public int getMinorVersion()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * This method should be used to find the actual path for the specified resource.
     * @param tcPath the path to get the resource for
     * @return the real path to a resource
     */
    @Override
    public String getRealPath(String tcPath)
    {   
        // If the path starts with a "/", then we want to prepend a "." so we stay in the correct root
        tcPath = Goliath.Utilities.getRegexMatcher("^([/\\\\])", tcPath).replaceFirst(".$1");
        tcPath = Goliath.Utilities.getRegexMatcher("\\?.+$|#.+$", tcPath).replaceFirst("");
        try
        {
            return new Goliath.IO.File(tcPath).getPath();
        }
        catch(FileNotFoundException ex)
        {
            try
            {
                // Try htdocs
                return new Goliath.IO.File(Goliath.Utilities.getRegexMatcher("(?i)^[\\.]+[/]+htdocs/|^\\.", tcPath).replaceFirst(Application.getInstance().getDirectory("htdocs"))).getPath();
            }
            catch (FileNotFoundException ex1)
            {}
            
            try
            {
                // Try htdocs
                return new Goliath.IO.File(Goliath.Utilities.getRegexMatcher("(?i)^[\\.]+[/]+resources/|^\\.", tcPath).replaceFirst(Application.getInstance().getDirectory("resources"))).getPath();
            }
            catch (FileNotFoundException ex1)
            {}
        }
        return tcPath;
    }

    @Goliath.Annotations.NotProperty
    @Override
    public IRequestDispatcher getRequestDispatcher(String tcURLPath)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Goliath.Annotations.NotProperty
    @Override
    public URL getResource(String tcPath) throws MalformedURLException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Goliath.Annotations.NotProperty
    @Override
    public InputStream getResourceAsStream(String tcPath)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Goliath.Annotations.NotProperty
    @Override
    public String getServerInfo()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Goliath.Annotations.NotProperty
    @Override
    public void log(String tcMessage)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Goliath.Annotations.NotProperty
    @Override
    public void log(Exception toEx, String tcMessage)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Goliath.Annotations.NotProperty
    @Override
    public void log(String tcMessage, Throwable toEx)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Goliath.Annotations.NotProperty
    @Override
    public void removeAttribute(String tcName)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Goliath.Annotations.NotProperty
    @Override
    public void setAttribute(String tcName, Object toObject)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
