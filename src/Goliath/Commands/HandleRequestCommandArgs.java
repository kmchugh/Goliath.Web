/* =========================================================
 * HandleRequestCommandArgs.java
 *
 * Author:      kmchugh
 * Created:     09-Apr-2008, 15:06:06
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

package Goliath.Commands;

import Goliath.Arguments.Arguments;
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
 * @version     1.0 09-Apr-2008
 * @author      kmchugh
**/
public class HandleRequestCommandArgs extends Arguments
{
    private IHTTPRequest m_oRequest = null;
    private IHTTPResponse m_oResponse = null;
    
    /** Creates a new instance of HandleRequestCommandArgs
     * @param toRequest the request
     * @param toResponse the response
     */
    public HandleRequestCommandArgs(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        m_oRequest = toRequest;
        m_oResponse = toResponse;
    }
    
    /**
     * Gets the request associated with this command
     * @return the request
     */
    public IHTTPRequest getRequest()
    {
        return m_oRequest;
    }
    
    /**
     * Gets the response associated with this command
     * @return the response
     */
    public IHTTPResponse getResponse()
    {
        return m_oResponse;
    }
}
