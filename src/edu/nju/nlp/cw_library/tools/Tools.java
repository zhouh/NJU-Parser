package edu.nju.nlp.cw_library.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import edu.nju.nlp.cw_library.types.ParameterOptimizable;
import edu.nju.nlp.online.types.Instance;

/**
 * Useful tools for the CW library.
 * @author Mark Dredze
 *
 */
public class Tools {

	/**
	 * These strings are used to specify the options on the command line. 
	 */
	public static final String ALGORITHMS = "perceptron, mira, cw, top_1_perceptron, top_1_mira, top_1_cw";
	public static final String DATA_FORMATS = "default, svm";
	public static final String UPDATE_TYPES = "diag_variance_drop, diag_variance_project(default), diag_variance_exact, diag_stdev_drop, diag_stdev_project, diag_stdev_exact";
	public static final double DEFAULT_C = 1;
	public static final double[] C_RANGE = 
		new double[] {.000001, .00001, .0001, .001, .01, .1, 1, 10};
	
	public static final double DEFAULT_PHI = 1;
	public static final double[] PHI_RANGE = 
		new double[] {.000001, .00001, .0001, .001, .01, .1, 1, 10, 100, 1000, 10000, 100000};
	
	// eta = cdf(phi)
	public static final double DEFAULT_ETA = 0.841344746068543;
	public static final double[] ETA_RANGE = 
		new double[] {0.500000398942280, 0.500003989422804, 0.500039894227974, 0.500398942213911, 0.503989356314632, 0.539827837277029, 0.841344746068543, 0.999};
		
	/**
	 * Given some command line parameters, return a classifier trainer.
	 * @param algorithm The name of the algorithm.
	 * @param update_type The type of the update.
	 * @param growable Whether or not the trainer should be growable.
	 * @param phi The Phi parameter for CW learning.
	 * @param C The C parameter for MIRA.
	 * @return
	 */
	/*
	@SuppressWarnings("unchecked")
	public static ClassifierTrainer resolveAlgorithm(String algorithm, String update_type, boolean growable, double[] parameters) {
		ClassifierTrainer trainer = null;
		if (algorithm.equalsIgnoreCase("mira")) { 
			LinearUpdater updater = new MIRAUpdater();
			trainer = new BinaryLinearClassifierTrainer(updater, growable);
		} else if (algorithm.equalsIgnoreCase("perceptron")) {
			LinearUpdater updater = new PerceptronUpdater();
			trainer = new BinaryLinearClassifierTrainer(updater, growable);
		} else if (algorithm.equalsIgnoreCase("cw")) {
			// Create the correct matrix type. Default is diagonal.
			if (update_type == null || update_type.startsWith("diag")) {
				// Create the correct update type. Default is variance.
				DiagonalUpdater updater = getUpdater(update_type);
				trainer = new CWBinaryDiagonalClassifierTrainer(updater, growable);
			} else if (update_type.startsWith("full")) {
				
			}	
		} else if (algorithm.equalsIgnoreCase("top_1_cw")) {
			DiagonalUpdater updater = getUpdater(update_type);
			trainer = new CWBinaryDiagonalClassifierTrainer(updater, growable);
			MultiPrototypeAlphabet feature_alphabet = new MultiPrototypeAlphabet();
			if (trainer != null)
				trainer = new MCOneBestBinaryReductionClassifierTrainer((ClassifierTrainer.ByInstanceIncrements)trainer, 
																		new MCCrossProductFeatureFunction(feature_alphabet));
			
		} else if (algorithm.equalsIgnoreCase("top_1_mira")) {
			LinearUpdater updater = new MIRAUpdater();
			trainer = new BinaryLinearClassifierTrainer(updater, growable);
			MultiPrototypeAlphabet feature_alphabet = new MultiPrototypeAlphabet();
			if (trainer != null)
				trainer = new MCOneBestBinaryReductionClassifierTrainer((ClassifierTrainer.ByInstanceIncrements)trainer, 
																		new MCCrossProductFeatureFunction(feature_alphabet));
		} else if (algorithm.equalsIgnoreCase("top_1_perceptron")) {
			LinearUpdater updater = new PerceptronUpdater();
			trainer = new BinaryLinearClassifierTrainer(updater, growable);
			MultiPrototypeAlphabet feature_alphabet = new MultiPrototypeAlphabet();
			if (trainer != null)
				trainer = new MCOneBestBinaryReductionClassifierTrainer((ClassifierTrainer.ByInstanceIncrements)trainer, 
						new MCCrossProductFeatureFunction(feature_alphabet));
		} else
			throw new IllegalArgumentException("Algorithm must be of type: " + ALGORITHMS);

		if (trainer == null)
			throw new IllegalArgumentException("Algorithm must be of type: " + ALGORITHMS);

		if (trainer instanceof ParameterOptimizable && parameters != null)
			((ParameterOptimizable)trainer).setParameterValues(parameters);
		return trainer;
	}
	*/
	/*
	private static DiagonalUpdater getUpdater(String update_type) {
		DiagonalUpdater updater = null;
		if (update_type == null || update_type.equalsIgnoreCase("diag_variance_project")) {
			updater = new DiagonalVarianceProjectUpdater();
		} else if (update_type.equalsIgnoreCase("diag_variance_drop")) {
			updater = new DiagonalVarianceDropUpdater();
		} else if (update_type.equalsIgnoreCase("diag_variance_exact")) {
			updater = new DiagonalVarianceExactUpdater();
		} else if (update_type.equalsIgnoreCase("diag_stdev_drop")) {
			updater = new DiagonalStdevDropUpdater();
		} else if (update_type.equalsIgnoreCase("diag_stdev_project")) {
			updater = new DiagonalStdevProjectUpdater();
		} else if (update_type.equalsIgnoreCase("diag_stdev_exact")) {
			updater = new DiagonalStdevExactUpdater();
		} else {
			throw new IllegalArgumentException("Update type must be: " + UPDATE_TYPES);
		}
		return updater;
	}
	*/
	
