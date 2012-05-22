/* ========================================================
 * MultiPartParser.java
 *
 * Author:      kenmchugh
 * Created:     Jun 13, 2011, 3:04:46 PM
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
package Goliath.Web;

import Goliath.Applications.Application;
import Goliath.Collections.List;
import Goliath.Collections.PropertySet;
import Goliath.Constants.MimeType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Jun 13, 2011
 * @author      kenmchugh
 **/
public class MultiPartParser extends Goliath.Object
{

    /**
     * Creates a new instance of MultiPartParser
     */
    public MultiPartParser()
    {
    }

    public static List<Part> parseParts(String tcBoundary, InputStream toStream)
    {
        BufferedReader loReader;
        try
        {
            loReader = new BufferedReader(new InputStreamReader(toStream, "macroman"));
        }
        catch (Throwable ex)
        {
            Application.getInstance().log(ex);
            return new List<Part>(0);
        }

        List<Part> loParts = new List<Part>();

        FileOutputStream loFileStream = null;
        File loFileContents = null;
        StringBuilder loBuilder = null;
        PropertySet loProperties = new PropertySet();

        try
        {

            // Read up to the boundary
            String lcContent = loReader.readLine();
            while (lcContent != null && !lcContent.equals(tcBoundary))
            {
                // Do nothing, all this content is not part of the data
            }

            // Now we have hit the boundary which means we are reading the parts
            lcContent = loReader.readLine();


            // We will continue reading until we are finished all of the content
            while (lcContent != null && !lcContent.equals(tcBoundary + "--"))
            {
                // This will mark the finsh of the properties and the start of the content
                if (lcContent.length() == 0)
                {
                    Part loPart = new Part(loProperties);
                    loProperties.clear();
                    
                    MimeType loMimeType = loPart.getContentType();

                    lcContent = loReader.readLine();
                    while(lcContent != null && !lcContent.startsWith(tcBoundary))
                    {
                        // If there is no content type, then this is a form field
                        if (loMimeType != MimeType.TEXT_PLAIN())
                        {
                            // File data

                            // If the temp file does not exist, then create it
                            if (loFileContents == null)
                            {
                                String lcContentType = loMimeType.getValue();
                                loFileContents = Goliath.IO.Utilities.File.getTemporary();
                                loFileStream = new FileOutputStream(loFileContents);
                            }
                            loFileStream.write(lcContent.getBytes("macroman"));
                            loFileStream.write("\n".getBytes("macroman"));
                        }
                        else
                        {
                            // Form Field
                            if (loBuilder == null)
                            {
                                loBuilder = new StringBuilder();
                            }
                            loBuilder.append(lcContent);
                        }
                        lcContent = loReader.readLine();
                    }
                    if (loFileContents != null)
                    {
                        loPart.setFile(loFileContents);
                    }
                    else if (loBuilder != null)
                    {
                        loPart.setContents(loBuilder);
                    }
                    loBuilder = null;
                    loParts.add(loPart);
                    loFileContents = null;
                    if (loFileStream != null)
                    {
                        loFileStream.close();
                        loFileStream = null;
                    }
                }
                else
                {
                    String[] laValues = lcContent.split("; ");
                    for (String lcFullPropertyValue : laValues)
                    {
                        if (lcFullPropertyValue.indexOf(": ") >= 0)
                        {
                            String[] laProperty = lcFullPropertyValue.split(": ", 2);
                            loProperties.setProperty(laProperty[0], laProperty[1]);
                        }
                        else
                        {
                            if (lcFullPropertyValue.indexOf("=\"") >= 0)
                            {
                                String[] laProperty = lcFullPropertyValue.split("=", 2);
                                loProperties.setProperty(laProperty[0], laProperty[1].replaceAll("\"", ""));
                            }
                        }
                    }
                }
                lcContent = loReader.readLine();
            }
        }
        catch (Throwable ex)
        {
            Application.getInstance().log(ex);
        }
        finally
        {
            if (loFileStream != null)
            {
                try
                {
                    loFileStream.close();
                }
                catch (Throwable ex)
                {

                }
            }
        }

        return loParts;
    }
}
