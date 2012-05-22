/* =========================================================
 * WebSession.java
 *
 * Author:      kmchugh
 * Created:     26 November 2007, 15:33
 *
 * Description
 * --------------------------------------------------------
 * A web based session
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Applications;

/**
 * A web based session class
 *
 * @see         Goliath.Applications.Session
 * @version     1.0 26 November 2007
 * @author      kmchugh
 **/
public class WebSession extends Goliath.Session
{

    // TODO: Find if this is needed anymore
    
    /** Creates a new instance of WebSession */
    public WebSession(String tcSessionID)
    {
        super(tcSessionID);
    }
    
}
