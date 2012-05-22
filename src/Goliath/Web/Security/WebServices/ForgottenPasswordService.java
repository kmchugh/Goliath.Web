/* ========================================================
 * ForgottenPasswordService.java
 *
 * Author:      manamimajumdar
 * Created:     Sep 15, 2011, 4:51:59 PM
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
import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.Servlets.IServletConfig;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.JSON.JSON;
import Goliath.Web.WebServices.WebServiceServlet;
import java.io.IOException;
import org.w3c.dom.Document;

/**
 * * Description: The forgotten password web service reset the password for the
 * users who have forgotten their password.
 *
 * To use this service, the session must not be authenticated
 *
 * The post request data should look like one of the following:
 *
 *  <ForgottenPassword>
 *  <Email>email@email.com</Email>
 *  </ForgottenPassword>
 *
 * The encoded data should be in the form:
 * {Email:"email@email.com"}
 *
 * This service does not accept get requests, and only aceepts HTTPS
 *
 *
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Sep 15, 2011
 * @author      manamimajumdar
 **/
public class ForgottenPasswordService extends WebServiceServlet
{

    /**
     * Creates a new instance of ForgottenPasswordService
     */
    public ForgottenPasswordService()
    {
    }

    @Override
    public final void onInit(IServletConfig toConfig) throws ServletException
    {
    }

    /**
     * Do not allow authenticated sessions here
     * @return false
     */
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
            String tcEmail;
            tcEmail = toXML.getElementsByTagName("Email").item(0).getTextContent();
            resetPassword(tcEmail, toResponse, toBuffer);
        }
    }

    @Override
    protected void doPost(IHTTPRequest toRequest, IHTTPResponse toResponse, JSON toJSON, StringBuilder toBuffer) throws ServletException, IOException
    {

        if (toJSON != null)
        {
            String tcEmail;
            tcEmail = toJSON.get("Email").getStringValue();
            resetPassword(tcEmail, toResponse, toBuffer);
        }
    }

    private void resetPassword(String tcEmail, IHTTPResponse toResponse, StringBuilder toBuffer)
    {
        try
        {
            if (!Goliath.Security.Utilities.resetUserPassword(toResponse.getSession(), tcEmail, new StringBuilder(), new List<Throwable>()))
            {
                addError(toResponse, new Goliath.Exceptions.Exception("Password reset unsuccessful."));
            }
        }
        catch (Throwable ex)
        {
            addError(toResponse, ex);
        }
    }
}
