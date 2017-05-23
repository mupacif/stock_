package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class StockDetail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    TextView symbolTV;
    TextView priceTV;
    TextView absoluteChangeTV;
    TextView pourcentageChangeTV;
    TextView historyTV;
    LineChart lineChart;
    private static final int STOCK_LOADER = 0;
    private String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
        symbolTV = (TextView) findViewById(R.id.tv_stock_detail_symbol);
        priceTV = (TextView) findViewById(R.id.tv_stock_detail_price);
        absoluteChangeTV = (TextView) findViewById(R.id.tv_stock_detail_absolute_change);
        pourcentageChangeTV = (TextView) findViewById(R.id.tv_stock_detail_pourcentage_change);
        historyTV = (TextView) findViewById(R.id.tv_stock_detail_history);
        lineChart = (LineChart) findViewById(R.id.ct_stock_detail_chart);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            this.symbol = extra.getString(Contract.Quote.COLUMN_SYMBOL, "error");
            Timber.d("Symbol clicked: %s", this.symbol);
        }
        getSupportLoaderManager().initLoader(STOCK_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.URI.buildUpon().appendPath(this.symbol).build(),
                Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e("Loader", "loading finished");
        if (data.getCount() != 0) {
            Log.i("Loader", "some data size" + data.getCount() + " loaded");
            data.moveToNext();
            Log.i("Loader deload", "data:" + data.getFloat(Contract.Quote.POSITION_HISTORY));
            symbolTV.setText(data.getString(1));
            priceTV.setText(String.valueOf(data.getFloat(2)));
            absoluteChangeTV.setText(String.valueOf(data.getFloat(3)));
            pourcentageChangeTV.setText(String.valueOf(data.getFloat(4)));
          //  historyTV.setText(data.getString(5));
            Log.i("chart drawer",data.getString(5));
            List<Entry> entries = new ArrayList<Entry>();

            ByteArrayInputStream byteArrayInputStream
                    = new ByteArrayInputStream(data.getString(5).getBytes(StandardCharsets.UTF_8));

            InputStreamReader is = new InputStreamReader(byteArrayInputStream);

            BufferedReader br = new BufferedReader(is);

            try {
                br.readLine(); // skip the first line
                Log.e("chart drawer","entered try");
                // Parse CSV
                int i =0;
                for (String line = br.readLine(); line != null; line = br.readLine(),i++) {

                    String[] chartLine = line.split(",");
                    Log.e("chart drawer","line:"+line+"date:"+chartLine[0]+", value:"+chartLine[1]);
                    entries.add(new Entry(Long.valueOf(chartLine[0]),Float.valueOf(chartLine[1])));

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
          //  Collections.reverse(entries);
            XAxis xAxis = this.lineChart.getXAxis();
            xAxis.setGranularity(1f);
            xAxis.setValueFormatter(new IAxisValueFormatter() {
                private SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM");
                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                  //  long millis = TimeUnit.DAYS.toMillis((long) value);
                    return mFormat.format(new Date((long)value));
                }
            });
            this.lineChart.setBackgroundColor(Color.WHITE);
            LineDataSet dataSet = new LineDataSet(entries, "Label");

            dataSet.setColor(Color.RED);
            dataSet.setLineWidth(1.f);
            dataSet.setValueTextColor(Color.CYAN);
            LineData lineData = new LineData(dataSet);
            this.lineChart.setData(lineData);
            this.lineChart.invalidate(); // refresh
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
