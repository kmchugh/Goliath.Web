/* =========================================================
 * IHTTPResponse.java
 *
 * Author:      kmchugh
 * Created:     29-May-2008, 11:49:17
 * 
 * Description
 * --------------------------------------------------------
 * General Interface Description.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/

package Goliath.Interfaces.Web;

import Goliath.Collections.List;
import Goliath.Constants.MimeType;
import Goliath.Web.Constants.ResultCode;

/**
 * Interface Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 29-May-2008
 * @author      kmchugh
**/
public interface IHTTPResponse
{
    /**
     * Checks if the response headers have been sent
     * @return true if they have been sent
     */
    boolean isResponseHeadersSent();
    
    /**
     * Sends the response headers to the client
     */
    void sendResponseHeaders();

    /**
     * Sends a redirect to the client, sending them on to the new path
     * @param tcPath the path the client should browse to
     */
    void sendRedirect(String tcPath);
    
    /**
     * Closes the response
     */
    void close();

    /**
     * Checks if this connection should be kept alive
     * @return
     */
    boolean keepAlive();
    
    /**
     * Gets the list of types that this response is expecting
     * @return the list of types that are expected by this response
     */
    List<MimeType> getAccepts();
    
    /**
     * Checks if the response can accept the mime type specified
     * @param toType the type to check
     * @return true if the response will accept this mime type
     */
    boolean accepts(MimeType toType);
    
    /**
     * Sets the specified response header
     * @param tcHeader the header value to set
     * @param tcValue the value to set it to
     */
    void setResponseHeaders(String tcHeader, String tcValue);

    /**
     * Sets the content type of this response
     * @param toType the content type
     */
    void setContentType(MimeType toType);
    
    /**
     * Writes a string value to the client
     * @param tcValue the string to write
     */
    void write(String tcValue);
    
    /**
     * Writes a byte array to the client
     * @param taValue the array of bytes to write
     */
    void write(byte[] taValue);
    
    /**
     * Writes the specified file out to the response
     * @param toFile the file to write
     * @param tlAllowCompression true to allow the file to be compressed if the response supports compression
     */
    void write(Goliath.IO.File toFile, boolean tlAllowCompression);

    /**
     * Writes the byte array to the client starting at tnOffset and writing only
     * tnLength bytes
     * @param taValue the array to write
     * @param tnOffset the start position to write from
     * @param tnLength the number of bytes to write
     */
    void write(byte[] taValue, int tnOffset, int tnLength);

    /**
     * Ensures all of the content has been written, flushes the outputstream
     */
    void flush();

    /**
     * Gets the content type of this response
     * @return the content type of the response
     */
    MimeType getContentType();

    /**
     * Sets if the Character set should be appendted to the content type for this response
     * @param tlAppend true to append
     */
    void setAppendCharSetToResponse(boolean tlAppend);
    
    /**
     * Sets the result code for this response
     * @param tnCode the result code to set to
     */
    void setResultCode(ResultCode tnCode);

    /**
     * Gets the current result code for this response
     * @return the code that has been set for this response
     */
    ResultCode getResultCode();
    
    /**
     * Sets the length of the data to be sent back to the client
     * @param tnLength the length of data
     */
    void setResultLength(long tnLength);
    
    /**
     * Gets the session associated with the response
     * @return the session
     */
    Goliath.Interfaces.ISession getSession();
    
    /**
     * Set if the response should use compression for this round.
     * Compression will only be used if it is supported by the browser
     * By default the response will use compression if available.
     * 
     * @param tlUseCompression it true will allow the response to use compression
     */
    void setUseCompression(boolean tlUseCompression);
    
    /**
     * Checks if the response will attempt to use compression
     * This value does not say if the browser can use compression, only if this connection
     * will attempt compression
     * @return a boolean value saying if this response will attempt to use compression
     */
    boolean getUseCompression();
    
    /**
     * Forces the server to use gzip encoding, this should be used if the file is already gzipped
     * @param tlGZipEncoded set to true to force the encoding
     */
    void setGZipEncoded(boolean tlGZipEncoded);
    
    /**
     * returns true if the response will force gzip encoding
     * @return true if force gzip encoding
     */
    boolean getGZipEncoded();


    /**
     * Checks if this response supports responding with compressed content.
     * Currenlty this only supports GZip, but thsi may be expanded to include
     * deflate.
     * @return true if this response supports compressed content
     */
    public boolean getSupportsCompression();
    
    /**
     * Returns true if the session cookie has been set
     * @return true if the session cookie has already been set and retrieved from the client
     */
    boolean isSessionCookieSet();
    
    /**
     * Checks if the response is cachable
     * @return true if cacheable
     */
    boolean getCached();
    
    /**
     * Sets the response to be cacheable, this must be done before the response headers are sent or it will have no effect
     * @param tlCache true to set the response as cacheable
     */
    void setCached(boolean tlCache);
    
    /**
     * Gets the number of seconds until the response expires
     * @return the number of seconds till this respons will expire
     */
    int getExpirySeconds();
    
    /**
     * Sets the number of seconds for this response to expire
     * @param tnSeconds the number of seconds until this response expires
     */
    void setExpirySeconds(int tnSeconds);

    /**
     * Returns the Character set that is preferred
     * @return A string representing the character set that should be used when writing back to the client
     */
    String getPreferredCharSet();

    /**
     * Gets the output stream that can be used to write back to the client
     * @return the stream to write to in order to send data to the client
     */
    // TODO: Refactor this out
    IHTMLOutputStream getStream();

    /**
     * Adds an error to the response
     */
    boolean addError(Throwable ex);

    /**
     * Gets the list of errors that have occured in this resposne.  The list that
     * is returned may not be the internally stored list of errors, therefore it should
     * not be used to manipulate the error list
     * @return the list of errors, an empty list if there are no errors
     */
    List<Throwable> getErrors();

    /**
     * Clears all of the errors that are on the response
     */
    void clearErrors();

    /**
     * Checks if this response has had any errors
     * @return
     */
    boolean hasErrors();


}
