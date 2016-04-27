package edu.nju.nlp.corpusprocess.type;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * This Tree node represents a tree structure.
 * (UCP (VP (VA 古老 )   )   ), spaces between brackets will be ignored.
 * */
public class TreeNode {
	
	public String content;	//label
	public String lexical;	//ConstituentLabel
	public ArrayList<TreeNode> children;
	
	public int srcStart = -1;
	public int srcEnd = -1;
	public int trgStart = -1;
	public int trgEnd = -1;
		
	/**
	 * Constructor of leaf node
	 * */
	public TreeNode(String content, String lexical)
	{
		this.content = content;
		this.lexical = lexical;
		this.children = null;
	}
	
	/**
	 * Constructor of internal node
	 * */
	public TreeNode(String content, ArrayList<TreeNode> children)
	{
		this.content = content;
		this.children = children;
		this.lexical = null;
	}
	
	public TreeNode(TreeNode t)
	{
		this.content = new String(t.content);
		if(lexical!=null)
			this.lexical = new String(t.lexical);
		else
		{
			this.children = new ArrayList<TreeNode>();
			for(int i=0;i<t.children.size();i++)
				this.children.add(new TreeNode(t.children.get(i)));
		}
	}
	
	/**
	 * 
	     * Returns a new string that is a substring of this string. The
	     * substring begins with the character at the specified index and
	     * extends to the end of this string. <p>
	     * Examples:
	     * <blockquote><pre>
	     * "unhappy".substring(2) returns "happy"
	     * "Harbison".substring(3) returns "bison"
	     * "emptiness".substring(9) returns "" (an empty string)
	     * </pre></blockquote>
	     *
	     * @param      beginIndex   the beginning index, inclusive.
	     * @return     the specified substring.
	     * @exception  IndexOutOfBoundsException  if
	     *             <code>beginIndex</code> is negative or larger than the
	     *             length of this <code>String</code> object.
	 */
	public TreeNode treeStandard(TreeNode node){
		
		/*
		 * normalize the children of the node 
		 */
		if(node.children!=null){
			for(int i=0;i<node.children.size();i++){
				
				TreeNode newChildNode=treeStandard(node.children.get(i));	
				if(newChildNode==null) {	//if the node is a -NONE- node
					node.children.remove(i);
					i--;	//after remove ,every node add one 
					continue;
				}
				
				node.children.set(i, newChildNode);
			}
		}
		
		/*
		 * empty terminal nodes removed
		 * (-NONE- XXX) are removed
		 */
		if(node.content.equals("-NONE-")) 
			return null;
		
		/*
		 * non-terminal nodes with no children were removed
		 * (CP ) are removed
		 */
		//===================================================================
		//It would not be captured, I don't know how to solve it
		//===================================================================
		//2013-7-15
		//I find the non-terminal nodes with no child is just parent node with all -NONE node 
		if(node.lexical==null&&(node.children==null||node.children.size()==0))
			return null;
		
		/*
		 * Any layer deeper than one are removed
		 * IP-CP => IP
		 */
		if(node.children!=null){
			
			String[] labelTokens=node.content.split("-");
			node.content=labelTokens[0];
		}
		
		/*
		 * Any unary X->X nodes collapsed into one
		 * (IP (IP XXX)) are collapsed into (IP XXX)
		 */
		if(node.children==null) return node;
		else if(node.children.size()==1 
				&& (node.content.equals(node.children.get(0).content)) )
		node=node.children.get(0);	
			
			
		return node;
	}
	
