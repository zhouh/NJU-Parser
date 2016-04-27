package edu.nju.nlp.parser.transition;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Constituent Action class
 * used in shift-reduce constituent parser
 * shift
 * unary-reduce
 * binary-reduce 
 * terminate
 * and some parameter like temporary, leftHead
 * */
public class CountConAction {

	/* The action type of each action */
	public enum actionType{IDLE,SHIFT,END,BINARY_REDUCE,UNARY_REDUCE,OTHERS};
	
	/* action that only */
	public static final CountConAction NOT_AVAILABLE = new CountConAction("NA","NA",actionType.OTHERS);
	public static final CountConAction END_STATE = new CountConAction("E","E",actionType.END);
	public static final CountConAction PENDING = new CountConAction("P","P",actionType.OTHERS);
	public static final CountConAction SHIFT = new CountConAction("S","S",actionType.SHIFT);
	public static final CountConAction TERMINATE = new CountConAction("T", "T",actionType.OTHERS);
	public static final CountConAction IDLE =new CountConAction("I","I",actionType.IDLE);
	
	public static int size = 0;
	
	/*action list map, from String to constituent action*/
	private static final Map<String,CountConAction> m_shiftTagActions;	// shift with a pos-tag action
																		// used in the parse and pos joint model
	private static final Map<String,CountConAction> m_labeledUnaryReduceActions;
	/*binary reduce action*/
	private static final Map<String,CountConAction> m_labeledBinaryReduceLActions;
	private static final Map<String,CountConAction> m_labeledBinaryReduceRActions;
	private static final Map<String,CountConAction> m_labeledBinaryReduceLTempActions;
	private static final Map<String,CountConAction> m_labeledBinaryReduceRTempActions;
	
	/*
	 * String to label action, used in constructor and toString() 
	 * and Judge which kind of action this action is.
	 * */
	private static final String m_actShiftTag = "ST";
	private static final String m_actUnaryReduce ="UR";
	private static final String m_actBinaryReduceL="BRL";
	private static final String m_actBinaryReduceR="BRR";
	private static final String m_actBinaryReduceLTemp="BRL*";
	private static final String m_actBinaryReduceRTemp="BRR*";
	
	/*
	 * Initial the action list 
	 * */
	static
	{
		m_shiftTagActions = new ConcurrentHashMap<String,CountConAction>();
		m_labeledUnaryReduceActions = new ConcurrentHashMap<String,CountConAction>();
		m_labeledBinaryReduceLActions =new ConcurrentHashMap<String,CountConAction>();
		m_labeledBinaryReduceRActions =new ConcurrentHashMap<String,CountConAction>();		
		m_labeledBinaryReduceLTempActions =new ConcurrentHashMap<String,CountConAction>();
		m_labeledBinaryReduceRTempActions =new ConcurrentHashMap<String,CountConAction>();
	}
	
	/* para member */
	private final String m_action;
	private final String m_tag;
	private final int code;
	private final boolean isTemp;
	private final actionType type;

	/**
	 * Constructor
	 * */
	private CountConAction(String sAct, String sTag, actionType type)
	{
		this(sAct, sTag,false,type);
	}

	public CountConAction(String sAct, String sTag,boolean isTemp, actionType type)
	{
		m_action = sAct;
		m_tag = sTag;
		code = size++;	//the code of a tag is the create order of the object, unique in the action space
		this.isTemp=isTemp;
		this.type=type;
	}
	