	public static double getBinaryLabel(Instance instance) {
		/*
		double y = 1.0d;
		if (instance.getLabeling().getBestIndex() == 0)
			y = -1.0d;
		return y;
		*/
		throw new RuntimeException("NYI");
	}
	
	/**
	 * Returns the data loader that matches the specified loader.
	 * @param data_format The loader type to return.
	 * @param filename The filename of the data to load.
	 * @param pipe The pipe for this data.
	 * @param data_in_gzip_format 
	 * @return
	 * @throws IOException
	 */
	/*
	public static DataLoader resolveDataLoader(String data_format, String filename, Pipe pipe, boolean data_in_gzip_format) throws IOException {
		DataLoader data_loader = null;
		if (data_format == null || data_format.equalsIgnoreCase("default")) {
			data_loader = new DefaultDataLoader(filename, pipe, data_in_gzip_format);
		} else if (data_format.equalsIgnoreCase("svm")) {
			data_loader = new SVMDataLoader(filename, pipe, data_in_gzip_format);
		} else {
			throw new IllegalArgumentException("Illegal data_format: must be " + DATA_FORMATS);
		}
		return data_loader;
	}
	*/
	
	/**
	 * Save an object into a file.
	 * @param object
	 * @param file_name
	 */
	public static void saveObject(Object object, String file_name) {
		try {
			ObjectOutputStream oos =
				new ObjectOutputStream(new BufferedOutputStream(
						new FileOutputStream(new File(file_name))));
			oos.writeObject(object);
			oos.close();
		}
		catch (IOException e) {
			System.err.println("Exception writing file " + file_name + ": " + e);
		}
	}
	
	/**
	 * Loads a classifier from a given filename.
	 * @param file_name
	 * @return
	 */
	/*
	public static Classifier loadModel(String file_name) {	
		return (Classifier)loadObject(file_name);	
	}
	*/

	/**
	 * Load a single object from a filename. 
	 * @param file_name
	 * @return
	 */
	public static Object loadObject(String file_name) {
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(file_name))));
			Object object = ois.readObject();
			ois.close();
			return object;
		} catch (IOException e) {
			System.err.println("Error loading: " + file_name);
		} catch (ClassNotFoundException e) {
			System.err.println("Error loading: " + file_name);
		}
		return null;
	}

	public static double[] parseParameterString(String parameter_string) {
		String[] split_string = (parameter_string == null) ? null : parameter_string.split("/");
		if (split_string != null) {
			double[] parameters = new double[split_string.length];
			for (int ii = 0; ii < parameters.length; ii++) {
				try {
					if (split_string == null || split_string[ii].equals("+"))
						parameters[ii] = Double.NaN;
					else
						parameters[ii] = Double.parseDouble(split_string[ii]);
				} catch (Exception e) {
					throw new IllegalArgumentException("Parameter values must be numbers of the default \"+\"");
				}
			}
			return parameters;
		} else
			return null;
	}

	public static double[] resolveDefaultParameters(double[] parameters,
			ParameterOptimizable trainer) {
		double[] return_params = new double[trainer.getNumberOfParameters()];
		
		if (parameters == null) {
			for (int ii = 0; ii < return_params.length; ii++) {
				return_params[ii] = trainer.getDefaultParameterValue(ii);
			}
		} else {
			if (return_params.length != parameters.length) {
				StringBuffer sb = new StringBuffer();
				sb.append("Incorrect number of parameters for trainer " + trainer.getClass().getSimpleName() + ". ");
				sb.append("This classifier takes " + trainer.getNumberOfParameters() + " parameters.\n");
				sb.append(trainer.getParameterDescriptions() + "\n");
				throw new IllegalArgumentException(sb.toString());
			}
			for (int ii = 0; ii < return_params.length; ii++) {
				if (Double.isNaN(parameters[ii])) {
					return_params[ii] = trainer.getDefaultParameterValue(ii);
				} else {
					return_params[ii] = parameters[ii];
				}
			}
		}
		return return_params;
	}
	
}
