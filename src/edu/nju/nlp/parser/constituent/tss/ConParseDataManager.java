package edu.nju.nlp.parser.constituent.tss;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.nju.nlp.main.Parser.Options.languageType;
import edu.nju.nlp.online.types.DataManager;
import edu.nju.nlp.online.types.FeatureVector;
import edu.nju.nlp.online.types.Instance;
import edu.nju.nlp.online.types.MultiHashAlphabet;
import edu.nju.nlp.parser.transition.ConAction;

/**
 * 
 * @author Hao Zhou
 * */
public class ConParseDataManager implements DataManager {

	public static MultiHashAlphabet dataAlphabet;
	public ConParseFeatureHandler fHandler;
	
	/**
	 * Constructor
	 * */
	public ConParseDataManager(){
		dataAlphabet=new MultiHashAlphabet();
		fHandler=new ConParseFeatureHandlerZZ13(dataAlphabet);	//TODO the now feature set is Zhang2009
	}
	
	public ConParseDataManager(MultiHashAlphabet dataAlphabet1){
		dataAlphabet=dataAlphabet1;
		fHandler=new ConParseFeatureHandlerZZ13(dataAlphabet);	//TODO the now feature set is Zhang2009
	}
	
	/**
	 * In the constituent parsing task, Alphabet needn't be created before 
	 * training module.
	 * So the function from the base class is not initial.
	 * */
	@Override
	public void createAlphabets(String file) throws IOException {
		// TODO Auto-generated method stub

	}

	/**
	 * set the Alphabet never grow
	 * */
	@Override
	public void closeAlphabets() {
		dataAlphabet.stopGrowth();
		ConParseInstance.setDataAlphabet(dataAlphabet);
	}
	
	@Override
	public Instance[] readData(String file) throws IOException{
		return readData(file, false);
	}

	/**
	 * read data from file 
	 * convert data in file to instance
	 * @param	file input file
	 * @param	createFeatureFile whether create feature file or not
	 * */
	@Override
	public Instance[] readData(String file, boolean createFeatureFile)
			throws IOException {

		// the reader of train file
		InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
		BufferedReader in = new BufferedReader(isr);
		
		// get a line from the file
		String treeString = in.readLine();
		LinkedList<ConParseInstance> lt=new LinkedList<ConParseInstance>();
		int num1=0;	//indicator of console output
		
		while(treeString!=null){
			
			if(num1>0&&num1%500==0)	//every 500 sentence , console print
				System.out.println("Creating Feature Vector Instance: " + num1+ ", Num Feats: " + dataAlphabet.size());
			if(treeString.matches("\\S*")){	// empty line skip
				treeString=in.readLine();
				num1++;
				continue;
			}
			else{
				/* construct the constituent parser instance*/
				
				//	construct the CFG tree
				CFGTree cfgTree=new CFGTree();
				List<String> tokens=getTokens(treeString);
				cfgTree.CTBReadNote(tokens);
				
				/*
				 * In the create input module
				 * set the leaf node's constituent "NONE"
				 */
				ConParseInput cInput=new ConParseInput(cfgTree,true);	//set the gold tag true!
				ConParseLabel cLabel=new ConParseLabel(cfgTree);
																		//here will modify with more parameter
																		//are available
				ConParseInstance cInstance=new ConParseInstance(cInput,cLabel,null);	//the instance feature is not set
																						//here, because early update needn't 
																						//compute all of the feature vectors
				/*prepare for next tree*/
				lt.add(cInstance);
				treeString=in.readLine();
				num1++;
				
			}
		}
		in.close();
		System.out.println("The instances have been read in !");
		
		/*from link list to array*/
		ConParseInstance[] cInstances=new ConParseInstance[lt.size()];
		for(int i=0;i<cInstances.length;i++)
			cInstances[i]=lt.get(i);
		System.out.println("Already change Link List to Array!");
		
		return cInstances;
	}
	
	/**
	 * get tokens from a String line
	 * 
	 * @param line the line of input corpus
	 * @return the list of string split by space in the line
	 * */
	public List<String> getTokens(String line) {
		
		List<String> rtn=new ArrayList<String>();    //返回值
		String[] str=line.split("\\s{1,}");
		
		for(int i=0;i<str.length;i++)  {
			if(str[i].equals(" ")||str[i].equals("　")) continue;
			rtn.add(str[i]);
		}
		
		return rtn;
	}

