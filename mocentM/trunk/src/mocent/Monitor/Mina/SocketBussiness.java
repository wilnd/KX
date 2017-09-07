package mocent.Monitor.Mina;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

public class SocketBussiness extends Thread{
	
	//日志记录器
    private static final Logger logger = Logger.getLogger(SocketBussiness.class);
	
    private Socket s;  
    private DataOutputStream out;  
    private DataInputStream in;  
    public SocketBussiness() throws IOException {  
    }  
  
  
    public static void main(String[] args) throws Exception {  
    	SocketBussiness c = new SocketBussiness();  
        c.run();
    }  
//  发送对象  
//  ObjectOutputStream oos;  
//  TransferObj obj;  
    public void sendMessage(Socket s) {  
        try {  
              
            //socket传字符串  
            out = new DataOutputStream(s.getOutputStream());  
            byte[] bt="中文\n".getBytes();  
            out.write(bt);  
            out.writeBytes("nafio_date\n");  
            //out.writeUTF("中文\n");//by nafio这么写不行  
              
            //socket传对象  
//          oos = new ObjectOutputStream(s.getOutputStream());  
//          obj=new TransferObj();  
//          obj.setDate("socketDateToMina");  
//          oos.writeObject(obj);  
//          oos.flush();  
              
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
  
    }  
  
    public void receiveMessage(Socket s) {  
        try {  
            in = new DataInputStream(s.getInputStream());  
            System.out.println(new String(in.toString()));
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    @Override
    public void run() {  
        /*while (true)*/ {  
            try {  
                //发送对象  
                //oos.close();  
                s = new Socket("120.25.94.112", 9001);  
                System.out.println("客户端:发送信息");  
                sendMessage(s);  
                System.out.println("发送信息完毕!");  
                //发字符串  
                receiveMessage(s);  
                out.close();  
                //in.close();  
            }  
            catch(Exception e){  
                e.printStackTrace();  
            }  
            finally {  
                try{  
                    if(s!=null)s.close();  //断开连接  
                }catch (IOException e) {e.printStackTrace();}  
            }  
        }  
    }  
}