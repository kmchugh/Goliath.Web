/* =========================================================
 * TakeScreenshot.java
 *
 * Author:     Victor Bayon
 * Created:    
 *
 * Description
 * --------------------------------------------------------
 * Launches a external shell command
 * 
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/
package Goliath.Web.Command;


import Goliath.Applications.ServiceConfiguration;
import Goliath.Interfaces.Web.ICommand;
import java.util.Hashtable;

/**
 * Class that prepares the environment to launch a external comand via Exec that will takes the website screenshot 
 * The command should be configured with an instance of {@link ServiceConfiguration} 
 * 
 * @version     1.0
 * @author      Victor Bayon
**/
public class TakeScreenshot implements ICommand {
	
	
	
	private String m_cUrl;
	private String m_cOutput;
	private Hashtable m_oServiceProperties;
	
	/** Creates an instance of this class */
	public TakeScreenshot()
	{
		
	}
	/** Launches the external command */
	public void execute() throws Exception {
		
		// Do we have a url?
		if (m_cUrl == null)
		{
			throw new Exception(this + "input url is null");
		}
		
		
		// get the configuration
		ServiceConfiguration serviceConfiguration = ServiceConfiguration.getInstance();
		m_oServiceProperties = serviceConfiguration.getProperties();


		String lcCoginitiveEdgeNodeLocalDir = m_oServiceProperties.get("coginitiveEdgeNodeLocalDir").toString();
		String lcCmdPathScreenGrab = m_oServiceProperties.get("cmdpath").toString();
		String lcCogitiveEdgeNodeURL = m_oServiceProperties.get("cogitiveEdgeNodeURL").toString();
		
		
		// Prepare the command line (as in bash commands)
		String[] cmds = new String[5];
		cmds[0] = lcCmdPathScreenGrab;
		cmds[1] = m_cUrl;
		cmds[2] = lcCoginitiveEdgeNodeLocalDir;
		cmds[3] = "ce" + Integer.toString(m_cUrl.hashCode());
		
		// This is a dummy, should be removed from the bash command
		// TODO: remove this
		cmds[4] = Integer.toString(10);

		String m_cOutput = "NOTFOUND";
		
		// We launch the command now
		Exec exec = new Exec(cmds);
		// Was the execution valid?
		if (exec.execute() == 0)
		{			

			m_cOutput = lcCogitiveEdgeNodeURL = lcCogitiveEdgeNodeURL + m_cUrl.hashCode();
		}
	
	}
	/** Sets the URL */
	public void setUrl(String url) {
		this.m_cUrl = url;
	}
	/** Gets the output that results in executing the command */
	public String getOutput() {
		return m_cOutput;
	}
}

