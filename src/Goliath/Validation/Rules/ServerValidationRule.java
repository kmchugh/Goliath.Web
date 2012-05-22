/* ========================================================
 * ServerValidationRule.java
 *
 * Author:      home_stanbridge
 * Created:     Jun 8, 2011, 1:52:56 PM
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

package Goliath.Validation.Rules;

import Goliath.Arguments.Arguments;
import Goliath.Validation.RuleHandler;
import Goliath.Applications.Application;
import Goliath.Web.ServerConnection;
import Goliath.Collections.List;
import Goliath.Environment;
import Goliath.Exceptions.UncheckedException;
import Goliath.Session;
import org.w3c.dom.Node;
import Goliath.Web.WebServices.WebServiceRequest;
        
/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Jun 8, 2011
 * @author      home_stanbridge
**/
public class ServerValidationRule extends  RuleHandler<ServerConnection, String, Arguments>
{
    private WebServiceRequest m_oLastErrorRequest;

    /**
     * Called when the rule needs to be executed
     * @param toTarget the target object of the rule, this is the object being validated
     * @param tcPropertyValue the value of the property being assessed for validity
     * @param toArgs The arguments paassed to the Rule
     * @return true if this property passes the validation rule, otherwise false
     */
    @Override
    protected boolean onExecuteRule(ServerConnection toTarget, String tcPropertyValue, Arguments toArgs)
    {
        if (toTarget.isDeleted())
        {
            return true;
        }

        // first, validate server unsecure
        boolean llReturn = toTarget.isURLValidated();

        if (!llReturn)
        {
            llReturn = validateServer(toTarget, false);
            toTarget.setURLValidated(llReturn);
        }

        // Only validate secure if the non-secure passes
        if (llReturn && toTarget.getServerSecureURL() != null && !toTarget.isSecureURLValidated())
        {
            llReturn = validateServer(toTarget, true);
            toTarget.setSecureURLValidated(llReturn);
        }
        return llReturn;
    }


    /**
     * Validates the specified server connection making sure that it is able to connection to a
     * server that can respond to a GetApplicationIdentity web service request.  This also confirms
     * that the server being connected to has not changed it's application guid
     * @param toConnection the connection to check
     * @param tlSecure true to check the secure connection, false to check the non secured connection
     * @return true if the server is valid and responding
     */
    private boolean validateServer(ServerConnection toConnection, boolean tlSecure)
    {
        // TODO: Also implement private and public key checks on the server
        try
        {
            WebServiceRequest loRequest = toConnection.makeWebServiceRequest("GetApplicationIdentity", tlSecure, Session.getCurrentSession().getSessionID());

            if (loRequest.hasErrors())
            {
                // Store the request with errors so we can pick up the errors in the message if required
                // It is expected that the getfailedmessage is called right away as thie error request will be overwritten by the next failed connection
                m_oLastErrorRequest = loRequest;
                String lcErrors = getRequestErrors(loRequest);
                throw new UncheckedException(lcErrors);
            }
            
            List<Node> loNodes = Goliath.XML.Utilities.getElementsByTagName(loRequest.getResult(), "Application", 1);
            if (loNodes.size() == 1)
            {
                // TODO: We can also extract all of the version information of the server here as well.
                String lcKey = loNodes.get(0).getAttributes().getNamedItem("id").getNodeValue();
                if (Goliath.Utilities.isNullOrEmpty(toConnection.getServerID()))
                {
                    toConnection.setServerID(lcKey);
                }

                if (!toConnection.getServerID().equalsIgnoreCase(lcKey))
                {
                    throw new UncheckedException("Error with server key", false);
                }
            }
            else
            {
                throw new UncheckedException("Error with server key", false);
            }
            return true;
        }
        catch (Throwable ex)
        {
            Application.getInstance().log(ex);
            return false;
        }
    }

    /**
     * Gets the list of failed messages from the
     * @param toObject the server connection that could not be connected to
     * @param tcPropertyName the property that we were validating with this rule
     * @param tcServerURL the value of the property we were validating
     * @param toArgs the arguments provided
     * @return the error string
     */
    @Override
    protected String onGetFailedMessage(ServerConnection toObject, String tcPropertyName, String tcServerURL, Arguments toArgs)
    {
        // If we can build up the actual errors from the server
        return (m_oLastErrorRequest != null) ? getRequestErrors(m_oLastErrorRequest) : "Undetermined Error connecting to the server";
    }

    /**
     * Determines if arguments are required for this rule
     * @return true if arguments are required, false if they are not
     */
    @Override
    protected boolean requiresArgs()
    {
        return false;
    }

    /**
     * Helper function to get the errors from the web service request so that we can report them accurately to the client
     * @param toRequest the request object to get the errors from
     * @return the string version of the error list
     */
    private String getRequestErrors(WebServiceRequest toRequest)
    {
        StringBuilder loBuilder = new StringBuilder();
        for (String lcError : toRequest.getErrors())
        {
            Goliath.Utilities.appendToStringBuilder(loBuilder,
                lcError,
                Environment.NEWLINE());
        }

        return loBuilder.toString();
    }

  
  
    

}
