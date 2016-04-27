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
 * This class implements a multi-class top-k classifier by reducing the problem
 * to binary classification. The correct label is a positive example whereas the
 * top-k predicted incorrect labels are a negative example. The effect is that
 * the learning constraint is enforced for the correct label and top-k predicted
 * labels, but not every predicted label.
 * 
 * @author Mark Dredze
 * 
 */
public class KBestBinaryReductionCWUpdator implements OnlineUpdator {

	protected double _a = 1;
	protected int _negative_index = 0;
	protected int _positive_index = 1;
	protected double _phi = Tools.DEFAULT_PHI;
	protected boolean _growable;
	String cwUpdaterType;
	DiagonalUpdater updater;

	public int K;

	public KBestBinaryReductionCWUpdator(String cwUpdaterType,
			double phi, int K) {
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
		this.K = K;
	}

	boolean isLabelDifferentFromAllOtherLabels(Label currentLabel,
			Label[] otherLabels, int numLabelsToCheck) {
		for (int labelIdx = 0; labelIdx < numLabelsToCheck; labelIdx++) {
			Label otherLabel = otherLabels[labelIdx];
			if (currentLabel.loss(otherLabel) == 0) {
				// current and one of other labels are same - return false
				return false;
			}
		}
		return true;
	}

	Label[] genKDifferentLabels(Instance inst, Features feats,
			CWConParsePredictor predictor) {
		int KMult = 5;
		int labelsToDraw = K * KMult;
		// Get the best K sequence labels:
		// First draw X*K labels - then choose the first K different ones
		Prediction pred = predictor.decode(inst, feats, labelsToDraw);
		Label[] labels = new Label[labelsToDraw];
		int k_labels = 0;
		for (int k = 0; k < labelsToDraw; k++) {
			labels[k] = pred.getLabelByRank(k);
			if (labels[k] == null)
				break;
			k_labels = k + 1;
		}

		//
		// If using K-Best Viterbi we may now actually have less than
		// 'labelsToDraw' labels
		// since there are no 'labelsToDraw' different labels for this instance.
		//

		//
		// Now search for K different labels among the set of drawn labels
		//
		Label[] differentLabels = new Label[K];
		int differentLabelsCount = 0;
		// The first label is different
		differentLabels[differentLabelsCount] = labels[0];
		differentLabelsCount++;

		for (int curLabIdx = 1; curLabIdx < k_labels; curLabIdx++) {
			Label currLabel = labels[curLabIdx];
			if (isLabelDifferentFromAllOtherLabels(currLabel, differentLabels,
					differentLabelsCount)) {
				// label is different from all previous - add it to set of
				// different
				differentLabels[differentLabelsCount] = currLabel;
				differentLabelsCount++;
				if (differentLabelsCount == K) {
					break;
				}
			}
		}

		if (differentLabelsCount == K) {
			return differentLabels;
		} else {
			Label[] diffLabels = new Label[differentLabelsCount];
			for (int i = 0; i < differentLabelsCount; i++) {
				diffLabels[i] = differentLabels[i];
			}
			return diffLabels;
		}
	}

