/* =========================================================
 * ServiceConfiguration.java
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
package Goliath.Applications;
/**
 * Singleton helper class to act as an adapter to obtain configuration settings to different app servers or web applications 
 * needs to be configured. Currently only adapts to servent (Properties) 
 *
 * @version    
 * @author      Victor Bayon
 **/
import java.util.Properties;
import java.util.Hashtable;

public class ServiceConfiguration {
	
	// the properties as a Hashtable
	private  Hashtable<String,String> m_oServiceProperties;
	// This instance
	public static ServiceConfiguration m_oINSTANCE;
	/**
	 * 
	 */
	private ServiceConfiguration()
	{
		m_oServiceProperties = new Hashtable();
	}
	/**
	 * Gets an instance of this class
	 * @return this instance
	 */
	public static ServiceConfiguration getInstance()
	{
		if (m_oINSTANCE == null)
		{
			m_oINSTANCE = new ServiceConfiguration();
		}
		return m_oINSTANCE;
		
	}
	/**
	 * Converts the servent properties into a Hashtable
	 * @param toProperties
	 * @return
	 */
	public Hashtable<String,String> getConfiguration(Properties toProperties)
	{
		// TODO: removed hardcoded values
		m_oServiceProperties.put("coginitiveEdgeNodeLocalDir",toProperties.getProperty("coginitiveEdgeNodeLocalDir"));
		m_oServiceProperties.put("cmdpath",toProperties.getProperty("cmdpath"));
		m_oServiceProperties.put("cogitiveEdgeNodeURL",toProperties.getProperty("cogitiveEdgeNodeURL"));
		m_oServiceProperties.put("maxNumOfThreads",toProperties.getProperty("maxNumOfThreads"));
		return m_oServiceProperties;
		
	}
	/**
	 * Gets the properties
	 * @return the properties
	 */
	public Hashtable<String,String> getProperties()
	{
		// TODO: handle when this properties are null
		return m_oServiceProperties;
	}
}
