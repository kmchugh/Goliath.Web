/* =========================================================
 * Element.java
 *
 * Author:      kmchugh
 * Created:     16-Feb-2008, 12:23:14
 *
 * Description
 * --------------------------------------------------------
 * General Class Description.
 *
 * Change Log
 * --------------------------------------------------------
 * Init.Date        Ref.            Description
 * --------------------------------------------------------
 *
 * =======================================================*/

package Goliath.Web.Data;

import Goliath.Applications.Application;
import Goliath.Collections.List;
import Goliath.Constants.LogType;
import Goliath.Interfaces.Collections.IPropertySet;
import Goliath.Interfaces.IProperty;
import Goliath.Web.LightHTTPRequest;
import com.sun.net.httpserver.Headers;

/**
 * Class Description.
 * For example:
 * <pre>
 *      Example usage
 * </pre>
 *
 * @see         Related Class
 * @version     1.0 16-Feb-2008
 * @author      kmchugh
**/
public abstract class ActivityLogEntry extends Goliath.Data.DataObjects.UndoableDataObject<ActivityLogEntry>
{
    /*
    private static List<RuleMethod> g_oValidationRules;
    private static List<RuleMethod> getRules()
    {
        if (g_oValidationRules == null)
        {
            g_oValidationRules = new List<RuleMethod>(0);
        }
        return g_oValidationRules;
    }

    private String m_cFile;
    private String m_cHeaders;
    private String m_cHTTPContext;
    private String m_cLocalAddress;
    private String m_cMethod;
    private String m_cPath;
    private String m_cCharSet;
    private String m_cProperties;
    private String m_cProtocol;
    private String m_cQueryString;
    private String m_cReferer;
    private String m_cRemoteAddress;
    private String m_cSession;
    private String m_cURI;
    private String m_cUserAgent;
    private boolean m_lisSessionCookieSet;
    private String m_cUser;

    public ActivityLogEntry()
    {
    }

    @Goliath.Annotations.NotProperty
    public void setRequestInformation(LightHTTPRequest toRequest)
    {
        m_cFile = toRequest.getFile();

        StringBuilder lcTmp = new StringBuilder("|");
        Headers loHeaders = toRequest.getHeaders();
        for (String lcHeader : loHeaders.keySet())
        {
            lcTmp.append(lcHeader);
            lcTmp.append(":=");
            lcTmp.append(Goliath.Utilities.isNull(loHeaders.get(lcHeader), ""));
            lcTmp.append("|");
        }
        m_cHeaders = lcTmp.toString();
        m_cHTTPContext = toRequest.getHttpContext().getPath();
        m_cLocalAddress = toRequest.getLocalAddress().toString();
        m_cMethod = toRequest.getMethod().getValue();
        m_cPath = toRequest.getPath();
        m_cCharSet = toRequest.getPreferredCharSet();


        IPropertySet loProperties = toRequest.getProperties();
        lcTmp = new StringBuilder("|");
        for (IProperty loProperty : loProperties)
        {
            lcTmp.append(loProperty.getName());
            lcTmp.append(":=");
            lcTmp.append(Goliath.Utilities.isNull(loProperty.getValue(), "").toString());
            lcTmp.append("|");
        }
        m_cProperties = lcTmp.toString();
        m_cProtocol = toRequest.getProtocol();
        m_cQueryString = toRequest.getQueryString();
        m_cReferer = toRequest.getReferer();
        m_cRemoteAddress = toRequest.getRemoteAddress().toString();
        m_cSession = toRequest.getSession().getSessionID();

        Application.getInstance().log("Creating Request -- " + m_cFile, LogType.TRACE());

        m_cUser = toRequest.getSession().getUser().getName();

        Application.getInstance().log("Created Request -- " + m_cFile, LogType.TRACE());
        
        m_cURI = toRequest.getURI().toString();
        m_cUserAgent = toRequest.getUserAgent();
        m_lisSessionCookieSet = toRequest.isSessionCookieSet();
    }

    @Override
    public boolean hasGUID()
    {
        return false;
    }

    public boolean getIsSessionCookieSet()
    {
        canReadProperty();
        return m_lisSessionCookieSet;
    }

    public void setIsSessionCookieSet(boolean tlValue)
    {
        canWriteProperty();
        if (m_lisSessionCookieSet != tlValue)
        {
            m_lisSessionCookieSet = tlValue;
            propertyHasChanged();
        }
    }


    

    public String getFile()
    {
        canReadProperty();
        return m_cFile;
    }

    @Goliath.Annotations.MaximumLength(length=50)
    public void setFile(String tcValue)
    {
        canWriteProperty();

        tcValue = Goliath.Utilities.trimToLength(50, tcValue);

        if (tcValue == null)
        {
            tcValue = "";
        }
        if (!m_cFile.equals(tcValue))
        {
            m_cFile = tcValue;
            propertyHasChanged();
        }
    }
    
    public String getHeaders()
    {
        canReadProperty();
        return m_cHeaders;
    }

    @Goliath.Annotations.MaximumLength(length=1000)
    public void setHeaders(String tcValue)
    {
        canWriteProperty();

        tcValue = Goliath.Utilities.trimToLength(1000, tcValue);

        if (tcValue == null)
        {
            tcValue = "";
        }
        if (!m_cHeaders.equals(tcValue))
        {
            m_cHeaders = tcValue;
            propertyHasChanged();
        }
    }
    public String getHTTPContext()
    {
        canReadProperty();
        return m_cHTTPContext;
    }

    @Goliath.Annotations.MaximumLength(length=255)
    public void setHTTPContext(String tcValue)
    {
        canWriteProperty();

        tcValue = Goliath.Utilities.trimToLength(255, tcValue);

        if (tcValue == null)
        {
            tcValue = "";
        }
        if (!m_cHTTPContext.equals(tcValue))
        {
            m_cHTTPContext = tcValue;
            propertyHasChanged();
        }
    }

    public String getLocalAddress()
    {
        canReadProperty();
        return m_cLocalAddress;
    }

    @Goliath.Annotations.MaximumLength(length=50)
    public void setLocalAddress(String tcValue)
    {
        canWriteProperty();

        tcValue = Goliath.Utilities.trimToLength(50, tcValue);

        if (tcValue == null)
        {
            tcValue = "";
        }
        if (!m_cLocalAddress.equals(tcValue))
        {
            m_cLocalAddress = tcValue;
            propertyHasChanged();
        }
    }

    public String getMethod()
    {
        canReadProperty();
        return m_cMethod;
    }

    @Goliath.Annotations.MaximumLength(length=10)
    public void setMethod(String tcValue)
    {
        canWriteProperty();

        tcValue = Goliath.Utilities.trimToLength(10, tcValue);

        if (tcValue == null)
        {
            tcValue = "";
        }
        if (!m_cMethod.equals(tcValue))
        {
            m_cMethod = tcValue;
            propertyHasChanged();
        }
    }

    public String getPath()
    {
        canReadProperty();
        return m_cPath;
    }

    @Goliath.Annotations.MaximumLength(length=255)
    public void setPath(String tcValue)
    {
        canWriteProperty();

        tcValue = Goliath.Utilities.trimToLength(255, tcValue);

        if (tcValue == null)
        {
            tcValue = "";
        }
        if (!m_cPath.equals(tcValue))
        {
            m_cPath = tcValue;
            propertyHasChanged();
        }
    }


    public String getCharSet()
    {
        canReadProperty();
        return m_cCharSet;
    }

    @Goliath.Annotations.MaximumLength(length=25)
    public void setCharSet(String tcValue)
    {
        canWriteProperty();

        tcValue = Goliath.Utilities.trimToLength(25, tcValue);

        if (tcValue == null)
        {
            tcValue = "";
        }
        if (!m_cCharSet.equals(tcValue))
        {
            m_cCharSet = tcValue;
            propertyHasChanged();
        }
    }

    public String getProperties()
    {
        canReadProperty();
        return m_cCharSet;
    }

    @Goliath.Annotations.MaximumLength(length=1000)
    public void setProperties(String tcValue)
    {
        canWriteProperty();

        tcValue = Goliath.Utilities.trimToLength(1000, tcValue);

        if (tcValue == null)
        {
            tcValue = "";
        }
        if (!m_cProperties.equals(tcValue))
        {
            m_cProperties = tcValue;
            propertyHasChanged();
        }
    }

    public String getProtocol()
    {
        canReadProperty();
        return m_cProtocol;
    }

    @Goliath.Annotations.MaximumLength(length=10)
    public void setProtocol(String tcValue)
    {
        canWriteProperty();

        tcValue = Goliath.Utilities.trimToLength(10, tcValue);

        if (tcValue == null)
        {
            tcValue = "";
        }
        if (!m_cProtocol.equals(tcValue))
        {
            m_cProtocol = tcValue;
            propertyHasChanged();
        }
    }

    public String getQueryString()
    {
        canReadProperty();
        return m_cQueryString;
    }

    @Goliath.Annotations.MaximumLength(length=255)
    public void setQueryString(String tcValue)
    {
        canWriteProperty();

        tcValue = Goliath.Utilities.trimToLength(255, tcValue);

        if (tcValue == null)
        {
            tcValue = "";
        }
        if (!m_cQueryString.equals(tcValue))
        {
            m_cQueryString = tcValue;
            propertyHasChanged();
        }
    }

    public String getReferer()
    {
        canReadProperty();
        return m_cReferer;
    }

    @Goliath.Annotations.MaximumLength(length=255)
    public void setReferer(String tcValue)
    {
        canWriteProperty();

        tcValue = Goliath.Utilities.trimToLength(255, tcValue);

        if (tcValue == null)
        {
            tcValue = "";
        }
        if (!m_cReferer.equals(tcValue))
        {
            m_cReferer = tcValue;
            propertyHasChanged();
        }
    }

    public String getRemoteAddress()
    {
        canReadProperty();
        return m_cRemoteAddress;
    }

    @Goliath.Annotations.MaximumLength(length=50)
    public void setRemoteAddress(String tcValue)
    {
        canWriteProperty();

        tcValue = Goliath.Utilities.trimToLength(50, tcValue);

        if (tcValue == null)
        {
            tcValue = "";
        }
        if (!m_cRemoteAddress.equals(tcValue))
        {
            m_cRemoteAddress = tcValue;
            propertyHasChanged();
        }
    }

    public String getSession()
    {
        canReadProperty();
        return m_cSession;
    }

    @Goliath.Annotations.MaximumLength(length=40)
    public void setSession(String tcValue)
    {
        canWriteProperty();

        tcValue = Goliath.Utilities.trimToLength(40, tcValue);

        if (tcValue == null)
        {
            tcValue = "";
        }
        if (!m_cSession.equals(tcValue))
        {
            m_cSession = tcValue;
            propertyHasChanged();
        }
    }

    public String getURI()
    {
        canReadProperty();
        return m_cURI;
    }

    @Goliath.Annotations.MaximumLength(length=255)
    public void setURI(String tcValue)
    {
        canWriteProperty();

        tcValue = Goliath.Utilities.trimToLength(255, tcValue);

        if (tcValue == null)
        {
            tcValue = "";
        }
        if (!m_cURI.equals(tcValue))
        {
            m_cURI = tcValue;
            propertyHasChanged();
        }
    }

    public String getUserAgent()
    {
        canReadProperty();
        return m_cUserAgent;
    }

    @Goliath.Annotations.MaximumLength(length=100)
    public void setUserAgent(String tcValue)
    {
        canWriteProperty();

        tcValue = Goliath.Utilities.trimToLength(100, tcValue);

        if (tcValue == null)
        {
            tcValue = "";
        }
        if (!m_cUserAgent.equals(tcValue))
        {
            m_cUserAgent = tcValue;
            propertyHasChanged();
        }
    }

    public String getUser()
    {
        canReadProperty();
        return m_cUser;
    }

    @Goliath.Annotations.MaximumLength(length=100)
    public void setUser(String tcValue)
    {
        canWriteProperty();

        tcValue = Goliath.Utilities.trimToLength(100, tcValue);

        if (tcValue == null)
        {
            tcValue = "";
        }
        if (!m_cUser.equals(tcValue))
        {
            m_cUser = tcValue;
            propertyHasChanged();
        }
    }


    @Override
    protected void onValidationRulesCreated(ValidationRules toRules)
    {
        for (RuleMethod loMethod : getRules())
        {
            toRules.addRule(loMethod);
        }
    }
     * 
     */

}
