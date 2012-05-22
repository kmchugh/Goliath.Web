/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Goliath.Web;

/**
 *
 * @author admin
 */
public class LightHTTPProxyHandler
{
    // TODO: This class should allow a server to act as a simple proxy, forwarding any communication
    
    /*
    // Check if we are just forwarding requests
        // TODO: This should be refactored out to a LightHTTPProxyHandler class instead of this class
        ServerConnection loConnection = getForwardingConnection();
        if (loConnection != null)
        {
            WebRequest loRemoteRequest = null;
            try
            {
                // TODO: Refactor this to a check for isWebServiceRequest on the request object
                if (loRequest.getPath().toLowerCase().startsWith("/ws/"))
                {
                    loRemoteRequest = loConnection.makeWebServiceRequest(loRequest, loRequest.getSession().getSessionID());
                }
                else
                {
                    loRemoteRequest = loConnection.makeWebRequest(loRequest, loRequest.getSession().getSessionID());
                }
            }
            catch (Throwable ex)
            {
                Application.getInstance().log(ex);
            }

            if (loRemoteRequest != null)
            {
                int lnLength = loRemoteRequest.getResultLength();
                loResponse.setResultLength(lnLength >= 0 ? lnLength : 0);
                loResponse.setResultCode(loRemoteRequest.getResultCode());
                loResponse.setContentType(loRemoteRequest.getContentType());

                int lnBufferLength = 8192;
                int lnReadLength = 0;
                byte[] laBuffer = new byte[lnBufferLength];
                InputStream loStream = new BufferedInputStream(loRemoteRequest.getResponseStream());
                boolean llWritten = false;
                while((lnReadLength = loStream.read(laBuffer, 0, lnBufferLength)) != -1)
                {
                    loResponse.write(laBuffer, 0, lnReadLength);
                    llWritten = true;
                }
                if (!llWritten)
                {
                    loResponse.sendResponseHeaders();
                }
                loResponse.close();
                
                return;
            }
        }
     * 
     */
    
}
