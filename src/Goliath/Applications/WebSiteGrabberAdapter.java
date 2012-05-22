/* =========================================================
 * WebSiteGrabberAdapter.java
 *
 * Author:     Victor Bayon
 * Created:     19 January 2008, 16:05
 *
 * Description
 * --------------------------------------------------------
 * Implementation of the Service
 * 
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/
package Goliath.Applications;
/**
 * This class is the "Adapter" in Servent's terminology. This class connects the service to the application server (servent). It is similar to 
 * a servlet  but with more distributed computing possibilities (P2P, REST, "RMI" POJO)
 * 
 * @version     1.0 
 * @author      Victor Bayon
**/


import Goliath.Interfaces.Web.IWebSiteGrabberInterface;

import Goliath.Web.Threads.ServiceThread;


import java.util.Hashtable;
import java.util.ArrayList;
import java.io.File;


public class WebSiteGrabberAdapter implements IWebSiteGrabberInterface {
	
	
	// We have our capture threads on this array. However we are not doing any thread management yet
	// TODO: Do thread management (worker pool, etc)
	private ArrayList<ServiceThread> m_aoThreads = new ArrayList<ServiceThread>();

	
	// Service properties at start up. I don't use generics here as the properties can contain different type of  objects (I think)
	private Hashtable<String,String> m_oServiceProperties = new Hashtable<String,String>();

    public String captureSite(String url, String outputType, boolean refresh)
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }
	

	
	/***
	 * User for static testing (non deployment on app server)
	 * @param toLogger
	 */
	
}
