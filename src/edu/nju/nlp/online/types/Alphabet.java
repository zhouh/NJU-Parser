/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */

/** 
 @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 */

package edu.nju.nlp.online.types;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

public class Alphabet implements Serializable {
	TObjectIntHashMap map;
	ArrayList entries;
	boolean growthStopped = false;   //����ĸ�� ��������Ԫ��
	Class entryClass = null;

	public Alphabet(int capacity, Class entryClass) {
		this.map = new TObjectIntHashMap(capacity);
		this.entries = new ArrayList(capacity);
		this.entryClass = entryClass;
	}

	public Alphabet(Class entryClass) {
		this(8, entryClass);
	}

	public Alphabet(int capacity) {
		this(capacity, null);
	}

	public Alphabet() {
		this(8, null);
	}

//	public Object clone() {
//		// try {
//		// Wastes effort, because we over-write ivars we create
//		Alphabet ret = new Alphabet();
//		ret.map = (TObjectIntHashMap) map.clone();
//		ret.entries = (ArrayList) entries.clone();
//		ret.growthStopped = growthStopped;
//		ret.entryClass = entryClass;
//		return ret;
//		// } catch (CloneNotSupportedException e) {
//		// e.printStackTrace();
//		// throw new IllegalStateException
//		// ("Couldn't clone InstanceList Vocabuary");
//		// }
//	}

	/** Return -1 if entry isn't present. 
	 *  ��ѯ ����int ���û�ҵ� ��������� �� ���� ���ض�Ӧ��value
	 * */
	public int lookupIndex(Object entry, boolean addIfNotPresent) { 
		if (entry == null)
			throw new IllegalArgumentException(
					"Can't lookup \"null\" in an Alphabet.");
		if (entryClass == null)
			entryClass = entry.getClass();     //����ʵ�����
		else
		// Insist that all entries in the Alphabet are of the same
		// class. This may not be strictly necessary, but will catch a
		// bunch of easily-made errors.����ĸ�������е�Ԫ�ض���ͳһ����
		if (entry.getClass() != entryClass)
			throw new IllegalArgumentException("Non-matching entry class, "
					+ entry.getClass() + ", was " + entryClass);
		
		int ret;
		if ( map.contains(entry) )
			ret = map.get(entry);  //����entry��Ϊkey��Ӧ��value
		else
			ret = -1;
		 
		//���û�и�Ԫ��
		if (ret == -1 && !growthStopped && addIfNotPresent) {

			ret = entries.size();
			map.put(entry, ret);  //����   �������Ӧλ����Ϊ���ֵ
			entries.add(entry);   
		}
		return ret;
	}

	//���� û�оͲ��� ���ض�Ӧkey��value
	public int lookupIndex(Object entry) {
		return lookupIndex(entry, true);
	}

	//����index ��Ϊvalue��Ӧ��key ʵ��
	public Object lookupObject(int index) {
		return entries.get(index);
	}

	public Object[] toArray() {
		return entries.toArray();
	}

	// xxx This should disable the iterator's remove method...
	public Iterator iterator() {
		return entries.iterator();
	}

	public Object[] lookupObjects(int[] indices) {
		Object[] ret = new Object[indices.length];
		for (int i = 0; i < indices.length; i++)
			ret[i] = entries.get(indices[i]);
		return ret;
	}

	public int[] lookupIndices(Object[] objects, boolean addIfNotPresent) {
		int[] ret = new int[objects.length];
		for (int i = 0; i < objects.length; i++)
			ret[i] = lookupIndex(objects[i], addIfNotPresent);
		return ret;
	}

	public boolean contains(Object entry) {
		return map.contains(entry);
	}

	public int size() {
		return entries.size();
	}

	public void stopGrowth() {
		growthStopped = true;
	}

	public void allowGrowth() {
		growthStopped = false;
	}

	public boolean growthStopped() {
		return growthStopped;
	}

	public Class entryClass() {
		return entryClass;
	}

	/**
	 * Return String representation of all Alphabet entries, each separated by a
	 * newline.
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < entries.size(); i++) {
			sb.append(entries.get(i).toString());
			sb.append('\n');
		}
		return sb.toString();
	}
	public String toStringIndex(int index){
		if(index>=entries.size()) {
			System.out.println("����");
			return null;
		}
		return entries.get(index).toString();
		
	}

	public void dump() {
		dump(System.out);
	}

	public void dump(PrintStream out) {
		for (int i = 0; i < entries.size(); i++) {
			out.println(i + " => " + entries.get(i));
		}
	}

	// Serialization

	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 0;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(CURRENT_SERIAL_VERSION);
		out.writeInt(entries.size());
		for (int i = 0; i < entries.size(); i++)
			out.writeObject(entries.get(i));
		out.writeBoolean(growthStopped);
		out.writeObject(entryClass);
	}

	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int version = in.readInt();
		int size = in.readInt();
		entries = new ArrayList(size);
		map = new TObjectIntHashMap(size);
		for (int i = 0; i < size; i++) {
			Object o = in.readObject();
			map.put(o, i);
			entries.add(o);
		}
		growthStopped = in.readBoolean();
		entryClass = (Class) in.readObject();
	}

}
