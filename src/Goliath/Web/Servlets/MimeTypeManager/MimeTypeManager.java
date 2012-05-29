/* ========================================================
 * MimeTypeManager.java
 *
 * Author:      kenmchugh
 * Created:     Mar 13, 2011, 11:55:23 AM
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
package Goliath.Web.Servlets.MimeTypeManager;

import Goliath.Applications.Application;
import Goliath.Collections.HashTable;
import Goliath.Collections.List;
import Goliath.Constants.LogType;
import Goliath.Constants.MimeType;
import Goliath.Date;
import Goliath.Interfaces.Servlets.IServlet;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.SingletonHandler;
import Goliath.Web.Constants.ResultCode;
import Goliath.Web.Server;
import java.io.File;

/**
 * The MimeTypeManager is a singleton class which handles the response for requests
 * of specific mime types.
 * @author admin
 */
public class MimeTypeManager extends Goliath.Object
{
    private static HashTable<String, String> g_oPathMap;

    private static MimeTypeManager g_oMimeTypeManager;

    /**
     * Gets the instance of the mime type manager
     * @return the global mime type manager instance
     */
    public static MimeTypeManager getInstance()
    {
        if (g_oMimeTypeManager == null)
        {
            g_oMimeTypeManager = new MimeTypeManager();
        }
        return g_oMimeTypeManager;
    }

    private HashTable<String, String> m_oFileMap;
    private static String m_cDefaultfileName;
    private HashTable<String, MimeTypeManager> m_oManagers;
    private MimeTypeManager m_oDefaultManager;
    private String m_cCompressionRegex;



    /**
     * A MimeTypeManager is not publically createable
     */
    protected MimeTypeManager()
    {
    }

    /**
     * Gets the Global default file name.  This file name is the name that is
     * used when a request is made without providing a file name and a servlet
     * does not handle the context that has been specified
     * @return the global default file name
     */
    public static String getDefaultFileName()
    {
        if (m_cDefaultfileName == null)
        {
            m_cDefaultfileName = Application.getInstance().getPropertyHandlerProperty("WebServer.DefaultFile", "index.gsp");
        }
        return m_cDefaultfileName;
    }

    /**
     * Sets the default headers based on the file being processed
     * @param toResponse the response to set the headers on
     * @param toProcessFile the file that is being processed
     * @param toContentType the content type to give this response
     */
    protected void setDefaultHeaders(IHTTPResponse toResponse, Goliath.IO.File toProcessFile, MimeType toContentType)
    {
        toResponse.setResponseHeaders("Server", SingletonHandler.getInstance(Goliath.Web.LibraryVersion.class).toString());
        toResponse.setResponseHeaders("Date", Goliath.Utilities.Date.getRFC1123Date(new Date()));
        toResponse.setContentType(toContentType);
        
        toResponse.setCached(canCache(toProcessFile, toResponse));
        if (toResponse.getCached())
        {
            toResponse.setResponseHeaders("Last-Modified", Goliath.Utilities.Date.getRFC1123Date(new Date(toProcessFile.lastModified())));
            
            // Expiry is the time from the last modification up to a maximum of 4 hours
            // TODO: Make the 4 hours configurable
            setExpiry(toResponse, new java.util.Date().getTime() - toProcessFile.lastModified());
        }
        else
        {
            setExpiry(toResponse, 0);
        }
    }
    
