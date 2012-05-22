/* ========================================================
 * WebRequest.java
 *
 * Author:      kenmchugh
 * Created:     May 23, 2011, 4:05:08 PM
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

package Goliath.Web;

import Goliath.Web.Constants.ResultCode;
import Goliath.Applications.Application;
import Goliath.Collections.List;
import Goliath.Collections.PropertySet;
import Goliath.Constants.LogType;
import Goliath.Constants.MimeType;
import Goliath.DynamicCode.Java;
import Goliath.Interfaces.Applications.IApplicationSettings;
import Goliath.Interfaces.Web.IHTTPRequest;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.SSLContext;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import org.w3c.dom.Document;


        
/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 May 23, 2011
 * @author      kenmchugh
**/
public class WebRequest extends Goliath.Object
{
    private static SSLContext g_oSSLContext;
    private static int g_nDefaultTimeout = 30000;

    private MimeType m_oBodyContentType;
    private MimeType m_oContentType;
    private String m_cResourceURL;
    private String m_cBody;
    private ResultCode m_oResultCode;
    private List<String> m_oErrors;
    private String m_cResultString;
    private URLConnection m_oConnection;
    private String m_cSessionID;

    // TODO: Implement a check of a successful response based on the return code
    // TODO: Need to implement caching of requests
    // TODO: Need to implement checking of response expiry

    public WebRequest(String tcServerURL, IHTTPRequest toRequest, int tnTimeout, String tcSessionID)
    {
        String lcPath = toRequest.getPath();
        String lcFile = toRequest.getFile();
        String lcQueryString = toRequest.getQueryString();
        lcPath = lcPath.startsWith("/") ? lcPath.substring(1) : lcPath;
        lcQueryString = Goliath.Utilities.isNullOrEmpty(lcQueryString) ? "" : "?" + lcQueryString;

        // TODO: Implement streamed bodies
        m_cBody = toRequest.getBody();
        m_oBodyContentType = toRequest.getContentType();
        m_cSessionID = tcSessionID;

        setResourceURL(tcServerURL + lcPath + lcFile + lcQueryString);

        doRequest(tnTimeout);
    }

    public WebRequest(String tcResourceURL, String tcBody, MimeType toBodyType, String tcSessionID)
    {
        this(tcResourceURL, tcBody, g_nDefaultTimeout, toBodyType, tcSessionID);
    }

    public WebRequest(String tcResourceURL, String tcBody, int tnTimeout, MimeType toBodyType, String tcSessionID)
    {
        m_cBody = tcBody;
        m_oBodyContentType = toBodyType;
        m_cSessionID = tcSessionID;
        setResourceURL(tcResourceURL);

        doRequest(tnTimeout);
    }

    public WebRequest(String tcResourceURL, Document toBody, int tnTimeout, String tcSessionID)
    {
        m_cBody = Goliath.XML.Utilities.toString(toBody);
        m_oBodyContentType = MimeType.APPLICATION_XML();
        m_cSessionID = tcSessionID;
        setResourceURL(tcResourceURL);

        doRequest(tnTimeout);
    }

    public WebRequest(String tcResourceURL, Document toBody, String tcSessionID)
    {
        this(tcResourceURL, toBody, g_nDefaultTimeout, tcSessionID);
    }

    public WebRequest(String tcResourceURL, PropertySet toEncodedBody, int tnTimeout, String tcSessionID)
    {
        m_cBody = encodeAsData(toEncodedBody);
        m_cSessionID = tcSessionID;
        m_oBodyContentType = MimeType.APPLICATION_XML();
        setResourceURL(tcResourceURL);

        doRequest(tnTimeout);
    }

    public WebRequest(String tcResourceURL, PropertySet toEncodedBody, String tcSessionID)
    {
        this(tcResourceURL, toEncodedBody, g_nDefaultTimeout, tcSessionID);
    }

    public WebRequest(String tcResourceURL, int tnTimeout, String tcSessionID)
    {
        m_cSessionID = tcSessionID;
        setResourceURL(tcResourceURL);
        doRequest(tnTimeout);
    }

    public WebRequest(String tcResourceURL, String tcSessionID)
    {
        this(tcResourceURL, g_nDefaultTimeout, tcSessionID);
    }

    private void setResourceURL(String tcResourceURL)
    {
        m_cResourceURL = tcResourceURL;
        cleanResourceURL();
    }
    
    private String encodeAsData(PropertySet toProperties)
    {
        StringBuilder loBuilder = new StringBuilder("<Data>");

        loBuilder.append(Goliath.Utilities.encodeBase64(Goliath.JSON.Utilities.toJSON(toProperties)));

        loBuilder.append("</Data>");

        return loBuilder.toString();
    }

