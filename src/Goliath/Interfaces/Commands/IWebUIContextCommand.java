/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Interfaces.Commands;

import Goliath.Arguments.Arguments;
import Goliath.Interfaces.UI.Controls.IControl;

/**
 *
 * @author kenmchugh
 */
public interface IWebUIContextCommand<A extends Arguments, T> extends IWebContextCommand<A, T>
{
    void addContent(IControl toControl);
    boolean getShowMenuBar();
    boolean getShowStatusBar();
    boolean getClearContent();
}
