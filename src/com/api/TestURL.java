package com.api;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.LongSummaryStatistics;

public class TestURL {
	private String id;
	private URL url;
	private List<Long> results = new LinkedList<Long>();

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
	
	public void addResult(long result) {
		results.add(result);
	}
	
	public long getResult(int index) {
		return results.get(index);
	}

	public List<Long> getResults() {
		return this.results;
	}
	
	public TestStatistics getTestStatistics() {
		TestStatistics stat = new TestStatistics();
		if (this.results == null || this.results.size() < 5) return stat;
		long[] longres = this.results.stream().mapToLong(l->l).toArray();
		LongSummaryStatistics s1 = Arrays.stream(longres).summaryStatistics();
		stat.count = s1.getCount();
		stat.sum = s1.getSum();
		stat.min = s1.getMin();
		stat.max = s1.getMax();
		stat.average = (long)s1.getAverage();
		stat.median = (long)median(longres);
		stat.stderr = (long)Math.sqrt(variance(longres, s1.getAverage()));
		return stat;
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
    	return id+"\n" + getTestStatistics().toString()+"\n";
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
    		String s = "";
    		s += "    CNT: "+count+"\n";
    		s += "    SUM: "+sum+"\n";
    		s += "    MIN: "+min+"\n";
    		s += "    MAX: "+max+"\n";
    		s += "    AVG: "+average+"\n";
    		s += "    MED: "+median+"\n";
    		s += "    STD: "+stderr;
    		return s;
    	}
    }
}
