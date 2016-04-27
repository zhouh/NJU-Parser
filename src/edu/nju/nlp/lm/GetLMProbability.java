package edu.nju.nlp.lm;

import java.io.IOException;
import java.nio.ByteOrder;

public class GetLMProbability {
	private final int ngram ;
	
	enum MachineStyle{
		NJU,
		MSRA
	};
	
	private MachineStyle machineStyle = MachineStyle.NJU;
	//文件在内存中的映射视图
	private MappedByteBufferList buffer;
	//语言模型的总的阶数
	private int nOrder;
	//共有的单词的数目，实际为1-gram的数目
	private int nVocab;
	//内部节点的大小(bits)
	private int innerNodeSize;
	//内部节点的大小(bytes)
	private int innerNodeBytes;
	//bow 在内部节点中的偏移
	private int bowOffset;
	//Int所占的字节数
	private final int sizeOfInt = Integer.SIZE / Byte.SIZE;
	private final int sizeOfLong = Long.SIZE / Byte.SIZE;
	//每一阶的第一个词在文件中的偏移(以byte为单位)
	private long[] pOffset;
	
	
	//语言模型源文件的默认路径
	private final String srcFilePath = ".\\resources\\LanguageModel\\gigaword_xinhua.5gram.bin";
	
	//单体模式
	static private GetLMProbability instance = null;
	
	static public GetLMProbability getInstance(String lmPath, int n) throws IOException{
		if(instance == null) {
			instance = new GetLMProbability(lmPath,n);
		}
		
		return instance;
	}
	
	/**
	 * 加载文件，构造内存
	 * @param lmPath
	 * @throws IOException 
	 */
	public GetLMProbability(String lmPath,int ngram) throws IOException{
		String srcFile;
		if(lmPath == null){
			srcFile = srcFilePath;
		} else srcFile = lmPath;
		
		this.ngram=ngram;
		
		innerNodeSize = 24;
		innerNodeBytes = innerNodeSize/sizeOfInt;
		
		{
			
			this.buffer = new MappedByteBufferList(srcFile);
			
			/**header
			 ********************
			 *   nonsense(4 bytes)  *
			 ********************
			 *   阶数(4 bytes)  *
			 ********************
			 *     空(4 bytes)  *
			 ********************
			 *1-gram数(4 bytes) *
			 ********************
			 *2-gram数(4 bytes) *
			 ********************
			 *      ......      *
			 ********************
			 *5-gram数(4 bytes) *
			 ********************
			 *     5 * 4 bytes  *
			 ********************
			 */
			buffer.order(ByteOrder.LITTLE_ENDIAN);//小端存储方式
			nVocab = buffer.getInt(3 * sizeOfInt);//number of 1grams
			nOrder = buffer.getInt(1 * sizeOfInt);
			int sizeOfMostHead = 3 * sizeOfInt;
			
			long ngramCountOff = sizeOfMostHead;
			long[] ngramCounts = new long[nOrder];
			for(int i=0; i<nOrder; i++)
				ngramCounts[i] = buffer.getLong(ngramCountOff + (i*sizeOfLong)); 
			pOffset = new long[nOrder];
			pOffset[0] = sizeOfMostHead + ngramCounts.length * sizeOfLong;
			
			for(int i=1; i<nOrder; i++)
				pOffset[i] = pOffset[i-1] + innerNodeSize * (ngramCounts[i-1] + 1);
			
			bowOffset = 4;
			
//			int ID = 618114;
//			int iID = mmf.getInt((int) (pOffset[0]+ID*sizeOfInt*innerNodeBytes));
//			System.out.println("I's ID is:"+iID);
			
			/**
			 **************************************************
			 * 阶数：           初始偏移(十进制)	           初始偏移(十六进制) *
			 * 	1              52                   34        *
			 * 	2           16245940             F7 E4 B4     *
			 * 	3           236377140            E1 6D 43     *
			 * 	4           496486388           01 D9 7C 7F   *
			 * 	5           846585668           03 27 5D F4   *
			 **************************************************
			 */
//			System.out.println("----------------每一阶的初始节点的偏移------------------");
//			for (int i=0; i<pOffset.length; i++) {
//				System.out.println("第"+(i+1)+"阶: "+pOffset[i]);
//			}
//			System.out.println("--------------------------------------------------------");
		}
	}
	
