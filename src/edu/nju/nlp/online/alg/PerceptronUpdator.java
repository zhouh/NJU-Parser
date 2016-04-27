package edu.nju.nlp.online.alg;

import edu.nju.nlp.online.types.FeatureVector;
import edu.nju.nlp.online.types.Features;
import edu.nju.nlp.online.types.Instance;
import edu.nju.nlp.online.types.Label;
import edu.nju.nlp.online.types.Prediction;

public class PerceptronUpdator implements OnlineUpdator {

	public void update(Instance inst, Features feats, Predictor predictor,
			double avg_upd) {

		Prediction pred = predictor.decode(inst, feats);
		Label label = pred.getBestLabel();
		FeatureVector guessed_fv = label.getFeatureVectorRepresentation();
		FeatureVector corr_fv = inst.getLabel()
				.getFeatureVectorRepresentation();

//		corr_fv.sort();
//		double corr_score=predictor.score(corr_fv);
//		double guessed_score=predictor.score(guessed_fv);
//		System.out.println("guessed: "+guessed_score);
//		System.out.println("guessed fv : "+guessed_fv);
//		System.out.println("correct: "+corr_score);
//		System.out.println("correct fv: "+corr_fv);
		if (predictor.score(corr_fv) > predictor.score(guessed_fv)) {
			System.out.print("+");
//			throw new RuntimeException("Can't choose the best output!");
			return;
		}

		double[] weights = predictor.weights;
		double[] avg_weights = predictor.avg_weights;

		for (FeatureVector curr = corr_fv; curr.next != null; curr = curr.next) {
			if (curr.index >= 0) {
				weights[curr.index] += curr.value;
				avg_weights[curr.index] += avg_upd * curr.value;
			}
		}

		for (FeatureVector curr = guessed_fv; curr.next != null; curr = curr.next) {
			if (curr.index >= 0) {
				weights[curr.index] -= curr.value;
				avg_weights[curr.index] -= avg_upd * curr.value;
			}
		}

	}

}
