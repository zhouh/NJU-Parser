package edu.nju.nlp.parser.constituent.tss;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import edu.nju.nlp.main.Parser.Options.LMType;
import edu.nju.nlp.online.alg.Predictor;
import edu.nju.nlp.online.types.Alphabet;
import edu.nju.nlp.online.types.FeatureVector;
import edu.nju.nlp.online.types.Features;
import edu.nju.nlp.online.types.Instance;
import edu.nju.nlp.online.types.MultiHashAlphabet;
import edu.nju.nlp.online.types.Prediction;
import edu.nju.nlp.parser.transition.ConAction;
import edu.nju.nlp.parser.types.ConstituentLabel;
import edu.nju.nlp.parser.types.Tag;
import edu.nju.nlp.parser.types.Word;

/**
 * the label predictor class
 * predicate the label of the classification
 * return the prediction consists of K-best Labels
 * 
 * @author Hao Zhou
 *
 */
public class ConParsePredictor extends Predictor{
	

	public double[] sigma;	//used for weighted confidence
	public ConParseDataManager manager;
	int K;	//get the K-best result
	int beam;
	public boolean m_bAssignPosFollowsShift=false;
	public boolean m_beL2R;	//scan from left to right
	boolean earlyUpdate;
	boolean bAvgParams;
	ConstituentParser parser;
	
	public ConParsePredictor(int dimensions){
		super(dimensions);
		this.manager=new ConParseDataManager();
		this.earlyUpdate = true;
		this.m_beL2R = true;
		K = 1;
		this.beam=16;
		this.bAvgParams=true;
		parser=new ConstituentParser(true);
	}
	
	/**
	 * constructor 
	 * the dictionary is new constructed and all other parameter
	 * are get from the caller
	 * 
	 * @param dimensions	the initial size of the weight array
	 * @param earlyUpdate	the manager to extract feature 
	 * @param m_beL2R	boolean to decode from left to right
	 * @param m_dic new constructor a dictionary	the dictionary of pos-tags and constituent label  
	 * @param k		get the K-best label from the instance	
	 * @param beam	the size of the beam for search
	 */
	public ConParsePredictor(int dimensions,ConParseDataManager manager,boolean earlyUpdate, 
			boolean m_beL2R, int k, int beam, boolean bAvgParams,boolean bTrain) {
		super(dimensions);
		this.manager=manager;
		this.earlyUpdate = earlyUpdate;
		this.m_beL2R = m_beL2R;
		K = k;
		this.beam=beam;
		this.bAvgParams=bAvgParams;
		parser=new ConstituentParser(bTrain);
	}
	
	/**
	 * the constructor in test
	 * used with a load function
	 * the dimensions will be 0, and the weight will be load from load function
	 * the manager's Alphabet will also be loaded in load function
	 */
	public ConParsePredictor(int dimensions, boolean m_beL2R, int beam, boolean bAvgParams,boolean bTrain) {
		super(dimensions);
		this.manager=new ConParseDataManager();
		this.earlyUpdate = false;
		this.m_beL2R = m_beL2R;
		K = 1;	//in test the K-best is only one, the test result.
		this.beam=beam;
		this.bAvgParams=bAvgParams;
		parser=new ConstituentParser(bTrain);
	}
	
	/**
	 * constituent parser class
	 * 
	 * 
	 * */
	private final class ConstituentParser
	{
		final boolean bTrain;
		double[] w;   //权值向量
		final boolean earlyUpdateInParser;
		
		/**
		 * 
		 * */
		ConstituentParser(final boolean bTrain)
		{
			this.bTrain = bTrain;
			earlyUpdateInParser=bTrain&&earlyUpdate;
			//the weight in decoding will be averaged when test but not when train
			w = (!this.bTrain && bAvgParams) ? averageWeights() : weights;
		}
		
