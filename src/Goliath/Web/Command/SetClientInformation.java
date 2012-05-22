/* =========================================================
 * SetClientInformation.java
 *
 * Author:      kenmchugh
 * Created:     Aug 26, 2010, 2:40:18 PM
 *
 * Description
 * --------------------------------------------------------
 * Sets the information of the client so that it can be
 * picked up by the session if needed
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Web.Command;

import Goliath.Applications.ClientInformation;
import Goliath.ClientType;
import Goliath.Graphics.Dimension;
import Goliath.UI.UIClientType;
import Goliath.Web.WebServices.WebServiceCommand;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author kenmchugh
 */
public class SetClientInformation extends WebServiceCommand
{

    @Override
    protected boolean onDoPostWebService(StringBuilder toBuilder, Document toXML)
    {
        // Get the current client information
        // Check if the session knows the client information, if not, inform the session
        ClientInformation loClientInfo = getSession().getClientInformation();
        if (loClientInfo != null)
        {
            try
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
            catch(Throwable ex)
            {
            }
        }
        return true;
    }
}
