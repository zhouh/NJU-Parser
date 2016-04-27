package edu.nju.nlp.parser.constituent.tss;

import edu.nju.nlp.online.types.FeatureVector;
import edu.nju.nlp.online.types.Features;

public class ConParseFeatures implements Features {

	private FeatureVector[] fv;
	
	public ConParseFeatures(FeatureVector[] fv) {
		this.fv=fv;
	}
	public FeatureVector getFeatureVector(int n) {
		return fv[n];
	}
}
