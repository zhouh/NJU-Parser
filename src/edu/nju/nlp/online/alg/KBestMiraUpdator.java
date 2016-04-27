package edu.nju.nlp.online.alg;

import edu.nju.nlp.online.types.FeatureVector;
import edu.nju.nlp.online.types.Features;
import edu.nju.nlp.online.types.Instance;
import edu.nju.nlp.online.types.Label;
import edu.nju.nlp.online.types.Prediction;
import edu.nju.nlp.solver.QPSolver;

public class KBestMiraUpdator implements OnlineUpdator {

	public int K;
	private double C;
	
	public KBestMiraUpdator(int K) {
		this.K = K;
		this.C = Double.POSITIVE_INFINITY;
	}
	
	public KBestMiraUpdator(int K, double c) {
		this.K = K;
		this.C = c;
	}
	
	public void update(Instance inst, Features feats, Predictor predictor,
			double avg_upd) {

		Prediction pred = predictor.decode(inst, feats, K);
		Label[] labels = new Label[K];
		int k_new = 0;
		for (int k = 0; k < K; k++) {
			labels[k] = pred.getLabelByRank(k);
			if (labels[k] == null)
				break;
			k_new = k + 1;
		}

		FeatureVector corr_fv = inst.getLabel()
				.getFeatureVectorRepresentation();

		FeatureVector[] guessed_fvs = new FeatureVector[k_new];
		double[] b = new double[k_new];
		double[] lam_dist = new double[k_new];
		FeatureVector[] dist = new FeatureVector[k_new];

		for (int k = 0; k < k_new; k++) {
			guessed_fvs[k] = labels[k].getFeatureVectorRepresentation();
			b[k] = inst.getLabel().loss(labels[k]);
			lam_dist[k] = predictor.score(corr_fv)
					- predictor.score(guessed_fvs[k]);
			b[k] -= lam_dist[k];
			dist[k] = FeatureVector.getDistVector(corr_fv, guessed_fvs[k]);
		}

		double[] alpha = QPSolver.hildreth(dist, b, C);

		double[] weights = predictor.weights;
		double[] avg_weights = predictor.avg_weights;

		FeatureVector fv = null;
		for (int k = 0; k < k_new; k++) {
			fv = dist[k];
			for (FeatureVector curr = fv; curr != null; curr = curr.next) {
				if (curr.index < 0)
					continue;
				weights[curr.index] += alpha[k] * curr.value;
				avg_weights[curr.index] += avg_upd * alpha[k] * curr.value;
			}
		}
	}
}
