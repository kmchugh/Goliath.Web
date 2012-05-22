/* ========================================================
 * UpdateSessionExpiryService.java
 *
 * Author:      christinedorothy
 * Created:     May 31, 2011, 9:34:07 AM
 *
 * Description
 * --------------------------------------------------------
 * Web service for updating the expiry length of server's session
 * that corresponds to a certain client application's user session.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * ===================================================== */

package Goliath.Web.WebServices;

import Goliath.Collections.PropertySet;
import Goliath.Exceptions.InvalidParameterException;
import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.ISession;
import Goliath.Interfaces.Servlets.IServletConfig;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import java.io.IOException;
import org.w3c.dom.Document;


        
/**
 * This class provides a web service to update the expiry length
 * of server's session that corresponds to a certain client application's
 * user session.
 *
 * @version     1.0 May 31, 2011
 * @author      christinedorothy
**/
public class UpdateSessionExpiryService extends WebServiceServlet
{
    /**
     * Creates a new instance of UpdateSessionExpiryService
     */
    public UpdateSessionExpiryService()
    {
    }

    @Override
    public void onInit(IServletConfig toConfig) throws ServletException
    {
        // TODO: Unable to get the return value when calling super and adds POST as supported method
    }

    @Override
    protected void doPost(IHTTPRequest toRequest, IHTTPResponse toResponse, Document toXML, StringBuilder toBuffer) throws ServletException, IOException
    {
        PropertySet loSessionDetails = toXML == null ? new PropertySet() : Goliath.Web.Utilities.getSessionDetails(toXML);

        if (loSessionDetails != null && !Goliath.Utilities.isNullOrEmpty(loSessionDetails.<String>getProperty("ExpiryLength")))
        {
            try
            {
                ISession loSession = toRequest.getSession();
                String lcExpiryParameter = loSessionDetails.getProperty("ExpiryLength");
                long lnExpiryLength = 0;
                try
                {
                    lnExpiryLength = Long.parseLong(lcExpiryParameter);
                    loSession.setExpiryLength(lnExpiryLength);

                    Goliath.Utilities.appendToStringBuilder(toBuffer, "<Data><ExpiryLength>", Long.toString(loSession.getExpiryLength()), "</ExpiryLength></Data>");
                }
                catch (NumberFormatException e)
                {
                    addError(toResponse, new InvalidParameterException("Invalid Expiry Length - [" + lcExpiryParameter + "]"));
                }
            }
            catch (Throwable ex)
            {
                addError(toResponse, ex);
            }
        }
        else
        {
            addError(toResponse, new InvalidParameterException("Required fields were not found in the post"));
        }
    }
}
