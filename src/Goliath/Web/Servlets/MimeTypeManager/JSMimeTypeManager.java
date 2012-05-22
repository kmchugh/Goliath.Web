/* ========================================================
 * MinifyJSMimeTypeManager.java
 *
 * Author:      admin
 * Created:     Oct 13, 2011, 11:35:35 AM
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

import Goliath.Collections.List;
import Goliath.Constants.MimeType;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.WarningLevel;
import java.io.File;
import java.io.FileWriter;
import java.util.logging.Level;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 Oct 13, 2011
 * @author      admin
 **/
public class JSMimeTypeManager extends MimeTypeManager
{
    /**
     * Creates a new instance of MinifyJSMimeTypeManager
     */
    public JSMimeTypeManager()
    {
    }

    @Override
    protected List<String> getSupportedMimeTypes()
    {
        return new List<String>(new String[]{
                MimeType.TEXT_JAVASCRIPT().getValue()
        });
    }
    
    @Override
    protected MimeType getMimeType(Goliath.IO.File toFile, IHTTPResponse toResponse)
    {
        return MimeType.TEXT_JAVASCRIPT();
    }

    @Override
    public void process(File toFile, IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        // TODO: Add a minify flag to prevent minification of the script
        if (toFile.exists() && toFile.isFile())
        {
            boolean llRefresh = Goliath.Utilities.isNull(toRequest.getParameter("refresh"), "false").equalsIgnoreCase("true");
            boolean llMinify = Goliath.Utilities.isNull(toRequest.getParameter("minify"), "true").equalsIgnoreCase("true");
            
            // Check if the minified .js exists
            File loFile = llMinify ? getTempFile(toFile.getPath(), "min.js") : toFile;
            if (loFile == null || llRefresh)
            {
                // TODO: Make this configurable with a flag
                // TODO: Make a gsp tool for compiling the javascript
                com.google.javascript.jscomp.Compiler.setLoggingLevel(Level.OFF);
                CompilerOptions loOptions = new CompilerOptions();
                CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(loOptions);
                WarningLevel.QUIET.setOptionsForWarningLevel(loOptions);
 
                
                List<JSSourceFile> loOptimisableSources = new List<JSSourceFile>();
                List<JSSourceFile> loNonOptimisableSources = new List<JSSourceFile>();
                
                loOptimisableSources.add(JSSourceFile.fromFile(toFile));
                
                com.google.javascript.jscomp.Compiler loCompiler = new com.google.javascript.jscomp.Compiler();
                
                loCompiler.compile(loNonOptimisableSources, loOptimisableSources, loOptions);
                
                loFile = new File(getTempFileName(toFile.getPath(), "min.js"));
                try
                {
                    if (!loFile.exists())
                    {
                        Goliath.IO.Utilities.File.create(loFile);
                    }
                    FileWriter loWriter = new FileWriter(loFile);
                    if(Goliath.Utilities.isNull(toRequest.getParameter("showInfo"), "false").equalsIgnoreCase("true"))
                    {
                        loWriter.write("/* \n");
                        
                        loWriter.write("\n***********  WARNINGS  ***********\n");
                        for (JSError loError : loCompiler.getWarnings())
                        {
                            loWriter.write(loError.toString());
                            loWriter.write("\n");
                        }
                        
                        
                        loWriter.write("\n***********   ERRORS   ***********\n");
                        for (JSError loError : loCompiler.getErrors())
                        {
                            loWriter.write(loError.toString());
                            loWriter.write("\n");
                        }
                        
                        loWriter.write("\n\n */\n\n");
                    }
                    loWriter.write(loCompiler.toSource());
                    loWriter.close();
                }
                catch (Throwable ex)
                {
                    writeError(ex, toResponse);
                }
            }
            
            try
            {
                setDefaultHeaders(toResponse, new Goliath.IO.File(toFile), getMimeType(new Goliath.IO.File(loFile), toResponse));
                writeFileToResponse(new Goliath.IO.File(loFile), toResponse);
            }
            catch (Throwable ex)
            {
                writeError(ex, toResponse);
            }
        }
    }

    @Override
    public boolean supportsCompression()
    {
        // TODO: Include support for compression
        return true;
    }
}