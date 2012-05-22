/* ========================================================
 * ExecuteServletCommand.java
 *
 * Author:      kenmchugh
 * Created:     Mar 13, 2011, 12:58:26 AM
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

package Goliath.Commands;

import Goliath.Web.Servlets.ServletCommandArgs;
import Goliath.Applications.Application;
import Goliath.Delegate;
import Goliath.Event;
import Goliath.Exceptions.InvalidMethodException;
import Goliath.Exceptions.InvalidProtocolException;
import Goliath.Exceptions.ServletException;
import Goliath.Exceptions.UnavailableException;
import Goliath.Interfaces.Commands.ICommand;
import Goliath.Interfaces.ISession;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.Web.Constants.ResultCode;
import java.io.IOException;


        
/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Mar 13, 2011
 * @author      kenmchugh
**/
public class ExecuteServletCommand extends Command<ServletCommandArgs, Object>
{
    /**
     * Creates a new instance of ExecuteServletCommand
     */
    public ExecuteServletCommand(ServletCommandArgs toArgs, ISession toSession)
    {
        super(false);
        setSession(toSession);
        setArguments(toArgs);
        registerCommand();
    }

    /**
     * Helper function that changes the status code to the new code, only if the
     * current code is OK
     * @param toResponse the response
     * @param toNewCode the new code
     * @return true if the code was changed
     */
    protected final boolean changeIfOK(IHTTPResponse toResponse, ResultCode toNewCode)
    {
        if (toResponse.getResultCode() == ResultCode.OK())
        {
            toResponse.setResultCode(toNewCode);
            return true;
        }
        return false;
    }

    @Override
    public Object doExecute() throws Throwable
    {
        ServletCommandArgs loArgs = getArguments();
        IHTTPResponse loResponse = loArgs.getResponse();

        try
        {
            // By default the servlet will return HTML
            loArgs.getServlet().service(loArgs.getRequest(), loResponse);
        }
        catch (InvalidProtocolException ex)
        {
            loResponse.setResultCode(ResultCode.HTTP_VERSION_NOT_SUPPORTED());
        }
        catch (InvalidMethodException ex)
        {
            loResponse.setResultCode(ResultCode.METHOD_NOT_ALLOWED());
        }
        catch (UnavailableException ex)
        {
            /**
             * An UnavailableException signals that the servlet is unable to handle request either temporarily or permanently
             *
             * If permanaent, the servlet container must remove the servlet from service and call destory
             *
             * Any request refused by the container should return 404
             *
             * If temporary unavailability the container may stop requests to the servlet for the period of time
             * if that is the case then 503 should be returned along with a Retry-After header indicating unavailability
             */
            // TODO: Implement this
        }
        catch (ServletException ex)
        {
            changeIfOK(loResponse, ResultCode.INTERNAL_SERVER_ERROR());
            // TODO : unload servlet, return 404
            Application.getInstance().log(ex);

        }
        catch (IOException ex)
        {
            changeIfOK(loResponse, ResultCode.INTERNAL_SERVER_ERROR());
            Application.getInstance().log(ex);

        }
        catch (Throwable ex)
        {
            changeIfOK(loResponse, ResultCode.INTERNAL_SERVER_ERROR());
            Application.getInstance().log(ex);

        }
        finally
        {

            if (!loResponse.isResponseHeadersSent())
            {
                //loResponse.setResultLength(0);
                loResponse.sendResponseHeaders();
            }
            
            loResponse.close();
            this.addEventListener(CommandEventType.ONCOMPLETE(), Delegate.build(this, "onCommandComplete"));
        }

        return null;
    }
    
    private void onCommandComplete(Event<ICommand> toEvent)
    {
        this.getSession().getCommandResults().remove(toEvent.getTarget());
        this.clearEventListeners();
    }


}
