package bitmapbenchmarks.synth;


import it.uniroma3.mat.extendedset.intset.ConciseSet;
import java.text.DecimalFormat;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import org.devbrat.util.WAHBitSet;
import javaewah.EWAHCompressedBitmap;
import javaewah32.EWAHCompressedBitmap32;

public class benchmark {

  public static void main(String args[]) {
    test(10, 18, 10);
  }
  
    public static long testWAH32(int[][] data, int repeat,DecimalFormat df ) {
    System.out.println("# WAH 32 bit using the compressedbitset library");
    System.out.println("# size, construction time, time to recover set bits, time to compute unions");
    long bef,aft;
    String line = "";
    long bogus = 0;
    int N = data.length;
    bef = System.currentTimeMillis();
    WAHBitSet[] bitmap = new WAHBitSet[N];
    int size = 0;
    for (int r = 0; r < repeat; ++r) {
      size = 0; 
      for (int k = 0; k < N; ++k) {
        bitmap[k] = new WAHBitSet();
        for (int x = 0; x < data[k].length; ++x) {
          bitmap[k].set(data[k][x]);
        }
        size += bitmap[k].memSize()*4;
      }
    }
    aft = System.currentTimeMillis();
    line += "\t" + size/1024;
    line += "\t" + df.format((aft - bef) / 1000.0);
    // uncompressing
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        int[] array = new int[bitmap[k].cardinality()];
        int c = 0;
        for(@SuppressWarnings("unchecked")
        Iterator<Integer> i = bitmap[k].iterator(); i.hasNext();array[c++] =i.next().intValue()){}
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    // logical or
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        WAHBitSet bitmapor = bitmap[0];
        for (int j = 1; j < k; ++j) {
          bitmapor = bitmapor.or(bitmap[j]);
        }
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    System.out.println(line);
    return bogus;
  }  


  public static long testConciseSet(int[][] data, int repeat,DecimalFormat df ) {
    System.out.println("# ConciseSet 32 bit using the extendedset_2.2 library");
    System.out.println("# size, construction time, time to recover set bits, time to compute unions");
    long bef,aft;
    String line = "";
    long bogus = 0;
    int N = data.length;
    bef = System.currentTimeMillis();
    ConciseSet[] bitmap = new ConciseSet[N];
    int size = 0;
    for (int r = 0; r < repeat; ++r) {
      size = 0; 
      for (int k = 0; k < N; ++k) {
        bitmap[k] = new ConciseSet();
        for (int x = 0; x < data[k].length; ++x) {
          bitmap[k].add(data[k][x]);
        }
        size += (int)(bitmap[k].size() *bitmap[k].collectionCompressionRatio())*4;
      }
    }
    aft = System.currentTimeMillis();
    line += "\t" + size/1024;
    line += "\t" + df.format((aft-bef) / 1000.0);
    // uncompressing
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        int[] array = bitmap[k].toArray();
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    // logical or
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        ConciseSet bitmapor = bitmap[0];
        for (int j = 1; j < k; ++j) {
          bitmapor = bitmapor.union(bitmap[j]);
        }
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    System.out.println(line);
    return bogus;
  }  
  
  
  
  
  public static long testEWAH64(int[][] data, int repeat,DecimalFormat df ) {
    System.out.println("# EWAH using the javaewah library");
    System.out.println("# size, construction time, time to recover set bits, time to compute unions");
    long bef,aft;
    String line = "";
    long bogus = 0;
    int N = data.length;
    bef = System.currentTimeMillis();
    EWAHCompressedBitmap[] ewah = new EWAHCompressedBitmap[N];
    int size = 0;
    for (int r = 0; r < repeat; ++r) {
      size = 0; 
      for (int k = 0; k < N; ++k) {
        ewah[k] = new EWAHCompressedBitmap();
        for (int x = 0; x < data[k].length; ++x) {
          ewah[k].set(data[k][x]);
        }
        size += ewah[k].sizeInBytes();
      }
    }
    aft = System.currentTimeMillis();
    line += "\t" + size/1024;
    line += "\t" + df.format((aft - bef) / 1000.0);
    // uncompressing
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        int[] array = ewah[k].toArray();
        bogus += array.length;
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    // fast logical or
    bef = System.currentTimeMillis();
    for (int r = 0; r < repeat; ++r)
      for (int k = 0; k < N; ++k) {
        EWAHCompressedBitmap[] ewahcp = new EWAHCompressedBitmap[k + 1];
        for (int j = 0; j < k + 1; ++j) {
          ewahcp[j] = ewah[k];
        }
        EWAHCompressedBitmap bitmapor = EWAHCompressedBitmap.or(ewahcp);
        bogus += bitmapor.sizeInBits();
      }
    aft = System.currentTimeMillis();
    line += "\t" + df.format((aft - bef) / 1000.0);
    System.out.println(line);
    return bogus;
  }

  public static void test(int N, int nbr, int repeat) {
    DecimalFormat df = new DecimalFormat("0.###");
    ClusteredDataGenerator cdg = new ClusteredDataGenerator();
    System.out.println("# For each instance, we report the size, the construction time, ");
    System.out.println("# the time required to recover the set bits,");
    System.out.println("# and the time required to compute logical ors (unions) between lots of bitmaps.");
    for (int sparsity = 1; sparsity < 31 - nbr; sparsity += 4) {
      System.out.println("# sparsity "+sparsity+" average set bit per 32-bit word = "+(1<<nbr)*32.0/(1 << (nbr + sparsity)));
      int[][] data = new int[N][];
      int Max = (1 << (nbr + sparsity));
      System.out.println("# generating random data...");
      for (int k = 0; k < N; ++k)
        data[k] = cdg.generateClustered(1 << nbr, Max);
      System.out.println("# generating random data... ok.");
      // building
      testConciseSet(data,repeat,df);
      testWAH32(data,repeat,df);
      testEWAH64(data,repeat,df);
      System.out.println();

    }
  }
}
