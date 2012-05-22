/* =========================================================
 * UpdateSessionExpiry.java
 *
 * Author:      home_stanbridge
 * Created:     June 15, 2010, 12:00:00 PM
 *
 * Description
 * --------------------------------------------------------
 * <Description>
 *  Web service to update the current user/requester session expiry length to
 *  the value specified in the "ExpiryLength" parameter
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Web.WebServices;

import Goliath.Exceptions.InvalidParameterException;
/**
 *
 * @author home_stanbridge
 */
public class UpdateSessionExpiry extends GetSessionInformation
{
    /**
     * get session and reset expiry length. After renew call super
     * to return the updated session information
     * @param toBuilder string containing the xml
     * @return true if the parent wrapper to be included in returned xml
     */
    @Override
    protected boolean onDoGetWebService(StringBuilder toBuilder)
    {
        String lcExpiryParameter = getRequest().getParameter("expiryLength");

        long lnExpiryLength = 0;
        try
        {
            lnExpiryLength = Long.parseLong(lcExpiryParameter);
            getSession().setExpiryLength(lnExpiryLength);
        }
        catch (NumberFormatException e)
        {
            addError(new InvalidParameterException("Invalid Expiry Length - [" + lcExpiryParameter + "]"));
        }

        return true;
    }

}