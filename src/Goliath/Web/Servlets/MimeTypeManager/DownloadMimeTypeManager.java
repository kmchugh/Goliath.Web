/* ========================================================
 * DownloadMimeTypeManager.java
 *
 * Author:      admin
 * Created:     Oct 11, 2011, 6:19:23 PM
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

import Goliath.Collections.List;
import Goliath.Constants.MimeType;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import java.io.File;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Oct 11, 2011
 * @author      admin
 **/
public class DownloadMimeTypeManager extends MimeTypeManager
{

    /**
     * Creates a new instance of DownloadMimeTypeManager
     */
    public DownloadMimeTypeManager()
    {
    }

    @Override
    protected List<String> getSupportedMimeTypes()
    {
        return new List<String>(new String[]{
                MimeType.IMAGE_ICON().getValue(),
                MimeType.IMAGE_GIF().getValue(),
                MimeType.IMAGE_JPG().getValue(),
                MimeType.IMAGE_PNG().getValue(),
                MimeType.TEXT_HTML().getValue(),
                MimeType.APPLICATION_XML().getValue(),
                "application/xslt+xml",
                MimeType.TEXT_CSS().getValue(),
                MimeType.APPLICATION_X_SHOCKWAVE_FLASH().getValue(),
                MimeType.APPLICATION_JAR().getValue(),
                MimeType.APPLICATION_ZIP().getValue(),
                MimeType.TEXT_PLAIN().getValue(),
                MimeType.APPLICATION_PDF().getValue(),
                MimeType.APPLICATION_OCTET_STREAM().getValue(),
                MimeType.AUDIO_X_WAV().getValue(),
                MimeType.TEXT_CACHE_MANIFEST().getValue()
        });
    }

    @Override
    public void process(File toFile, IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        try
        {
            Goliath.IO.File loFile = new Goliath.IO.File(toFile);
            
            setDefaultHeaders(toResponse, loFile, getMimeType(loFile, toResponse));
            writeFileToResponse(loFile, toResponse);
        }
        catch (Throwable ex)
        {
            writeError(ex, toResponse);
        }
    }

    @Override
    public boolean supportsCompression()
    {
        return true;
    }
}