		void setWeight(){
			w = (!bTrain && bAvgParams) ? averageWeights() : weights;
		}
		
		
		//z真正的重头戏 parse sentence 真正的工作都在这里做
		Prediction parseSentence(Instance inst, Features feats, int K) 
		{
			/*Some control variable*/
			boolean isAllEnd=false;	// all of the states in the beam are in the end
			boolean beamStillGold=true;	//gold state still in the beam
			
			/* Initial the state */
			ConParseTSSState state=new ConParseTSSState((ConParseInput)inst.getInput());
			ConParseTSSState gState=new ConParseTSSState((ConParseInput)inst.getInput());
			
			//if it's test, the gLabel will be null and the goldTree will be null too !
			ConParseLabel gLabel=(ConParseLabel)inst.getLabel();
			CFGTree goldTree=gLabel==null?null:gLabel.getCfgTree();	//get the instance's label's gold CFG tree
			
			BeamChart chart1=new BeamChart(beam);
			BeamChart chart2=new BeamChart(beam);
			
			chart1.insert(generateItem(state, ConAction.SHIFT, true));	//insert the initial state
		
			while( !isAllEnd && (!earlyUpdateInParser||beamStillGold) ){	//all states end or it's time to early update
				
//				ChartItem bestGen=null;
//				for(ChartItem item:chart1){
//					if(bestGen==null||item.score>bestGen.score)
//						bestGen=item;
//				}
//				if(bestGen.bEnd)
//					break;
				/*
				 * If the best generated state is terminated
				 * return it directly
				 */
				chart2.reset();	//clear the chart2
				
				/*
				 * move the state in the chart1 to next state and
				 * save the newly generated state into chart2
				 */
				MoveNextStateBatch(chart1, chart2, goldTree);	
				
				//swap chart1 and chart2
				//get the allEnd and bGold parameter's value
				BeamChart tmp=chart2;
				chart2=chart1;
				chart1=tmp;
				chart1.setPara();	
				
			    if(beDebug){
			    	
////				ONLY FOR　DEBUG			
			    	int j=0;
			    	for(ChartItem item:chart1){
			    		System.out.println(j+": "+item.curState.getActSeq()+"  gold: "
			    				+item.getGold()+" end:"+item.bEnd()+" Score:"+item.getCurState().getScore());
//					item.fv.sort();
			    		System.out.println("fv:	"+item.fv);
			    		System.out.println("fv score:	"+score(item.fv, w));
			    		j++;
			    	}
			    }
				
				//move the gold state
				gState=moveNextGold(gState, goldTree);
				
				isAllEnd=chart1.allEnd;
				beamStillGold=chart1.gold;
			}
			
//			if(isAllEnd) System.out.println("All-END");
			
			/*
			 * get the predicate feature vector
			 * */
			ChartItem[] preItems=chart1.getKBest(K);	//get but don't remove
			ConParseLabel[] preLabels=items2Labels(preItems);
			
			//create the prediction
			ConParsePrediction prediction=new ConParsePrediction(preLabels);
			
//			if(!Arrays.equals(weights, w)) throw new RuntimeException("The weight vector do not euqal!");
			/*
			 * get the gold feature vector
			 */
			if(goldTree!=null){
				//set the gold feature vector into the instance's label
				//the gold feature vector will be got from instance by the updator 
				ConParseInstance cIns=(ConParseInstance)inst;
				FeatureVector goldfv=getTreeFeature(gState);
				cIns.setLableFeature(goldfv);
//				System.out.println("gold score: "+score(goldfv,w));
//				System.out.println("gold ActList: "+gState.getActSeq());
//				System.out.println("gold fv: "+goldfv);
			}
			
			if(beDebug){
				
////			ONLY FOR　DEBUG
				int j=0;
				for(ChartItem item:preItems){
					System.out.println(j+": "+item.curState.getActSeq()+
							" end:"+item.bEnd()+" Score:"+item.getCurState().getScore());
//				preLabels[j].fv.sort();
					System.out.println("fv:	"+preLabels[j].fv);
					System.out.println("Tree:	"+preLabels[j].cfgTree.toString());
					j++;
				}
				ConParseInstance cIns=(ConParseInstance)inst;
				if(bTrain)
					System.out.println("gold: "+gState.getActSeq()+
						" Score:"+score(cIns.getLabel().getFeatureVectorRepresentation()));
			}
			
			
			
			return prediction;
		}

		
		/**
		 * convert the ChartItem to ConParseLabel for creating ConParsePrediction 
		 * 
		 * @param preItems
		 * @return
		 */
		private ConParseLabel[] items2Labels(ChartItem[] preItems) {

			ConParseLabel[] labels=new ConParseLabel[preItems.length];
			
			for(int i=0;i<preItems.length;i++)
				labels[i]=item2Label(preItems[i]);
			
			return labels;
		}

