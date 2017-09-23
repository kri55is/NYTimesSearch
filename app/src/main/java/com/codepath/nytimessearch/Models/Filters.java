package com.codepath.nytimessearch.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by emilie on 9/19/17.
 */

public class Filters implements Serializable {

    private int beginDateDay;
    private int beginDateMonth;
    private int beginDateYear;

    private boolean sortingOldest;

    private ArrayList<String> newsDeskValues;

    public int getBeginDateDay() {
        return beginDateDay;
    }

    public int getBeginDateMonth() {
        return beginDateMonth;
    }

    public int getBeginDateYear() {
        return beginDateYear;
    }

    public boolean isSortingOldest() {
        return sortingOldest;
    }

    public ArrayList<String> getNewsDeskValues() {
        return newsDeskValues;
    }

    public String getBeginDateDayAsString() {
        String sBeginDateDay = Integer.toString(beginDateDay);

        if (beginDateDay > 10) {
            return sBeginDateDay;
        }
        else{
            String result = "0";
            result = result + sBeginDateDay;
            return result;
        }
    }

    public String getBeginDateMonthAsString() {
        String sBeginDateMonth = Integer.toString(beginDateMonth);

        if (beginDateMonth > 10) {
            return sBeginDateMonth;
        }
        else{
            String result = "0";
            result = result + sBeginDateMonth;
            return result;
        }
    }

    public String getBeginDateYearAsString() {
        return Integer.toString(beginDateYear);
    }



    public Filters(int day, int month, int year, boolean sortingOldest, ArrayList<String> newsDeskValues) {

        this.beginDateDay = day;
        this.beginDateMonth = month;
        this.beginDateYear = year;

        this.sortingOldest = sortingOldest;

        this.newsDeskValues= new ArrayList<>(newsDeskValues);

    }
}
