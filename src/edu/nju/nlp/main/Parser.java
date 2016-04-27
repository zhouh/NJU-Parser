package edu.nju.nlp.main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import edu.nju.nlp.commons.dict.Dictionary;
import edu.nju.nlp.parser.constituent.evalb.ConParserEvalb;
import edu.nju.nlp.util.Option;
import edu.nju.nlp.util.OptionParser;



public class Parser {

	/**
	 * @param args
	 */
	
	public static class Options {

		public enum languageType{Chinese,English};
		public enum decodeType{BeamSearch,DP};
		public enum modeType{test,train,IncreTrainAndTest,outputActionSequence,printDetail};
		public enum LMType{tag,head,label,action,actionLabel,actionHead,actionTag};

		@Option(name="-configureFile", required=true, usage="The laguage type to be parsed\n")
		public String configureFile;

		@Option(name="-mode", required=false, usage="The laguage type to be parsed\n")
		public modeType mode=modeType.train;
		
		@Option(name="-language", required=false, usage="The laguage type to be parsed\n")
		public languageType language=languageType.Chinese;
		
		@Option(name="-trainFile", usage="The file to be trained(UTF-8 encode and zpar style binary tree\n)")
		public String trainFile;
		
		@Option(name="-testFile", usage="The file to test(UTF-8 encode and different language has different input format\n)")
		public String testFile;
		
		@Option(name="-devFile", usage="The dev file name(UTF-8 encode and different language has different input format\n)")
		public String devFile;
		
		@Option(name="-testResultFile", usage="The result file after test(UTF-8 encode\n)")
		public String testResultFile;
		
		@Option(name="-modelFile", usage="The model file name(Generated and read by generized \n)")
		public String modelFile;
		
		@Option(name="-detailFile", usage="The detail file name for recording all the state detail in the beam during parsing (Generated and read by generized \n)")
		public String detailFile;
		
		@Option(name="-trainIters", usage="The iteration of training \n")
		public int iteration;
		
		@Option(name="-averageParameter", usage="Whether avarage the parameter after training \n")
		public boolean averageParameter=false;
		
		@Option(name="-decode", usage="The decode type \n")
		public decodeType decode=decodeType.BeamSearch;
		
		@Option(name="-beamSize", usage="The beam size  \n")
		public int beamSize;
		
		@Option(name="-LMType", usage="The LM type of ALM \n")
		public LMType lmType;

		@Option(name="-LMResult", usage="The ALM train sequence file name \n")
		public String lmResult;
		
		@Option(name="-LMOutputKBest", usage="The ALM train sequence file name \n")
		public boolean LMOutoutKBest;
		
		@Option(name="-LMOutputKBestFile", usage="ALM rerank output options \n")
		public String LMOutputKBestFile;
		
		@Option(name="-LMgoldFile", usage="ALM rerank output options \n")
		public String LMgoldFile;
		
		@Option(name="-actionALM", usage="ALM rerank output options \n")
		public String actionALM;
		
		@Option(name="-headALM", usage="ALM rerank output options \n")
		public String headALM;
		
		@Option(name="-labelALM", usage="ALM rerank output options \n")
		public String labelALM;
		
		@Option(name="-tagALM", usage="ALM rerank output options \n")
		public String tagALM;
		
		@Option(name="-actionLabelALM", usage="ALM rerank output options \n")
		public String actionLabelALM;
		
		@Option(name="-actionHeadALM", usage="ALM rerank output options \n")
		public String actionHeadALM;
		
		@Option(name="-actionTagALM", usage="ALM rerank output options \n")
		public String actionTagALM;
		
		@Option(name="-tagALMGold", usage="ALM rerank output options \n")
		public String tagALMGold;
		
		@Option(name="-headALMGold", usage="ALM rerank output options \n")
		public String headALMGold;
		
		@Option(name="-outputALMKBestAnalysisFile", usage="ALM rerank output options \n")
		public String outputALMKBestAnalysisFile;
		
		
		
	}
	
	/**
	 * Begin Parsing from here.
	 * 
	 * There are 2 different way to get arguments options.
	 * One is from the XML configure file.
	 * The other is from the command input.
	 * The command input is prior than XML configure file.
	 *  
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
//		/*
//	     * get a XML format properties file
//	     */
//		Properties prop=new Properties();
//		InputStream in1= new BufferedInputStream(new FileInputStream("./props/Parser.properties"));
//	    FileOutputStream fos = new FileOutputStream("./props/sample.xml");
//	    prop.load(in1);
//        prop.storeToXML(fos, "Store Sample");
//        in1.close();
//        fos.close();
        
		OptionParser optParser = new OptionParser(Options.class);
		Options opts = (Options) optParser.parse(args, true);
		
		InputStream in=null;
		Properties p=new Properties();
		try {
			//load properties from XML file
		    in = new BufferedInputStream(new FileInputStream(opts.configureFile));
		    p.loadFromXML(in);
		    
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		//fill the option from XML file
		try {
			optParser.parsePropertiesFromXML(p, opts);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		
		/*
		 * model entrance module
		 */
		ConParse cParser=new ConParse(opts.language);
		
		/*
		 * ALM sequence probability calculator 
		 * 
		 * send the parameter to ConParser before new the object
		 * by set all the parameter static :(
		 */
//		ALMEvalb.outputFile=opts.LMOutputKBestFile;
//		ALMEvalb.goldFile=opts.LMgoldFile;
		if(opts.LMOutoutKBest){
			ConParserEvalb.actionALM=opts.actionALM;
			ConParserEvalb.labelALM=opts.labelALM;
			ConParserEvalb.headALM=opts.headALM;
			ConParserEvalb.tagALM=opts.tagALM;
			ConParserEvalb.actionLabelALM=opts.actionLabelALM;
			ConParserEvalb.actionHeadALM=opts.actionHeadALM;
			ConParserEvalb.actionTagALM=opts.actionTagALM;
			ConParserEvalb.outputAnalysisFile=opts.outputALMKBestAnalysisFile;
			ConParserEvalb ALMEvalb=ConParserEvalb.getInstance(false,opts.LMOutputKBestFile,opts.LMgoldFile,opts.outputALMKBestAnalysisFile);
			ConParserEvalb.outputKBestALMDetail=opts.LMOutoutKBest;
			ALMEvalb.beamSize=opts.beamSize;
		}
		
		
		//set the language of dictionary
		Dictionary.setLanguageType(opts.language);
		
		//train module
		if(opts.mode==Options.modeType.train){
			
			//model file exits and bResume , overwritten
			if ((new File(opts.modelFile)).exists())     //如果模型文件已存在
				System.err.println(opts.modelFile + " already exists. Will be overwritten.");
			
			cParser.train(opts.trainFile, opts.modelFile, opts.iteration, opts.averageParameter);
		}
		//test module
		else if(opts.mode==Options.modeType.test){
			cParser.test(opts.testFile, opts.testResultFile, opts.modelFile, opts.averageParameter,opts.beamSize);
		}
		//printDetail module
		else if(opts.mode==Options.modeType.printDetail){
//			@TODO 
		}
		else if(opts.mode==Options.modeType.IncreTrainAndTest){
			cParser.incretrainAndTest(opts.trainFile, opts.modelFile, opts.testFile, opts.testResultFile, opts.iteration, opts.averageParameter);
		}
		else if(opts.mode==Options.modeType.outputActionSequence){
			cParser.outputActionSequence(opts.trainFile,opts.lmResult,opts.lmType);
		}
		else System.err.println("Invalid run mode!");
	}

}
