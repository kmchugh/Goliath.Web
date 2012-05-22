/* =========================================================
 * ServiceThread.java
 *
 * Author:      Victor Bayon
 * Created:     
 * 
 * Description
 * --------------------------------------------------------
 * Service Thread
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/
package Goliath.Web.Threads;
/**
 * Thread that executes the command. It only executes the command when the service is free to do so
 * The application server will generate a new thread for each service request. Each service request 
 * will then create this thread that waits till the 
 * server/computer where the service is installed is free to take the actual screen shot
 *
 * @version    
 * @author      Victor Bayon
 **/

import java.net.MalformedURLException;
import java.net.URL;
import Goliath.Web.Command.CommandControl;
import Goliath.Web.Command.TakeScreenshot;

public class ServiceThread extends Thread {

	// Waiting time till we try to execute again
	// TODO change this value
	private int m_nWait  = 5;

	// Web site that we want to capture
	private String m_cWebsiteURL;

	/**
	 * @param websiteURL
	 */
	
	public ServiceThread(String lcWebsiteURL)
	{
		this.m_cWebsiteURL = lcWebsiteURL;
		
	}
	public void run() {
		
		while (true){
			
			try {
				System.out.println("Going to take a pic - " + this.getName());
				sleep(m_nWait  * 1000);
				CommandControl  loCommandControl = CommandControl.getINSTANCE();
				TakeScreenshot loTakeScreenshot = new TakeScreenshot();
				URL loUrl = null;
				try {
					loUrl = new URL(m_cWebsiteURL);
				} catch (MalformedURLException e) {
					// We got a URL with the wrong syntax
					// TODO handle this
					e.printStackTrace();
					break;
				}
                                
				loTakeScreenshot.setUrl(m_cWebsiteURL);
				                                 
				loCommandControl.setCommand(loTakeScreenshot);
				
				
				// Try configuration
				
				try {
					loCommandControl.execute();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (loTakeScreenshot.getOutput() != null)
				{
					System.out.println(" We are finished - " + this.getName() + " - " + loUrl.toString() );
					//we got it
					break;
				}
			} catch (InterruptedException ex) 
			{ 
				ex.printStackTrace();
			}
		}
	}
}

