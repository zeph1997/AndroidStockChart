package com.example.zeph1.stockprediction;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

public class fetchAlphaVantageData extends AsyncTask<Void, Void, Void> {
    String function = "TIME_SERIES_DAILY";
    String symbol = MainActivity.ptSearchStock.getText().toString();
    String outputsize = "full";
    String apikey = ""; // ACCESS TOKEN KEY FOR ALPHAVANTAGE
    String data = ""; // To convert website to String format
    String date = "";
    String opening = "";
    String closing = "";
    float[] closings = new float[20];
    String[] dates = new String[20];

    @Override
    protected Void doInBackground(Void... voids) {
        // Making a request to url and getting response
        try {
            URL url = new URL("https://www.alphavantage.co/query?function=" + function + "&symbol=" + symbol + "&outputsize" + outputsize + "&apikey=" + apikey);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while(line!=null) {
                line = bufferedReader.readLine(); // Read each line of JSON file
                data += line; // Save each line of JSON file into 'data'
            }
            JSONObject timesSeriesDaily = new JSONObject(data);
            Log.d("Json TimeseriesDaily",timesSeriesDaily.toString());
            //If the latest data for the listed company exists
            if(timesSeriesDaily.has("Time Series (Daily)") && !timesSeriesDaily.isNull("Time Series (Daily)")) {
                JSONObject timeSeriesDailyAvailable = timesSeriesDaily.getJSONObject("Time Series (Daily)");
                //we get more than 20 days worth of data
                Log.e("JSON Object",timeSeriesDailyAvailable.toString());
                // Set keys to Iterator through timeSeriesDailyAvailable
                Iterator<String> timeSeriesDailyDates = timeSeriesDailyAvailable.keys();
                // Get latest date, opening and closing
                if(timeSeriesDailyDates.hasNext()) {
                    date = timeSeriesDailyDates.next();
                    opening = timeSeriesDailyAvailable.getJSONObject(date).getString("1. open");
                    closing = timeSeriesDailyAvailable.getJSONObject(date).getString("4. close");


                    closings[0] = Float.parseFloat(closing);
                    dates[0] = date;
                    for(int i = 1;i < 20;i++){
                        dates[i] = timeSeriesDailyDates.next();
                        closings[i] = Float.parseFloat(timeSeriesDailyAvailable.getJSONObject(dates[i]).getString("4. close"));
                    }
                }
                /*
                    // TODO: iterate and plot range of dates opening/closing on the line graph
                    (Iterating graph in the future if date range of data exists)
                    for (Iterator<String> iter = timeSeriesDailyDates; iter.hasNext();) {
                        String element = iter.next();
                        if (element.equals(getLatestDate())) {
                            opening = timeseriesdaily.getJSONObject(element).getString("1. open");
                            closing = timeseriesdaily.getJSONObject(element).getString("4. close");
                            break;
                        }
                    }
                 */
            }
            else {
                date = "No dates found";
                opening = "-";
                closing = "-";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //Set latest date, opening and closing
        MainActivity.tvDate.setText("Latest Update: " + date);
        MainActivity.tvOpening.setText("Opening: $" + opening);
        MainActivity.tvClosing.setText("Closing: $" + closing);

        //reverse the array containing all the data
        Collections.reverse(Arrays.asList(dates));
        float[] new_closings = new float[20];
        for(int i=0;i<closings.length;i++){
            new_closings[19-i]=closings[i];
        }
        List<PointValue> pv = new ArrayList<PointValue>();
        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        for(int i=0;i<dates.length;i++){
            pv.add(new PointValue(i,new_closings[i]));
            axisValues.add(new AxisValue(i,dates[i].toCharArray()));
        }
        Line line = new Line(pv).setColor(Color.BLUE).setCubic(true);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        Axis axisX = new Axis(axisValues).setHasLines(true).setLineColor(Color.BLACK);
        Axis axisY = new Axis().setHasLines(true).setLineColor(Color.BLACK);

        axisX.setHasTiltedLabels(true);
        axisY.setName("Price").setTextColor(Color.BLACK);
        axisX.setName("Time").setTextColor(Color.BLACK);
        LineChartData data = new LineChartData();
        data.setLines(lines);
        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        MainActivity.lcv.setLineChartData(data);
    }

    public String getLatestDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd ");
        String latestDate = mdformat.format(calendar.getTime());
        return latestDate;
    }

}
