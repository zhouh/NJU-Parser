package edu.nju.nlp.parser.constituent.tss;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;




/**
 * the whole syntax tree structure class
 * the syntax tree is connected by many CFGTreeNode
 * and the CTB bracket tree to syntax tree function is also in the class
 * 
 * @author hao zhou 
 * */
public class CFGTree{

	int root;   // the root node id
	public List<CFGTreeNode> nodes;   //the node list in the tree
	 
	/**
	 * No-argument constructor
	 * Construct a CFGTree with the empty parameter
	 * @return a empty CFGTree with empty nodes
	 * */
	public CFGTree(){
		root=-1;
		nodes=new ArrayList<CFGTreeNode>();
	}
	
	/**
	 * Copy Constructor
	 * return a CFGTree with the same nodes with the input CFGTree
	 * 
	 * @param forclone the tree to be copied
	 * @return a tree have the same nodes with input tree, but the root is -1
	 * 
	 * */
	public CFGTree(CFGTree forclone){
		root=-1;
//		nodes=(ArrayList<CFGTreeNode>)((ArrayList<CFGTreeNode>)forclone.nodes).clone();
		nodes=new ArrayList<CFGTreeNode>();
		for(int i=0;i<forclone.nodes.size();i++)
			this.nodes.add(new CFGTreeNode(forclone.nodes.get(i)));	/*TODO in beam search the node here may not need copy constructor
			 																because the tree node never delete or change*/ 
	}
	

	/**
	 * construct a unParsed ConstituentLabel and tag sequence from the string
	 * the sequence saved in the nodes array to be the input of the parser
	 * 
	 * @param tokens a sequence of ConstituentLabel and tag pairs split by '_' character
	 * @return a CFGTee only contains ConstituentLabel and tag information of a unParsed sentence
	 * 
	 * */
	public CFGTree(String[] tokens){
		
		root=-1;
		nodes=new ArrayList<CFGTreeNode>();
		
		for(int i=0;i<tokens.length;i++){
			CFGTreeNode node=new CFGTreeNode();

			String[] t=tokens[i].split("_");
			String word=t[0];
			String tag=t[1];
			
			//System.out.println(ConstituentLabel+" "+tag);
			
			node.word=word;
			node.is_constituent=false;
			node.constituent=tag;
			node.token=i;
			
			//System.out.print(node.word+" "+node.constituent);
			
			nodes.add(node);
		}
		
	}
	
	
	
	public void setRoot(int root) {
		this.root = root;
	}

	/**
	 * the function read CTB Node recursively from a list of 
	 * String named tokens consisted of a sentence
	 * a node like ( NP l* ( VP t XXX ) ( NP t XXX ) ), first 
	 * read '(' and the node's label NP and read the left and 
	 * right node recursively. The label l is a indicator for head ConstituentLabel 
	 * '*' means the node is a temporary node, make up by CNF binarization
	 * 
	 * TODO:// set the input bracket tree format much more free 
	 *         instead of keeping a space between every tag or 
	 *         ConstituentLabel or bracket 
	 * @param String tokens split by space from the CTB sentence
	 * @return the node id just read by the function
	 * 
	 * */
	public int CTBReadNote(List<String> tokens){
		
		int node;   // the read node id to be returned
		
		//get the node's name and 
		//keep a space between ConstituentLabel , tag and bracket 
		String s,name;
		s=tokens.get(0);
		
		//the first character of a node must be a left bracket
		assert(!s.equals("("));  
		tokens.remove(0);
		name=tokens.get(0);   //get the tag 
		tokens.remove(0);
		//get s and judge the node is constituent or a ConstituentLabel
		s=tokens.get(0);   
		tokens.remove(0);
		
		assert(s.length()<=2);
		
		int left,right;
		boolean temporary;
		//default temp is false
		temporary=false;
		
		if(s.length()==2){
			assert(s.charAt(1)=='*');
			temporary=true;
		}
		
		/*
		 * if the label is l or r or e, the node is not a leaf node
		 * read recursively is is a binary tree
		 * */
		if(s.charAt(0)=='l'||s.charAt(0)=='r'||s.charAt(0)=='e'){
			left=CTBReadNote(tokens);
			right=CTBReadNote(tokens);
			
			//new a node and return the node's id and set para
			node=newNode();
			nodes.get(node).is_constituent=true;
			nodes.get(node).single_child=false;
			
			//l -head left; r/e -head right
			if(s.charAt(0)=='l') nodes.get(node).head_left=true;
			else nodes.get(node).head_left=false;
			
			nodes.get(node).left_child=left;
			nodes.get(node).right_child=right;
			
			//e -NONE node; l/r labeled node
			if(s.charAt(0)=='e')
				nodes.get(node).constituent="NONE";
			else
				nodes.get(node).constituent=name;
			
			//e - no token ; l/r -has token
			if(s.charAt(0)=='e') nodes.get(node).token=-1;
			else nodes.get(node).token=s.charAt(0)=='l' ? nodes.get(left).token : nodes.get(right).token;
		
			nodes.get(node).temp=temporary;
			
			s=tokens.get(0);
			tokens.remove(0);
			assert(s.equals(")"));
		}
		/*
		 * the node is a unary node
		 * */
		else if(s.charAt(0)=='s'){
			
			left=CTBReadNote(tokens);
			node=newNode();
			CFGTreeNode cNode=nodes.get(node);
			cNode.is_constituent=true;
			cNode.single_child=true;
			cNode.left_child=left;
			cNode.constituent=name;
			cNode.right_child=-1;
			cNode.token=nodes.get(left).token;
			cNode.head_left=false;
			cNode.temp=temporary;
			assert(temporary==false); //single node can't be binarized temp
			
			s=tokens.get(0);
			tokens.remove(0);
			assert(s.equals(")"));
			
		}
		/*
		 * the node is a leaf node
		 * */
		else{
			String token;
			node = newNode();
			CFGTreeNode cNode=nodes.get(node);
			cNode.is_constituent=false;
			cNode.single_child=false;
			cNode.head_left=false;
		    cNode.constituent=name;
				
			token=tokens.get(0);
			tokens.remove(0);
			s=tokens.get(0);
			tokens.remove(0);
			
			while(!s.equals(")")){
				token=token+""+s;
				s=tokens.get(0);
				tokens.remove(0);
				
			}
			cNode.word=token;
			cNode.token=node;
			cNode.left_child=-1;
			cNode.right_child=-1;
		}

		return node;
	}
	
