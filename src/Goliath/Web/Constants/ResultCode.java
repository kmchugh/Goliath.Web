/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Goliath.Web.Constants;

/**
 *
 * @author kenmchugh
 */
public class ResultCode extends Goliath.DynamicEnum
{
    /**
     * Gets the result code by numeric value
     * @param tnValue the numeric value of the result code to get
     * @return the result code
     */
    public static ResultCode getResultCode(int tnValue)
    {
        for(ResultCode loCode: getEnumerations(ResultCode.class))
        {
            if (loCode.getCode() == tnValue)
            {
                return loCode;
            }
        }
        return null;
    }

    private int m_nValue;
    /**
     * Creates a new instance of a RequestMethods Object
     *
     * @param tcValue The value for the string format type
     * @throws Goliath.Exceptions.InvalidParameterException
     */
    public ResultCode(int tnValue, String tcValue)
        throws Goliath.Exceptions.InvalidParameterException
    {
        super(tcValue);
        m_nValue = tnValue;
    }

    public int getCode()
    {
        return m_nValue;
    }

    private static ResultCode g_oContinue;
    private static ResultCode g_oSwitchingProtocols;

    private static ResultCode g_oOkay;
    private static ResultCode g_oCreated;
    private static ResultCode g_oAccepted;
    private static ResultCode g_oNonAuthoritativeInfo;
    private static ResultCode g_oNoContent;
    private static ResultCode g_oResetContent;
    private static ResultCode g_oPartialContent;
    private static ResultCode g_oMultiStatus;

    private static ResultCode g_oMultipleChoices;
    private static ResultCode g_oMovedPermanently;
    private static ResultCode g_oFound;
    private static ResultCode g_oSeeOther;
    private static ResultCode g_oNotModified;
    private static ResultCode g_oUseProxy;
    private static ResultCode g_oUnused;
    private static ResultCode g_oTemporaryRedirect;

    private static ResultCode g_oBadRequest;
    private static ResultCode g_oUnauthorized;
    private static ResultCode g_oPaymentRequired;
    private static ResultCode g_oForbidden;
    private static ResultCode g_oNotFound;
    private static ResultCode g_oMethodNotAllowed;
    private static ResultCode g_oNotAcceptable;
    private static ResultCode g_oProxyAuthenticationRequired;
    private static ResultCode g_oRequestTimeout;
    private static ResultCode g_oConflict;
    private static ResultCode g_oGone;
    private static ResultCode g_oLengthRequired;
    private static ResultCode g_oPreconditionFailed;
    private static ResultCode g_oRequestEntityTooLarge;
    private static ResultCode g_oRequestURITooLong;
    private static ResultCode g_oUnsupportedMediaType;
    private static ResultCode g_oRequestedRangeNotSatisfiable;
    private static ResultCode g_oExpectationFailed;

    private static ResultCode g_oInternalServerError;
    private static ResultCode g_oNotImplemented;
    private static ResultCode g_oBadGateway;
    private static ResultCode g_oServiceUnavailable;
    private static ResultCode g_oGateWayTimeout;
    private static ResultCode g_oHTTPVersionNotSupported;


