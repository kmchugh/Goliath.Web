/* =========================================================
 * Utilities.java
 *
 * Author:      kmchugh
 * Created:     03-Mar-2008, 12:10:03
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
import Goliath.Applications.ApplicationState;
import Goliath.Arguments.Arguments;
import Goliath.Collections.List;
import Goliath.Collections.PropertySet;
import Goliath.Constants.LogType;
import Goliath.DynamicCode.Java;
import Goliath.Exceptions.InvalidIOException;
import Goliath.Interfaces.ISession;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Session;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 03-Mar-2008
 * @author      kmchugh
**/
public class Utilities
{

    /** Creates a new instance of Utilities */
    private Utilities()
    {
    }

    /**
     * This method will parse the base64 encoded string and extract any name value pairs
     * that in the format {variable:"<variableStringValue>", variable2:"<variable2Value>"}
     * @param tcBase64Data the Base64 encoded string
     * @return the Name value pairs of all the values found
     */
    public static PropertySet parseEncodedData(String tcBase64Data)
    {
        // TODO: Create a helper method in the javascript for creating this type of data
        // TODO: This can be turned into a JSON Parser, need to use a tokenizer
        PropertySet loReturn = new PropertySet();

        if (!Goliath.Utilities.isNullOrEmpty(tcBase64Data))
        {
            tcBase64Data = Goliath.Utilities.decodeBase64(tcBase64Data);
            // Remove the first and last braces for ease
            if (tcBase64Data.indexOf("{") >= 0)
            {
                tcBase64Data = tcBase64Data.substring(tcBase64Data.indexOf("{") + 1);
                tcBase64Data = tcBase64Data.substring(0, tcBase64Data.lastIndexOf("}"));
                String[] laValues = tcBase64Data.split("[\\s]?,[\\s]?");

                for (int i=0, lnLength = laValues.length; i<lnLength; i++)
                {
                    String[] laValuePair = laValues[i].split("[\\s]?:[\\s]?", 2);
                    if (laValuePair.length == 2)
                    {
                        String lcProperty = laValuePair[0];
                        String lcValue = laValuePair[1];

                        lcProperty = cleanupValues(lcProperty, "\"");
                        lcValue = cleanupValues(lcValue, "\"");
                        loReturn.setProperty(lcProperty, lcValue);
                    }
                }
            }
            else
            {
                loReturn.setProperty("content", tcBase64Data);
            }
        }
        return loReturn;
    }

    private static String cleanupValues(String tcValue, String tcTrimChar)
    {
        tcValue = tcValue.trim();
        if (tcValue.startsWith(tcTrimChar))
        {
            tcValue = tcValue.substring(tcTrimChar.length());
        }

        if (tcValue.endsWith(tcTrimChar))
        {
            tcValue = tcValue.substring(0, tcValue.length() - tcTrimChar.length());
        }
        return tcValue;
    }
    
    /**
     * Parses requested expiry length in the xml document passed in.
     * XML document content will have the following elements
     *
     *  <ExpiryLength>30000</ExpiryLength>
     *
     * @return a property set containing the expiry length or <code>null</code> if the details couldn't be parsed
     */
    public static PropertySet getSessionDetails(Document toXML)
    {
        if (toXML == null)
        {
            return null;
        }
        if (toXML.getElementsByTagName("Data").getLength() > 0)
        {
            Node loNode = toXML.getElementsByTagName("Data").item(0);

            PropertySet loProperties = Goliath.Web.Utilities.parseEncodedData(loNode.getTextContent());

            // We must have expiry length. Session ID is passed in through the URL
            if (loProperties.hasProperty("ExpiryLength"))
            {
                return loProperties;
            }
        }

        // If we got this far, then either there was no data element, or the encoded data did not have the required fields
        if (toXML.getElementsByTagName("ExpiryLength").getLength() >0)
        {
            // We have the require fields as a minimum
            PropertySet loReturn = new PropertySet();
            loReturn.setProperty("ExpiryLength", toXML.getElementsByTagName("Email").item(0).getTextContent());

            return loReturn;
        }

        // We couldn't parse anything so return null
        return null;
    }


