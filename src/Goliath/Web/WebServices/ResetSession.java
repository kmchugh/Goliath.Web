/* =========================================================
 * ResetSession.java
 *
 * Author:      home_stanbridge
 * Created:     June 15, 2010, 14:00:00 PM
 *
 * Description
 * --------------------------------------------------------
 * <Description>
 *  Web service to execute a reset on the user/requester session and return
 *  xml of session information
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Web.WebServices;

import Goliath.Interfaces.ISession;

/**
 *
 * @author home_stanbridge
 */
public class ResetSession extends GetSessionInformation
{

     /**
     * get session and reset. After reset call super
     * to return the updated session information
     * @param toBuilder string containing the xml
     * @return true if the parent wrapper to be included in returned xml
     */
    @Override
    protected boolean onDoGetWebService(StringBuilder toBuilder)
    {
        ISession loSession = getSession();
        loSession.reset();
        super.onDoGetWebService(toBuilder);
        return true;
    }
}

