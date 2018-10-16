package com.laxi.data.algorithm;

public class DynamicProgramming implements SimilarAlgorithm {

    private final static int MAX_STRING_LEN = 200;

    @Override
    public float getSimilarity(String source, String target) {
        EditDistance(source, target);
        int len_a = source.length();
        int len_b = target.length();
        int[][] temp = new int[len_a + 1][len_b + 1];
        float distance = compute_distance(source, 0, len_a - 1, target, 0, len_b - 1, temp);
        float similarity = 1 - distance / Math.max(len_a, len_b);
        return similarity;
    }

    /*
     * 动态规划的方法求解两个字符串的最小编辑距离
     * source和target字符串的长度不能超过d矩阵的限制
     */
    private void EditDistance(String source, String target) {
        int i, j;
        int[][] d = new int[MAX_STRING_LEN][MAX_STRING_LEN];

        for (i = 0; i <= source.length(); i++) {
            d[i][0] = i;
        }
        for (j = 0; j <= target.length(); j++) {
            d[0][j] = j;
        }

        for (i = 1; i <= source.length(); i++) {
            for (j = 1; j <= target.length(); j++) {
                if (source.charAt(i - 1) == target.charAt(j - 1)) {
                    d[i][j] = d[i - 1][j - 1]; //不需要编辑操作
                } else {
                    int edIns = d[i][j - 1] + 1; //source 插入字符
                    int edDel = d[i - 1][j] + 1; //source 删除字符
                    int edRep = d[i - 1][j - 1] + 1; //source 替换字符
                    d[i][j] = min(edIns, edDel, edRep);
                }
            }
        }
        float distance = d[source.length()][target.length()];
        float similarity = 1 - distance / Math.max(source.length(), target.length());
        //System.out.println("动态规范方法求解相似度为"+similarity);
    }

    /**
     * 递归求解最小编辑距离
     *
     * @param strA
     * @param pABegin
     * @param pAEnd
     * @param strB
     * @param pBBegin
     * @param pBEnd
     * @param temp
     * @return
     */
    public int compute_distance(String strA, int pABegin, int pAEnd,
                                String strB, int pBBegin, int pBEnd, int[][] temp) {
        int a, b, c;
        if (pABegin > pAEnd) {
            if (pBBegin > pBEnd) {
                return 0;
            } else {
                return pBEnd - pBBegin + 1;
            }
        }

        if (pBBegin > pBEnd) {
            if (pABegin > pAEnd) {
                return 0;
            } else {
                return pAEnd - pABegin + 1;
            }
        }

        if (strA.charAt(pABegin) == strB.charAt(pBBegin)) {
            if (temp[pABegin + 1][pBBegin + 1] != 0) {
                a = temp[pABegin + 1][pBBegin + 1];
            } else {
                a = compute_distance(strA, pABegin + 1, pAEnd, strB, pBBegin + 1, pBEnd, temp);
            }
            return a;
        } else {
            if (temp[pABegin + 1][pBBegin + 1] != 0) {
                a = temp[pABegin + 1][pBBegin + 1];
            } else {
                a = compute_distance(strA, pABegin + 1, pAEnd, strB, pBBegin + 1, pBEnd, temp);
                temp[pABegin + 1][pBBegin + 1] = a;
            }

            if (temp[pABegin + 1][pBBegin] != 0) {
                b = temp[pABegin + 1][pBBegin];
            } else {
                b = compute_distance(strA, pABegin + 1, pAEnd, strB, pBBegin, pBEnd, temp);
                temp[pABegin + 1][pBBegin] = b;
            }

            if (temp[pABegin][pBBegin + 1] != 0) {
                c = temp[pABegin][pBBegin + 1];
            } else {
                c = compute_distance(strA, pABegin, pAEnd, strB, pBBegin + 1, pBEnd, temp);
                temp[pABegin][pBBegin + 1] = c;
            }

            return min(a, b, c) + 1;

        }

    }

    //得到最小值
    private int min(int... is) {
        int min = Integer.MAX_VALUE;
        for (int i : is) {
            if (min > i) {
                min = i;
            }
        }
        return min;
    }

}
