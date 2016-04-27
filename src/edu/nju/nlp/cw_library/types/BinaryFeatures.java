/*
 * The source file is get from struct.sequence
 * I'm not sure whether the change would make mistake or not  
 * 
 * */
package edu.nju.nlp.cw_library.types;

import edu.nju.nlp.online.types.FeatureVector;
import edu.nju.nlp.online.types.Features;

public class BinaryFeatures implements Features {

	private FeatureVector featureVector;

	public BinaryFeatures(FeatureVector _fvs) {
		this.featureVector = _fvs;
	}

	public FeatureVector getFeatureVector() {
		return featureVector;
	}

}
