package mocent.Monitor.SocketProxy;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
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

public class NioClient implements Serializable{
	//日志记录器
	private static final Logger logger = Logger.getLogger(NioClient.class);
	
	public interface SocketListener {
		public void onAddressUnresolved();
		public void onSocketConnecting();
		public void onSocketError(String error);
		public void onSocketConnectTimeout();
		public void onSocketConnected();
		public void onSocketDisconnected();
		public void onSocketDataReceived(ByteBuffer buf);
		public void onSocketDataSent(byte[] data);
	}
	public static boolean SocketListener;
	private SocketListener _socket_listener = null;
	private SocketChannel _client = null;
	private Selector _selector = null;
	private InetSocketAddress _address = null;
	private List<ByteBuffer> _pending_data = new ArrayList<ByteBuffer>();
	private ByteBuffer _read_buffer = ByteBuffer.allocate(8192);
	private boolean _init_conn_done = false;
	private boolean _is_closed = false;
	private String _host;
	private int _port;
	
	public NioClient(String host, int port, SocketListener listener)
	{
		logger.info("NioClient.java\n[地址:"+host+"端口号:"+port+"]");
		if (listener == null) {
			_socket_listener = new SocketListener()
			{
				@Override
				public void onAddressUnresolved()
				{
					logger.info("NioClient.java\n[onAddressUnresolved]");
				}
				@Override
				public void onSocketConnecting()
				{
					logger.info("NioClient.java\n[onSocketConnecting]");
				}
				@Override
				public void onSocketError(String error)
				{
					logger.info("NioClient.java\n[onSocketError]");
				}
				@Override
				public void onSocketConnectTimeout()
				{
					logger.info("NioClient.java\n[onSocketConnectTimeout]");
				}
				@Override
				public void onSocketConnected()
				{
					logger.info("NioClient.java\n[onSocketConnected]");
				}
				@Override
				public void onSocketDisconnected()
				{
					logger.info("NioClient.java\n[onSocketDisconnected]");
				}
				@Override
				public void onSocketDataReceived(ByteBuffer buf)
				{
					logger.info("NioClient.java\n[onSocketDataReceived]");
				}
				@Override
				public void onSocketDataSent(byte[] data)
				{
					logger.info("NioClient.java\n[onSocketDataSent]");
				}
			};
		} else {
			logger.info("NioClient.java\n[_socket_listener = listener]");
			_socket_listener = listener;
		}
		try {
			logger.info("NioClient.java\n[try new InetScocketAddress]");
			this._address = new InetSocketAddress(host, port);
			_host = host;
			_port = port;
		} catch (Exception e) {
			logger.info("NioClient.java\n[try Error line 96]");
			this._address = null;
			_socket_listener.onAddressUnresolved();
		}
	}
	
