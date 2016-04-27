package edu.nju.nlp.lm;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 该类用于加载此表并且按文件的顺序将每个词和整数关联起来
 * 编码是GBK
 * @author chengc
 *
 */
public class LoadVocabulary {
	private final String srcFilePath = ".\\resources\\LanguageModel\\gigaword_xinhua.vocab";
	private HashMap<String, Integer> word2IDMap;
	private ArrayList<String> id2WordList;
	
	static private LoadVocabulary instance = null;
	static private HashMap<String, LoadVocabulary> instanceMap=new HashMap<String,LoadVocabulary>();
	
	public static LoadVocabulary getInstance(String path){
		if(instanceMap.containsKey(path))
			return instanceMap.get(path);
		else{
			try {
				LoadVocabulary tmp=new LoadVocabulary(path);
				instanceMap.put(path, tmp);
				return tmp;
			} catch (IOException e) {
				System.out.println("词表加载失败!");
				e.printStackTrace();
			}
			return instance;
		}
	}
	
	/**
	 * 构造函数，从文件中加载此表，并按词在文件中的函数顺序为各个词编号(从0开始)
	 * @param vocabPath
	 * @throws IOException
	 */
	private LoadVocabulary(String vocabPath) throws IOException{
		String srcPath;
		if(vocabPath == null) srcPath = srcFilePath;
		else srcPath = vocabPath;
		
		InputStreamReader isr = new InputStreamReader(new FileInputStream(srcPath), "gbk");
		BufferedReader reader = new BufferedReader(isr);
		
		id2WordList = new ArrayList<String>();
		word2IDMap = new HashMap<String, Integer>();
		String line;
		int wid = 0;
		while((line = reader.readLine()) != null){
			line = line.trim();
//			System.out.println("line is: "+line);
//			System.out.println("wid is:"+wid);
			id2WordList.add(line);
			word2IDMap.put(line, wid++);
		}
		
		reader.close();
	}
	
	/**
	 * 将一个整数字符串(实际为词在文件中对应的ID的串)转换成其对应的词串
	 * @param wids
	 * @return
	 */
	public String id2Words(int[] wids){
		String result = "";
		for (int i : wids) {
			result += id2WordList.get(i)+" ";
		}
		result = result.substring(0, result.length()-1);
		
		return result;
	}
	
	/**
	 * 根据单词返回其对应的ID
	 * @param word
	 * @return
	 */
	public int idOfWord(String word){
		word = word.toLowerCase();
		return (word2IDMap.get(word)==null)?word2IDMap.get(word):word2IDMap.get("<unk>");
	}
	
	/**
	 * 根据一组词串，返回其各个单词对应的ID的整数数组
	 * 参数词串的单词之间可以由空格或制表符隔开
	 * @param wordString
	 * @return
	 */
	public int[] words2ID(String wordString){
		
		String[] words = wordString.trim().split("[ \\t]+");
		int[] result = new int[words.length];

		for(int i=0; i<words.length; i++){
			if(word2IDMap.containsKey(words[i])){
				result[i] = word2IDMap.get(words[i]);
			} else {
				result[i] = word2IDMap.get("<unk>");
			}
		}
		return result;
	}
	
	/**
	 * 返回词表中含有的词的个数
	 * @return
	 */
	public int getWordCount(){
		return word2IDMap.size();
	}
}
