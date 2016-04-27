package edu.nju.nlp.commons.dict;

import edu.nju.nlp.main.Parser.Options.languageType;

public class Dictionary {
	
	private static languageType language;
	
	/*
	 * CTB constituent label
	 */
	public static final String[] ctbLabels =
		{  "IP","NP","VP","FRAG",
		"LCP","VCD","CP","QP","CLP","DNP","ADJP","ADVP","PP","DVP",
		"DP","PRN","VRD","UCP","VSB","LST","VNV","VPT","VCP","INTJ"};
	
	/*
	 * pos tag 
	 */
	public static final String[] ssCtbTags = { "AD", "AS", "BA", "CC", "CD", "CS",
		"DEC", "DEG", "DER", "DEV", "DT", "ETC", "FW", "IJ", "JJ", "LB",
		"LC", "M", "MSP", "NN", "NR", "NT", "OD", "ON", "P", "PN", "PU",
		"SB", "SP", "VA", "VC", "VE", "VV" };
	
	public static final String[] ptbLabels={ "S", "SBAR", "SBARQ", "SINV", "SQ", 
		   "ADJP", "ADVP", 
		   "CONJP", 
		   "FRAG", 
		   "INTJ", 
		   "LST", 
		   "NAC", "NP", "NX", 
		   "PP", "PRN", "PRT",
		   "QP", 
		   "RRC", 
		   "UCP",
		   "VP",
		   "WHADJP", "WHADVP", "WHNP", "WHPP",
		   "X"
		};
	
	public static void setLanguageType(languageType lan){
		language=lan;
	}
	public String[] getTags(){
		return ssCtbTags;
	}
	
	public String[] getConLabels(){
		if(language==languageType.Chinese) return ctbLabels;
		else if(language==languageType.English) return ptbLabels;
		else throw new RuntimeException("Unvalid language type!");
	}
}
