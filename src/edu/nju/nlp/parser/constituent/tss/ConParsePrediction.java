package edu.nju.nlp.parser.constituent.tss;

import edu.nju.nlp.online.types.Label;
import edu.nju.nlp.online.types.Prediction;

public class ConParsePrediction implements Prediction {

	public ConParseLabel[] labels;
	
	public ConParsePrediction(ConParseLabel[] labels){
		this.labels=labels;
	}

	public ConParsePrediction(ConParseLabel label) {
		this.labels = new ConParseLabel[1];
		this.labels[0] = label;
	}
	
	@Override
	public Label getBestLabel() {
		return labels[0];
	}

	@Override
	public Label getLabelByRank(int rank) {
		if (rank >= labels.length)
			return null;
		return labels[rank];
	}
	public ConParseLabel[] getAllLabels() {
		return labels;
	}

	public int getNumLabels() {
		return labels.length;
	}

}
