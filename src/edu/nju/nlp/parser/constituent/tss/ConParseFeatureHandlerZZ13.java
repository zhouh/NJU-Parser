package edu.nju.nlp.parser.constituent.tss;

import java.util.ArrayList;
import edu.nju.nlp.online.types.FeatureVector;
import edu.nju.nlp.online.types.MultiHashAlphabet;
import edu.nju.nlp.parser.transition.ConAction;
import edu.nju.nlp.parser.types.ConstituentLabel;
import edu.nju.nlp.parser.types.Tag;
import edu.nju.nlp.parser.types.Word;
import gnu.trove.map.hash.TIntIntHashMap;

/** 
 * Constituent parsing features from Zhang and Clark (2009)
 * The Chinese punctuation and bracket feature are removed from the parser for 
 * language independent.
 * 
 * @author Hao Zhou
 */
public class ConParseFeatureHandlerZZ13  extends ConParseFeatureHandler
{

	/**
	 * Atomic feature subclass
	 * All atomic features are extracted here
	 * */
	class AtomicFeaturesZZ13 extends AtomicFeatures
	{
		//TODO a lot of bracket and separator feature is not added
		
		ConParseTSSStateNode S0 ;
		ConParseTSSStateNode S1 ;
		ConParseTSSStateNode S2 ;
		ConParseTSSStateNode S3 ;
		ConParseTSSStateNode S0u;
		ConParseTSSStateNode S0r;
		ConParseTSSStateNode S0l;
		ConParseTSSStateNode S1u;
		ConParseTSSStateNode S1r;
		ConParseTSSStateNode S1l;
		ConParseTSSStateNode S0ll;
		ConParseTSSStateNode S0lr;
		ConParseTSSStateNode S0lu;
		ConParseTSSStateNode S0rl;
		ConParseTSSStateNode S0rr;
		ConParseTSSStateNode S0ru;
		ConParseTSSStateNode S0ul;
		ConParseTSSStateNode S0ur;
		ConParseTSSStateNode S0uu;
		ConParseTSSStateNode S1ll;
		ConParseTSSStateNode S1lr;
		ConParseTSSStateNode S1lu;
		ConParseTSSStateNode S1rl;
		ConParseTSSStateNode S1rr;
		ConParseTSSStateNode S1ru;
		ConParseTSSStateNode S1ul;
		ConParseTSSStateNode S1ur;
		ConParseTSSStateNode S1uu;
		
		int N0=-1;
		int N1=-1;
		int N2=-1;
		int N3=-1;
		//feature id
		
		/*
		 * All the feature id involved in the feature template
		 */
		//S0
		static final int F_S0wt=0;   
		static final int F_S0c=1; //S0c!=NONE  
		static final int F_S0tc=2;   
		static final int F_S0wc=3;
		
		//S1
		static final int F_S1wt=4;   
		static final int F_S1c=5; 	//S1c!=NONE  
		static final int F_S1tc=6;   
		static final int F_S1wc=7;
		
		//S2
		static final int F_S2tc=8;   
		static final int F_S2wc=9;
		
		//S3
		static final int F_S3tc=10;   
		static final int F_S3wc=11;
		
		//N0 N1 N2 N3
		static final int F_N0wt=12;   
		static final int F_N1wt=13;   
		static final int F_N2wt=14;   
		static final int F_N3wt=15;   
		
		//S0l S0r S0u
		static final int F_S0ltc=16;   
		static final int F_S0lwc=17;   
		static final int F_S0rtc=18;   
		static final int F_S0rwc=19;
		static final int F_S0utc=20;   
		static final int F_S0uwc=21;
		
		//S1l S1r S1u
		static final int F_S1ltc=22;   
		static final int F_S1lwc=23;   
		static final int F_S1rtc=24;   
		static final int F_S1rwc=25;
		static final int F_S1utc=26;   
		static final int F_S1uwc=27;
		
		//new feature
	    static final int F_S0llwc=28;
	    static final int F_S0lrwc=29;
	    static final int F_S0luwc=30;
	    static final int F_S0rlwc=31;
	    static final int F_S0rrwc=32;
	    static final int F_S0ruwc=33;
	    static final int F_S0ulwc=34;
	    static final int F_S0urwc=35;
	    static final int F_S0uuwc=36;
	    static final int F_S1llwc=37;
	    static final int F_S1lrwc=38;
	    static final int F_S1luwc=39;
	    static final int F_S1rlwc=40;
	    static final int F_S1rrwc=41;
	    static final int F_S1ruwc=41;
	    static final int F_S1ulwc=43;
	    static final int F_S1urwc=44;
	    static final int F_S1uuwc=45;
		
	    //S0 S1
		static final int F_S0wS1wS0cS1c=46;
		static final int F_S1wS0cS1c=47;
		static final int F_S0wS0cS1c=48;
		static final int F_S0cS1c=49;
		
		//S0 N0
		static final int F_S0wN0wS0cN0t=50;
		static final int F_N0wS0cN0t=51;
		static final int F_S0wS0cN0t=52;
		static final int F_S0cN0t=53;
		
		//S1 N0
		static final int F_S1wN0wS1cN0t=54;
		static final int F_N0wS1cN0t=55;
		static final int F_S1wS1cN0t=56;
		static final int F_S1cN0t=57;
		
		//N0 N1
		static final int F_N0wN1wN0tN1t=58;
		static final int F_N1wN0tN1t=59;
		static final int F_N0wN0tN1t=60;
		static final int F_N0tN1t=61;
		
		//S0 S1 N0
		static final int F_S0wS0cS1cN0t=62;
		static final int F_S1wS0cS1cN0t=63;	//S1 != 0
		static final int F_N0wS0cS1cN0t=64;	//N0 != -1
		static final int F_S0cS1cN0t=65;
		static final int F_S0tS1tN0t=66;
		
		//S0 N0 N1
		static final int F_S0wS0cN0tN1t=67;
		static final int F_N0wS0cN0tN1t=68;
		static final int F_N1wS0cN0tN1t=69;
		static final int F_S0cN0tN1t=70;
		static final int F_S0tN0tN1t=71;
		
		//S0 S1 S2
		static final int F_S0wS0cS1cS2c=72;
		static final int F_S1wS0cS1cS2c=73;
		static final int F_S2wS0cS1cS2c=74;
		static final int F_S0cS1cS2c=75;
		static final int F_S0tS1tS2t=76;
		
