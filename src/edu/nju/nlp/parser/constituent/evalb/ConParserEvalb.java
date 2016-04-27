package edu.nju.nlp.parser.constituent.evalb;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;

import edu.berkeley.nlp.parser.EnglishPennTreebankParseEvaluator;
import edu.berkeley.nlp.syntax.Tree;
import edu.berkeley.nlp.syntax.Trees;
import edu.nju.nlp.lm.GetLMProbability;
import edu.nju.nlp.parser.constituent.tss.ChartItem;
import edu.nju.nlp.parser.constituent.tss.ConParseTSSState;
import edu.nju.nlp.parser.transition.ConAction;
import edu.nju.nlp.util.Correlation;
import edu.nju.nlp.util.FourTripple;
import edu.nju.nlp.util.doubleGram;

/**
 * Syntax parser evalb with shift-reduce score, action-based language model score
 * and Collins EVALB score.
 * 
 * @author Hao Zhou
 *
 */
public class ConParserEvalb {

	/**
	 * @param args
	 */
	 private enum evalbType{TEST,TRAIN};
	 @SuppressWarnings("unused")
	 private evalbType type;
	 public static boolean outputKBestALMDetail;
//	 public String outputFile;
//	 public String goldFile;
	 public static String actionALM;
	 public static String headALM;
	 public static String labelALM;
	 public static String tagALM;
	 public static String actionLabelALM;
	 public static String actionHeadALM;
	 public static String actionTagALM;
	 public static String tagALMGold;
	 public static String headALMGold;
	 public static String outputAnalysisFile;
	 public int beamSize;
	 //////////////////////////////////////////////////////////////////////////
	 PrintWriter pw_out_action;
	 PrintWriter pw_out_label;
	 PrintWriter pw_out_head;
	 PrintWriter pw_out_tag;
	 PrintWriter pw_out_actionHead;
	 PrintWriter pw_out_actionTag;
	 PrintWriter pw_out;
	 //////////////////////////////////////////////////////////////////////////
	 PrintWriter pw_analysis;
	 
	 
	 /*
	  * different LM object
	  * 
	  * There are two main action list language model here,
	  * 1. head action LM, action+head ConstituentLabel, i.e. S-（ S-完 S -） BRL*-完 BRR-完 
	  * 2. tag action LM, action+tag, i.e. S-S-PU S-S-VV S-S-PU BRL*-FRAG* BRR-FRAG 
	  * 
	  * But we have 2 train file here. One is extracted from CTB5.0 directed with gold pos-tag, and the 
	  * other are got from muhua zhu which's pos-tag are auto generated.
	  */
//	 private static GetLMProbability posLM5;
//	 private static GetLMProbability headLM5;
//	 private static GetLMProbability posLM4;
//	 private static GetLMProbability headLM4;
//	 private static GetLMProbability posLM3;
//	 private static GetLMProbability headLM3;
//	 private static GetLMProbability posLM2;
//	 private static GetLMProbability headLM2;
//	 private static GetLMProbability posLM_gold;
//	 private static GetLMProbability headLM_gold;
	 private static GetLMProbability actionLM;
	 private static GetLMProbability actionLM4;
	 private static GetLMProbability actionLM3;
	 private static GetLMProbability actionLM2;
	 private static GetLMProbability labelLM;
	 private static GetLMProbability labelLM4;
	 private static GetLMProbability labelLM3;
	 private static GetLMProbability labelLM2;
	 private static GetLMProbability headLM;
	 private static GetLMProbability headLM4;
	 private static GetLMProbability headLM3;
	 private static GetLMProbability headLM2;
	 private static GetLMProbability tagLM;
	 private static GetLMProbability tagLM4;
	 private static GetLMProbability tagLM3;
	 private static GetLMProbability tagLM2;
	 private static GetLMProbability actionLabelLM;
	 /*
	  * get 3 new actionLabel of 4,3,2 gram 
	  */
	 private static GetLMProbability actionLabelLM4;
	 private static GetLMProbability actionLabelLM3;
	 private static GetLMProbability actionLabelLM2;
	 private static GetLMProbability actionHeadLM;
	 private static GetLMProbability actionHeadLM4;
	 private static GetLMProbability actionHeadLM3;
	 private static GetLMProbability actionHeadLM2;
	 private static GetLMProbability actionTagLM;
	 private static GetLMProbability actionTagLM4;
	 private static GetLMProbability actionTagLM3;
	 private static GetLMProbability actionTagLM2;
	 static int sentID=0;
	 
