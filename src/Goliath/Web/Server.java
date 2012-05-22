/* =========================================================
 * Server.java
 *
 * Author:      kmchugh
 * Created:     27-Feb-2008, 13:40:18
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
 * =======================================================*/
package Goliath.Web;

import Goliath.Applications.Application;
import Goliath.Constants.LogType;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import java.net.InetSocketAddress;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

/**
 * The web Server class
 *
 * @see         Related Class
 * @version     1.0 27-Feb-2008
 * @author      kmchugh
 **/
public class Server extends Goliath.Object
{
    // TODO: Implement this as a proper singleton
    private static Server g_oServer;

    public static Server getServer()
    {
        return g_oServer;
    }

    private com.sun.net.httpserver.HttpServer m_oServer;
    private com.sun.net.httpserver.HttpsServer m_oHTTPSServer;
    private String m_cHostName;

    // Get both ssl and non ssl contexts
    /** Creates a new instance of Server 
     * Does not use SSL
     */
    public Server()
    {
        this(false, null, null);
    }

    /**
     * Creates a new instance of server
     * @param tlUseSSL it true, then the server uses ssl for connections
     * @param tcKeyFile
     * @param tcPasscode
     */
    public Server(boolean tlUseSSL, String tcKeyFile, String tcPasscode)
    {
        this("localhost", new java.net.InetSocketAddress("localhost", 80), 500);
    }

    /**
     * Creates a new instance of the server
     * @param toHostName the host name of this server
     * @param toAddress the address to bind to
     * @param tnMaxConnections the maximum number of connections to this server
     */
    public Server(String tcHostName, java.net.InetSocketAddress toAddress, int tnMaxConnections)
    {
        this(tcHostName, toAddress, null, tnMaxConnections, null, null);
    }

    /**
     * Creates a new instance of server
     * @param toHostName the host name of this server
     * @param toHTTPAddress the address object for the regular non secured connection
     * @param toSSLAddress the address object for the secured connection
     * @param tnMaxConnections the maximum number of connections
     * @param tcKeyFile the name of the key file to use for ssl keys
     * @param tcPasscode the passcode for the ssl key file
     */
    public Server(String tcHostName, java.net.InetSocketAddress toHTTPAddress, java.net.InetSocketAddress toSSLAddress, int tnMaxConnections, String tcKeyFile, String tcPasscode)
    {
        m_cHostName = tcHostName;
        try
        {
            if (toSSLAddress != null)
            {
                com.sun.net.httpserver.HttpsServer loServer = com.sun.net.httpserver.HttpsServer.create(toSSLAddress, tnMaxConnections);
                m_oHTTPSServer = loServer;
                LightSSLContext loSSLContext = new LightSSLContext(tcKeyFile, tcPasscode);
                loServer.setHttpsConfigurator(new HttpsConfigurator(loSSLContext.getContext())
                {

                    @Override
                    public void configure(HttpsParameters params)
                    {
                        SSLContext loContext = getSSLContext();

                        // get the default parameters
                        SSLParameters loSSLparams = loContext.getDefaultSSLParameters();
                        params.setSSLParameters(loSSLparams);
                    }
                });

                Goliath.Applications.Application.getInstance().log("Secure Web server bound to " + toSSLAddress.getAddress().toString() + " port: " + Integer.toString(toSSLAddress.getPort()) + " Maximum Connections: " + Integer.toString(tnMaxConnections), Goliath.Constants.LogType.EVENT());

            }
            m_oServer = com.sun.net.httpserver.HttpServer.create(toHTTPAddress, tnMaxConnections);

            Goliath.Applications.Application.getInstance().log("Web server bound to " + toHTTPAddress.getAddress().toString() + " port: " + Integer.toString(toHTTPAddress.getPort()) + " Maximum Connections: " + Integer.toString(tnMaxConnections), Goliath.Constants.LogType.EVENT());
        }
        catch (java.net.BindException ex)
        {
            if (toHTTPAddress.getPort() <= 1024)
            {
                Application.getInstance().log("Failure to bind may be because of attempting to bind to a privileged port with a non privileged user", LogType.ERROR());
            }
            // TODO: Give better errors, for example when the ip address is not for the same machine
            throw new Goliath.Exceptions.ObjectNotCreatedException("Could not create web server, another service is already bound to " + toHTTPAddress.getAddress().toString(), ex);
        }
        catch (Throwable ex)
        {
            throw new Goliath.Exceptions.ObjectNotCreatedException("Could not create web server", ex);
        }

        // TODO: Create a thread factory that names threads so that we can identify them e.g. "WebServer_Thread"
        java.util.concurrent.Executor loExecutor = java.util.concurrent.Executors.newCachedThreadPool();
        m_oServer.setExecutor(loExecutor);
        m_oServer.start();
        if (m_oHTTPSServer != null)
        {
            m_oHTTPSServer.setExecutor(loExecutor);
            m_oHTTPSServer.start();
        }

        LightHTTPHandler loHandler = new LightHTTPHandler();
        for (String lcContext : loHandler.getContexts())
        {
            m_oServer.createContext(lcContext, loHandler);
            if (m_oHTTPSServer != null)
            {
                m_oHTTPSServer.createContext(lcContext, loHandler);
            }
        }

        // TODO: This is not the correct way of doing this
        g_oServer = this;
    }

    /**
     * Returns the unresolved host name of the server
     * @return the host name as provided when the server was created
     */
    public String getHostName()
    {
        return m_cHostName;
    }

    /**
     * Checks if this server can use SSL
     * @return true if SSL can be used by this server
     */
    public boolean usesSSL()
    {
        return m_oHTTPSServer != null;
    }

    /**
     * Gets the HTTPS Address this server is bound to
     * @return
     */
    public InetSocketAddress getSSLAddress()
    {
        return m_oHTTPSServer == null ? null : m_oHTTPSServer.getAddress();
    }

    /**
     * Gets the HTTPS Address this server is bound to
     * @return
     */
    public InetSocketAddress getAddress()
    {
        return m_oServer == null ? null : m_oServer.getAddress();
    }
}
