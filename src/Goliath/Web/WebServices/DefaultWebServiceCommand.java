/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Web.WebServices;

import Goliath.Applications.Application;
import Goliath.Collections.List;
import Goliath.Exceptions.InvalidParameterException;
import Goliath.Interfaces.Collections.IList;
import Goliath.Interfaces.IXMLMapped;
import Goliath.Interfaces.Web.IWebServiceCommand;
import Goliath.Web.Constants.ResultCode;

/**
 *
 * @author kenmchugh
 */
public class DefaultWebServiceCommand extends WebServiceCommand
{

    @Override
    protected boolean onDoHeadWebService(StringBuilder toBuilder)
    {
        return onDoGetWebService(toBuilder);
    }



    @Override
    protected boolean onDoGetWebService(StringBuilder toBuilder)
    {
        // We need to decide what we are trying to do here.
        if (this.getContext().equals(this.getRequest().getPath()))
        {
            List<Class<IXMLMapped>> loXMLMapped = Application.getInstance().getObjectCache().getClasses(IXMLMapped.class);
            List<Class<IWebServiceCommand>> loWebServiceCommands = Application.getInstance().getObjectCache().getClasses(IWebServiceCommand.class);
            toBuilder.append("<List count=\"" + Integer.toString(loXMLMapped.size() + loWebServiceCommands.size()) + "\">"  + Goliath.Environment.NEWLINE());

            // We are actually looking for a list of all the objects that are supported by web services
            for (Class<IXMLMapped> loService : loXMLMapped)
            {
                toBuilder.append("<Service>");
                toBuilder.append("<Name>" + loService.getSimpleName() + "</Name>");
                toBuilder.append("<Description></Description>");
                toBuilder.append("<Context>" + this.getContext() + loService.getSimpleName() + "</Context>");
                toBuilder.append("</Service>" + Goliath.Environment.NEWLINE());
            }

            // Also add in any IWebServiceCommands
            for (Class<IWebServiceCommand> loService : loWebServiceCommands)
            {
                toBuilder.append("<Service>");
                toBuilder.append("<Name>" + loService.getSimpleName() + "</Name>");
                toBuilder.append("<Description></Description>");
                try
                {
                    IWebServiceCommand loCommand = loService.newInstance();
                    toBuilder.append("<Context>" + loCommand.getContext() + "</Context>");
                }
                catch (Throwable ex)
                {}


                toBuilder.append("</Service>" + Goliath.Environment.NEWLINE());
            }

            toBuilder.append("</List>" + Goliath.Environment.NEWLINE());

            Application.getInstance().getObjectCache().clearClass(IXMLMapped.class);
            Application.getInstance().getObjectCache().clearClass(IWebServiceCommand.class);

            return true;
        }
        else
        {
            // Check if there is a file url
            String lcURL = this.getRequest().getURI().toString();
            String lcFile = this.getRequest().getFile();

            // We are looking for a specific buisness object
            for (Class<IXMLMapped> loService : Application.getInstance().getObjectCache().getClasses(IXMLMapped.class))
            {
                if (lcURL.indexOf("/" + loService.getSimpleName() + "/") > 0 || loService.getSimpleName().equals(lcFile))
                {
                    // Found the type of object, just need to check if we are specifically requesting a guid or id

                    String lcID = null;
                    String lcGUID = null;

                    if (loService.getSimpleName().equals(lcFile))
                    {
                        // Passed as a parameter
                        lcID = getRequest().getParameter("id");
                        lcGUID = getRequest().getParameter("guid");
                    }
                    else
                    {
                        // URL
                        lcGUID = lcFile;
                        if (lcGUID.length() != 36)
                        {
                            lcID = lcFile;
                            lcGUID = null;
                        }
                    }

                    if (Goliath.Utilities.isNullOrEmpty(lcID) && Goliath.Utilities.isNullOrEmpty(lcGUID))
                    {
                        // Create a full list of objects
                        createXMLList(loService, toBuilder);
                        return true;
                    }
                    else
                    {
                        IList<IXMLMapped> loList = new List<IXMLMapped>();
                        try
                        {
                            if (Goliath.Utilities.isNullOrEmpty(lcID))
                            {
                                loList.add(loService.newInstance().getByGUID(lcGUID));
                            }
                            else
                            {
                                loList.add(loService.newInstance().getByID(Goliath.Utilities.parseLong(lcID)));
                            }
                            createXMLList(loList, toBuilder);
                            return true;
                        }
                        catch (Throwable ex)
                        {
                            addError(ex);
                            getResponse().setResultCode(ResultCode.NOT_FOUND());
                            return true;
                        }
                    }
                }
            }

            // Not quite sure what we are doing, so just throw an error
            addError(new InvalidParameterException("The GET method is not supported, or no object exists at " + this.getRequest().getPath(), "DefaultContext"));
            getResponse().setResultCode(ResultCode.BAD_REQUEST());
        }
        return true;
    }


    @Override
    protected String getWebServiceContext()
    {
        return "";
    }

}
