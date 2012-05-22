/* ========================================================
 * WebApplicationController.java
 *
 * Author:      kenmchugh
 * Created:     Apr 30, 2011, 10:11:02 AM
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

package Goliath.Applications;

import Goliath.Constants.EventType;
import Goliath.Web.ServerConnection;


        
/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Apr 30, 2011
 * @author      kenmchugh
**/
public abstract class WebApplicationController<E extends EventType, A extends WebApplicationController<E, A>> extends Goliath.Applications.ApplicationController<E, A>
{
    public static WebApplicationController getInstance()
    {
        return (WebApplicationController)Application.getInstance().getApplicationController();
    }

    private ServerConnection m_oMasterServerConnection;
    private boolean m_lMasterLoaded;

    private ServerConnection m_oServerConnection;

    /**
     * The master server is the server that all of the data will be downloaded from.  If there is no master server
     * then this server is treated as the master
     *
     * @return the connection to the master server, or null if there is no master server.
     */
    protected ServerConnection getMasterServer()
    {
        if (!m_lMasterLoaded && m_oMasterServerConnection == null)
        {
            m_lMasterLoaded = true;
            m_oMasterServerConnection = Application.getInstance().getPropertyHandlerProperty("WebServer.MasterServerConnection");
        }
        return m_oMasterServerConnection;
    }

    /**
     * Gets the current Server connection for this application that this application should be acting on globally
     * 
     * @return The server connection or null if the server connection has not been set
     */
    public ServerConnection getServerConnection()
    {
        return m_oServerConnection;
    }

    /**
     * Sets the server connection that this application will work with on a global level
     * @param toConnection the new server connection to use
     */
    public void setServerConnection(ServerConnection toConnection)
    {
        m_oServerConnection = toConnection;
    }
}
