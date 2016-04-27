package edu.nju.nlp.online.alg;

import java.io.IOException;

import edu.nju.nlp.online.types.Evaluator;
import edu.nju.nlp.online.types.Features;
import edu.nju.nlp.online.types.Instance;
import edu.nju.nlp.parser.constituent.tss.ConParsePredictor;

public class OnlineLearner implements StructuredLearner {

	public void train(Instance[] training, OnlineUpdator update,
			Predictor predictor, int numIters, String featureFile)
			throws IOException {
		train(training, update, predictor, numIters, featureFile, true);
	}

	public void train(Instance[] training, OnlineUpdator update,
			Predictor predictor, int numIters, String featureFile,
			boolean avgParams) {

		System.out.println("Training instances: " + training.length
				+ ", Iterations: " + numIters);
		
		for (int i = 0; i < numIters; i++) {
			for (int j = 0; j < training.length; j++) {
				// System.out.print(".");

//				if(i==2&&j==10553){
//					predictor.beigiDebug();
//					try {
//						((ConParsePredictor)predictor).saveModel("10553.model");
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
				
				
				System.out.println("i="+i+";s="+j);

				Instance inst = training[j];
				
				Features feats;

			    feats = training[j].getFeatures();

			    //The old method of average parameter was dropped
				//double avg_upd = (double) (numIters * training.length - ((training.length * i) + j));
				
				int iStep=predictor.getStep();
				
				update.update(inst, feats, predictor, iStep);
				
				predictor.nextStep();
			}
////			//test------------------------------------------------------
//			ConParseDataManager manager=((ConParsePredictor)predictor).getManager();
//			
//			ConParseInstance[] sis=manager.readTestData("J:/zhouh/eclipse-workspace/NJU-Parser/data/CTB5/testi.txt");
//			
//			//store the predction into the arrays
//			ConParsePrediction[] pres=new ConParsePrediction[sis.length];
//			
//			long startTime=System.nanoTime();
//			for(int i_tmp=0;i_tmp<sis.length;i_tmp++){
//				
//				System.out.println("test: "+i_tmp);
//				
//				pres[i_tmp]=(ConParsePrediction) predictor.decode(sis[i_tmp], null);
//			}
//			long endTime=System.nanoTime();
//			System.out.println("Tss cost time:	"+(endTime-startTime)+"	ns");
//			
//			//save the parsed tree into the file
//			manager.saveParsedFile(pres, "J:/zhouh/eclipse-workspace/NJU-Parser/data/CTB5/result/BS.result"+i);
////			//test end--------------------------------------------------
		}

//		if (avgParams) {
//			System.out.println("Averaging parameters...");
//			predictor.averageWeights(numIters * training.length);
//		}
	}

	public void trainAndEvaluate(Instance[] training, Instance[] testing,
			OnlineUpdator update, Predictor predictor, int numIters,
			String trainingFeatureFile, String testingFeatureFile,
			Evaluator eval) throws IOException {

		trainAndEvaluate(training, testing, update, predictor, numIters,
				trainingFeatureFile, testingFeatureFile, eval, true);

	}

	public void trainAndEvaluate(Instance[] training, Instance[] testing,
			OnlineUpdator update, Predictor predictor, int numIters,
			String trainingFeatureFile, String testingFeatureFile,
			Evaluator eval, boolean avgParams) throws IOException {

		train(training, update, predictor, numIters, trainingFeatureFile,
				avgParams);

		/*
		 * for (int iterNum = 0; iterNum < numIters; iterNum++) { //
		 * System.out.print(i + " ");
		 * System.out.println("==========================");
		 * System.out.println("Training iteration: " + iterNum);
		 * System.out.println("=========================="); long start =
		 * System.currentTimeMillis();
		 * 
		 * ObjectInputStream in = new ObjectInputStream(new FileInputStream(
		 * trainingFeatureFile));
		 * 
		 * for (int instIdx = 0; instIdx < training.length; instIdx++) { //
		 * System.out.print(".");
		 * 
		 * Instance inst = training[instIdx]; Features feats =
		 * training[instIdx].getFeatures(in);
		 * 
		 * double avg_upd = (double) (numIters training.length -
		 * (training.length * ((iterNum + 1) - 1) + (instIdx + 1)) + 1);
		 * 
		 * update.update(inst, feats, predictor, avg_upd);
		 * 
		 * }
		 * 
		 * // System.out.println("");
		 * 
		 * in.close();
		 * 
		 * long end = System.currentTimeMillis();
		 * System.out.println("Training took: " + (end - start)); //
		 * System.out.println("Training"); //
		 * eval.evaluate(training,predictor,trainingFeatureFile); //
		 * System.out.println("Testing"); //
		 * eval.evaluate(testing,predictor,testingFeatureFile); }
		 * System.out.println("");
		 * 
		 * if (avgParams) { System.out.println("Averaging parameters...");
		 * predictor.averageWeights(numIters * training.length); }
		 */
		System.out.println("==========================");
		System.out.println("Final Performance.");
		System.out.println("==========================");
		System.out.println("Training");
		eval.evaluate(training, predictor, trainingFeatureFile);
		System.out.println("Testing");
		eval.evaluate(testing, predictor, testingFeatureFile);

	}

}
