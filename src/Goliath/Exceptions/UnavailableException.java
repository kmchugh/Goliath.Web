/* ========================================================
 * UnavailableException.java
 *
 * Author:      kenmchugh
 * Created:     Mar 13, 2011, 12:18:31 PM
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
 * @version     1.0 Mar 13, 2011
 * @author      kenmchugh
**/
public class UnavailableException extends ServletException
{
    private long m_nWaitTime;

    /**
     * Creates a new instance of UnavailableException
     */
    public UnavailableException(long tnWaitTime)
    {
        super("The services is not available for " + tnWaitTime + "ms");
        m_nWaitTime = tnWaitTime;
    }

    public long getWaitTime()
    {
        return m_nWaitTime;
    }
}