	@Override
	public MultiHashAlphabet getDataAlphabet() {
		return dataAlphabet;
	}
	
	/**
	 * get the current state and next action's 
	 * */
	public FeatureVector createFeatureVector(ConParseTSSState state, ConAction action, FeatureVector fv, boolean bAdd){
		return fHandler.getFeatures(state.atomic, action, fv, bAdd);
	}

	public static void setDataAlphabet(MultiHashAlphabet alphabet) {
		dataAlphabet=alphabet;
		ConParseFeatureHandlerZZ13.dataAlphabet=alphabet;
	}

	/**
	 * Read the test sentence from the test file
	 * input sentence just as NP_XX VP_XX
	 * 
	 * @param testFileName the test file name
	 * @param language different language has different input file format
	 * @return	the test instance with the label null
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public ConParseInstance[] readTestData(String testFileName,languageType language) throws IOException {
		
		// the reader of test file
		InputStreamReader isr = new InputStreamReader(new FileInputStream(testFileName), "UTF-8");
		BufferedReader in = new BufferedReader(isr);
		
		// get a line from the file
		String line = in.readLine();
		LinkedList<ConParseInstance> lt=new LinkedList<ConParseInstance>();
		int num1=0;	//indicator of console output
		
		while(line!=null){
			
			if(num1>0&&num1%500==0)	//every 500 sentence , console print
				System.out.println("Creating Feature Vector Instance: " + num1+ ", Num Feats: " + dataAlphabet.size());
			if(line.matches("\\s*")){	// empty line skip
				line=in.readLine();
				num1++;
				continue;
			}
			else{
				/* construct the constituent parser instance*/
				
				//	construct the CFG tree
				List<String> tokens=getTokens(line);
				
				/*
				 * get the ConstituentLabel and tag sequence from the tokens
				 */
				ArrayList<String> words=new ArrayList<String>();
				ArrayList<String> tags=new ArrayList<String>();
				for(String s:tokens){
					String[] pair;
					if(language==languageType.Chinese)
						pair=s.split("_");
					else if(language==languageType.English){
						pair=s.split("/");
						if(pair.length>2)
						{
							/*
							 * add the string of pair[1]~pair[length-2] to pair[0]
							 * i.e. de/dw/NN the word is de/dw and the pos tag is NN
							 */
							for(int i=1;i<(pair.length-1);i++){
								pair[0]=pair[0]+"/"+pair[i];
							}
							pair[1]=pair[pair.length-1];
						}
					}
					else {
						throw new RuntimeException("language "+language+" has not been supported!");
					}
					words.add(pair[0]);
					tags.add(pair[1]);
				}
				
				ConParseInput cInput=new ConParseInput(words, tags);	//set the gold tag true!
																		//here will modify with more parameter
																		//are available
				ConParseInstance cInstance=new ConParseInstance(cInput,null,null);	//the instance feature is not set
																						//here, because early update needn't 
																						//compute all of the feature vectors
				/*prepare for next tree*/
				lt.add(cInstance);
				line=in.readLine();
				num1++;
			}
		}
		in.close();
		System.out.println("The test instances have been read in !");
		
		/*from link list to array*/
		ConParseInstance[] cInstances=new ConParseInstance[lt.size()];
		for(int i=0;i<cInstances.length;i++)
			cInstances[i]=lt.get(i);
		System.out.println("Already change Link List to Array!");
		
		return cInstances;		
	}

	/**
	 * save the test module's parsed file 
	 * 
	 * @param pres	predication list
	 * @param outputFile
	 * @throws IOException
	 */
	public void saveParsedFile(ConParsePrediction[] pres, String outputFile) throws IOException {

		OutputStreamWriter osw=new OutputStreamWriter(new FileOutputStream(outputFile),"UTF-8");
		PrintWriter pw=new PrintWriter(osw,true);
		
		for(ConParsePrediction pre:pres){
			ConParseLabel label=(ConParseLabel) pre.getBestLabel();
			CFGTree preTree=label.getCfgTree();
			preTree.root=preTree.nodes.size()-1;
			preTree.writeCTBTree(pw);
			pw.println();
			pw.flush();
		}
		
		pw.close();
	}
	
}
