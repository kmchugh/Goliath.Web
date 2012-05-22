/* ========================================================
 * Part.java
 *
 * Author:      kenmchugh
 * Created:     Jun 9, 2011, 6:38:18 PM
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

package Goliath.Web;

import Goliath.Collections.List;
import Goliath.Collections.PropertySet;
import Goliath.Constants.MimeType;
import Goliath.Web.Constants.ContentDisposition;
import java.io.File;
import java.io.InputStream;


        
/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Jun 9, 2011
 * @author      kenmchugh
**/
public class Part extends Goliath.Object
{
    private String m_cName;
    private MimeType m_oContentType;
    private ContentDisposition m_oContentDisposition;
    private PropertySet m_oProperties;
    private StringBuilder m_oContents;
    private File m_oFileContents;
    /**
     * Creates a new instance of Part
     */
    public Part(PropertySet toProperties)
    {
        for (String lcKey : toProperties.getPropertyKeys())
        {
            setProperty(lcKey, toProperties.getProperty(lcKey));
        }
    }

    public void setFile(File toFile)
    {
        m_oFileContents = toFile;
    }

    public void setContents(StringBuilder toContents)
    {
        m_oContents = toContents;
    }
    
    

    private boolean setProperty(String tcProperty, Object toValue)
    {
        // The properties name, content-disposition, and content-type have special meaning
        if (tcProperty.equalsIgnoreCase("name") ||
                tcProperty.equalsIgnoreCase("content-disposition") ||
                tcProperty.equalsIgnoreCase("content-type"))
        {
            if (tcProperty.equalsIgnoreCase("name"))
            {
                m_cName = (String)toValue;
            }
            else if (tcProperty.equalsIgnoreCase("content-disposition"))
            {
                m_oContentDisposition = ContentDisposition.getEnumeration(ContentDisposition.class, (String)toValue);
            }
            else if (tcProperty.equalsIgnoreCase("content-type"))
            {
                m_oContentType = MimeType.getEnumeration(MimeType.class, (String)toValue);
            }
        }
        else
        {
            if (m_oProperties == null)
            {
                m_oProperties = new PropertySet();
            }
            return m_oProperties.setProperty(tcProperty, toValue);
        }
        return false;
    }

    public String getName()
    {
        return m_cName;
    }

    public ContentDisposition getContentDisposition()
    {
        return m_oContentDisposition;
    }

    public MimeType getContentType()
    {
        if (m_oContentType == null)
        {
            m_oContentType = MimeType.TEXT_PLAIN();
        }
        return m_oContentType;
    }

    public String getHeader(String tcHeader)
    {
        return m_oProperties != null ? (String)m_oProperties.getProperty(tcHeader) : null;
    }

    public List<String> getHeaderNames()
    {
        return m_oProperties != null ? m_oProperties.getPropertyKeys() : new List<String>(0);
    }

    public String getHeaderValues(String tcHeader)
    {
        return null;
    }

    public InputStream getInputStream()
    {
        return null;
    }

    public String getContent()
    {
        return m_oContents != null ? m_oContents.toString() : null;
    }

    public File getFile()
    {
        return m_oFileContents;
    }

    public long getSize()
    {
        return m_oContents == null ? (m_oFileContents == null ? 0 : m_oFileContents.length()) : m_oContents.length();
    }

    public boolean delete()
    {
        if (m_oFileContents != null)
        {
            return m_oFileContents.delete();
        }
        return false;
    }
}
