package mocent.uum.util;

import java.util.HashMap;
import java.util.Map;

public class ObjectFactory {
	
	/**
	 * 装数据传送到前台
	 * @return
	 */
	public static Map<String,Object>getMap(){
		return new HashMap<String, Object>();
	}
	
	/**
	 * 
	 * @return 分页实体类对象
	 */
	public static Page createPage(){
		return new Page();
	}
}
