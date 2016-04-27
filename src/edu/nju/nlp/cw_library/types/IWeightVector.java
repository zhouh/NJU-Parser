package edu.nju.nlp.cw_library.types;

/**
 * A weight vector interface. Allows access to and setting of elements in the vector.
 * @author Mark Dredze
 *
 */
public interface IWeightVector {
	public int size();
	
	public double get(int index);
	
	public void set(int index, double value);

	public IWeightVector createVectorOfSameType();
	
	public void fill(double value);
	
	public void print();

	public IWeightVector copy();
}
