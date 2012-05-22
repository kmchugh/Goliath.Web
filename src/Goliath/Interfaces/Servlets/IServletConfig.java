/* ========================================================
 * IServletConfig.java
 *
 * Author:      kenmchugh
 * Created:     Mar 12, 2011, 11:13:03 PM
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

import Goliath.Collections.List;



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
public interface IServletConfig
{
    IServletContext getServletContext();

    Object getInitParameter(String tcName);

    List<String> getInitParameterNames();

    void setServletContext(String tcContext);

}