	/**
	 * return labeled reduce constituent action
	 * if do not exits, insert into the hashmap
	 * else directly return
	 * 
	 * @param bBinary binary reduce or unary reduce
	 * @param bLeft after binary reduce, the head ConstituentLabel is in left or right child
	 * @param Temp after action, the node is temporary or not
	 * @param label the label of the newly generated node
	 * @return the constituent action 
	 * */
	public static CountConAction getLabeledReduceAction(boolean bBinary, boolean bLeft,boolean Temp,String label)
	{	
		if(Temp){	//if the node is temporary
			if (bBinary)	//if the action is Binary
			{
				if(bLeft){	//head ConstituentLabel is in the left child
					if (!m_labeledBinaryReduceLTempActions.containsKey(label))
						m_labeledBinaryReduceLTempActions.put(label, new CountConAction(m_actBinaryReduceLTemp, label,true,actionType.BINARY_REDUCE));
					return m_labeledBinaryReduceLTempActions.get(label);
				}
				else{
					if (!m_labeledBinaryReduceRTempActions.containsKey(label))
						m_labeledBinaryReduceRTempActions.put(label, new CountConAction(m_actBinaryReduceRTemp, label,true,actionType.BINARY_REDUCE));
					return m_labeledBinaryReduceRTempActions.get(label);
				}
			}
			else{
				throw new RuntimeException("Temporary unary node do not exit!");
			}
		}
		else{
			if (bBinary)
			{
				if(bLeft){
					if (!m_labeledBinaryReduceLActions.containsKey(label))
						m_labeledBinaryReduceLActions.put(label, new CountConAction(m_actBinaryReduceL, label,false,actionType.BINARY_REDUCE));
					return m_labeledBinaryReduceLActions.get(label);
				}
				else{
					if (!m_labeledBinaryReduceRActions.containsKey(label))
						m_labeledBinaryReduceRActions.put(label, new CountConAction(m_actBinaryReduceR, label,false,actionType.BINARY_REDUCE));
					return m_labeledBinaryReduceRActions.get(label);
				}
			}
			else	//unary reduce , unary reduce mustn't be temporary
			{
				if (!m_labeledUnaryReduceActions.containsKey(label))
					m_labeledUnaryReduceActions.put(label, new CountConAction(m_actUnaryReduce, label,false,actionType.UNARY_REDUCE));
				return m_labeledUnaryReduceActions.get(label);
			}
		}
	}
	
	public static CountConAction getShiftTagAction(String sPos)
	{
		if (!m_shiftTagActions.containsKey(sPos))
			m_shiftTagActions.put(sPos, new CountConAction(m_actShiftTag, sPos,actionType.SHIFT));
		return m_shiftTagActions.get(sPos);
	}
	
	public boolean isLeftReduce(){
		
		return (type==actionType.BINARY_REDUCE&&m_action.startsWith("BL"));
	}
	
	public boolean isRightReduce(){
		
		return (type==actionType.BINARY_REDUCE&&m_action.startsWith("BR"));
	}
	
	public boolean isBinaryReduce(){
		return type==actionType.BINARY_REDUCE;
	}

	public boolean isLabeledUnaryReduce()
	{
		return type==actionType.UNARY_REDUCE;
	}
	
	public boolean isReduce(){
		return type==actionType.BINARY_REDUCE||
			   type==actionType.UNARY_REDUCE;
	}
	
	public boolean isShiftTagAction()
	{
		return m_action.equals(m_actShiftTag);
	}
	
	public boolean isShiftAction(){
		return type==actionType.SHIFT;
	}
	
	public boolean isEndAction(){
		return type==actionType.END;
	}
	
	public boolean isIDLEAction(){
		return type==actionType.IDLE;
	}
	
	/**
	 * returns a part of speech for tagging actions, and an arc label for reduce
	 * actions
	 * 
	 * @return
	 */
	public String getTag()
	{
		return m_tag;
	}

	public boolean isTemp(){
		
		return this.isTemp;
	
	}
	
	@Override
	public String toString()
	{
		return m_action + "-" + m_tag;
	}
	
	/**
	 * @return the unique code of the action
	 */
	public int code(){
		return code;
	}
	
	public boolean shallowEquals(CountConAction a)
	{
		return this==a;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof CountConAction))
			return false;
		CountConAction a = (CountConAction)o;
		return m_action.equals(a.m_action);
	}

	@Override
	public int hashCode()
	{
		return m_action.hashCode() * 17 + 1;
	}
}