    public static ResultCode CONTINUE()
    {
        if (g_oContinue == null)
        {
            try
            {
                g_oContinue = new ResultCode(100, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oContinue;
    }

    public static ResultCode SWITCHING_PROTOCOLS()
    {
        if (g_oSwitchingProtocols == null)
        {
            try
            {
                g_oSwitchingProtocols = new ResultCode(101, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oSwitchingProtocols;
    }
    
    public static ResultCode OK()
    {
        if (g_oOkay == null)
        {
            try
            {
                g_oOkay = new ResultCode(200, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oOkay;
    }

    public static ResultCode CREATED()
    {
        if (g_oCreated == null)
        {
            try
            {
                g_oCreated = new ResultCode(201, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oCreated;
    }

    public static ResultCode ACCEPTED()
    {
        if (g_oAccepted == null)
        {
            try
            {
                g_oAccepted = new ResultCode(202, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oAccepted;
    }

    public static ResultCode NON_AUTHORITATIVE_INFORMATION()
    {
        if (g_oNonAuthoritativeInfo == null)
        {
            try
            {
                g_oNonAuthoritativeInfo = new ResultCode(203, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oNonAuthoritativeInfo;
    }

    public static ResultCode NO_CONTENT()
    {
        if (g_oNoContent == null)
        {
            try
            {
                g_oNoContent = new ResultCode(204, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oNoContent;
    }

    public static ResultCode RESET_CONTENT()
    {
        if (g_oResetContent == null)
        {
            try
            {
                g_oResetContent = new ResultCode(205, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oResetContent;
    }

    public static ResultCode PARTIAL_CONTENT()
    {
        if (g_oPartialContent == null)
        {
            try
            {
                g_oPartialContent = new ResultCode(206, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oPartialContent;
    }

    public static ResultCode MULTI_STATUS()
    {
        if (g_oMultiStatus == null)
        {
            try
            {
                g_oMultiStatus = new ResultCode(207, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oMultiStatus;
    }

    public static ResultCode MULTIPLE_CHOICES()
    {
        if (g_oMultipleChoices == null)
        {
            try
            {
                g_oMultipleChoices = new ResultCode(300, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oMultipleChoices;
    }

    public static ResultCode MOVED_PERMANENTLY()
    {
        if (g_oMovedPermanently == null)
        {
            try
            {
                g_oMovedPermanently = new ResultCode(301, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oMovedPermanently;
    }

    public static ResultCode FOUND()
    {
        if (g_oFound == null)
        {
            try
            {
                g_oFound = new ResultCode(301, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oFound;
    }

    public static ResultCode SEE_OTHER()
    {
        if (g_oSeeOther == null)
        {
            try
            {
                g_oSeeOther = new ResultCode(303, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oSeeOther;
    }

    public static ResultCode NOT_MODIFIED()
    {
        if (g_oNotModified == null)
        {
            try
            {
                g_oNotModified = new ResultCode(304, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oNotModified;
    }

    public static ResultCode USE_PROXY()
    {
        if (g_oUseProxy == null)
        {
            try
            {
                g_oUseProxy = new ResultCode(305, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oUseProxy;
    }

    public static ResultCode UNUSED()
    {
        if (g_oUnused == null)
        {
            try
            {
                g_oUnused = new ResultCode(306, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oUnused;
    }

    public static ResultCode TEMPORARY_REDIRECT()
    {
        if (g_oTemporaryRedirect == null)
        {
            try
            {
                g_oTemporaryRedirect = new ResultCode(307, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oTemporaryRedirect;
    }

    public static ResultCode BAD_REQUEST()
    {
        if (g_oBadRequest == null)
        {
            try
            {
                g_oBadRequest = new ResultCode(400, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oBadRequest;
    }

    public static ResultCode UNAUTHORIZED()
    {
        if (g_oUnauthorized == null)
        {
            try
            {
                g_oUnauthorized = new ResultCode(401, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oUnauthorized;
    }

    public static ResultCode PAYMENT_REQUIRED()
    {
        if (g_oPaymentRequired == null)
        {
            try
            {
                g_oPaymentRequired = new ResultCode(402, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oPaymentRequired;
    }

    public static ResultCode FORBIDDEN()
    {
        if (g_oForbidden == null)
        {
            try
            {
                g_oForbidden = new ResultCode(403, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oForbidden;
    }

    public static ResultCode NOT_FOUND()
    {
        if (g_oNotFound == null)
        {
            try
            {
                g_oNotFound = new ResultCode(404, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oNotFound;
    }

    public static ResultCode METHOD_NOT_ALLOWED()
    {
        if (g_oMethodNotAllowed == null)
        {
            try
            {
                g_oMethodNotAllowed = new ResultCode(405, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oMethodNotAllowed;
    }

    public static ResultCode NOT_ACCEPTABLE()
    {
        if (g_oNotAcceptable == null)
        {
            try
            {
                g_oNotAcceptable = new ResultCode(406, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oNotAcceptable;
    }

    public static ResultCode PROXY_AUTHENTICATION_REQUIRED()
    {
        if (g_oProxyAuthenticationRequired == null)
        {
            try
            {
                g_oProxyAuthenticationRequired = new ResultCode(407, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oProxyAuthenticationRequired;
    }

    public static ResultCode REQUEST_TIMEOUT()
    {
        if (g_oRequestTimeout == null)
        {
            try
            {
                g_oRequestTimeout = new ResultCode(408, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oRequestTimeout;
    }

    public static ResultCode CONFLICT()
    {
        if (g_oConflict == null)
        {
            try
            {
                g_oConflict = new ResultCode(409, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oConflict;
    }

    public static ResultCode GONE()
    {
        if (g_oGone == null)
        {
            try
            {
                g_oGone = new ResultCode(410, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oGone;
    }

    public static ResultCode LENGTH_REQUIRED()
    {
        if (g_oLengthRequired == null)
        {
            try
            {
                g_oLengthRequired = new ResultCode(411, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oLengthRequired;
    }

    public static ResultCode PRECONDITION_FAILED()
    {
        if (g_oPreconditionFailed == null)
        {
            try
            {
                g_oPreconditionFailed = new ResultCode(412, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oPreconditionFailed;
    }

    public static ResultCode REQUEST_ENTITY_TOO_LARGE()
    {
        if (g_oRequestEntityTooLarge == null)
        {
            try
            {
                g_oRequestEntityTooLarge = new ResultCode(413, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oRequestEntityTooLarge;
    }

    public static ResultCode REQUEST_URI_TOO_LONG()
    {
        if (g_oRequestURITooLong == null)
        {
            try
            {
                g_oRequestURITooLong = new ResultCode(414, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oRequestURITooLong;
    }

    public static ResultCode UNSUPPORTED_MEDIA_TYPE()
    {
        if (g_oUnsupportedMediaType == null)
        {
            try
            {
                g_oUnsupportedMediaType = new ResultCode(415, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oUnsupportedMediaType;
    }

    public static ResultCode REQUESTED_RANGE_NOT_SATISFIABLE()
    {
        if (g_oRequestedRangeNotSatisfiable == null)
        {
            try
            {
                g_oRequestedRangeNotSatisfiable = new ResultCode(416, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oRequestedRangeNotSatisfiable;
    }

    public static ResultCode EXPECTATION_FAILED()
    {
        if (g_oExpectationFailed == null)
        {
            try
            {
                g_oExpectationFailed = new ResultCode(417, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oExpectationFailed;
    }

    public static ResultCode INTERNAL_SERVER_ERROR()
    {
        if (g_oInternalServerError == null)
        {
            try
            {
                g_oInternalServerError = new ResultCode(500, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oInternalServerError;
    }

    public static ResultCode NOT_IMPLEMENTED()
    {
        if (g_oNotImplemented == null)
        {
            try
            {
                g_oNotImplemented = new ResultCode(501, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oNotImplemented;
    }

    public static ResultCode BAD_GATEWAY()
    {
        if (g_oBadGateway == null)
        {
            try
            {
                g_oBadGateway = new ResultCode(502, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oBadGateway;
    }

    public static ResultCode SERVICE_UNAVAILABLE()
    {
        if (g_oServiceUnavailable == null)
        {
            try
            {
                g_oServiceUnavailable = new ResultCode(503, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oServiceUnavailable;
    }

    public static ResultCode GATEWAY_TIMEOUT()
    {
        if (g_oGateWayTimeout == null)
        {
            try
            {
                g_oGateWayTimeout = new ResultCode(504, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oGateWayTimeout;
    }

    public static ResultCode HTTP_VERSION_NOT_SUPPORTED()
    {
        if (g_oHTTPVersionNotSupported == null)
        {
            try
            {
                g_oHTTPVersionNotSupported = new ResultCode(505, Goliath.DynamicCode.Java.getCallingMethodName(false));
            }
            catch (Goliath.Exceptions.InvalidParameterException ex)
            {}
        }
        return g_oHTTPVersionNotSupported;
    }





}
