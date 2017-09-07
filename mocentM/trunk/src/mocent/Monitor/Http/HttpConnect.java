package mocent.Monitor.Http;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import mocent.Monitor.Util.ConvertUtil;
/**
 * 发送HTTP请求
 */
public class HttpConnect
{

    /**
     * 连接超时
     */
    private int connTimeout;
    /**
     * 读取超时
     */
    private int readTimeout;
    /**
     * 日志
     */
    private static final Logger logger = Logger.getLogger(HttpConnect.class);

    public HttpConnect()
    {
        this.connTimeout = 10000;
        this.readTimeout = 10000;
    }

    public void setConnTimeOut(int connTimeout)
    {
        if (connTimeout > 0)
        {
            this.connTimeout = connTimeout;
        }
    }

    public void setReadTimeOut(int readTimeout)
    {
        if (readTimeout > 0)
        {
            this.readTimeout = readTimeout;
        }
    }

    /**
     * 发送GET请求
     * @param urlString URL地址
     * @return 响应对象
     * @throws Exception
     */
    public HttpResponse sendGet(String urlString) throws Exception
    {
        return this.send(urlString, "GET", null, null);
    }

    /**
     * 发送GET请求
     * @param urlString URL地址
     * @param params 参数集合
     * @return 响应对象
     * @throws Exception
     */
    public HttpResponse sendGet(String urlString, Map<String, String> params) throws Exception
    {

        return this.send(urlString, "GET", params, null);

    }

    /**
     * 发送GET请求
     * @param urlString URL地址
     * @param params 参数集合
     * @param propertys 请求属性
     * @return 响应对象
     * @throws Exception
     */
    public HttpResponse sendGet(String urlString, Map<String, String> params, Map<String, String> headParams) throws Exception
    {
        return this.send(urlString, "GET", params, headParams);
    }

    /**
     * 发送POST请求
     * @param urlString URL地址
     * @return 响应对象
     * @throws Exception
     */
    public HttpResponse sendPost(String urlString) throws Exception
    {
        return this.send(urlString, "POST", null, null);
    }

    /**
     * 发送POST请求
     * @param urlString URL地址
     * @param params 参数集合
     * @return 响应对象
     * @throws Exception
     */
    public HttpResponse sendPost(String urlString, Map<String, String> headParams) throws Exception
    {
        return this.send(urlString, "POST", headParams, null);
    }

    /**
     * 发送POST请求
     * @param urlString URL地址
     * @param params 参数集合
     * @param headParams 请求属性
     * @return 响应对象
     * @throws Exception
     */
    public HttpResponse sendPost(String urlString, Map<String, String> params, Map<String, String> headParams) throws Exception
    {
        return this.send(urlString, "POST", params, headParams);
    }

    /**
     * 发送请求
     * @param urlString URL地址
     * @param params 参数集合
     * @param headParams 请求属性
     * @return 响应对象
     * @throws Exception
     */
    public HttpResponse send(String urlString, String method, Map<String, String> params, Map<String, String> headParams) throws Exception
    {
        HttpRequest req = new HttpRequest();
        req.urlString = urlString;
        req.method = method;
        req.params = params;
        req.headParams = headParams;
        return this.send(req);
    }

    /**
     * 发送HTTP请求
     */
    public HttpResponse send(HttpRequest req) throws Exception
    {
        HttpResponse retval = null;
        InputStream in = null;
        HttpURLConnection urlConn = null;

        try
        {
            String urlString = HttpUtil.ensureSendUrl(req);

            URL url = new URL(urlString);
            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setInstanceFollowRedirects(false);
            urlConn.setConnectTimeout(this.connTimeout);
            urlConn.setReadTimeout(this.readTimeout);
            HttpUtil.setConnInfo(req, urlConn);

            retval = this.genResponse(urlConn);
            if (retval.code == 200)
            {
                in = urlConn.getInputStream();
                if (in != null)
                {
                    retval.content = this.getContent(in);
                }
            }
        }
        catch (ConnectException cex)
        {
            String msg = String.format("地址(%s)拒绝连接或连接超时！", req.urlString);
            logger.error(msg, cex);
            throw new Exception(msg);
        }
        catch (Exception ex)
        {
            String msg = String.format("地址(%s)请求异常?", req.urlString);
            logger.error(msg, ex);
            if (urlConn != null)
            {
                retval = this.genResponse(urlConn);
                retval.content = this.getContent(in);
            }
            throw new Exception(msg);
        }
        finally
        {
            if (urlConn != null)
            {
                try
                {
                    urlConn.disconnect();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                    throw ex;
                }
            }
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                    throw ex;
                }
            }
        }
        return retval;
    }

    /**
     * 获取请求内容，二进制表示
     * @param in
     * @return
     * @throws Exception
     */
    protected byte[] getContent(InputStream in) throws Exception
    {
        byte[] retval = null;
        BufferedInputStream bin = null;
        ByteArrayOutputStream bout = null;
        try
        {
            bin = new BufferedInputStream(in);
            bout = new ByteArrayOutputStream();
            int i = -1;
            while ((i = bin.read()) != -1)
            {
                bout.write(i);
            }
            retval = bout.toByteArray();
        }
        finally
        {
            if (bin != null)
            {
                try
                {
                    bin.close();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
            }
            if (bout != null)
            {
                try
                {
                    bout.close();
                }
                catch (Exception ex)
                {
                    logger.error("", ex);
                }
            }
        }
        return retval;
    }

    protected HttpResponse genResponse(HttpURLConnection urlConn) throws Exception
    {
        HttpResponse httpResponser = new HttpResponse();
        // 赋值
        httpResponser.code = urlConn.getResponseCode();
        httpResponser.message = urlConn.getResponseMessage();
        httpResponser.contentType = urlConn.getContentType();
        httpResponser.connectTimeout = urlConn.getConnectTimeout();
        httpResponser.readTimeout = urlConn.getReadTimeout();
        if (HttpResponse.isRedirect(httpResponser.code))
        {
            httpResponser.redirect = urlConn.getHeaderField("Location");
        }
        Map<String, List<String>> headers = urlConn.getHeaderFields();
        Set<String> keys = headers.keySet();
        for (String key : keys)
        {
            httpResponser.headerParams.put(key, ConvertUtil.listToString(headers.get(key), ""));
        }
        return httpResponser;
    }

    public static void main(String[] args)
    {
        try
        {
            Map<String, String> map = new HashMap<String, String>();
            map.put("clientId", "1232");
            HttpResponse rew = new HttpConnect().sendPost("http://192.168.2.101/ngod/services/GetFolderContents/", map, null);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