	/**
	 * the function read TCT Node from a list of 
	 * String named tokens consisted of a sentence
	 * 
	 * recursed used
	 * 
	 * */
	public int TCTReadNode(List<String> tokens){
		
		int node;
		String s,tag;
		int left,right;
		
		//取出string之后立刻就从数组中remove掉 做成一种类似流的感觉
		
		s=tokens.get(0);	
		//如果刚开始 就是） 说明只有一个左孩子 或者没有孩子 直接返回-1
		if(s.equals(")")) return -1;	
		
		if(!s.equals("(")) return -2;   //返回-2就是出错
				
		tokens.remove(0);
		assert(s.equals("("));   //第一个字符不是 就报错
		
		tag=tokens.get(0);
		tokens.remove(0);
		//依次读左右孩子 没有孩子会返回-1
		left=TCTReadNode(tokens);
		right=TCTReadNode(tokens);
		node=newNode();
		//如果=-2 说明是叶子结点
		if(left==-2) {
			nodes.get(node).constituent=tag;
			nodes.get(node).word=tokens.get(0); 
			nodes.get(node).is_constituent=false;
			tokens.remove(0);
		}
		//否则 是成分
		else{
			if(right!=-1)  nodes.get(node).single_child=false;
			nodes.get(node).left_child=left;
			nodes.get(node).right_child=right;
			nodes.get(node).constituent=tag;
		}
		
		
		s=tokens.get(0);
		assert(s.equals(")"));   //
		tokens.remove(0);
		
		return node;
	}
	
	
	/**
	 * write the tree use pw from the root node
	 * for the binary tree with head indicator
	 * 
	 * @param pw PrintWriter para 
	 * @author zhouh
	 * */
	public void writeTree(PrintWriter pw) throws IOException{
		
		writeNode(root,pw);
		
	}
	
	/**
	 * write the multi-cross tree without head ConstituentLabel indicator
	 * 
	 * @param PrintWriter para
	 * @author zhouh
	 * 
	 * */
	public void writeCTBTree(PrintWriter pw) throws IOException{
		
		writeFalseCTBNode(root,pw);
		
	}

	/**
	 * for debug 
	 * 
	 * */
	public void writeFalseCTBTree(PrintWriter pw) throws IOException{
		
		writeFalseCTBNode(root,pw);
		
	}
	
