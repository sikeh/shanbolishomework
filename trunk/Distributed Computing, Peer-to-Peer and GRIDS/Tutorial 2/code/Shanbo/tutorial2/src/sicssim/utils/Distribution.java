package sicssim.utils;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;


public class Distribution {
	public static int poisson(double lambda) {
		double L = 1 / Math.exp(lambda);
		int k = 0;
		double p = 1;
		double u;
		
	    do {
	         k = k + 1;
	         u = (new Random()).nextDouble();
	         p = p * u;
	    } while (p >= L);

	    return k - 1;
	}
	
//----------------------------------------------------------------------------------
	public static int normal(double mean, double var) {
		double a = (new Random()).nextGaussian();
		int result = (int)(Math.round((var * a) + mean));
		
		if (result < 0)
			result = 0;
		
		return result;
		
	}

//----------------------------------------------------------------------------------
	public static int normal(double mean, double var, int seed) {
		double a = (new Random(seed)).nextGaussian();
		int result = (int)(Math.round((var * a) + mean));
		
		if (result < 0)
			result = 0;
		
		return result;
		
	}

//----------------------------------------------------------------------------------
	public static int uniform(int value) {
		return (new Random()).nextInt(value);
	}

//----------------------------------------------------------------------------------
	public static int uniform(int value, int seed) {
		return (new Random(seed)).nextInt(value);
	}
	
//----------------------------------------------------------------------------------
	public static int exp(int mean) {
		return (int)(((double) - mean) * Math.log(1.0 - (new Random()).nextDouble()));
	}

//----------------------------------------------------------------------------------
	public static int exp(int mean, int seed) {
		return (int)(((double) - mean) * Math.log(1.0 - (new Random(seed)).nextDouble()));
	}

//----------------------------------------------------------------------------------
    public static int Sripanidkulachi() {
        double randomNumber = new Random().nextDouble() * 100;
        int result = -1;
        int bitRate = 250;
        
        while (result < 0) {
            if (randomNumber <= 49.3)
                result = 0 + new Random().nextInt(bitRate);
            else if (randomNumber <= 68)
                result = 1 * bitRate + new Random().nextInt(bitRate);
            else if (randomNumber <= 76.4)
                result = 2 * bitRate + new Random().nextInt(bitRate);
            else if (randomNumber <= 81.6)
                result = (3 + (new Random().nextInt(16))) * bitRate + new Random().nextInt(bitRate);
            else if (randomNumber <= 88.4)
                result = 20 * bitRate;
            else
                randomNumber = ((100 - randomNumber) * 100) / 11.6; //using the same distribution again
        }
        
        return result;
    }
	
//----------------------------------------------------------------------------------
	public static void main(String args[]) {
		int a;
		int count = 0;
		Hashtable<Integer, Integer> h = new Hashtable<Integer, Integer>();
		
		for (int i = 0; i < 100000; i++) {
			a = Distribution.normal(4, 0.5);
			if (h.containsKey(Integer.valueOf(a)))
				count = h.get(Integer.valueOf(a));
			else
				count = 0;
			
			h.put(Integer.valueOf(a), count + 1);
		}
		
		Enumeration<Integer> en = h.keys();
		while(en.hasMoreElements()) {
			a = en.nextElement();
			System.out.println(a + "         " + h.get(Integer.valueOf(a)));
		}
		
	}
	
//----------------------------------------------------------------------------------
    public static int pareto(long xm, double k) {
        double uniformRandomNumber = new Random().nextDouble();
        double paretoRandomNumber =  xm / (Math.pow(uniformRandomNumber, (1 / k)));
        return (int)paretoRandomNumber;
    }  

}
