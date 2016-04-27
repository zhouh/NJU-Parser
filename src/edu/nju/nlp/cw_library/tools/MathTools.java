package edu.nju.nlp.cw_library.tools;

import java.util.Random;

import edu.nju.nlp.cw_library.types.IWeightVector;
import edu.nju.nlp.cw_library.types.LineOptimizable;
import edu.nju.nlp.cw_library.types.SparseVector;
import edu.nju.nlp.online.types.FeatureVector;
import edu.nju.nlp.online.types.Instance;
import gnu.trove.map.hash.TIntDoubleHashMap;

/**
 * Some useful math tools for the library.
 * 
 * @author Mark Dredze
 * 
 */
public class MathTools {

	/**
	 * Find the minimum value in the array of values.
	 * 
	 * @param values
	 * @return The min of the values.
	 */
	public static double minValue(double[] values) {
		double min = Double.MAX_VALUE;
		for (double value : values) {
			if (value < min) {
				min = value;
			}
		}
		return min;
	}

	/**
	 * Compute the mean of a set of values.
	 * 
	 * @param values
	 * @return The mean of the values.
	 */
	public static double calculateMean(double[] values) {
		double sum = 0;
		for (double value : values)
			sum += value;
		return sum / (double) values.length;
	}

	public static int calculateMean(int[] values) {
		int sum = 0;
		for (int value : values)
			sum += value;
		return (int) (sum / (double) values.length);
	}

	//
	// Calc mean of absolute values
	//
	public static double calculateAbsMean(double[] values) {
		double sum = 0;
		for (double value : values)
			sum += Math.abs(value);
		return sum / (double) values.length;
	}

	//
	// Calc mean of sqrt of values in array
	//
	public static double calculateMeanOfSqrts(double[] values) {
		double sum = 0;
		for (double value : values)
			sum += Math.sqrt(value);
		return sum / (double) values.length;
	}

	//
	// res[0] = (values[0][0]+values[0][1]+...+values[0][k])/k
	// ...
	// res[n] = (values[n][0]+values[n][1]+...+values[n][k])/k
	//
	public static double[] calculateArrayOfMean(double[][] values) {
		int rows = values[0].length;
		int cols = values.length;
		double[] res = new double[rows];
		for (int i = 0; i < res.length; i++) {
			double sum = 0;
			for (int c = 0; c < cols; c++) {
				sum += values[c][i];
			}
			res[i] = (sum / cols);
		}
		return res;
	}

	/**
	 * Computer the standard deviation of a set of values.
	 * 
	 * @param values
	 *            The values
	 * @param mean
	 *            The mean of these values.
	 * @return The standard deviation of the values.
	 */
	public static double calculateSTDev(double[] values) {
		double mean = calculateMean(values);
		return calculateSTDev(values, mean);
	}

	public static double calculateSTDev(double[] values, double mean) {
		// sqrt[(1/values) sum_values (value - mean)^2]
		double total = 0;
		for (int ii = 0; ii < values.length; ii++)
			total += (values[ii] - mean) * (values[ii] - mean);
		total /= values.length;
		total = Math.sqrt(total);
		return total;
	}

	//
	// Calc stddev of the sqrt of values from the mean
	//
	public static double calculateSTDevOfSqrts(double[] values, double mean) {
		double total = 0;
		for (int ii = 0; ii < values.length; ii++) {
			double sqrtVal = Math.sqrt(values[ii]);
			total += (sqrtVal - mean) * (sqrtVal - mean);
		}
		total /= values.length;
		total = Math.sqrt(total);
		return total;
	}

	//
	// Calc the std-dev of the absolute values from the mean
	//
	public static double calculateAbsSTDev(double[] values, double mean) {
		double total = 0;
		for (int ii = 0; ii < values.length; ii++) {
			double absVal = Math.abs(values[ii]);
			total += (absVal - mean) * (absVal - mean);
		}
		total /= values.length;
		total = Math.sqrt(total);
		return total;
	}

