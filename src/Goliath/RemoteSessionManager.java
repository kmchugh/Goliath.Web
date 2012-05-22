/* ========================================================
 * RemoteSessionManager.java
 *
 * Author:      christinedorothy
 * Created:     May 25, 2011, 2:54:35 PM
 *
 * Description
 * --------------------------------------------------------
 * Manages RemoteSession in desktop applications
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * ===================================================== */

package Goliath;

import Goliath.Session;


        
/**
 * This class manages RemoteSession object and is used in desktop applications.
 *
 * @version     1.0 May 25, 2011
 * @author      christinedorothy
**/
public class RemoteSessionManager extends Goliath.SessionManager
{

    /**
     * Creates a RemoteSession object with id as given in the parameter.
     *
     * @param tcSessionID   The session id to be assigned to the newly created RemoteSession
     * @return The newly created {@link RemoteSession} object
     */
    @Override
    protected Goliath.Interfaces.ISession onCreateSession(String tcSessionID)
    {
        return Session.isSystemSession(tcSessionID) ? super.onCreateSession(tcSessionID) : new Goliath.RemoteSession(tcSessionID);
    }

}
