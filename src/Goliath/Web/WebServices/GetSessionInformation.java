/* =========================================================
 * GetSessionInformation.java
 *
 * Author:      home_stanbridge
 * Created:     June 15, 2010, 12:00:00 PM
 *
 * Description
 * --------------------------------------------------------
 * <Description>
 *  Web service to get particular values of the current user/requester session
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Web.WebServices;

import Goliath.Interfaces.ISession;
import Goliath.Date;
import org.w3c.dom.Document;
/**
 *
 * @author home_stanbridge
 */
    public class GetSessionInformation extends WebServiceCommand
{
    /**
     * get the Session and return (in WS XML format) the values from the session
     * @param toBuilder - the XML string
     * @return set to true if the wrapper xml is to be included. 
     */
    @Override
    protected boolean onDoGetWebService(StringBuilder toBuilder)
    {
        /* Each time we get the session information for this session, we will also increase the expiry time, this is done on the assumption
          That if you are on the page for more than 5 seconds, chances are you will stay longer. */
        
        ISession loSession = getSession();
        Long loCount = loSession.getProperty("SessionInformationCount");
        loCount = (loCount == null) ? 1 : loCount + 1;
        loSession.setProperty("SessionInformationCount", loCount);

        toBuilder.append("<SessionInformation version=\"1.0\">");
        toBuilder.append("<SessionID>");
        toBuilder.append(loSession.getSessionID());
        toBuilder.append("</SessionID>");
        toBuilder.append("<UserGUID>");
        toBuilder.append(loSession.getUserGUID());
        toBuilder.append("</UserGUID>");
        toBuilder.append("<ExpiryLength>");
        toBuilder.append(loSession.getExpiryLength());
        toBuilder.append("</ExpiryLength>");
        toBuilder.append("<Authenticated>");
        toBuilder.append(loSession.isAuthenticated());
        toBuilder.append("</Authenticated>");
        toBuilder.append("<Expired>");
        toBuilder.append(loSession.isExpired());
        toBuilder.append("</Expired>");
        toBuilder.append("<Expires>");
        toBuilder.append(new Date(loSession.getExpiry()).getLong());
        toBuilder.append("</Expires>");
        toBuilder.append("<UserDisplayName>");
        toBuilder.append(loSession.getDisplayName());
        toBuilder.append("</UserDisplayName>");
        toBuilder.append("<MultiLingual>");
        toBuilder.append(loSession.isMultiLingual());
        toBuilder.append("</MultiLingual>");
        toBuilder.append("<Language");
        toBuilder.append((loSession.isMultiLingual() ? ">" + loSession.getLanguage() + "</Language>" : "/>"));
        toBuilder.append("</SessionInformation>");
        return true;
    }

    @Override
    protected boolean onDoPostWebService(StringBuilder toBuilder, Document toXML)
    {
        return onDoGetWebService(toBuilder);
    }



}