	public void connect()
	{
		if (_init_conn_done)
		{
			logger.info("NioClient.java\n[Socket Already connected]");
			return;
		}
		if (_address.isUnresolved())
		{
			logger.info("NioClient.java\n[_address.isUnresolved]");
			_socket_listener.onAddressUnresolved();
		}
		_is_closed = false;
		logger.info("NioClient.java\n[_is_closed :"+_is_closed+"]");
		try 
		{
			_client = SocketChannel.open();
			logger.info("NioClient.java\n[SocketChannel.open]");
			_client.configureBlocking(false);
			_socket_listener.onSocketConnecting();
			_client.connect(_address);
		} 
		catch (IOException e) 
		{
			logger.info("NioClient.java\n[IOException Error Line 123]");
			e.printStackTrace();
			_socket_listener.onSocketError(e.getLocalizedMessage());
		}
		
		new Thread() {
			public void run() {
				logger.info("NioClient.java\n[Thread is Runing]");
				try {
					_selector = Selector.open();
					_client.register(_selector, SelectionKey.OP_CONNECT);
					int timeout = 5 * 60 * 1000;
					while (!_is_closed)
					{
						if (_selector.select(timeout) > 0) {
							Set<SelectionKey> sk = _selector.selectedKeys();
							Iterator<SelectionKey> it = sk.iterator();
							while (it.hasNext()) {
								SelectionKey key = it.next();
								try
								{
									it.remove();
									if (!key.isValid())
										continue;

									if (key.isConnectable()) 
									{
										handleConnect(key);
										if (((SocketChannel) key.channel()).isConnected())
											timeout = 0;
									}
									if (key.isReadable()) 
									{
										handleRead(key);
									}
									if (key.isWritable()) 
									{
										handleWrite(key);
									}
								}
								catch(Exception ex)
								{
									key.cancel();
								}
							}
						} else if (timeout > 0) {
							_socket_listener.onSocketConnectTimeout();
							break;
						}
					}
				} 
				catch (IOException e) 
				{
					logger.info("NioClient.java\n[IOException Line 162]");
					e.printStackTrace();
					_socket_listener.onSocketError(e.getLocalizedMessage());
				} 
				catch (CancelledKeyException e) 
				{
					logger.info("NioClient.java\n[CancelledKeyException Line 166]");
					e.printStackTrace();
					// mSocketListener.onSocketError(e.getMessage());
				} 
				catch (Exception e) 
				{
					logger.info("NioClient.java\n[Exception Line 170]");
					e.printStackTrace();
				} 
				finally 
				{
					logger.info("NioClient.java\n[Arrive finally]");
					_init_conn_done = false;
					try 
					{
						_selector.close();
						if (!_is_closed) 
						{
							_is_closed = true;
							_client.close();
						}
					} 
					catch (IOException e) 
					{
						logger.info("NioClient.java\n[IOException Line 182]");
						_socket_listener.onSocketError(e.getLocalizedMessage());
						e.printStackTrace();
					} 
					catch (Exception e) 
					{
						logger.info("NioClient.java\n[Exception Line 186]");
						_socket_listener.onSocketError(e.getLocalizedMessage());
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	//socket连接处理
	protected void handleConnect(SelectionKey key) throws ClosedChannelException, IOException
	{
		logger.info("NioClient.java\n[handleConnect]");
		SocketChannel sc = (SocketChannel) key.channel();
		if (sc.finishConnect()) 
		{
			key.interestOps(SelectionKey.OP_READ);
			_init_conn_done = true;
			_socket_listener.onSocketConnected();
		}
	}
	//socket读处理
	protected void handleRead(SelectionKey key) throws IOException
	{
		logger.info("NioClient.java\n[handleRead]");
		SocketChannel sc = (SocketChannel) key.channel();
		_read_buffer.clear();
		int numRead = sc.read(this._read_buffer);
		if (numRead == -1) {
			key.cancel();
			_socket_listener.onSocketError("Socket closed by remote peer! ");
		} else if (numRead > 0) {
			_read_buffer.flip();
			_socket_listener.onSocketDataReceived(_read_buffer);
		}
		if (key.isValid() && sc.isOpen()) {
			synchronized (this._pending_data) {
				if (_pending_data.isEmpty()) {
					key.interestOps(SelectionKey.OP_READ);
				} else {
					key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
				}
			}
		}
	}
	//socket写处理
	protected void handleWrite(SelectionKey key) throws IOException
	{
		logger.info("handleWrite");
		SocketChannel sc = (SocketChannel) key.channel();
		synchronized (this._pending_data) {
			// Write until there's not more data ...
			while (!_pending_data.isEmpty()) {
				ByteBuffer buf = (ByteBuffer) _pending_data.get(0);
				sc.write(buf);
				if (buf.remaining() > 0) {
					// ... or the socket's buffer fills up
					break;
				} else {
					_socket_listener.onSocketDataSent(buf.array());
					_pending_data.remove(0);
				}
			}
			if (sc.isOpen()) {
				if (_pending_data.isEmpty()) {
					key.interestOps(SelectionKey.OP_READ);
				} else {
					key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
				}
			}
		}
	}
	
	public void sendData(byte[] data)
	{
		logger.info("sendData");
		// And queue the data we want written
		if (_client != null && _client.isOpen()) {
			synchronized (this._pending_data) {
				_pending_data.add(ByteBuffer.wrap(data));
				try {
					_client.register(_selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);

					_selector.wakeup();
				} catch (ClosedChannelException e) {
					_socket_listener.onSocketError(e.getLocalizedMessage());
				}
			}
		}
	}
	public String get_host() {
		/*
		 * String hostname = ""; if (_address != null) hostname =
		 * _address.getHostName();
		 * 
		 * return hostname;
		 */
		return _host;
	}
	public int get_port() {
		/*
		 * int port = 0; if (_address != null) port = _address.getPort();
		 * 
		 * return port;
		 */
		return _port;
	}

	public boolean isConnected()
	{
		logger.info("isConnected");
		return (_init_conn_done && _client != null && _client.isConnected());
	}

	public void disconnect()
	{
		logger.info("disconnect");
		try {
			close();
			_socket_listener.onSocketDisconnected();
		} catch (IOException e) {
			logger.info("IOException");
			_socket_listener.onSocketError(e.getLocalizedMessage());
		}
	}
	private void close() throws IOException
	{
		logger.info("close");
		if (!_is_closed) {
			_is_closed = true;
			_client.close();

			_init_conn_done = false;
			if (_selector != null) {
				_selector.wakeup();
			}
		}
	}
	public InetSocketAddress getRemoteAddress() {
		logger.info("getRemoteAddress");
		if (_client != null)
			return (InetSocketAddress) (_client.socket().getRemoteSocketAddress());
		return null;
	}
	public InetSocketAddress getLocalAddress() {
		logger.info("getLocalAddress");
		if (_client != null)
			return (InetSocketAddress) (_client.socket().getLocalSocketAddress());
		return null;
	}
}