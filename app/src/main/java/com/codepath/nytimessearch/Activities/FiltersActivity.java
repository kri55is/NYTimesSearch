package com.codepath.nytimessearch.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;

import com.codepath.nytimessearch.Models.Filters;
import com.codepath.nytimessearch.R;

import java.util.ArrayList;


public class FiltersActivity extends AppCompatActivity {

    private final String TAG = "FiltersActivityTAG";

    private DatePicker dpStaringDate;
    private Spinner spSortOrder;
    private CheckBox cbArt;
    private CheckBox cbFashion;
    private CheckBox cbSports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        setupViews();
    }

    private void setupViews() {

        dpStaringDate = (DatePicker) findViewById(R.id.dpStartingDate);
        spSortOrder = (Spinner) findViewById(R.id.spSortOrder);
        cbArt = (CheckBox) findViewById(R.id.cbArt);
        cbFashion = (CheckBox) findViewById(R.id.cbFashion);
        cbSports = (CheckBox) findViewById(R.id.cbSports);

    }

    public void onClickSave(View view) {

        int year = dpStaringDate.getYear();
        //getMonth returns 0 for january!?
        int month = dpStaringDate.getMonth() + 1;
        int day = dpStaringDate.getDayOfMonth();

        boolean sortOldest = false;
        String selectedSort = spSortOrder.getSelectedItem().toString();
        if (selectedSort.equals("Oldest")){
            sortOldest = true;
        }

        ArrayList<String> arrayNewsDeskValues = new ArrayList<>();

        if (cbArt.isChecked()) arrayNewsDeskValues.add(cbArt.getText().toString());
        if (cbFashion.isChecked()) arrayNewsDeskValues.add(cbFashion.getText().toString());
        if (cbSports.isChecked()) arrayNewsDeskValues.add(cbSports.getText().toString());

        Filters filter = new Filters(day, month, year, sortOldest,arrayNewsDeskValues);


        Intent intent = new Intent();
        intent.putExtra("filter", filter);
//        intent.putExtra("year", 198);
        setResult(RESULT_OK, intent);
//        else{
//            setResult(RESULT_CANCELED);
//        }
        finish();
    }
}
