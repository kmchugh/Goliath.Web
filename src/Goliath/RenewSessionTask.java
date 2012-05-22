/* ========================================================
 * RemoteSessionObserverTask.java
 *
 * Author:      christinedorothy
 * Created:     May 26, 2011, 10:50:28 AM
 *
 * Description
 * --------------------------------------------------------
 * A task to renew a given session's next expiry time
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * ===================================================== */

package Goliath;

import Goliath.Applications.Application;
import Goliath.Constants.LogType;
import Goliath.Interfaces.ISession;


        
/**
 * RenewSessionTask renews the given {@link ISession} object given in the constructor
 * and then schedules itself for the next run.
 *
 * @version     1.0 May 26, 2011
 * @author      christinedorothy
**/
public class RenewSessionTask extends java.util.TimerTask
{
    private RemoteSession m_oSession;

    /**
     * Creates a new instance of RenewSessionTask.
     *
     * @param toSession     The session whose expiry time needs to be renewed
     */
    public RenewSessionTask(RemoteSession toSession)
    {
        m_oSession = toSession;
    }

    /**
     * If the session set in the constructor has not expired, this method will
     * renew the session and then schedule this task again for the next renew.
     * The next run of the task is half of the time from now to the session's next expiry time.
     * If the session has expired, this task will cancel itself and therefore the task
     * will not be executed anymore.
     */
    @Override
    public void run()
    {
        if(!m_oSession.isExpired())
        {
            m_oSession.remoteRenew();
            long lnInterval = Math.abs(System.currentTimeMillis()-m_oSession.getExpiry().getTime())/2;
            Application.getInstance().log(
                "New session life [" + m_oSession.getSessionID() + "] ExpiryLength: " + m_oSession.getExpiryLength() + 
                " NextExpiry: " + m_oSession.getExpiry() + " NextRenew: " + lnInterval,
                LogType.TRACE());
            Application.getInstance().scheduleTask(new RenewSessionTask(m_oSession), lnInterval);
        }
        else
        {
            // TODO: need to check if this remove the task from memory
            this.cancel();
            m_oSession = null;
        }

    }

}
