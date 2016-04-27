package edu.nju.nlp.cw_library.algorithms.binary.cw_updaters;

import edu.nju.nlp.cw_library.types.IWeightVector;
import edu.nju.nlp.online.types.FeatureVector;
import edu.nju.nlp.online.types.Instance;

/**
 * An abstract class for updating a diagonal CW classifier.
 * 
 * @author Mark Dredze
 * 
 */
public abstract class DiagonalUpdater {

	protected Instance _instance;
	protected IWeightVector _sigma;
	protected IWeightVector _mu;
	protected double _phi = 0;
	protected FeatureVector _sigma_x;
	protected double _factor;
	protected double _alpha;
	protected boolean _update = false;

	/**
	 * Called at the start of an update. Establishes the parameters to use for
	 * this update.
	 * 
	 * @param instance
	 *            The current training instance.
	 * @param mu
	 *            The mean of the distribution.
	 * @param sigma
	 *            The variance of the distribution.
	 * @param phi
	 *            The Phi parameter for CW learning.
	 */
	public void setParameters(Instance instance, IWeightVector mu,
			IWeightVector sigma, double phi) {
		this._instance = instance;
		this._mu = mu;
		this._sigma = sigma;
		this._phi = phi;

		// System.out.println("phi = " + this._phi);

		// We have new parameters so clear the old ones.
		reset();
	}

	/**
	 * 
	 * @return The alpha to use for the update.
	 */
	public double getAlpha() {
		if (!this._update)
			throw new IllegalArgumentException(
					"Cannot call method if not updating.");
		return this._alpha;
	}

	/**
	 * 
	 * @return The factor to use for updating sigma.
	 */
	public double getFactor() {
		if (!this._update)
			throw new IllegalArgumentException(
					"Cannot call method if not updating.");
		return this._factor;
	}

	/**
	 * 
	 * @return Sigma times the instance.
	 */
	public FeatureVector getSigmaX() {
		if (!this._update)
			throw new IllegalArgumentException(
					"Cannot call method if not updating.");
		return this._sigma_x;
	}

	protected void reset() {
		this._factor = 0;
		this._sigma_x = null;
		this._alpha = 0;
		this._update = false;
	}

	/**
	 * 
	 * @return Whether or not an update is made for this instance with the
	 *         current parameters.
	 */
	public abstract boolean shouldUpdate();

	/**
	 * The new value for sigma for the given index and the value of the instance
	 * at that index.
	 * 
	 * @param index
	 * @param value
	 * @return
	 */
	public abstract double getNewSigmaValue(int index, double value);

	public double getNewMuValue(int index, double Y, double sigma_x) {
		double new_value = Y * this._alpha * sigma_x + this._mu.get(index);
		return new_value;
	}

	public double getMuValue(int index) {
		return this._mu.get(index);
	}

	public void setMuValue(int index, double newValue) {
		this._mu.set(index, newValue);
	}

	public double getSigmaValue(int index) {
		return this._sigma.get(index);
	}

	public void setSigmaValue(int index, double newValue) {
		this._sigma.set(index, newValue);
	}
}
