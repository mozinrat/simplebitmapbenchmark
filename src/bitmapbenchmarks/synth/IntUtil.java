package bitmapbenchmarks.synth;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;


public final class IntUtil {
  
  public static int[] unite(int[]... set) {
    if(set.length == 0) throw new RuntimeException("nothing");
    PriorityQueue<int[]> pq = new PriorityQueue<int[]>(set.length,
      new Comparator<int[]>(){
        public int compare(int[] a, int[] b) {
          return a.length - b.length;
        }}
     );
    int[] buffer = new int[32];
    for(int[] x : set) 
      pq.add(x);
    while(pq.size()>1) {
    int[] x1 = pq.poll();
    int[] x2 = pq.poll();
    if(buffer.length<x1.length+x2.length)
      buffer =  new int[x1.length+x2.length];
    int [] a = unite2by2(x1,x2,buffer);
    pq.add(a);
    } 
    return pq.poll();
  }
  
  static public int[] unite2by2(final int[] set1, final int[] set2, final int[] buffer) {
    int pos = 0;
    int k1 = 0, k2 = 0;
    if(0==set1.length)
      return Arrays.copyOf(set2, set2.length);
    if(0==set2.length)
      return Arrays.copyOf(set1, set1.length);
    while(true) {
      if(set1[k1]<set2[k2]) {
        buffer[pos++] = set1[k1];
        ++k1;
        if(k1>=set1.length) {
          for(; k2<set2.length;++k2)
            buffer[pos++] = set2[k2];
          break;
        }
      } else if (set1[k1]==set2[k2]) {
        buffer[pos++] = set1[k1];
        ++k1;
        ++k2;
        if(k1>=set1.length) {
          for(; k2<set2.length;++k2)
            buffer[pos++] = set2[k2];
          break;
        }
        if(k2>=set2.length) {
          for(; k1<set1.length;++k1)
            buffer[pos++] = set1[k1];
          break;
        }
      } else {//if (set1[k1]>set2[k2]) {
        buffer[pos++] = set2[k2];
        ++k2;
        if(k2>=set2.length) {
          for(; k1<set1.length;++k1)
            buffer[pos++] = set1[k1];
          break;
        }
      }
    }
    return Arrays.copyOf(buffer, pos);
  }


  public static int[] intersect(int[]... set) {
    if(set.length == 0) throw new RuntimeException("nothing");
    int[] answer = set[0];
    int[] buffer = new int[32];
    for(int k = 1; k<set.length;++k) {
      if(buffer.length<answer.length+set[k].length)
        buffer =  new int[answer.length+set[k].length];
      answer = intersect2by2(answer, set[k], buffer);
    }
    return answer;
  }

  public static int[] intersect2by2(final int[] set1, final int[] set2, final int[] buffer) {
    int pos = 0;
    for(int k1 = 0, k2 = 0; k1 <set1.length; ++k1) {
      while(set2[k2]<set1[k1] && (k2+1 < set2.length)) {
        ++k2;          
      }
      if(k2 < set2.length) {
        if(set2[k2]==set1[k1]) {
          buffer[pos++] = set1[k1];
        }
      } else break;
    }
    return Arrays.copyOf(buffer, pos);
  }

	public static int maxlength(int[]... set) {
		int m = 0;
		for (int k = 0; k < set.length; ++k)
			if (m < set[k].length)
				m = set[k].length;
		return m;
	}


	public static int[] frogintersect(int[]... set) {
		if (set.length == 0)
			throw new RuntimeException("nothing");
		if (set.length == 1)
			return set[0];
		int[] answer = set[0];
		int[] buffer = new int[maxlength(set)];
		for (int k = 1; k < set.length; ++k) {
			answer = frogintersect2by2(answer, set[k], buffer);
		}
		return answer;
	}
	
	
	public static int[] frogintersect2by2(final int[] set1, final int[] set2,
			final int[] buffer) {
		if ((0 == set1.length) || (0 == set2.length))
			return new int[0];
		int k1 = 0;
		int k2 = 0;
		int pos = 0;
		mainwhile: while (true) {
			if (set1[k1] < set2[k2]) {
				k1 = advanceUntil(set1,k1,set2[k2]);
				if (k1 == set1.length)
					break mainwhile;
			}
			if (set2[k2] < set1[k1]) {
				k2 = advanceUntil(set2,k2,set1[k1]);
				if (k2 == set2.length)
						break mainwhile;
			} else {
				// (set2[k2] == set1[k1])
				buffer[pos++] = set1[k1];
				++k1;
				if (k1 == set1.length)
					break;
				++k2;
				if (k2 == set2.length)
					break;

			}

		}
		return Arrays.copyOf(buffer, pos);
		
	}

	/**
	 * Find the smallest integer larger than pos such 
	 * that array[pos]>= min.
	 * If none can be found, return array.length.
	 * Based on code by O. Kaser.
	 * 
	 * @param array
	 * @param pos
	 * @param min
	 * @return
	 */
	public static int advanceUntil(int[] array, int pos, int min) {
		int lower = pos+1;
		
		// special handling for a possibly common sequential case
		if (lower >= array.length || array[lower] >= min) {
		    return lower;
		}

		int spansize=1;  // could set larger
		// bootstrap an upper limit
	       
		while (lower+spansize < array.length && array[lower+spansize] < min) 
		    spansize *= 2;  // hoping for compiler will reduce to shift
		int upper = (lower+spansize < array.length) ? lower+spansize : array.length-1;
		
		// maybe we are lucky (could be common case when the seek ahead expected to be small and sequential will otherwise make us look bad)
		if (array[upper] == min) {
		    return upper;
		}
		
		if (array[upper] < min) {// means array has no item >= min
		    //pos = array.length;
		    return array.length;
		}

		// we know that the next-smallest span was too small
		lower += (spansize/2);

		// else begin binary search
		// invariant: array[lower]<min && array[upper]>min
		int mid=0;
		while (lower+1 != upper) {
		    mid = (lower+upper)/2;
		    if (array[mid] == min) {
			return mid;
		    } else
			if (array[mid] < min) 
			    lower = mid;
			else
			    upper = mid;
		}
		return upper;

	}
 
}
