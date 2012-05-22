/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Web;

import Goliath.Applications.Application;
import Goliath.Arguments.SingleParameterArguments;
import Goliath.Collections.HashTable;
import Goliath.Collections.List;
import Goliath.Constants.LogType;
import Goliath.Exceptions.InvalidParameterException;
import Goliath.Interfaces.ISession;
import Goliath.Property;
import Goliath.Threading.ThreadJob;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kenmchugh
 */
public class Crawler extends Goliath.Object
{
    // TODO: Add ability to filter crawl results, such as image only, link only, script only, etc

    private boolean m_lLeaveRoot;

    private boolean m_lGetHTML = false;
    private boolean m_lGetChildHTML = false;
    private boolean m_lPaused = false;
    private List<String> m_oThreads = new List<String>();
    private HashTable<String, List<String>> m_oDisallowed = new HashTable<String, List<String>>();
    private int m_nMaxThreads;
    private int m_nMaxURLs = 100;
    private CrawlerLink m_oRootLink;
    private int m_nLevels = 0;

    private java.util.concurrent.ConcurrentLinkedQueue<CrawlerLink> m_oWaitingLinks = new ConcurrentLinkedQueue<CrawlerLink>();

    public Crawler(String toStartURL)
    {
        m_oRootLink = new CrawlerLink(toStartURL);
        m_lLeaveRoot = false;
    }

    public Crawler(String toStartURL, boolean tlLeaveRoot)
    {
        m_oRootLink = new CrawlerLink(toStartURL);
        m_lLeaveRoot = tlLeaveRoot;
    }

    public Crawler(String toStartURL, boolean tlLeaveRoot, boolean tlGetHTML, boolean tlGetChildHTML)
    {
        m_oRootLink = new CrawlerLink(toStartURL);
        m_lLeaveRoot = tlLeaveRoot;
        m_lGetHTML = tlGetHTML;
        m_lGetChildHTML = tlGetChildHTML;
    }

    public Crawler(String toStartURL, boolean tlLeaveRoot, boolean tlGetHTML, boolean tlGetChildHTML, int tnMaxURLs)
    {
        m_oRootLink = new CrawlerLink(toStartURL);
        m_lLeaveRoot = tlLeaveRoot;
        m_lGetHTML = tlGetHTML;
        m_lGetChildHTML = tlGetChildHTML;
        m_nMaxURLs = tnMaxURLs;
    }

    public String getRootURL()
    {
        return m_oRootLink.getURL();
    }

    public CrawlerLink getRootLink()
    {
        return m_oRootLink;
    }

    public void start()
    {
        start(0);
    }

    public void start(int tnLevels)
    {
        m_nLevels = tnLevels;
        // if paused, just continue
        if (m_lPaused)
        {
            m_lPaused = false;
            return;
        }

        m_oWaitingLinks.offer(m_oRootLink);
        m_lPaused = false;
        Goliath.Threading.ThreadJob loThreadJob = new ThreadJob<SingleParameterArguments<ISession>>(new SingleParameterArguments(null))
        {
            @Override
            protected void onRun(SingleParameterArguments<ISession> toCommandArgs)
            {
                try
                {
                    while(!isFinished())
                    {
                        if (!m_lPaused)
                        {
                            CrawlerLink loLink = null;
                            synchronized(m_oThreads)
                            {
                                // Notify that we are actually doing a crawl
                                m_oThreads.add(Thread.currentThread().getName());
                                loLink = m_oWaitingLinks.poll();
                            }
                            // Do the crawling
                            try
                            {
                                if (loLink != null)
                                {
                                    crawl(loLink);
                                    m_lGetHTML = m_lGetChildHTML;
                                }
                            }
                            catch(Throwable ex)
                            {
                                Application.getInstance().log(ex);
                            }
                            finally
                            {
                                synchronized(m_oThreads)
                                {
                                    // Notify that we are finished a crawl
                                    m_oThreads.remove(Thread.currentThread().getName());
                                }
                            }
                        }
                        Goliath.Threading.Thread.sleep(100);
                    }
                }
                catch (Exception toException)
                {
                    Application.getInstance().log(toException);
                }
            }
        };

        for (int i=0; i< getMaxThreads(); i++)
        {
            Goliath.Threading.Thread loThread = new Goliath.Threading.Thread(loThreadJob);
            loThread.setName("WebCrawler_" + Integer.toString(i));
            loThread.start();
        }
    }

    public void pause()
    {
        m_lPaused = true;
    }

    public void stop()
    {
        synchronized(m_oWaitingLinks)
        {
            m_oWaitingLinks.clear();
            m_oThreads.clear();
        }
    }

    public boolean isFinished()
    {
        return m_oWaitingLinks.size() == 0 && m_oThreads.size() == 0;
    }


