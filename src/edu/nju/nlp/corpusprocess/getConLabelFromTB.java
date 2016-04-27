 /*
  * @(#){ 类名称 }.java       { 创建时间 }
  *
  * { 某人或某公司具有完全的版权 }
  * { 使用者必须经过许可 }
  */
package edu.nju.nlp.corpusprocess;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;

import edu.nju.nlp.online.types.Alphabet;
import edu.nju.nlp.parser.constituent.tss.CFGTree;
import edu.nju.nlp.parser.constituent.tss.CFGTreeNode;
import edu.nju.nlp.parser.constituent.tss.ConParseDataManager;
import edu.nju.nlp.parser.constituent.tss.ConParseInstance;
import edu.nju.nlp.parser.constituent.tss.ConParseLabel;
 
/**
  * get all the constituent label in the tree bank
  *
  * @author  Hao Zhou
  * @version 0.1, 2013/27/07
  * @see     java.lang.Class
  * @since   JDK1.0
 */
public class getConLabelFromTB {

	public getConLabelFromTB(){
		
	}
	
	public void getLabel(String inputFile,String outputFile) throws IOException{
		BufferedReader brOrigin = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile),"UTF-8"));
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile),"UTF-8"));
		
		ConParseDataManager manager=new ConParseDataManager();
		ConParseInstance[] instances=(ConParseInstance[]) manager.readData(inputFile);
		
		Alphabet labelAlphabet=new Alphabet(1000);
		
		for(ConParseInstance instance:instances){
			ConParseLabel label=(ConParseLabel) instance.getLabel();
			CFGTree tree=label.cfgTree;
			getLabelFromTreeNode(tree.nodes.get(tree.nodes.size()-1),labelAlphabet,tree);
			
		}
		
		String[] labelArray=new String[labelAlphabet.size()];
		for(int i=0;i<labelAlphabet.size();i++)
			labelArray[i]=(String)labelAlphabet.lookupObject(i);
		Arrays.sort(labelArray);
		
		for(int i=0;i<labelArray.length;i++){
			
			String s=(String) labelArray[i];
			pw.println(s);
			pw.flush();
		}
		
		
	}

	 /**
	  *   
	  */
	private void getLabelFromTreeNode(CFGTreeNode node, Alphabet labelAlphabet,CFGTree tree) {
		
		if(!node.is_constituent) return;
		
		if(node.is_constituent) labelAlphabet.lookupIndex(node.constituent.trim(), true);
		
		//recursive for the left child and the right child
		getLabelFromTreeNode(tree.nodes.get(node.left_child),labelAlphabet,tree);
		
		if(!node.single_child)  getLabelFromTreeNode(tree.nodes.get(node.right_child),labelAlphabet,tree);
		
	}
	
	public static void main(String[] args) throws IOException{
		String inFile="./data/train.txt";
		String outFile="./data/conlabel.ctb2";
		
		getConLabelFromTB getLabel=new getConLabelFromTB();
		getLabel.getLabel(inFile, outFile);
	}
	
}
