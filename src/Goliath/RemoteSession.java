/* ========================================================
 * RemoteSession.java
 *
 * Author:      christinedorothy
 * Created:     May 25, 2011, 2:59:12 PM
 *
 * Description
 * --------------------------------------------------------
 * Controls the session on remote client e.g. desktop application.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * ===================================================== */

package Goliath;

import Goliath.Applications.Application;
import Goliath.Applications.WebApplicationController;
import Goliath.Collections.PropertySet;
import Goliath.Security.User;
import Goliath.Web.ServerConnection;
import Goliath.Web.WebServices.WebServiceRequest;
import org.w3c.dom.Node;


        
/**
 * RemoteSession object is used as a session object in client application.
 * It informs the server that it is still alive by
 * making web service calls when it renews itself.
 *
 * @version     1.0 May 25, 2011
 * @author      christinedorothy
**/
public class RemoteSession extends Goliath.Session
{
    private long m_nExpiryLengthCheck;
    /**
     * Creates a new instance of RemoteSession and schedules a task {@link RenewSessionTask} to renew its expiry time
     */
    public RemoteSession(String tcSessionID)
    {
        super(tcSessionID);
        Application.getInstance().scheduleTask(new RenewSessionTask(this), getExpiryLength()/2);
    }

    /**
     * Gets the default expiry length of 5 minutes or as specified in
     * ApplicationSettings.settings under Application.Settings.DefaultSessionExpiryLength key.
     *
     * @return the default expiry length
     */
    @Override
    protected long getDefaultExpiryLength()
    {
        // Default expiry length for remote session is 5 mins (300000 ms)
        try
        {
            return isSystem() ? 300000L : Application.getInstance().getPropertyHandlerProperty("Application.Settings.DefaultSessionExpiryLength", 300000L);
        }
        catch (Exception e)
        {
            return 300000L;
        }
    }

    /**
     * Gets the authenticated expiry length of 10 minutes or as specified in
     * ApplicationSettings.settings under Application.Settings.AuthenticatedSessionExpiryLength key.
     *
     * @return the authenticated expiry length
     */
    protected long getAuthenticatedExpiryLength()
    {
        // Authenticated expiry length is used after the user is logged in
        // However this amount will still be validated against the max allowed session length in the server
        try
        {
            return isSystem() ? 600000L : Application.getInstance().getPropertyHandlerProperty("Application.Settings.AuthenticatedSessionExpiryLength", 600000L);
        }
        catch (Exception e)
        {
            return 600000L;
        }
    }

    /**
     * Authenticates the given username and password against local database.
     * If the username and password are valid, {@link RemoteSession.isAuthenticated()}
     * will return <code>true</code> and then this object's expiry length will be set
     * to a value returned by {@link RemoteSession.getAuthenticatedExpiryLength()}.
     *
     * @param toUser    the username to be authenticated
     * @param tcPassword    the password to be authenticated
     * @return <code>true</code> if the username and password is validated, <code>false</code> otherwise.
     */
    @Override
    public synchronized boolean authenticate(User toUser, String tcPassword)
    {
        boolean llReturn = super.authenticate(toUser, tcPassword);
        if(llReturn) 
        {
            setExpiryLength(getAuthenticatedExpiryLength());
        }
        return llReturn;
    }

    /**
     * Unauthenticate the session therefore {@link RemoteSession.isAuthenticated()} will return
     * <code>false</code>. It will then set this object's expiry length to the value
     * returned by {@link RemoteSesion.getDefaultExpiryLength()}.
     *
     * @return <code>true</code> if the unauthenticate is successful, <code>false</code> otherwise.
     */
    @Override
    public boolean unauthenticate()
    {
        boolean llReturn = super.unauthenticate();
        if(llReturn)
        {
            setExpiryLength(getDefaultExpiryLength());
        }
        return llReturn;
    }

    /**
     * When a server connection has been setup, this method makes a request
     * to the server to validate its expiry length by calling
     * a web service UpdateSessionExpiryService. It will then set this object's expiry length
     * to the returned allowed value given by the server. However if there is no
     * server connection, this method will just renew the object's next expiry time
     * locally without the server knowing that it is still alive.
     *
     * @return <code>true</code> if the renewing of the expiry time is successful,
     * <code>false</code> if the web service request has an error or fail to parse
     * the return value from the web service
     */
    public boolean remoteRenew()
    {
        long lnExpiryLength = getExpiryLength();
        // Only set up a new call to the server if the expiry time or expiry length has changed
        if (m_nExpiryLengthCheck != getExpiryLength() || (getRemainingTime() < m_nExpiryLengthCheck * .5))
        {
            m_nExpiryLengthCheck = lnExpiryLength;
            ServerConnection loConnection = WebApplicationController.getInstance().getServerConnection();
            if (loConnection != null)
            {
                PropertySet loProperties = new PropertySet();
                loProperties.setProperty("ExpiryLength", lnExpiryLength);

                try
                {
                    WebServiceRequest loRequest = loConnection.makeWebServiceRequest("UpdateSessionExpiryService", loProperties, getSessionID());

                    if (loRequest.hasErrors())
                    {
                        return false;
                    }
                    else
                    {
                        Node loNode = loRequest.getResult();
                        String lcAllowedExpiryLength = Goliath.XML.Utilities.getElementValue(loNode, "ExpiryLength");
                        if(Goliath.Utilities.isNullOrEmpty(lcAllowedExpiryLength))
                        {
                            return false;
                        }
                        else
                        {
                            long lnAllowedExpiryLength = Long.parseLong(lcAllowedExpiryLength);
                            if(lnAllowedExpiryLength!=lnExpiryLength)
                            {
                                super.setExpiryLength(lnAllowedExpiryLength);
                                m_nExpiryLengthCheck = lnAllowedExpiryLength;
                                return true;
                            }

                        }
                    }
                }
                catch (Throwable ex)
                {
                    Application.getInstance().log(ex);
                    return false;
                }
            }
        }
        return super.renew();
    }
    
}
