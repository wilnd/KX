package mocent.Monitor.Util;

import java.io.UnsupportedEncodingException;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * 
 * @author Handscm
 *
 */
public class NTV {
	private static final byte NTV_TYPE_INT = 3;
	private static final byte NTV_TYPE_STR = 4;
	private static final byte NTV_TYPE_HASH = 6;

	private Map<String, Object> kv = new HashMap<String, Object>(); /*
																	 * key -->
																	 * value
																	 */

	public static NTV parse(byte[] bytes) {
		ByteBuffer bb = ByteBuffer.wrap(bytes);
		return parse(bb);
	}

	private static NTV parse(ByteBuffer bb) {
		byte nl, head, type;
		int cnt;

		try {
			head = bb.get();
			type = (byte) (head >> 4);
			if (type != NTV_TYPE_HASH)
				return null;

			nl = (byte) (head & 0xf);
			cnt = parseInt_(bb, nl);
			return parseNtv(bb, cnt);
		} catch (BufferUnderflowException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static int parseInt_(ByteBuffer bb, byte len) {

		int ret, i, k, b;

		ret = 0;
		for (k = 0; k < len; k++) {
			b = (int)(bb.get()) & 0xFF;			
			i = b << ((len - k - 1) << 3);
			ret |= i;
		}
		return ret;
	}

	private static byte[] parseBytes(ByteBuffer bb, int len) {
		int i;
		byte[] bytes;

		bytes = new byte[len];
		for (i = 0; i < len; i++) {
			bytes[i] = bb.get();
		}

		return bytes;
	}

	private static NTV parseNtv(ByteBuffer bb, int cnt) {
		NTV ntv;
		int i;

		ntv = new NTV();
		for (i = 0; i < cnt; i++) {
			byte type, nl, head;

			/* parse key */
			byte[] key;
			head = bb.get();
			type = (byte) (head >> 4);
			if (type != (byte) NTV_TYPE_STR)
				return null;
			nl = (byte) (head & 0xf);
			key = parseBytes(bb, parseInt_(bb, nl));

			/* parse value */
			head = bb.get();
			type = (byte) (head >> 4);
			nl = (byte) (head & 0xf);

			String skey = "";
			try {
				skey = new String(key, "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}

			if (type == (byte) NTV_TYPE_INT)
				ntv.setInt(skey, parseInt_(bb, nl));
			else if (type == (byte) NTV_TYPE_STR)
				ntv.setBytes(skey, parseBytes(bb, parseInt_(bb, nl)));
			else if (type == (byte) NTV_TYPE_HASH)
				ntv.setNtv(skey, parseNtv(bb, parseInt_(bb, nl)));
			else
				return null;
		}
		return ntv;
	}

	public byte[] serialize() {
		try {
			ByteBuffer bb = ByteBuffer.allocate(1024);
			return serialize(bb);
		} catch (BufferOverflowException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static boolean serializeInt(ByteBuffer bb, int i) {
		return serializeInt_(bb, i, NTV_TYPE_INT);
	}

	private static boolean serializeInt_(ByteBuffer bb, int i, byte type) {
		byte nl, k, head;

		if ((i & 0xff000000) != 0)
			nl = 4;
		else if ((i & 0x00ff0000) != 0)
			nl = 3;
		else if ((i & 0x0000ff00) != 0)
			nl = 2;
		else
			nl = 1;

		head = (byte) ((type << 4) | nl);
		bb.put(head);

		for (k = 0; k < nl; k++) {
			bb.put((byte) ((i >> ((nl - k - 1) << 3)) & 0xff));
		}
		return true;
	}

	private static boolean serializeBytes(ByteBuffer bb, byte[] bytes) {
		serializeInt_(bb, bytes.length, NTV_TYPE_STR);
		bb.put(bytes);
		return true;
	}

	private byte[] serialize(ByteBuffer bb) {
		serializeInt_(bb, kv.size(), NTV_TYPE_HASH);

		@SuppressWarnings("rawtypes")
		Iterator it = kv.entrySet().iterator();
		while (it.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) it.next();

			String k = (String) entry.getKey();
			byte[] kb = k.getBytes();
			NTV.serializeBytes(bb, kb);

			Object v = entry.getValue();
			if (v instanceof Integer)
				serializeInt(bb, ((Integer) v).intValue());
			else if (v instanceof byte[])
				serializeBytes(bb, (byte[]) v);
			else if (v instanceof NTV)
				((NTV) v).serialize(bb);
			else
				return null;
		}

		return Arrays.copyOfRange(bb.array(), 0, bb.position());
	}

	public boolean setInt(String key, int i) {
		Integer v = Integer.valueOf(i);
		kv.put(key, (Object) v);
		return true;
	}

	public int getInt(String key) {
		Object obj = kv.get(key);
		if (obj == null || !(obj instanceof Integer)) {
			return 0;
		}
		return ((Integer) obj).intValue();
	}

	public boolean setBytes(String key, byte[] bytes) {
		kv.put(key, (Object) bytes);
		return true;
	}

	public byte[] getBytes(String key) {
		Object obj = kv.get(key);
		if (obj == null || !(obj instanceof byte[])) {
			return null;
		}
		return (byte[]) obj;
	}

	/*
	 * public boolean setString(String key, String value) { try { return
	 * setBytes(key, value.getBytes("UTF-8")); } catch
	 * (UnsupportedEncodingException e) { e.printStackTrace(); return false; } }
	 * 
	 * public String getString(String key) { try { return new
	 * String(getBytes(key), "UTF-8"); } catch (UnsupportedEncodingException e)
	 * { return null; } }
	 */
	/**
	 * 默认GBK编码
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean setString(String key, String value){
		return setString(key,value,"GBK");
	}
	
	public boolean setString(String key, String value, String charsetname) {
		try {
			return setBytes(key, value==null?null:value.getBytes(charsetname));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getString(String key, String charsetname) {
		try {
			byte[] bytes = getBytes(key);
			if(bytes!=null){
				return new String(bytes, charsetname);
			}
			return "";
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	public boolean setNtv(String key, NTV ntv) {
		kv.put(key, (Object) ntv);
		return true;
	}

	public NTV getNtv(String key) {
		Object obj = kv.get(key);
		if (obj == null || !(obj instanceof NTV)) {
			return null;
		}
		return (NTV) obj;
	}
}
