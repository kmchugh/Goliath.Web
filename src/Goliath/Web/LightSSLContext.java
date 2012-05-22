/* =========================================================
 * SSLContext.java
 *
 * Author:      kmchugh
 * Created:     05-Jul-2008, 20:25:37
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

import Goliath.Exceptions.CriticalException;
import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 05-Jul-2008
 * @author      kmchugh
**/
public class LightSSLContext
{
    private SSLContext m_oSSLContext;
    
    /** Creates a new instance of SSLContext */
    public LightSSLContext(String tcKeyFile, String tcPasscode)
    {
        char[] laPasscode = tcPasscode.toCharArray();
        KeyManagerFactory loKeyManager = null;
        TrustManagerFactory loTrustManager = null;
        try
        {
            KeyStore loKeyStore = KeyStore.getInstance("JKS");
            loKeyStore.load(new FileInputStream(tcKeyFile), laPasscode);

            loKeyManager = KeyManagerFactory.getInstance("SunX509");
            loKeyManager.init(loKeyStore, laPasscode);

            loTrustManager = TrustManagerFactory.getInstance("SunX509");
            loTrustManager.init(loKeyStore);
         }
         catch (Exception ex)
         {
            throw new CriticalException(ex);
         }
           
         try
         {
            m_oSSLContext = SSLContext.getInstance("TLS");
            m_oSSLContext.init(loKeyManager.getKeyManagers(), loTrustManager.getTrustManagers(), null);
         }
         catch (Exception ex)
         {
            throw new CriticalException(ex);
         }
    }
    
    public SSLContext getContext()
    {
        return m_oSSLContext;
    }
}
