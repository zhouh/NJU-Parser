package edu.nju.nlp.online.alg;

import edu.nju.nlp.online.types.FeatureVector;
import edu.nju.nlp.online.types.Features;
import edu.nju.nlp.online.types.Instance;
import edu.nju.nlp.online.types.Prediction;

//public abstract class Predictor {
//
//	public double[] weights;
//	public double[] avg_weights;
//
//	//���ά�� ��������Ȩֵ���� ��ʼֵΪ0
//	public Predictor(int dimensions) {
//		weights = new double[dimensions];
//		avg_weights = new double[dimensions];
//		for (int i = 0; i < dimensions; i++) {
//			weights[i] = 0.0;
//			avg_weights[i] = 0.0;
//		}
//	}
//
//	public void averageWeights(int factor) {
//		for (int i = 0; i < avg_weights.length; i++)
//			weights[i] = avg_weights[i] / factor;
//	}
//
//	public double score(FeatureVector fv) {
//		return score(fv, this.weights);
//	}
//
//	public double score(FeatureVector fv, double[] weightsVector) {
//		double score = 0.0;
//		for (FeatureVector curr = fv; curr != null; curr = curr.next) {
//			if (curr.index >= 0)
//				score += weightsVector[curr.index] * curr.value;
//		}
//		if ( Double.isInfinite(score) || Double.isNaN(score )){
//			System.out.println("Score is Inf or NaN");
//		}
//		return score;
//	}
//
//	public abstract Prediction decode(Instance inst, Features feats);
//
//	public abstract Prediction decode(Instance inst, Features feats, int K);
//
//}

public abstract class Predictor {

	public double[] weights;
	public double[] avg_weights;
	public int iStep;
	public boolean beDebug=false;
	

	public Predictor(int dimensions) {
		weights = new double[dimensions];
		avg_weights = new double[dimensions];
		for (int i = 0; i < dimensions; i++) {
			weights[i] = 0.0;
			avg_weights[i] = 0.0;
			iStep=1;
		}
	}
	
	public void nextStep(){
		iStep++;
	}
	
	public int getStep(){
		return iStep;
	}

	public void averageWeights(int factor) {
		for (int i = 0; i < avg_weights.length; i++)
			weights[i] = avg_weights[i] / factor;
	}
	
	/**
	 * 
	 * DAUME III (2006) describe a sufficient algorithm for averaged perceptron 
	 * 
	 */
	public double[] averageWeights(){
		
		double[] averageWeight=new double[weights.length];
		
		
		for(int i=0;i<weights.length;i++){
			averageWeight[i]=weights[i]-avg_weights[i]/iStep;
		}
		
		return averageWeight;
	}

	public double score(FeatureVector fv) {
		return score(fv, this.weights);
	}

	public double score(FeatureVector fv, double[] weightsVector) {
		double score = 0.0;
		for (FeatureVector curr = fv; curr != null; curr = curr.next) {
			if (curr.index >= 0)
				score += weightsVector[curr.index] * curr.value;
//			System.out.print(weightsVector[curr.index]+"	");
		}
//		System.out.println();
		
		if ( Double.isInfinite(score) || Double.isNaN(score )){
			System.out.println("Score is Inf or NaN");
		}
		return score;
	}
	
	public void beigiDebug(){
		beDebug=true;
	}

	public void endDebug(){
		beDebug=false;
	}
	
	public abstract Prediction decode(Instance inst, Features feats);

	public abstract Prediction decode(Instance inst, Features feats, int K);

}
