package Goliath.Web.Servlets;


import Goliath.Exceptions.ServletException;
import Goliath.Interfaces.Servlets.IRequestDispatcher;
import Goliath.Interfaces.Servlets.IServlet;
import Goliath.Interfaces.Web.IHTTPRequest;
import Goliath.Interfaces.Web.IHTTPResponse;
import java.io.IOException;

/* ========================================================
 * RequestDispatcher.java
 *
 * Author:      kenmchugh
 * Created:     Mar 12, 2011, 11:24:48 PM
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




public class RequestDispatcher extends Goliath.Object
        implements IRequestDispatcher
{
    private IServlet m_oServlet;

    public RequestDispatcher(IServlet toServlet)
    {
        m_oServlet = toServlet;
    }

    @Override
    public void forward(IHTTPRequest toRequest, IHTTPResponse toResponse) throws ServletException, IOException
    {
        m_oServlet.service(toRequest, toResponse);
    }

    @Override
    public void include(IHTTPRequest toRequest, IHTTPResponse toResponse) throws ServletException, IOException
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public IServlet getServlet()
    {
        return m_oServlet;
    }



}