    // TODO: This needs to be refactored into an SSL Certificate manager
    private void updateSSLContext()
    {
        if (g_oSSLContext == null)
        {
            TrustManager[] loTrust = new TrustManager[]
            {
                new X509TrustManager()
                {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
                    {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
                    {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers()
                    {
                        return null;
                    }
                }
            };
            try
            {
                g_oSSLContext = SSLContext.getInstance("SSL");
                g_oSSLContext.init(null, loTrust, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(g_oSSLContext.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

                    @Override
                    public boolean verify(String hostname, SSLSession session)
                    {
                        return true;
                    }
                });
            }
            catch (Throwable ex)
            {
                Application.getInstance().log(ex);
            }
        }
    }

    private void doRequest(int tnTimeout)
    {
        // TODO: This is workaround for invalid SSL certificates.  This needs to be updated to use a Certificate Manager instead
        updateSSLContext();

        URL loURL = null;

        try
        {
            loURL = new URL(m_cResourceURL);
            m_oConnection = loURL.openConnection();
        }
        catch (Throwable ex)
        {
            addError(ex);
        }

        if (loURL != null)
        {
            m_oConnection.setConnectTimeout(tnTimeout);

            // If this is has a body, then it is a post request
            // TODO: Implement other request methods
            if (!Goliath.Utilities.isNullOrEmpty(m_cBody))
            {
                OutputStreamWriter loWriter = null;
                try
                {
                    m_oConnection.setRequestProperty("Content-Type", m_oBodyContentType.getValue());
                    m_oConnection.setDoOutput(true);

                    loWriter = new OutputStreamWriter(m_oConnection.getOutputStream());
                    loWriter.write(m_cBody);
                    loWriter.flush();
                    
                }
                catch (Throwable ex)
                {
                    Application.getInstance().log("Error writing to url " + m_cResourceURL, LogType.TRACE());
                    Application.getInstance().log(ex);
                    addError(ex);
                    setResultCode(ResultCode.INTERNAL_SERVER_ERROR());
                }
                finally
                {
                    if (loWriter != null)
                    {
                        try
                        {
                            loWriter.close();
                        }
                        catch (Throwable ex)
                        {}
                    }
                }
            }
        }
    }

    public InputStream getResponseStream()
    {
        try
        {
            if (wasSuccessful())
            {
                return m_oConnection.getInputStream();
            }
            else
            {
                if (Java.isEqualOrAssignable(HttpURLConnection.class, m_oConnection.getClass()))
                {
                    return ((HttpURLConnection)m_oConnection).getErrorStream();
                }
            }
        }
        catch (Throwable ex)
        {
            Application.getInstance().log(ex);
        }
        return null;
    }

    public final boolean hasErrors()
    {
        return !wasSuccessful() || getErrors().size() != 0;
    }

    public final List<String> getErrors()
    {
        if (m_oErrors == null)
        {
            m_oErrors = new List<String>(0);
        }
        return m_oErrors;
    }

    public final int getResultLength()
    {
        return m_oConnection.getContentLength();
    }

    public boolean wasSuccessful()
    {
        boolean llReturn = false;
        if (m_oConnection != null)
        {
            int lnCode = getResultCode().getCode();
            // We check the errors directly here otherwise we would go in to an endless loop
            llReturn = lnCode >= 200 && lnCode <300 && getErrors().size() == 0;
        }
        return llReturn;
    }
    
    protected void populateFailedErrors()
    {
        addError(new Goliath.Exceptions.Exception(m_oResultCode.getValue(), false));
    }

    public final ResultCode getResultCode()
    {
        if (m_oResultCode == null)
        {
            if (Java.isEqualOrAssignable(HttpURLConnection.class, m_oConnection.getClass()))
            {
                try
                {
                    m_oResultCode = ResultCode.getResultCode(((HttpURLConnection)m_oConnection).getResponseCode());

                    // If the result code is outside the successful range, add an error
                    if (!wasSuccessful())
                    {
                        populateFailedErrors();
                    }
                }
                catch (Throwable ex)
                {
                    m_oResultCode = ResultCode.INTERNAL_SERVER_ERROR();
                    addError(ex);
                    Application.getInstance().log(ex);
                }
            }
            else
            {
                Application.getInstance().log("Unknown Connection Class " + m_oConnection.getClass().getName(), LogType.WARNING());
            }
        }
        return m_oResultCode;
    }

    protected final void setResultCode(ResultCode toCode)
    {
        m_oResultCode = toCode;
    }

    public final String getResultDescription()
    {
        return m_oResultCode.getValue();
    }

    protected final void addError(Throwable ex)
    {
        getErrors().add(ex.getLocalizedMessage());
    }

    public MimeType getContentType()
    {
        if (m_oContentType == null)
        {
            m_oContentType = MimeType.getEnumeration(MimeType.class, m_oConnection.getContentType());
        }
        return m_oContentType;
    }

    private void cleanResourceURL()
    {
        if (!m_cResourceURL.startsWith("http:") && !m_cResourceURL.startsWith("https:"))
        {
            IApplicationSettings loSettings = Application.getInstance().getApplicationSettings();
            // Find the method for getting the server URL
            String lcValue = Goliath.DynamicCode.Java.getPropertyValue(loSettings, "ServerBaseURL");

            m_cResourceURL = lcValue + "/WS/" + Goliath.Utilities.encode(m_cResourceURL).replaceAll("%2F", "/");
        }

        if (m_cResourceURL.indexOf("sessionID") < 0)
        {
            m_cResourceURL += (m_cResourceURL.contains("?") ? "&" : "?" ) + "sessionID=" + m_cSessionID;
        }
    }

    public final String getResourceURL()
    {
        return m_cResourceURL;
    }

    public final String getResultString()
    {
        if (m_cResultString == null)
        {
            int lnBufferLength = 8192;
            StringBuilder loBuffer = new StringBuilder();
            int lnReadLength = 0;
            byte[] laBuffer = new byte[lnBufferLength];
            byte[] laTemp = null;
            InputStream loReader = new BufferedInputStream(getResponseStream());
            try
            {
                while((lnReadLength = loReader.read(laBuffer, 0, lnBufferLength)) != -1)
                {
                    laTemp = new byte[lnReadLength];
                    System.arraycopy(laBuffer, 0, laTemp, 0, lnReadLength);
                    loBuffer.append(Goliath.Utilities.toString(laTemp));
                }
            }
            catch(Throwable ex)
            {
                Application.getInstance().log(ex);
            }
            m_cResultString = loBuffer.toString();
        }
        return m_cResultString;
    }


    /**
     * Helper function to ensure the results have been read and parsed
     */
    private void ensureResult()
    {
        if (m_cResultString == null)
        {
            getResultString();
        }
    }
}