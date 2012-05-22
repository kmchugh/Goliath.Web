/* ========================================================
 * DefaultMimeTypeManager.java
 *
 * Author:      admin
 * Created:     Oct 11, 2011, 5:59:41 PM
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
package Goliath.Web.Servlets.MimeTypeManager;

import Goliath.Applications.Application;
import Goliath.Constants.LogType;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.Web.Constants.ResultCode;
import java.io.File;

/**
 * This mime type manager will result in an "Invalid Resource"
 * message and is the default resource manager.
 *
 * @see         Related Class
 * @version     1.0 Oct 11, 2011
 * @author      admin
 **/
public class DefaultMimeTypeManager extends MimeTypeManager
{

    /**
     * Creates a new instance of DefaultMimeTypeManager
     */
    public DefaultMimeTypeManager()
    {
    }

    @Override
    public void process(File toFile, IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        // We won't cache this result just in case the file is then put in place
        toResponse.setCached(false);
        
        // Process the invalid resource gsp
        Application.getInstance().log("The file " + toFile.getPath()  + " is not downloadable.", LogType.WARNING());
        File loFile = new File(Application.getInstance().getPropertyHandlerProperty("WebServer.ResourceNotFoundContext", "./htdocs/invalidresource.gsp"));
        
        // Force the result code to not found
        toResponse.setResultCode(ResultCode.NOT_FOUND());
        
        if (loFile.exists())
        {
            MimeTypeManager.getInstance().process(loFile, toRequest, toResponse);
        }
        else
        {
            // Just send the response headers
            toResponse.sendResponseHeaders();
        }
    }

    @Override
    public boolean supportsCompression()
    {
        return false;
    }
    
    
    
    
}
