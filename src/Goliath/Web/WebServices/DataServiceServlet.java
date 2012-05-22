/* ========================================================
 * DataServiceServlet.java
 *
 * Author:      manamimajumdar
 * Created:     Aug 13, 2011, 12:03:38 PM
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
package Goliath.Web.WebServices;

import Goliath.Applications.Application;
import Goliath.Collections.List;
import Goliath.Collections.SimpleDataObjectCollection;
import Goliath.Data.DataObjects.UndoableDataObject;
import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.IXMLFormatter;
import Goliath.Interfaces.Servlets.IServletConfig;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.JSON.JSON;
import Goliath.Web.Constants.RequestMethod;
import Goliath.XML.XMLFormatter;
import java.io.IOException;
import org.w3c.dom.Document;
import Goliath.Constants.MimeType;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Aug 13, 2011
 * @author      manamimajumdar
 **/
public class DataServiceServlet extends WebServiceServlet
{

    private static List<Class<UndoableDataObject>> g_oDOList;

    @Override
    public void onInit(IServletConfig toConfig) throws ServletException
    {
        clearSupportedMethods();
        addSupportedMethod(RequestMethod.GET());
        addSupportedMethod(RequestMethod.POST());
        addSupportedMethod(RequestMethod.DELETE());
        addSupportedMethod(RequestMethod.PUT());
    }

    /**
     * Get the DataObject class requested through the URL.
     * @param taPath Array of Strings which holds the Path after /DO/
     * @return the DataObject class.
     */
    private Class<UndoableDataObject> getDataObjectType(String[] taPath)
    {
        Class<UndoableDataObject> loReturn = null;
        List<Class<UndoableDataObject>> loResultList = new List<Class<UndoableDataObject>>(1);
        for (int i = 0; i < taPath.length; i++)
        {
            String lcName = taPath[i];
            List<Class<UndoableDataObject>> loClassList = getClassList();
            for (Class<UndoableDataObject> loClass : loClassList)
            {
                if (loClass.getName().equalsIgnoreCase(lcName) || loClass.getSimpleName().equalsIgnoreCase(lcName))
                {
                    loResultList.add(loClass);
                }
            }

            // If there is only one matched class, then use it
            if (loResultList.size() == 1 || (loResultList.size() > 1 && lcName.indexOf(".") < 0))
            {
                loReturn = loResultList.get(0);
            }
            else
            {
                for (Class<UndoableDataObject> loClass : loResultList)
                {
                    if (loClass.getSimpleName().equalsIgnoreCase(lcName))
                    {
                        loReturn = loClass;
                        break;
                    }
                }
            }
        }
        return loReturn;
    }

    /**
     * Get all the DataObject classes.
     * @return List of all UndoableDataObject classes.
     */
    private synchronized List<Class<UndoableDataObject>> getClassList()
    {
        if (g_oDOList == null)
        {
            g_oDOList = Application.getInstance().getObjectCache().getClasses(UndoableDataObject.class);
        }
        return new List<Class<UndoableDataObject>>(g_oDOList);
    }

    /**
     * Get the Path as an array of Strings.
     * @param toRequest
     * @return Array of Strings which holds the path separated by "/"
     */
    private String[] getPath(IHTTPRequest toRequest)
    {
        return toRequest.getPath().replaceFirst(getDefaultContext(), "").split("/");
    }

    /**
     * Get GUID or ID
     * @param taPath Array of Strings which holds the Path after /DO/
     * @return GUID or ID if exists.
     */
    private String getIDField(String[] taPath)
    {
        return (taPath.length >= 2) ? taPath[1] : null;
    }

    /**
     * Save the data object for Put and Post request.
     * @param toDO
     * @param toObject
     * @param toClass
     * @param toRequest
     */
    private void saveDataObject(UndoableDataObject toDO, Object toObject, Class<UndoableDataObject> toClass, IHTTPRequest toRequest)
    {
        if (toRequest.getContentType() == MimeType.APPLICATION_JSON())
        {

            JSON loJSON = (JSON) toObject;
            List<String> loList = Goliath.DynamicCode.Java.getPropertyMethods(toClass);
            for (int i = 0; i < loJSON.getProperties().size(); i++)
            {
                for (String loProperty : loList)
                {
                    if (loJSON.getProperties().get(i).equalsIgnoreCase(loProperty))
                    {
                        Goliath.DynamicCode.Java.setPropertyValue(toDO, loProperty, loJSON.get(loJSON.getProperties().get(i)).getValue());
                    }
                }
            }

        }

        if (toRequest.getContentType() == MimeType.APPLICATION_XML())
        {
            Document loXml = (Document) toObject;
            List<String> loList = Goliath.DynamicCode.Java.getPropertyMethods(toClass);
            for (int i = 0; i < loXml.getFirstChild().getChildNodes().getLength(); i++)
            {
                for (String loProperty : loList)
                {
                    if (loXml.getFirstChild().getChildNodes().item(i).getNodeName().equalsIgnoreCase(loProperty))
                    {
                        Goliath.DynamicCode.Java.setPropertyValue(toDO, loProperty, loXml.getFirstChild().getChildNodes().item(i).getTextContent());
                    }
                }
            }
        }

        if (!toDO.save()) {
            // TODO: The exception was logged, but is lost now, so we cannot give details.
            throw new RuntimeException("Could not save data object.");
        }

    }

