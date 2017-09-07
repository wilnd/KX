package mocent.Monitor.Util;

public class ByteUtil {
	/**
	 * 整型转成四个字节的二进制
	 * @param i
	 * @return
	 */
	public static byte[] IntToByteArray(int i) {   
		  byte[] result = new byte[4];   
		  result[0] = (byte)((i >> 24) & 0xFF);
		  result[1] = (byte)((i >> 16) & 0xFF);
		  result[2] = (byte)((i >> 8) & 0xFF); 
		  result[3] = (byte)(i & 0xFF);
		  return result;
	}
	
	/**
	 * 将字节转换成整型
	 * @param src
	 * @return
	 */
	public static int BytesToInt(byte[] src)
	{
		int value = src[0] & 0xFF;
		return value;
	}
	/**
	 * 将4字节数组转成整型
	 * @param src
	 * @param offset
	 * @return
	 */
	public static int ByteArrayToInt(byte[] src, int offset){
		int num1 = src[offset] & 0xFF;
		int num2 = src[offset + 1] & 0xFF;
		int num3 = src[offset + 2] & 0xFF;
		int num4 = src[offset + 3] & 0xFF;
		int value = (num1 << 24) | (num2 << 16) | (num3 << 8) | num4;
		return value;
	}
	
	/**
	 * 将2字节数组转成整型
	 * @param src
	 * @param offset
	 * @return
	 */
	public static int Byte2ArrayToInt(byte[] src, int offset){
		int value = -1;
		value = (int) ((src[offset] & 0xFF<<8)   
	            | ((src[offset+1] & 0xFF))); 
		return value;
	}
}
