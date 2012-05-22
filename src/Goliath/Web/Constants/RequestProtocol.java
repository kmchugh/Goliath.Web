/* ========================================================
 * RequestProtocol.java
 *
 * Author:      kenmchugh
 * Created:     Mar 13, 2011, 3:14:57 PM
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

package Goliath.Web.Constants;

import Goliath.DynamicEnum;


        
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
public class RequestProtocol extends DynamicEnum
{
    private static RequestProtocol g_oHttp;
    public static RequestProtocol HTTP()
    {
        if (g_oHttp == null)
        {
            g_oHttp = new RequestProtocol("HTTP");
        }
        return g_oHttp;
    }


    private static RequestProtocol g_oHttps;
    public static RequestProtocol HTTPS()
    {
        if (g_oHttps == null)
        {
            g_oHttps = new RequestProtocol("HTTPS");
        }
        return g_oHttps;
    }

    private static RequestProtocol g_oUnknown;
    public static RequestProtocol UNKNOWN()
    {
        if (g_oUnknown == null)
        {
            g_oUnknown = new RequestProtocol("UNKNOWN");
        }
        return g_oUnknown;
    }

    /**
     * Creates a new instance of RequestProtocol
     */
    protected RequestProtocol(String tcValue)
    {
        super(tcValue);
    }


}
