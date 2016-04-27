package edu.nju.nlp.parser.constituent.tss;

import java.util.ArrayList;

import edu.nju.nlp.online.types.FeatureVector;
import edu.nju.nlp.online.types.MultiHashAlphabet;
import edu.nju.nlp.parser.transition.ConAction;

/**
 * feature extractor and feature Alphabet add class
 * 
 * @author Hao Zhou
 * */
public abstract class ConParseFeatureHandler {

	/*
	 * parameters
	 */
	static MultiHashAlphabet dataAlphabet;
	protected static final String SEP = "-";
	/**
	 * getter/setter and constructor
	 */
	public ConParseFeatureHandler(MultiHashAlphabet dataAlphabet) {
		ConParseFeatureHandler.dataAlphabet=dataAlphabet;
	}

	/*
	 * abstract section
	 */
	
	public abstract AtomicFeatures getAtomicFeatures(ConParseTSSState state);

	public abstract FeatureVector getFeatures(AtomicFeatures atomic, ConAction act, FeatureVector v, boolean bAdd);  //从state和action中获得特征
	
//	protected static FeatureVector addFeature(FeatureVector fv,int featureID,int actionID,long feat,double value,boolean bAdd) {
//		fv=fv.add(featureID, actionID, feat, value, dataAlphabet, bAdd);
//		return fv;
//	}

	public abstract FeatureVector[] createFeatureBatch(AtomicFeatures atomic,
			ArrayList<ConAction> actions, boolean addIfNotFound) ;
	
	
	
}
