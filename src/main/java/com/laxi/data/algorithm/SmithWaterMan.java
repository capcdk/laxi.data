package com.laxi.data.algorithm;

import java.util.ArrayList;
import java.util.Stack;

public class SmithWaterMan implements SimilarAlgorithm {
    private int[][] H;
    private int[][] isEmpty;
    //空格匹配的得分
    private final static int SPACE = -4;
    //两个字母相同的得分
    private final static int MATCH = 3;
    //两个字母不同的得分
    private final static int DISMACH = -1;
    private int maxIndexM, maxIndexN;

    private Stack<Character> stk1 = new Stack<>(), stk2 = new Stack<>();

    //相似度最高的两个子串
    private String subSq1, subSq2;

    private int max(int a, int b, int c) {
        int maxN;
        if (a >= b) {
            maxN = a;
        } else {
            maxN = b;
        }
        if (maxN < c) {
            maxN = c;
        }
        if (maxN < 0) {
            maxN = 0;
        }
        return maxN;
    }

    private void calculateMatrix(String s1, String s2, int m, int n) {//计算得分矩阵

        if (m == 0) {
            this.H[m][n] = 0;
        } else if (n == 0) {
            this.H[m][n] = 0;
        } else {
            if (this.isEmpty[m - 1][n - 1] == 1) {
                this.calculateMatrix(s1, s2, m - 1, n - 1);
            }
            if (this.isEmpty[m][n - 1] == 1) {
                this.calculateMatrix(s1, s2, m, n - 1);
            }
            if (this.isEmpty[m - 1][n] == 1) {
                this.calculateMatrix(s1, s2, m - 1, n);
            }
            if (s1.charAt(m - 1) == s2.charAt(n - 1)) {
                this.H[m][n] = this.max(this.H[m - 1][n - 1] + MATCH, this.H[m][n - 1] + SPACE, this.H[m - 1][n] + SPACE);
            } else {
                this.H[m][n] = this.max(this.H[m - 1][n - 1] + DISMACH, this.H[m][n - 1] + SPACE, this.H[m - 1][n] + SPACE);
            }
        }
        this.isEmpty[m][n] = 0;
    }

    private void findMaxIndex(int[][] H, int m, int n) {//找到得分矩阵H中得分最高的元组的下标
        int curM, curN, i, j, max;
        curM = 0;
        curN = 0;
        max = H[0][0];
        for (i = 0; i < m; i++) {
            for (j = 0; j < n; j++) {
                if (H[i][j] > max) {
                    max = H[i][j];
                    curM = i;
                    curN = j;
                }
            }
        }
        this.maxIndexM = curM;
        this.maxIndexN = curN;
    }

    private void traceBack(String s1, String s2, int m, int n) {//回溯 寻找最相似子序列
        if (this.H[m][n] == 0) {
            return;
        }
        if (this.H[m][n] == this.H[m - 1][n] + SPACE) {
            this.stk1.add(s1.charAt(m - 1));
            this.stk2.add('-');
            this.traceBack(s1, s2, m - 1, n);
        } else if (this.H[m][n] == this.H[m][n - 1] + SPACE) {
            this.stk1.add('-');
            this.stk2.add(s2.charAt(n - 1));
            this.traceBack(s1, s2, m, n - 1);
        } else {
            this.stk1.push(s1.charAt(m - 1));
            this.stk2.push(s2.charAt(n - 1));
            this.traceBack(s1, s2, m - 1, n - 1);
        }
    }

    public void find(String s1, String s2) {
        //initMatrix(s1.length(), s2.length());
        int i, j;
        this.H = new int[s1.length() + 1][s2.length() + 1];
        this.isEmpty = new int[s1.length() + 1][s2.length() + 1];
        for (i = 0; i <= s1.length(); i++) {
            for (j = 0; j <= s2.length(); j++) {
                this.isEmpty[i][j] = 1;
            }
        }
        this.calculateMatrix(s1, s2, s1.length(), s2.length());
        this.findMaxIndex(this.H, this.H.length, this.H[0].length);
        this.traceBack(s1, s2, this.maxIndexM, this.maxIndexN);
        ArrayList<Character> arr1 = new ArrayList<Character>();
        ArrayList<Character> arr2 = new ArrayList<Character>();
        while (!this.stk1.empty()) {
            arr1.add(this.stk1.pop());
        }
        this.subSq1 = arr1.toString();
        while (!this.stk2.empty()) {
            arr2.add(this.stk2.pop());
        }
        this.subSq2 = arr2.toString();
    }

    @Override
    public float getSimilarity(String source, String target) {
        subSq1 = null;
        subSq2 = null;
        maxIndexM = maxIndexN = 0;
        find(source, target);
        char[] comchar = subSq1.toCharArray();
        int comlen = 0;
        for (char c : comchar) {
            if (c != '-' && c != ',' && c != '[' && c != ']' && c != ' ') {
                comlen++;
            }
        }
        return (float) (comlen * 1.0 / source.length());
    }
}
