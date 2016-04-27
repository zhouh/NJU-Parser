/**
 * 
 */
package edu.nju.nlp.arow;

import edu.nju.nlp.cw_library.algorithms.binary.cw_updaters.DiagonalUpdater;
import edu.nju.nlp.cw_library.tools.MathTools;
import edu.nju.nlp.cw_library.types.BinaryFeatures;
import edu.nju.nlp.cw_library.types.BinaryInstance;
import edu.nju.nlp.cw_library.types.BinaryLabel;
import edu.nju.nlp.online.types.FeatureVector;

/**
 * @author crammer phi is r^2
 */
public class DiagonalKalmanExactUpdater extends DiagonalUpdater {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cw_library.algorithms.binary.cw_updaters.DiagonalUpdater#getNewSigmaValue
	 * (int, double)
	 */
	@Override
	public double getNewSigmaValue(int index, double value) {
		double new_val = this._sigma.get(index)
				/ (1 + this.getFactor() * this._sigma.get(index)
						* Math.pow(value, 2));
		return new_val;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * cw_library.algorithms.binary.cw_updaters.DiagonalUpdater#shouldUpdate()
	 */
	@Override
	public boolean shouldUpdate() {

		// Make a prediction with this instance.
		BinaryInstance binInst = (BinaryInstance) (this._instance);
		BinaryFeatures binFeats = (BinaryFeatures) binInst.getFeatures();
		FeatureVector instFeaturesVector = binFeats.getFeatureVector();
		double prediction = MathTools.dotProduct(instFeaturesVector, this._mu);
		BinaryLabel binLabel = (BinaryLabel) binInst.getLabel();
		double Y = binLabel.tag;
		double m = prediction * Y;
		// Make an update
		this._update = m <= 1;

		if (this._update) {

			// Should we make an update?
			// Sigma * X
			this._sigma_x = MathTools.product(this._sigma, instFeaturesVector);
			// B = x' * sigma * x;
			double B = FeatureVector.dotProduct(this._sigma_x,
					instFeaturesVector);

			this._alpha = (1 - m) / (B + this._phi);
			this._factor = 1 / this._phi;

		}

		return this._update;

	}

}
