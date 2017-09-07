package mocent.Monitor.Util;

import  java.util.List;

public class ConvertUtil 
{
	/**
	 * List对象转成字符串
	 * @param stringList
	 * @return
	 * @throws Exception
	 */
	public static String listToString(List<String> stringList, String separator) throws Exception
	{
		StringBuilder result=new StringBuilder();
		try
		{
			if (stringList==null) 
			{
	            return null;
	        }
	        
	        boolean flag=false;
	        for (String string : stringList) 
	        {
	            if (flag) 
	            {
	                result.append(separator);
	            }
	            else 
	            {
	                flag=true;
	            }
	            result.append(string);
	        }
	        
		}
		catch(Exception ex)
		{
			throw ex;
		}
		return result.toString();
	}
}