	/**
	 * Invert the vector as if it were a diagonal matrix.
	 * 
	 * @param vector
	 * @return
	 */
	public static IWeightVector invertDiag(IWeightVector vector) {
		// Create a new weight vector of the same type.
		IWeightVector invVector = vector.createVectorOfSameType();

		for (int ii = 0; ii < vector.size(); ii++) {
			double value = vector.get(ii);
			if (value != 0) {
				invVector.set(ii, 1.0d / value);
			}
		}
		return invVector;
	}

	/**
	 * Classify an instance using the dot product between the weights and
	 * instance.
	 * 
	 * @param instance
	 * @param weights
	 * @return The prediction.
	 */
	public static double predict(Instance instance, IWeightVector weights) {
		if (weights == null || instance == null)
			return 0;
		throw new RuntimeException("NYI");
		// return dotProduct((FeatureVector)instance.getData(),weights);
	}

	/**
	 * Compute the dot product between a feature vector and a weight vector.
	 * 
	 * @param feature_vector
	 * @param weights
	 * @return
	 */
	public static double dotProduct(FeatureVector feature_vector,
			IWeightVector weights) {
		if (weights == null || feature_vector == null)
			return 0;

		double value;
		double weight;
		double score = 0.0;
		for (FeatureVector curr = feature_vector; curr != null; curr = curr.next) {
			if (curr.index >= 0) {
				value = curr.value;
				weight = weights.get(curr.index);
				score += weight * value;
			}
		}
		return score;
	}

	/**
	 * Compute the dot product between a feature vector and a weight vector
	 * represented as a hash map.
	 * 
	 * @param feature_vector
	 * @param weights
	 * @return
	 */
	@SuppressWarnings("unused")
	public static double dotProduct(FeatureVector feature_vector,
			TIntDoubleHashMap weights) {
		if (weights == null || feature_vector == null)
			return 0;

		double dot_product = 0;
		/*
		 * for (int ii = 0; ii < feature_vector.numLocations(); ii++) { int
		 * index = feature_vector.indexAtLocation(ii); double value =
		 * feature_vector.valueAtLocation(ii); double weight =
		 * weights.get(index); dot_product += weight * value; }
		 * 
		 * return dot_product;
		 */
		throw new RuntimeException("NYI");
	}

	public static double dotProductSample(FeatureVector feature_vector,
			IWeightVector weights, IWeightVector sigma, Random rnd) {
		if (weights == null || feature_vector == null)
			return 0;

		double dot_product = 0;
		/*
		 * for (int ii = 0; ii < feature_vector.numLocations(); ii++) { int
		 * index = feature_vector.indexAtLocation(ii); double value =
		 * feature_vector.valueAtLocation(ii);
		 * 
		 * // sample weight from gaussian with (mean = weights.get(index) ) and
		 * (std^2 = sigma.get(index) ) double weight = weights.get(index) +
		 * rnd.nextGaussian() * Math.sqrt(sigma.get(index)) ;
		 * 
		 * dot_product += weight * value; }
		 * 
		 * return dot_product;
		 */
		throw new RuntimeException("NYI");
	}

	/**
	 * Compute A * B where A is an array representing a diagonal matrix and B is
	 * a feature vector.
	 * 
	 * @param array
	 * @param fv
	 * @return
	 */
	public static FeatureVector product(IWeightVector array, FeatureVector fv) {
		FeatureVector newFv = new FeatureVector(-1, -1.0, null);
		double value, weight, newValue;

		for (FeatureVector curr = fv; curr != null; curr = curr.next) {
			if (curr.index >= 0) {
				value = curr.value;
				weight = array.get(curr.index);
				newValue = weight * value;
				newFv = new FeatureVector(curr.index, newValue, newFv);
			}
		}
		return newFv;
	}

	/**
	 * Compute bT * A * b where A is an array representing a diagonal matrix and
	 * B is a feature vector.
	 * 
	 * @param array
	 * @param fv
	 * @return
	 */
	public static double squaredNorm(IWeightVector array, FeatureVector fv) {
		double res = 0;
		/*
		 * for (int ii = 0; ii < fv.numLocations(); ii++) { int index =
		 * fv.indexAtLocation(ii); double value = fv.valueAtLocation(ii); res +=
		 * array.get(index) * value * value; } return res;
		 */
		throw new RuntimeException("NYI");
	}

