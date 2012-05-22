/* =========================================================
 * RequestMethods.java
 *
 * Author:      kmchugh
 * Created:     29-May-2008, 12:17:26
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

package Goliath.Web.Constants;


/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 29-May-2008
 * @author      kmchugh
**/
public class RequestMethod extends Goliath.DynamicEnum
{
    /**
     * Creates a new instance of a RequestMethods Object 
     *
     * @param tcValue The value for the string format type
     * @throws Goliath.Exceptions.InvalidParameterException
     */
    protected RequestMethod(String tcValue)
    {
        super(tcValue);
    }
    
    private static RequestMethod g_oGet;
    public static RequestMethod GET()
    {
        if (g_oGet == null)
        {
            g_oGet = createEnumeration(RequestMethod.class, "GET", (java.lang.Object[])null);
        }
        return g_oGet;
    }
    
    private static RequestMethod g_oPost;
    public static RequestMethod POST()
    {
        if (g_oPost == null)
        {
            g_oPost = createEnumeration(RequestMethod.class, "POST", (java.lang.Object[])null);
        }
        return g_oPost;
    }
    
    private static RequestMethod g_oDelete;
    public static RequestMethod DELETE()
    {
        if (g_oDelete == null)
        {
            g_oDelete = createEnumeration(RequestMethod.class, "DELETE", (java.lang.Object[])null);
        }
        return g_oDelete;
    }
    
    private static RequestMethod g_oHead;
    public static RequestMethod HEAD()
    {
        if (g_oHead == null)
        {
            g_oHead = createEnumeration(RequestMethod.class, "HEAD", (java.lang.Object[])null);
        }
        return g_oHead;
    }
    
    private static RequestMethod g_oUnknown;
    public static RequestMethod UNKNOWN()
    {
        if (g_oUnknown == null)
        {
            g_oUnknown = createEnumeration(RequestMethod.class, "UNKNOWN", (java.lang.Object[])null);
        }
        return g_oUnknown;
    }
    
    private static RequestMethod g_oPut;
    public static RequestMethod PUT()
    {
        if (g_oPut == null)
        {
            g_oPut = createEnumeration(RequestMethod.class, "PUT", (java.lang.Object[])null);
        }
        return g_oPut;
    }
    
    private static RequestMethod g_oOptions;
    public static RequestMethod OPTIONS()
    {
        if (g_oOptions == null)
        {
            g_oOptions = createEnumeration(RequestMethod.class, "OPTIONS", (java.lang.Object[])null);
        }
        return g_oOptions;
    }

    private static RequestMethod g_oTrace;
    public static RequestMethod TRACE()
    {
        if (g_oTrace == null)
        {
            g_oTrace = createEnumeration(RequestMethod.class, "TRACE", (java.lang.Object[])null);
        }
        return g_oTrace;
    }

    private static RequestMethod g_oConnect;
    public static RequestMethod CONNECT()
    {
        if (g_oConnect == null)
        {
            g_oConnect = createEnumeration(RequestMethod.class, "CONNECT", (java.lang.Object[])null);
        }
        return g_oConnect;
    }
    
}
