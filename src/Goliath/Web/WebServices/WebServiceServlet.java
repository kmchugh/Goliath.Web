/* ========================================================
 * WebServiceServlet.java
 *
 * Author:      kenmchugh
 * Created:     Mar 15, 2011, 10:43:42 AM
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
import Goliath.Constants.MimeType;
import Goliath.Constants.XMLFormatType;
import Goliath.Exceptions.InvalidMethodException;
import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.Collections.IList;
import Goliath.Interfaces.IXMLFormatter;
import Goliath.Interfaces.Servlets.IServletConfig;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.JSON.JSON;
import Goliath.Web.Constants.RequestMethod;
import Goliath.Web.Constants.ResultCode;
import Goliath.Web.Servlets.Servlet;
import Goliath.XML.XMLFormatter;
import java.io.IOException;
import org.w3c.dom.Document;

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
public abstract class WebServiceServlet extends Servlet
{

    private static String g_cContext;
    private static String g_cRoot;
    private static int g_nMaxResults;

    /**
     * Creates a new instance of WebServiceServlet
     */
    public WebServiceServlet()
    {
    }

    @Override
    public void onInit(IServletConfig toConfig) throws ServletException
    {
        // Be default only the GET method is supported
        clearSupportedMethods();
        addSupportedMethod(RequestMethod.GET());
    }

    protected void appendObjectToResponse(IHTTPResponse toResponse, StringBuilder toBuilder, Object toObject)
    {
        if (toResponse.getContentType() == MimeType.APPLICATION_JSON())
        {
            toBuilder.append("\"" + toObject.getClass().getSimpleName() + "\":");
            toBuilder.append(Goliath.JSON.Utilities.toJSON(toObject));
        }
        else
        {
            toResponse.setContentType(MimeType.APPLICATION_XML());
            toBuilder.append(Goliath.XML.Utilities.toXMLString(toObject, XMLFormatType.TYPED()));
        }
        /*
        toBuilder.append(toResponse.getContentType() == MimeType.APPLICATION_JSON() ?
        Goliath.JSON.Utilities.toJSON(toObject) :
        // TODO: Implement the xml formatter as a StringFormatter
        //Goliath.XML.Utilities.toXMLString(toObject));
         * 
         */
    }

    /**
     * Gets the Context string, uses the application properties so this can be changed in setup
     * @return the String that represents the root of the web services
     */
    public static String getContextString()
    {
        if (g_cContext == null)
        {
            g_cContext = Application.getInstance().getPropertyHandlerProperty("WebServer.WebServices.DefaultURI", "/WS/");
        }
        return g_cContext;
    }

    /**
     * Gets the string that represents the root to all the web service commands
     * @return the string that represents the root
     */
    private static String getRootString()
    {
        if (g_cRoot == null)
        {
            g_cRoot = Application.getInstance().getPropertyHandlerProperty("WebServer.WebServices.DefaultRoot", "WSResult");
        }
        return g_cRoot;
    }

    /**
     * Helper function to get the maximum number of results shown by this request
     * @param toRequest the request
     * @return the max number of results
     */
    private int getMaxResults(IHTTPRequest toRequest)
    {
        if (g_nMaxResults == 0)
        {
            g_nMaxResults = Application.getInstance().getPropertyHandlerProperty("WebServer.WebServices.DefaultMaxResults", 50);
        }

        if (toRequest.getParameter("maxResults") == null)
        {
            return g_nMaxResults;
        }
        else
        {
            int lnCount = Integer.parseInt(toRequest.getParameter("maxResults"));
            if (lnCount <= 0)
            {
                return g_nMaxResults;
            }
            return lnCount;
        }
    }

    /**
     * Executes the correct function based on the method
     * @param toMethod this request is being executed for
     * @param toRequest the request to process
     * @param toResponse the response
     * @throws ServletException if there is an error
     * @throws IOException if there is a read/write error
     */
    @Override
    protected final void doService(RequestMethod toMethod, IHTTPRequest toRequest, IHTTPResponse toResponse) throws ServletException, IOException
    {
        // Find the list of the mime types that this service will accept, and set the content type to the preferred type
        List<MimeType> loPreferredTypes = toResponse.getAccepts();

        // If there is no preferred type, then the default will be xml
        MimeType loPreferredType = MimeType.APPLICATION_XML();
        if (loPreferredTypes != null)
        {
            for (MimeType loType : loPreferredTypes)
            {
                if (loType == MimeType.APPLICATION_JSON())
                {
                    loPreferredType = loType;
                    break;
                }
            }
        }
        toResponse.setContentType(loPreferredType);


        StringBuilder loBuffer = new StringBuilder();

        try
        {
            if (toMethod == RequestMethod.GET())
            {
                doGet(toRequest, toResponse, loBuffer);
            }
            else if (toMethod == RequestMethod.POST())
            {
                try
                {
                    Object loRequestBody = null;
                    try
                    {
                        loRequestBody = (toRequest.getContentType() == MimeType.APPLICATION_XML())
                                        ? Goliath.XML.Utilities.toXML(toRequest.getBody())
                                        : new JSON(toRequest.getBody());
                    }
                    catch (Throwable ex)
                    {
                        addError(toResponse, ex);
                        Application.getInstance().log(ex);
                    }
                    if (toRequest.getContentType() == MimeType.APPLICATION_XML())
                    {
                        doPost(toRequest, toResponse, (Document) loRequestBody, loBuffer);
                    }
                    else if (toRequest.getContentType() == MimeType.APPLICATION_JSON())
                    {
                        doPost(toRequest, toResponse, (JSON) loRequestBody, loBuffer);
                    }
                    else
                    {
                        Application.getInstance().log("Invalid request type for WebServiceServlet " + getClass().getName());
                        doPost(toRequest, toResponse, (Document) null, loBuffer);
                    }
                }
                catch (Throwable ex)
                {
                    Application.getInstance().log(ex);
                    Application.getInstance().log(toRequest.getBody());
                    addError(toResponse, ex);
                }
            }
            else if (toMethod == RequestMethod.PUT())
            {
                try
                {
                    Object loRequestBody = null;
                    try
                    {
                        loRequestBody = (toRequest.getContentType() == MimeType.APPLICATION_XML())
                                        ? Goliath.XML.Utilities.toXML(toRequest.getBody())
                                        : new JSON(toRequest.getBody());
                    }
                    catch (Throwable ex)
                    {
                        addError(toResponse, ex);
                        Application.getInstance().log(ex);
                    }
                    if (toRequest.getContentType() == MimeType.APPLICATION_XML())
                    {
                        doPut(toRequest, toResponse, (Document) loRequestBody, loBuffer);
                    }
                    else if (toRequest.getContentType() == MimeType.APPLICATION_JSON())
                    {
                        doPut(toRequest, toResponse, (JSON) loRequestBody, loBuffer);
                    }
                    else
                    {
                        Application.getInstance().log("Invalid request type for WebServiceServlet " + getClass().getName());
                        doPut(toRequest, toResponse, (Document) null, loBuffer);
                    }
                }
                catch (Throwable ex)
                {
                    Application.getInstance().log(ex);
                    Application.getInstance().log(toRequest.getBody());
                    addError(toResponse, ex);
                }
            }
            else if (toMethod == RequestMethod.DELETE())
            {
                doDelete(toRequest, toResponse, loBuffer);
            }
            else if (toMethod == RequestMethod.OPTIONS())
            {
                doOptions(toRequest, toResponse, loBuffer);
            }
            else if (toMethod == RequestMethod.HEAD())
            {
                doHead(toRequest, toResponse, loBuffer);
            }
            else if (toMethod == RequestMethod.TRACE())
            {
                doTrace(toRequest, toResponse, loBuffer);
            }
            else if (toMethod == RequestMethod.CONNECT())
            {
                doConnect(toRequest, toResponse, loBuffer);
            }
            else
            {
                addAllowHeaders(toResponse);
                throw new InvalidMethodException(toRequest);
            }
        }
        catch (Throwable ex)
        {
            addError(toResponse, ex);
        }

        // If we have been writing to the buffer, we need to send it to the response
        if ((loBuffer.length() > 0 || hasErrors(toResponse)) && !toResponse.isResponseHeadersSent())
        {
            Application.getInstance().log("Writing buffer for WebService " + getClass().getName());
            writeResult(toResponse, loBuffer);
        }
    }

    @Override
    protected void completeServletWithDispatcher(IHTTPResponse toResponse, IHTTPRequest toRequest, String tcMessage) throws ServletException, IOException
    {
        writeResult(toResponse, null);
    }

    private String getResultFooter(IHTTPResponse toResponse)
    {
        StringBuilder loBuilder = new StringBuilder();
        if (toResponse.getContentType() == MimeType.APPLICATION_JSON())
        {
            loBuilder.append("}");
        }
        else
        {
            Goliath.Utilities.appendToStringBuilder(loBuilder,
                                                    "</",
                                                    getRootString(),
                                                    ">");
        }
        return loBuilder.toString();
    }

    private String getResultHeader(IHTTPResponse toResponse)
    {
        StringBuilder loBuilder = new StringBuilder();
        if (toResponse.getContentType() == MimeType.APPLICATION_JSON())
        {
            Goliath.Utilities.appendToStringBuilder(loBuilder,
                                                    "{",
                                                    "\"result\" : {",
                                                    "\"sessionID\" : \"", toResponse.getSession().getSessionID(), "\",",
                                                    "\"code \": ", Integer.toString(toResponse.getResultCode().getCode()), ",",
                                                    "\"description \": \"", toResponse.getResultCode().getValue(), "\",",
                                                    "\"response object\":");

            if (hasErrors(toResponse))
            {
                List<Throwable> loErrors = getErrors(toResponse);
                if (loErrors.size() > 0)
                {
                    loBuilder.append(",\"errorList\":[");
                    int lnCount = 1;
                    for (Throwable loError : loErrors)
                    {

                        Goliath.Utilities.appendToStringBuilder(loBuilder,
                                                                "\"" + loError.toString() + "\"",
                                                                lnCount < loErrors.size() ? "," : "");
                        lnCount++;
                    }
                    loBuilder.append("]");
                }
            }
            loBuilder.append("}");
        }
        else
        {
            loBuilder.append(getXMLHeader());
            Goliath.Utilities.appendToStringBuilder(loBuilder,
                                                    "<",
                                                    getRootString(),
                                                    ">",
                                                    "<Result>",
                                                    "<SessionID>",
                                                    toResponse.getSession().getSessionID(),
                                                    "</SessionID>",
                                                    "<Code>",
                                                    Integer.toString(toResponse.getResultCode().getCode()),
                                                    "</Code>",
                                                    "<Description>",
                                                    toResponse.getResultCode().getValue(),
                                                    "</Description>");

            // Write the errors if needed
            if (hasErrors(toResponse))
            {
                List<Throwable> loErrors = getErrors(toResponse);
                if (loErrors.size() > 0)
                {
                    Goliath.Utilities.appendToStringBuilder(loBuilder,
                                                            "<ErrorList count=\"",
                                                            Integer.toString(loErrors.size()),
                                                            "\">");

                    for (Throwable loError : loErrors)
                    {
                        Goliath.Utilities.appendToStringBuilder(loBuilder,
                                                                "<Error>",
                                                                "<![CDATA[",
                                                                loError.toString(),
                                                                "]]>",
                                                                "</Error>");
                    }
                    loBuilder.append("</ErrorList>");
                }
            }
            loBuilder.append("</Result>");
        }

        return loBuilder.toString();
    }

    protected void writeList(IHTTPResponse toResponse, IList toList)
    {
        // TODO: This should stream directly to the respose rather than writing out like this

        boolean llHasErrors = hasErrors(toResponse);
        // If there are any errors, and the result type is okay, then set it to server error
        if (llHasErrors && toResponse.getResultCode() == ResultCode.OK())
        {
            toResponse.setResultCode(ResultCode.INTERNAL_SERVER_ERROR());
        }

        try
        {

            toResponse.getStream().write(getResultHeader(toResponse));

            toResponse.getStream().write("<List size=\"" + toList.size() + "\" type=\"" + toList.getClass().getName() + "\">");

            IXMLFormatter loFormatter = null;
            for (java.lang.Object loItem : toList)
            {
                if (loFormatter == null)
                {
                    loFormatter = XMLFormatter.getXMLFormatter(loItem.getClass());
                }
                toResponse.getStream().write("<Item>");
                toResponse.getStream().write(loFormatter.toXMLString(loItem, XMLFormatType.TYPED()));
                toResponse.getStream().write("</Item>");
            }
            toResponse.getStream().write("</List>");

            toResponse.getStream().write(getResultFooter(toResponse));
        }
        catch (Throwable ex)
        {
            Application.getInstance().log(ex);
        }
    }

    /**
     * Helper function to write a standardised XML result
     * @param toResponse the response to write to
     * @param toBuilder the content to write
     */
    private void writeResult(IHTTPResponse toResponse, StringBuilder toBuilder)
    {
        boolean llHasErrors = hasErrors(toResponse);
        // If there are any errors, and the result type is okay, then set it to server error
        if (llHasErrors && toResponse.getResultCode() == ResultCode.OK())
        {
            toResponse.setResultCode(ResultCode.INTERNAL_SERVER_ERROR());
        }

        StringBuilder loBuilder = new StringBuilder(getResultHeader(toResponse));

        if (toBuilder != null && (toBuilder.toString().contains("{") || toBuilder.toString().contains("<")))
        {
            if (toResponse.getContentType() == MimeType.APPLICATION_JSON() && toBuilder.toString().contains("\""))
            {
                loBuilder.append(" ,");
                String loResponseObject = toBuilder.toString().split("\"")[1];
                loBuilder = new StringBuilder(loBuilder.toString().replaceFirst("\"response object\":", "\"response object\":\"" + loResponseObject+"\""));
            }
            loBuilder.append(toBuilder.toString());
        }
        else
        {
            if (toResponse.getContentType() == MimeType.APPLICATION_JSON())
            {
                loBuilder = new StringBuilder(loBuilder.toString().replaceFirst("\"response object\":", "\"response object\":\"response\""));
                loBuilder.append(",\"response\" : {}");
            }
        }

        loBuilder.append(getResultFooter(toResponse));

        // Write the return
        toResponse.write(loBuilder.toString());
    }

    protected String getXMLHeader()
    {
        return "<?xml version=\"1.0\" encoding=\"" + getEncodingString().toUpperCase() + "\"?>" + Goliath.Environment.NEWLINE();
    }

    protected String getEncodingString()
    {
        return "utf-8";
    }

    /**
     * Executes the OPTIONS method of the web server
     * @param toRequest the request
     * @param toResponse the response
     * @throws ServletException if there is an error
     * @throws IOException if there is a read/write error
     */
    protected void doOptions(IHTTPRequest toRequest, IHTTPResponse toResponse, StringBuilder toBuffer)
            throws ServletException, IOException
    {
        addAllowHeaders(toResponse);
        toResponse.setResultCode(ResultCode.OK());
    }

    /**
     * The GET request processing, at a minimum all sub classes should override this method
     * @param toRequest the request
     * @param toResponse the response
     * @throws ServletException if there is an error
     * @throws IOException if there is a read/write error
     */
    protected void doGet(IHTTPRequest toRequest, IHTTPResponse toResponse, StringBuilder toBuffer)
            throws ServletException, IOException
    {
    }

    protected void doPost(IHTTPRequest toRequest, IHTTPResponse toResponse, Document toXML, StringBuilder toBuffer)
            throws ServletException, IOException
    {
    }

    protected void doPost(IHTTPRequest toRequest, IHTTPResponse toResponse, JSON toJSON, StringBuilder toBuffer)
            throws ServletException, IOException
    {
    }

    protected void doPut(IHTTPRequest toRequest, IHTTPResponse toResponse, Document toXML, StringBuilder toBuffer)
            throws ServletException, IOException
    {
    }

    protected void doPut(IHTTPRequest toRequest, IHTTPResponse toResponse, JSON toJSON, StringBuilder toBuffer)
            throws ServletException, IOException
    {
    }

    protected void doDelete(IHTTPRequest toRequest, IHTTPResponse toResponse, StringBuilder toBuffer)
            throws ServletException, IOException
    {
    }

    protected void doHead(IHTTPRequest toRequest, IHTTPResponse toResponse, StringBuilder toBuffer)
            throws ServletException, IOException
    {
    }

    protected void doTrace(IHTTPRequest toRequest, IHTTPResponse toResponse, StringBuilder toBuffer)
            throws ServletException, IOException
    {
    }

    protected void doConnect(IHTTPRequest toRequest, IHTTPResponse toResponse, StringBuilder toBuffer)
            throws ServletException, IOException
    {
    }

    /**
     * Gets the default context for this
     * @return
     */
    @Override
    protected final String getDefaultContext()
    {
        String lcContext = onGetDefaultContext();
        if (lcContext.startsWith("/"))
        {
            lcContext = lcContext.substring(1);
        }
        if (!lcContext.endsWith("/") && lcContext.length() > 0)
        {
            lcContext += "/";
        }
        return onGetContext() + onGetDefaultContext();
    }

    protected String onGetDefaultContext()
    {
        return getClass().getSimpleName() + "/";
    }

    protected String onGetContext()
    {
        return getContextString();
    }
}
