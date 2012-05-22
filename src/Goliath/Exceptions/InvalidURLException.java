/* ========================================================
 * InvalidURLException.java
 *
 * Author:      kenmchugh
 * Created:     May 23, 2011, 5:46:37 PM
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


        
/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 May 23, 2011
 * @author      kenmchugh
**/
public class InvalidURLException extends Exception
{
    /**
     * Creates a new instance of UnavailableException
     */
    public InvalidURLException(String tcMessage)
    {
        super(tcMessage);
    }

    /**
     * Creates a new instance of UnavailableException
     */
    public InvalidURLException(String tcMessage, boolean tlLogError)
    {
        super(tcMessage, tlLogError);
    }
}