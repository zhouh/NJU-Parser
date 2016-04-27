/*
 * The source file is get from struct.sequence
 * I'm not sure whether the change would make mistake or not  
 * 
 * */
package edu.nju.nlp.cw_library.types;

import java.io.IOException;
import java.io.ObjectInputStream;

import edu.nju.nlp.online.types.FeatureVector;
import edu.nju.nlp.online.types.Features;
import edu.nju.nlp.online.types.Input;
import edu.nju.nlp.online.types.Instance;
import edu.nju.nlp.online.types.Label;

public class BinaryInstance implements Instance {

	protected BinaryLabel label;
	protected BinaryFeatures featureVector;

	public BinaryInstance(double _label, FeatureVector fv) {
		featureVector = new BinaryFeatures(fv);
		label = new BinaryLabel(_label, fv);
	}

	public Input getInput() {
		throw new RuntimeException("Not supported");
	}

	public Label getLabel() {
		return label;
	}

	public Features getFeatures() {
		return featureVector;
	}

	public Features getFeatures(ObjectInputStream in) throws IOException {
		throw new RuntimeException("Not supported");
	}
}
