/* =========================================================
 * HTMLOutputStream.java
 *
 * Author:      kenmchugh
 * Created:     Dec 22, 2009, 11:30:00 AM
 *
 * Description
 * --------------------------------------------------------
 * <Description>
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Web;

import Goliath.Applications.Application;
import Goliath.Interfaces.Web.IHTMLOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author kenmchugh
 */
public class HTMLOutputStream
        extends OutputStream
        implements IHTMLOutputStream
{
    
    // TODO: Optimise this
    
    private OutputStream m_oStream;
    private String m_cCharset;
    private boolean m_lClosed;

    public HTMLOutputStream(OutputStream toStream)
    {
        Goliath.Utilities.checkParameterNotNull("toStream", toStream);
        m_oStream = toStream;
        m_lClosed = false;
    }

    @Override
    public void close()
    {
        try
        {
            if (!m_lClosed)
            {
                m_oStream.flush();
                m_oStream.close();
            }
        }
        catch (IOException ex)
        {
            // We are not logging this because if the stream could not close, that means it was already closed.
            // Because we have to use OutputStream, we are unable to check if the stream is already closed
        }
        finally
        {
            // One way or another, the stream is now closed
            m_lClosed = true;
        }
    }

    @Override
    public void flush() throws IOException
    {
        if (!m_lClosed)
        {
            m_oStream.flush();
        }
    }

    @Override
    public String getCharset()
    {
        if (m_cCharset == null)
        {
            m_cCharset = "utf-8";
        }
        return m_cCharset;
    }

    @Override
    public void setCharset(String tcCharset)
    {
        m_cCharset = tcCharset;
    }

    @Override
    public void write(byte[] taContent)
    {
        write(taContent, false);
    }

    @Override
    public void write(byte[] taContent, boolean tlGZip)
    {
        write(taContent, 0, taContent.length, tlGZip);
    }

    @Override
    public void write(byte[] taContent, int tnOffset, int tnLength)
    {
        write(taContent, tnOffset, tnLength, false);
    }

    @Override
    public void write(byte[] taContent, int tnOffset, int tnLength, boolean tlGZip)
    {
        ByteArrayOutputStream loBAStream = null;
        GZIPOutputStream loGZIPStream = null;
        int lnLength = tnLength;
        try
        {
            if (! m_lClosed)
            {
                if (tlGZip)
                {
                    loBAStream = new ByteArrayOutputStream();
                    loGZIPStream = new GZIPOutputStream(loBAStream);

                    loGZIPStream.write(taContent, tnOffset, tnLength);
                    loGZIPStream.finish();
                    loGZIPStream.flush();
                    taContent = loBAStream.toByteArray();
                    lnLength = taContent.length;
                }

                m_oStream.write(taContent, tnOffset, lnLength);
                m_oStream.flush();
            }
        }
        catch (IOException ex)
        {
            // We are not going to log this as it means that
            // the stream was closed or connection aborted
            Application.getInstance().log(ex);
        }
        catch (Throwable ex)
        {
            Application.getInstance().log(ex);
        }
        finally
        {
            try
            {
                if (loBAStream != null)
                {
                    loBAStream.close();
                }
            }
            catch (Throwable ex)
            {
                Application.getInstance().log(ex);
            }
            try
            {
                if (loGZIPStream != null)
                {
                    loGZIPStream.close();
                }
            }
            catch (Throwable ex)
            {
                Application.getInstance().log(ex);
            }
        }
    }

    @Override
    public void write(String tcContent)
    {
        write(tcContent, false);
    }

    @Override
    public void write(String tcContent, boolean tlGZip)
    {
        if (!Goliath.Utilities.isNullOrEmpty(tcContent))
        {
            try
            {
                write(tcContent.getBytes(getCharset()), tlGZip);
            }
            catch (Throwable ex)
            {
                Application.getInstance().log(ex);
            }
        }
    }

    @Override
    public void write(int i) throws IOException
    {
        m_oStream.write(i);
    }
    
    

}
