package Goliath.Web.Security.WebServices;

import Goliath.Applications.Application;
import Goliath.Collections.List;
import Goliath.Collections.PropertySet;
import Goliath.Constants.MimeType;
import Goliath.Data.DataObjects.SimpleDataObject;
import Goliath.DynamicCode.Java;
import Goliath.DynamicCode.Java.ClassDefinition;
import Goliath.Environment;
import Goliath.Exceptions.FileNotFoundException;
import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import Goliath.Web.Servlets.HTTPServlet;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

// TODO : This class is to test the functionallity and viability, this should be turned into a string formatter

/**
 * PHPModelGenerator creates all of the model files required to interact with
 * this system through php.  The files will be provided as a zip
 * @author admin
 */
public class PHPModelGenerator extends HTTPServlet
{

    @Override
    protected void doGet(IHTTPRequest toRequest, IHTTPResponse toResponse) throws ServletException, IOException
    {
        File loTemplate = Goliath.IO.Utilities.File.getRelativeLocation(new File("."), Goliath.Utilities.isNull(toRequest.getParameter("template"), "/resources/php/CodeIgniter/ModelTemplate.php"));
        if (loTemplate.exists())
        {
            // Create a temporary directory for this
            File loZipFolder = new File(Application.getInstance().getDirectory("tmp") + "PHPModels");
            loZipFolder.delete();
            loZipFolder.mkdirs();
            if (loZipFolder.exists())
            {
                // The directory exists, so get the list of classes to generate the model for
                List<Class<SimpleDataObject>> loClasses = getDataObjects();
                List<Throwable> loErrors = new List<Throwable>();
                for (Class<SimpleDataObject> loClass : loClasses)
                {
                    createModel(loTemplate, loClass, loZipFolder, loErrors);
                }
                
                if (loErrors.size() > 0)
                {
                    for (Throwable loError : loErrors)
                    {
                        addError(toResponse, loError);
                    }
                }
                else
                {
                    File loZip = new File(loZipFolder.getPath() + ".zip");
                    // Compress the file and serve it
                    Goliath.IO.Utilities.File.compress(loZipFolder, loZip, true);
                    try
                    {
                        toResponse.setContentType(MimeType.APPLICATION_ZIP());
                        toResponse.write(new Goliath.IO.File(loZip), false);
                    }
                    catch (FileNotFoundException ex)
                    {
                        addError(toResponse, ex);
                    }
                }
            }
            else
            {
                addError(toResponse, new Goliath.Exceptions.Exception("Unable to create the temp model directory" + loZipFolder.getPath(), false));
            }
        }
        else
        {
            addError(toResponse, new Goliath.Exceptions.Exception("A file with the name " + loTemplate.getPath() + " was not found", false));
        }
    }
    
    /**
     * Creates the model from the template provided
     * @param toTemplate The template to base the model on
     * @param toClass the class to template
     * @param toDestinationFolder the folder to place the generated tempplate
     * @param toErrors the list of errors if any
     */
    private void createModel(File toTemplate, Class<SimpleDataObject> toClass, File toDestinationFolder, List<Throwable> toErrors)
    {
        // TODO: look into converting camel case to underscore instead for readability
        String lcClassName = toClass.getSimpleName().substring(0, 1).toUpperCase() + toClass.getSimpleName().substring(1).toLowerCase();
        StringBuilder loProperties = new StringBuilder();
        StringBuilder loFields = new StringBuilder();
        
        
        ClassDefinition loClass = Java.getClassDefinition(toClass);
        List<String> lcProperties = loClass.getProperties();
        for(String lcProperty : lcProperties)
        {
            Java.MethodDefinition loMethod = loClass.getMethod(lcProperty);
            Goliath.Utilities.appendToStringBuilder(loFields, 
                                                    "protected \\$_", 
                                                    lcProperty, 
                                                    " = ", 
                                                    getDefaultValue(loMethod.getReturnType()), 
                                                    ";",
                                                    Environment.NEWLINE());
            
            Goliath.Utilities.appendToStringBuilder(loProperties, 
                                                    "public function get", 
                                                    lcProperty, 
                                                    "\\(\\)\\{",
                                                        Environment.NEWLINE(),
                                                        "\treturn \\$this->_",
                                                        lcProperty,
                                                        ";",
                                                        Environment.NEWLINE(),
                                                    "\\}", 
                                                    Environment.NEWLINE(),
                                                    "public function set", 
                                                    lcProperty, 
                                                    "\\(\\$tcValue\\)\\{", 
                                                        Environment.NEWLINE(),
                                                        // TODO: Implement the validation rules
                                                        "\t\\$this->_",
                                                        lcProperty,
                                                        " = \\$tcValue;",
                                                        Environment.NEWLINE(),
                                                    "\\}", 
                                                    Environment.NEWLINE(), Environment.NEWLINE());
        }
        
        //Prepare and replace the parameters in the templates
        PropertySet loParameters = new PropertySet();
        loParameters.setProperty("className", lcClassName);
        loParameters.setProperty("fields", loFields.toString());
        loParameters.setProperty("properties", loProperties.toString());
        
        String lcContent = Goliath.IO.Utilities.replaceParameters(Goliath.IO.Utilities.File.toString(toTemplate), loParameters);
        
        // Prepare the file for writing
        File loFile = new File(toDestinationFolder.getPath() + "/" + toClass.getName().replaceAll("\\.", "/") + ".php");
        try
        {
            Goliath.IO.Utilities.File.create(loFile);
            
            FileWriter loWriter = new FileWriter(loFile);
            loWriter.write(lcContent);
            loWriter.close();
        }
        catch (Throwable ex)
        {
            toErrors.add(ex);
        }
    }
    
    /**
     * Determines the default value for the type provided
     * @param toClass the class that is the return type
     * @return the value to set as default in PHP
     */
    private String getDefaultValue(Type toClass)
    {
        if (Java.isPrimitive(toClass))
        {
            return "NULL";
        }

        if (toClass.equals(java.lang.Boolean.class))
        {
            return "false";
        }
        else if (toClass.equals(java.lang.Byte.class))
        {
            return "0";
        }
        else if (toClass.equals(java.lang.Character.class))
        {
            return "''";
        }
        else if (toClass.equals(java.lang.Double.class))
        {
            return "0";
        }
        else if (toClass.equals(java.lang.Integer.class))
        {
            return "0";
        }
        else if (toClass.equals(java.lang.Float.class))
        {
            return "0";
        }
        else if (toClass.equals(java.lang.Long.class))
        {
            return "0";
        }
        else if (toClass.equals(java.lang.Short.class))
        {
            return "0";
        }
        return "''";
    }
    
    
    
    /**
     * Gets all of the SimpleDataObjects that this system has access to
     * @return the list of data objects within this system
     */
    private List<Class<SimpleDataObject>> getDataObjects()
    {
        return Application.getInstance().getObjectCache().getClasses(SimpleDataObject.class);
    }
    
}