	/**
	 * 计算成句的概率
	 * @param input
	 * @return
	 */
	public float calSentenceProbWith5gram(String input,String vabPath){
		int ids[] = LoadVocabulary.getInstance(vabPath).words2ID(input);
		
		return calProb(ids);
	}
	
	/**
	 * 计算一个短语的条件概率，即P(wn|w1,w2,...,wn-1)
	 * @param input
	 * @return
	 */
	public float calContionalPro(String input,String vabPath){
		int ids[] = LoadVocabulary.getInstance(vabPath).words2ID(input);
		
		return calProb(0, ids.length, ids);
	}
	
	private float calProb(int[] twid){
		float[] xscoreLM;
		if(twid != null && twid.length > 0){
			xscoreLM = new float[twid.length];
			for(int i=0; i<twid.length; i++){
				xscoreLM[i] = calProb(Math.max(i+1-ngram, 0), i+1, twid);
			}
			
			float result = 0.0f;
			for (float f : xscoreLM) {
				result += f;
			}
			
			return result;
		}
		
		return 0.0f;
	}
	
	static int depth = 1;
	private float calProb(int start, int end, int[] wids){
		int order = end - start;
		
		long wIndex = wids[start];
		//该ID不在词表之内
		if(wIndex < 0 || wIndex >= nVocab){
			//若第一个节点不在列表中，则对该词串概率进行平滑，实际上是少算一个词的词串的概率
			return (order==1)?((float)Math.log(0.0001)):(calProb(start+1, end, wids));
		}
		
		/***********************
		 *   word ID 4-bytes   *
		 ***********************
		 * probability(4-bytes)*
		 ***********************
		 *   child(4-bytes)    *   相对于其下一层的第一个位置的偏移量
		 ***********************
		 *	    bow(float)     *   这项其实是相对于上一阶的概率的相对值，比如第一阶为I，则probability项存储的是其概率的对数
		 ***********************   
		 */
		if(order >= 2){
			for(int level=1; level <= order-2; level++){
				//从bigram到order-1-gram
				long lowChildOffset = pOffset[level-1]+sizeOfInt*(innerNodeBytes * wIndex + 2);
				long highChildOffset = pOffset[level-1]+sizeOfInt*(innerNodeBytes*wIndex+innerNodeBytes+2);
				
				wIndex = indexSearch(buffer.getLong(lowChildOffset),
									 buffer.getLong(highChildOffset)-1,
									 wids[start+level],
									 level);
				
				if(wIndex < 0) return calProb(start+1, end, wids);
			}
			
			long currBowOffset = (pOffset[order-2]+sizeOfInt*(innerNodeBytes * wIndex + bowOffset));
			float bowt = buffer.getFloat(currBowOffset);
			
			long currLowChildOffset = pOffset[order-2]+sizeOfInt*(innerNodeBytes * wIndex + 2);
			long currHighChildOffset = pOffset[order-2]+sizeOfInt*(innerNodeBytes*wIndex+innerNodeBytes+2);
			
			wIndex = indexSearch(
						buffer.getLong(currLowChildOffset),
						buffer.getLong(currHighChildOffset)-1,
						wids[start+order-1],
						order-1);

			if(wIndex < 0) return bowt + calProb(start+1, end, wids);
		}
		float finalProb = buffer.getFloat( (pOffset[order-1]+sizeOfInt*(((order<nOrder)?innerNodeBytes:2)*wIndex+1)));
		
		return finalProb;
	}
	
	/**
	 * 根据当前的层数和目标节点可能的最开始节点的偏移(相对于文件初始)和可能的最末偏移来查找某个ID的具体偏移，如果没有这个ID，则返回-1
	 * @param low
	 * @param high
	 * @param wid
	 * @param level
	 * @return
	 */
	private long indexSearch(long low, long high, int wid, int level){
		int nodeWidth = (level == nOrder-1) ? 2 : innerNodeBytes;
		
		while(low <= high){
			long middleID = (low + high)/2;
			
			int remain = buffer.getInt((pOffset[level] + sizeOfInt*nodeWidth * middleID))-wid;
			
			if(remain == 0) {
				return middleID;
			}
			
			if(remain > 0) high = middleID-1;
			else low = middleID+1;
		}
		
		return -1;//	没有找到
	}
}
