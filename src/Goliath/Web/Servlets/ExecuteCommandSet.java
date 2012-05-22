/* ========================================================
 * ExecuteCommandSet.java
 *
 * Author:      kenmchugh
 * Created:     Mar 15, 2011, 12:55:36 PM
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

import Goliath.Collections.List;
import Goliath.Commands.CommandHandler;
import Goliath.Commands.HandleRequestCommandArgs;
import Goliath.Exceptions.InvalidOperationException;
import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.Commands.ICommand;
import Goliath.Interfaces.Commands.ICommandHandler;
import Goliath.Interfaces.Commands.IUICommand;
import Goliath.Interfaces.Servlets.IServletConfig;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.Web.Constants.RequestMethod;
import Goliath.Web.WebServices.WebServiceServlet;
import java.io.IOException;
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
 * @version     1.0 Mar 15, 2011
 * @author      kenmchugh
**/
public class ExecuteCommandSet extends WebServiceServlet
{
    private static ICommandHandler g_oCommandHandler;

    private static ICommandHandler getCommandHandler()
    {
        if (g_oCommandHandler == null)
        {
            g_oCommandHandler = new CommandHandler(Goliath.Interfaces.Commands.ICommand.class);
        }
        return g_oCommandHandler;
    }

    
    /**
     * Creates a new instance of ExecuteCommandSet
     */
    public ExecuteCommandSet()
    {
    }

    @Override
    public void onInit(IServletConfig toConfig) throws ServletException
    {
        clearSupportedMethods();
        addSupportedMethod(RequestMethod.GET());
        addSupportedMethod(RequestMethod.POST());
    }

    @Override
    protected void doGet(IHTTPRequest toRequest, IHTTPResponse toResponse, StringBuilder toBuffer) throws ServletException, IOException
    {
        // Doing it this way stops the conncurrent update error
        Goliath.Collections.List<ICommand<?, ?>> loCommandList = new Goliath.Collections.List<ICommand<?, ?>>(toResponse.getSession().getCommandResults().keySet());

        Goliath.Utilities.appendToStringBuilder(toBuffer,
                "<CommandResults count='",
                Integer.toString(loCommandList.size()),
                "'>");

        int lnCount = 0;
        for (ICommand<?, ?> loCommand : loCommandList)
        {
            if (loCommand == this)
            {
                continue;
            }

            if (loCommand.isComplete())
            {
                lnCount++;
                Goliath.Utilities.appendToStringBuilder(toBuffer,
                    "<Command id='",
                    loCommand.getID(),
                    "' name='",
                    loCommand.getName(),
                    "' progress='",
                    Float.toString(loCommand.getProgress()),
                    "'>",

                    "<CommandResult type='",
                    ((IUICommand.class.isAssignableFrom(loCommand.getClass())) ? ((IUICommand)loCommand).getCommandType().getValue() : "DEFAULT"),
                    "'>");

                String lcCommandResult = Goliath.Utilities.isNull(toResponse.getSession().popCommandResult(loCommand), "").toString();
                // May need to strip off <?xml tag from results
                if (!Goliath.Utilities.isNullOrEmpty(lcCommandResult))
                {
                    lcCommandResult = Goliath.XML.Utilities.stripXMLHeader(lcCommandResult);
                }
                
                Goliath.Utilities.appendToStringBuilder(toBuffer,
                    lcCommandResult,
                    "</CommandResult>",
                    "</Command>");
            }
            else
            {
                // TODO: Allow progress messages to be passed back to client
                Goliath.Utilities.appendToStringBuilder(toBuffer,
                    "<Command id='",
                    loCommand.getID(),
                    "' name='",
                    loCommand.getName(),
                    "' progress='",
                    Float.toString(loCommand.getProgress()),
                    "'/>");
            }
        }
        toBuffer.append("</CommandResults>");
    }

    @Override
    protected void doPost(IHTTPRequest toRequest, IHTTPResponse toResponse, Document toXML, StringBuilder toBuffer) throws ServletException, IOException
    {
        // TODO: Look into problems with Immediate commands that are not synchronous that fail using webservices and ajax for communication from the js framework

        if (toXML != null)
        {

            // Make sure there is a commands node
            List<Node> loCommandList = Goliath.XML.Utilities.getElementsByTagName(toXML, "Commands");

            List<Node> loCommands = new List<Node>();
            int lnCommandCount = 0;
            String lcSessionID = null;

            // Combine all of the commands into a single list
            for (Node loNode : loCommandList)
            {
                lnCommandCount += Integer.parseInt(Goliath.Utilities.isNull(Goliath.XML.Utilities.getAttributeValue(loNode, "count"), "0"));
                loCommands.addAll(Goliath.XML.Utilities.getElementsByTagName(loNode, "Command"));
                if (lcSessionID == null)
                {
                    lcSessionID = Goliath.XML.Utilities.getAttributeValue(loNode, "sessionID");
                }
                else
                {
                    // If the session id is different from other command sets, throw an error
                    String lcSession = Goliath.XML.Utilities.getAttributeValue(loNode, "sessionID");
                    if (lcSession != null && !lcSession.equalsIgnoreCase(lcSessionID))
                    {
                        InvalidOperationException loEx = new InvalidOperationException("Multiple Session IDs given in ExecuteCommandSet body: \n" + Goliath.XML.Utilities.toString(toXML));
                        Goliath.Applications.Application.getInstance().log(loEx);
                        addError(toResponse, loEx);
                        return;
                    }
                }
            }

            // Ensure the session ID is correct
            if (!Goliath.Utilities.isNullOrEmpty(lcSessionID) && !lcSessionID.equals(toResponse.getSession().getSessionID()))
            {
                addError(toResponse, new Goliath.Exceptions.Exception("Invalid Session ID"));
                return;
            }

            // Make sure the number of commands matches what came in
            if (lnCommandCount != loCommands.size())
            {
                addError(toResponse, new Goliath.Exceptions.Exception("Command count does not match number of commands in list"));
                return;
            }

            // Get the command handler
            ICommandHandler loHandler = getCommandHandler();
            for (Node loNode : loCommands)
            {
                try
                {
                    boolean llSync = "true".equalsIgnoreCase(Goliath.XML.Utilities.getAttributeValue(loNode, "sync"));
                    String lcID = Goliath.XML.Utilities.getAttributeValue(loNode, "id");

                    // TODO: Pass an error back to the client if the command does not exist
                    // Process each command here
                    ICommand<?, ?> loConcreteCommand = loHandler.handleCommand(Goliath.XML.Utilities.getAttributeValue(loNode, "name"), new HandleRequestCommandArgs(toRequest, toResponse), toRequest.getSession());
                    loConcreteCommand.setID(lcID);

                    // If this is a synchronous command, the client is actually waiting for a response
                    if (llSync)
                    {
                        loConcreteCommand.waitToComplete();
                    }
                }
                catch (Throwable ex)
                {
                    Goliath.Applications.Application.getInstance().log(ex);
                    addError(toResponse, ex);
                    return;
                }
            }
        }

        // Once all the commands are handled, create the response
        doGet(toRequest, toResponse, toBuffer);
    }



    
}
