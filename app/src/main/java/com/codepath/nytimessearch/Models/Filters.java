package com.codepath.nytimessearch.Models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by emilie on 9/19/17.
 */

public class Filters implements Serializable {

    int beginDateDay;
    int beginDateMonth;
    int beginDateYear;

    boolean sortingOldest;

    ArrayList<String> newsDeskValues;

    public Filters(int day, int month, int year, boolean sortingOldest, ArrayList<String> newsDeskValues) {

        this.beginDateDay = day;
        this.beginDateMonth = month;
        this.beginDateYear = year;

        this.sortingOldest = sortingOldest;

        this.newsDeskValues= new ArrayList<>(newsDeskValues);

    }
}
