/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Web;

import Goliath.Applications.Application;
import Goliath.Collections.List;
import Goliath.Constants.LogType;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 *
 * @author kenmchugh
 */
public class CrawlerLink
{
    private String m_cURL;
    private List<CrawlerLink> m_oChildren = new List<CrawlerLink>();
    private CrawlerLink m_oParent = null;
    private String m_cHTML;

    public CrawlerLink(String tcURL)
    {
        m_cURL = tcURL;
    }

    public synchronized void add(CrawlerLink toLink)
    {
        if (!m_oChildren.contains(this))
        {
            m_oChildren.add(toLink);
            toLink.m_oParent = this;
        }
    }

    public void clear()
    {
        for (CrawlerLink loLink : m_oChildren)
        {
            loLink.m_oParent = null;
            loLink.clear();
        }
    }

    public String getHTML()
    {
        if (m_cHTML == null)
        {
            try
            {
                 // Open connection to URL for reading.
                 BufferedReader loReader = new BufferedReader(new InputStreamReader(new URL(m_cURL).openStream()));

                 // Read page into buffer.
                 String lcLine;
                 StringBuffer loBuffer = new StringBuffer();
                 while ((lcLine = loReader.readLine()) != null)
                 {
                     loBuffer.append(lcLine);
                 }
                 m_cHTML = loBuffer.toString();
            }
            catch(Throwable ex)
            {
                Application.getInstance().log("Error in getHTML of Crawler for " + m_cURL, LogType.TRACE());
                Application.getInstance().log(ex);
                m_cHTML = "";
            }
        }
        return m_cHTML;
    }

    public void clearHTML()
    {
        m_cHTML = null;
    }

    public String getURL()
    {
        return m_cURL;
    }

    public synchronized boolean contains(CrawlerLink toLink)
    {
        for (CrawlerLink loLink : m_oChildren)
        {
            if (loLink.contains(toLink))
            {
                return true;
            }
        }
        return m_oChildren.contains(toLink);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final CrawlerLink other = (CrawlerLink) obj;
        if ((this.m_cURL == null) ? (other.m_cURL != null) : !this.m_cURL.equalsIgnoreCase(other.m_cURL))
        {
            return false;
        }
        return true;
    }

    public List<CrawlerLink> getChildren()
    {
        return m_oChildren;
    }

    public CrawlerLink getRoot()
    {
        if (m_oParent == null)
        {
            return this;
        }
        return m_oParent.getRoot();
    }

    public int getLevel()
    {
        if (m_oParent == null)
        {
            return 0;
        }
        return 1 + m_oParent.getLevel();
    }
    
    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 17 * hash + (this.m_cURL != null ? this.m_cURL.hashCode() : 0);
        return hash;
    }

    public int size()
    {
        int lnSize = 0;
        for (CrawlerLink loLink : m_oChildren)
        {
            lnSize+= loLink.size();
        }
        return lnSize + m_oChildren.size();
    }
}
