package com.zhangblue.demo1;

/**
 * @author zhangd
 * <p>
 * 64位系统，Java数组对象头固定占16字节，而long类型占8个字节。所以16+8*6=64字节，刚好等于一条缓存行的长度（一个缓存行可以装填6个long类型的数据）
 * @see {@code https://www.cnblogs.com/niutao/p/10567822.html}
 */
public class L1CacheMiss {

  private static final int RUNS = 10;
  private static final int DIMENSION_1 = 1024 * 1024;
  private static final int DIMENSION_2 = 62;

  private static long[][] longs;

  public static void main(String[] args) {
    new L1CacheMiss().slowDemo();
  }


  /**
   * 每次从内存抓取的都是同行不同列的数据块（如longs[i][0]到longs[i][5]的全部数据），但循环下一个的目标，却是同列不同行（如longs[0][0]下一个是longs[1][0]，造成了longs[0][1]-longs[0][5]无法重复利用
   */
  public void slowDemo() {
    longs = new long[DIMENSION_1][];
    for (int i = 0; i < DIMENSION_1; i++) {
      longs[i] = new long[DIMENSION_2];
      for (int j = 0; j < DIMENSION_2; j++) {
        longs[i][j] = 0L;
      }
    }
    System.out.println("starting....");

    final long start = System.currentTimeMillis();
    long sum = 0L;
    for (int r = 0; r < RUNS; r++) {
      for (int j = 0; j < DIMENSION_2; j++) {
        for (int i = 0; i < DIMENSION_1; i++) {
          sum += longs[i][j];
        }
      }
    }
    System.out.println("duration = " + (System.currentTimeMillis() - start));
  }

  /**
   * 每次开始内循环时，从内存抓取的数据块实际上覆盖了longs[i][0]到longs[i][5]的全部数据（刚好64字节）。因此，内循环时所有的数据都在L1缓存可以命中，遍历将非常快。
   */
  public void fastDemo() {
    longs = new long[DIMENSION_1][];
    for (int i = 0; i < DIMENSION_1; i++) {
      longs[i] = new long[DIMENSION_2];
      for (int j = 0; j < DIMENSION_2; j++) {
        longs[i][j] = 0L;
      }
    }
    System.out.println("starting....");

    final long start = System.currentTimeMillis();
    long sum = 0L;
    for (int r = 0; r < RUNS; r++) {
      for (int i = 0; i < DIMENSION_1; i++) {
        for (int j = 0; j < DIMENSION_2; j++) {
          sum += longs[i][j];
        }
      }
    }
    System.out.println("duration = " + (System.currentTimeMillis() - start));
  }
}