    /**
     * Handles common headers, this can be overridden in the subclass to adjust the functionality
     * @param toProcessFile the file being processed
     * @param toRequest the request
     * @param toResponse the response
     * @return true if the servlet should continue processing, false if not
     */
    protected boolean handleHeaders(File toProcessFile, IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        // TODO: Look into a better pattern for processing these headers
        
        // TODO: Implement If-None-Match header
        // TODO: Implement If-Match header
        // TODO: Implment if-unmodified-since header
        // TODO: Implement etag
        
        // Process the if-modified-since header if it exists
        String lcDate = toRequest.getHeader("if-modified-since");
        if (!Goliath.Utilities.isNullOrEmpty(lcDate))
        {
            try
            {
                Date loDate = Goliath.Utilities.Date.parseRFC1123Date(lcDate);
                if (toProcessFile.exists() && toProcessFile.lastModified() <= loDate.getLong())
                {
                    Goliath.IO.File loFile = new Goliath.IO.File(toProcessFile);
                    // The file has not been modified since the date provided, so just send a 304
                    toResponse.setResultCode(ResultCode.NOT_MODIFIED());
                    // No need to continue processing after this
                    
                    setDefaultHeaders(toResponse, loFile, getMimeType(loFile, toResponse));
                    
                    toResponse.sendResponseHeaders();
                    toResponse.close();
                    return false;
                }
            }
            catch (Throwable ex)
            {
                // Date was invalid, so we process as if it were not provided.
            }
        }
        return true;
    }

    /**
     * Helper function to set the expiry time for this response.  This will be limited by WebServer.MaximumCacheExpiryMillis.
     * @param toResponse The response to set the cache time for
     * @param tnSuggestedMillis the requested expiry milliseconds setting
     * @return the actual milliseconds that the expiry time was set to
     */
    protected long setExpiry(IHTTPResponse toResponse, long tnSuggestedMillis)
    {
        long lnExpiry = Math.min(tnSuggestedMillis, Application.getInstance().<Long>getPropertyHandlerProperty("WebServer.MaximumCacheExpiryMillis", 14400000L));
        toResponse.setExpirySeconds((int) lnExpiry / 1000);
        return lnExpiry;
    }

    /**
     * Gets the type that this manager is serving to the client based on the file that is being processed
     * @param toFile the file that is going to be sent to the client
     * @param toResponse the response object which can be used to determine the type to return
     * @return the mime type to specify for the file
     */
    public MimeType getMimeType(Goliath.IO.File toFile, IHTTPResponse toResponse)
    {
        return MimeType.getEnumeration(MimeType.class, getMimeType(toFile.getName()));
    }
    
    /**
     * Checks if this response can be cached
     * @param toFile the file to check
     * @param toResponse the response that is being processed
     * @return true to allow caching, false otherwise
     */
    protected boolean canCache(Goliath.IO.File toFile, IHTTPResponse toResponse)
    {
        return !toFile.isTemporary();
    }

    /**
     * Gets the mapping from specific hostnames to root folders
     * @return the map of hosts to root folders
     */
    private HashTable<String, String> getHostMap()
    {
        if (g_oPathMap == null)
        {
            g_oPathMap = Application.getInstance().<HashTable<String, String>>getPropertyHandlerProperty("WebServer.HostPathMap");
            if (g_oPathMap == null)
            {
                g_oPathMap = new HashTable<String, String>();
                g_oPathMap.put(Server.getServer().getHostName().toLowerCase(), "");
                Application.getInstance().setPropertyHandlerProperty("WebServer.HostPathMap", g_oPathMap);
            }
        }
        return g_oPathMap;
    }


    /**
     * Helper function for getting the file that will be processed by the request
     * @param toRequest the request
     * @param toServlet the servlet
     * @return the reference to the file
     */
    public File getFile(IHTTPRequest toRequest, IServlet toServlet)
    {
        // Get the file name that we are dealing with, if a file name was not specified, the use the default file name
        String lcFile = toRequest.getPath() + (Goliath.Utilities.isNullOrEmpty(toRequest.getFile()) ? getDefaultFileName() : toRequest.getFile());
        Application.getInstance().log("[" + toRequest.getSession().getSessionID() + "] - Requested File - " + lcFile, LogType.TRACE());

        String lcPrefix = "";
        if (!Goliath.Utilities.getRegexMatcher("(?i)^[\\.]?/resources/|^[\\.]?/htdocs/", lcFile).find())
        {
            HashTable<String, String> loPathMap = getHostMap();
            lcPrefix = loPathMap.get(toRequest.getHost().toLowerCase());
        }

        // Get the reference to the file being processed
        File loFile = Goliath.IO.Utilities.File.get(toServlet.getServletConfig().getServletContext().getRealPath(lcPrefix + lcFile));
        return !loFile.exists() ? Goliath.IO.Utilities.File.get(toServlet.getServletConfig().getServletContext().getRealPath(lcFile))  : loFile;
    }

