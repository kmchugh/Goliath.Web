/* ========================================================
 * ChangePasswordService.java
 *
 * Author:      manamimajumdar
 * Created:     Mar 23, 2011, 1:50:30 PM
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
package Goliath.Web.Security.WebServices;

import Goliath.Collections.List;
import Goliath.Collections.PropertySet;
import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.ISession;
import Goliath.Interfaces.Servlets.IServletConfig;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.JSON.JSON;
import Goliath.Web.Constants.RequestMethod;
import Goliath.Web.Constants.RequestProtocol;
import Goliath.Web.WebServices.WebServiceServlet;
import java.io.IOException;
import org.w3c.dom.Document;

/**
 * Description: the Change Password web service allows a user to change their
 * own password.
 *
 * To use this service, the user must be logged in to the current session.
 *
 * The post request data should look like one of the following:
 *
 * <ChangePassword encoded="true">
 *  <Data>TmFycmF0aXZlIENhcHR1cmUgUHJvamVjdHRlc3R9fXx7e3Rlc3Q=</Data>
 * </ChangePassword>
 *
 * The encoded data should be in the form:
 * {OldPassword:"myOldPassword", NewPassword:"myNewPassword"}
 *
 * <ChangePassword>
 *  <OldPassword>myoldpassword</OldPassword>
 *  <NewPassword>mynewpassword</NewPassword>
 * </ChangePassword>
 *
 * This service does not accept get requests, and only aceepts HTTPS
 *
 *
 * @see         Related Class
 * @version     1.0 Mar 23, 2011
 * @author      manamimajumdar
 **/
public class ChangePasswordService extends WebServiceServlet
{

    /**
     * Creates a new instance of the change password service
     */
    public ChangePasswordService()
    {
    }

    /**
     * This servlet requires authenticated sessions
     * @return
     */
    @Override
    protected final boolean requiresAuthentication(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return true;
    }

    @Override
    public final void onInit(IServletConfig toConfig) throws ServletException
    {
        clearSupportedMethods();
        addSupportedMethod(RequestMethod.POST());

        clearSupportedProtocols();
        addSupportedProtocol(RequestProtocol.HTTPS());
    }

    @Override
    protected final void doPost(IHTTPRequest toRequest, IHTTPResponse toResponse, Document toXML, StringBuilder toBuffer) throws ServletException, IOException
    {

        if (toXML != null)
        {
            String lcOldPassword;
            String lcNewPassword;

            boolean llEncoded = (toXML.getElementsByTagName("ChangePassword").item(0).getAttributes().getNamedItem("encoded") != null) ? toXML.getElementsByTagName("ChangePassword").item(0).getAttributes().getNamedItem("encoded").getNodeValue().toString().equals("true") : false;

            if (llEncoded)
            {
                PropertySet loProperties = Goliath.Web.Utilities.parseEncodedData((toXML.getElementsByTagName("Data").getLength() == 1) ? toXML.getElementsByTagName("Data").item(0).getTextContent() : "");
                lcOldPassword = loProperties.getProperty("OldPassword");
                lcNewPassword = loProperties.getProperty("NewPassword");
            }
            else
            {
                lcOldPassword = toXML.getElementsByTagName("OldPassword").item(0).getTextContent();
                lcNewPassword = toXML.getElementsByTagName("NewPassword").item(0).getTextContent();
            }
            ISession loSession = toRequest.getSession();

            changePassword(lcOldPassword, lcNewPassword, loSession, toRequest, toResponse, toBuffer);

        }
    }

    @Override
    protected final void doPost(IHTTPRequest toRequest, IHTTPResponse toResponse, JSON toJSON, StringBuilder toBuffer) throws ServletException, IOException
    {
        if (toJSON != null)
        {
            String lcOldPassword;
            String lcNewPassword;

            lcOldPassword = toJSON.get("OldPassword").getStringValue();
            lcNewPassword = toJSON.get("NewPassword").getStringValue();

            ISession loSession = toRequest.getSession();

            changePassword(lcOldPassword, lcNewPassword, loSession, toRequest, toResponse, toBuffer);
        }


    }

    private void changePassword(String tcOldPassword, String tcNewPassword, ISession toSession, IHTTPRequest toRequest, IHTTPResponse toResponse, StringBuilder toBuffer)
    {
        try
        {
            if (!Goliath.Security.Utilities.changeUserPassword(toSession, tcOldPassword, tcNewPassword, new List<Throwable>()))
            {
                addError(toResponse, new Goliath.Exceptions.Exception("Password change unsuccessful."));
            }
            else
            {
                Goliath.Utilities.appendToStringBuilder(toBuffer, "Password changed succesfully");
            }
        }
        catch (Throwable ex)
        {
            addError(toResponse, ex);
        }
    }
}
