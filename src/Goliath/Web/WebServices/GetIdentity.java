    /* =========================================================
 * GetIdentity.java
 *
 * Author:      home_stanbridge
 * Created:     June 23, 2010, 12:00:00 PM
 *
 * Description
 * --------------------------------------------------------
 * <Description>
 *  Web service to get identity information about the current session
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Web.WebServices;

import Goliath.Interfaces.ISession;
import Goliath.Collections.List;
import Goliath.Security.Group;
import Goliath.Security.User;
/**
 *
 * @author home_stanbridge
 */
    public class GetIdentity extends WebServiceCommand
{
    /**
     * get identity information about the current session
     * and return (in WS XML format) the values from the session
     * @param toBuilder - the XML string
     * @return set to true if the wrapper xml is to be included.
     */
    @Override
    protected boolean onDoGetWebService(StringBuilder toBuilder)
    {
        ISession loSession = getSession();
        User loUser = loSession.getUser();
        List<Group> loGroups = loUser.getMemberOfList();
        toBuilder.append("<SessionIdentity version=\"1.0\">");
        // pull of field values
        toBuilder.append("<DisplayName>" + loUser.getDisplayName() + "</DisplayName>");
        toBuilder.append("<UserName>" + loUser.getName() + "</UserName>");
        toBuilder.append("<Email>" + loUser.getEmailAddress() + "</Email>");
        toBuilder.append("<ExpiryDate>" + loUser.getExpiry().getLong() + "</ExpiryDate>");
        toBuilder.append("<GUID>" + loUser.getGUID() + "</GUID>");
        toBuilder.append("<UserID>" + loUser.getID() + "</UserID>");
        toBuilder.append("<GroupMembership>");
        // user group values - NOTE taken the three attributes here but checking with Ken regarding other attributes.
        for (Group loGroup : loGroups)
        {
            toBuilder.append("<GroupMember>");
            toBuilder.append("<Name>" + loGroup.getName() + "</Name>");
            toBuilder.append("<Description>" + loGroup.getDescription() + "</Description>");
            toBuilder.append("<GUID>" + loGroup.getGUID() + "</GUID>");
            toBuilder.append("<GroupID>" + loGroup.getID() + "</GroupID>");
            toBuilder.append("</GroupMember>");
        }
        toBuilder.append("</GroupMembership>");
        toBuilder.append("</SessionIdentity>");
        return true;
    }

}
