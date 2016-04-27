package edu.nju.nlp.parser.constituent.tss;

/**
 * the base class of features
 * 
 * @author Hao Zhou
 * */
public abstract class AtomicFeatures
{
	protected final int numFeatures;  
	protected long[] features;   

	protected AtomicFeatures(int n)  
	{
		numFeatures = n;
		features = new long[n];
	}

	public long get(int n)
	{
		return features[n];
	}
}