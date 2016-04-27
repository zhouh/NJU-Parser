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

public class TreeDegreeStatics {

	Alphabet treeChildNum;	//the hash map to store the tree node degree
	int[] childNumFrequent;	//the appearance frequent of the  node degree in the Alphabet treeChildNum
	
	public TreeDegreeStatics(){
		treeChildNum=new Alphabet(100);
		childNumFrequent=new int[100];
		
	}
	
	public void staticsTree(String inputFile,String outputFile) throws IOException{
		BufferedReader brOrigin = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile),"UTF-8"));
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile),"UTF-8"));

		String line=brOrigin.readLine();
		int num=0;
		
		while(line!=null){
			
			System.out.println(++num);
			
			TreeNode treeNode=TreeNode.string2tree(line);
			staticsTreeNode(treeNode);
			
			line=brOrigin.readLine();
		}
		
		for(int tmp_i=0;tmp_i<treeChildNum.size();tmp_i++)
		pw.println(treeChildNum.lookupObject(tmp_i)+" "+childNumFrequent[tmp_i]);
		
		brOrigin.close();
		pw.close();
	}


	private void staticsTreeNode(TreeNode treeNode) {
		
		if(treeNode.children==null||treeNode.children.size()==0)	//no children
			return;
		
		//statics the current node
		int index=treeChildNum.lookupIndex(treeNode.children.size(), true);
		childNumFrequent[index]++;
		
		//statics the children of current node
		for(TreeNode node:treeNode.children)
			staticsTreeNode(node);
	}

	public static void main(String[] args) throws IOException {
		
		String inputFile="./data/train.data.clean.with.auto.pos";
		String outputFile="./data/treeDegree.txt";
		Binary2RawTree b2r=new Binary2RawTree();
		b2r.convert(inputFile, "./data/rawtree.txt");
		
		TreeDegreeStatics degreeStatics=new TreeDegreeStatics();
		degreeStatics.staticsTree("./data/rawtree.txt", outputFile);
	}
}
