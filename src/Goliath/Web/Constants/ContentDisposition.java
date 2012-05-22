/* ========================================================
 * ContentDisposition.java
 *
 * Author:      kenmchugh
 * Created:     Jun 9, 2011, 6:39:34 PM
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


        
/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Jun 9, 2011
 * @author      kenmchugh
**/
public class ContentDisposition extends Goliath.DynamicEnum
{
    protected ContentDisposition(String tcValue)
    {
        super(tcValue);
    }

    private static ContentDisposition g_oFormData;
    public static ContentDisposition FORM_DATA()
    {
        if (g_oFormData == null)
        {
            g_oFormData = createEnumeration(ContentDisposition.class, "FORM-DATA", (java.lang.Object[])null);
        }
        return g_oFormData;
    }
}