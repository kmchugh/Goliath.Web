/* ========================================================
 * SetClientInformation.java
 *
 * Author:      kenmchugh
 * Created:     Mar 15, 2011, 12:03:46 PM
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

package Goliath.Web.Servlets;

import Goliath.Applications.ClientInformation;
import Goliath.ClientType;
import Goliath.Exceptions.ServletException;
import Goliath.Graphics.Dimension;
import Goliath.Interfaces.Servlets.IServletConfig;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.UI.UIClientType;
import Goliath.Web.Constants.RequestMethod;
import Goliath.Web.WebServices.WebServiceServlet;
import java.io.IOException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


        
/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Mar 15, 2011
 * @author      kenmchugh
**/
public class SetClientInformation extends WebServiceServlet
{

    // TODO: Change to ClientInformation servlet which supports get and post
    /**
     * Creates a new instance of SetClientInformation
     */
    public SetClientInformation()
    {
    }

    @Override
    public void onInit(IServletConfig toConfig) throws ServletException
    {
        clearSupportedMethods();
        addSupportedMethod(RequestMethod.POST());
    }



    @Override
    protected void doPost(IHTTPRequest toRequest, IHTTPResponse toResponse, Document toXML, StringBuilder toBuffer) throws ServletException, IOException
    {
        // Get the current client information
        // Check if the session knows the client information, if not, inform the session
        ClientInformation loClientInfo = toResponse.getSession().getClientInformation();
        if (loClientInfo != null)
        {
            // If there is client information, try to get the Client type
            ClientType loType = loClientInfo.getProperty("ClientType");

            // Check if this is a UIClientType
            if (Goliath.DynamicCode.Java.isEqualOrAssignable(UIClientType.class, loType.getClass()))
            {
                UIClientType loUIType = (UIClientType)loType;

                // Update the client sizes
                NodeList loNodes = toXML.getElementsByTagName("ClientHeight");
                NodeList loNodes1 = toXML.getElementsByTagName("ClientWidth");

                if (loNodes.getLength() == 1 && loNodes1.getLength() == 1)
                {
                    float lnValue = Float.parseFloat(loNodes.item(0).getTextContent());
                    float lnValue1 = Float.parseFloat(loNodes1.item(0).getTextContent());

                    Dimension loClientSize = new Dimension(lnValue1, lnValue);

                    loUIType.setClientSize(loClientSize);
                }

                // Update the application size
                loNodes = toXML.getElementsByTagName("Height");
                loNodes1 = toXML.getElementsByTagName("Width");

                if (loNodes.getLength() == 1 && loNodes1.getLength() == 1)
                {
                    float lnValue = Float.parseFloat(loNodes.item(0).getTextContent());
                    float lnValue1 = Float.parseFloat(loNodes1.item(0).getTextContent());

                    Dimension loSize = new Dimension(lnValue1, lnValue);

                    // TODO: Make this a screen object
                    loUIType.setSize(loSize);
                }

                // Update the screen size
                loNodes = toXML.getElementsByTagName("AvailableHeight");
                loNodes1 = toXML.getElementsByTagName("AvailableWidth");

                if (loNodes.getLength() == 1 && loNodes1.getLength() == 1)
                {
                    float lnValue = Float.parseFloat(loNodes.item(0).getTextContent());
                    float lnValue1 = Float.parseFloat(loNodes1.item(0).getTextContent());

                    Dimension loScreenSize = new Dimension(lnValue1, lnValue);

                    // TODO: Make this a screen object
                    loUIType.setScreenSize(loScreenSize);
                }

                // Update the pixel depth
                loNodes = toXML.getElementsByTagName("ColorDepth");
                if (loNodes.getLength() == 1)
                {
                    int lnValue = Integer.parseInt(loNodes.item(0).getTextContent());

                    loUIType.setColourDepth(lnValue);
                }
            }
        }
    }
}
