/* ========================================================
 * GetApplicationIdentity.java
 *
 * Author:      kenmchugh
 * Created:     Mar 15, 2011, 12:22:15 PM
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

import Goliath.Applications.Application;
import Goliath.Collections.List;
import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.Applications.IApplication;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.LibraryVersion;
import Goliath.Web.WebServices.WebServiceServlet;
import java.io.IOException;


        
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
public class GetApplicationIdentity extends WebServiceServlet
{
    /**
     * Creates a new instance of GetApplicationIdentity
     */
    public GetApplicationIdentity()
    {
    }

    @Override
    protected void doGet(IHTTPRequest toRequest, IHTTPResponse toResponse, StringBuilder toBuffer) throws ServletException, IOException
    {
        IApplication loApplication = Application.getInstance();
        List<LibraryVersion> loVersions = loApplication.getObjectCache().getObjects(LibraryVersion.class, "getName");

        Goliath.Utilities.appendToStringBuilder(toBuffer,
                "<Application version=\"1.0\" name = \"",
                loApplication.getName(),
                "\" id=\"",
                loApplication.getGUID(),
                "\">",
                "<ModuleList count =\"",
                Integer.toString(loVersions.size()),
                "\">");

        for (LibraryVersion loVersion : loVersions)
        {
            Goliath.Utilities.appendToStringBuilder(toBuffer,
                "<Module name=\"",
                loVersion.getName(),
                "\">",
                "<Version>",
                Integer.toString(loVersion.getMajor()),
                ".",
                Integer.toString(loVersion.getMinor()),
                ".",
                Integer.toString(loVersion.getBuild()),
                ".",
                Integer.toString(loVersion.getRevision()),
                "  ",
                loVersion.getReleaseDate().toString(),
                "</Version>",
                "</Module>");
        }
        Goliath.Utilities.appendToStringBuilder(toBuffer,
                "</ModuleList>,"
                + "</Application>");
    }
}
