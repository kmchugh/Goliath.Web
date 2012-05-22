/* ========================================================
 * InvalidMethodExceptoin.java
 *
 * Author:      kenmchugh
 * Created:     Mar 13, 2011, 3:27:42 PM
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

package Goliath.Exceptions;

import Goliath.Interfaces.Web.IHTTPRequest;


        
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
public class InvalidProtocolException extends ServletException
{
    /**
     * Creates a new instance of InvalidMethodExceptoin
     */
    public InvalidProtocolException(IHTTPRequest toRequest)
    {
        super("The service at " + toRequest.getPath() + " does not support " + toRequest.getProtocol().getValue());
    }
}
