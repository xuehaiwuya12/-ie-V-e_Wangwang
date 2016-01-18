package com.koen.wangdog.util;

import com.koen.wangdog.bean.User;

import java.util.Comparator;

/**
 * Created by smit on 2016/1/18.
 */
public class PinyinComparator implements Comparator<User>{
    @Override
    public int compare(User lhs, User rhs) {
        if (lhs.getSortLetters().equals("@")
                || rhs.getSortLetters().equals("#")) {
            return -1;
        } else if (lhs.getSortLetters().equals("#") || rhs.getSortLetters().equals("@")) {
            return 1;
        } else {
            return lhs.getSortLetters().compareTo(rhs.getSortLetters());
        }
    }
}
