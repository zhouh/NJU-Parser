package edu.nju.nlp.online.alg;

import edu.nju.nlp.online.types.Features;
import edu.nju.nlp.online.types.Instance;

public interface OnlineUpdator {

    public void update(Instance inst, Features feats, Predictor pred, double avg_upd);

}
