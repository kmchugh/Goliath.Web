/* =========================================================
 * Scrapper.java
 *
 * Author:      vbayon
 * Created:     07 January 2008, 16:34
 *
 * Description
 * --------------------------------------------------------
 * Provides a wrapper around the Goliath.Web.Client class to facilitate access 
 * to the remote Grabber service and the local caching mechanism
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/
package Goliath.Web;

/**
 * Provides a wrapper around the {@link Client} class that access the remote 
 * scrapper web service.
 * @version     1.0 7 January 2008
 * @author      vbayon
 **/
import java.awt.Image;
import java.awt.Toolkit;

public class Scrapper extends Goliath.Object {


	public Scrapper() {

	}

	/**
	 * Returns an {@link Image} associated to the url specified by tcUrl 
	 * argument
	 * @param tcUrl
	 * @return Image
	 */
	public Image getImage(String tcUrl) {
		Client client = new Client();
		Image loImage = Toolkit.getDefaultToolkit().getImage(client.grabSite(tcUrl));
		return loImage;
		
	}
	/**
	 * Not implemented yet 
	 * @param toUrl
	 * @return 
	 */
	public String getHTML(String toUrl) {
		// TODO: Implement this
		return null;
	}
}