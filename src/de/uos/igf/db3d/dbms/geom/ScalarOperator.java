/*
 * Copyright (C) Prof. Martin Breunig
 */

package de.uos.igf.db3d.dbms.geom;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import de.uos.igf.db3d.dbms.structure.PersistentObject;

/**
 * Class ScalarOperator - for epsilon-based arithmetic comparisons.<br>
 * Currently the epsilon for the ScalarOperator has to be something like<br>
 * 1/(10^n) where n is a natural number <br>
 * for example 0.01, 0.001, 0.0001 ... <br>
 * 
 * @author Wolfgang Baer / University of Osnabrueck
 */
public class ScalarOperator implements PersistentObject, Externalizable {

	/* persistent epsilon */
	private double epsilon;

	/**
	 * Default constructor.<br>
	 * Initializes the object with an epsilon of 0.0001
	 */
	public ScalarOperator() {
		this.epsilon = 0.0001;
	}

	/**
	 * Constructor.
	 * 
	 * @param epsilon
	 *            -double value Must be a double in form 1/10^n
	 */
	public ScalarOperator(double epsilon) {
		this.epsilon = epsilon;
	}

	// relation methods

	/**
	 * Checks whether the first parameter is equal to the second parameter.
	 * 
	 * @param first
	 *            double
	 * @param second
	 *            double
	 * @return boolean - true if equal within tolerance.
	 */
	public boolean equal(double first, double second) {
		double diff = first - second;
		if (diff > 0)
			return (diff < this.getEpsilon());
		return (diff > this.getEpsilonNeg());
	}

	/**
	 * Checks whether the first parameter is less than the second parameter.
	 * 
	 * @param first
	 *            double
	 * @param second
	 *            s double
	 * @return boolean - true if less within tolerance.
	 */
	public boolean lessThan(double first, double second) {
		return ((second - first) >= this.getEpsilon());
	}

	/**
	 * Checks whether the first parameter is greater than the second parameter.
	 * 
	 * @param first
	 *            double
	 * @param second
	 *            double
	 * @return boolean - true if greater within tolerance.
	 */
	public boolean greaterThan(double first, double second) {
		return ((first - second) >= this.getEpsilon());
	}

	/**
	 * Checks whether the first parameter is less or equal to the second
	 * parameter.
	 * 
	 * @param first
	 *            double
	 * @param second
	 *            double
	 * @return boolean - true if less or equal within tolerance.
	 */
	public boolean lessOrEqual(double first, double second) {
		double diff = second - first;
		if (diff >= 0)
			return true;
		return (diff > this.getEpsilonNeg());
	}

	/**
	 * Checks whether the first parameter is greater or equal to the second
	 * parameter.
	 * 
	 * @param first
	 *            double
	 * @param second
	 *            double
	 * @return boolean - true if greater or equal within tolerance.
	 */
	public boolean greaterOrEqual(double first, double second) {
		double diff = first - second;
		if (diff >= 0)
			return true;
		return (diff > this.getEpsilonNeg());
	}

	/**
	 * Returns a deep copy of this.
	 * 
	 * @return ScalarOperator
	 */
	public ScalarOperator copy() {
		return new ScalarOperator(this.getEpsilon());
	}

	/**
	 * Returns the epsilon for this.
	 * 
	 * @return double - epsilon
	 */
	public double getEpsilon() {
		return epsilon;
	}

	/**
	 * Returns the negative epsilon for this.
	 * 
	 * @return double - negative epsilon
	 */
	public double getEpsilonNeg() {
		return -1 * getEpsilon();
	}

	/**
	 * Reads the value for epsilon from an external source.
	 * 
	 * @param in
	 *            ObjectInput from which the new value is read
	 * @throws IOException
	 *             if an input error occurred.
	 * @throws ClassNotFoundException
	 *             if the class type of the input object was not found.
	 * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
	 */
	public void readExternal(ObjectInput in) throws IOException,
			ClassNotFoundException {
		this.epsilon = in.readDouble();
	}

	/**
	 * Writes the epsilon value to an external ObjectOutput.
	 * 
	 * @param out
	 *            ObjectOutput to which the epsilon value is written
	 * @throws IOException
	 *             if an output error occurred.
	 * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
	 */
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeDouble(getEpsilon());
	}

	@Override
	/**
	 * Converts this to string.
	 * @return String with the information of this.
	 */
	public String toString() {
		return "ScalarOperator [epsilon=" + epsilon + "]";
	}

	@Override
	/**
	 * Returns the hash code of this.
	 * @return int - hash code of this.
	 */
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(epsilon);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	/**
	 * Tests if this is equal to the given object.
	 * @param obj Object for test
	 * @return boolean - true if equal, false otherwise.
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ScalarOperator other = (ScalarOperator) obj;
		if (Double.doubleToLongBits(epsilon) != Double
				.doubleToLongBits(other.epsilon))
			return false;
		return true;
	}

}
