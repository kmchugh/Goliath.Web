/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Web.WebServices;

/**
 *
 * @author shakthikumarsubbaraj
 */

import Goliath.Applications.Application;
import Goliath.Constants.XMLFormatType;
import Goliath.LibraryVersion;
import Goliath.XML.XMLFormatter;
import java.util.List;

public class GetVersion extends Goliath.Web.WebServices.WebServiceCommand
{
    @Override
    public boolean onDoGetWebService(StringBuilder toBuilder)
    {
        List<LibraryVersion> loVersions = Application.getInstance().getObjectCache().getObjects(LibraryVersion.class, "getName");
        XMLFormatter.appendToXMLString(loVersions, XMLFormatType.DEFAULT(), toBuilder);
        return true;
    }

}
