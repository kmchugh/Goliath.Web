package Goliath.Interfaces.Web;

// TODO: This should be a different name.  Confusing with Goliath.Commands.Command
public interface ICommand {
	public void setUrl(String s);
	public void execute() throws Exception;
	public String getOutput();

}

