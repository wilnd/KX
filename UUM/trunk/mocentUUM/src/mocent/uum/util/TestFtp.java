package mocent.uum.util;

import mocent.uum.util.FtpUtils;

public class TestFtp {

	public static void main(String[] args) {
		boolean a=FtpUtils.connectServer("120.76.250.32", "21", "xiong", "xiong123.", "UTF-8", true);
		System.out.println(a);
	
	/*	String url="E:/image/2016/11/30/1480493256237.jpg";
		System.out.println(url.substring(url.lastIndexOf("/")+1));
		System.out.println(url.substring(0,url.lastIndexOf("/")));*/
	}

}
