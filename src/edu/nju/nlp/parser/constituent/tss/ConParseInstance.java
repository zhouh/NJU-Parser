package edu.nju.nlp.parser.constituent.tss;

import java.io.IOException;
import java.io.ObjectInputStream;

import edu.nju.nlp.online.types.FeatureVector;
import edu.nju.nlp.online.types.Features;
import edu.nju.nlp.online.types.Input;
import edu.nju.nlp.online.types.Instance;
import edu.nju.nlp.online.types.Label;
import edu.nju.nlp.online.types.MultiHashAlphabet;

/**
 * 
 * @author Hao Zhou
 * */
public class ConParseInstance implements Instance {

	protected ConParseInput input;
	protected ConParseLabel label;
	protected ConParseFeatures features;
	
	protected static MultiHashAlphabet dataAlphabet;
	
	/**
	 * null constructor
	 * */
	public ConParseInstance() {

	}
	
	/**
	 * constructor
	 * just reference
	 * */
	public ConParseInstance(ConParseInput input,ConParseLabel label,ConParseFeatures features) {
		this.input=input;
		this.label=label;
		this.features=features;
	}
	
	@Override
	public Input getInput() {
		return input;
	}

	@Override
	public Label getLabel() {
		return label;
	}

	@Override
	public Features getFeatures() {
		return features;
	}

	/**
	 * do nothing here
	 * */
	@Override
	public Features getFeatures(ObjectInputStream in) throws IOException {
		return null;
	}
	
	/**
	 * set the Alphabet
	 * the dataAlphabet is static here, all instance has only one Alpahbet
	 * the Alphabet may be set by the dataManager
	 * */
	public static void setDataAlphabet(MultiHashAlphabet dataAlphabet1){
		dataAlphabet=dataAlphabet1;
	}
	
	/**
	 * get the dataAlphabet
	 * */
	public MultiHashAlphabet getAlphabet(){
		return dataAlphabet;
	}
	
	public void setLableFeature(FeatureVector fv){
		label.setFeature(fv);
	}

}
