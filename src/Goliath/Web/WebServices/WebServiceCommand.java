/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Web.WebServices;

import Goliath.Applications.Application;
import Goliath.Collections.List;
import Goliath.Constants.XMLFormatType;
import Goliath.Exceptions.InvalidParameterException;
import Goliath.Interfaces.Collections.IList;
import Goliath.Interfaces.IXMLMapped;
import Goliath.Interfaces.Web.IWebServiceCommand;
import Goliath.Web.Constants.RequestMethod;
import Goliath.Web.Constants.ResultCode;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;

/**
 *
 * @author kenmchugh
 */
public abstract class WebServiceCommand extends Goliath.Web.Contexts.WebContextCommand<java.lang.Object>
        implements IWebServiceCommand
{
    private static int g_nMaxResults = 0;
    private static String g_cContext;
    private static String g_cRoot;

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
     * Web service should return xml by default
     */
    @Override
    protected void onSetResponseHeaders()
    {
        // TODO: Check which browsers support application/xml
        getResponse().setResponseHeaders("Content-Type", "text/xml; charset=" + getEncodingString());
    }

    private int getMaxResults()
    {
        if (g_nMaxResults == 0)
        {
            g_nMaxResults = Application.getInstance().getPropertyHandlerProperty("WebServer.WebServices.DefaultMaxResults", 50);
        }

        if (getRequest().getParameter("maxResults") == null)
        {
            return g_nMaxResults;
        }
        else
        {
            int lnCount = Integer.parseInt(getRequest().getParameter("maxResults"));
            if (lnCount <= 0)
            {
                return g_nMaxResults;
            }
            return lnCount;
        }
    }

    // TODO: Implement Trace and Connect headers


    @Override
    protected final Object onDoDefaultContext()
    {
        StringBuilder loBuilder = new StringBuilder();
        if (onDoDefaultWebService(loBuilder))
        {
            writeResult(loBuilder);
        }
        return loBuilder.toString();
    }

    @Override
    protected final Object onDoGetContext()
    {
        StringBuilder loBuilder = new StringBuilder();
        if (onDoGetWebService(loBuilder))
        {
            writeResult(loBuilder);
        }
        return loBuilder.toString();
    }

    @Override
    protected Object onDoPutContext()
    {
        StringBuilder loBuilder = new StringBuilder();

        try
        {
            org.w3c.dom.Document loXML = null;

            // TODO: Caching all of the DocumentBuilderFactory.newInstance and newDocumentBuilder will probably improve performance up a bit
            // TODO: Continued from above TODO, Getting a DocumentBuilder or a newDocumentBuilder, or a new Document should be part of goliath.utilities
            if (!Goliath.Utilities.isNullOrEmpty(getRequest().getBody()))
            {
                // First make sure the body is valid XML
                loXML = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new java.io.StringReader(getRequest().getBody())));
            }
            else
            {
                loXML = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            }
            
            if (onDoPutWebService(loBuilder, loXML))
            {
                writeResult(loBuilder);
            }
        }
        catch(Throwable ex)
        {
            Application.getInstance().log(ex);
            Application.getInstance().log(getRequest().getBody());
            addError(ex);
            writeResult(loBuilder);
        }
        return loBuilder.toString();
    }





    @Override
    protected final Object onDoPostContext()
    {
        StringBuilder loBuilder = new StringBuilder();

        try
        {
            org.w3c.dom.Document loXML = null;

            // TODO: Caching all of the DocumentBuilderFactory.newInstance and newDocumentBuilder will probably improve performance up a bit
            // TODO: Continued from above TODO, Getting a DocumentBuilder or a newDocumentBuilder, or a new Document should be part of goliath.utilities
            if (!Goliath.Utilities.isNullOrEmpty(getRequest().getBody()))
            {
                // First make sure the body is valid XML
                loXML = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new java.io.StringReader(getRequest().getBody())));
            }
            else
            {
                loXML = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            }
            
            if (onDoPostWebService(loBuilder, loXML))
            {
                writeResult(loBuilder);
            }
        }
        catch(Throwable ex)
        {
            Application.getInstance().log(ex);
            Application.getInstance().log(getRequest().getBody());
            addError(ex);
            writeResult(loBuilder);
        }

        return loBuilder.toString();
    }

    @Override
    protected final Object onDoDeleteContext()
    {
        StringBuilder loBuilder = new StringBuilder();
        if (onDoDeleteWebService(loBuilder))
        {
            writeResult(loBuilder);
        }
        return loBuilder.toString();
    }

    @Override
    protected final Object onDoOptionsContext()
    {
        StringBuilder loBuilder = new StringBuilder();
        if (onDoOptionsWebService(loBuilder))
        {
            writeResult(loBuilder);
        }
        return loBuilder.toString();
    }

    protected boolean onDoDefaultWebService(StringBuilder toBuilder)
    {
        addError(new InvalidParameterException("This method is not supported, or no object exists", "DefaultContext"));
        getResponse().setResultCode(ResultCode.METHOD_NOT_ALLOWED());
        return true;
    }

    protected boolean onDoGetWebService(StringBuilder toBuilder)
    {
        onDoDefaultWebService(toBuilder);
        return true;
    }

    protected boolean onDoHeadWebService(StringBuilder toBuilder)
    {
        onDoDefaultWebService(toBuilder);
        return true;
    }

    protected boolean onDoPutWebService(StringBuilder toBuilder, org.w3c.dom.Document toXML)
    {
        onDoDefaultWebService(toBuilder);
        return true;
    }

    protected boolean onDoPostWebService(StringBuilder toBuilder, org.w3c.dom.Document toXML)
    {
        onDoDefaultWebService(toBuilder);
        return true;
    }

    protected boolean onDoDeleteWebService(StringBuilder toBuilder)
    {
        onDoDefaultWebService(toBuilder);
        return true;
    }

    protected boolean onDoOptionsWebService(StringBuilder toBuilder)
    {
        onDoDefaultWebService(toBuilder);
        return true;
    }


    private void writeResult(StringBuilder toBuilder)
    {
        this.setReturnValue(toBuilder.toString());
        
        // If there are any errors, and the result type is okay, then set it to server error
        if (hasErrors() && getResponse().getResultCode() == ResultCode.OK())
        {
            getResponse().setResultCode(ResultCode.INTERNAL_SERVER_ERROR());
        }

        // Create the results section
        StringBuilder loResults = new StringBuilder(getXMLHeader());
        loResults.append("<");
        loResults.append(getRootString());
        loResults.append(">");
        loResults.append("<Result>");

        loResults.append("<SessionID>");
        loResults.append(getSession().getSessionID());
        loResults.append("</SessionID>");

        loResults.append("<Code>");
        loResults.append(Integer.toString(getResponse().getResultCode().getCode()));
        loResults.append("</Code>");

        loResults.append("<Description>");
        loResults.append(getResponse().getResultCode().getValue());
        loResults.append("</Description>");

        // Write the errors if needed
        if (hasErrors())
        {
            List<Throwable> loErrors = getErrors();
            if (loErrors.size() > 0)
            {
                loResults.append("<ErrorList count=\"");
                loResults.append(Integer.toString(loErrors.size()));
                loResults.append("\">");

                for (Throwable loError : loErrors)
                {
                    loResults.append("<Error>");

                    loResults.append("<![CDATA[");
                    loResults.append(loError.toString());
                    loResults.append("]]>");

                    loResults.append("</Error>");
                }
                loResults.append("</ErrorList>");
            }
        }

        loResults.append("</Result>");
        loResults.append(toBuilder);
        loResults.append("</");
        loResults.append(getRootString());
        loResults.append(">");

        // Write the return
        writeResponse(loResults.toString());
    }



    protected String getXMLHeader()
    {
        return "<?xml version=\"1.0\" encoding=\"" + getEncodingString().toUpperCase() + "\"?>" + Goliath.Environment.NEWLINE();
    }


    protected String getEncodingString()
    {
        return "utf-8";
    }


    protected void createXMLList(Class<IXMLMapped> toClass, StringBuilder toBuilder)
    {
        try
        {
            createXMLList(toClass.newInstance().getObjectList(), toBuilder);
        }
        catch (Throwable ex)
        {
            addError(ex);
        }
    }

    @Override
    protected final String onGetContext()
    {
        return getContextString() + getWebServiceContext();
    }

    protected String getWebServiceContext()
    {
        return getClass().getSimpleName() + "/";
    }



    

    protected void createXMLList(IList<IXMLMapped> toList, StringBuilder toBuilder)
    {
        IList<IXMLMapped> loList = new List<IXMLMapped>();
        int lnCount = getMaxResults();

        if (toList.size() <= lnCount)
        {
            loList = toList;
        }
        else
        {
            for (int i=0; i<(lnCount); i++)
            {
                loList.add(toList.get(i));
            }
        }

        boolean llTyped = Boolean.parseBoolean(getRequest().getParameter("typed"));
        
        // If this is a get request then detail is true
        boolean llDetail = getRequest().getMethod() == RequestMethod.GET();
                
        // Detail can be overridden by the parameters        
        if (getRequest().getParameter("detail") != null)
        {
            llDetail = Boolean.parseBoolean(getRequest().getParameter("detail"));
        }

        toBuilder.append("<List count=\"");
        toBuilder.append(Integer.toString(loList.size()));
        toBuilder.append("\">");

        for (IXMLMapped loItem : loList)
        {
            if (llTyped)
            {
                loItem.appendToXML(toBuilder, XMLFormatType.TYPED());
            }
            else if (llDetail)
            {
                loItem.appendToXML(toBuilder, XMLFormatType.DETAILED());
            }
            else
            {
                loItem.appendToXML(toBuilder, XMLFormatType.DEFAULT());
            }
        }

        toBuilder.append("</List>");
    }
}