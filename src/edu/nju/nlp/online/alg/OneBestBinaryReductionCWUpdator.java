package edu.nju.nlp.online.alg;

import edu.nju.nlp.arow.DiagonalKalmanDropUpdater;
import edu.nju.nlp.arow.DiagonalKalmanExactUpdater;
import edu.nju.nlp.arow.DiagonalKalmanProjectUpdater;
import edu.nju.nlp.cw_library.algorithms.binary.cw_updaters.DiagonalUpdater;
import edu.nju.nlp.cw_library.algorithms.binary.cw_updaters.DiagonalVarianceDropUpdater;
import edu.nju.nlp.cw_library.algorithms.binary.cw_updaters.DiagonalVarianceProjectUpdater;
import edu.nju.nlp.cw_library.tools.Tools;
import edu.nju.nlp.cw_library.types.BinaryInstance;
import edu.nju.nlp.cw_library.types.BinaryLabel;
import edu.nju.nlp.cw_library.types.WeightVector;
import edu.nju.nlp.online.types.FeatureVector;
import edu.nju.nlp.online.types.Features;
import edu.nju.nlp.online.types.Instance;
import edu.nju.nlp.online.types.Label;
import edu.nju.nlp.online.types.Prediction;
import edu.nju.nlp.parser.constituent.tss.CWConParsePredictor;

/**
 * This class implements a multi-class top-1 classifier by reducing the problem
 * to binary classification. The correct label is a positive example whereas the
 * top predicted incorrect label is a negative example. The effect is that the
 * learning constraint is enforced for the correct label and top predicted
 * label, but not every predicted label.
 * 
 * @author Mark Dredze
 * 
 */
public class OneBestBinaryReductionCWUpdator implements OnlineUpdator {

	protected double _a = 1;
	protected int _negative_index = 0;
	protected int _positive_index = 1;
	protected double _phi = Tools.DEFAULT_PHI;
	protected boolean _growable;
	String cwUpdaterType;
	DiagonalUpdater updater;

	// Always use K=2 since we reduce the update problem to binary case
	public int K = 2;

	public OneBestBinaryReductionCWUpdator(String cwUpdaterType, double phi) {
		this.cwUpdaterType = cwUpdaterType;
		/* CW updaters */
		if (cwUpdaterType.equals("diagvardrop")) {
			updater = new DiagonalVarianceDropUpdater();
		} else if (cwUpdaterType.equals("diagvarproj")) {
			updater = new DiagonalVarianceProjectUpdater();
		}
		/* AROW updaters */
		else if (cwUpdaterType.equals("arowdrop")) {
			updater = new DiagonalKalmanDropUpdater();
		} else if (cwUpdaterType.equals("arowproj")) {
			updater = new DiagonalKalmanProjectUpdater();
		} else if (cwUpdaterType.equals("arowexact")) {
			updater = new DiagonalKalmanExactUpdater();
		}

		_phi = phi;
	}

	

	public void update(Instance inst, Features feats, Predictor _predictor,
			double avg_upd) {

		CWConParsePredictor predictor = (CWConParsePredictor) _predictor;

		// Get the best 2 sequence labels
		//锟接撅拷锟斤拷锟斤拷锟揭筹拷锟斤拷锟斤拷锟斤拷玫锟絣abels 使锟斤拷vertibi锟侥凤拷锟斤拷
		Prediction pred = predictor.decode(inst, feats, K);
		Label[] labels = new Label[K];
		for (int k = 0; k < K; k++) {
			labels[k] = pred.getLabelByRank(k);
			if (labels[k] == null)
				break;
		}

		// Now find the best score incorrect label sequence
		// This is either the top ranked label if it is incorrect
		// or the second ranked label which must be incorrect (since the first
		// is the correct)
		Label bestIncorrectLabel;
		Label correctLabel = inst.getLabel();
		//锟斤拷锟斤拷锟矫碉拷锟斤拷锟斤拷确锟斤拷
		if (correctLabel.loss(labels[0]) == 0) {
			// Top ranked label Loss == 0 --> it is correct --> the best
			// incorrect is the second label
			bestIncorrectLabel = labels[1];    //锟斤拷玫拇锟斤拷锟絣abel
		} else {
			// Top ranked label Loss != 0 --> it is incorrect --> it is the best
			// incorrect label
			bestIncorrectLabel = labels[0];
		}

		// Create a new temporary binary instance with positive label (+1), with
		// the correct feature vector minus the top incorrect feature vector.
		// Later the weights will be trained to indeed predict this instance as
		// positive.
		FeatureVector correctFv = correctLabel.getFeatureVectorRepresentation();
		FeatureVector incorrectFv = bestIncorrectLabel
				.getFeatureVectorRepresentation();

		FeatureVector newInstFv;
		//
		// Given the correct instance and best incorrect instance we create a
		// new positive instance and train the model to classify it correctly.
		//
		newInstFv = FeatureVector.getDistVector(correctFv, incorrectFv);

		// New temporary binary instance with label=1
		BinaryInstance newBinaryInstance = new BinaryInstance(1, newInstFv);

		// Now Train the underlying classifier on this instance.
		WeightVector _mu = new WeightVector(predictor.weights);
		WeightVector _sigma = new WeightVector(predictor.sigma);
		updater.setParameters(newBinaryInstance, _mu, _sigma, this._phi);
		if (updater.shouldUpdate()) {

			double alpha = updater.getAlpha();
			double Y = ((BinaryLabel) newBinaryInstance.getLabel()).tag;

			if (Double.isInfinite(alpha) || Double.isNaN(alpha)) {
				return;
			}
			// Now we can compute the new weights.
			// mu = mu + y * alpha * sigma_x;
			// Only the elements that are on in the feature vector are non-zero
			// here, so only look at those.
			FeatureVector curr;
			FeatureVector sigma_x = updater.getSigmaX();
			sigma_x = FeatureVector.inverse(sigma_x);

			for (curr = newInstFv; curr != null; curr = curr.next, sigma_x = sigma_x.next) {
				if (curr.index < 0)
					continue;

				// This is a non-zero index.
				// For the FeatureVector sigma_x: Assume that the positions
				// correspond to the same positions as the inst fv.
				if (curr.index != sigma_x.index) {
					throw new RuntimeException(
							"Features vector and sigma_x vector indices don't match!");
				}

				int index = curr.index;
				double prevMuValue = updater.getMuValue(index);
				double newMuValue = updater.getNewMuValue(index, Y,
						sigma_x.value);
				if (Double.isInfinite(newMuValue) || Double.isNaN(newMuValue)) {
					continue;
				}
				updater.setMuValue(index, newMuValue);

				// Support for weights averaging
				double muValueDiff = newMuValue - prevMuValue;
				double[] avg_weights = predictor.avg_weights;
				avg_weights[curr.index] += (avg_upd * muValueDiff);
			}
			//
			// Let the predictor know which weights were changed so they will be
			// re-drawn next time a sampled weights vector is needed
			//
			predictor.setModifiedWeightsVector(newInstFv);

			// x * x' is a full matrix, but we just need the diagonal, which we
			// can get by squaring each element and placing it along the
			// diagonal.
			// Go through each element in x (the feature vector.)
			for (curr = newInstFv; curr != null; curr = curr.next) {
				if (curr.index < 0)
					continue;

				int index = curr.index;
				double value = curr.value;
				double new_val = this.updater.getNewSigmaValue(index, value);

				if (Double.isInfinite(new_val) || Double.isNaN(new_val)) {
					continue;
				}

				updater.setSigmaValue(index, new_val);
			}

		}
	}
}
