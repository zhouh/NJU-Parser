package edu.nju.nlp.main;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import edu.nju.nlp.main.Parser.Options.LMType;
import edu.nju.nlp.main.Parser.Options.languageType;
import edu.nju.nlp.online.alg.OnlineLearner;
import edu.nju.nlp.online.alg.OnlineUpdator;
import edu.nju.nlp.online.alg.PerceptronUpdator;
import edu.nju.nlp.parser.constituent.tss.CFGTree;
import edu.nju.nlp.parser.constituent.tss.ConParseDataManager;
import edu.nju.nlp.parser.constituent.tss.ConParseInput;
import edu.nju.nlp.parser.constituent.tss.ConParseInstance;
import edu.nju.nlp.parser.constituent.tss.ConParseLabel;
import edu.nju.nlp.parser.constituent.tss.ConParsePrediction;
import edu.nju.nlp.parser.constituent.tss.ConParsePredictor;


public class ConParse {

	languageType language;
	
	public ConParse(languageType language){
		this.language=language;
	}
	
	/**
	 * 
	 */
	public void train(String _trainFileName, String _modelFileName,int numTrainingIters, boolean bAvgParams) throws Exception{
		
		//dataManager,control the data,and the alphabet in it
		ConParseDataManager manager=new ConParseDataManager();
		
		ConParseInstance[] trainingData = (ConParseInstance[]) manager.readData(_trainFileName);	//generate the instance by the document                               //
		
		System.out.println("Instance input down!");
		
		//the train function and return a predictor for test
		trainOnData(_trainFileName, _modelFileName, numTrainingIters,bAvgParams, manager, trainingData);
	  
   }
	
	/*
	 * real train function
	 * 
	 * */
	public void trainOnData(String _trainFileName, String _modelFileName,
			int numTrainingIters, boolean bAvgParams, ConParseDataManager manager,
			ConParseInstance[] trainingData) throws Exception{
		
		int k = 1;                    //k best 
		//get the dataAlphabet and 
		// get the Alphabet size
		int numFeats = trainingData.length*10000;
		
		OnlineLearner learner = new OnlineLearner();  // initial the Online learner
		
		//set the ConParsePredictor as a CWConParsePredictor
		ConParsePredictor predictor = new ConParsePredictor(numFeats, manager, true, true, k, 16, bAvgParams,true);
		
		if((new File(_modelFileName)).exists())
			predictor.loadModel(_modelFileName);
		
		//the online updator 
		OnlineUpdator updator=new PerceptronUpdator();
		//start train
		learner.train(trainingData, updator, predictor, numTrainingIters,
				_trainFileName, bAvgParams);
		//save model by itself
		
		predictor.saveModel(_modelFileName);
			
	}
	

	public void test(String TestFileName,String OutputFile, String modelFile, 
			boolean bAvgParams,int beamSize)throws Exception{
		
		//get the instance from document for test
		
		ConParsePredictor predictor=new ConParsePredictor(0, true, beamSize, bAvgParams,false);
		
		predictor.loadModel(modelFile);
		
		ConParseDataManager manager=predictor.getManager();
		
		ConParseInstance[] sis=manager.readTestData(TestFileName,language);
		
		//store the predction into the arrays
		ConParsePrediction[] pres=new ConParsePrediction[sis.length];
		
		long startTime=System.nanoTime();
		for(int i=0;i<sis.length;i++){
			
			System.out.println("test: "+i);
			pres[i]=(ConParsePrediction) predictor.decode(sis[i], null);
		}
		long endTime=System.nanoTime();
		System.out.println("Tss cost time:	"+(endTime-startTime)+"	ns");
		
		//save the parsed tree into the file
		manager.saveParsedFile(pres, OutputFile);
		
		//close the printwriter
	}
	
	public void incretrainAndTest(String _trainFileName, String _modelFileName,
			String _testFileName, String _resultFileName,
			int numTrainingIters, boolean bAvgParams) throws Exception{
		
		ConParseDataManager manager=new ConParseDataManager();
		
		ConParseInstance[] trainingData = (ConParseInstance[]) manager.readData(_trainFileName);	//generate the instance by the document                               //
		System.out.println("Instance input down!");
		
		int k = 1;                    //k best 
		//get the dataAlphabet and 
		// get the Alphabet size
		int numFeats = trainingData.length*10000;
		
		OnlineLearner learner = new OnlineLearner();  // initial the Online learner
		
		//set the ConParsePredictor as a CWConParsePredictor
		ConParsePredictor predictor = new ConParsePredictor(numFeats, manager, true, true, k, 16, bAvgParams,true);
		
		//the online updator 
		OnlineUpdator updator=new PerceptronUpdator();
		
		for(int i=1;i<=numTrainingIters;i++){
			//start train
//			if((new File(_modelFileName)).exists())
//				predictor.loadModel(_modelFileName);
			learner.train(trainingData, updator, predictor, 1,
					_trainFileName, bAvgParams);
			//save model by itself
			predictor.saveModel(_modelFileName+i);
			test(_testFileName, _resultFileName+i, _modelFileName+i,bAvgParams,16);
			
		}
			
	}

	public void outputActionSequence(String _trainFileName, String outputFile,LMType lm) throws IOException {
		
		OutputStreamWriter osw=new OutputStreamWriter(new FileOutputStream(outputFile),"UTF-8");
		PrintWriter pw=new PrintWriter(osw,true);
		
		ConParseDataManager manager=new ConParseDataManager();
		ConParsePredictor predictor = new ConParsePredictor(100000, manager, true, true, 1, 16, true,true);
		
		//--------------------------------------------------------------------------
		// the reader of train file
		InputStreamReader isr = new InputStreamReader(new FileInputStream(_trainFileName), "UTF-8");
		BufferedReader in = new BufferedReader(isr);
		
		// get a line from the file
		String treeString = in.readLine();
		int num1=0;	//indicator of console output
		
		while(treeString!=null){
			
			if(num1>0&&num1%500==0)	//every 500 sentence , console print
				System.out.println(num1);
			if(treeString.matches("\\S*")){	// empty line skip
				treeString=in.readLine();
				num1++;
				continue;
			}
			else{
				/* construct the constituent parser instance*/
				
				//	construct the CFG tree
				CFGTree cfgTree=new CFGTree();
				List<String> tokens=manager.getTokens(treeString);
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
				treeString=in.readLine();
				num1++;
				
				pw.println(predictor.getActionLMSequence(cInstance, lm));
				pw.flush();
				
			}
		}
		in.close();
		System.out.println("Output done !");
		//--------------------------------------------------------------------------
		
		pw.close();
		
	}

//	public void outPutActionList(String treeFile, String actionSequenceFile) throws IOException {
//
//		OutputStreamWriter osw4=new OutputStreamWriter(new FileOutputStream(actionSequenceFile),"UTF-8");
//		PrintWriter pw4=new PrintWriter(osw4,true);
//		
//		ConParseDataManager manager=new ConParseDataManager();
//		
//		ConParseInstance[] treeInstances = (ConParseInstance[]) manager.readData(treeFile);	//generate the instance by the document                               //
//		
//		System.out.println("Instance input down!");
//		
//		ConParsePredictor predictor=new ConParsePredictor(100);
//		
//		for(int i=0;i<treeInstances.length;i++){
//			String actionSequence=predictor.getActionSequence(treeInstances[i]);
//			pw4.println(actionSequence);
//			pw4.flush();
//		}
//
//		pw4.close();
//	}
}