	/**
	 * 
	 * */
	public static TreeNode string2tree(String str)
	{
		if(str==null)
			return null;
		str = str.trim();
		if(str.length() == 0)
			return null;
		
		//(CLP (NR 常州市) (P 沿)) or (NR 常州市)
		String content = null;
		if(str.startsWith("(")&&str.endsWith(")") &&findingMatchingBracket(str, 0)==(str.length()-1))
		{//straight rule or lexical rule
			str = str.substring(1,str.length()-1);
			str = str.trim();
		}
		else
		{
			System.err.println("Error parsing string: " + str);
			return null;
		}
		
		if(str.startsWith("(")&&str.endsWith(")") &&findingMatchingBracket(str, 0)==(str.length()-1)){
			return string2tree(str);
		}
		
		//CLP (NR 常州市) (P 沿) or NR 常州市
		int firstBracket=str.indexOf("(");	//(NP(CP XXX))
		int firstSpace = str.indexOf(" ");	//(NP (CP XXX))
		int contentBound=firstBracket!=-1?(firstBracket<firstSpace?(firstBracket-1):firstSpace):firstSpace;
		if(firstSpace==-1)
		{
			System.err.println("Error parsing string: " + str);
			return null;
		}
		
		try{
			content = str.substring(0 ,contentBound);
			str = str.substring(contentBound + 1);
			str = str.trim();
		}catch(Exception e){
			System.out.println(str);
		}
		
		
		//(NR 常州市) (P 沿)  or  常州市
		if(!str.contains(" "))
		{//leaf nodes, e.g. 常州市
			TreeNode node = new TreeNode(content, str);
			return node;
		}
		else
		{//internal node: (NR 常州市) (P 沿)
			int childStart = 0;
			int childEnd = 0;
			int length = str.length();
			ArrayList<TreeNode> children = new ArrayList<TreeNode>();
			while(childStart<length&&str.length()>0)
			{
				if(str.charAt(childStart)=='(')
				{
					childEnd = findingMatchingBracket(str, childStart);
					children.add(string2tree(str.substring(childStart, childEnd+1)));
					str=str.substring(childEnd+1).trim();
					childStart =0;//skip the space between two children
				}
				else
				{
					System.err.println("Error parsing sub tree: " + str);
					return null;
				}
			}
			return new TreeNode(content, children);
		}
	}
	
	/**
	 * For a string that start with '(', find the corresponding ')'
	 * 
	 * In this class all brackets are '(' and ')'
	 * */
	public static int findingMatchingBracket(String str, int startIndex)
	{
		char bracketChar = str.charAt(startIndex);
		char reverseBracket = ' ';
		if(bracketChar == '[')
			reverseBracket = ']';
		else if(bracketChar == '<')
			reverseBracket = '>';
		else if(bracketChar == '(')
			reverseBracket = ')';
		else if(bracketChar == '{')
			reverseBracket = '}';
		else
			return -1;
		
		char[] array = str.toCharArray();
		int count = 0;
		int index = startIndex;
		for(index = startIndex;index<array.length;index++)
		{
			if(array[index]==bracketChar)
				count++;
			else if(array[index]==reverseBracket)
				count--;
			else
				continue;
			if(count==0)
				break;
		}
		if(index==array.length)
			return -1;
		return index;
	}
	
	protected static String tree2string(TreeNode node)
	{
		if(node==null)
			return "";
		StringBuilder output =new StringBuilder( "(");
		output.append(node.content);
		if(node.lexical!=null)
		{
			output.append(" ");
			output.append(node.lexical);
		}
		else if(node.children!=null)
		{
			for(int i = 0;i<node.children.size();i++)
			{
				output.append(" ");
				output.append(tree2string(node.children.get(i)));
			}
		}
		output.append(")");
		return output.toString();
	}

	public boolean equals(TreeNode t)
	{
		if(!content.equals(t.content))
			return false;
		if(lexical!=null)//leaf node
			return lexical.equals(t.lexical);
		else//internal node
		{
			if(children.size()!=t.children.size())
				return false;
			for(int i=0;i<children.size();i++)
				if(!children.get(i).equals(t.children.get(i)))
					return false;
		}
		return true;
	}
	
	public String toString()
	{
		return TreeNode.tree2string(this);
	}
	
