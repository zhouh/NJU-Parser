package edu.nju.nlp.parser.types;

import edu.nju.nlp.online.types.Alphabet;

public class ConstituentLabel {

	/**
	 * @param args
	 */
	private static Alphabet ConstituentLabelAlphabet=new Alphabet(100000);
	
	long code;
	
	public ConstituentLabel(Alphabet a){
		ConstituentLabelAlphabet=a;
	}
	
	public ConstituentLabel(String s){
		code=ConstituentLabelAlphabet.lookupIndex(s, true);
	}
	
	public static long load(String s){
		return ConstituentLabelAlphabet.lookupIndex(s, true);
	}
	
	public long getCode(){
		return code;
	}
	public static void setAlphabet(Alphabet alphabet){
		ConstituentLabelAlphabet=alphabet;
	}
	
	public static Alphabet getAlphabet(){
		return ConstituentLabelAlphabet;
	}

}
