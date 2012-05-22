/* ========================================================
 * SessionInformation.java
 *
 * Author:      admin
 * Created:     Aug 9, 2011, 12:36:36 PM
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
package Goliath.Web.WebServices;

import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.ISession;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import java.io.IOException;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Aug 9, 2011
 * @author      admin
 **/
public class SessionInformation extends WebServiceServlet
{
    
    /**
     * Helper class for allowing easy conversion to XML or json
     */
    public class SessionInfo extends Goliath.Object
    {
        private ISession m_oSession;
        
        public SessionInfo(ISession toSession)
        {
            m_oSession = toSession;
        }
        
        public String getSessionID(){return m_oSession.getSessionID();}
        public String getUserGUID(){return m_oSession.getUserGUID();}
        public long getExpiryLength(){return m_oSession.getExpiryLength();}
        public boolean getAuthenticated(){return m_oSession.isAuthenticated();}
        public boolean getIsExpired(){return m_oSession.isExpired();}
        public long getExpires(){return m_oSession.getExpiry().getTime();}
        public String getUserDisplayName(){return m_oSession.getDisplayName();}
        public boolean getIsMultiLingual(){return m_oSession.isMultiLingual();}
        public Object getLanguage(){return m_oSession.getLanguage();}
        public boolean getIsAnonymous(){return m_oSession.getUser().isAnonymous();}
    }
    
    /**
     * Creates a new instance of SessionInformation
     */
    public SessionInformation()
    {
    }

    @Override
    protected void doGet(IHTTPRequest toRequest, IHTTPResponse toResponse, StringBuilder toBuffer) throws ServletException, IOException
    {
        appendObjectToResponse(toResponse, toBuffer, new SessionInfo(toResponse.getSession()));
    }
    
    
    
    
}