		//
		static final int F_S0cS0rcN0t=77;
		static final int F_S0cS0rjN0t=78;
		static final int F_N0wS0cS0rc=79;
		
		//S0 S0LRU S1
		static final int F_S0cS0lcS1c=80;
		static final int F_S0cS0ljS1j=81;
		static final int F_S1wS0cS0lc=82;
		static final int F_S0cS1cS1rc=83;
		static final int F_S0jS1cS1rj=84;
		static final int F_S0wS1cS1rc=85;
		
		//Numerical Feature 
		static final int headLM5=0;
		static final int PosLM5=1;
		
	    
		/**
		 * The index id of a part of feature
		 */
		
//		//bigram
//	    static final int F_S0wS1w=18;
//	    static final int F_S0wS1c=19;
//	    static final int F_S0cS1w=20;   
//	    static final int F_S0cS1c=21;
//	    static final int F_S0wN0w=22;  // 
//	    static final int F_S0wN0t=23;
//	    static final int F_S0cN0w=24;
//	    static final int F_S0cN0t=25;   
//	    static final int F_N0wN1w=26;
//	    static final int F_N0wN1t=27;
//	    static final int F_N0tN1w=28;
//	    static final int F_N0tN1t=29;
//	    static final int F_S1wN0w=30;
//	    static final int F_S1wN0t=31;
//	    static final int F_S1cN0w=32;
//	    static final int F_S1cN0t=33;
//		
//		//Trigram
//	    static final int F_S0cS1cS2c=34;
//	    static final int F_S0wS1cS2c=35;
//	    static final int F_S0cS1wS2c=36;
//	    static final int F_S0cS1cS2w=37;
//	    static final int F_S0cS1cN0t=38;
//	    static final int F_S0wS1cN0t=39;
//	    static final int F_S0cS1wN0t=40;
//	    static final int F_S0cS1cN0w=41;
	    
		
		
		static final int NUM_FEATURE = 88;  
		
		
		public AtomicFeaturesZZ13() {
			super(NUM_FEATURE);
			features = new long[NUM_FEATURE];   //新建一个特征数组
		}
	}

		public ConParseFeatureHandlerZZ13(MultiHashAlphabet dataAlphabet)
		{
			super(dataAlphabet);
		}

		@Override
		public FeatureVector getFeatures(AtomicFeatures atomic, ConAction act, FeatureVector v,boolean bAdd)
		{
			ArrayList<ConAction> actions=new ArrayList<ConAction>();
			actions.add(act);
			return FeatureVector.cat(v, createFeatureBatch(atomic,actions,bAdd)[0]);
			
		}
		
