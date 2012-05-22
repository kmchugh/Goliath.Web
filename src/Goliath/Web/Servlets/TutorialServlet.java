/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Goliath.Web.Servlets;

import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import java.io.File;
import java.io.IOException;
import org.w3c.dom.Document;

/**
 *
 * @author admin
 */
public class TutorialServlet  extends HTTPServlet
{
    
    @Override
    protected void doGet(IHTTPRequest toRequest, IHTTPResponse toResponse) throws ServletException, IOException
    {
        createInclude(toRequest, toResponse);
    }

    @Override
    protected void doConnect(IHTTPRequest toRequest, IHTTPResponse toResponse) throws ServletException, IOException
    {
        doGet(toRequest, toResponse);
    }

    @Override
    protected void doDelete(IHTTPRequest toRequest, IHTTPResponse toResponse) throws ServletException, IOException
    {
        doGet(toRequest, toResponse);
    }

    @Override
    protected void doHead(IHTTPRequest toRequest, IHTTPResponse toResponse) throws ServletException, IOException
    {
        doGet(toRequest, toResponse);
    }

    @Override
    protected void doOptions(IHTTPRequest toRequest, IHTTPResponse toResponse) throws ServletException, IOException
    {
        doGet(toRequest, toResponse);
    }

    @Override
    protected void doPut(IHTTPRequest toRequest, IHTTPResponse toResponse, Document toXML) throws ServletException, IOException
    {
        doGet(toRequest, toResponse);
    }

    @Override
    protected void doTrace(IHTTPRequest toRequest, IHTTPResponse toResponse) throws ServletException, IOException
    {
        doGet(toRequest, toResponse);
    }
    
    
    
    /**
     * Helper function to set the errorList parameter
     * @param toRequest the request with the errors
     * @param toResponse the response with the errors
     */
    protected final void createInclude(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        String lcInclude = toRequest.getParameter("include");
        if (lcInclude != null)
        {
            File loFile = new File("./htdocs/" + toRequest.getPath() + lcInclude);
            if (loFile.exists())
            {
                toRequest.setParameter("includedContent", Goliath.IO.Utilities.File.toString(loFile));
            }
            else
            {
                addError(toResponse, new Goliath.Exceptions.Exception("The file [" + loFile.getPath() + "] does not exist.", false));
            }
        }
        else
        {
            addError(toResponse, new Goliath.Exceptions.Exception("The parameter [include] was not set", false));
        }
    }
}