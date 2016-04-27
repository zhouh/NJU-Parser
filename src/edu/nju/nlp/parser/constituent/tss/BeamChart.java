package edu.nju.nlp.parser.constituent.tss;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

import edu.nju.nlp.parser.constituent.evalb.ConParserEvalb;

/**
 * 
 * 
 * @author Hao Zhou
 *
 */
public class BeamChart extends PriorityQueue<ChartItem>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int beam;
	public boolean allEnd;
	public boolean gold;
	static int SentID=0;
	
	/**
	 * the constructor of the new priority queue
	 * 
	 */
	public BeamChart(int beam){
		
		//the new comparator function
		super(1,new Comparator<ChartItem>(){
			@Override
			public int compare(ChartItem o1, ChartItem o2) {
				// TODO Auto-generated method stub
				if( o1.getScore()< o2.getScore())
					return -1 ;
				else if(o1.getScore()==o2.getScore()){
					return 0;
				}else{
					return 1;
				}
			}   	 
	     });
		
		this.beam=beam;
		
		/*
		 * initially No one in the chart, so all the item in the chart 
		 * are of end state. No gold item in the chart.
		 */
		allEnd=true;	
		gold=false;
	}
	
	/**
	 * insert a new item into the chart
	 * the chart always keep the best beam item in the chart
	 * @param item
	 */
	public void insert(ChartItem item){
		if(this.size()<beam) offer(item);
		else if(item.getScore()<=peek().getScore()) return;
		else {
			poll();
			offer(item);
		}
	}
	
	/**
	 * offer a item into the chart
	 * if the item is gold, the chart will be a gold chart
	 * if the item is not a end state, the chart will not be a end chart.
	 *   
	 * @param item under pushed item
	 */
	public void push(ChartItem item){
//		if(item.getGold()) gold=true;
//		if(!item.bEnd()) allEnd=false;
		offer(item);
	}

	public void reset() {
		this.clear();
		this.allEnd=true;
		this.gold=false;
		
	}

	public boolean isAllEnd() {
		return allEnd;
	}

	public void setAllEnd(boolean allEnd) {
		this.allEnd = allEnd;
	}

	public boolean isGold() {
		return gold;
	}

	public void setGold(boolean gold) {
		this.gold = gold;
	}
	
	public void setPara(){
		for(ChartItem item:this){
			if(item.getGold()) this.gold=true;
			if(!item.bEnd()) this.allEnd=false;
		}
	}
	
	/**
	 * get the K largest item from the beam size min-heap
	 * 
	 */
//	public ChartItem[] getKBest(int k){
//		
//		//if the k is larger than beam
//		if(k>beam) throw new RuntimeException("The beam don't have enough K best item!");
//		
//		ChartItem[] items=new ChartItem[k];
//		
//		//insert the heap head items in the min-heap into a link list 
//		LinkedList<ChartItem> linkItems=new LinkedList<ChartItem>();
//		while(!this.isEmpty())
//			linkItems.push(this.poll());
//		
//		//get the last K insert item, the largest K items
//		for(int i=0;i<k;i++)
//			items[i]=linkItems.pop();
//		
//		return items;
//		
//	}
	
	public ChartItem[] getKBest(int k) {

		//if the k is larger than beam
		if(k>beam) throw new RuntimeException("The beam don't have enough K best item!");
		
		ChartItem[] items=new ChartItem[k];
		
		//insert the heap head items in the min-heap into a link list 
		LinkedList<ChartItem> linkItems=new LinkedList<ChartItem>();
		while(!this.isEmpty())
			linkItems.push(this.poll());
		
		if(ConParserEvalb.outputKBestALMDetail){
			try {
				ConParserEvalb evalb = ConParserEvalb.getInstance(false);
				evalb.outputDetailsOfBeam(linkItems,SentID++);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//get the last K insert item, the largest K items
		for(int i=0;i<k;i++)
			items[i]=linkItems.pop();
		
		return items;
	}
	
	

////    JUST FOR TEST
//	public static void main(String[] args){
//		ChartItem i0=new ChartItem(null, null,null, null, 1.0);
//		ChartItem i1=new ChartItem(null, null,null, null, 1.1);
//		ChartItem i2=new ChartItem(null, null, null, null,1.2);
//		
//		BeamChart chart=new BeamChart(3);
//		chart.insert(i1);
//		chart.insert(i0);
//		chart.insert(i2);
//		
//		System.out.println(chart.peebeam().getScore());
//		
//		for(ChartItem item:chart){
//			System.out.println(item.getScore());
//		}
//		
//		System.out.println("insert a new item");
//		chart.insert(new ChartItem(null, null, 1.6));
//		chart.insert(new ChartItem(null, null, 0.9));
//		chart.insert(new ChartItem(null, null, 1.8));
//		for(ChartItem item:chart){
//			System.out.println(item.getScore());
//		}
//	}
 
}  

