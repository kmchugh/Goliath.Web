/* =========================================================
 * LightHTTPResponse.java
 *
 * Author:      kmchugh
 * Created:     27-Feb-2008, 16:31:43
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
 * =======================================================*/

package Goliath.Web;

import Goliath.IO.File;
import Goliath.Web.Constants.ResultCode;
import Goliath.Applications.Application;
import Goliath.Constants.LogType;
import Goliath.Constants.MimeType;
import Goliath.Date;
import Goliath.Exceptions.InvalidOperationException;
import Goliath.Interfaces.Web.IHTMLOutputStream;
import Goliath.Interfaces.Web.IHTTPRequest;
import com.sun.corba.se.impl.oa.toa.TOAFactory;
import com.sun.net.httpserver.Headers;
import java.io.FileInputStream;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.zip.GZIPOutputStream;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 27-Feb-2008
 * @author      kmchugh
**/
public class LightHTTPResponse extends Goliath.Object
        implements Goliath.Interfaces.Web.IHTTPResponse
{
    com.sun.net.httpserver.HttpExchange m_oExchange;
    IHTMLOutputStream m_oResponse;
    IHTTPRequest m_oRequest;
    private Goliath.Collections.List<MimeType> m_oAcceptsMimeType;
    boolean m_lResponseHeadersSent;
    private ResultCode m_oResultCode;
    private long m_nResultLength;
    private Boolean m_lSupportsGZip;
    private boolean m_lUseCompression;
    private boolean m_lGZipEncoded;
    private boolean m_lCache;
    private int m_nExpiry;
    private boolean m_lAppendContentType;
    private MimeType m_oContentType;
    private Boolean m_lKeepalive;
    private List<Throwable> m_oErrors;
    
    // TODO: The compression, gzip, and deflate handling should be pushed to a stream factory
    
    /** Creates a new instance of LightHTTPResponse
     * @param toExchange  The exchange that took place
     * @param toRequest The request to respond to
     */
    public LightHTTPResponse(com.sun.net.httpserver.HttpExchange toExchange, IHTTPRequest toRequest)
    {
        m_oExchange = toExchange;
        m_oRequest = toRequest;
        m_nExpiry = 1200;
        // The Assumption will always be that the result code is OKAY
        setResultCode(ResultCode.OK());

        setContentType(MimeType.TEXT_HTML());
    }

    @Override
    public final boolean keepAlive()
    {
        if (m_lKeepalive == null)
        {
            m_lKeepalive = m_oRequest.getHeaders().hasProperty("Connection") && m_oRequest.getHeaderValues("Connection").contains("keep-alive");

            if (m_lKeepalive && m_oRequest.getHeaders().containsKey("Keep-Alive"))
            {
                String lcTimeout = m_oRequest.getHeader("Keep-Alive");
                // TODO : Process the keep alive parameter

            }
        }
        return m_lKeepalive;
    }

    @Override
    public final void setContentType(MimeType toType)
    {
        m_oContentType = toType;
    }

    @Override
    public final MimeType getContentType()
    {
        return m_oContentType != null ? m_oContentType : MimeType.TEXT_HTML();
    }

    @Override
    public final String getPreferredCharSet()
    {
        return m_oRequest.getPreferredCharSet();
    }

    @Override
    public boolean getCached()
    {
        return m_lCache;
    }

    @Override
    public int getExpirySeconds()
    {
        return m_nExpiry;
    }

    @Override
    public void setCached(boolean tlCache)
    {
        m_lCache = tlCache;
    }

    @Override
    public void setExpirySeconds(int tnSeconds)
    {
        m_nExpiry = tnSeconds;
    }
    
    

    @Override
    public boolean isSessionCookieSet()
    {
        return m_oRequest.isSessionCookieSet();
    }
    
    /**
     * Checks if this response supports GZIP
     * @return 
     */
    public boolean getSupportsGZIP()
    {
        if (m_lSupportsGZip == null)
        {
            // Check if this client supports gzip
            List<String> lcHeaders = m_oExchange.getRequestHeaders().get("accept-encoding");
            if (lcHeaders != null)
            {
                for (String lcHeader : lcHeaders)
                {
                    m_lSupportsGZip = lcHeader != null && lcHeader.indexOf("gzip") >= 0;
                    if (m_lSupportsGZip)
                    {
                        break;
                    }
                }
            }
        }
        return m_lSupportsGZip != null && m_lSupportsGZip;
    }

    // TODO: Implement default and sdch compression

    /**
     * Checks if this response supports responding with compressed content.
     * Currenlty this only supports GZip, but thsi may be expanded to include
     * deflate and sdch.
     * @return true if this response supports compressed content
     */
    @Override
    public boolean getSupportsCompression()
    {
        return getSupportsGZIP();
    }
    
    
    @Override
    public void setUseCompression(boolean tlUseCompression)
    {
        m_lUseCompression = getSupportsGZIP() && tlUseCompression;
        m_lGZipEncoded = m_lUseCompression;
    }
    @Override
    public boolean getUseCompression()
    {
        return m_lUseCompression && getSupportsGZIP();
    }
    
    @Override
    public void setGZipEncoded(boolean tlGZipEncoded)
    {
        m_lGZipEncoded = getSupportsGZIP() && tlGZipEncoded;
    }
    @Override
    public boolean getGZipEncoded()
    {
        return m_lGZipEncoded;
    }
    
    
    // TODO: Create expires method to change expiry of response
    /*
     *  The format is an absolute date and time as defined by HTTP-date in section 3.3.1; it MUST be in RFC 1123 date format:
     *  Expires = "Expires" ":" HTTP-date
     *  An example of its use is
     *  Expires: Thu, 01 Dec 1994 16:00:00 GMT
     */
    public Headers getHeaders()
    {
        return m_oExchange.getResponseHeaders();
    }
    
    /**
     * Checks if the response can accept the mime type specified
     * @param toType the type to check
     * @return true if the response will accept this mime type
     */
    @Override
    public boolean accepts(MimeType toMimeType)
    {
        return getAccepts().contains(toMimeType);
    }

    /**
     * Gets the list of preferred response types.  If there is no accepts
     * header then this will return null.  If this returns null, then it should be 
     * treated as if any mime type is allowed
     * @return return the list of accepted response types in order of preference
     */
    @Override
    public Goliath.Collections.List<MimeType> getAccepts()
    {
        if (m_oAcceptsMimeType == null)
        {
            String lcAccepts = m_oRequest.getHeader("accept");
            if (lcAccepts != null)
            {
                String[] laTypes = lcAccepts.split(",");
                m_oAcceptsMimeType = new Goliath.Collections.List<MimeType>(laTypes.length);
                for (String lcType : laTypes)
                {
                    MimeType loType = MimeType.getEnumeration(MimeType.class, lcType.toLowerCase().split(";")[0]);
                    if (loType != null)
                    {
                        m_oAcceptsMimeType.add(loType);
                    }
                }
            }
        }
        return m_oAcceptsMimeType;
    }
    
    

    public void setCookie(String tcName, String tcValue)
    {
        addCookieValue(tcName + "=" + tcValue);
    }

    public void setCookie(String tcName, String tcValue, String tcPath)
    {
        addCookieValue(tcName + "=" + tcValue +"; path=" + tcPath);
    }

    public void setCookie(String tcName, String tcValue, String tcPath, Date tdExpires)
    {
        addCookieValue(tcName + "=" + tcValue +"; path=" + tcPath + "; expires=" + tdExpires.getDate().toGMTString());
    }

    public void setCookie(String tcName, String tcValue, String tcPath, Date tdExpires, String tcDomain)
    {
        addCookieValue(tcName + "=" + tcValue +"; path=" + tcPath + "; expires=" + tdExpires.getDate().toGMTString() + "; domain=" + tcDomain);
    }

    public void setCookie(String tcName, String tcValue, String tcPath, Date tdExpires, String tcDomain, boolean tlSecure)
    {
        addCookieValue(tcName + "=" + tcValue +"; path=" + tcPath + "; expires=" + tdExpires.getDate().toGMTString() + "; domain=" + tcDomain + ((tlSecure) ? "; secure" : ""));
    }

    private void addCookieValue(String tcValue)
    {
        Goliath.Collections.List<String> loList = new Goliath.Collections.List<String>();
        loList.add(tcValue);
        m_oExchange.getResponseHeaders().put("Set-Cookie", loList);
    }
    
    /**
     * Sets the response header for a specified key
     * @param tcHeader the key to set the value for
     * @param tcValue the value to set
     */
    @Override
    public final void setResponseHeaders(String tcHeader, String tcValue)
    {
        m_oExchange.getResponseHeaders().set(tcHeader, tcValue);
    }

    @Override
    public void sendRedirect(String tcPath)
    {
        if (!isResponseHeadersSent())
        {
            setResultCode(ResultCode.MOVED_PERMANENTLY());

            // TODO: This should build a path relative or absolute depending on tcPath, starting with . should be relative to the current request, starting with / should be relative to the servlet container
            setResponseHeaders("Location", tcPath);
            sendResponseHeaders();
            close();
        }
        else
        {
            throw new InvalidOperationException("Can not redirect after headers are written");
        }
    }


    
    /**
     * Gets the session associated with the response
     * @return the session
     */
    @Override
    public Goliath.Interfaces.ISession getSession()
    {
        return m_oRequest.getSession();
    }
    
    /**
     * Writes a string value to the client
     * @param tcValue the string to write
     */
    @Override
    public void write(String tcValue)
    {
        try
        {
            write(tcValue.getBytes(getPreferredCharSet()));
        }
        catch (Throwable ex)
        {
            write(tcValue.getBytes());
        }
    }

    
    /**
     * Gets the result code for this response
     * @return the result code
     */
    @Override
    public ResultCode getResultCode()
    {
        // If the result code is null, we will assume OKAY
        if (m_oResultCode == null)
        {
            m_oResultCode = ResultCode.OK();
        }
        return m_oResultCode;
    }

    /**
     * Checks if the character set should be appendted to the response headers
     * @return true to append, false otherwise
     */
    protected boolean getAppendCharSetToResponse()
    {
        // TODO: This needs to be implemented with a mapping just like the compressed files
        MimeType loType = getContentType();
        return m_lAppendContentType ||
                loType == MimeType.APPLICATION_XML() ||
                loType == MimeType.TEXT_CSS() ||
                loType == MimeType.TEXT_HTML() ||
                loType == MimeType.TEXT_JAVASCRIPT() ||
                loType == MimeType.TEXT_PLAIN();
    }

    /**
     * Sets if the Character set should be appendted to the content type for this response
     * @param tlAppend true to append
     */
    @Override
    public void setAppendCharSetToResponse(boolean tlAppend)
    {
        m_lAppendContentType = tlAppend;
    }
    
    /**
     * Sets the result code for this response
     * @param tnCode the result code to set to
     */
    @Override
    public final void setResultCode(ResultCode tnCode)
    {
        Application.getInstance().log("Setting Result code to " + tnCode + " for response " + m_oRequest.getPath() + m_oRequest.getFile());
        m_oResultCode = tnCode;
    }
    
    /**
     * Sets the length of the data to be sent back to the client
     * @param tnLength the length of data
     */
    @Override
    public void setResultLength(long tnLength)
    {
        Application.getInstance().log("Setting Result length to " + tnLength + " for response " + m_oRequest.getPath() + m_oRequest.getFile());
        m_nResultLength = tnLength;
    }

    /**
     * Writes the byte array to the client starting at tnOffset and writing only
     * tnLength bytes
     * @param taValue the array to write
     * @param tnOffset the start position to write from
     * @param tnLength the number of bytes to write
     */
    @Override
    public void write(byte[] taValue, int tnOffset, int tnLength)
    {
        // TODO: implements buffering for streamed writes, or unknown write lengths
        try
        {
            IHTMLOutputStream loOutputStream = m_oResponse;
            if (loOutputStream == null)
            {
                if ((getSupportsGZIP() && m_lUseCompression) || (m_lGZipEncoded && getSupportsGZIP()))            
                {
                    this.setResponseHeaders("Content-Encoding", "gzip");
                }
                else
                {
                    setResultLength(taValue.length);
                }
            }
            if (!isResponseHeadersSent())
            {
                sendResponseHeaders();
            }
            getStream().write(taValue, tnOffset, tnLength, getSupportsGZIP() && m_lUseCompression);
        }
        catch (Exception e)
        {
            Application.getInstance().log(e);
        }
    }

    /**
     * Writes a byte array to the client
     * @param taValue the array of bytes to write
     */
    @Override
    public void write(byte[] taValue)
    {
        write(taValue, 0, taValue.length);
    }

    @Override
    public void write(File toFile, boolean tlAllowCompression)
    {
        // Prepare for writing
        /*
         * If this is not a temp file then we can cache it.  We will set the cache
         * time to the time between now and the creation time
         */
        if (!isResponseHeadersSent() && !toFile.isTemporary() && getSupportsCompression() && tlAllowCompression)
        {
            // See if a .gz file exists already
            java.io.File loGZipped = new java.io.File(Goliath.IO.Utilities.File.getTemporary(toFile.getPath(), "gz").getAbsolutePath());
            if (!loGZipped.exists() || loGZipped.lastModified() <= toFile.lastModified())
            {
                // Create the zipped version
                Goliath.IO.Utilities.File.compress(toFile, loGZipped, true);
            }
            if (loGZipped.exists())
            {
                try
                {
                    toFile = new Goliath.IO.File(loGZipped);
                    // We are no longer going to compress this file on the fly
                    setUseCompression(false);
                    setGZipEncoded(true);
                }
                catch(Throwable ex)
                {
                    return;
                }
            }
        }

        FileChannel loChannel = null;

        try
        {
            loChannel = new FileInputStream(toFile).getChannel();
            loChannel.transferTo(0, loChannel.size(), Channels.newChannel((HTMLOutputStream)getStream()));
        }
        catch(Throwable ex)
        {
            addError(ex);
            Application.getInstance().log(ex);
        }
        finally
        {
            if (loChannel != null)
            {
                try
                {
                    loChannel.close();
                }
                catch(Throwable ex)
                {
                }
            }
        }
    }
    
    

    @Override
    public void flush()
    {
        try
        {
            getStream().flush();
        }
        catch(Throwable ex)
        {
            Application.getInstance().log(ex);
        }
    }

    @Override
    public IHTMLOutputStream getStream()
    {
        if (m_oResponse == null)
        {
            // If response headers have not been sent, just assume everything is okay
            // because we are attempting to write
            if (!isResponseHeadersSent())
            {
                sendResponseHeaders();
            }
            
            if (this.getUseCompression() && this.getGZipEncoded())
            {
                try
                {
                    m_oResponse = new HTMLOutputStream(new GZIPOutputStream(m_oExchange.getResponseBody()));
                }
                catch (Throwable ex)
                {
                    Application.getInstance().log(ex);
                }
            }
            else
            {
                m_oResponse = new HTMLOutputStream(m_oExchange.getResponseBody());
            }
        }
        return m_oResponse;
    }
    
    /**
     * Checks if the response headers have been sent
     * @return true if they have been sent
     */
    @Override
    public boolean isResponseHeadersSent()
    {
        return m_lResponseHeadersSent;
    }
    
    private boolean contentTypeValid()
    {
        List<MimeType> loTypes = getAccepts();
        return loTypes == null || (loTypes.contains(MimeType.ALLTYPES()) || loTypes.contains(getContentType()));
    }
    
    
    /**
     * Send the response headers to the client
     */
    @Override
    public void sendResponseHeaders()
    {
        // TODO: Response headers need to be checked to see if they are already set before changing them individually here

        // Can only send once
        if (m_lResponseHeadersSent)
        {
            Application.getInstance().log("Response headers already sent.", LogType.DEBUG());
            return;
        }
        m_lResponseHeadersSent = true;
        
        try
        {
            // Check the response type
            if (!contentTypeValid())
            {
                m_oResultCode = ResultCode.NOT_ACCEPTABLE();
            }
            
            // Set the result code to an error code if the response has any errors
            if (this.hasErrors() && this.getResultCode() == ResultCode.OK())
            {
                m_oResultCode = ResultCode.INTERNAL_SERVER_ERROR();
            }

            // Set the Session response header
            if (Goliath.Utilities.isNullOrEmpty(m_oRequest.getCookie("goliath_app_id")))
            {
                // TODO: Implement expiring session cookies based on the session expiry times, this should only be done if the session expiry length is changed
                this.setCookie("goliath_app_id", getSession().getSessionID());
            }
            
            if (!m_lCache)
            {
                this.setResponseHeaders("Cache-Control", "no-store");
                this.setResponseHeaders("Pragma", "no-cache");
                this.setResponseHeaders("Expires", "0");
            }
            else
            {
                this.setResponseHeaders("Cache-Control", "max-age=" + Integer.toString(m_nExpiry) + ", must-revalidate");
            }
            this.setResponseHeaders("Robots", "none");

            // Set up the content type header

            String lcContentTypeHeader = getContentType().getValue() + (getAppendCharSetToResponse() ?"; charset=" + getPreferredCharSet() : "");
            Application.getInstance().log("Setting Content Type to : " + lcContentTypeHeader + " for " + m_oRequest.getPath() + m_oRequest.getFile());
            setResponseHeaders("Content-Type", lcContentTypeHeader);

            // TODO : Implement full headers properly

            if (keepAlive() && m_nResultLength == 0)
            {
                this.setResponseHeaders("Transfer-Encoding", "chunked");
            }
            else
            {
                this.setResponseHeaders("Content-Length", Long.toString(m_nResultLength >=0 ? m_nResultLength : 0));
            }
            
            this.setResponseHeaders("Connection", keepAlive() ? "Keep-Alive" : "close");

            if ((getUseCompression()) || (m_lGZipEncoded && getSupportsGZIP()))
            {
                this.setResponseHeaders("Content-Encoding", "gzip");
            }
        
        
            Application.getInstance().log("Sending response headers", LogType.DEBUG());
            m_oExchange.sendResponseHeaders(m_oResultCode.getCode(), keepAlive() ? 0 : m_nResultLength);
        }
        catch (Exception e)
        {
            // The response headers may have been sent already.
            // TODO: Change when response headers are sent so they can be controlled by the developer
            m_lResponseHeadersSent = false;
        }
        finally
        {
        }
    }


    /**
     * Closes the response
     */
    @Override
    public void close()
    {
        try
        {
            Application.getInstance().log("Closing response for " + m_oRequest.getPath() + m_oRequest.getFile());
            clearErrors();
            if (m_oResponse != null)
            {
                m_oResponse.close();
            }
            else
            {
                m_oExchange.close();
            }
        }
        catch (Exception e)
        {
            Application.getInstance().log(e);
        }
    }

    @Override
    public boolean addError(Throwable ex)
    {
        if (m_oErrors == null)
        {
            m_oErrors = new Goliath.Collections.List<Throwable>();
        }
        return m_oErrors.add(ex);
    }

    @Override
    public void clearErrors()
    {
        if (m_oErrors != null)
        {
            m_oErrors = null;
        }
    }

    @Override
    public Goliath.Collections.List<Throwable> getErrors()
    {
        return m_oErrors == null ? new Goliath.Collections.List<Throwable>(0) : new Goliath.Collections.List<Throwable>(m_oErrors);
    }

    @Override
    public boolean hasErrors()
    {
        return m_oErrors != null && m_oErrors.size() > 0;
    }


}
