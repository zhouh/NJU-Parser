package edu.nju.nlp.online.types;

import java.io.IOException;

import edu.nju.nlp.online.alg.Predictor;

public interface Evaluator {

    public void evaluate(Instance[] data, Predictor pred);

    public void evaluate(Instance[] data, Predictor pred, String featureFile) throws IOException;

}
