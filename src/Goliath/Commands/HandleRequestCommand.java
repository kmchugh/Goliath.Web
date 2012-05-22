/* =========================================================
 * handleRequestCommand.java
 *
 * Author:      kmchugh
 * Created:     09-Apr-2008, 15:04:44
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
package Goliath.Commands;

import Goliath.Applications.Application;
import Goliath.Applications.ClientInformation;
import Goliath.ClientType;
import Goliath.Collections.HashTable;
import Goliath.Collections.List;
import Goliath.Constants.LogType;
import Goliath.Interfaces.Commands.ICommand;
import Goliath.Interfaces.Servlets.IServlet;
import Goliath.Interfaces.Commands.IWebContextCommand;
import Goliath.Interfaces.ISession;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.Web.Constants.ResultCode;

/**
 * This command handles all the requests from the lighthttp client.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 09-Apr-2008
 * @author      kmchugh
 **/
public class HandleRequestCommand extends Goliath.Commands.Command<HandleRequestCommandArgs, String>
{
    private static HashTable<String, IServlet> g_oServlets;






    private static HashTable<String, Class<IWebContextCommand>> g_oContextHandlers = null;

    /** Creates a new instance of handleRequestCommand
     * 
     * @param toCommandArgs the command arguments for the request
     */
    public HandleRequestCommand(HandleRequestCommandArgs toCommandArgs)
    {
        super(false, toCommandArgs);
    }

    protected IHTTPResponse getResponse()
    {
        if (getArguments() != null)
        {
            return getArguments().getResponse();
        } else
        {
            throw new UnsupportedOperationException("Can not get response until execute is called and the arguments have been set");
        }
    }

    protected IHTTPRequest getRequest()
    {
        if (getArguments() != null)
        {
            return getArguments().getRequest();
        } else
        {
            throw new UnsupportedOperationException("Can not get request until execute is called and the arguments have been set");
        }
    }

    @Override
    public final String doExecute() throws Throwable
    {
        try
        {
            Application.getInstance().log("Requesting " + getRequest().getPath(), LogType.TRACE());

            return onDoExecute();
        } catch (Throwable ex)
        {
            try
            {
                Goliath.Exceptions.Exception loEx = new Goliath.Exceptions.Exception(ex);
                String lcString = loEx.getLocalizedMessage();
                /*
                IRenderFactory loRenderer = (IRenderFactory) Goliath.Applications.Application.getInstance().getRenderFactory();
                String lcString = loRenderer.render(new Goliath.UI.Errors.Error(loEx), new StringBuilder()).toString();
                 *
                 */
                byte[] laBytes = lcString.getBytes();
                
                getResponse().setResponseHeaders("Content-Type", "text/html; charset=utf-8");
                getResponse().setResultCode(ResultCode.INTERNAL_SERVER_ERROR());
                getResponse().write(laBytes);
            } catch (Throwable ex1)
            {
                Application.getInstance().log(ex);
            }
        }
        return null;
    }

    protected String onDoExecute() throws Throwable
    {
        IHTTPRequest loRequest = getRequest();
        IHTTPResponse loResponse = getResponse();


        // The request may be asking for a file, if so and if the file has an acceptable extension.
        // Download the file instead of handling the response.
        if (!Goliath.Utilities.isNullOrEmpty(loRequest.getFile()) && !isExactContext(loRequest))
        {
//            FileRequestCommand loCommand = new FileRequestCommand(this.getArguments());
//            loCommand.setSession(this.getSession());
//            loCommand.execute();
//            while (!loCommand.isComplete())
            {
                Thread.sleep(100);
            }
            if (loResponse.isResponseHeadersSent())
            {
                // The command was handled
                return null;
            }
            loResponse.setResultCode(ResultCode.OK());
        }

        // Just a normal query
        handle(loRequest, loResponse);
        
        return null;
    }

