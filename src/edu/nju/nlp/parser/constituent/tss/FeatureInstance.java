package edu.nju.nlp.parser.constituent.tss;

import java.util.Arrays;

/**
 * 
 * The instantiation of a feature 
 * 
 * @author Hao Zhou
 *
 */
public class FeatureInstance {

	private String features[];	// atomic feature String array 
	private String name;	// feature name
	
	/**
	 * Construction
	 * 
	 * @param name
	 * @param features
	 */
	public FeatureInstance(String name, String[] features){
		this.name=name;
		this.features=features;
	}
	
	public void setFeatureInstance(String name, String[] features){
		this.name=name;
		this.features=features;
	}
	
	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + Arrays.hashCode(features);
		result = 31 * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		FeatureInstance other = (FeatureInstance) obj;
		if (!Arrays.equals(features, other.features))
			return false;
		if (!name.equals(other.name)) 
			return false;
		return true;
	}
	

}
