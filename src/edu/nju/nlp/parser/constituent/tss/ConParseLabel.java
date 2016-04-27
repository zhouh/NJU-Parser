//NJU-Parser/src/edu/nju/nlp/parser/constituent/ConParseLabel

package edu.nju.nlp.parser.constituent.tss;

import edu.nju.nlp.online.types.FeatureVector;
import edu.nju.nlp.online.types.Label;

/**
 * the constituent parsing label class
 * implements the base class - label
 * act as the correct or predicated label
 * 
 * @author Hao Zhou
 * */
public class ConParseLabel implements Label {

	public CFGTree cfgTree;	//CFG tree
	public FeatureVector fv;	
	public double pro;	//the probability of the label
	
	/**
	 * constructor function
	 * */
	public ConParseLabel(CFGTree cfgTree,FeatureVector fv,double pro){
		this.cfgTree=cfgTree;
		this.fv=fv;
		this.pro=pro;
	}
	
	/**
	 * Constructor 
	 * with only CFG tree and feature vector , pro is default null set
	 * */
	public ConParseLabel(CFGTree cfgTree) {
		this.cfgTree=cfgTree;
		this.fv=null;
		this.pro=0.0;
		
	} 
	
	public CFGTree getCfgTree() {
		return cfgTree;
	}

	/**
	 * return the feature vector of the label 
	 * */
	@Override
	public FeatureVector getFeatureVectorRepresentation() {
		return fv;
	}

	/**
	 * return the loss distance of the predicate label and this label
	 * @param pred the under compared label
	 * @return loss distance  
	 * */
	@Override
	public double loss(Label pred) {
		return hammingDistance(pred);
	}

	/**
	 * return the hamming distance of the two label
	 * compare the label's CFGTree,recursively compare root
	 * node inside 
	 * */
	public int hammingDistance(Label pred) {   

		ConParseLabel conPred=(ConParseLabel)pred;
		CFGTree predTree=conPred.cfgTree;
		if(!this.cfgTree.nodes.get(cfgTree.root).equals(predTree.nodes.get(predTree.root)))
				return 1;
		
		return 0;
	}
	
	/**
	 * set the label's feature vector 
	 * In the constructor, the feature vector is not set
	 * */
	public void setFeature(FeatureVector fv){
		this.fv=fv;
	}
}