	/**
	 * for debug 
	 * 
	 * write the tree by left and right node id
	 * but the id in training module of dp decode is orderless
	 * so the function only be used in the tree is read before training 
	 * 
	 * */
	public void writeFalseCTBNode(int nodeID,PrintWriter pw)throws IOException{
		
        CFGTreeNode node=nodes.get(nodeID);

        if(node.temp){
        	if(node.left_child!=-1) writeFalseCTBNode(node.left_child,pw);
			pw.print(" ");
			if(node.right_child!=-1) writeFalseCTBNode(node.right_child,pw);
        }
        else{
        	pw.print("("+node.constituent+" ");
    		
    		if(node.is_constituent==false){
    			
    			pw.print(node.word+")");
    			return;
    		}
    		else{
    			if(node.left_child!=-1) writeFalseCTBNode(node.left_child,pw);
    			pw.print(" ");
    			if(node.right_child!=-1) writeFalseCTBNode(node.right_child,pw);
    			pw.print(")");
    		}
        }
		
	}
	
//	All the write tree function with node.L or node.R are noted
//	/**
//	 * write the sub tree of root node 
//	 * without l or r or t tag 
//	 * temp node are merged
//	 * 
//	 * @param node the root node of the tree
//	 * @param the PrinterWriter para
//	 * 
//	 * */
//	public static void writeCTBNode(CFGTreeNode node,PrintWriter pw)throws IOException{
//		
//        //CFGTreeNode node=nodes.get(root);
//
//        if(node.temp){
//        	if(node.L!=null) writeCTBNode(node.L,pw);
//			pw.print(" ");
//			if(node.R!=null) writeCTBNode(node.R,pw);
//        }
//        else{
//        	pw.print("("+node.constituent+" ");
//    		
//    		if(node.is_constituent==false){
//    			
//    			pw.print(node.word+")");
//    			return;
//    		}
//    		else{
//    			if(node.L!=null) writeCTBNode(node.L,pw);
//    			pw.print(" ");
//    			if(node.R!=null) writeCTBNode(node.R,pw);
//    			pw.print(")");
//    		}
//        }
//		
//	}
//	
//	/**
//	 * write the sub tree of root node 
//	 * with l or r or t tag 
//	 * temp node are not merged
//	 * 
//	 * @param node the root node of the tree
//	 * @param the PrinterWriter para
//	 * 
//	 * */
//	public static void writeNodeWithTemp(CFGTreeNode node,PrintWriter pw) throws IOException{
//		
//		//CFGTreeNode node=nodes.get(root);
//		
//		
//		//String isTemp=node.temp?"*":"";
//		pw.print("("+node.constituent+" ");
//		
//		if(node.is_constituent==false){
//			
//			pw.print("t "+node.word+" )");
//			return;
//		}
//		else{
//			
//			String childLabel=node.head_left?"l":"r";
//			String isTemp=node.temp?"*":"";
//			
//			pw.print(childLabel+isTemp+" ");
//			
//			if(node.left_child!=-1) writeNodeWithTemp(node.L,pw);
//			pw.print("  ");
//			if(node.right_child!=-1) writeNodeWithTemp(node.R,pw);
//			pw.print(" )");
//		}
//		
//	}
//	
	/**
	 * write the sub tree of root node 
	 * */
	public void writeNode(int root,PrintWriter pw) throws IOException{
	
		CFGTreeNode node=nodes.get(root);
		pw.print("( "+node.constituent+" ");
		
		if(node.is_constituent==false){
			
			pw.print("t "+node.word+" )");
			return;
		}
		else{
			String childLabel=node.head_left?"l":"r";
			String isTemp=node.temp?"*":"";
			
			pw.print(childLabel+isTemp+" ");
			
			if(node.left_child!=-1) writeNode(node.left_child,pw);
			pw.print("  ");
			if(node.right_child!=-1) writeNode(node.right_child,pw);
			pw.print(" )");
		}
		
	}
	public int size(){
		return nodes.size();
	}
	
	/**
	 * create a new node in the CFGTree nodes list
	 * and return the new node index
	 * */
	private int newNode() {
		CFGTreeNode newTree=new CFGTreeNode();
		nodes.add(newTree);	
		return nodes.size()-1;  //返回刚刚新建的node的index
	}
	
	/**
	 * return the String of CFGTree
	 */
	public String toString(){
		String retval="";
		
		retval=getNodeString(nodes.size()-1,retval);
		
		return retval;
	}
	
	public String toBiTreeString(){
		String retval="";
		
		retval=getNodeBiTreeString(nodes.size()-1,retval);
		
		return retval;
	}

	/**
	 * get the binary tree string of a CFGTree node
	 * 
	 * @param i
	 * @param retval
	 * @return
	 */
	private String getNodeBiTreeString(int i, String retval) {

		CFGTreeNode node=nodes.get(i);

		retval+="("+node.constituent+" ";
		
		if(node.is_constituent==false){
			
			retval+=node.word+")";
			return retval;
		}
		else{
			if(node.left_child!=-1) retval=getNodeBiTreeString(node.left_child,retval);
			retval+=" ";
			if(node.right_child!=-1) retval=getNodeBiTreeString(node.right_child,retval);
			retval+=")";
		}
        
        return retval;
	}

	/**
	 * get the bracket string of a CFGTree node
	 * 
	 * @param i
	 * @param retval
	 */
	private String getNodeString(int i, String retval) {

		 CFGTreeNode node=nodes.get(i);

	        if(node.temp){
	        	if(node.left_child!=-1) retval=getNodeString(node.left_child,retval);
				retval+=" ";
				if(node.right_child!=-1) retval=getNodeString(node.right_child,retval);
	        }
	        else{
	        	retval+="("+node.constituent+" ";
	    		
	    		if(node.is_constituent==false){
	    			
	    			retval+=node.word+")";
	    			return retval;
	    		}
	    		else{
	    			if(node.left_child!=-1) retval=getNodeString(node.left_child,retval);
	    			retval+=" ";
	    			if(node.right_child!=-1) retval=getNodeString(node.right_child,retval);
	    			retval+=")";
	    		}
	        }
	        
	        return retval;
	}
}
