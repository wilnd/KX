package mocent.Monitor.Memcached;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.danga.MemCached.MemCachedClient;

public class MemCachedUtils {

	private static final Logger logger = Logger.getLogger(MemCachedUtils.class);

	private static MemCachedClient cachedClient;

	static 
	{
		if (cachedClient == null)
		{
			// 括号中的名称要和配置文件memcached-context.xml中的名称一致
			ApplicationContext atx = new ClassPathXmlApplicationContext("/memcached.xml");
			cachedClient = (MemCachedClient)atx.getBean("memCachedClient");
		}
			
	}

	private MemCachedUtils() 
	{
		
	}

	/**
	 * 键值是否存在
	 * @param key
	 * @return
	 */
	public static boolean isExist(String key)
	{
		return cachedClient.keyExists(key);
	}
	/**
	 * 
	 * 向缓存添加新的键值对。如果键已经存在，则之前的值将被替换。
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public static boolean set(String key, Object value) 
	{
		return setExp(key, value, null);
	}

	/**
	 * 向缓存添加新的键值对。如果键已经存在，则之前的值将被替换。
	 * @param key 键
	 * @param value 值
	 * @param expire 过期时间 New Date(1000*10)：十秒后过期
	 * @return
	 */
	public static boolean set(String key, Object value, Date expire) 
	{
		return setExp(key, value, expire);
	}

	/**
	 * 向缓存添加新的键值对。如果键已经存在，则之前的值将被替换。
	 * @param key 键
	 * @param value 值
	 * @param expire 过期时间 New Date(1000*10)：十秒后过期
	 * @return
	 */
	private static boolean setExp(String key, Object value, Date expire) 
	{
		boolean flag = false;
		try 
		{
			flag = cachedClient.set(key, value, expire);
		} 
		catch (Exception e) 
		{
			// 记录Memcached日志
			logger.error("Memcached set方法报错，key值：" + key + "\r\n");
		}
		return flag;
	}

	/**
	 * 仅当缓存中不存在键时，add 命令才会向缓存中添加一个键值对。
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public static boolean add(String key, Object value) 
	{
		return addExp(key, value, null);
	}

	/**
	 * 仅当缓存中不存在键时，add 命令才会向缓存中添加一个键值对。
	 * @param key 键
	 * @param value 值
	 * @param expire 过期时间 New Date(1000*10)：十秒后过期
	 * @return
	 */
	public static boolean add(String key, Object value, Date expire) 
	{
		return addExp(key, value, expire);
	}

	/**
	 * 仅当缓存中不存在键时，add 命令才会向缓存中添加一个键值对。
	 * @param key 键
	 * @param value 值
	 * @param expire 过期时间 New Date(1000*10)：十秒后过期
	 * @return
	 */
	private static boolean addExp(String key, Object value, Date expire) 
	{
		boolean flag = false;
		try 
		{
			flag = cachedClient.add(key, value, expire);
		} 
		catch (Exception e) 
		{
			// 记录Memcached日志
			logger.error("Memcached add方法报错，key值：" + key + "\r\n");
		}
		return flag;
	}

	/**
	 * 仅当键已经存在时，replace 命令才会替换缓存中的键。
	 * @param key 键
	 * @param value 值
	 * @return
	 */
	public static boolean replace(String key, Object value) 
	{
		return replaceExp(key, value, null);
	}

	/**
	 * 仅当键已经存在时，replace 命令才会替换缓存中的键。
	 * @param key 键 
	 * @param value 值
	 * @param expire 过期时间 New Date(1000*10)：十秒后过期
	 * @return
	 */
	public static boolean replace(String key, Object value, Date expire) 
	{
		return replaceExp(key, value, expire);
	}

	/**
	 * 仅当键已经存在时，replace 命令才会替换缓存中的键。 
	 * @param key 键
	 * @param value 值
	 * @param expire 过期时间 New Date(1000*10)：十秒后过期
	 * @return
	 */
	private static boolean replaceExp(String key, Object value, Date expire) 
	{
		boolean flag = false;
		try 
		{
			flag = cachedClient.replace(key, value, expire);
		} 
		catch (Exception e) 
		{
			logger.error("Memcached replace方法报错，key值：" + key + "\r\n");
		}
		return flag;
	}

	/**
	 * get 命令用于检索与之前添加的键值对相关的值。
	 * @param key 键
	 * @return
	 */
	public static Object get(String key)
	{
		Object obj = null;
		try 
		{
			obj = cachedClient.get(key);
		} 
		catch (Exception e) 
		{
			logger.error("Memcached get方法报错，key值：" + key + "\r\n");
		}
		return obj;
	}

	/**
	 * 删除 memcached 中的任何现有值。
	 * @param key 键
	 * @return
	 */
	public static boolean delete(String key) 
	{
		return deleteExp(key, null);
	}

	/**
	 * 删除 memcached 中的任何现有值。
	 * @param key 键
	 * @param expire 过期时间 New Date(1000*10)：十秒后过期
	 * @return
	 */
	public static boolean delete(String key, Date expire) 
	{
		return deleteExp(key, expire);
	}

	/**
	 * 删除 memcached 中的任何现有值。
	 * @param key 键
	 * @param expire 过期时间 New Date(1000*10)：十秒后过期
	 * @return
	 */
	private static boolean deleteExp(String key, Date expire) 
	{
		boolean flag = false;
		try 
		{
			flag = cachedClient.delete(key, expire);
		}
		catch (Exception e) 
		{
			logger.error("Memcached delete方法报错，key值：" + key + "\r\n");
		}
		return flag;
	}

	/**
	 * 清理缓存中的所有键/值对
	 * @return
	 */
	public static boolean flashAll() 
	{
		boolean flag = false;
		try 
		{
			flag = cachedClient.flushAll();
		} 
		catch (Exception e) 
		{
			logger.error("Memcached flashAll方法报错\r\n");
		}
		return flag;
	}
}