		@Override
		public FeatureVector[] createFeatureBatch(AtomicFeatures atomic,ArrayList<ConAction> actions,boolean addIfNotFound){
			
			AtomicFeaturesZZ13 atomicZZ13=(AtomicFeaturesZZ13)atomic;
			
			FeatureVector[] retval=new FeatureVector[actions.size()];
			
			TIntIntHashMap actionWeightIndexMap;
			
			for(int i=0;i<retval.length;i++)
				retval[i]=new FeatureVector(-1,-1.0,null);
			
			if(atomic==null) return retval;	//if the atomic feature string is null, return the feature vector directly!

		    //S0
			actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0wt, atomicZZ13.get(AtomicFeaturesZZ13.F_S0wt),addIfNotFound);
			if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0c, atomicZZ13.get(AtomicFeaturesZZ13.F_S0c),addIfNotFound);
			if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0tc, atomicZZ13.get(AtomicFeaturesZZ13.F_S0tc),addIfNotFound);
			if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0wc, atomicZZ13.get(AtomicFeaturesZZ13.F_S0wc),addIfNotFound);
			if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			
			//S1
			if(atomicZZ13.S1!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1wt, atomicZZ13.get(AtomicFeaturesZZ13.F_S1wt),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1c, atomicZZ13.get(AtomicFeaturesZZ13.F_S1c),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1tc, atomicZZ13.get(AtomicFeaturesZZ13.F_S1tc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1wc, atomicZZ13.get(AtomicFeaturesZZ13.F_S1wc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			
			//S2
			if(atomicZZ13.S2!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S2tc, atomicZZ13.get(AtomicFeaturesZZ13.F_S2tc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S2wc, atomicZZ13.get(AtomicFeaturesZZ13.F_S2wc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}

			//S3
			if(atomicZZ13.S3!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S3tc, atomicZZ13.get(AtomicFeaturesZZ13.F_S3tc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S3wc, atomicZZ13.get(AtomicFeaturesZZ13.F_S3wc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			
			//N0 N1 N2 N3
			if(atomicZZ13.N0!=-1){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_N0wt, atomicZZ13.get(AtomicFeaturesZZ13.F_N0wt),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.N1!=-1){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_N1wt, atomicZZ13.get(AtomicFeaturesZZ13.F_N1wt),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.N2!=-1){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_N2wt, atomicZZ13.get(AtomicFeaturesZZ13.F_N2wt),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.N3!=-1){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_N3wt, atomicZZ13.get(AtomicFeaturesZZ13.F_N3wt),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			
			//S0l
			if(atomicZZ13.S0l!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0ltc, atomicZZ13.get(AtomicFeaturesZZ13.F_S0ltc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0lwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S0lwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			
			//S0r
			if(atomicZZ13.S0r!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0rtc, atomicZZ13.get(AtomicFeaturesZZ13.F_S0rtc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0rwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S0rwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}

			//S0u
			if(atomicZZ13.S0u!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0utc, atomicZZ13.get(AtomicFeaturesZZ13.F_S0utc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0uwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S0uwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			
			//S1l
			if(atomicZZ13.S1l!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1ltc, atomicZZ13.get(AtomicFeaturesZZ13.F_S1ltc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1lwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S1lwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}

			//S1r
			if(atomicZZ13.S1r!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1rtc, atomicZZ13.get(AtomicFeaturesZZ13.F_S1rtc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1rwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S1rwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}

			//S1u
			if(atomicZZ13.S1u!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1utc, atomicZZ13.get(AtomicFeaturesZZ13.F_S1utc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1uwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S1uwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			
			//new feature
			if(atomicZZ13.S0ll!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0llwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S0llwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.S0lr!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0lrwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S0lrwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.S0lu!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0luwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S0luwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.S0lu!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0rlwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S0rlwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.S0rr!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0rrwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S0rrwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.S0ru!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0ruwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S0ruwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.S0ul!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0ulwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S0ulwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.S0ur!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0urwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S0urwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.S0uu!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0uuwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S0uuwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.S1ll!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1llwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S1llwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.S1lr!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1lrwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S1lrwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.S1lu!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1luwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S1luwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.S1rl!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1rlwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S1rlwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.S1rr!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1rrwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S1rrwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.S1ru!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1ruwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S1ruwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.S1ul!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1ulwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S1ulwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.S1ur!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1urwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S1urwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.S1uu!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1uuwc, atomicZZ13.get(AtomicFeaturesZZ13.F_S1uuwc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			
			//S0 S1
			if(atomicZZ13.S1!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0wS1wS0cS1c, atomicZZ13.get(AtomicFeaturesZZ13.F_S0wS1wS0cS1c),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1wS0cS1c, atomicZZ13.get(AtomicFeaturesZZ13.F_S1wS0cS1c),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0wS0cS1c, atomicZZ13.get(AtomicFeaturesZZ13.F_S0wS0cS1c),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0cS1c, atomicZZ13.get(AtomicFeaturesZZ13.F_S0cS1c),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}

			//S0 N0
			if(atomicZZ13.N0!=-1){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0wN0wS0cN0t, atomicZZ13.get(AtomicFeaturesZZ13.F_S0wN0wS0cN0t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_N0wS0cN0t, atomicZZ13.get(AtomicFeaturesZZ13.F_N0wS0cN0t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0wS0cN0t, atomicZZ13.get(AtomicFeaturesZZ13.F_S0wS0cN0t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0cN0t, atomicZZ13.get(AtomicFeaturesZZ13.F_S0cN0t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			
			//S1 N0
			if(atomicZZ13.S1!=null&&atomicZZ13.N0!=-1){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1wN0wS1cN0t, atomicZZ13.get(AtomicFeaturesZZ13.F_S1wN0wS1cN0t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_N0wS1cN0t, atomicZZ13.get(AtomicFeaturesZZ13.F_N0wS1cN0t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1wS1cN0t, atomicZZ13.get(AtomicFeaturesZZ13.F_S1wS1cN0t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1cN0t, atomicZZ13.get(AtomicFeaturesZZ13.F_S1cN0t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			
			//N0 N1
			if(atomicZZ13.N1!=-1){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_N0wN1wN0tN1t, atomicZZ13.get(AtomicFeaturesZZ13.F_N0wN1wN0tN1t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_N1wN0tN1t, atomicZZ13.get(AtomicFeaturesZZ13.F_N1wN0tN1t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_N0wN0tN1t, atomicZZ13.get(AtomicFeaturesZZ13.F_N0wN0tN1t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_N0tN1t, atomicZZ13.get(AtomicFeaturesZZ13.F_N0tN1t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			
			//S0 S1 N0
			actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0wS0cS1cN0t, atomicZZ13.get(AtomicFeaturesZZ13.F_S0wS0cS1cN0t),addIfNotFound);
			if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			
			if(atomicZZ13.S1!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1wS0cS1cN0t, atomicZZ13.get(AtomicFeaturesZZ13.F_S1wS0cS1cN0t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.N0!=-1){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_N0wS0cS1cN0t, atomicZZ13.get(AtomicFeaturesZZ13.F_N0wS0cS1cN0t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0cS1cN0t, atomicZZ13.get(AtomicFeaturesZZ13.F_S0cS1cN0t),addIfNotFound);
			if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0tS1tN0t, atomicZZ13.get(AtomicFeaturesZZ13.F_S0tS1tN0t),addIfNotFound);
			if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			
			//S0 N0 N1
			if(atomicZZ13.N0!=-1){
				
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0wS0cN0tN1t, atomicZZ13.get(AtomicFeaturesZZ13.F_S0wS0cN0tN1t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_N0wS0cN0tN1t, atomicZZ13.get(AtomicFeaturesZZ13.F_N0wS0cN0tN1t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				
				if(atomicZZ13.N1!=-1){
					actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_N1wS0cN0tN1t, atomicZZ13.get(AtomicFeaturesZZ13.F_N1wS0cN0tN1t),addIfNotFound);
					if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				}
				
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0cN0tN1t, atomicZZ13.get(AtomicFeaturesZZ13.F_S0cN0tN1t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0tN0tN1t, atomicZZ13.get(AtomicFeaturesZZ13.F_S0tN0tN1t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			
			if(atomicZZ13.S1!=null){
				
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0wS0cS1cS2c, atomicZZ13.get(AtomicFeaturesZZ13.F_S0wS0cS1cS2c),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1wS0cS1cS2c, atomicZZ13.get(AtomicFeaturesZZ13.F_S1wS0cS1cS2c),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				
				if(atomicZZ13.S2!=null){
					actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S2wS0cS1cS2c, atomicZZ13.get(AtomicFeaturesZZ13.F_S2wS0cS1cS2c),addIfNotFound);
					if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				}
				
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0cS1cS2c, atomicZZ13.get(AtomicFeaturesZZ13.F_S0cS1cS2c),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0tS1tS2t, atomicZZ13.get(AtomicFeaturesZZ13.F_S0tS1tS2t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			
			//N0 S0r
			if(atomicZZ13.N0!=-1&&atomicZZ13.S0r!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0cS0rcN0t, atomicZZ13.get(AtomicFeaturesZZ13.F_S0cS0rcN0t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0cS0rjN0t, atomicZZ13.get(AtomicFeaturesZZ13.F_S0cS0rjN0t),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_N0wS0cS0rc, atomicZZ13.get(AtomicFeaturesZZ13.F_N0wS0cS0rc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			
			// S0 S0LRU S1
			
			if(atomicZZ13.S1!=null&&atomicZZ13.S0l!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0cS0lcS1c, atomicZZ13.get(AtomicFeaturesZZ13.F_S0cS0lcS1c),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0cS0ljS1j, atomicZZ13.get(AtomicFeaturesZZ13.F_S0cS0ljS1j),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S1wS0cS0lc, atomicZZ13.get(AtomicFeaturesZZ13.F_S1wS0cS0lc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
			if(atomicZZ13.S1!=null&&atomicZZ13.S1r!=null){
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0cS0lcS1c, atomicZZ13.get(AtomicFeaturesZZ13.F_S0cS0lcS1c),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0jS1cS1rj, atomicZZ13.get(AtomicFeaturesZZ13.F_S0jS1cS1rj),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
				actionWeightIndexMap=dataAlphabet.lookupIndexBatch(AtomicFeaturesZZ13.F_S0wS1cS1rc, atomicZZ13.get(AtomicFeaturesZZ13.F_S0wS1cS1rc),addIfNotFound);
				if(actionWeightIndexMap!=null) addFeatureByActions(retval,actionWeightIndexMap,actions,addIfNotFound);
			}
		    return retval;
		}

		private void addFeatureByActions(FeatureVector[] retval,
				TIntIntHashMap actionWeightIndexMap,
				ArrayList<ConAction> actions,
				boolean addIfNotFound) {

			int index;
			
			for(int i=0;i<retval.length;i++){
//				if(actions.get(i).code()==45)
//					System.out.println();
				if((index=actionWeightIndexMap.get(actions.get(i).code()) )>=0){
					retval[i].add(index, 1.0);
//					if(index==133)
//						System.out.println("133 index found!");
				}
				else if(addIfNotFound){
					index=dataAlphabet.InsertInt2Map(actionWeightIndexMap, actions.get(i).code());
//					System.out.println("new index:	"+index);
					retval[i].add(index, 1.0);
				}
			}
		}

		@Override
		public AtomicFeatures getAtomicFeatures(ConParseTSSState state) {
			
			AtomicFeaturesZZ13 retval=new AtomicFeaturesZZ13();

			//if the state is null, the state may be a initial state, return null!
			if(state==null||state.node==null) return null;	
			
			/*
			 * if the state is a initial state, with no stack node then return null
			 */
			if(state.stackSize()==0) return null;
			
			// TODO if the node is null, the String here is NONE
			// but may be the feature with NONE should not insert the Alphabet
			// get the first 4 element of the top stack 
			ConParseTSSStateNode S0 = state.node;
			ConParseTSSStateNode S1 = state.stackSize>=2?state.stackPtr.node:null;
			ConParseTSSStateNode S2 = state.stackSize>=3?state.stackPtr.stackPtr.node:null;
			ConParseTSSStateNode S3 = state.stackSize>=4?state.stackPtr.stackPtr.stackPtr.node:null;
			ConParseTSSStateNode S0u=null;
			ConParseTSSStateNode S0r=null;
			ConParseTSSStateNode S0l=null;
			ConParseTSSStateNode S1u=null;
			ConParseTSSStateNode S1r=null;
			ConParseTSSStateNode S1l=null;
			ConParseTSSStateNode S0ll=null;
			ConParseTSSStateNode S0lr=null;
			ConParseTSSStateNode S0lu=null;
			ConParseTSSStateNode S0rl=null;
			ConParseTSSStateNode S0rr=null;
			ConParseTSSStateNode S0ru=null;
			ConParseTSSStateNode S0ul=null;
			ConParseTSSStateNode S0ur=null;
			ConParseTSSStateNode S0uu=null;
			ConParseTSSStateNode S1ll=null;
			ConParseTSSStateNode S1lr=null;
			ConParseTSSStateNode S1lu=null;
			ConParseTSSStateNode S1rl=null;
			ConParseTSSStateNode S1rr=null;
			ConParseTSSStateNode S1ru=null;
			ConParseTSSStateNode S1ul=null;
			ConParseTSSStateNode S1ur=null;
			ConParseTSSStateNode S1uu=null;
			
			int N0=100;
			int N1=100;
			int N2=100;
			int N3=100;
			
			//the sentence ConstituentLabel and tag sequence
			Word[] words_code=state.input.words_code;
			Tag[] tags_code=state.input.tags_code;
			
			
			
			int idx = state.currentWord;     //the index of the first element of the queue
			int sentLen=state.input.sentLen;     //the sentence size
			
			
			//get the left right unary children of stack element
			if(S0!=null&&!S0.beLeaf()) {
				
				if(S0.singleChild()) {
					S0u=S0.left_child;
			    }
			    else {
			    		S0l=S0.left_child;
			    		S0r=S0.right_child;
			    	}
				
			}
			
			if(S1!=null&&!S1.beLeaf()) {
				if(S1.singleChild()) {
					S1u=S1.left_child;
			    }
			    else {
			    		S1l=S1.left_child;
			    		S1r=S1.right_child;
			    	}
				
			}
			
			//get the 2 layer child feature
			//get the parent's left child's left child, the second kid of the node
			if(S0l!=null&&!S0l.beLeaf()) {
				if(S0l.singleChild()) {
					S0lu=S0l.left_child;
			    }
			    else {
			    	S0ll=S0l.left_child;
			    	S0lr=S0l.right_child;
			    	}
				
			}
			
			if(S0r!=null&&!S0r.beLeaf()) {
				if(S0r.singleChild()) {
					S0ru=S0r.left_child;
			    }
			    else {
			    	S0rl=S0r.left_child;
			    	S0rr=S0r.right_child;
			    	}
				
			}
			
			if(S0u!=null&&!S0u.beLeaf()) {
				if(S0u.singleChild()) {
					S0uu=S0u.left_child;
			    }
			    else {
			    	S0ul=S0u.left_child;
			    	S0ur=S0u.right_child;
			    	}
				
			}
			
			if(S1l!=null&&!S1l.beLeaf()) {
				if(S1l.singleChild()) {
					S1lu=S1l.left_child;
			    }
			    else {
			    	S1ll=S1l.left_child;
			    	S1lr=S1l.right_child;
			    	}
				
			}
			
			if(S1r!=null&&!S1r.beLeaf()) {
				if(S1r.singleChild()) {
					S1ru=S1r.left_child;
			    }
			    else {
			    	S1rl=S1r.left_child;
			    	S1rr=S1r.right_child;
			    	}
			}
			
			if(S1u!=null&&!S1u.beLeaf()) {
				if(S1u.singleChild()) {
					S1uu=S1.left_child;
				}
				else {
					S1ul=S1u.left_child;
					S1ur=S1u.right_child;
				}
			}

			//get the stack element's constituent label
			long S0c=S0!=null?ConstituentLabel.load(S0.constituent):ConstituentLabel.load("BEGIN");
			long S1c=S1!=null?ConstituentLabel.load(S1.constituent):ConstituentLabel.load("BEGIN");
			long S2c=S2!=null?ConstituentLabel.load(S2.constituent):ConstituentLabel.load("BEGIN");
			long S3c=S3!=null?ConstituentLabel.load(S3.constituent):ConstituentLabel.load("BEGIN");
			
			//get the stack  element's head ConstituentLabel
			long S0w=S0!=null?words_code[S0.lexical_head].getCode():Word.NONE;
			long S1w=S1!=null?words_code[S1.lexical_head].getCode():Word.NONE;
			long S2w=S2!=null?words_code[S2.lexical_head].getCode():Word.NONE;
			long S3w=S3!=null?words_code[S3.lexical_head].getCode():Word.NONE;
			
			//get the stack element's head ConstituentLabel's tag 
			long S0t=S0!=null?tags_code[S0.lexical_head].getCode():Tag.NONE;
			long S1t=S1!=null?tags_code[S1.lexical_head].getCode():Tag.NONE;
			long S2t=S2!=null?tags_code[S2.lexical_head].getCode():Tag.NONE;
			long S3t=S3!=null?tags_code[S3.lexical_head].getCode():Tag.NONE;
			
			//the node S3 is not in the stack.for the update of the feature add the s3 in the atomic feature
			
			//get the queue element's ConstituentLabel
			//get the queue element's tag,but as in joint model the tag of the queue is now UNSET
			long N0w;
			long N1w;
			long N2w;
			long N3w;
			long N0t;
			long N1t;
			long N2t;
			long N3t;
			
			//N0
			if(idx<sentLen){
				N0w=words_code[idx].getCode();
				N0t=tags_code[idx].getCode();
			}
			else{
				N0=-1;
				N0w=Word.NONE;
				N0t=Tag.NONE;
			}
			//N1
			if((idx+1)<sentLen){
				N1w=words_code[idx+1].getCode();
				N1t=tags_code[idx+1].getCode();
			}
			else{
				N1=-1;
				N1w=Word.NONE;
				N1t=Tag.NONE;
			}
			//N2
			if((idx+2)<sentLen){
				N2w=words_code[idx+2].getCode();
				N2t=tags_code[idx+2].getCode();
			}
			else{
				N2=-1;
				N2w=Word.NONE;
				N2t=Tag.NONE;
			}
			//N3
			if((idx+3)<sentLen){
				N3w=words_code[idx+3].getCode();
				N3t=tags_code[idx+3].getCode();
			}
			else{
				N3=-1;
				N3w=Word.NONE;
				N3t=Tag.NONE;
			}

			//get the left or right or unary child 's head ConstituentLabel or constituent 
			long S0lw=S0l!=null?words_code[S0l.lexical_head].getCode():Word.NONE;
			long S1lw=S1l!=null?words_code[S1l.lexical_head].getCode():Word.NONE;
			long S0rw=S0r!=null?words_code[S0r.lexical_head].getCode():Word.NONE;
			long S1rw=S1r!=null?words_code[S1r.lexical_head].getCode():Word.NONE;
			long S0uw=S0u!=null?words_code[S0u.lexical_head].getCode():Word.NONE;
			long S1uw=S1u!=null?words_code[S1u.lexical_head].getCode():Word.NONE;
			
			long S0lc=S0l!=null?ConstituentLabel.load(S0l.constituent):ConstituentLabel.load("BEGIN");
			long S1lc=S1l!=null?ConstituentLabel.load(S1l.constituent):ConstituentLabel.load("BEGIN");
			long S0rc=S0r!=null?ConstituentLabel.load(S0r.constituent):ConstituentLabel.load("BEGIN");
			long S1rc=S1r!=null?ConstituentLabel.load(S1r.constituent):ConstituentLabel.load("BEGIN");
			long S0uc=S0u!=null?ConstituentLabel.load(S0u.constituent):ConstituentLabel.load("BEGIN");
			long S1uc=S1u!=null?ConstituentLabel.load(S1u.constituent):ConstituentLabel.load("BEGIN");
			
			long S0lt=S0l!=null?tags_code[S0l.lexical_head].getCode():Tag.NONE;
			long S1lt=S1l!=null?tags_code[S1l.lexical_head].getCode():Tag.NONE;
			long S0rt=S0r!=null?tags_code[S0r.lexical_head].getCode():Tag.NONE;
			long S1rt=S1r!=null?tags_code[S1r.lexical_head].getCode():Tag.NONE;
			long S0ut=S0u!=null?tags_code[S0u.lexical_head].getCode():Tag.NONE;
			long S1ut=S1u!=null?tags_code[S1u.lexical_head].getCode():Tag.NONE;
			
			//get the second layer node feature of the node!
			long S0llw=S0ll!=null?words_code[S0ll.lexical_head].getCode():Word.NONE;
			long S0lrw=S0lr!=null?words_code[S0lr.lexical_head].getCode():Word.NONE;
			long S0luw=S0lu!=null?words_code[S0lu.lexical_head].getCode():Word.NONE;
			long S0rlw=S0rl!=null?words_code[S0rl.lexical_head].getCode():Word.NONE;
			long S0rrw=S0rr!=null?words_code[S0rr.lexical_head].getCode():Word.NONE;
			long S0ruw=S0ru!=null?words_code[S0ru.lexical_head].getCode():Word.NONE;
			long S0ulw=S0ul!=null?words_code[S0ul.lexical_head].getCode():Word.NONE;
			long S0urw=S0ur!=null?words_code[S0ur.lexical_head].getCode():Word.NONE;
			long S0uuw=S0uu!=null?words_code[S0uu.lexical_head].getCode():Word.NONE;
			long S1llw=S1ll!=null?words_code[S1ll.lexical_head].getCode():Word.NONE;
			long S1lrw=S1lr!=null?words_code[S1lr.lexical_head].getCode():Word.NONE;
			long S1luw=S1lu!=null?words_code[S1lu.lexical_head].getCode():Word.NONE;
			long S1rlw=S1rl!=null?words_code[S1rl.lexical_head].getCode():Word.NONE;
			long S1rrw=S1rr!=null?words_code[S1rr.lexical_head].getCode():Word.NONE;
			long S1ruw=S1ru!=null?words_code[S1ru.lexical_head].getCode():Word.NONE;
			long S1ulw=S1ul!=null?words_code[S1ul.lexical_head].getCode():Word.NONE;
			long S1urw=S1ur!=null?words_code[S1ur.lexical_head].getCode():Word.NONE;
			long S1uuw=S1uu!=null?words_code[S1uu.lexical_head].getCode():Word.NONE;
			
			long S0llc=S0ll!=null?Tag.load(S0ll.constituent):ConstituentLabel.load("BEGIN");
			long S0lrc=S0lr!=null?Tag.load(S0lr.constituent):ConstituentLabel.load("BEGIN");
			long S0luc=S0lu!=null?Tag.load(S0lu.constituent):ConstituentLabel.load("BEGIN");
			long S0rlc=S0rl!=null?Tag.load(S0rl.constituent):ConstituentLabel.load("BEGIN");;
			long S0rrc=S0rr!=null?Tag.load(S0rr.constituent):ConstituentLabel.load("BEGIN");
			long S0ruc=S0ru!=null?Tag.load(S0ru.constituent):ConstituentLabel.load("BEGIN");
			long S0ulc=S0ul!=null?Tag.load(S0ul.constituent):ConstituentLabel.load("BEGIN");
			long S0urc=S0ur!=null?Tag.load(S0ur.constituent):ConstituentLabel.load("BEGIN");
			long S0uuc=S0uu!=null?Tag.load(S0uu.constituent):ConstituentLabel.load("BEGIN");
			long S1llc=S1ll!=null?Tag.load(S1ll.constituent):ConstituentLabel.load("BEGIN");
			long S1lrc=S1lr!=null?Tag.load(S1lr.constituent):ConstituentLabel.load("BEGIN");
			long S1luc=S1lu!=null?Tag.load(S1lu.constituent):ConstituentLabel.load("BEGIN");
			long S1rlc=S1rl!=null?Tag.load(S1rl.constituent):ConstituentLabel.load("BEGIN");
			long S1rrc=S1rr!=null?Tag.load(S1rr.constituent):ConstituentLabel.load("BEGIN");
			long S1ruc=S1ru!=null?Tag.load(S1ru.constituent):ConstituentLabel.load("BEGIN");
			long S1ulc=S1ul!=null?Tag.load(S1ul.constituent):ConstituentLabel.load("BEGIN");
			long S1urc=S1ur!=null?Tag.load(S1ur.constituent):ConstituentLabel.load("BEGIN");
			long S1uuc=S1uu!=null?Tag.load(S1uu.constituent):ConstituentLabel.load("BEGIN");
			
			//S0
			retval.features[AtomicFeaturesZZ13.F_S0wt]=codeBigram(S0w, S0t);
			retval.features[AtomicFeaturesZZ13.F_S0c]=S0c;   
			retval.features[AtomicFeaturesZZ13.F_S0tc]=codeBigram(S1t,S1c);   
			retval.features[AtomicFeaturesZZ13.F_S0wc]=codeBigram(S0w,S0c);
			
			//S1
			if(S1!=null){
				retval.features[AtomicFeaturesZZ13.F_S1wt]=codeBigram(S1w,S1t);
				retval.features[AtomicFeaturesZZ13.F_S1c]=S1c;   
				retval.features[AtomicFeaturesZZ13.F_S1tc]=codeBigram(S1t,S1c);
				retval.features[AtomicFeaturesZZ13.F_S1wc]=codeBigram(S1w,S1c);
			}
			
			//S2
			if(S2!=null){
				retval.features[AtomicFeaturesZZ13.F_S2tc]=codeBigram(S2t,S2c);
				retval.features[AtomicFeaturesZZ13.F_S2wc]=codeBigram(S2w,S2c);
			}

			//S3
			if(S3!=null){
				retval.features[AtomicFeaturesZZ13.F_S3tc]=codeBigram(S3t,S3c);
				retval.features[AtomicFeaturesZZ13.F_S3wc]=codeBigram(S3w,S3c);
			}
			
			//N0 N1 N2 N3
			if(N0!=-1)
				retval.features[AtomicFeaturesZZ13.F_N0wt]=codeBigram(N0w,N0t);
			if(N1!=-1)
				retval.features[AtomicFeaturesZZ13.F_N1wt]=codeBigram(N1w,N1t);
			if(N2!=-1)
				retval.features[AtomicFeaturesZZ13.F_N2wt]=codeBigram(N2w,N2t);
			if(N3!=-1)
				retval.features[AtomicFeaturesZZ13.F_N3wt]=codeBigram(N3w,N3t);
			
			//S0l
			if(S0l!=null){
				retval.features[AtomicFeaturesZZ13.F_S0ltc]=codeBigram(S0lt,S0lc);
				retval.features[AtomicFeaturesZZ13.F_S0lwc]=codeBigram(S0lw,S0lc);
			}
			
			//S0r
			if(S0r!=null){
				retval.features[AtomicFeaturesZZ13.F_S0rtc]=codeBigram(S0rt,S0rc);
				retval.features[AtomicFeaturesZZ13.F_S0rwc]=codeBigram(S0rw,S0rc);
			}

			//S0u
			if(S0u!=null){
				retval.features[AtomicFeaturesZZ13.F_S0utc]=codeBigram(S0ut,S0uc);
				retval.features[AtomicFeaturesZZ13.F_S0uwc]=codeBigram(S0uw,S0uc);
			}
			
			//S1l
			if(S1l!=null){
				retval.features[AtomicFeaturesZZ13.F_S1ltc]=codeBigram(S1lt,S1lc);
				retval.features[AtomicFeaturesZZ13.F_S1lwc]=codeBigram(S1lw,S1lc);
			}

			//S1r
			if(S1r!=null){
				retval.features[AtomicFeaturesZZ13.F_S1rtc]=codeBigram(S1rt,S1rc);
				retval.features[AtomicFeaturesZZ13.F_S1rwc]=codeBigram(S1rw,S1rc);
			}

			//S1u
			if(S1u!=null){
				retval.features[AtomicFeaturesZZ13.F_S1utc]=codeBigram(S1ut,S1uc);
				retval.features[AtomicFeaturesZZ13.F_S1uwc]=codeBigram(S1uw,S1uc);
			}
			
			//new feature
			if(S0ll!=null)
				retval.features[AtomicFeaturesZZ13.F_S0llwc]=codeBigram(S0llw,S0llc);
			if(S0lr!=null)
				retval.features[AtomicFeaturesZZ13.F_S0lrwc]=codeBigram(S0lrw,S0lrc);
			if(S0lu!=null)
				retval.features[AtomicFeaturesZZ13.F_S0luwc]=codeBigram(S0luw,S0luc);
			if(S0lu!=null)
				retval.features[AtomicFeaturesZZ13.F_S0rlwc]=codeBigram(S0rlw,S0rlc);
			if(S0rr!=null)
				retval.features[AtomicFeaturesZZ13.F_S0rrwc]=codeBigram(S0rrw,S0rrc);
			if(S0ru!=null)
				retval.features[AtomicFeaturesZZ13.F_S0ruwc]=codeBigram(S0ruw,S0ruc);
			if(S0ul!=null)
				retval.features[AtomicFeaturesZZ13.F_S0ulwc]=codeBigram(S0ulw,S0ulc);
			if(S0ur!=null)
				retval.features[AtomicFeaturesZZ13.F_S0urwc]=codeBigram(S0urw,S0urc);
			if(S0uu!=null)
				retval.features[AtomicFeaturesZZ13.F_S0uuwc]=codeBigram(S0uuw,S0uuc);
			if(S1ll!=null)
				retval.features[AtomicFeaturesZZ13.F_S1llwc]=codeBigram(S1llw,S1llc);
			if(S1lr!=null)
				retval.features[AtomicFeaturesZZ13.F_S1lrwc]=codeBigram(S1lrw,S1lrc);
			if(S1lu!=null)
				retval.features[AtomicFeaturesZZ13.F_S1luwc]=codeBigram(S1luw,S1luc);
			if(S1rl!=null)
				retval.features[AtomicFeaturesZZ13.F_S1rlwc]=codeBigram(S1rlw,S1rlc);
			if(S1rr!=null)
				retval.features[AtomicFeaturesZZ13.F_S1rrwc]=codeBigram(S1rrw,S1rrc);
			if(S1ru!=null)
				retval.features[AtomicFeaturesZZ13.F_S1ruwc]=codeBigram(S1ruw,S1ruc);
			if(S1ul!=null)
				retval.features[AtomicFeaturesZZ13.F_S1ulwc]=codeBigram(S1ulw,S1ulc);
			if(S1ur!=null)
				retval.features[AtomicFeaturesZZ13.F_S1urwc]=codeBigram(S1urw,S1urc);
			if(S1uu!=null)
				retval.features[AtomicFeaturesZZ13.F_S1uuwc]=codeBigram(S1uuw,S1uuc);
			
			//S0 S1
			if(S1!=null){
				long S0cS1c=codeBigram10(S0c,S1c);
				long S0wS1w=codeBigram(S0w, S1w);
				retval.features[AtomicFeaturesZZ13.F_S0wS1wS0cS1c]=codeBigram(S0wS1w,S0cS1c);
				retval.features[AtomicFeaturesZZ13.F_S1wS0cS1c]=codeBigram(S1w,S0cS1c);
				retval.features[AtomicFeaturesZZ13.F_S0wS0cS1c]=codeBigram(S0w,S0cS1c);
				retval.features[AtomicFeaturesZZ13.F_S0cS1c]=S0cS1c;
			}

			//S0 N0
			if(N0!=-1){
				long S0wN0w=codeBigram(S0w, N0w);
				long S0cN0t=codeBigram10(S0c, N0t);
				retval.features[AtomicFeaturesZZ13.F_S0wN0wS0cN0t]=codeBigram(S0wN0w,S0cN0t);
				retval.features[AtomicFeaturesZZ13.F_N0wS0cN0t]=codeBigram(N0w,S0cN0t);
				retval.features[AtomicFeaturesZZ13.F_S0wS0cN0t]=codeBigram(S0w,S0cN0t);
				retval.features[AtomicFeaturesZZ13.F_S0cN0t]=S0cN0t;
			}
			
			//S1 N0
			if(S1!=null&&N0!=-1){
				long S1wN0w=codeBigram(S1w, N0w);
				long S1cN0t=codeBigram10(S1c, N0t);
				retval.features[AtomicFeaturesZZ13.F_S1wN0wS1cN0t]=codeBigram(S1wN0w,S1cN0t);
				retval.features[AtomicFeaturesZZ13.F_N0wS1cN0t]=codeBigram(N0w,S1cN0t);
				retval.features[AtomicFeaturesZZ13.F_S1wS1cN0t]=codeBigram(S1w,S1cN0t);
				retval.features[AtomicFeaturesZZ13.F_S1cN0t]=S1cN0t;
			}
			
			//N0 N1
			if(N1!=-1){
				long N0wN1w=codeBigram(N0w, N1w);
				long N0tN1t=codeBigram10(N0t, N1t);
				retval.features[AtomicFeaturesZZ13.F_N0wN1wN0tN1t]=codeBigram(N0wN1w,N0tN1t);
				retval.features[AtomicFeaturesZZ13.F_N1wN0tN1t]=codeBigram(N1w,N0tN1t);
				retval.features[AtomicFeaturesZZ13.F_N0wN0tN1t]=codeBigram(N0w,N0tN1t);
				retval.features[AtomicFeaturesZZ13.F_N0tN1t]=N0tN1t;
			}
			
			//S0 S1 N0
			long S0cS1cN0t=codeBigram10(codeBigram10(S0c, S1c),N0t);
			long S0tS1tN0t=codeBigram10(codeBigram10(S0t, S1t), N0t);
			retval.features[AtomicFeaturesZZ13.F_S0wS0cS1cN0t]=codeBigram30(S0w,S0cS1cN0t);
			if(S1!=null){
				retval.features[AtomicFeaturesZZ13.F_S1wS0cS1cN0t]=codeBigram30(S1w,S0cS1cN0t);
			}
			if(N0!=-1){
				retval.features[AtomicFeaturesZZ13.F_N0wS0cS1cN0t]=codeBigram30(N0w,S0cS1cN0t);
			}
			retval.features[AtomicFeaturesZZ13.F_S0cS1cN0t]=S0cS1cN0t;
			retval.features[AtomicFeaturesZZ13.F_S0tS1tN0t]=S0tS1tN0t;
			
			//S0 N0 N1
			if(N0!=-1){
				long S0cN0tN1t=codeBigram10(codeBigram10(S0c, N0t),N1t);
				long S0tN0tN1t=codeBigram10(codeBigram10(S0t, N0t),N1t);
				retval.features[AtomicFeaturesZZ13.F_S0wS0cN0tN1t]=codeBigram30(S0w,S0cN0tN1t);
				retval.features[AtomicFeaturesZZ13.F_N0wS0cN0tN1t]=codeBigram30(N0w,S0cN0tN1t);
				if(N1!=-1){
					retval.features[AtomicFeaturesZZ13.F_N1wS0cN0tN1t]=codeBigram30(N1w,S0cN0tN1t);
				}
				retval.features[AtomicFeaturesZZ13.F_S0cN0tN1t]=S0cN0tN1t;
				retval.features[AtomicFeaturesZZ13.F_S0tN0tN1t]=S0tN0tN1t;
			}
			
			if(S1!=null){
				long S0cS1cS2c=codeBigram10(codeBigram10(S0c, S1c),S2c);
				long S0tS1tS2t=codeBigram10(codeBigram10(S0t, S1t),S2t);
				retval.features[AtomicFeaturesZZ13.F_S0wS0cS1cS2c]=codeBigram30(S0w,S0cS1cS2c);
				retval.features[AtomicFeaturesZZ13.F_S1wS0cS1cS2c]=codeBigram30(S1w,S0cS1cS2c);
				if(S2!=null){
					retval.features[AtomicFeaturesZZ13.F_S2wS0cS1cS2c]=codeBigram30(S2w,S0cS1cS2c);
				}
				retval.features[AtomicFeaturesZZ13.F_S0cS1cS2c]=S0cS1cS2c;
				retval.features[AtomicFeaturesZZ13.F_S0tS1tS2t]=S0tS1tS2t; 
			}
			
			//N0 S0r
			if(N0!=-1&&S0r!=null){
				long S0cS0rc=codeBigram10(S0c, S0rc);
				long S0rj=S0r.beLeaf()? S0t:S0c;
				long S0cS0rcN0t=codeBigram10(S0cS0rc,N0t);
				long S0cS0rjN0t=codeBigram10(codeBigram10(S0c, S0rj),N0t);
				retval.features[AtomicFeaturesZZ13.F_S0cS0rcN0t]=S0cS0rcN0t;
				retval.features[AtomicFeaturesZZ13.F_S0cS0rjN0t]=S0cS0rjN0t;
				retval.features[AtomicFeaturesZZ13.F_N0wS0cS0rc]=codeBigram(N0w, S0cS0rc);
			}
			
			// S0 S0LRU S1
			
			if(S1!=null&&S0l!=null){
				long S0lj=S0l.beLeaf()?S0lt:S0lc;
				long S1j=S1.beLeaf()?S1t:S1c;
				long S0cS0lc=codeBigram10(S0c, S0lc);
				long S0cS0lcS1c=codeBigram10(S0cS0lc, S1c);
				long S0cS0ljS1j=codeBigram10(codeBigram10(S0c, S0lj), S1j);
				retval.features[AtomicFeaturesZZ13.F_S0cS0lcS1c]=S0cS0lcS1c;
				retval.features[AtomicFeaturesZZ13.F_S0cS0ljS1j]=S0cS0ljS1j;
				retval.features[AtomicFeaturesZZ13.F_S1wS0cS0lc]=codeBigram(S1w, S0cS0lc);
			}
			if(S1!=null&&S1r!=null){
				long S0j=S0.beLeaf()?S0t:S0c;
				long S1rj=S1r.beLeaf()?S1rt:S1rc;
				long S1cS1rc=codeBigram10(S1c, S1rc);
				long S0cS1cS1rc=codeBigram10(codeBigram10(S0c, S1c), S1rc);
				long S0jS1cS1rj=codeBigram10(codeBigram10(S0j, S1c), S1rj);
				retval.features[AtomicFeaturesZZ13.F_S0cS0lcS1c]=S0cS1cS1rc;
				retval.features[AtomicFeaturesZZ13.F_S0jS1cS1rj]=S0jS1cS1rj;
				retval.features[AtomicFeaturesZZ13.F_S0wS1cS1rc]=codeBigram(S0w, S1cS1rc);
			}
			
			retval.N0=N0;
			retval.N1=N1;
			retval.N2=N2;
			retval.N3=N3;
			
			retval.S0=S0;
			retval.S1=S1;
			retval.S2=S2;
			retval.S3=S3;
			retval.S0u=S0u;
			retval.S0r=S0r;
			retval.S0l=S0l;
			retval.S1u=S1u;
			retval.S1r=S1r;
			retval.S1l=S1l;
			retval.S0ll=S0ll;
			retval.S0lr=S0lr;
			retval.S0lu=S0lu;
			retval.S0rl=S0rl;
			retval.S0rr=S0rr;
			retval.S0ru=S0ru;
			retval.S0ul=S0ul;
			retval.S0ur=S0ur;
			retval.S0uu=S0uu;
			retval.S1ll=S1ll;
			retval.S1lr=S1lr;
			retval.S1lu=S1lu;
			retval.S1rl=S1rl;
			retval.S1rr=S1rr;
			retval.S1ru=S1ru;
			retval.S1ul=S1ul;
			retval.S1ur=S1ur;
			retval.S1uu=S1uu;
			
			/*
			 * return the atomic feature
			 */
			return retval;
		}
		
		private long codeBigram30(long b1, long b2) {
			return (b1<<30)|b2;
		}

		private long codeBigram10(long b1, long b2) {
			return (b1<<10)|b2;
		}

		private long codeBigram(long b1,long b2){
			return (b1<<20)|b2;
		}
		
//		private long codeTrigram(long t1,long t2,long t3){
//			return (t1<<40)|(t2<<20)|t3;
//		}
		
}
