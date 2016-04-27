package edu.nju.nlp.parser.types;

import edu.nju.nlp.online.types.Alphabet;

public class Word {

	/**
	 * @param args
	 */
	private static Alphabet wordAlphabet=new Alphabet(1000000);
	public static long NONE=999999;
	
	long code;
	
	public Word(Alphabet a){
		wordAlphabet=a;
	}
	
	public Word(String s){
		code=wordAlphabet.lookupIndex(s, true);
	}
	
	public static long load(String s){
		return wordAlphabet.lookupIndex(s, true);
	}
	
	public long getCode(){
		return code;
	}

	public static void setAlphabet(Alphabet alphabet){
		wordAlphabet=alphabet;
	}
	
	public static Alphabet getAlphabet(){
		return wordAlphabet;
	}
}
