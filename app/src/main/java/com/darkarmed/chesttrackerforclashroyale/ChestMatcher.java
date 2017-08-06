package com.darkarmed.chesttrackerforclashroyale;

import java.util.Comparator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Created by Xu on 6/10/16.
 */
public class ChestMatcher {

    private String mLoop;
    private int mLoopLength;

    public ChestMatcher(String loop) {
        mLoopLength = loop.length();
        mLoop = loop + loop;
    }

    public SortedSet<Map.Entry<Integer, Integer>> getMatchedPositions(String chests, boolean fuzzy) {
        Map<Integer, Integer> treeMap = new TreeMap<>();
        int chestsNum = chests.length();
        if (chestsNum > 0) {
            for (int pos = chestsNum - 1; pos < mLoopLength + chestsNum; ++pos) {
                int i = 0;
                for ( ; i < chestsNum && mLoop.charAt(pos - i) == chests.charAt(chestsNum - 1 - i); ++i);
                if (i == chestsNum || fuzzy && i > 4) {
                    treeMap.put(pos % mLoopLength + 1, i);
                }
//                int matchedChestNum = 0;
//                for (int i = 0; i < chestsNum && mLoop.charAt(pos - i) == chests.charAt(chestsNum - 1 - i); ++i) {
//                    matchedChestNum++;
//                }
//                if (matchedChestNum == chestsNum || fuzzy && matchedChestNum > 4) {
//                    treeMap.put(pos % mLoopLength + 1, matchedChestNum);
//                }
            }
        }
        SortedSet<Map.Entry<Integer, Integer>> matched = new TreeSet<>(
                new Comparator<Map.Entry<Integer, Integer>>() {
                    @Override
                    public int compare(Map.Entry<Integer, Integer> lhs, Map.Entry<Integer, Integer> rhs) {
                        int res = rhs.getValue().compareTo(lhs.getValue());     //descending order
                        return res != 0 ? res : 1;
                    }
                }
        );
        matched.addAll(treeMap.entrySet());
        return matched;
    }

    public Map<Integer, Integer> getMatchedPositions(String chests) {
        Map<Integer, Integer> matched = new TreeMap<>();
        int chestsNum = chests.length();
        if (chestsNum > 0) {
            int matchLength = 3;
            for (int pos = chestsNum - 1; pos < mLoopLength + chestsNum; ++pos) {
                int i = 0;
                for ( ; i < chestsNum && mLoop.charAt(pos - i) == chests.charAt(chestsNum - 1 - i); ++i);
                if (i >= matchLength) {
                    if (i > matchLength) {
                        matched.clear();
                        matchLength = i;
                    }
                    matched.put(pos % mLoopLength + 1, i);
                }
            }
        }

        return matched;
    }

}
