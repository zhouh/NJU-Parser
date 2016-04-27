package edu.nju.nlp.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class aa {

	/**
	 * @param args2B 41 4F C0
	 */
	public static void main(String[] args) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte)0X2B;
		bytes[1] = (byte)0X41;
		bytes[2] = (byte)0X4F;
		bytes[3] = (byte)0XC0;
		
		ByteBuffer buffer = ByteBuffer.allocateDirect(4);
		buffer.put(bytes);
		buffer.rewind();
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		System.out.println(buffer.getFloat());
	}

}
