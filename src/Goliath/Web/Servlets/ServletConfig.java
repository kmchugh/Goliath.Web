/* ========================================================
 * ServletConfig.java
 *
 * Author:      kenmchugh
 * Created:     Mar 12, 2011, 10:57:10 PM
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

import Goliath.Collections.List;
import Goliath.Collections.PropertySet;
import Goliath.Interfaces.Servlets.IServletConfig;
import Goliath.Interfaces.Servlets.IServletContext;


        
/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Mar 12, 2011
 * @author      kenmchugh
**/
public class ServletConfig extends Goliath.Object
        implements IServletConfig
{
    private IServletContext m_oContext;
    private PropertySet m_oInitParameters;

    /**
     * Creates a new instance of ServletConfig
     */
    public ServletConfig()
    {
    }

    @Override
    public void setServletContext(String tcContext)
    {
        m_oContext = new ServletContext(tcContext);
    }

    @Override
    public Object getInitParameter(String tcName)
    {
        return m_oInitParameters == null ? null : (String)m_oInitParameters.getProperty(tcName);
    }

    @Override
    public List<String> getInitParameterNames()
    {
        return m_oInitParameters == null ? null : m_oInitParameters.getPropertyKeys();
    }

    public boolean setParameter(String tcParameter, Object toValue)
    {
        if (m_oInitParameters == null)
        {
            m_oInitParameters = new PropertySet();
        }
        return m_oInitParameters.setProperty(tcParameter, toValue);
    }

    @Override
    public IServletContext getServletContext()
    {
        return m_oContext;
    }




}
