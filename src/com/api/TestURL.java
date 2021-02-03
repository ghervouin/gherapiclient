package com.api;

import java.net.URL;
import java.util.Arrays;
import java.util.LongSummaryStatistics;

public class TestURL {
	String id;
	URL url;
	long[] results;

	TestURL(String id, URL url) {
		this.id = id;
		this.url = url;
	}

	String getPrintableResult() {
		String s = id+"\n";
		LongSummaryStatistics s1 = Arrays.stream(results).summaryStatistics();
		s += "  CNT: "+s1.getCount()+"\n";
		s += "  SUM: "+s1.getSum()+"\n";
		s += "  MIN: "+s1.getMin()+"\n";
		s += "  MAX: "+s1.getMax()+"\n";
		s += "  AVG: "+(long)s1.getAverage()+"\n";
		s += "  STD: "+(long)Math.sqrt(variance(results, s1.getAverage()));
		return s;
	}

	private static double variance(long a[], double average) { 
    	int n = a.length;
        double sqDiff = 0; 
        for (int i = 0; i < n; i++)  
            sqDiff += (a[i] - average) *  
                      (a[i] - average); 
          
        return (double)sqDiff / n; 
    }
    
    public String toString() {
    	return id+":"+Arrays.toString(results);
    }
}
