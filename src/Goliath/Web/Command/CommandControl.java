/* =========================================================
 * CommandControl.java
 *
 * Author:      Victor Bayon
 * Created:     
 * 
 * Description
 * --------------------------------------------------------
 * Control the execution of commands in threads
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 * 
 * =======================================================*/
package Goliath.Web.Command;


/**
 * Implementation of the State pattern inside a singleton object to make sure there is only one screenshot being taken at any single time
 * The computer/server on which the service is running has only one screen (assuming that is the default configuration).  
 *
 * @version     
 * @author      Victor Bayon
**/

import Goliath.Interfaces.Web.ICommand;

public class CommandControl {
	
	private ICommand m_oSlot;
	private java.util.Random m_oRandom = new java.util.Random(java.util.Calendar.getInstance().getTimeInMillis());
	private final static int m_nFREE = 0;
	private final static int m_nBUSY = 1;
	private final static int m_nNleep = 10;
	private int m_nState = m_nFREE;
	private static CommandControl m_oINSTANCE;
	
	private CommandControl()
	{
		
	}

	/** Sets the execution slot
	 * 
	 * @param toSlot the command that we want to execute. This class does not know about the specifics of the command to execute,
	 * it simply expects that the command will have an execute method via its interface 
	 */
	public void setCommand(ICommand toSlot) {
		this.m_oSlot = toSlot;
	}
	/**
	 * Executes the command
	 * @throws Exception
	 */
	public void execute() throws Exception
	{
		if (m_nState == m_nFREE)
		{
			m_nState = m_nBUSY;
			System.out.println("Now we are BUSY...." + m_oSlot);
			try {
				m_oSlot.execute();
				// We wait a bit
				Thread.sleep(m_oRandom.nextInt(m_nNleep) * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			m_nState = m_nFREE;
		}
		if (m_nState == m_nBUSY)
		{
			try {
				System.out.println(" STATE IS BUSY");
				Thread.sleep(m_oRandom.nextInt(m_nNleep) * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}	
	}
	public static CommandControl getINSTANCE() {
		if (m_oINSTANCE == null)
		{
			m_oINSTANCE = new CommandControl();
		}
		return m_oINSTANCE;
		
	}
	
	

}
