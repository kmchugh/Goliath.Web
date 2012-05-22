/* ========================================================
 * ServletCommandArgs.java
 *
 * Author:      kenmchugh
 * Created:     Mar 13, 2011, 1:00:20 AM
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

import Goliath.Arguments.Arguments;
import Goliath.Interfaces.Servlets.IServlet;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;


        
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
public class ServletCommandArgs extends Arguments
{

    private IServlet m_oServlet;
    private IHTTPRequest m_oRequest;
    private IHTTPResponse m_oResponse;

    /**
     * Creates a new instance of ServletCommandArgs
     */
    public ServletCommandArgs(IServlet toServlet, IHTTPRequest toHTTPRequest, IHTTPResponse toHTTPResponse)
    {
        m_oServlet = toServlet;
        m_oRequest = toHTTPRequest;
        m_oResponse = toHTTPResponse;
    }

    /**
     * Gets the servlet related to this command
     * @return the servlet this command is related to
     */
    public IServlet getServlet()
    {
        return m_oServlet;
    }

    /**
     * Gets the request that is being processed
     * @return the request
     */
    public IHTTPRequest getRequest()
    {
        return m_oRequest;
    }

    /**
     * Gets the response that is being processed
     * @return the response
     */
    public IHTTPResponse getResponse()
    {
        return m_oResponse;
    }
}