		private ConParseLabel item2Label(ChartItem chartItem) {

			//get the whole feature vector of the tree. 
			//The tree would not be a complete syntax tree. In training 
			//process the update may be early update
			FeatureVector preFV=getTreeFeature(chartItem.curState);
			
			// In the training module, we don't print the result parsing tree.
			//So set the result parsing tree null directly.
			if(bTrain) return new ConParseLabel(null, preFV, chartItem.getScore());
			
			//The test module, parsing must be got by back forwarding for output result
			return new ConParseLabel(chartItem.getCurState().convert2CFGTree(), preFV, chartItem.getScore());
		}


		/**
		 * get the feature from the tree generated from the 
		 * current state to the initial state
		 * All the feature vector will be insert into the Alphabet
		 * 
		 * But extract feature from the state directly do not equal 
		 * lazy expansion, So the state is the last state and the 
		 * action is the current state's action
		 * 
		 * @param state
		 * @return
		 */
		private FeatureVector getTreeFeature(ConParseTSSState state) {

			FeatureVector retval=new FeatureVector(-1, -1, null);
			ConParseTSSState current=state;
			if(current.statePtr==null) return retval;
			ConParseTSSState last=current.statePtr;
			//back forward to the initial state
			//get every state's feature vector in the back forward path
			while(last!=null){
				retval=manager.createFeatureVector(last, current.action, retval, true);
				current=last;
				last=current.statePtr;
			}
			return retval;
		}

		/**
		 * move the state in chart1 to next state 
		 * and sort the newly state by heap sort with score priority
		 * 
		 * @param chart1
		 * @param chart2
		 * @param goldTree
		 */
		@SuppressWarnings("unused")
		private void MoveNextState(BeamChart chart1, BeamChart chart2, CFGTree goldTree) {
			
			for(ChartItem item:chart1){	//for every item in the chart
				
				ConParseTSSState state=item.getCurState();
				ConAction goldAction=state.getGoldAction(goldTree);	//get the gold action of the state
				
//				DEBUG				
//				if(state.gold)
//					System.out.println();
//				ArrayList<ConAction> validActions=state.getNextActions();
				
				for(ConAction action:state.getNextActions()){
					
					boolean bGoldAction=goldAction.shallowEquals(action);	//be gold action?
					
					chart2.insert(generateItem(state, action, bGoldAction));
				}
				
			}
			if(chart2.size()==0)
				throw new RuntimeException("No next valid state!");
			
			/*
			 * move the states and actions in the beam 
			 * to the next state 
			 */
			for(ChartItem item:chart2){
				moveNext(item);
			}
		}
		