    public final void handle(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        try
        {
            // Get the query path
            String lcPath = toRequest.getPath();
            // Create the context handler for the path, and handle the context if one was created
            ICommand<?, ?> loCommand = getContextHandler(lcPath, toRequest, toResponse);
            ISession loSession = this.getSession();

            if (loCommand != null)
            {
                // This command was a command passed by a user, so we want to renew the session as it means
                // there is an actual client at the other end of the session
                loSession.renew();

                // Check if the session knows the client information, if not, inform the session
                ClientInformation loClientInfo = loSession.getClientInformation();
                if (loClientInfo == null)
                {
                    // Create a new client information object and set the sessions info
                    loSession.updateClientInformation(createClientInfoFromRequest(toRequest));
                }
            }
            else
            {
                //loCommand = new FileRequestCommand(this.getArguments());
            }

            // We have a context handler so use it, notice we are not 
            // putting this command on the session command queue because it is 
            // happening on the same thread, and we would end up in an endless wait if
            // we used waitToComplete() to wait for the command
            loCommand.setSession(loSession);
            loCommand.execute();
        }
        catch (Exception ex)
        {
            throw new Goliath.Exceptions.InvalidOperationException("Could not handle response", ex);
        }
    }

    private ClientInformation createClientInfoFromRequest(IHTTPRequest toRequest)
    {
        ClientInformation loReturn = new ClientInformation();

        ClientType loClientType = ClientType.createFromString(toRequest.getUserAgent());

        loClientType.populateFromObject(toRequest);

        loReturn.setProperty("ClientType", loClientType);

        return loReturn;
    }
    
    private HashTable<String, Class<IWebContextCommand>> getContextHandlers()
    {
        // Make sure all the context handlers are loaded
        if (g_oContextHandlers == null)
        {
            g_oContextHandlers = new HashTable<String, Class<IWebContextCommand>>();
            List<Class<IWebContextCommand>> loContexts = Application.getInstance().getObjectCache().getClasses(IWebContextCommand.class);
            for (Class<IWebContextCommand> loCommandClass : loContexts)
            {
                try
                {
                    IWebContextCommand loCommand = loCommandClass.newInstance();
                    for (Object lcContext : loCommand.getContexts())
                    {
                        if (lcContext != null)
                        {
                            g_oContextHandlers.put(lcContext.toString().toLowerCase(), loCommandClass);
                        }
                    }
                } catch (Throwable ignore)
                {
                    Application.getInstance().log(ignore);
                }
            }
        }
        return g_oContextHandlers;
    }

    private boolean isExactContext(IHTTPRequest toRequest)
    {
        HashTable<String, Class<IWebContextCommand>> loHandlers = getContextHandlers();
        return loHandlers.containsKey(toRequest.getPath().toLowerCase());
    }

    private ICommand<?, ?> getContextHandler(String tcContext, IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        HashTable<String, Class<IWebContextCommand>> loHandlers = getContextHandlers();
        try
        {
            // Try to find the context handler for this.
            String lcContext = tcContext.toLowerCase();
            while (lcContext != null)
            {
                if (loHandlers.containsKey(lcContext))
                {
                    IWebContextCommand loCommand = loHandlers.get(lcContext).newInstance();
                    loCommand.setArguments(new HandleRequestCommandArgs(toRequest, toResponse));
                    return loCommand;
                }
                lcContext = getHandler(lcContext);
            }

            // Still no context found so try to use a file context
            // First handle the default contexts
            if (Goliath.Utilities.isNullOrEmpty(toRequest.getFile()))
            {
                //FileRequestCommand loCommand = new FileRequestCommand(this.getArguments());
                //loCommand.setSession(this.getSession());
                //return loCommand;
            }
            return null;
        }
        catch (Throwable ex)
        {
            Application.getInstance().log(ex);
            return null;
        }
    }

    private String getHandler(String tcHandler)
    {
        // If the last character is /, then just strip it off
        if (tcHandler.endsWith("/"))
        {
            return tcHandler.substring(0, tcHandler.length() - 1);
        }

        StringBuilder lcReturn = new StringBuilder("/");
        String[] laParts = tcHandler.split("/");
        if (laParts.length == 0)
        {
            return null;
        }

        for (int i = 0; i < laParts.length - 1; i++)
        {
            if (!laParts[i].isEmpty())
            {
                lcReturn.append(laParts[i]);
                lcReturn.append("/");
            }

        }


        if (lcReturn.length() != 1)
        {
            return lcReturn.toString();
        }
        return null;
    }
}
