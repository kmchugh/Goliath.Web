/* ========================================================
 * ServletPropetyHelper.java
 *
 * Author:      kenmchugh
 * Created:     Mar 13, 2011, 4:21:43 PM
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
import Goliath.Collections.PropertySet;
import Goliath.Constants.XMLFormatType;
import Goliath.XML.XMLFormatter;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

        
/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Mar 13, 2011
 * @author      kenmchugh
**/
public class ServletConfigXMLFormatter extends Goliath.XML.XMLFormatter<ServletConfig>
{
    @Override
    public Class supports()
    {
        return ServletConfig.class;
    }

    @Override
    protected boolean allowContent(ServletConfig toObject, XMLFormatType toFormatType)
    {
        return true;
    }

    @Override
    protected void onWriteContent(XMLStreamWriter toStream, ServletConfig toObject, XMLFormatType toFormatType)
    {
        if (toObject == null)
        {
            return;
        }
        
        try
        {
            toStream.writeStartElement("url-pattern");
            toStream.writeCharacters(toObject.getServletContext().getURLPattern());
            toStream.writeEndElement();
        }
        catch (Throwable ex)
        {
            Application.getInstance().log(ex);
        }

        if (toObject.getInitParameterNames() != null)
        {
            // Write any other parameters that exist
            for (String lcString : toObject.getInitParameterNames())
            {
                Object loValue = toObject.getInitParameter(lcString);
                if (loValue != null)
                {
                    try
                    {
                        toStream.writeStartElement(lcString);
                        if (Goliath.DynamicCode.Java.isPrimitive(loValue) && toFormatType == XMLFormatType.TYPED())
                        {
                            toStream.writeAttribute("type", loValue.getClass().getName());
                        }

                        XMLFormatter.appendToXMLStream(loValue, toFormatType, toStream, null);

                        toStream.writeEndElement();
                    }
                    catch (Throwable ex)
                    {
                        Application.getInstance().log(ex);
                    }
                }
            }
        }
    }

    @Override
    protected void onStartedElement(XMLStreamReader toReader, String tcNodeName, PropertySet toAttributes, Object toObject, XMLFormatType toFormatType)
    {
        if (tcNodeName.equalsIgnoreCase("url-pattern"))
        {
            try
            {
                String lcKey = toReader.getElementText();
                ((ServletConfig)toObject).setServletContext(lcKey);
            }
            catch (Throwable ex)
            {
                Application.getInstance().log(ex);
            }
        }
        else
        {
            iterateToNextPosition(toReader);
            Object loValue = fromXMLReader(toReader, toFormatType, null);

            // TODO: Check if we are able to use the Generic type for toObject rather than specifing the Object class
            ((ServletConfig)toObject).setParameter(tcNodeName, loValue);
        }
    }
}