	 private static double P1=0;
	 private static double P2=0;
	 private static double P3=0;
	 private static double P4=0;
	 private static double P5=0;
	 private static double P6=0;
	 private static double P7=0;
	 private static double P8=0;
	 private static int sum1=0;
	 private static int sum2=0;
	 private static int sum3=0;
	 private static int sum4=0;
	 private static int sum5=0;
	 private static int sum6=0;
	 private static int sum7=0;
	 private static int sum8=0;
	 
	 
	 private static ConParserEvalb trainInstance=null;
	 private static ConParserEvalb testInstance=null;
	 
	 public static ArrayList<String> goldTestTree=new ArrayList<String>();
	 
	 private ConParserEvalb(boolean bTrain,String outputFileName,String goldFileName,String analysisFile) throws IOException{
		 type=bTrain?evalbType.TRAIN:evalbType.TEST;
		 
		 OutputStreamWriter o1=new OutputStreamWriter(new FileOutputStream(outputFileName),"UTF-8");
		 pw_out=new PrintWriter(o1,true);
		 OutputStreamWriter o2=new OutputStreamWriter(new FileOutputStream(outputFileName+".action"),"UTF-8");
		 pw_out_action=new PrintWriter(o2,true);
		 OutputStreamWriter o3=new OutputStreamWriter(new FileOutputStream(outputFileName+".label"),"UTF-8");
		 pw_out_label=new PrintWriter(o3,true);
		 OutputStreamWriter o4=new OutputStreamWriter(new FileOutputStream(outputFileName+".head"),"UTF-8");
		 pw_out_head=new PrintWriter(o4,true);
		 OutputStreamWriter o5=new OutputStreamWriter(new FileOutputStream(outputFileName+".tag"),"UTF-8");
		 pw_out_tag=new PrintWriter(o5,true);
		 OutputStreamWriter o6=new OutputStreamWriter(new FileOutputStream(outputFileName+".actionHead"),"UTF-8");
		 pw_out_actionHead=new PrintWriter(o6,true);
		 OutputStreamWriter o7=new OutputStreamWriter(new FileOutputStream(outputFileName+".actionTag"),"UTF-8");
		 pw_out_actionTag=new PrintWriter(o7,true);
		 OutputStreamWriter eno=new OutputStreamWriter(new FileOutputStream(analysisFile+".analysis"),"UTF-8");
		 pw_analysis=new PrintWriter(eno,true);
		 
		 
		 //add the test gold tree 
		 if(!bTrain){
			 InputStreamReader isr = new InputStreamReader(new FileInputStream(goldFileName), "UTF-8");
				BufferedReader in = new BufferedReader(isr);
				
				String line=in.readLine();
				
				while(line!=null){
					
					goldTestTree.add(line.trim());
					line=in.readLine();
				}
				in.close();
		 }
			
		 
//		 posLM5=new GetLMProbability(actionLabelALM+".bin", 5);
//		 headLM5=new GetLMProbability(actionHeadALM+".bin", 5);
//		 posLM4=new GetLMProbability(actionLabelALM+".bin", 4);
//		 headLM4=new GetLMProbability(actionHeadALM+".bin", 4);
//		 posLM3=new GetLMProbability(actionLabelALM+".bin", 3);
//		 headLM3=new GetLMProbability(actionHeadALM+".bin", 3);
//		 posLM2=new GetLMProbability(actionLabelALM+".bin", 2);
//		 headLM2=new GetLMProbability(actionHeadALM+".bin", 2);
//		 posLM_gold=new GetLMProbability(tagALMGold+".bin", 5);
//		 headLM_gold=new GetLMProbability(headALMGold+".bin", 5);
		 actionLM=new GetLMProbability(actionALM+".bin", 5);
		 labelLM=new GetLMProbability(labelALM+".bin", 5);
		 headLM=new GetLMProbability(headALM+".bin", 5);
		 tagLM=new GetLMProbability(tagALM+".bin", 5);
		 actionLM4=new GetLMProbability(actionALM+".bin", 4);
		 labelLM4=new GetLMProbability(labelALM+".bin", 4);
		 headLM4=new GetLMProbability(headALM+".bin", 4);
		 tagLM4=new GetLMProbability(tagALM+".bin", 4);
		 actionLM3=new GetLMProbability(actionALM+".bin", 3);
		 labelLM3=new GetLMProbability(labelALM+".bin", 3);
		 headLM3=new GetLMProbability(headALM+".bin", 3);
		 tagLM3=new GetLMProbability(tagALM+".bin", 3);
		 actionLM2=new GetLMProbability(actionALM+".bin", 2);
		 labelLM2=new GetLMProbability(labelALM+".bin", 2);
		 headLM2=new GetLMProbability(headALM+".bin", 2);
		 tagLM2=new GetLMProbability(tagALM+".bin", 2);
		 
		 
		 actionLabelLM=new GetLMProbability(actionLabelALM+".bin", 5);
		 
		 /*
		  * 4 3 2 gram of action label LM
		  */
		 actionLabelLM4=new GetLMProbability(actionLabelALM+".bin", 4);
		 actionLabelLM3=new GetLMProbability(actionLabelALM+".bin", 3);
		 actionLabelLM2=new GetLMProbability(actionLabelALM+".bin", 2);
		 /*
		  * 4 3 2 gram of action head LM
		  */
		 actionHeadLM4=new GetLMProbability(actionHeadALM+".bin", 4);
		 actionHeadLM3=new GetLMProbability(actionHeadALM+".bin", 3);
		 actionHeadLM2=new GetLMProbability(actionHeadALM+".bin", 2);
		 
		 actionHeadLM=new GetLMProbability(actionHeadALM+".bin", 5);
		 
		 /**
		  * 4 3 2 gram of action tag LM
		  */
		 actionTagLM=new GetLMProbability(actionTagALM+".bin", 5);
		 
		 actionTagLM4=new GetLMProbability(actionTagALM+".bin", 4);
		 actionTagLM3=new GetLMProbability(actionTagALM+".bin", 3);
		 actionTagLM2=new GetLMProbability(actionTagALM+".bin", 2);
	 }
	 
