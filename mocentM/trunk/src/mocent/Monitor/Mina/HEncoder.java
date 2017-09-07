package mocent.Monitor.Mina;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * @Description: 编码工具类
 * @author 周磊
 * @date 2016-06-14
 *
 */
public class HEncoder implements ProtocolEncoder {
	
	private static final Logger logger = Logger.getLogger(HEncoder.class);
	
	private final Charset charset;

	public HEncoder(Charset charset) {
		this.charset = charset;
	}

	@Override
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		
		CharsetEncoder ce = charset.newEncoder();
		String mes = (String) message;
		IoBuffer buffer = IoBuffer.allocate(100).setAutoExpand(true);
        buffer.putString(mes, ce);
        buffer.flip();
        out.write(buffer);
	}

	@Override
	public void dispose(IoSession session) throws Exception {
		logger.info("Dispose called,session is " + session);
	}
}