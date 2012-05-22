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
package Goliath.Web.Security;

import Goliath.Collections.PropertySet;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import Goliath.Security.User;
import Goliath.Applications.Application;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.Web.Servlets.Servlet;

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
     * Parses login details from the xml document provided.
     * Login details consist of the following elements
     *
     * * EITHER *
     *  <Data>TmFycmF0aXZlIENhcHR1cmUgUHJvamVjdHRlc3R9fXx7e3Rlc3Q=</Data>
     *  The encoded data should be in the form:
     *  {
     *      DisplayName: "Test",            <!-- If not provided, email address will be used-->
     *      Email: "email@email.com",       <!-- Required-->
     *      UserName: "myUserName",         <!-- If not provided, email address will be used-->
     *      Password: "myPassword"          <!-- Required-->
     *      VerifyEmail: "email@email.com"  <!-- optional-->
     *      VerifyPassword: "myPassword"    <!-- optional-->
     *  }
     *
     *  * WHERE THE ENCODED DATA FITS THE FORM SPECIFIED, OR *
     *
     *  <DisplayName>test</DisplayName>
     *  <Email>email@email.com</Email>
     *  <UserName>myUserName</UserName>
     *  <Password>myPassword</Password>
     *  <VerifyEmail>email@email.com</VerifyEmail>
     *  <VerifyPassword>myPassword</VerifyPassword>
     *
     * @return a property set containing the login details or null if the details couldn't be parsed
     */
    public static PropertySet getLoginDetails(Document toXML)
    {
        if (toXML == null)
        {
            return null;
        }
        if (toXML.getElementsByTagName("Data").getLength() > 0)
        {
            Node loNode = toXML.getElementsByTagName("Data").item(0);

            PropertySet loProperties = Goliath.Web.Utilities.parseEncodedData(loNode.getTextContent());

            // Check if we have at least an email and password
            if ((loProperties.hasProperty("Email") || loProperties.hasProperty("UserName")) && loProperties.hasProperty("Password"))
            {
                // We have the required properties, so fill in the others.
                if (!loProperties.hasProperty("DisplayName"))
                {
                    loProperties.setProperty("DisplayName", loProperties.getProperty("Email"));
                }
                if (!loProperties.hasProperty("Email"))
                {
                    loProperties.setProperty("Email", loProperties.getProperty("UserName"));
                }
                if (!loProperties.hasProperty("UserName"))
                {
                    loProperties.setProperty("UserName", loProperties.getProperty("Email"));
                }
                if (!loProperties.hasProperty("VerifyPassword"))
                {
                    loProperties.setProperty("VerifyPassword", "");
                }
                if (!loProperties.hasProperty("VerifyEmail"))
                {
                    loProperties.setProperty("VerifyEmail", "");
                }

                return loProperties;
            }
        }

        // If we got this far, then either there was no data element, or the encoded data did not have the required fields
        if ((toXML.getElementsByTagName("Email").getLength() > 0 || toXML.getElementsByTagName("UserName").getLength() > 0) && toXML.getElementsByTagName("Password").getLength() > 0)
        {
            // We have the require fields as a minimum
            PropertySet loReturn = new PropertySet();

            loReturn.setProperty("UserName", toXML.getElementsByTagName("UserName").getLength() > 0 ? toXML.getElementsByTagName("UserName").item(0).getTextContent() : loReturn.getProperty("Email"));
            loReturn.setProperty("Password", toXML.getElementsByTagName("Password").item(0).getTextContent());
            loReturn.setProperty("DisplayName", toXML.getElementsByTagName("DisplayName").getLength() > 0 ? toXML.getElementsByTagName("DisplayName").item(0).getTextContent() : loReturn.getProperty("UserName"));
            loReturn.setProperty("Email", toXML.getElementsByTagName("Email").getLength() > 0 ? toXML.getElementsByTagName("Email").item(0).getTextContent() : "");
            loReturn.setProperty("VerifyEmail", toXML.getElementsByTagName("VerifyEmail").getLength() > 0 ? toXML.getElementsByTagName("VerifyEmail").item(0).getTextContent() : "");
            loReturn.setProperty("VerifyPassword", toXML.getElementsByTagName("VerifyPassword").getLength() > 0 ? toXML.getElementsByTagName("VerifyPassword").item(0).getTextContent() : "");

            return loReturn;
        }

        // We couldn't parse anything so return null
        return null;
    }

    

    

    

    

}
