/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Goliath.Web.WebServices;

import Goliath.Applications.Application;
import Goliath.Collections.List;
import Goliath.Collections.PropertySet;
import Goliath.Constants.MimeType;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Web.Constants.ResultCode;
import Goliath.Web.WebRequest;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 *
 * @author kenmchugh
 */
public class WebServiceRequest extends WebRequest
{
    private Node m_oResult;

    public WebServiceRequest(String tcServerURL, IHTTPRequest toRequest, int tnTimeout, String tcSessionID)
    {
        super(tcServerURL, toRequest, tnTimeout, tcSessionID);
    }

    public WebServiceRequest(String tcResourceURL, String tcBody, int tnTimeout, String tcSessionID)
    {
        super(tcResourceURL, tcBody, tnTimeout, MimeType.APPLICATION_XML(), tcSessionID);
    }

    public WebServiceRequest(String tcResourceURL, String tcBody, String tcSessionID)
    {
        super(tcResourceURL, tcBody, MimeType.APPLICATION_XML(), tcSessionID);
    }

    public WebServiceRequest(String tcResourceURL, Document toBody, int tnTimeout, String tcSessionID)
    {
        super(tcResourceURL, toBody, tnTimeout, tcSessionID);
    }

    public WebServiceRequest(String tcResourceURL, Document toBody, String tcSessionID)
    {
        super(tcResourceURL, toBody, tcSessionID);
    }

    public WebServiceRequest(String tcResourceURL, PropertySet toEncodedBody, int tnTimeout, String tcSessionID)
    {
        super(tcResourceURL, toEncodedBody, tnTimeout, tcSessionID);
    }

    public WebServiceRequest(String tcResourceURL, PropertySet toEncodedBody, String tcSessionID)
    {
        super(tcResourceURL, toEncodedBody, tcSessionID);
    }

    public WebServiceRequest(String tcResourceURL, int tnTimeout, String tcSessionID)
    {
        super(tcResourceURL, tnTimeout, tcSessionID);
    }
    
    public WebServiceRequest(String tcResourceURL, String tcSessionID)
    {
        super(tcResourceURL, tcSessionID);
    }

    private void parseResults(Node toResult)
    {
        // Parse the result codes and descriptions
        List<Node> loCodeNodes = Goliath.XML.Utilities.getElementsByTagName(toResult, "Code");

        if (loCodeNodes.size() == 1)
        {
            setResultCode(ResultCode.getResultCode(Integer.parseInt(Goliath.XML.Utilities.toString(loCodeNodes.get(0).getFirstChild()))));
        }

        // Parse the errors if there are any
        // TODO: Parse the errors
    }

    private void parseXML(String tcXMLString)
    {
        org.w3c.dom.Document loXML = null;
        Node loWSResult = null;

        // If the string does not contain WSResults, then this is invalid
        // TODO : Shouldn't hardcode the Root node, this needs to be defineable in the server connection
        if (tcXMLString.indexOf("WSResult") == -1)
        {
            addError(new Exception("URL is not a web service: " + getResourceURL()));
            setResultCode(ResultCode.INTERNAL_SERVER_ERROR());
            return;
        }

        try
        {
            loXML = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new java.io.StringReader(tcXMLString)));
        }
        catch(Throwable ex)
        {
            Application.getInstance().log(ex);
            addError(ex);
            setResultCode(Goliath.Web.Constants.ResultCode.INTERNAL_SERVER_ERROR());
            return;
        }

        NodeList loWSResults = loXML.getElementsByTagName("WSResult");
        if (loWSResults.getLength() != 1)
        {
            Exception ex = new Goliath.Exceptions.Exception("Malformed Web Service Result from URL - " + getResourceURL());
            Application.getInstance().log(tcXMLString);
            addError(ex);
            setResultCode(ResultCode.INTERNAL_SERVER_ERROR());
            return;
        }
        else
        {
            loWSResult = loWSResults.item(0);
            
            // Parse any errors
            NodeList loErrors = loXML.getElementsByTagName("ErrorList");
            if (loErrors.getLength() >= 1)
            {
                for (int i=0; i<loErrors.item(0).getChildNodes().getLength(); i++)
                {
                    Node loNode = loErrors.item(0).getChildNodes().item(i);
                    if (loNode.getNodeName().equalsIgnoreCase("error"))
                    {
                        if (loNode.getNodeType() == Node.ELEMENT_NODE)
                        {
                            addError(new Goliath.Exceptions.Exception(loNode.getTextContent(), false));
                            continue;
                        }
                    }
                }
            }
        }

        // Parse the actual results
        for (int i=0; i<loWSResult.getChildNodes().getLength(); i++)
        {
            Node loNode = loWSResult.getChildNodes().item(i);
            if (!loNode.getNodeName().equalsIgnoreCase("result"))
            {
                if (loNode.getNodeType() == Node.ELEMENT_NODE)
                {
                    m_oResult = loNode;
                    break;
                }
            }
            else
            {
                parseResults(loNode);
            }
        }
    }

    public final Node getResult()
    {
        if (m_oResult == null && wasSuccessful())
        {
            parseXML(getResultString());
        }
        return m_oResult;
    }
    
    @Override
    protected void populateFailedErrors()
    {
        parseXML(getResultString());
        if (!hasErrors())
        {
            super.populateFailedErrors();
        }
    }
}
