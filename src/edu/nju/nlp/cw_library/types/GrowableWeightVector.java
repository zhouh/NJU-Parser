package edu.nju.nlp.cw_library.types;

import gnu.trove.list.array.TDoubleArrayList;

/**
 * An implementation of a growable weight vector. This implementation uses TDoubleArrayList from the gnu trove collection.
 * @author Mark Dredze
 *
 */
public class GrowableWeightVector implements IWeightVector {
	
	protected final TDoubleArrayList _vector;
	protected double _default_value;
	
	public GrowableWeightVector(int size, double default_value) {
		this._vector = new TDoubleArrayList(size);
		this._default_value = default_value;
	}
	
	public int size() {
		return this._vector.size();
	}
	
	public double get(int index) {
		if (index >= this._vector.size())
			return this._default_value;
		return this._vector.get(index);
	}
	
	public void set(int index, double value) {
		// i'm not sure if this is a reasonable way to do things, so i'll leave it commented for now.
		// grow(index); 
		this._vector.set(index, value);
	}

	public IWeightVector createVectorOfSameType() {
		return new GrowableWeightVector(this.size(), this._default_value);
	}
	
	public void fill(double value) {
		this._vector.fill(value);
	}
	
	public void addToEnd(int positions, double value) {
		for (int ii = 0; ii < positions; ii++) {
			this._vector.add(value);
		}
	}
	
	public void grow(int size) {
		int gap = size - this.size();
		if(gap > 0)
			this.addToEnd(gap,this._default_value);
	}
	
	public void print() {
		// TODO
		System.out.println("Not implemented");
	}

	public IWeightVector copy() {
		GrowableWeightVector copy = new GrowableWeightVector(this._vector.size(), this._default_value);
		copy.grow(this._vector.size());
		for (int ii = 0; ii < this._vector.size(); ii++) {
			copy.set(ii, this._vector.get(ii));
		}
		return copy;
	}
}