	 public static ConParserEvalb getInstance(boolean bTrain) throws IOException{
		 if(bTrain)
			if(trainInstance==null)
				throw new RuntimeException("The function must be used after the object has been exited!");
			else return trainInstance;
		 else
			 if(testInstance==null)
				 throw new RuntimeException("The function must be used after the object has been exited!");
			 else return testInstance;
	 }
	 
	 public static ConParserEvalb getInstance(boolean bTrain,String outputFileName,String goldFileName,String analysisFile) throws IOException{
		 if(bTrain)
			 return trainInstance=new ConParserEvalb(true,outputFileName,goldFileName,analysisFile);
		 else
			 return testInstance=new ConParserEvalb(false,outputFileName,goldFileName,analysisFile);
	 }
	 
	 
	 public void outputDetailsOfBeam(LinkedList<ChartItem> linkItems,int sentID) throws IOException {
			
			
			double score[]=new double[beamSize];
			double actionLM_score[]=new double[beamSize];
			double labelLM_score[]=new double[beamSize];
			double headLM_score[]=new double[beamSize];
			double tagLM_score[]=new double[beamSize];
			double actionLabelLM_score[]=new double[beamSize];
			double actionHeadLM_score[]=new double[beamSize];
			double actionTagLM_score[]=new double[beamSize];
			double evalb[]=new double[beamSize];
			
//			pw_analysis.println("//////////////////////////////////////////////////////////////");
			int i=0;
			for(ChartItem item:linkItems){
//				pw_analysis.println(i);
				doubleGram nineTripple=outputItem(item,pw_analysis,sentID);
				score[i]=nineTripple.value[0];
				actionLM_score[i]=nineTripple.value[1];
				labelLM_score[i]=nineTripple.value[2];
				headLM_score[i]=nineTripple.value[3];
				tagLM_score[i]=nineTripple.value[4];
				actionLabelLM_score[i]=nineTripple.value[5];
				actionHeadLM_score[i]=nineTripple.value[6];
				actionTagLM_score[i]=nineTripple.value[7];
				evalb[i]=nineTripple.value[8];
				i++;
				
			}
			////////////////////////////////////////////////////////////////////////////
			pw_out_action.println("======");
			pw_out_label.println("======");
			pw_out_head.println("======");
			pw_out_tag.println("======");
			pw_out_actionHead.println("======");
			pw_out_actionTag.println("======");
			pw_out_action.flush();
			pw_out_label.flush();
			pw_out_head.flush();
			pw_out_tag.flush();
			pw_out_actionHead.flush();
			pw_out_actionTag.flush();
			
			double p1=Correlation.pearson(evalb, score);
			double p2=Correlation.pearson(evalb, actionLM_score);
			double p3=Correlation.pearson(evalb, labelLM_score);
			double p4=Correlation.pearson(evalb, headLM_score);
			double p5=Correlation.pearson(evalb, tagLM_score);
			double p6=Correlation.pearson(evalb, actionLabelLM_score);
			double p7=Correlation.pearson(evalb, actionHeadLM_score);
			double p8=Correlation.pearson(evalb, actionTagLM_score);
			
			if(!Double.isNaN(p1)) {
				P1+=p1;
				sum1++;
			}
			if(!Double.isNaN(p2)) {
				P2+=p2;
				sum2++;
			}
			if(!Double.isNaN(p3)) {
				P3+=p3;
				sum3++;
			}
			if(!Double.isNaN(p4)) {
				P4+=p4;
				sum4++;
			}
			if(!Double.isNaN(p5)) {
				P5+=p5;
				sum5++;
			}
			if(!Double.isNaN(p6)) {
				P6+=p6;
				sum6++;
			}
			if(!Double.isNaN(p7)) {
				P7+=p7;
				sum7++;
			}
			if(!Double.isNaN(p8)) {
				P8+=p8;
				sum8++;
			}
			
			System.out.println("p1:	"+P1/sum1+"	"+"p2:	"+P2/sum2+"	"+"p3:	"+P3/sum3+"	"+"p4:	"+P4/sum4+"	"+"p5:	"+P5/sum5+"	"
					+"p5:	"+P6/sum6+"	"+"p5:	"+P7/sum7+"	"+"p8:	"+P8/sum8+"	");
			
			pw_analysis.println(p1+"\t"+p2+"\t"+p3+"\t"+p4+"\t"+p5+"\t"+p6+"\t"+p7+"\t"+p8);
			pw_analysis.flush();
		}

