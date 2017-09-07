package mocent.Monitor.Mina;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * @Description: 解码工具类
 * @author 周磊
 * @date 2016-06-14
 *
 */
public class HDecoder extends CumulativeProtocolDecoder {
	private final Charset charset;

	public HDecoder(Charset charset) {
		this.charset = charset;
	}

	public boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		
		//System.out.println("-------doDecode----------");
		
		CharsetDecoder cd = charset.newDecoder();
		String mes = in.getString(cd);
		out.write(mes);
		return true;
	}
}
