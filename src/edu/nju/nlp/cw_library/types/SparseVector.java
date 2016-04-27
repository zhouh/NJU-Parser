package edu.nju.nlp.cw_library.types;

/**
 * A sparse vector, represented by two lists: one for indices and one for
 * values. This class has limited usage and is primarily intended as a temporary
 * holder and not a robust SparseVector.
 * 
 * Assumptions: elements are added and accessed in order.
 * 
 * @author Mark Dredze
 * 
 */
public class SparseVector {
	int[] indicies = null;
	double[] values = null;
	int location = 0;

	public SparseVector(int size) {
		indicies = new int[size];
		values = new double[size];
	}

	public SparseVector(int[] i, double[] v) {
		indicies = i;
		values = v;
		location = i.length;
	}

	public int numLocations() {
		return indicies.length;
	}

	public int indexAtLocation(int ii) {
		return indicies[ii];
	}

	public void setValueAtLocation(int location, double value) {
		values[location] = value;
	}

	public double valueAtLocation(int ii) {
		return values[ii];
	}

	public void addValue(int index, double value) {
		indicies[location] = index;
		values[location] = value;
		location++;
	}

	public void print() {
		for (int ii = 0; ii < numLocations(); ii++)
			System.out.print("(" + indicies[ii] + ") = " + values[ii] + ", ");
		System.out.println();
	}
}