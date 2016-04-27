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

import edu.nju.nlp.corpusprocess.type.TreeNode;
import edu.nju.nlp.online.types.Alphabet;
import edu.nju.nlp.parser.constituent.tss.CFGTree;
import edu.nju.nlp.parser.constituent.tss.ConParseDataManager;
import edu.nju.nlp.parser.constituent.tss.ConParseInstance;
import edu.nju.nlp.parser.constituent.tss.ConParseLabel;


public class Binary2RawTree {

	
	public Binary2RawTree(){
	}
	
	public void convert(String inputFile,String outputFile) throws IOException{
		BufferedReader brOrigin = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile),"UTF-8"));
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile),"UTF-8"));
		
		ConParseDataManager manager=new ConParseDataManager();
		ConParseInstance[] instances=(ConParseInstance[]) manager.readData(inputFile);
		
		
		for(ConParseInstance instance:instances){
			ConParseLabel label=(ConParseLabel) instance.getLabel();
			CFGTree tree=label.cfgTree;
			tree.writeFalseCTBNode(tree.size()-1,pw);
			pw.println();
			pw.flush();
			
		}
		brOrigin.close();
		pw.close();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