    /**
     * Get the list of all records of a particular DataObject. User Authentication required.
     * if GUID or ID is passed through the URL , it would return only the object having this GUID/ID.
     * @param toRequest
     * @param toResponse
     * @param toBuffer
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(IHTTPRequest toRequest, IHTTPResponse toResponse, StringBuilder toBuffer) throws ServletException, IOException
    {

        Class<UndoableDataObject> loClass = getDataObjectClass(toRequest);

        UndoableDataObject loDO = null;
        SimpleDataObjectCollection loDataList = null;

        if (loClass != null)
        {
            String lcIDorGUID = getIDField(getPath(toRequest));
            if (Goliath.Utilities.isNullOrEmpty(lcIDorGUID))
            {
                if (toResponse.getSession().isAuthenticated())
                {
                    // There was no ID which means we should return a list
                    loDataList = new SimpleDataObjectCollection(loClass);
                    loDataList.loadList();

                    IXMLFormatter loFormatter = XMLFormatter.getXMLFormatter(loDataList.getClass());
                    appendObjectToResponse(toResponse, toBuffer, loDataList);
                }
                else
                {
                    addError(toResponse, new Goliath.Exceptions.Exception("session not authenticated"));
                }
            }
            else
            {

                // Get the object, if lcIDorGUID is all numeric, then we assume it is an ID, otherwise we assume GUID
                try
                {
                    loDO = (lcIDorGUID.matches("-?\\d+(.\\d+)?"))
                           ? UndoableDataObject.getObjectByID(loClass, Long.parseLong(lcIDorGUID)) : // lcIDorGUID was numeric
                            UndoableDataObject.getObjectByGUID(loClass, lcIDorGUID);
                }
                catch (NumberFormatException ex)
                {
                    // Okay, lcIDorGUID was numeric, but not a Long value;
                    loDO = UndoableDataObject.getObjectByGUID(loClass, lcIDorGUID);
                }

                if (loDO != null)
                {
                    appendObjectToResponse(toResponse, toBuffer, loDO);
                }


            }
        }
        else
        {
            appendObjectToResponse(toResponse, toBuffer, getClassList());
        }



    }

    /**
     * Put new records for the DataObject. 
     *
     * Example of xml to be passed for User Object(For User DataObject, user and password must be provided):
     *
     * <User>
     * <Name>UserName</Name>
     * <Password>UserPassword</Password>
     * <DisplayName>UserDisplay</DisplayName>
     * <Email>email@email.com</Email>
     * </User>
     * @param toRequest
     * @param toResponse
     * @param toXML
     * @param toBuffer
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(IHTTPRequest toRequest, IHTTPResponse toResponse, Document toXML, StringBuilder toBuffer)
            throws ServletException, IOException
    {
        if (toXML != null)
        {
            processPut(toRequest, toResponse, toXML, toBuffer);

        }
    }

    /**
     * Put new records for the DataObject.
     *
     * Example of JSON to be passed for User Object(For User DataObject, user and password must be provided):
     *
     * {
     * Name : UserName,
     * Password : UserPassword,
     * DisplayName : UserDisplay,
     * Email : email.email.com
     * }
     *
     * @param toRequest
     * @param toResponse
     * @param toJSON
     * @param toBuffer
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPut(IHTTPRequest toRequest, IHTTPResponse toResponse, JSON toJSON, StringBuilder toBuffer) throws ServletException, IOException
    {
        if (toJSON != null)
        {
            processPut(toRequest, toResponse, toJSON, toBuffer);
        }
    }

    /**
     * Update existing record for DataObject.
     * GUID should be passed for the object to be updated.
     * User Authentication required.
     *
     * Example of xml to be passed for User Object:
     *
     * <User>
     * <Name>UserName</Name>
     * <Password>UserPassword</Password>
     * <DisplayName>UserDisplay</DisplayName>
     * <Email>email.email.com</Email>
     * </User>
     *
     * @param toRequest
     * @param toResponse
     * @param toXML
     * @param toBuffer
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(IHTTPRequest toRequest, IHTTPResponse toResponse, Document toXML, StringBuilder toBuffer)
            throws ServletException, IOException
    {
        if (toXML != null)
        {
            processPost(toRequest, toResponse, toXML, toBuffer);

        }
    }

    /**
     * Update existing record for DataObject. 
     * GUID should be passed for the object to be updated.
     * Authentication required.
     *
     * Example of JSON to be passed for User Object:
     *
     * {
     * Name : UserName,
     * Password : UserPassword,
     * DisplayName : UserDisplay,
     * Email : email.email.com
     * }
     *
     * @param toRequest
     * @param toResponse
     * @param toJSON
     * @param toBuffer
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(IHTTPRequest toRequest, IHTTPResponse toResponse, JSON toJSON, StringBuilder toBuffer) throws ServletException, IOException
    {
        if (toJSON != null)
        {
            processPost(toRequest, toResponse, toJSON, toBuffer);
        }
    }

    /**
     * Delete existing record.
     * GUID should be passed through URL for the object to be deleted.
     * Session should be authenticated.
     * @param toRequest
     * @param toResponse
     * @param toBuffer
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(IHTTPRequest toRequest, IHTTPResponse toResponse, StringBuilder toBuffer) throws ServletException, IOException
    {
        Class<UndoableDataObject> loClass = getDataObjectClass(toRequest);

        UndoableDataObject loDO = null;

        if (loClass != null)
        {
            String lcIDorGUID = getIDField(getPath(toRequest));
            if (!Goliath.Utilities.isNullOrEmpty(lcIDorGUID))
            {
                if (toResponse.getSession().isAuthenticated())
                {
                    loDO = UndoableDataObject.getObjectByGUID(loClass, lcIDorGUID);

                    if (loDO != null)
                    {
                        try
                        {
                            loDO.delete();
                            loDO.save();
                        }
                        catch (Throwable ex)
                        {
                            addError(toResponse, new Goliath.Exceptions.Exception(ex));
                        }
                    }
                }
                else
                {
                    addError(toResponse, new Goliath.Exceptions.Exception("Session not authenticated."));
                }

            }

        }
    }

    @Override
    protected String onGetDefaultContext()
    {
        return "/DO/";
    }

    @Override
    protected String onGetContext()
    {
        return "";
    }

    /**
     * Creates a new instance of DataServiceServlet
     */
    public DataServiceServlet()
    {
    }