	public static void standardFile(String inputFile,String outputFile) throws IOException{
		
		BufferedReader brOrigin = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile),"UTF-8"));
		PrintWriter pwOut = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile),"UTF-8"));
		
		String line=brOrigin.readLine();
		
		int num=0;
		
		while(line!=null){
			
			if(num==11468)
				System.out.println(num);
				
			System.out.println(num++);
			
			//delete the outside bracket
//			line = line.trim();
//			line = line.substring(1,line.length()-1);
//			line = line.trim();
			
			while(line.length()!=0){
				int treeBound=findingMatchingBracket(line,0);
				String str=line.substring(0,treeBound+1);	//get the first tree sentence
				
				//print the node
				try{
					TreeNode node = TreeNode.string2tree(str);
					node=node.treeStandard(node);
					pwOut.println(TreeNode.tree2string(node));
					pwOut.flush();
				}catch(Exception e){
					
				}
				
				line=line.substring(treeBound+1);
				line=line.trim();
			}
			
			line=brOrigin.readLine();
		}
		
		brOrigin.close();
		pwOut.close();
	}
	
	public static void deleteTopNode(String inputFile,String outputFile) throws IOException{
		
		BufferedReader brOrigin = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile),"UTF-8"));
		PrintWriter pwOut = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile),"UTF-8"));
		
		String line=brOrigin.readLine();
		
		while(line!=null){
			
			TreeNode node=string2tree(line);
			
			if(node.children.size()!=1) 
				throw new RuntimeException("the first node is not TOP!");
			
			pwOut.println(tree2string(node.children.get(0)));
			
			line=brOrigin.readLine();
		}
		
		brOrigin.close();
		pwOut.close();
		
	}
	
	public static void main(String[] args) throws IOException
	{
//		String treeLine = "( (IP (NP-SBJ (CP (WHNP-4 (-NONE- *OP*))(CP (IP (NP-SBJ (-NONE- *T*-4))(VP (LCP-TMP (NP (NT 去年))(LC 初))(NP-LOC (NP-PN (NR 浦东))(NP (NN 新区)))(VP (VV 诞生))))(DEC 的)))(NP-PN (NR 中国))(QP (OD 第一)(CLP (M 家)))(NP (NN 医疗)(NN 机构)(NN 药品)(NN 采购)(NN 服务)(NN 中心)))(PU ，)(VP (PP(ADVP (AD 正))(PP (P 因为)(IP (NP-SBJ (-NONE- *pro*))(VP (VP (ADVP (AD 一))(VP (VV 开始)))(VP (ADVP (AD 就))(ADVP (AD 比较))(VP (VA 规范)))))))(PU ，)(IP-ADV (IP-SBJ (NP-SBJ (-NONE- *pro*))(VP (VV 运转)))(VP (VV 至今)))(PU ，)(VP (VP (VV 成交)(NP-OBJ (NN 药品))(QP-EXT (CD 一亿多)(CLP (M 元))))(PU ，)(VP (VV 没有)(VP (VV 发现)(NP-OBJ (QP (CD 一)(CLP (M 例)))(NP (NN 回扣)))))))(PU 。)) )";
//		TreeNode node = TreeNode.string2tree(treeLine);
//		System.out.println(TreeNode.tree2string(node));
//		TreeNode newNode= node.treeStandard(node);
//		System.out.println(TreeNode.tree2string(newNode));
		
//		//FOR STANDARD THE TREE BANK FILE
//		String inputFile="./data/CTB5.0/dev.txt";
//		String outputFile="./data/CTB5.0/dev.standard";
//		standardFile(inputFile,outputFile);
		
//		FOR DELETE THE TOP NODE OF THE TREE(SINGLE CHILD)
		String inputFile="J:/huangsj/EclipseData/TreeLM/xin/xin.en.encorp.tok.treetok.berkeleytree";
		String outputFile="./data/giga.delTop";
		deleteTopNode(inputFile,outputFile);
//		standardFil?e("./data/WSJ-autoPos/testr.txt.delTop", "./data/WSJ-autoPos/testr.txt.new");
		
		
	}
}

