package edu.nju.nlp.parser.constituent.tss;

import edu.nju.nlp.cw_library.tools.MathTools;
import edu.nju.nlp.online.types.FeatureVector;



public class CWConParsePredictor extends ConParsePredictor{

	public CWConParsePredictor(int dimensions, double initSigmaValue) {
		
		super(dimensions);
		sigma = new double[dimensions];

		for (int i = 0; i < dimensions; i++) {
			sigma[i] = initSigmaValue;
		}
	}

	

	public double getMeanOfSigma() {
		return MathTools.calculateMean(sigma);
	}

	public void setModifiedWeightsVector(FeatureVector newInstFv) {
		// TODO Auto-generated method stub
		
	}


}
