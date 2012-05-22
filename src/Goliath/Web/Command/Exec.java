/* =========================================================
 * StreamGobbler.java
 *
 * Author:     Victor Bayon
 * Created:     19 January 2008, 16:05
 *
 * Description
 * --------------------------------------------------------
 * Manages the actual command execution
 * 
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Web.Command;

import Goliath.Web.Command.Exec.*;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
/**
 * Helper class to manage the input/output streams that result from the command execution such as 
 * Standard input, Standard output and standard error). This class is launched as a thread to be able to read the different input/output channels  
 * 
 * @version     1.0 
 * @author      Victor Bayon
**/
class StreamGobbler extends Thread
{
	// The input stream
	private InputStream m_oIs;
	
	// Type of stream  (ERROR/OUTPUT)
	private String m_cType;

	public StreamGobbler(InputStream toIs, String type)
	{
		this.m_oIs = toIs;
		this.m_cType = type;
	}
	public void run()
	{
		// On this thread we read "Gobble" the stream
		try
		{
			InputStreamReader loIsr = new InputStreamReader(m_oIs);
			BufferedReader loBr = new BufferedReader(loIsr);
			String lcLine=null;
			while ( (lcLine = loBr.readLine()) != null)
				System.out.println(m_cType + ">" + lcLine);    
		} catch (IOException loIoe)
		{
			loIoe.printStackTrace();  
		}
	}
}

/**
 * This class is the actual class that launches the command as an external process and waits for
 * the external command to complete and return.  
 * 
 * @version     1.0 
 * @author      Victor Bayon
**/
public class Exec
{
	// Command that we want to execute
	private String[] m_acCmds;
	
	/**
     * Executes the command
     *
     * @return the return value (from the shell) of the execution or -20 if there was an exception
     */
	public int execute()
	{
		try
		{
			// get the runtime object 
			Runtime loRunTime = Runtime.getRuntime();
			
			System.out.println("RT " + loRunTime);
			System.out.println("Execing " + m_acCmds);
			System.out.println("Execing " + m_acCmds.length);
			
			// we execute the command now 
			Process loProc = loRunTime.exec(m_acCmds);
			
			// did we get  error messages?
			StreamGobbler loErrorGobbler = new 
			StreamGobbler(loProc.getErrorStream(), "ERROR");            

			// did we get any output messages?
			StreamGobbler loOutputGobbler = new 
			StreamGobbler(loProc.getInputStream(), "OUTPUT");

			// kick them off
			loErrorGobbler.start();
			loOutputGobbler.start();

			// We wait for the command to complete, any errors???
			int lnExitVal = loProc.waitFor();
			System.out.println("ExitValue: " + lnExitVal);  
			
			// we return the value
			return lnExitVal;
		} catch (Throwable loT)
		{
			loT.printStackTrace();
			return -20;
		}

	}
	/**
     * Creates an instance of this class
     *
     * @param  tcCmds the actual command line. Each argument is an element in the array
     */
	public Exec(String[] tacCmds)
	{
		this.m_acCmds = tacCmds;

	}
}


