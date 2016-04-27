package edu.nju.nlp.online.types;

import java.io.Serializable;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;

public class MultiHashAlphabet implements Serializable{

	// Serialization
	private static final long serialVersionUID = 1;
	/*
	 * The generic arrays of map.
	 * Each of the map array is a map for the feature template
	 */
	private TLongObjectHashMap<TIntIntHashMap>[] maps;
	private int size;
	boolean growthStopped = false;   

	/**
	 * Initial the maps of map of map
	 * The initial load factor is 0.9
	 * 
	 * @param capacity
	 * @param ArraySize
	 */
	@SuppressWarnings("unchecked")
	public MultiHashAlphabet(int capacity,int ArraySize) {
		
		maps =(TLongObjectHashMap<TIntIntHashMap>[]) new TLongObjectHashMap[ArraySize];
		
		for(int i=0;i<maps.length;i++){
			maps[i]=new TLongObjectHashMap<TIntIntHashMap>(capacity,(float) 0.9,-1);
		}
		size=0;	//No element in the Hash Map
	}

	public MultiHashAlphabet() {
		this(100000, 100);
	}

	/** Return -1 if entry isn't present. 
	 *  
	 * */
//	public int lookupIndex(int featureID, int actionID, long entry, boolean addIfNotPresent) { 
//		
//		int ret;
//		if ( maps[featureID].contains(entry) )
//			ret = maps[featureID].get(entry).get(actionID);  // we will set the default noneValue return -2
//		else
//			ret = -1;
//		 
//		if (ret <0 && !growthStopped && addIfNotPresent) {
//
//			if(ret==-1) {
//				maps[featureID].put(entry, new TIntIntHashMap(129,(float) 1.5,-1,-2));
//				maps[featureID].get(entry).put(actionID, (ret=size++));
//			}
//			else if(ret==-2) maps[featureID].get(entry).put(actionID, (ret = size++));
//		}
//		return ret;
//	}
	
	public int InsertInt2Map(TIntIntHashMap map,int actionID){
		int ret;
		map.put(actionID, (ret = size++));
		return ret;
	}

	/**
	 * Only for look index batch in query without updating
	 * if the key exits, return the Int2Int HashMap,
	 * else return null.
	 * 
	 * @param featureID
	 * @param entry
	 */
	public TIntIntHashMap lookupIndexBatch(int featureID,long entry,boolean addIfNotPresent){

		if ( maps[featureID].contains(entry) )
			return maps[featureID].get(entry);
		else if(addIfNotPresent){
			TIntIntHashMap indexMap=new TIntIntHashMap(513,(float) 1.5,-1,-2);
			maps[featureID].put(entry, indexMap);
			return indexMap;
			
		}
		else return null;
	}

	public int size() {
		return size;
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
}
