package edu.nju.nlp.online.alg;

import java.io.IOException;

import edu.nju.nlp.online.types.Instance;

public interface BatchUpdator {

    public void update(Instance[] inst, Predictor pred, String featureFile) throws IOException;

}