		private void MoveNextStateBatch(BeamChart chart1, BeamChart chart2, CFGTree goldTree) {
			
			for(ChartItem item:chart1){	//for every item in the chart
				
				ConParseTSSState state=item.getCurState();
				ConAction goldAction=state.getGoldAction(goldTree);	//get the gold action of the state
				
////				DEBUG				
//				if(state.gold)
//					System.out.println();
//				ArrayList<ConAction> validActions=state.getNextActions();
				
				ArrayList<ConAction> actions=state.getNextActions();
				FeatureVector[] fvs=manager.fHandler.createFeatureBatch(state.atomic, actions,false);
				
				for(int i=0;i<actions.size();i++){
					
					ConAction action=actions.get(i);
					boolean bGoldAction=goldAction.shallowEquals(action);	//be gold action?
					chart2.insert(generateItemWithoutExtractFeature(state, action,fvs[i], bGoldAction));
				}
				
			}
			if(chart2.size()==0)
				throw new RuntimeException("No next valid state!");
			
			/*
			 * move the states and actions in the beam 
			 * to the next state 
			 */
			for(ChartItem item:chart2){
				moveNext(item);
			}
		}

		/**
		 * move the state in item to next state 
		 * with the action in the state
		 * 
		 * @param item
		 */
		private void moveNext(ChartItem item) {

			ConParseTSSState state=item.getCurState();
			ConAction action=item.getAction();
			boolean beGold=item.getGold();
			double score=item.getScore();
			
			state=state.move(action, beGold, manager);
			state.setScore(score);
			item.setCurState(state);
		}

		/**
		 * generate the new ChartItem
		 * 
		 * get feature with the current state and action
		 * calculate the score with the feature vector 
		 * combine the state , score and feature vector to a new ChartItem
		 * 
		 * @param state
		 * @param action
		 * @return
		 */
		public ChartItem generateItem(ConParseTSSState state, ConAction action, boolean goldAction){ 
			
			//get the local feature vector
			FeatureVector fv=manager.createFeatureVector(state, action, new FeatureVector(-1, -1, null), false);
			double score=score(fv, w)+state.getScore();	//get the global action score
			return new ChartItem(state, action, fv, goldAction, score);
		}
		
		public ChartItem generateItemWithoutExtractFeature(ConParseTSSState state, ConAction action,FeatureVector fv, boolean goldAction){
			double score=score(fv, w)+state.getScore();	//get the global action score
			return new ChartItem(state, action, fv, goldAction, score);
		}
		
		/**
		 * get gold action from current state with 
		 * with different shift-reduce scan direction may have different action 
		 * TODO: the right to left and left to right scan are deleted in this version
		 * 
		 * @param s current state
		 * @param gTree gold constituent tree
		 * @return the pair of gold action and gold state after the gold action 
		 * */
		public ConParseTSSState moveNextGold(ConParseTSSState s, CFGTree gTree)
		{
			if(gTree==null) return s;
			s=s.moveNextGold(gTree,manager);	//move to next state, 
			
			return s;
		}
		
