package edu.nju.nlp.online.types;

public interface Prediction {
    public Label getBestLabel();
    public Label getLabelByRank(int rank);
}
