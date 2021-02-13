package com.api;

import java.net.URL;
import java.util.Arrays;
import java.util.LongSummaryStatistics;

public class TestURL {
	private String id;
	private URL url;
	private long[] results;

	TestURL(String id, URL url) {
		this.id = id;
		this.url = url;
	}

	public String getId() {
		return this.id;
	}
	
	public URL getUrl() {
		return this.url;
	}
	
	public void setResults(long[] results) {
		this.results = results;
	}
	
	public long[] getResults() {
		return this.results;
	}
	
	public TestStatistics getTestStatistics() {
		TestStatistics stat = new TestStatistics();
		
		LongSummaryStatistics s1 = Arrays.stream(results).summaryStatistics();
		stat.count = s1.getCount();
		stat.sum = s1.getSum();
		stat.min = s1.getMin();
		stat.max = s1.getMax();
		stat.average = (long)s1.getAverage();
		stat.median = (long)median(results);
		stat.stderr = (long)Math.sqrt(variance(results, s1.getAverage()));
		return stat;
	}

	String getPrintableResult() {		
		return id+"\n" + getTestStatistics().toString();
	}

	private static double variance(long values[], double average) { 
    	int n = values.length;
        double sqDiff = 0; 
        for (int i = 0; i < n; i++)  
            sqDiff += (values[i] - average) *  
                      (values[i] - average); 
        return (double)sqDiff / n; 
    }
    
	private static long median(long[] values) {
		Arrays.sort(values);
	    long median;
	    int totalElements = values.length;
	    if (totalElements % 2 == 0) {
	        long sumOfMiddleElements = values[totalElements / 2] +
	                                  values[totalElements / 2 - 1];
	        median = ((long) sumOfMiddleElements) / 2;
	    } else {
	        median = (long) values[values.length / 2];
		}
	    return median;
	}

    public String toString() {
    	return id+":"+Arrays.toString(results);
    }
    
    public class TestStatistics {
    	private long count;
    	private long sum;
    	private long min;
    	private long max;
    	private long average;
    	private long median;
    	private long stderr;

    	public long getCount() {
			return count;
		}

		public long getSum() {
			return sum;
		}

		public long getMin() {
			return min;
		}

		public long getMax() {
			return max;
		}

		public long getAverage() {
			return average;
		}

		public long getMedian() {
			return median;
		}

		public long getStderr() {
			return stderr;
		}

    	public String toString() {
    		String s = id+"\n";
    		s += "  CNT: "+count+"\n";
    		s += "  SUM: "+sum+"\n";
    		s += "  MIN: "+min+"\n";
    		s += "  MAX: "+max+"\n";
    		s += "  AVG: "+average+"\n";
    		s += "  MED: "+median+"\n";
    		s += "  STD: "+stderr;
    		return s;
    	}
    }
}