		@SuppressWarnings("unused")
		private doubleGram outputItem(ChartItem item, PrintWriter pw,int sentID) {
			
			Tree<String> goldTree1 = (new Trees.PennTreeReader(new StringReader(
					goldTestTree.get(sentID)))).next();
			
			String guessedTreeString=item.curState.convert2CFGTree().toString();
			Tree<String> guessedTree1 = (new Trees.PennTreeReader(new StringReader(
					guessedTreeString))).next();
			EnglishPennTreebankParseEvaluator.LabeledConstituentEval<String> eval1 = new EnglishPennTreebankParseEvaluator.LabeledConstituentEval<String>(
					new HashSet<String>(Arrays.asList(new String[] { "ROOT"})),
					new HashSet<String>());
			
			
			String actionSequence="";
			String labelSequence="";
			String headSequence="";
			String tagSequence="";
			String actionLabelSequence="";
			String actionHeadSequence="";
			String actionTagSequence="";
			ConParseTSSState current=item.curState;
			 
			@SuppressWarnings("unused")
			int actionSize=0;
			int actionSizeWithoutIdleAndEnd=0;
			
			
			while(current!=null&&current.action!=null){
				ConAction action=current.action;
				if(action.isEndAction()||action.isIDLEAction())
				{
					current=current.statePtr;
					actionSize++;
					continue;
				}
				actionSize++;
				actionSizeWithoutIdleAndEnd++;
				
				String actionString=action.toString();
				String[] tokens=actionString.split("-");
				
				
				String labelString=tokens[tokens.length-1];
					
				if(action.isShiftAction()) {
					actionString=actionString+"-"+item.curState.input.tags.get(current.node.lexical_head);
					labelString=item.curState.input.tags.get(current.node.lexical_head);
				}
				actionSequence=tokens[0]+" "+actionSequence;
				labelSequence=labelString+" "+labelSequence;
				headSequence=item.curState.input.words.get(current.node.lexical_head).toLowerCase()+" "+headSequence;
				tagSequence=item.curState.input.tags.get(current.node.lexical_head)+" "+tagSequence;
				actionLabelSequence=actionString+" "+actionLabelSequence;
				actionHeadSequence=tokens[0]+"-"+item.curState.input.words.get(current.node.lexical_head).toLowerCase()+" "+actionHeadSequence;
				actionTagSequence=tokens[0]+"-"+item.curState.input.tags.get(current.node.lexical_head)+" "+actionTagSequence;
				
				current=current.statePtr;
			}
			
			
			float p_action=actionLM.calSentenceProbWith5gram(actionSequence, actionALM+".vocab");
			float p_label=labelLM.calSentenceProbWith5gram(labelSequence, labelALM+".vocab");
			float p_head=headLM.calSentenceProbWith5gram(headSequence, headALM+".vocab");
			float p_tag=tagLM.calSentenceProbWith5gram(tagSequence, tagALM+".vocab");
			float p_action4=actionLM4.calSentenceProbWith5gram(actionSequence, actionALM+".vocab");
			float p_label4=labelLM4.calSentenceProbWith5gram(labelSequence, labelALM+".vocab");
			float p_head4=headLM4.calSentenceProbWith5gram(headSequence, headALM+".vocab");
			float p_tag4=tagLM4.calSentenceProbWith5gram(tagSequence, tagALM+".vocab");
			float p_action3=actionLM3.calSentenceProbWith5gram(actionSequence, actionALM+".vocab");
			float p_label3=labelLM3.calSentenceProbWith5gram(labelSequence, labelALM+".vocab");
			float p_head3=headLM3.calSentenceProbWith5gram(headSequence, headALM+".vocab");
			float p_tag3=tagLM3.calSentenceProbWith5gram(tagSequence, tagALM+".vocab");
			float p_action2=actionLM2.calSentenceProbWith5gram(actionSequence, actionALM+".vocab");
			float p_label2=labelLM2.calSentenceProbWith5gram(labelSequence, labelALM+".vocab");
			float p_head2=headLM2.calSentenceProbWith5gram(headSequence, headALM+".vocab");
			float p_tag2=tagLM2.calSentenceProbWith5gram(tagSequence, tagALM+".vocab");
			float p_actionLabel=actionLabelLM.calSentenceProbWith5gram(actionLabelSequence, actionLabelALM+".vocab");
			float p_actionLabel4=actionLabelLM4.calSentenceProbWith5gram(actionLabelSequence, actionLabelALM+".vocab");
			float p_actionLabel3=actionLabelLM3.calSentenceProbWith5gram(actionLabelSequence, actionLabelALM+".vocab");
			float p_actionLabel2=actionLabelLM2.calSentenceProbWith5gram(actionLabelSequence, actionLabelALM+".vocab");
			float p_actionHead=actionHeadLM.calSentenceProbWith5gram(actionHeadSequence, actionHeadALM+".vocab");
			float p_actionHead4=actionHeadLM4.calSentenceProbWith5gram(actionHeadSequence, actionHeadALM+".vocab");
			float p_actionHead3=actionHeadLM3.calSentenceProbWith5gram(actionHeadSequence, actionHeadALM+".vocab");
			float p_actionHead2=actionHeadLM2.calSentenceProbWith5gram(actionHeadSequence, actionHeadALM+".vocab");
			float p_actionTag=actionTagLM.calSentenceProbWith5gram(actionTagSequence, actionTagALM+".vocab");
			float p_actionTag4=actionTagLM4.calSentenceProbWith5gram(actionTagSequence, actionTagALM+".vocab");
			float p_actionTag3=actionTagLM3.calSentenceProbWith5gram(actionTagSequence, actionTagALM+".vocab");
			float p_actionTag2=actionTagLM2.calSentenceProbWith5gram(actionTagSequence, actionTagALM+".vocab");
			
			
			/*
			 * Output the detail of action sequence in the analysis file
			 */
//			pw.println("$gold:	"+item.gold);
//			pw.println("$sore:	"+item.score);
//			pw.println("$action:	"+actionSequence);
//			pw.println("$label:	"+labelSequence);
//			pw.println("$head:	"+headSequence);
//			pw.println("$tag:	"+tagSequence);
//			pw.println("$actionLabel:	"+actionLabelSequence);
//			pw.println("$actionHead:	"+actionHeadSequence);
//			pw.println("$actionTag:	"+actionTagSequence);
//			pw.println("$tree:	"+item.curState.convert2CFGTree().toString());
//			pw.println("$Bitree:	"+item.curState.convert2CFGTree().toBiTreeString());
//			pw.println("$p_action:	"+p_action);
//			pw.println("$p_label:	"+p_label);
//			pw.println("$p_head:	"+p_head);
//			pw.println("$p_tag:	"+p_tag);
//			pw.println("$p_actionLabel:	"+p_actionLabel);
//			pw.println("$p_actionLabel4:	"+p_actionLabel4);
//			pw.println("$p_actionLabel3:	"+p_actionLabel3);
//			pw.println("$p_actionLabe2l:	"+p_actionLabel2);
//			pw.println("$p_actionHead:	"+p_actionHead4);
//			pw.println("$p_actionHead4:	"+p_actionHead3);
//			pw.println("$p_actionHead3:	"+p_actionHead2);
//			pw.println("$p_actionHead2:	"+p_actionHead2);
//			pw.println("$p_actionTag:	"+p_actionTag);
//			pw.println("$p_actionTag4:	"+p_actionTag4);
//			pw.println("$p_actionTag3:	"+p_actionTag3);
//			pw.println("$p_actionTag2:	"+p_actionTag2);
//			pw.print("$EVALB Score:	");
//			pw.print("===============================================");
			
			//======get the f1 score and other parameter
			FourTripple f_tripple=eval1.evaluateAndReturnPara(guessedTree1, goldTree1, null);
			pw.flush();
			
//			7 feature all!
//			pw_out.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_action+" "+p_label+" "+p_head+" "+p_tag+" "+p_actionLabel+" "+p_actionHead+" "+p_actionTag+"	"+guessedTreeString);
			//only 2 actionLabel and actionHead combine
//			pw_out.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_actionLabel+" "+p_actionHead+"	"+guessedTreeString);
			//only actionLabel Feature are maintained
//			pw_out.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_actionLabel+"	"+guessedTreeString);
			//only 4 feature :label head actionLabel actionTag  Feature are maintained
//			pw_out.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_label+" "+p_head+" "+p_actionLabel+" "+p_actionTag+"	"+guessedTreeString);			
			//only 2 feature :actionLabel actionTag  Feature are maintained
//			pw_out.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_actionLabel+" "+p_actionTag+"	"+guessedTreeString);			
//			only 4 feature :actionLabel5 actionLabel4 actionLabel3 actionLabel2
//			pw_out.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_actionLabel+" "+p_actionLabel4+" "+p_actionLabel3+" "+p_actionLabel2+"	"+guessedTreeString);			
//			only 8 feature :actionLabel5 actionLabel4 actionLabel3 actionLabel2 actionHead5 actionHead4 actionHead3 actionHead2
//			pw_out.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_actionLabel+" "+p_actionHead3+" "+p_actionHead2+" "+"	"+guessedTreeString);			
//			4 feature + action num :actionLabel5 actionLabel4 actionLabel3 actionLabel2 action quantity
//			pw_out.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_actionLabel+" "+p_actionLabel4+" "+p_actionLabel3+" "+p_actionLabel2+" "+actionSizeWithoutIdleAndEnd+"	"+guessedTreeString);			
//			8 feature all + action count
//			pw_out.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_action+" "+p_label+" "+p_head+" "+p_tag+" "+p_actionLabel+" "+p_actionHead+" "+p_actionTag+" "+actionSizeWithoutIdleAndEnd+"	"+guessedTreeString);
//			only 9 feature :actionLabel5 actionLabel4 actionLabel3 actionLabel2 actionHead5 actionHead4 actionHead3 actionHead2 +actionCount
//			pw_out.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_actionLabel+" "+p_actionLabel4+" "+p_actionLabel3+" "+p_actionLabel2+" "+p_actionHead+" "+p_actionHead4+" "+p_actionHead3+" "+p_actionHead2+" "+actionSizeWithoutIdleAndEnd+"	"+guessedTreeString);			

			//////////////////////////////////////////////////////////////////////////////////////////
			//action action+actionLabel action+actionLabel+actionHead action+actionLabel+actionTag action+actionLabel+actionHead+actionTag 
			pw_out_action.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_action+" "+p_action4+" "+p_action3+" "+p_action2+" "+actionSizeWithoutIdleAndEnd+"	"+guessedTreeString);			
			pw_out_label.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_action+" "+p_action4+" "+p_action3+" "+p_action2+" "+p_actionLabel+" "+p_actionLabel4+" "+p_actionLabel3+" "+p_actionLabel2+" "+actionSizeWithoutIdleAndEnd+"	"+guessedTreeString);			
			pw_out_head.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_action+" "+p_action4+" "+p_action3+" "+p_action2+" "+p_actionLabel+" "+p_actionLabel4+" "+p_actionLabel3+" "+p_actionLabel2+" "+" "+p_actionHead+" "+p_actionHead4+" "+p_actionHead3+" "+p_actionHead2+" "+actionSizeWithoutIdleAndEnd+"	"+guessedTreeString);			
			pw_out_tag.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_action+" "+p_action4+" "+p_action3+" "+p_action2+p_actionLabel+" "+p_actionLabel4+" "+p_actionLabel3+" "+p_actionLabel2+" "+p_actionTag+" "+p_actionTag4+" "+p_actionTag3+" "+p_actionTag2+" "+actionSizeWithoutIdleAndEnd+"	"+guessedTreeString);			
			pw_out_actionHead.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_action+" "+p_action4+" "+p_action3+" "+p_action2+p_actionLabel+" "+p_actionLabel4+" "+p_actionLabel3+" "+p_actionLabel2+" "+p_actionHead+" "+p_actionHead4+" "+p_actionHead3+" "+p_actionHead2+" "+p_actionTag+" "+p_actionTag4+" "+p_actionTag3+" "+p_actionTag2+" "+actionSizeWithoutIdleAndEnd+"	"+guessedTreeString);			
			pw_out_actionTag.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_actionTag+" "+p_actionTag4+" "+p_actionTag3+" "+p_actionTag2+" "+actionSizeWithoutIdleAndEnd+"	"+guessedTreeString);			
			
			//action+actionLabel action+actionLabel+actionTag action+actionLabel+label+actionTag actionLabel+actonHead+actionTag actionHead actionTag
