package edu.nju.nlp.cw_library.types;

/**
 * Marks a function that can be optimized using a simple Line Optimization routine.
 * @author Mark Dredze
 *
 */
public interface LineOptimizable {
	/**
	 * Return the value of the function at this point.
	 * @param value The X value of the function.
	 * @return The Y value of the function given the X.
	 */
	double computeFunction(double value);
}