    /**
     * Processes the file specified in the request, this will find the correct
     * Manager and ask that manager to process the file
     * @param toRequest the request object
     * @param toResponse the response object
     * @param toServlet the servlet asking for the file to be processed
     */
    public final void process(IHTTPRequest toRequest, IHTTPResponse toResponse, IServlet toServlet)
    {
        process(getFile(toRequest, toServlet),toRequest, toResponse);
    }

    /**
     * Checks if this manager supports compression or not
     * @return true if compression is supported, false otherwise
     */
    public boolean supportsCompression()
    {
        return false;
    }

    /**
     * Processes the file specified in the request, this will write the result
     * out to the response, this should be overridden in subclasses
     * @param toRequest the request object
     * @param toResponse the response object
     */
    public void process(File toFile, IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        List<MimeType> loMimeType = new List<MimeType>(1);
        String lcPath = toFile.getPath();
        
        // Get the manager for the specified mime type
        MimeTypeManager loManager = get(toFile, toRequest, toResponse, loMimeType);
        
        toResponse.setContentType(loMimeType.get(0));

        // Turn on compression if required
        // Save some processing by pre checking
        toResponse.setUseCompression(toResponse.getSupportsCompression() && loManager.supportsCompression() && canCompress(lcPath));

        Goliath.Applications.Application.getInstance().log("Processing File - " + lcPath + " using (" + loMimeType.get(0).getValue() + ")" + loManager.getClass().getSimpleName() + (toResponse.getUseCompression() ? " with " : " without ") + "compression", LogType.TRACE());
        
        if (loManager.handleHeaders(toFile, toRequest, toResponse))
        {
            // User the manager to process the request
            loManager.process(toFile, toRequest, toResponse);
        }
    }
    
    /**
     * Gets the MimeTypeManager for the file type specified
     * @param toFile the file
     * @param toRequest the request
     * @param toResponse the response
     * @param toType the mime type that is used for this, this is overwritten by this call
     * @param tlExists, if true then the file must exist to get a Mime Type Manager
     * @return the Mime Type Manager that would normally be used to process this file
     */
    public MimeTypeManager get(File toFile, IHTTPRequest toRequest, IHTTPResponse toResponse, List<MimeType> toType, boolean tlExists)
    {
        String lcPath = toFile.getPath();
        
        // Extract the mime type we are dealing with
        String lcMimeType = getMimeType(lcPath);
        
        // Set the mime type used
        toType.add(MimeType.getEnumeration(MimeType.class, getMimeType(lcPath)));

        // Get the manager for the specified mime type
        return getManager(lcPath, lcMimeType, tlExists);
    }
    
    /**
     * Gets the MimeTypeManager for the file type specified
     * @param toFile the file
     * @param toRequest the request
     * @param toResponse the response
     * @param toType the mime type that is used for this, this is overwritten by this call
     * @return the Mime Type Manager that would normally be used to process this file
     */
    public MimeTypeManager get(File toFile, IHTTPRequest toRequest, IHTTPResponse toResponse, List<MimeType> toType)
    {
        return get(toFile, toRequest, toResponse, toType, true);
    }

    /**
     * Logs and writes the specified error to the response
     * @param toEx the exception to write
     *
     */
    public final void writeError(Throwable toEx, IHTTPResponse toResponse)
    {
        Application.getInstance().log(toEx);
        toResponse.setContentType(MimeType.TEXT_PLAIN());
        toResponse.setResultCode(ResultCode.INTERNAL_SERVER_ERROR());
        toResponse.setUseCompression(false);
        toResponse.setGZipEncoded(false);
        toResponse.write(toEx.toString().getBytes());
    }