//			pw_out_action.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_action+" "+p_action4+" "+p_action3+" "+p_action2+" "+p_actionLabel+" "+p_actionLabel4+" "+p_actionLabel3+" "+p_actionLabel2+" "+actionSizeWithoutIdleAndEnd+"	"+guessedTreeString);			
//			pw_out_label.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_action+" "+p_action4+" "+p_action3+" "+p_action2+" "+p_actionLabel+" "+p_actionLabel4+" "+p_actionLabel3+" "+p_actionLabel2+" "+" "+p_actionTag+" "+p_actionTag4+" "+p_actionTag3+" "+p_actionTag2+" "+actionSizeWithoutIdleAndEnd+"	"+guessedTreeString);			
//			pw_out_head.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_action+" "+p_action4+" "+p_action3+" "+p_action2+" "+p_actionLabel+" "+p_actionLabel4+" "+p_actionLabel3+" "+p_actionLabel2+" "+p_label+" "+p_label4+" "+p_label3+" "+p_label2+" "+p_actionTag+" "+p_actionTag4+" "+p_actionTag3+" "+p_actionTag2+" "+actionSizeWithoutIdleAndEnd+"	"+guessedTreeString);			
//			pw_out_tag.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_actionLabel+" "+p_actionLabel4+" "+p_actionLabel3+" "+p_actionLabel2+" "+p_actionHead+" "+p_actionHead4+" "+p_actionHead3+" "+p_actionHead2+" "+p_actionTag+" "+p_actionTag4+" "+p_actionTag3+" "+p_actionTag2+" "+actionSizeWithoutIdleAndEnd+"	"+guessedTreeString);			
//			pw_out_actionHead.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_actionHead+" "+p_actionHead4+" "+p_actionHead3+" "+p_actionHead2+" "+actionSizeWithoutIdleAndEnd+"	"+guessedTreeString);			
//			pw_out_actionTag.println(f_tripple.v1+" "+f_tripple.v2+" "+f_tripple.v3+" "+f_tripple.v4+"	"+item.score+" "+p_actionTag+" "+p_actionTag4+" "+p_actionTag3+" "+p_actionTag2+" "+actionSizeWithoutIdleAndEnd+"	"+guessedTreeString);			
			
			
			pw_out_action.flush();
			pw_out_label.flush();
			pw_out_head.flush();
			pw_out_tag.flush();
			pw_out_actionHead.flush();
			pw_out_actionTag.flush();
			///////////////////////////////////////////////////////////////////////////////////////////
			
			double[] nineGrams=new double[9];
			nineGrams[0]=item.score;
			nineGrams[1]=p_action;
			nineGrams[2]=p_label;
			nineGrams[3]=p_head;
			nineGrams[4]=p_tag;
			nineGrams[5]=p_actionLabel;
			nineGrams[6]=p_actionHead;
			nineGrams[7]=p_actionTag;
			nineGrams[8]=f_tripple.v1;
			return new doubleGram(nineGrams);
		}
}
