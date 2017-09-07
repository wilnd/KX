package mocent.Monitor.Util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil
{
	/**
	 * 判断字符串为null或者为空
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static boolean isNullOrEmpty(String str) throws Exception
	{
		if( null == str)
		{
			return true;
		}
		if(str.length() == 0)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 在字符串的左边填充字节
	 * @param orginStr
	 * @param totalLength
	 * @param c
	 * @return
	 * @throws Exception
	 */
	public static String padLeft(String originStr, int totalLength, char c) throws Exception
	{
		if(originStr.length() == totalLength)
		{
			return originStr;
		}
		else
		{
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < totalLength - originStr.length(); i++)
			{
				sb.append(c);
			}
			sb.append(originStr);
			return sb.toString();
		}
	}
	
	/**
	 * 替换字符串中的空格、回车、换行符、制表符
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
        String dest = "";
        if (str!=null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
	
	public static String Bytes2HexString(byte[] src)
	{
		StringBuilder stringBuilder = new StringBuilder("");   
	    if (src == null || src.length <= 0) {   
	        return null;   
	    }   
	    for (int i = 0; i < src.length; i++) {   
	        int v = src[i] & 0xFF;   
	        String hv = Integer.toHexString(v);   
	        if (hv.length() < 2) {   
	            stringBuilder.append(0);   
	        }   
	        stringBuilder.append(hv);   
	    }   
	    return stringBuilder.toString();
	}
}