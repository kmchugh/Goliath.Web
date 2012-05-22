/*
 * This Context is a fix for browsers that will sometimes browse to the site using /non for non HTML content
 * // TODO: Find out which browser and why they do it
 */

package Goliath.Web.Contexts;

/**
 *
 * @author kenmchugh
 */
public class NonContext  extends Goliath.Web.Contexts.WebUIContext<String>
{
    /** Creates a new instance of collectorContextHandler */
    public NonContext()
    {
    }

    @Override
    protected boolean clearSessionThemes()
    {
        return false;
    }



    @Override
    protected boolean onClearContent()
    {
        return false;
    }

    @Override
    protected boolean onShowMenuBar()
    {
        //return getApplicationInstance().getMenuBar().getVisible();
        return false;
    }

    @Override
    protected boolean onShowStatusBar()
    {
        //return getApplicationInstance().getStatusBar().getVisible();
        return false;
    }



    @Override
    protected String onDoDefaultContext()
    {
        writeResponse("");

        return null;
    }

    @Override
    public String onGetContext()
    {
        return "/non";
    }
}
