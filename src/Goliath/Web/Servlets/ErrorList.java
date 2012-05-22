package Goliath.Web.Servlets;

import Goliath.Collections.List;
import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import java.io.IOException;
import org.w3c.dom.Document;

/**
 * Adds an errorList parameter to the response parameters if there is an
 * error that has occurred in any previously included servlets.  This servlet
 * should generally be included as the last servlet in order to pick up all
 * the errors from previously executed servlets
 * @author admin
 */
public class ErrorList extends HTTPServlet
{
    // TODO: Implement a servlet priority which will allow the order to be adjusted based on a priority parameter
    

    @Override
    protected void doGet(IHTTPRequest toRequest, IHTTPResponse toResponse) throws ServletException, IOException
    {
        updateErrorParameters(toRequest, toResponse);
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
    protected final void updateErrorParameters(IHTTPRequest toRequest, IHTTPResponse toResponse)
    {
        if (!hasErrors(toResponse))
        {
            toRequest.clearParameter("errorList");
        }
        else
        {
            toRequest.setParameter("errorList", getErrorHTML(toResponse));
        }
    }

    /**
     * Helper function to turn errors into HTML for rendering to the client
     * @param toResponse the response that contains the errors
     * @return the HTML String with the errors
     */
    protected final String getErrorHTML(IHTTPResponse toResponse)
    {
        StringBuilder loBuilder = new StringBuilder();
        List<Throwable> loErrors = getErrors(toResponse);
        if (loErrors.size() > 0)
        {
            loBuilder.append("<div class=\"errorList\">");
            for (Throwable loError : loErrors)
            {
                Goliath.Utilities.appendToStringBuilder(loBuilder,
                "<div class=\"error\">",
                Goliath.XML.Utilities.makeSafeForXML(loError.toString()).replaceAll("\\n", "<br/>"),
                "</div>");
            }
            loBuilder.append("</div>");
        }
        return loBuilder.toString();
    }
    
    
    
}
