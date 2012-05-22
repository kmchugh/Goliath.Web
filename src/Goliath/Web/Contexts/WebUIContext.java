/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Web.Contexts;

import Goliath.Commands.*;
import Goliath.Interfaces.Commands.IWebUIContextCommand;
import Goliath.Interfaces.UI.Controls.IControl;

/**
 *
 * @param <V> 
 * @author kenmchugh
 */
public abstract class WebUIContext<V extends java.lang.Object> extends Goliath.Web.Contexts.WebContextCommand<V>
        implements IWebUIContextCommand<HandleRequestCommandArgs, V>
{


    protected boolean clearSessionThemes()
    {
        return true;
    }
    
    @Override
    public final V onDoExecute() throws Throwable
    {
        return null;
    }
    
    @Override
    public final boolean getShowMenuBar()
    {
        return onShowMenuBar();
    }
    
    @Override
    public final boolean getShowStatusBar()
    {
        return onShowStatusBar();
    }
    
    protected boolean onShowMenuBar()
    {
        return true;
    }
    
    protected boolean onShowStatusBar()
    {
        return true;
    }
    
    @Override
    public final boolean getClearContent()
    {
        return onClearContent();
    }
    
    protected boolean onClearContent()
    {
        return true;
    }

    /*
    protected IRenderFactory getRenderer()
    {
        return (IRenderFactory)Application.getInstance().getRenderFactory();
    }
     *
     */

    /*
    protected UIApplicationInstance getApplicationInstance()
    {
        UIApplicationInstance loInstance = (UIApplicationInstance)Application.getInstance().getApplicationInstance(getResponse().getSession());
        loInstance.setSessionCookieSet(getResponse().isSessionCookieSet());
        
        // First check if there is any browser info.
        IUIApplicationInstance loAppInstance = (IUIApplicationInstance)Application.getInstance().getApplicationInstance(getSession());
        IClientInformation loInfo = loAppInstance.getClientInformation();
        if (loInfo == null)
        {
            loInfo = BrowserInformation.create(getRequest(), loAppInstance);
            loAppInstance.setClientInformation(loInfo);
        }
        
        if (loInfo != null)
        {
            loInstance.getControl().suspendLayout();
            loInstance.getControl().setWidth(loInfo.getClientWidth());
            loInstance.getControl().setHeight(loInfo.getClientHeight());
            loInstance.getControl().resumeLayout();
        }
        
        return loInstance;
    }
     *
     */
    
    @Override
    public void addContent(IControl toControl)
    {
        //getApplicationInstance().getContent().addControl(toControl);
    }
    
}