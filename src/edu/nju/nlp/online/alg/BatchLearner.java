package edu.nju.nlp.online.alg;

import java.io.IOException;

import edu.nju.nlp.online.types.Evaluator;
import edu.nju.nlp.online.types.Instance;

public class BatchLearner implements StructuredLearner {

	public void trainAndEvaluate(Instance[] training, Instance[] testing,
			BatchUpdator update, Predictor predictor,
			String trainingFeatureFile, String testingFeatureFile,
			Evaluator eval) throws IOException {

		long start = System.currentTimeMillis();

		update.update(training, predictor, trainingFeatureFile);

		long end = System.currentTimeMillis();
		System.out.println("==========================");
		System.out.println("Final Performance.");
		System.out.println("==========================");
		System.out.println("Training took: " + (end - start));
		System.out.println("Training");
		eval.evaluate(training, predictor, trainingFeatureFile);
		System.out.println("Testing");
		eval.evaluate(testing, predictor, testingFeatureFile);
	}

}
