/* ========================================================
 * IServlet.java
 *
 * Author:      kenmchugh
 * Created:     Mar 12, 2011, 10:47:38 PM
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

import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import java.io.IOException;



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
public interface IServlet
{
    /**
     * Initialises the servlet, this will only happen once in the lifetime
     * of the servlet, the servlet is not gauranteed to be ready for use until
     * after this method has been called once
     */
    void init(IServletConfig toConfig)
            throws ServletException;

    /**
     * Gets the configruation of the servlet
     * @return the servlet config
     */
    IServletConfig getServletConfig();

    /**
     * Executes the servlet, handling the request
     */
    void service(IHTTPRequest toRequest, IHTTPResponse toResponse)
            throws ServletException, IOException;

    /**
     * Destroys the sevlet, only called once in the lifecycle.  This should
     * clear up any resources held by the servlet
     */
    void destroy();

    /**
     * Gets an information string about the servlet
     * @return the information about the servlet
     */
    String getServletInfo();

    /**
     * Gets the maximum length of data the body can contain when sending to this servlet
     * @return the maximum length of body data
     */
    int getMaximumPostBodySize();
}