	/**
	 * A line search optimizer.
	 * 
	 * @param initial_guess
	 *            The initial guess to start the search.
	 * @param function
	 *            The function to optimize.
	 * @return The result of the optimization.
	 */
	public static double lineSearch(double initial_guess,
			LineOptimizable function) {
		// This is based on Koby's myfzero implementation.
		int max1 = 1000;
		int max2 = 5000;
		double accuracy = 1e-10;
		double zero = 1e-10;

		boolean positive_at_zero = false;
		// Assumes f(0)<=0
		double value_at_0 = function.computeFunction(zero);
		if (value_at_0 > 0) {
			// System.err.println("error, assumes f(0)<0");
			// return Double.NaN;
			positive_at_zero = true;
		}

		double x_left = 0;
		double x_right = 0;
		double value_left = 0;
		double value_right = 0;
		double initial_value = function.computeFunction(initial_guess);
		if (positive_at_zero) {
			// Find a value for which the function is negative.
			int count1 = 0;
			x_left = initial_guess;
			value_left = function.computeFunction(x_left);
			while (value_left > 0 && count1 < max1) {
				x_left = x_left / 2;
				value_left = function.computeFunction(x_left);
				count1++;
			}
			if (value_left > 0) {
				System.err.println("error, cant find x_left with f(x_left)<0");
				return x_left;
			}
		}
		if (initial_value > 0) {
			x_left = zero;
			x_right = initial_guess;

			value_left = function.computeFunction(x_left);
			value_right = function.computeFunction(x_right);
		} else {
			x_left = initial_guess;
			value_left = initial_value;
			x_right = initial_guess;
			value_right = initial_value;
			int count1 = 0;

			while (value_right < 0 && count1 < max1) {
				x_right = x_right * 2;
				value_right = function.computeFunction(x_right);
				count1++;
			}
			if (value_right < 0) {
				System.err
						.println("error, cant find x_right with f(x_right)>0");
				return x_left;
			}
		}

		// Search between x_left and x_right.
		int count2 = 0;
		double x_middle = (x_right + x_left) / 2;
		double value_middle = function.computeFunction(x_middle);

		while (Math.abs(value_middle) > accuracy && count2 < max2) {
			x_middle = (x_right + x_left) / 2;
			value_middle = function.computeFunction(x_middle);

			if (value_middle < 0) {
				value_left = value_middle;
				x_left = x_middle;
			} else {
				value_right = value_middle;
				x_right = x_middle;
			}
			count2++;
		}
		if (Math.abs(value_middle) > accuracy) {
			System.err
					.println("error, cant find x_middle with good resolution");
			return x_middle;
		}

		return x_middle;
	}

	/**
	 * A line search optimizer that doesn't assume the parameter is positive. It
	 * still DOES assume that the function is monotone increasing.
	 * 
	 * @param initial_guess
	 *            The initial guess to start the search.
	 * @param function
	 *            The function to optimize.
	 * @return The result of the optimization.
	 */
	public static double lineSearchFullRange(double initial_guess,
			LineOptimizable function) {
		// This is based on Koby's myfzero implementation.
		int max1 = 1000;
		int max2 = 5000;
		double accuracy = 1e-4;
		double initial_step_size = 0.01;

		double x_left = 0;
		double x_right = 0;
		double value_left = 0;
		double value_right = 0;
		double initial_value = function.computeFunction(initial_guess);

		if (initial_value > 0) {
			x_right = initial_guess;
			value_right = initial_value;

			// Find a value for which the function is negative.
			int count1 = 0;
			double step_size = initial_step_size;
			x_left = initial_guess;
			value_left = function.computeFunction(x_left);
			while (value_left > 0 && count1 < max1) {
				x_left = x_left - step_size;
				step_size = step_size * 2;
				value_left = function.computeFunction(x_left);
				count1++;
			}
			if (value_left > 0) {
				System.err.println("error, cant find x_left with f(x_left)<0");
				return x_left;
			}
		} else {
			x_left = initial_guess;
			value_left = initial_value;

			// Find a value for which the function is positive.
			int count1 = 0;
			double step_size = initial_step_size;
			x_right = initial_guess;
			value_right = function.computeFunction(x_right);
			while (value_right < 0 && count1 < max1) {
				x_right = x_right + step_size;
				step_size = step_size * 2;
				value_right = function.computeFunction(x_right);
				count1++;
			}
			if (value_right < 0) {
				System.err
						.println("error, cant find x_right with f(x_right)<0");
				return x_right;
			}
		}

		// Search between x_left and x_right.
		int count2 = 0;
		double x_middle = (x_right + x_left) / 2;
		double value_middle = function.computeFunction(x_middle);

		while (Math.abs(value_middle) > accuracy && count2 < max2) {
			x_middle = (x_right + x_left) / 2;
			value_middle = function.computeFunction(x_middle);

			if (value_middle < 0) {
				value_left = value_middle;
				x_left = x_middle;
			} else {
				value_right = value_middle;
				x_right = x_middle;
			}
			count2++;
		}
		if (Math.abs(value_middle) > accuracy) {
			System.err
					.println("error, cant find x_middle with good resolution");
			return x_middle;
		}

		return x_middle;
	}

