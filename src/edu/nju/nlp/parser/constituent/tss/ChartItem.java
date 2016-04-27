package edu.nju.nlp.parser.constituent.tss;

import edu.nju.nlp.online.types.FeatureVector;
import edu.nju.nlp.parser.transition.ConAction;

/**
 * the item used in transition by beam search decode
 * from one state to another state need calculate much more unnecessary 
 * But with the Chart Item could reduce a lot calculation.
 * 
 * @author Hao Zhou
 *
 */
public class ChartItem {

	public ConParseTSSState curState;
	public ConAction action;
	public double score;
	public boolean bEnd;
	public boolean gold;
	public FeatureVector fv;

	public ChartItem(ConParseTSSState curState, ConAction action, FeatureVector fv, boolean goldAction, double score) {
		this.curState = curState;
		this.action = action;
		this.score = score;
		this.fv=fv;
		bEnd=( action==ConAction.IDLE || action==ConAction.END_STATE );
		gold=curState.gold&&goldAction;
	}
	
	public FeatureVector getFv() {
		return fv;
	}
	
	public void setFv(FeatureVector fv) {
		this.fv = fv;
	}

	public ConParseTSSState getCurState() {
		return curState;
	}
	
	public void setCurState(ConParseTSSState curState) {
		this.curState = curState;
	}
	public ConAction getAction() {
		return action;
	}
	public void setAction(ConAction action) {
		this.action = action;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public boolean getGold() {
		return gold;
	}
	
	public boolean bEnd() {
		return bEnd;
	}
	
	
}
