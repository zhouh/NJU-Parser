package edu.nju.nlp.parser.constituent.tss;

import java.util.ArrayList;

import edu.nju.nlp.commons.dict.Dictionary;
import edu.nju.nlp.parser.transition.ConAction;

/**
 * check the current state's validation
 * And return the validation
 * TODO : maybe no bug, but under debuging
 * @author Hao Zhou
 * */
public class Rules {

	public Dictionary m_dic;	//the dictionary here is a simple dic TODO be more functional 
	final int UNARY_MOVES=3;
	
	public Rules(){
		m_dic=new Dictionary();
	}                                                  
	
	private String extractConstituent(String tmpLabel){
		return tmpLabel.endsWith("*")?tmpLabel.substring(0, tmpLabel.length()-1):tmpLabel;
	}
	
	public ArrayList<ConAction> getActions(ConParseTSSState state){
		ArrayList<ConAction> actions=new ArrayList<ConAction>();
		
//		DEBUG
//		if(state.gold)
//			System.out.print("");
		
		int stackSize=state.stackSize();
		int length=state.input.sentLen;
		
		if(state.hasEnd()) {
			actions.add(ConAction.IDLE);	//if the state has already been end , just return end state!
			return actions;
		}
		
		//add the end action
		if(state.isEnd()) actions.add(ConAction.END_STATE);
		
		//add the shift action
		if(state.currentWord<length){
			if(stackSize>0&&state.node.temp&&state.node.headBeLeft()==false){
				
			}
			else{
				actions.add(ConAction.SHIFT);                                
			}
		}
		
		//add the binary reduce action
		if(stackSize>1){
			getBinaryRules(state,actions);
		}
		//add the unary reduce
		if(stackSize>=1&&state.unaryReduceSize()<UNARY_MOVES&&!state.node.temp)
			getUnaryRules(state,actions);
		
		return actions;
	}

	private void getUnaryRules(ConParseTSSState state,
			ArrayList<ConAction> actions) {

		ConParseTSSStateNode child=state.node;
		for(String constituent:m_dic.getConLabels()){
			//the unary reduce will not contain a temporary label so NP* will not exists here.
			//so the constituent could be equal directly
			if(!constituent.equals(child.constituent))
				actions.add(ConAction.getLabeledReduceAction(false,false,false, constituent));
		}
		
	}

	private void getBinaryRules(ConParseTSSState state,
			ArrayList<ConAction> actions) {

		int stack_size=state.stackSize();
		final ConParseTSSStateNode right=state.node;
		final ConParseTSSStateNode left=state.stackPtr.node;
		int sentSize=state.input.sentLen;
	    // the normal method
		boolean prev_temp = stack_size>2 ? state.stackPtr.stackPtr.node.temp:false;	//the third node's temporary in the stack
		      for (String constituent:m_dic.getConLabels()) {
		         for (int i=0; i<=1; ++i) {
		        	 boolean head_left = i==0?false:true;
		            for (int j=0; j<=1; ++j) {
		               boolean temporary = j==0?false:true;
		               if ( ( !left.temp || !right.temp ) &&
		                     ( !(stack_size==2 && state.currentWord==sentSize) || !temporary ) &&
		                     ( !(stack_size==2) || (!temporary||head_left) ) &&
		                     ( !(prev_temp && state.currentWord==sentSize) || !temporary ) &&
		                     ( !(prev_temp) || (!temporary||head_left) ) &&
		                     ( !left.temp || (head_left&&constituent.equals(extractConstituent(left.constituent))) ) &&
		                     ( !right.temp || (!head_left&&constituent.equals(extractConstituent(right.constituent))) ) //&&
//		                     ( !temporary || CConstituent::canBeTemporary(constituent) ) 
		                 ) {
		                        actions.add(ConAction.getLabeledReduceAction(true,head_left,temporary, constituent));
		                  }
		               } // for j
		            } // for i
		         } // for constituent
	}
	
}
