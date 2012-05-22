/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Interfaces.Web;

import java.io.Closeable;
import java.io.Flushable;

/**
 *
 * @author kenmchugh
 */
public interface IHTMLOutputStream
        extends Closeable, Flushable
        
{
    void write(String tcResponse);
    void write(byte[] taResponse);
    public void write(byte[] taContent, int tnOffset, int tnLength);

    void write(String tcResponse, boolean tlCompress);
    void write(byte[] taResponse, boolean tlCompress);
    public void write(byte[] taContent, int tnOffset, int tnLength, boolean tlCompress);


    void setCharset(String tcCharset);
    String getCharset();
}
