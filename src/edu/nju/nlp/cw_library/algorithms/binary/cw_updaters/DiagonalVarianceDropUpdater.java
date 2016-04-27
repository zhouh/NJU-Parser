package edu.nju.nlp.cw_library.algorithms.binary.cw_updaters;

import edu.nju.nlp.cw_library.tools.MathTools;
import edu.nju.nlp.cw_library.types.BinaryFeatures;
import edu.nju.nlp.cw_library.types.BinaryInstance;
import edu.nju.nlp.cw_library.types.BinaryLabel;
import edu.nju.nlp.online.types.FeatureVector;

/**
 * Variance update for diagonal CW learning. The variance update is introduced
 * in: Mark Dredze, Koby Crammer, Fernando Pereira. Confidence-Weighted Linear
 * Classification. ICML 2008. Instead of projecting the matrix, this update
 * simply drops the off diagonal elements after the update. This is equation
 * #13.
 */
public class DiagonalVarianceDropUpdater extends DiagonalUpdater {

	public boolean shouldUpdate() {
		// Make a prediction with this instance.
		// double prediction = MathTools.predict(this._instance, this._mu);
		BinaryInstance binInst = (BinaryInstance) (this._instance);
		BinaryFeatures binFeats = (BinaryFeatures) binInst.getFeatures();
		FeatureVector instFeaturesVector = binFeats.getFeatureVector();
		double prediction = MathTools.dotProduct(instFeaturesVector, this._mu);

		// Should we make an update?
		// Sigma * X
		this._sigma_x = MathTools.product(this._sigma, instFeaturesVector);
		// B = x' * sigma * x;
		double B = FeatureVector.dotProduct(this._sigma_x, instFeaturesVector);
		BinaryLabel binLabel = (BinaryLabel) binInst.getLabel();
		double Y = binLabel.tag;

		double m = prediction * Y;

		// update?
		this._update = (m <= this._phi * B) && (B != 0);

		if (this._update) {
			double term1 = 1 + 2 * this._phi * m;
			double term2 = m - this._phi * B;
			this._alpha = (-term1 + Math.sqrt(Math.pow(term1, 2) - 8
					* this._phi * term2))
					/ (4 * this._phi * B);
			this._factor = (2 * this._alpha * this._phi)
					/ (1 + 2 * this._alpha * this._phi * B);
		}

		return this._update;
	}

	public double getNewSigmaValue(int index, double value) {
		double new_val = this._sigma.get(index) - this.getFactor()
				* Math.pow(value * this._sigma.get(index), 2);
		return new_val;
	}
}