    private void crawl(CrawlerLink toLink)
    {
        if (toLink == null || toLink.size() >= m_nMaxURLs)
        {
            return;
        }

        Application.getInstance().log("Crawling Link " + toLink.getURL(), LogType.TRACE());

        // Verify the URL
        URL loURL = verifyURL(toLink.getURL());

        // Check if we are allowed to crawl
        if (isCrawlAllowed(loURL))
        {
            // Get the HTML for the current URL
            // Parse all of the links
            for(String lcLink : parseLinks(loURL, toLink.getHTML()))
            {
                CrawlerLink loLink = new CrawlerLink(lcLink);
                if (!m_oRootLink.contains(loLink))
                {
                    toLink.add(loLink);
                    if (toLink.getLevel() < m_nLevels && toLink.getRoot().size() < m_nMaxURLs)
                    {
                        m_oWaitingLinks.offer(loLink);
                    }
                }
            }
            if ((toLink == m_oRootLink && !m_lGetHTML) || (toLink != m_oRootLink && !m_lGetChildHTML))
            {
                toLink.clearHTML();
            }
        }
    }

    private boolean isCrawlAllowed(URL toURL)
    {
        // Check disallowed list

        // Check for robots
        String lcHost = toURL.getHost().toLowerCase();
        List<String> loNoCrawl = m_oDisallowed.get(lcHost);
        if (loNoCrawl == null)
        {
            // Create a disallow list
            loNoCrawl = new List<String>();

            try
            {
                String lcLine = null;
                URL loRobotsFileUrl = new URL("http://" + lcHost + "/robots.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(loRobotsFileUrl.openStream()));
                while ((lcLine = reader.readLine()) != null)
                {

                    if (lcLine.indexOf("Disallow:") == 0)
                    {
                        String lcDisallowPath = lcLine.substring("Disallow:".length());
                        // Check disallow path for comments and remove if present.
                        int lnCommentIndex = lcDisallowPath.indexOf("#");
                        if (lnCommentIndex != - 1)
                        {
                            lcDisallowPath = lcDisallowPath.substring(0, lnCommentIndex);
                        }
                        lcDisallowPath = lcDisallowPath.trim();
                        loNoCrawl.add(lcDisallowPath);
                    }
                }
                m_oDisallowed.put(lcHost, loNoCrawl);
            }
            catch(Throwable ex)
            {
                m_oDisallowed.put(lcHost, loNoCrawl);
                return true;
            }
        }

        String lcFile = toURL.getFile();
        for (String lcDisallow : loNoCrawl)
        {
            if (lcFile.startsWith(lcDisallow))
            {
                return false;
            }
        }
        return true;
    }

    private URL verifyURL(String tcURL)
    {
        try
        {
            return new URL(tcURL);
        }
        catch (Exception ex)
        {
            Application.getInstance().log(ex);
            throw new InvalidParameterException("The URL[" + tcURL + "] parameter is invalid");
        }
    }

    private int getMaxThreads()
    {
        if (m_nMaxThreads == 0)
        {
            int tnMaxThreadsPerProcessor = Application.getInstance().getPropertyHandlerProperty("Application.Settings.WebCrawler.ThreadsPerProcessor", 4);
            m_nMaxThreads = tnMaxThreadsPerProcessor * Runtime.getRuntime().availableProcessors();
        }
        return m_nMaxThreads;
    }

    public List<String> parseLinks(URL toURL, String tcHTML)
    {
        Pattern loLinkPattern = Pattern.compile("(href|src)\\w*=\\w*[\"|'](.*?)[\"|']", Pattern.CASE_INSENSITIVE);
        Matcher loMatches = loLinkPattern.matcher(tcHTML);

        List<String> lcMatches = new List<String>();

        while (loMatches.find())
        {
            String lcLink = loMatches.group(2).trim();

            // Skip empty links.
            if (lcLink.length() < 1 ||
                    lcLink.charAt(0) == '#' ||
                    lcLink.indexOf("mailto:") >= 0 ||
                    lcLink.indexOf("javascript:") >= 0)
            {
              continue;
            }

            // Prefix absolute and relative URLs if necessary.
            if (lcLink.indexOf("://") == -1)
            {
                  // Handle absolute URLs.
                  if (lcLink.charAt(0) == '/')
                  {
                        lcLink = "http://" + toURL.getHost() + lcLink;
                  }
                  else
                  {
                      // Handle relative URLs.
                        String lcFile = toURL.getFile();
                        // TODO: Fix this for HTTPS
                        if (lcFile.indexOf('/') == -1)
                        {
                              lcLink = "http://" + toURL.getHost() + "/" + lcLink;
                        }
                        else
                        {
                             String lcPath = lcFile.substring(0, lcFile.lastIndexOf('/') + 1);
                             lcLink = "http://" + toURL.getHost() + lcPath + lcLink;
                        }
                  }
            }

            // Remove anchors from link.
            int lnIndex = lcLink.indexOf('#');
            if (lnIndex != -1)
            {
              lcLink = lcLink.substring(0, lnIndex);
            }

            URL loVerified = null;
            // Verify link and skip if invalid.
            try
            {
                loVerified = this.verifyURL(lcLink);
            }
            catch (Throwable ex)
            {
                continue;
            }


            if (!m_lLeaveRoot && !toURL.getHost().toLowerCase().equals(loVerified.getHost().toLowerCase()))
            {
              continue;
            }

            lcMatches.add(lcLink);
        }
        return lcMatches;
    }

}