		public String getGoldActionSequence(ConParseInstance conParseInstance,LMType lm) {
			
			String actionSeuquence="";
			
			ConParseTSSState gState=new ConParseTSSState((ConParseInput)conParseInstance.getInput());
			ConParseLabel gLabel=(ConParseLabel)conParseInstance.getLabel();
			CFGTree goldTree=gLabel==null?null:gLabel.getCfgTree();	//get the instance's label's gold CFG tree
			
			
			gState=moveNextGold(gState, goldTree);
			ConAction action=gState.action;
			
			if(lm==LMType.actionLabel){
				do{
					if(action.isShiftAction()){
						
						actionSeuquence+=" "+action.toString()+"-"+gState.input.tags.get(gState.node.lexical_head);
					}
					else actionSeuquence+=" "+action.toString();
				
					gState=moveNextGold(gState,goldTree);	//move to next state, 
					action=gState.action;
				}
				while( !(action.isEndAction()||action.isIDLEAction()) );
			}
			else if(lm==LMType.actionHead){
				
				do{
					String[] tokens=action.toString().split("-");
					actionSeuquence+=" "+tokens[0]+"-"+gState.input.words.get(gState.node.lexical_head).toLowerCase();
					gState=moveNextGold(gState,goldTree);	//move to next state, 
					action=gState.action;
					
					
				}
				while( !(action.isEndAction()||action.isIDLEAction()) );
			}
			else if(lm==LMType.actionTag){
				do{
					String[] tokens=action.toString().split("-");
					actionSeuquence+=" "+tokens[0]+"-"+gState.input.tags.get(gState.node.lexical_head);
					gState=moveNextGold(gState,goldTree);	//move to next state, 
					action=gState.action;
					
				}
				while( !(action.isEndAction()||action.isIDLEAction()) );
			}
			else if(lm==LMType.action){
				do{
					String actionString=action.toString();
					String[] tokens=actionString.split("-");	//segment BRR-NP
					actionSeuquence+=" "+tokens[0];
					gState=moveNextGold(gState,goldTree);	//move to next state, 
					action=gState.action;
				}
				while( !(action.isEndAction()||action.isIDLEAction()) );
			}
			else if(lm==LMType.label){
				do{
					String[] tokens=action.toString().split("-");
					String labelOrTag;	//get the label tag
					
					//S-S-NN or BR-NP
					if(action.isShiftAction()){
						
						labelOrTag=gState.input.tags.get(gState.node.lexical_head);
					}
					else labelOrTag= tokens[tokens.length-1];
					
					actionSeuquence+=" "+labelOrTag;
				
					gState=moveNextGold(gState,goldTree);	//move to next state, 
					action=gState.action;
				}
				while( !(action.isEndAction()||action.isIDLEAction()) );
			}
			else if(lm==LMType.head){
				do{
					actionSeuquence+=" "+gState.input.words.get(gState.node.lexical_head).toLowerCase();
					gState=moveNextGold(gState,goldTree);	//move to next state, 
					action=gState.action;
				}
				while( !(action.isEndAction()||action.isIDLEAction()) );
			}
			else if(lm==LMType.tag){
				do{
					actionSeuquence+=" "+gState.input.tags.get(gState.node.lexical_head);
					gState=moveNextGold(gState,goldTree);	//move to next state, 
					action=gState.action;
				}
				while( !(action.isEndAction()||action.isIDLEAction()) );
			}
			
			
			
			return actionSeuquence;
		}
	}

	@Override
	public Prediction decode(Instance inst, Features feats) {
		return decode(inst, feats, K);
	}

	/**
	 * the main decode module
	 * return the prediction 
	 */
	@Override
	public Prediction decode(Instance inst, Features feats, int K) {
		
		return parser.parseSentence(inst, feats, K);
	}
	
	public String getActionLMSequence(ConParseInstance conParseInstance,LMType lm){
		return parser.getGoldActionSequence(conParseInstance, lm);
	}
	
	public void saveModel(String file) throws Exception {
		System.out.print("Saving model ... ");
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
				file));
		out.writeObject(weights);
		out.writeObject(avg_weights);
		out.writeObject(this.iStep);
		out.writeObject(manager.getDataAlphabet());
		out.writeObject(ConstituentLabel.getAlphabet());
		out.writeObject(Tag.getAlphabet());
		out.writeObject(Word.getAlphabet());
		ConAction.writeObject(out);
		out.close();
		System.out.println("Save model done.");
	}

	public void loadModel(String file) throws Exception {
		System.out.print("Loading model ... ");
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		weights = (double[]) in.readObject();
		avg_weights = (double[]) in.readObject();
		iStep=(Integer)in.readObject();
		ConParseDataManager.setDataAlphabet((MultiHashAlphabet) in.readObject());
		ConstituentLabel.setAlphabet((Alphabet)in.readObject());
		Tag.setAlphabet((Alphabet)in.readObject());
		Word.setAlphabet((Alphabet)in.readObject());
		ConAction.readObject(in);
		in.close();
		parser.setWeight();
		System.out.println("Load model done.");
	}
	
	public ConParseDataManager getManager() {
		return manager;
	}
}
