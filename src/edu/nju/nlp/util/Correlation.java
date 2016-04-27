package edu.nju.nlp.util;

public class Correlation {

	public static double pearson(double[] f1, double[] f2)
	{
		double total = 0;
		double average1 = average(f1);
		double average2 = average(f2);
		for(int i=0;i<f1.length;i++)
		{
			total+=(f1[i]-average1)*(f2[i]-average2);
		}
		return total/std(f2)/std(f1);
	}
	
	public static double average(double[] f)
	{
		double total = 0;
		for(int i = 0;i<f.length;i++)
			total+=f[i];
		return total/f.length;
	}
	
	public static double std(double[] f)
	{
		double average = average(f);
		double total = 0;
		for(int i = 0;i<f.length;i++)
		{
			total += ((f[i]-average)*(f[i]-average));
		}
		return Math.sqrt(total);
	}

	public static void main(String[] args)
	{
		//test pearson
		double[] f1 = new double[]{0.087546667,
			0.102668433,
			0.145025566,
			0.07246067,
			0.04951687,
			0.083218395,
			0.09554143,
			0.066277365,
			0.057900244,
			0.09214894,
			0.111231558,
			0.077448643					
		};
		
		double[] f2 = new double[] {
				0.1653,
				0.1394,
				0.151,
				0.1763,
				0.1866,
				0.1671,
				0.174,
				0.1892,
				0.2016,
				0.1608,
				0.1764,
				0.1935
		}; 
		
		System.out.println(pearson(f1, f2));
	}
}
