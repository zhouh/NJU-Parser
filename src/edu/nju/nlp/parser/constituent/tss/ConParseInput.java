package edu.nju.nlp.parser.constituent.tss;

import java.util.ArrayList;
import java.util.List;
import edu.nju.nlp.online.types.Input;
import edu.nju.nlp.parser.types.Tag;
import edu.nju.nlp.parser.types.Word;

public class ConParseInput implements Input {

	public List<String> words;
	public List<String> tags;
	public Word[] words_code;
	public Tag[] tags_code;
	public final int sentLen;
	public boolean withGoldTag;
	
	/**
	 * the constructor 
	 * used in parsing with gold pos-tag
	 * */
	public ConParseInput(List<String> words,List<String> tags){
		if(words.size()!=tags.size())	//the pairs' length must match
			System.err.println("The words and tags pairs do not match!"); 
		this.words=words;
		this.tags=tags;
		this.withGoldTag=true;
		this.sentLen=words.size();
		computeCode();
	}
	
	/**
	 * Constructor
	 * with a CFG tree  
	 * if addGoldTag==false, then tags=null
	 * */
	public ConParseInput(CFGTree cTree, boolean addGoldTag) {
		
		this.withGoldTag=addGoldTag;
		
		if(addGoldTag){
			List<String> wordsList=new ArrayList<String>();
			List<String> tagsList=new ArrayList<String>();
			for(int i=0;i<cTree.nodes.size();i++){
				CFGTreeNode node=cTree.nodes.get(i);
				if(!node.is_constituent) {
					wordsList.add(node.word);
					tagsList.add(node.constituent);
					node.constituent="NONE";
				}
			}
			words=wordsList;
			tags=tagsList;
		}
		else{
			List<String> wordsList=new ArrayList<String>();
			for(int i=0;i<cTree.nodes.size();i++){
				CFGTreeNode node=cTree.nodes.get(i);
				if(node.is_constituent) 
					wordsList.add(node.word);
			}
			words=wordsList;
			tags=null;
		}
		
		this.sentLen=words.size();
		computeCode();
		
	}
	
//	/**
//	 * the constructor 
//	 * used in parsing without gold pos-tag
//	 * */
//	public ConParseInput(List<String> words){
//		this.words=words;
//		this.tags=null;
//		this.withGoldTag=false;
//		this.sentLen=words.size();
//	}
	
	/**
	 * Convert the word and tag's string class to its code class
	 */
	public void computeCode(){
		words_code=new Word[words.size()];
		for(int i=0;i<words_code.length;i++)
			words_code[i]=new Word(words.get(i));
		tags_code=new Tag[tags.size()];
		for(int i=0;i<tags_code.length;i++)
			tags_code[i]=new Tag(tags.get(i));
	}

}
