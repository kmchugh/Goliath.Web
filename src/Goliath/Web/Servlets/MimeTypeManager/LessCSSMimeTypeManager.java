/* ========================================================
 * LessCSSMimeTypeManager.java
 *
 * Author:      admin
 * Created:     Oct 13, 2011, 11:35:47 AM
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
import Goliath.Collections.List;
import Goliath.Constants.LogType;
import Goliath.Constants.MimeType;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import com.google.javascript.jscomp.mozilla.rhino.JavaScriptException;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.tools.shell.Global;

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
public class LessCSSMimeTypeManager extends MimeTypeManager
{

    /**
     * Creates a new instance of LessCSSMimeTypeManager
     */
    public LessCSSMimeTypeManager()
    {
    }

    @Override
    protected List<String> getSupportedMimeTypes()
    {
        return new List<String>(new String[]{
                MimeType.TEXT_CSS_LESS().getValue(),
                MimeType.TEXT_CSS().getValue()
        });
    }
    
    /**
     * Gets the RegEx Matcher for matching include files in the specified string
     * @param tcMatchString the string to extract include files from
     * @return the Matcher
     */
    private Matcher getIncludeMatcher(String tcMatchString)
    {
        return Goliath.Utilities.getRegexMatcher("@import\\s(?:url)?(?:\\()?[\"|'](.+?)[\"|'](?:\\))?;", tcMatchString);
    }
    
    /**
     * Extracts the relative file location from the xslt
     * @param toRootLocation the file to base the location on
     * @param toMatcher the Matcher to get the file name from
     * @return the Relative file.
     */
    private File getFileFromMatch(File toRootLocation, Matcher toMatcher)
    {
        return Goliath.IO.Utilities.File.getRelativeLocation(toRootLocation, toMatcher.group(1));
    }
    
    /**
     * Checks if the file is valid, this returns true if the original file and
     * all of its dependencies have not changed since the cached version was created.
     * If there is no cached version this will also return false
     * @param toFile 
     * @return 
     */
    private boolean isValid(File toOriginal, File toCached)
    {
        boolean llReturn = false;
        if (toOriginal.equals(toCached) || (
            toOriginal != null && toOriginal.exists() && toCached != null && toCached.exists()))
        {
            // Check the original
            llReturn = true;
            List<File> loDependencies = new List<File>();
            getDependencies(toOriginal, loDependencies);
            for (File loFile : loDependencies)
            {
                if (loFile.lastModified() > toCached.lastModified())
                {
                    llReturn = false;
                    break;
                }
            }
        }
        return llReturn;
    }
    
    /**
     * Gets the list of dependencies from the .css or .less file.  A dependency
     * is any file that has beein imported using the @import command.  This will
     * also include the file being checked as a dependency.
     * @param toFile the file to extract the dependencies from
     * @param toDependencies the running collection of dependencies
     */
    private void getDependencies(File toFile, List<File> toDependencies)
    {
        Goliath.Utilities.checkParameterNotNull("toGSP", toFile);
        Goliath.Utilities.checkParameterNotNull("toList", toDependencies);

        if (toFile.exists() && !toDependencies.contains(toFile))
        {
            toDependencies.add(toFile);
            // Load the GSP document in to memory to transform
            String lcCSSDocument = Goliath.IO.Utilities.File.toString(toFile);

            // Extract all of the includes from the .gsp
            Matcher loMatcher = getIncludeMatcher(lcCSSDocument);
            while (loMatcher.find())
            {
                // Loop through each of the groups and get the dependencies
                File loIncluded = getFileFromMatch(toFile, loMatcher);
                if (loIncluded.exists() && !toDependencies.contains(loIncluded))
                {
                    // Also need to process the file just included if it is a .css or less
                    if (loIncluded.getName().toLowerCase().endsWith(".css") ||
                            loIncluded.getName().toLowerCase().endsWith(".less"))
                    {
                        getDependencies(loIncluded, toDependencies);
                    }
                    else
                    {
                        toDependencies.add(loIncluded);
                    }
                }
                else if (!loIncluded.exists())
                {
                    Application.getInstance().log("The file " + loIncluded.getAbsolutePath() + " was included in " + toFile.getAbsolutePath() + " but it does not exist", LogType.ERROR());
                }
            }
        }
    }
    
    @Override
    public MimeType getMimeType(Goliath.IO.File toFile, IHTTPResponse toResponse)
    {
        return MimeType.TEXT_CSS();
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
            File loFile = llMinify ? getTempFile(toFile.getPath(), "min.css") : toFile;
            if (!isValid(toFile, loFile) || llRefresh)
            {
                try
                {
                    // Set up the less compiler in rhino
                    URL loWindowEngine = new File("./resources/javascript/scripting/browser.js").toURI().toURL();
                    URL loLessJS = new File("./resources/javascript/less/less.js").toURI().toURL();
                    URL loLessEngine = new File("./resources/javascript/less/engine.js").toURI().toURL();
                    URL loCSSEngine = new File("./resources/javascript/css/cssmin.js").toURI().toURL();
                    URL loCSSCompile = new File("./resources/javascript/css/compileCSS.js").toURI().toURL();
                    
                    // Check if the compiled .css exists
                    loFile = new File(getTempFileName(toFile.getPath(), "min.css"));
                    URL loLessURL = toFile.toURI().toURL();
                    // Transform the .less to .css
                    Context loContext = Context.enter();
                    Global loGlobal = new Global(loContext);
                    Scriptable loScope = loContext.initStandardObjects(loGlobal);

                    // Create the window object
                    // Load the .js files
                    loContext.evaluateReader(loScope, new InputStreamReader(loWindowEngine.openConnection().getInputStream()), loWindowEngine.getFile() , 1, null);
                    loContext.evaluateReader(loScope, new InputStreamReader(loLessJS.openConnection().getInputStream()), loLessJS.getFile() , 1, null);
                    loContext.evaluateReader(loScope, new InputStreamReader(loLessEngine.openConnection().getInputStream()), loLessEngine.getFile() , 1, null);
                    loContext.evaluateReader(loScope, new InputStreamReader(loCSSEngine.openConnection().getInputStream()), loCSSEngine.getFile() , 1, null);
                    loContext.evaluateReader(loScope, new InputStreamReader(loCSSCompile.openConnection().getInputStream()), loCSSCompile.getFile() , 1, null);
                    Function loCompileFile = (Function) loScope.get("compileCSS", loScope);
                    Context.exit();

                    String lcCSS = (String)Context.call(null, loCompileFile, loScope, loScope, new Object[]{loLessURL.getProtocol() + ":" + loLessURL.getFile(), getClass().getClassLoader(), toFile.getPath().toLowerCase().endsWith(".less")});
                    
                    
                    Goliath.IO.Utilities.File.create(loFile);
                    FileWriter loWriter = new FileWriter(loFile);
                    loWriter.write(lcCSS);
                    loWriter.close();
                }
                catch (JavaScriptException ex)
                {
                    writeError(ex, toResponse);
                }
                catch (org.mozilla.javascript.JavaScriptException ex)
                {
                    writeError(ex, toResponse);
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
        // TODO: include support for compression
        return true;
    }
}