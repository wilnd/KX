package mocent.Monitor.SocketProxy;

import java.util.Date;

import mocent.Monitor.Memcached.MemCachedUtils;


public class Test {

	public static void main(String[] args) {
		boolean variable = MemCachedUtils.set("name", "simple",new Date(1000 * 60));
		System.out.println(variable);
        String name = (String)MemCachedUtils.get("name");

        System.out.println(name);
	}
}
