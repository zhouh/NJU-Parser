package edu.nju.nlp.parser.types;

import java.io.Serializable;

import edu.nju.nlp.online.types.Alphabet;

public class Tag implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param args
	 */
	 
	private static Alphabet TagAlphabet=new Alphabet(100000);
	public static long NONE=999;
	
	long code;
	
	public Tag(Alphabet a){
		TagAlphabet=a;
	}
	
	public Tag(String s){
		code=TagAlphabet.lookupIndex(s, true);
		
	}
	
	public static long load(String s){
		return TagAlphabet.lookupIndex(s, true);
	}
	
	public long getCode(){
		return code;
	}
	
	public static void setAlphabet(Alphabet alphabet){
		TagAlphabet=alphabet;
	}
	
	public static Alphabet getAlphabet(){
		return TagAlphabet;
	}
}
