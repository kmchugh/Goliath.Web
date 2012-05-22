/* =========================================================
 * IWebContextCommand.java
 *
 * Author:      kmchugh
 * Created:     09-Apr-2008, 17:34:08
 * 
 * Description
 * --------------------------------------------------------
 * General Interface Description.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/

package Goliath.Interfaces.Commands;

import Goliath.Arguments.Arguments;
import Goliath.Interfaces.Web.IHTMLOutputStream;

/**
 * Interface Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @param <A> 
 * @param <T> 
 * @see         Related Class
 * @version     1.0 09-Apr-2008
 * @author      kmchugh
**/
public interface IWebContextCommand<A extends Arguments, T> extends IContextCommand<A, T>
{
    /**
     * Should be used to write the entire response to the client
     * @param tcResponse
     */
    void writeResponse(String tcResponse);

    /**
     * Gets the stream that should be written to in order to write to the client
     * @return
     */
    IHTMLOutputStream getStream();

    
    boolean allowSSL();
    boolean allowHTTP();
}