	public void update(Instance inst, Features feats, Predictor _predictor,
			double avg_upd) {

		CWConParsePredictor predictor = (CWConParsePredictor) _predictor;

		// Get the K sequence labels...
		Label[] labels = genKDifferentLabels(inst, feats, predictor);
		int k_labels = labels.length;

		//
		// Now from each of the k labels - produce a binary instance to train
		// the model
		//
		Label correctLabel = inst.getLabel();
		FeatureVector muChanges = new FeatureVector(-1, -1.0, null);
		FeatureVector sigmaChanges = new FeatureVector(-1, -1.0, null);
		int updatesCount = 0;
		for (int curLabIdx = 0; curLabIdx < k_labels; curLabIdx++) {
			// Create a new temporary binary instance with positive label (+1),
			// with
			// the correct feature vector minus the top incorrect feature
			// vector.
			// Later the weights will be trained to indeed predict this instance
			// as
			// positive.
			FeatureVector correctFv = correctLabel
					.getFeatureVectorRepresentation();
			FeatureVector incorrectFv = labels[curLabIdx]
					.getFeatureVectorRepresentation();

			FeatureVector newInstFv;
			//
			// Given the correct instance and best incorrect instance we create
			// a
			// new positive instance and train the model to classify it
			// correctly.
			//
			newInstFv = FeatureVector.getDistVector(correctFv, incorrectFv);

			// New temporary binary instance with label=1
			BinaryInstance newBinaryInstance = new BinaryInstance(1, newInstFv);

			// Now Train the underlying classifier on this instance.
			WeightVector _mu = new WeightVector(predictor.weights);
			WeightVector _sigma = new WeightVector(predictor.sigma);
			updater.setParameters(newBinaryInstance, _mu, _sigma, this._phi);
			if (updater.shouldUpdate()) {

				// Count number of updates actually done (may be different than
				// K) so later average accumulated changes according to it
				updatesCount++;

				double alpha = updater.getAlpha();
				double Y = ((BinaryLabel) newBinaryInstance.getLabel()).tag;

				if (Double.isInfinite(alpha) || Double.isNaN(alpha)) {
					return;
				}
				// Now we can compute the new weights.
				// mu = mu + y * alpha * sigma_x;
				// Only the elements that are on in the feature vector are
				// non-zero
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
					if (Double.isInfinite(newMuValue)
							|| Double.isNaN(newMuValue)) {
						continue;
					}

					double muValueDiff = newMuValue - prevMuValue;
					muChanges = new FeatureVector(curr.index, muValueDiff,
							muChanges);
				}

				// x * x' is a full matrix, but we just need the diagonal, which
				// we
				// can get by squaring each element and placing it along the
				// diagonal.
				// Go through each element in x (the feature vector.)
				for (curr = newInstFv; curr != null; curr = curr.next) {
					if (curr.index < 0)
						continue;

					int index = curr.index;
					double featureValue = curr.value;
					double prevSigmaValue = updater.getSigmaValue(index);
					double newSigmaValue = updater.getNewSigmaValue(index,
							featureValue);

					if (Double.isInfinite(newSigmaValue)
							|| Double.isNaN(newSigmaValue)) {
						continue;
					}

					double sigmaValueDiff = newSigmaValue - prevSigmaValue;
					sigmaChanges = new FeatureVector(curr.index,
							sigmaValueDiff, sigmaChanges);
				}
			}// if should update
		}// for all k labels

		//
		// Finally update the model by the calculate changes
		//
		if (updatesCount > 0) {
			FeatureVector curr;
			for (curr = muChanges; curr != null; curr = curr.next) {
				if (curr.index < 0)
					continue;
				int index = curr.index;
				double change = curr.value;
				// average the change value by number of updates
				change /= updatesCount;
				double muValue = updater.getMuValue(index);
				double newMuValue = muValue + change;
				if (Double.isInfinite(newMuValue)
						|| Double.isNaN(newMuValue)) {
					System.out.println("New mu at index "+index+" is Inf or NaN");
					continue;
				}
				updater.setMuValue(index, newMuValue);
				// Support for weights averaging
				double[] avg_weights = predictor.avg_weights;
				avg_weights[curr.index] += (avg_upd * change);
			}
			//
			// Let the predictor know which weights were changed so they will be
			// re-drawn next time a sampled weights vector is needed
			//
			predictor.setModifiedWeightsVector(muChanges);

			for (curr = sigmaChanges; curr != null; curr = curr.next) {
				if (curr.index < 0)
					continue;
				int index = curr.index;
				double change = curr.value;
				// average the change value by number of updates
				change /= updatesCount;
				double sigmaValue = updater.getSigmaValue(index);
				double newSigmaValue = sigmaValue + change;
				if (Double.isInfinite(newSigmaValue)
						|| Double.isNaN(newSigmaValue)) {
					System.out.println("New sigma at index "+index+" is Inf or NaN");
					continue;
				}				
				updater.setSigmaValue(index, newSigmaValue);
			}
		}
	}
}
