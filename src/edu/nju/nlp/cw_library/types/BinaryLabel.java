/*
 * The source file is get from struct.sequence
 * I'm not sure whether the change would make mistake or not  
 * 
 * */

package edu.nju.nlp.cw_library.types;

import edu.nju.nlp.online.types.FeatureVector;
import edu.nju.nlp.online.types.Label;

public class BinaryLabel implements Label {
	public double tag;
	public FeatureVector fv;

	public BinaryLabel(double tag, FeatureVector fv) {
		this.tag = tag;
		this.fv = fv;
	}

	public FeatureVector getFeatureVectorRepresentation() {
		return fv;
	}

	// evaluates a prediction against this label
	public double loss(Label pred) {
		BinaryLabel bLabel = (BinaryLabel) pred;
		return (double) ((bLabel.tag == this.tag) ? 0 : 1);
	}

	public String toString() {
		return (String.valueOf(tag));

	}

}
