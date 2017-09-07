package mocent.Monitor.SocketProxy;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class IoTest {
	 public static void main(String[] args) throws Exception {
	  int i = 4;   
	  byte[] b = intToByteArray1(i);
	  for(byte bb : b) {
	   System.out.print(bb + " ");
	  }
	 }
	 
	 public static byte[] intToByteArray1(int i) {   
	  byte[] result = new byte[4];   
	  result[0] = (byte)((i >> 24) & 0xFF);
	  result[1] = (byte)((i >> 16) & 0xFF);
	  result[2] = (byte)((i >> 8) & 0xFF); 
	  result[3] = (byte)(i & 0xFF);
	  return result;
	 }
	 
	 public static byte[] intToByteArray2(int i) throws Exception {
	  ByteArrayOutputStream buf = new ByteArrayOutputStream();   
	  DataOutputStream out = new DataOutputStream(buf);   
	  out.writeInt(i);   
	  byte[] b = buf.toByteArray();
	  out.close();
	  buf.close();
	  return b;
	 }
	}
