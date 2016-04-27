package edu.nju.nlp.lm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 * This class uses a list of MappedByteBuffer for memory mapping of a single file. 
 * It is used when the file to be mapped is larger than int.max_value (around 2GB).
 * Each buffer will be smaller than int.max_value. A uniform interface is maintained.
 * 
 * @author huangsj
 * */
public class MappedByteBufferList {

	MappedByteBuffer[] bufferList;
	long size = (long)Integer.MAX_VALUE/2;
	int pieces;
	long fileSize_global=0;
	
	
	public MappedByteBufferList(String file)
	{
		createBufferList(file, size);
	}
	
	/**
	 * Create a buffer list with a given piece size.
	 * */
	public MappedByteBufferList(String file, long size)
	{
		this.size = size;
		createBufferList(file, this.size);
	}
	
	/**
	 * parameter size specifies the exact size of each memory buffer.
	 * */
	public boolean createBufferList(String file, long size)
	{
		FileChannel tempChannel;
		try {
			tempChannel = new RandomAccessFile(file, "r").getChannel();
			
			long fileSize = tempChannel.size();//the long type decides the largest file that could processed by this program.
			fileSize_global=fileSize;
			
			pieces = (int) (fileSize / size) + 1;
			
			bufferList = new MappedByteBuffer[pieces];
			int i = 0;
			for(;i<pieces -1;i++)
			{
				bufferList[i] = tempChannel.map(FileChannel.MapMode.READ_ONLY, i * size, Math.min(size + 1024, fileSize - i * size ));// read an extra 1024 byte as buffer, but make sure that the buffer does not exceeds the file ends
			}
			bufferList[pieces-1] = tempChannel.map(FileChannel.MapMode.READ_ONLY, i * size, fileSize - i * size);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private int getPieceOfAddress(long address)
	{
		return (int) (address/size);
	}
	
	private int getAddressOnPieceOfAddress(long address)
	{
		return (int) (address%size);
	}
	
	public void order(ByteOrder bo)
	{
		for(MappedByteBuffer mbb : bufferList)
			mbb.order(bo);
	}

	public int getInt(long address) {
		
		return bufferList[getPieceOfAddress(address)].getInt(getAddressOnPieceOfAddress(address));
	}

	public long getLong(long address) {
		return bufferList[getPieceOfAddress(address)].getLong(getAddressOnPieceOfAddress(address));
	}

	public float getFloat(long address) {
		return bufferList[getPieceOfAddress(address)].getFloat(getAddressOnPieceOfAddress(address));
	}
	
	
}
