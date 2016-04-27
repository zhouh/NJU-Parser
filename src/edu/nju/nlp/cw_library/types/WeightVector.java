package edu.nju.nlp.cw_library.types;

import java.util.Arrays;

/**
 * A standard weight vector represented by an array of doubles.
 * @author Mark Dredze
 *
 */
public class WeightVector implements IWeightVector {

	protected final double[] _vector;
	
	public WeightVector(int size) {
		this._vector = new double[size]; 
	}
	
	/**
	 * Create a new weight vector around the provided vector.
	 * @param vector
	 */
	public WeightVector(double[] vector) {
		this._vector = vector; 
	}
	
	public double get(int index) {
		return this._vector[index];
	}

	public void set(int index, double value) {
		this._vector[index] = value;
	}

	public int size() {
		return this._vector.length;
	}

	public IWeightVector createVectorOfSameType() {
		return new WeightVector(this.size());
	}

	public void fill(double value) {
		Arrays.fill(this._vector, value);
		
	}

	public void print() {
		for(int i=0; i<this._vector.length; i++)
			System.out.printf("%f ", this._vector[i]);
		System.out.println();	
	}

	public IWeightVector copy() {
		WeightVector copy = new WeightVector(this._vector.length);
		System.arraycopy(this._vector, 0, copy._vector, 0, this._vector.length);
		return copy;
	}
}