    public static void startWebBrowser(String tcURL)
    {
        // Start the default web browser if possible
        if (java.awt.Desktop.isDesktopSupported())
        {
            try
            {
                java.awt.Desktop.getDesktop().browse(new java.net.URI(tcURL));
            }
            catch (Exception ex)
            {
                // TODO: Change this to invalid parameter and add exception constructor to allow passing inner exception
                throw new Goliath.Exceptions.InvalidOperationException("URL is incorrect", ex);
            }
        }
        else
        {
            Application.getInstance().log("Desktop not supported for startWebBrowser command", LogType.WARNING());
        }
    }

    private static class URLCommandArgs extends Arguments
    {
        private String m_cURL;
        public URLCommandArgs(){}
        public void setURL(String tcUrl)
        {
            m_cURL = tcUrl;
        }
        public String getURL()
        {
            return m_cURL;
        }
    }
    public static void startWebBrowserOnApplicationStart(String tcURL)
    {
        URLCommandArgs loArgs = new URLCommandArgs();
        loArgs.setURL(tcURL);

        Goliath.Threading.Thread loThread = new Goliath.Threading.Thread(
                new Goliath.Threading.ThreadJob<URLCommandArgs>(loArgs)
        {

            @Override
            @SuppressWarnings("static-access")
            protected void onRun(URLCommandArgs toCommandArgs)
            {
                while(!Goliath.Applications.Application.getInstance().getState().equals(ApplicationState.RUNNING()))
                {
                    try
                    {
                        Thread.currentThread().sleep(100);
                    }
                    catch (Exception ex)
                    {
                        Application.getInstance().log(ex);
                    }
                }
                startWebBrowser(toCommandArgs.getURL());
            }
        });
        loThread.setName("StartWebBrowser_Thread");
        loThread.start();
    }


    public static String getURLContent(URL toURL)
            throws InvalidIOException
    {
        return getURLContent(toURL, 5000);
    }

    public static String getURLContent(URL toURL, int tnTimeout)
            throws InvalidIOException
    {
        StringBuilder loBuilder = new StringBuilder();

        try
        {
            URLConnection loConn = toURL.openConnection();
            loConn.setConnectTimeout(tnTimeout);

            BufferedReader loStream = null;
            try
            {
                loStream = new BufferedReader(new InputStreamReader(loConn.getInputStream()));
                String lcInput = null;
                while ((lcInput = loStream.readLine()) != null)
                {
                    loBuilder.append(lcInput);
                }
            }
            catch (Throwable ex)
            {
                Application.getInstance().log(ex);
            }
            finally
            {
                if (loStream != null)
                {
                    loStream.close();
                }
            }
        }
        catch (Throwable ex)
        {
            throw new InvalidIOException(ex);
        }
        return loBuilder.toString();
    }
    
    
    /**
     * Populates toParameters with the current values of the parameters listed in toParameters.
     * This will build up a secondary list from the Session and the HTTPRequest properties
     * @param toParameters the list of parameters to fill with values
     * @param toHTTPRequest the request object to use to help fill, if null, it will be ignored
     * @return toParameters is returned to allow chaining of this method
     */
    public static PropertySet populatePropertyHash(PropertySet toParameters, IHTTPRequest toHTTPRequest)
    {
        PropertySet loRequestProperties = new PropertySet();
        
        // Pull the properties needed from the session if they exist
        List<String> loProperties = toParameters.getPropertyKeys();
        ISession loSession = Session.getCurrentSession();
        
        for (String lcProperty : loProperties)
        {
            if (toParameters.getProperty(lcProperty) == null)
            {
                loRequestProperties.setProperty(lcProperty, loSession.getProperty(lcProperty));
            }
        }
        
        // If the HTTP Request has been passed, then look for values in that as well
        if (toHTTPRequest != null)
        {
            // Only get the properties that are needed
            for (String lcProperty : loProperties)
            {
                if (toParameters.getProperty(lcProperty) == null)
                {
                    Object loValue = toHTTPRequest.getParameter(lcProperty);
                    if (loValue == null)
                    {
                        loValue = Java.getPropertyValue(toHTTPRequest, lcProperty, false);
                    }
                    loRequestProperties.setProperty(lcProperty, loValue);
                }
            }
        }
        return Goliath.IO.Utilities.File.populatePropertyHash(toParameters, loSession, loRequestProperties);
    }

    


}