    /**
     * Gets the MimeTypeManager for the file and mime type specified, if the
     * file does not exist this will return the default manager, otherwise this
     * will return the manager mapped to the mime type specified
     * @param tcPath the full path of the file being requested
     * @param tcMimeType the mime type of the file being requested
     * @return the Manager for the file and mime type specified
     */
    private MimeTypeManager getManager(String tcPath, String tcMimeType, boolean tlExists)
    {
        MimeTypeManager loReturn = null;

        // TODO: Allow individual files or directories to be excluded in the app settings
        // TODO: Allow security to restrict files and directories based on user id

        // If the path is empty there is nothing we can do
        if (!Goliath.Utilities.isNullOrEmpty(tcPath))
        {
            File loFile = new File(tcPath);
            if (!tlExists || (loFile.exists() && loFile.isFile()))
            {
                loReturn = getManagers().get(tcMimeType);
            }
        }
        return loReturn == null ? getDefaultManager() : loReturn;
    }

    /**
     * Returns the default mime type manager, this is the manager that will be
     * used when another manager can not be found for the specified mime type
     * @return the default mime type manager
     */
    private MimeTypeManager getDefaultManager()
    {
        if (m_oDefaultManager == null)
        {
            m_oDefaultManager = new DefaultMimeTypeManager();
        }
        return m_oDefaultManager;
    }

    /**
     * Gets the full list of managers that have been plugged in to the system
     * @return the full list of managers
     */
    private HashTable<String, MimeTypeManager> getManagers()
    {
        if (m_oManagers == null)
        {
            m_oManagers = new HashTable<String, MimeTypeManager>();
            List<Class<MimeTypeManager>> loManagers = Application.getInstance().getObjectCache().getClasses(MimeTypeManager.class);
            for (Class<MimeTypeManager> loManagerClass : loManagers)
            {
                try
                {
                    // Create the Manager
                    MimeTypeManager loManager = loManagerClass.newInstance();
                    List<String> loMimeTypes = loManager.getSupportedMimeTypes();
                    if (loMimeTypes != null)
                    {
                        for (String lcMimeType : loMimeTypes)
                        {
                            m_oManagers.put(lcMimeType, loManager);
                        }
                    }
                }
                catch (Throwable ex)
                {
                    Application.getInstance().log(ex);
                }
            }
        }
        return m_oManagers;
    }

    /**
     * Gets the mime types that are supported by this manager, this should be
     * overridden in subclasses, returning null or an empty list will stop the mime type manager
     * from being plugged in
     * @return the mime type supported by this Manager
     */
    protected List<String> getSupportedMimeTypes()
    {
        // TODO: Refactor to return a list of mime types
        return null;
    }

    /**
     * Gets the mime type for the file name specified.  The Mime type will be determined
     * from the file extension
     * @param tcFileName the file name used
     * @return the mime type
     */
    protected final String getMimeType(String tcFileName)
    {
        // Loop through all of the mime types, and find the first match
        // Load the file map if needed
        if (m_oFileMap == null)
        {
            getFileMap();
        }
        for (String lcExtension : m_oFileMap.keySet())
        {
            if (Goliath.Utilities.getRegexMatcher(lcExtension, tcFileName).find())
            {
                return m_oFileMap.get(lcExtension);
            }
        }
        return null;
    }

    /**
     * Gets a file that has been cached, using the path to determine the name of the cached file.
     * If the cached file does not exist, this will return null
     * @param tcPath the path of the cached file
     * @param tcExtension the extension of the cached file, not including the "."
     * @return the cached version of the file if it exists and if it was created after the original, otherwise returns null
     */
    protected final File getTempFile(String tcPath, String tcExtension)
    {
        File loRealFile = new File(tcPath);
        File loCachedFile = Goliath.IO.Utilities.File.getTemporary(tcPath, tcExtension);

        // Only return the file if both files exists, and if the real file has not been modified since the cached file was created
        return (loRealFile.exists() && loRealFile.isFile() &&
                loCachedFile.exists() && loCachedFile.isFile() &&
                loCachedFile.lastModified() >= loRealFile.lastModified()) ? loCachedFile : null;
    }

