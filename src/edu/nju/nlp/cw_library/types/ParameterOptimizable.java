package edu.nju.nlp.cw_library.types;

/**
 * This interface is used to support parameter optimization. Classifier trainers that implement
 * this interface can have their parameters optimized.
 * @author Mark Dredze
 *
 */
public interface ParameterOptimizable {

	/**
	 * Get a description of the parameters. This will be printed to the command line when an error is made in supplying parameters.
	 * @return
	 */
	public String getParameterDescriptions();
	/**
	 * Get the description for the parameter indexed by index.
	 * @param index
	 * @return
	 */
	public String getParameterDescription(int index);
	
	/**
	 * Get the current values of the parameter indexed by index.
	 * @param index
	 * @return
	 */
	public double getParameterValue(int index);
	
	/**
	 * Set a new value for the parameter indexed by index.
	 * @param index
	 * @param value
	 */
	public void setParameterValue(int index, double value);
	
	/**
	 * Set new values for all of the parameters.
	 * @param values
	 */
	public void setParameterValues(double[] values);
	
	/**
	 * Returns the range of values to try for the parameter indexed by index.
	 * @param index
	 * @return
	 */
	public double[] getParameterRange(int index);
	
	/**
	 * Get the default value for the parameter indexed by index.
	 * @param index
	 * @return
	 */
	public double getDefaultParameterValue(int index);
	
	/**
	 * Get the default value for all of the parameters.
	 * @return
	 */
	public double[] getDefaultParameterValues();
	
	/**
	 * How many parameters can be optimized.
	 * @return
	 */
	public int getNumberOfParameters();
	
	/**
	 * Resets the trainer's internal state but keeps the current parameters.
	 */
	public void reset();
	
	/**
	 * A human readable description of the current state of the parameters.
	 * @return
	 */
	public String getCurrentParameterDescriptions();

}
