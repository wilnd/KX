package mocent.Monitor.Http;

import java.net.HttpURLConnection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import mocent.Monitor.Util.StringUtil;

public class HttpUtil
{

    private static final Logger logger = Logger.getLogger(HttpUtil.class);

    /**
     * 纭畾HTTP璇锋眰鍦板潃瀛楃涓诧紝GET璇锋眰闇�瑕佹嫾鎺ユ煡璇㈠瓧绗︿�?
     * @param req
     * @return
     */
    public static String ensureSendUrl(HttpRequest req) throws Exception
    {
        String method = req.method;
        String urlStr = req.urlString;
        Map<String, String> params = req.params;
        // GET鏃讹紝濡傛灉鍙傛暟涓嶄负绌猴紝缂栬緫璇锋眰鍦板潃
        if (method.equalsIgnoreCase("GET") && params != null)
        {
            StringBuilder param = new StringBuilder();
            for (String key : params.keySet())
            {
                if (param.length() == 0)
                {
                    param.append("?");
                }
                else
                {
                    param.append("&");
                }
                //				param.append(key).append("=").append(URLEncoder.encode(params.get(key), "GBK"));
                param.append(key).append("=").append(params.get(key));
            }
            urlStr += param;
        }
        return urlStr;
    }

    public static void setConnInfo(HttpRequest req, HttpURLConnection conn) throws Exception
    {
        String method = req.method;

        conn.setRequestMethod(method);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setUseCaches(false);

        // 璁剧疆HTTP澶撮儴鍙傛暟
        Map<String, String> headParams = req.headParams;
        if (headParams != null)
        {
            for (String key : headParams.keySet())
            {
                conn.addRequestProperty(key, headParams.get(key));
            }
        }

        // 璁剧疆鍙傛暟
        Map<String, String> params = req.params;
        if (method.equalsIgnoreCase("POST") && params != null)
        {
            StringBuilder param = new StringBuilder();
            for (String key : params.keySet())
            {
                if(!StringUtil.isNullOrEmpty(key))
                {
                    param.append(key).append("=").append(params.get(key));
                    param.append("&");
                }
                else
                {
                    param.append(params.get(key));
                }
            }
            String msg = param.toString();
            if(msg.charAt(msg.length() - 1) == '&')
            {
                msg = msg.substring(0, msg.length() - 1);
            }
            
            // 灏嗗唴�?�规坊鍔犲埌HTTP鎶ユ枃涓�?
            conn.getOutputStream().write(msg.getBytes("utf-8")); 
            conn.getOutputStream().flush();
            conn.getOutputStream().close();
        }
    }

    /**
       * Utf8URL缂栫�?
       * @param s
       * @return
       */
    public static String encode(String text, String coding)
    {
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < text.length(); i++)
        {
            char c = text.charAt(i);
            if (c >= 0 && c <= 255)
            {
                result.append(c);
            }
            else
            {
                try
                {
                    byte[] b = Character.toString(c).getBytes("UTF-8");
                    for (int j = 0; j < b.length; j++)
                    {
                        result.append("%");
                        int k = b[j] & 255;
                        result.append(Integer.toHexString(k).toUpperCase());
                    }
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
            }
        }
        return result.toString();
    }

    /**
     * 鍒よ璇锋眰涓篏ET杩樻槸POST
     * 
     * @param req
     * @return
     */
    public static boolean isGet(HttpServletRequest req)
    {
        if (req == null)
        {
            throw new NullPointerException();
        }
        if ("GET".equalsIgnoreCase(req.getMethod()))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
