/* ========================================================
 * IRequestDispatcher.java
 *
 * Author:      kenmchugh
 * Created:     Mar 12, 2011, 11:24:48 PM
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
public interface IRequestDispatcher
{
    /**
     * Forwards the request to the specified handler
     * @param toRequest the request to forward
     * @param toResponse the response to write to
     * @throws ServletException
     * @throws IOException
     */
    void forward(IHTTPRequest toRequest,
                              IHTTPResponse toResponse)
                              throws ServletException, IOException;

    void include(IHTTPRequest toRequest,
                              IHTTPResponse toResponse)
                              throws ServletException, IOException;

    /**
     * Gets the servlet that this dispatcher controls
     * @return the servlet for this dispatcher
     */
    IServlet getServlet();



}
