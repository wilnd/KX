package mocent.Monitor.Http;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * HTTP请求
 */
public class HttpRequest
{

    /**
     * 目标地址
     */
    public String urlString;
    /**
     * 方法
     */
    public String method;
    /**
     * 参数
     */
    public Map<String, String> params;
    /**
     * 头部参数
     */
    public Map<String, String> headParams;

    public HttpRequest()
    {
        params = new LinkedHashMap<String, String>();
        headParams = new LinkedHashMap<String, String>();
    }

    public HttpRequest(String url, String method)
    {
        this.urlString = url;
        this.method = method;
        params = new LinkedHashMap<String, String>();
        headParams = new LinkedHashMap<String, String>();
    }
}
