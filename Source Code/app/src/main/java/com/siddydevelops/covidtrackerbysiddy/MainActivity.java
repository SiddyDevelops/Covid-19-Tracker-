package com.siddydevelops.covidtrackerbysiddy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.hbb20.CountryCodePicker;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    CountryCodePicker countryCodePicker;
    TextView mtodayTotal, mtotal, mactive, mdeaths, mtodayDeaths, mrecovered, mtodayRecovered, mtodayActive;
    String country;
    TextView mfilter;
    Spinner spinner;
    String[] types = {"Cases","Deaths","Recovered","Active"};
    private List<ModelClass> modelClassList;
    private List<ModelClass> modelClassList2;
    PieChart mpieChart;
    private RecyclerView recyclerView;
    com.siddydevelops.covidtrackerbysiddy.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        countryCodePicker = findViewById(R.id.ccp);
        mtodayActive = findViewById(R.id.todayActive);
        mactive = findViewById(R.id.activeCase);
        mdeaths = findViewById(R.id.totalDeaths);
        mtodayDeaths = findViewById(R.id.todayDeaths);
        mrecovered = findViewById(R.id.recoveredCase);
        mtodayRecovered = findViewById(R.id.todayRecovered);
        mtodayTotal = findViewById(R.id.totalCase);
        mtotal = findViewById(R.id.todayTotal);

        mpieChart = findViewById(R.id.pieChart);
        spinner = findViewById(R.id.spinner);
        mfilter = findViewById(R.id.filter);
        recyclerView = findViewById(R.id.recyclerView);
        modelClassList = new ArrayList<>();
        modelClassList2 = new ArrayList<>();

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        ApiUtilities.getAPIInterface().getCountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList2.addAll(response.body());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });

        adapter = new Adapter(getApplicationContext(),modelClassList2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        countryCodePicker.setAutoDetectedCountry(true);
        country = countryCodePicker.getSelectedCountryName();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country = countryCodePicker.getSelectedCountryName();
                fetchData();
            }
        });

        fetchData();

    }

    private void fetchData()
    {
        ApiUtilities.getAPIInterface().getCountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList.addAll(response.body());
                for(int i=0;i<modelClassList.size();i++)
                {
                    if(modelClassList.get(i).getCountry().equals(country))
                    {
                        mactive.setText((modelClassList.get(i).getActive()));
                        mtodayDeaths.setText((modelClassList.get(i).getTodayDeaths()));
                        mtodayRecovered.setText((modelClassList.get(i).getTodayRecovered()));
                        mtodayTotal.setText((modelClassList.get(i).getTodayCases()));
                        mtotal.setText((modelClassList.get(i).getCases()));
                        mdeaths.setText((modelClassList.get(i).getDeaths()));
                        mrecovered.setText((modelClassList.get(i).getRecovered()));

                        int active, total, recovered, deaths;
                        active = Integer.parseInt(modelClassList.get(i).getActive());
                        total = Integer.parseInt(modelClassList.get(i).getCases());
                        recovered = Integer.parseInt(modelClassList.get(i).getRecovered());
                        deaths = Integer.parseInt(modelClassList.get(i).getDeaths());

                        updateGraph(active,total,recovered,deaths);

                    }
                }
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });



    }

    private void updateGraph(int active, int total, int recovered, int deaths)
    {
        mpieChart.clearChart();
        mpieChart.addPieSlice(new PieModel("Confirm",total,Color.parseColor("#FFB701")));
        mpieChart.addPieSlice(new PieModel("Active",active,Color.parseColor("#FF4CAF50")));
        mpieChart.addPieSlice(new PieModel("Recovered",recovered,Color.parseColor("#38ACCD")));
        mpieChart.addPieSlice(new PieModel("Deaths",deaths,Color.parseColor("#F55C47")));
        mpieChart.startAnimation();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String item = types[position];
        mfilter.setText(item);
        adapter.filter(item);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}