    /**
     * Get the DataObjectClass
     * @param toRequest
     * @return DataObjectClass
     */
    private Class<UndoableDataObject> getDataObjectClass(IHTTPRequest toRequest)
    {
        String[] laPath = getPath(toRequest);
        Class<UndoableDataObject> loClass = getDataObjectType(laPath);
        return loClass;

    }

    //Logic for Put method is here.
    private void processPut(IHTTPRequest toRequest, IHTTPResponse toResponse, Object toObject, StringBuilder toBuffer)
    {
        Class<UndoableDataObject> loClass = getDataObjectClass(toRequest);

        UndoableDataObject loDO = null;

        if (loClass != null)
        {
            try
            {
                loDO = loClass.newInstance();
                saveDataObject(loDO, toObject, loClass, toRequest);
                appendObjectToResponse(toResponse, toBuffer, loDO);
            }
            catch (Throwable ex)
            {
                addError(toResponse, new Goliath.Exceptions.Exception(ex));
            }
        }
    }

    //Logic for Post method is here.
    private void processPost(IHTTPRequest toRequest, IHTTPResponse toResponse, Object toObject, StringBuilder toBuffer)
    {
        Class<UndoableDataObject> loClass = getDataObjectClass(toRequest);

        UndoableDataObject loDO = null;
        List<String> loList;

        if (loClass != null)
        {
            String lcIDorGUID = getIDField(getPath(toRequest));
            if (!Goliath.Utilities.isNullOrEmpty(lcIDorGUID))
            {
                if (toResponse.getSession().isAuthenticated())
                {
                    loDO = UndoableDataObject.getObjectByGUID(loClass, lcIDorGUID);

                    if (loDO != null)
                    {
                        try
                        {
                            saveDataObject(loDO, toObject, loClass, toRequest);
                            appendObjectToResponse(toResponse, toBuffer, loDO);
                        }
                        catch (Throwable ex)
                        {
                            addError(toResponse, new Goliath.Exceptions.Exception(ex));
                        }
                    }
                }

            }

        }

    }
}
