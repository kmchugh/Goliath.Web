/* =========================================================
 * GetSessionCommandStatus.java
 *
 * Author:      home_stanbridge
 * Created:     June 18, 2010, 15:08:00 PM
 *
 * Description
 * --------------------------------------------------------
 * <Description>
 *  Web service to return the command status of any executing commands
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/
package Goliath.Web.WebServices;
import Goliath.Interfaces.ISession;
import Goliath.Collections.HashTable;

/**
 *
 * @author home_stanbridge
 */
public class GetSessionCommandStatus extends WebServiceCommand
{
     /**
     * return command status of any commands attached to this session
     * @param toBuilder string containing the xml
     * @return true if the parent wrapper to be included in returned xml
     */
    @Override
    protected boolean onDoGetWebService(StringBuilder toBuilder)
    {
    toBuilder.append(Goliath.XML.Utilities.toXMLString(getSession().getCommandStatus()));
    return true;
    }

}
