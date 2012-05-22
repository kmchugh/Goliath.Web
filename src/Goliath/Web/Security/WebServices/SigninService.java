/* ========================================================
 * LoginService.java
 *
 * Author:      manamimajumdar
 * Created:     Mar 26, 2011, 3:21:04 PM
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
import Goliath.Constants.XMLFormatType;
import Goliath.Exceptions.InvalidParameterException;
import Goliath.Web.WebServices.WebServiceServlet;
import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.ISession;
import Goliath.Interfaces.Servlets.IServletConfig;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import java.io.IOException;
import org.w3c.dom.Document;
import Goliath.JSON.JSON;

/**
 * Description: the Sign in web service logs a user in, if the user is already authenticated, then
 * this service will simply refresh their session.  If the user name is different from that of the 
 * session already logged in, then this will log out the current user
 *
 * The post request data should look like one of the following:
 *
 * * EITHER *
 *  <Data>TmFycmF0aXZlIENhcHR1cmUgUHJvamVjdHRlc3R9fXx7e3Rlc3Q=</Data>
 *  The encoded data should be in the form:
 *  {
 *      UserName: "myUserName",         <!-- If not provided, email address will be used-->
 *      Password: "myPassword"          <!-- Required-->
 *  }
 *
 *  * WHERE THE ENCODED DATA FITS THE FORM SPECIFIED, OR *
 *
 *  <UserName>myUserName</UserName>
 *  <Password>myPassword</Password>
 *
 * This service does not accept get requests, and only aceepts HTTPS
 *
 *
 * @see         Related Class
 * @version     1.0 Mar 26, 2011
 * @author      manamimajumdar
 **/
public class SigninService extends WebServiceServlet
{

    public SigninService()
    {
    }

    @Override
    public final void onInit(IServletConfig toConfig) throws ServletException
    {
        // TODO: For now, allow non HTTPS for this servlet.  Need to fix the javascript collector login before this can be reinstated
        /*
        clearSupportedMethods();
        addSupportedMethod(RequestMethod.POST());

        clearSupportedProtocols();
        addSupportedProtocol(RequestProtocol.HTTPS());
         *
         */
    }

    @Override
    protected boolean allowAuthenticated(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        return false;
    }

    @Override
    protected void doPost(IHTTPRequest toRequest, IHTTPResponse toResponse, Document toXML, StringBuilder toBuffer) throws ServletException, IOException
    {

        if (toXML != null)
        {
            String lcUser;
            String lcPassword;

            PropertySet loLoginDetails = toXML == null ? new PropertySet() : Goliath.Web.Security.Utilities.getLoginDetails(toXML);
            lcUser = loLoginDetails.getProperty("UserName");
            lcPassword = loLoginDetails.getProperty("Password");
            ISession loSession = toRequest.getSession();

            signinService(lcUser,lcPassword,loSession,toBuffer,toRequest,toResponse);
        }


    }

    @Override
    protected void doPost(IHTTPRequest toRequest, IHTTPResponse toResponse, JSON toJSON, StringBuilder toBuffer) throws ServletException, IOException
    {

        if (toJSON != null)
        {
            String lcUser;
            String lcPassword;

            lcUser = toJSON.get("UserName").getStringValue();
            lcPassword = toJSON.get("Password").getStringValue();

            ISession loSession = toRequest.getSession();

            signinService(lcUser,lcPassword,loSession,toBuffer,toRequest,toResponse);

            
        }


    }

    private void signinService(String tcUser,String tcPassword,ISession toSession,StringBuilder toBuffer,IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        if (!Goliath.Utilities.isNullOrEmpty(tcUser) && !Goliath.Utilities.isNullOrEmpty(tcPassword))
            {
                if (Goliath.Security.Utilities.signinUser(toRequest.getSession(), tcUser, tcPassword, new List<Throwable>()))
                {
                    Goliath.Utilities.appendToStringBuilder(toBuffer, Goliath.XML.Utilities.toXMLString(tcUser, XMLFormatType.TYPED()));
                    toSession.renew();

                }
            }
            else
            {
                addError(toResponse, new InvalidParameterException("Required fields were not found in the post"));
            }
    }
}