    /**
     * Gets the full path and name of what a temporary file would be called
     * @param tcPath the path of the real file
     * @param tcExtension the extension to append to the temp file
     * @return the absolute path of the temporary file that would represent the real path passed in
     */
    protected final String getTempFileName(String tcPath, String tcExtension)
    {
        return Goliath.IO.Utilities.File.getTemporary(tcPath, tcExtension).getAbsolutePath();
    }

    /**
     * Writes the file specified out to the stream
     * @param toFile the file to write the contents of
     * @param toResponse the response to write to
     * @return true if the write completed
     */
    protected final boolean writeFileToResponse(Goliath.IO.File toFile, IHTTPResponse toResponse)
    {
        toResponse.write(toFile, canCompress(toFile.getPath()));
        return toResponse.getErrors().size() == 0;
    }


    /**
     * Gets, and creates if needed, a file map showing what mime times are used by what extensions
     * @return The file map
     */
    private HashTable<String, String> getFileMap()
    {
        // TODO : Make this part of the servlet properties
        if (m_oFileMap == null)
        {
            m_oFileMap = Application.getInstance().getPropertyHandlerProperty("WebServer.ContentTypeMap");
            if (m_oFileMap == null)
            {
                // TODO: Should be loading from the mime type table here
                m_oFileMap = new HashTable<String, String>();

                // TODO: The Map should be storing MimeTypes instead of Strings
                m_oFileMap.put("(?i)\\.ico$", MimeType.IMAGE_ICON().getValue());
                m_oFileMap.put("(?i)\\.xml$|\\.xsl$", MimeType.APPLICATION_XML().getValue());
                m_oFileMap.put("(?i)\\.xslt$", "application/xslt+xml");
                m_oFileMap.put("(?i)\\.css$", MimeType.TEXT_CSS().getValue());
                m_oFileMap.put("(?i)\\.x?html?$", MimeType.TEXT_HTML().getValue());
                m_oFileMap.put("(?i)\\.gif$", MimeType.IMAGE_GIF().getValue());
                m_oFileMap.put("(?i)\\.swf$", MimeType.APPLICATION_X_SHOCKWAVE_FLASH().getValue());
                m_oFileMap.put("(?i)\\.png$", MimeType.IMAGE_PNG().getValue());
                m_oFileMap.put("(?i)\\.jar$", MimeType.APPLICATION_JAR().getValue());
                m_oFileMap.put("(?i)\\.zip$", MimeType.APPLICATION_ZIP().getValue());
                m_oFileMap.put("(?i)\\.js$", MimeType.TEXT_JAVASCRIPT().getValue());
                m_oFileMap.put("(?i)\\.txt$", MimeType.TEXT_PLAIN().getValue());
                m_oFileMap.put("(?i)\\.pdf$", MimeType.APPLICATION_PDF().getValue());
                m_oFileMap.put("(?i)\\.jpe?g?$", MimeType.IMAGE_JPG().getValue());
                m_oFileMap.put("(?i)\\.gsp$", MimeType.TEXT_HTML().getValue());
                m_oFileMap.put("(?i)\\.dmg$", MimeType.APPLICATION_OCTET_STREAM().getValue());
                m_oFileMap.put("(?i)\\.wav$", MimeType.AUDIO_X_WAV().getValue());
                m_oFileMap.put("(?i)\\.gsp$", MimeType.APPLICATION_GOLIATH_SERVER_PAGE().getValue());
                m_oFileMap.put("(?i)\\.appcache$", MimeType.TEXT_CACHE_MANIFEST().getValue());
                m_oFileMap.put("(?i)\\.less$", MimeType.TEXT_CSS_LESS().getValue());
                m_oFileMap.put("(?i)\\.mp3", MimeType.AUDIO_MPEG().getValue());
                m_oFileMap.put("(?i)\\.m4a", MimeType.AUDIO_MP4().getValue());
                m_oFileMap.put("(?i)\\.og[g|a]", MimeType.AUDIO_OGG().getValue());
                m_oFileMap.put("(?i)\\.webma", MimeType.AUDIO_WEBM().getValue());
                m_oFileMap.put("(?i)\\.wav|\\.pcm", MimeType.AUDIO_WAV().getValue());
                m_oFileMap.put("(?i)\\.mp4|\\.m4v", MimeType.VIDEO_MP4().getValue());
                m_oFileMap.put("(?i)\\.ogv", MimeType.VIDEO_OGG().getValue());
                m_oFileMap.put("(?i)\\.webmv?", MimeType.VIDEO_WEBM().getValue());
                
                /*
                323 	text/h323
                acx 	application/internet-property-stream
                ai 	application/postscript
                aif 	audio/x-aiff
                aifc 	audio/x-aiff
                aiff 	audio/x-aiff
                asf 	video/x-ms-asf
                asr 	video/x-ms-asf
                asx 	video/x-ms-asf
                au 	audio/basic
                avi 	video/x-msvideo
                axs 	application/olescript
                bas 	text/plain
                bcpio 	application/x-bcpio
                bin 	application/octet-stream
                bmp 	image/bmp
                c 	text/plain
                cat 	application/vnd.ms-pkiseccat
                cdf 	application/x-cdf
                cer 	application/x-x509-ca-cert
                class 	application/octet-stream
                clp 	application/x-msclip
                cmx 	image/x-cmx
                cod 	image/cis-cod
                cpio 	application/x-cpio
                crd 	application/x-mscardfile
                crl 	application/pkix-crl
                crt 	application/x-x509-ca-cert
                csh 	application/x-csh
                css 	text/css
                dcr 	application/x-director
                der 	application/x-x509-ca-cert
                dir 	application/x-director
                dll 	application/x-msdownload
                dms 	application/octet-stream
                doc 	application/msword
                dot 	application/msword
                dvi 	application/x-dvi
                dxr 	application/x-director
                eps 	application/postscript
                etx 	text/x-setext
                evy 	application/envoy
                exe 	application/octet-stream
                fif 	application/fractals
                flr 	x-world/x-vrml
                gtar 	application/x-gtar
                gz 	application/x-gzip
                h 	text/plain
                hdf 	application/x-hdf
                hlp 	application/winhlp
                hqx 	application/mac-binhex40
                hta 	application/hta
                htc 	text/x-component
                htt 	text/webviewhtml
                ief 	image/ief
                iii 	application/x-iphone
                ins 	application/x-internet-signup
                isp 	application/x-internet-signup
                jfif 	image/pipeg
                js 	application/x-javascript
                latex 	application/x-latex
                lha 	application/octet-stream
                lsf 	video/x-la-asf
                lsx 	video/x-la-asf
                lzh 	application/octet-stream
                m13 	application/x-msmediaview
                m14 	application/x-msmediaview
                m3u 	audio/x-mpegurl
                man 	application/x-troff-man
                mdb 	application/x-msaccess
                me 	application/x-troff-me
                mht 	message/rfc822
                mhtml 	message/rfc822
                mid 	audio/mid
                mny 	application/x-msmoney
                movie 	video/x-sgi-movie
                mp2 	video/mpeg
                mpe 	video/mpeg
                mpeg 	video/mpeg
                mpg 	video/mpeg
                mpp 	application/vnd.ms-project
                mpv2 	video/mpeg
                ms 	application/x-troff-ms
                mvb 	application/x-msmediaview
                nws 	message/rfc822
                oda 	application/oda
                p10 	application/pkcs10
                p12 	application/x-pkcs12
                p7b 	application/x-pkcs7-certificates
                p7c 	application/x-pkcs7-mime
                p7m 	application/x-pkcs7-mime
                p7r 	application/x-pkcs7-certreqresp
                p7s 	application/x-pkcs7-signature
                pbm 	image/x-portable-bitmap
                pfx 	application/x-pkcs12
                pgm 	image/x-portable-graymap
                pko 	application/ynd.ms-pkipko
                pma 	application/x-perfmon
                pmc 	application/x-perfmon
                pml 	application/x-perfmon
                pmr 	application/x-perfmon
                pmw 	application/x-perfmon
                pnm 	image/x-portable-anymap
                pot, 	application/vnd.ms-powerpoint
                ppm 	image/x-portable-pixmap
                pps 	application/vnd.ms-powerpoint
                ppt 	application/vnd.ms-powerpoint
                prf 	application/pics-rules
                ps 	application/postscript
                pub 	application/x-mspublisher
                qt 	video/quicktime
                ra 	audio/x-pn-realaudio
                ram 	audio/x-pn-realaudio
                ras 	image/x-cmu-raster
                rgb 	image/x-rgb
                rmi 	audio/mid
                roff 	application/x-troff
                rtf 	application/rtf
                rtx 	text/richtext
                scd 	application/x-msschedule
                sct 	text/scriptlet
                setpay 	application/set-payment-initiation
                setreg 	application/set-registration-initiation
                sh 	application/x-sh
                shar 	application/x-shar
                sit 	application/x-stuffit
                snd 	audio/basic
                spc 	application/x-pkcs7-certificates
                spl 	application/futuresplash
                src 	application/x-wais-source
                sst 	application/vnd.ms-pkicertstore
                stl 	application/vnd.ms-pkistl
                stm 	text/html
                svg 	image/svg+xml
                sv4cpio 	application/x-sv4cpio
                sv4crc 	application/x-sv4crc
                t 	application/x-troff
                tar 	application/x-tar
                tcl 	application/x-tcl
                tex 	application/x-tex
                texi 	application/x-texinfo
                texinfo 	application/x-texinfo
                tgz 	application/x-compressed
                tif 	image/tiff
                tiff 	image/tiff
                tr 	application/x-troff
                trm 	application/x-msterminal
                tsv 	text/tab-separated-values
                txt 	text/plain
                uls 	text/iuls
                ustar 	application/x-ustar
                vcf 	text/x-vcard
                vrml 	x-world/x-vrml
                wcm 	application/vnd.ms-works
                wdb 	application/vnd.ms-works
                wks 	application/vnd.ms-works
                wmf 	application/x-msmetafile
                wps 	application/vnd.ms-works
                wri 	application/x-mswrite
                wrl 	x-world/x-vrml
                wrz 	x-world/x-vrml
                xaf 	x-world/x-vrml
                xbm 	image/x-xbitmap
                xla 	application/vnd.ms-excel
                xlc 	application/vnd.ms-excel
                xlm 	application/vnd.ms-excel
                xls 	application/vnd.ms-excel
                xlt 	application/vnd.ms-excel
                xlw 	application/vnd.ms-excel
                xof 	x-world/x-vrml
                xpm 	image/x-xpixmap
                xwd 	image/x-xwindowdump
                z 	application/x-compress
                 * */

               Application.getInstance().setPropertyHandlerProperty("WebServer.ContentTypeMap", m_oFileMap);
            }
        }
        return m_oFileMap;
    }

    /**
     * Gets the list of extenstions that need to be compressed before sending to the client
     * @return the list of files to be compressed
     */
    private boolean canCompress(String tcFile)
    {
        if (m_cCompressionRegex == null)
        {
            m_cCompressionRegex = Application.getInstance().getPropertyHandlerProperty("WebServer.FilesSupportingCompression");
            if (m_cCompressionRegex == null)
            {
                m_cCompressionRegex =
                        "(?i)\\.css$|"+
                        "\\.js$|" +
                        "\\.gsp$|" +
                        "\\.less$|" +
                        "\\.png$|" +
                        "\\.ico$|" +
                        "\\.gif$|" +
                        "\\.css$|" +
                        "\\.jpe?g$|" +
                        "\\.x?html?$";
                Application.getInstance().setPropertyHandlerProperty("WebServer.FilesSupportingCompression", m_cCompressionRegex);
            }
        }
        return Goliath.Utilities.getRegexMatcher(m_cCompressionRegex, tcFile).find();
    }

}