	/**
	 * Computes the product of two matrices.
	 * 
	 * @param matrix
	 *            A row vector of size 1 x N
	 * @param fv
	 *            A feature vector that is treated as a column vector of size N
	 *            x 1
	 * @return The product of the two matrices, which is a 1x1 matrix (a single
	 *         value)
	 */
	public static double productT(FeatureVector vector, FeatureVector fv) {
		// Give the matricies, compute the dot product between them to get the
		// resulting product.
		double sum = 0;
		/*
		 * for (int ii = 0; ii < fv.numLocations(); ii++) { //int index =
		 * fv.indexAtLocation(ii); double value = fv.valueAtLocation(ii); sum +=
		 * vector.valueAtLocation(ii) * value; // Assume the locations for the
		 * sparse vector are the same as for FeatureVector. } return sum;
		 */
		throw new RuntimeException("NYI");
	}

	/**
	 * Copies a weight vector.
	 * 
	 * @param source
	 * @param destination
	 */
	public static void weightVectorCopy(IWeightVector source,
			IWeightVector destination) {
		// Copy the source into the destination.
		for (int ii = 0; ii < source.size(); ii++) {
			double value = source.get(ii);
			destination.set(ii, value);
		}
	}

	public static SparseVector product(SparseVector A, FeatureVector fv) {
		/*
		 * SparseVector matrix = new SparseVector(fv.numLocations()); for (int
		 * ii = 0; ii < fv.numLocations(); ii++) { int index =
		 * fv.indexAtLocation(ii); double value = fv.valueAtLocation(ii); if
		 * (A.indexAtLocation(ii) != index) throw new
		 * IllegalArgumentException("The indicies of the two vectors must match."
		 * ); double new_value = A.valueAtLocation(ii) * value;
		 * matrix.addValue(index, new_value); } return matrix;
		 */
		throw new RuntimeException("NYI");
	}

	/**
	 * Add one feature vector to another times a factor: X = A + B * factor
	 * 
	 * @param a
	 *            A
	 * @param b
	 *            B
	 * @param factor
	 *            Factor
	 * @return The result (X)
	 */
	public static FeatureVector addWithFactor(FeatureVector a, FeatureVector b,
			double factor) {
		/*
		 * assert(a.getAlphabet() == b.getAlphabet()); AugmentableFeatureVector
		 * res = new
		 * AugmentableFeatureVector(a.getAlphabet(),a.numLocations(),false);
		 * 
		 * // First copy a for(int i=0; i<a.numLocations(); i++) { int index =
		 * a.indexAtLocation(i); double value = a.valueAtLocation(i);
		 * res.add(index,value); }
		 * 
		 * // Then add in b * factor. for(int i=0; i<b.numLocations(); i++) {
		 * int index = b.indexAtLocation(i); double value =
		 * b.valueAtLocation(i); res.add(index,res.value(index)+factor*value); }
		 * 
		 * return res;
		 */
		throw new RuntimeException("NYI");
	}

}
