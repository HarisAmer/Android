package com.zkstudios.currencyconverterlite;

import static com.zkstudios.currencyconverterlite.JobClass.fromJobClass;
import static com.zkstudios.currencyconverterlite.JobClass.showNotification;
import static com.zkstudios.currencyconverterlite.MainActivity.displayed;
import static com.zkstudios.currencyconverterlite.MainActivity.information;
import static com.zkstudios.currencyconverterlite.MainActivity.keyVal3;
import static com.zkstudios.currencyconverterlite.MainActivity.progressBar;
import static com.zkstudios.currencyconverterlite.MainActivity.pastRate;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;




public class FetchData extends AsyncTask<String,Void,String>
{
    String str="";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(displayed && !fromJobClass)
        {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPostExecute(String unused) {
        if(unused != null) {
            if (displayed && !fromJobClass) {
                progressBar.setVisibility(View.GONE);
                super.onPostExecute(unused);
                information.setText(str);
                pastRate = str;
                Toast.makeText(progressBar.getContext(), "Refreshed", Toast.LENGTH_SHORT).show();
                SharedPreferences preferences;
                preferences = PreferenceManager.getDefaultSharedPreferences(JobClass.context);
                preferences.edit().putString(keyVal3, str).apply();
            } else {
                super.onPostExecute(unused);
                SharedPreferences preferences;
                preferences = PreferenceManager.getDefaultSharedPreferences(JobClass.context);
                preferences.edit().putString(keyVal3, str).apply();
                if(preferences.getBoolean("showNotification",true))
                {
                    showNotification(str);
                }
            }
        }
        else {
            if(displayed && !fromJobClass)
            {
                super.onPostExecute(unused);
                progressBar.setVisibility(View.GONE);
                information.setText(pastRate);
                Toast.makeText(progressBar.getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }

    }
    @Override
    protected String doInBackground(String... strings) {
        try {
            String url="https://www.xe.com/currencyconverter/convert/?Amount=900&From=USD&To=EUR";
            Document doc= Jsoup.connect(url)
                    .get();
            Elements data=doc.select("div");
            //Getting the size
            int size=data.size();
            for(int i=0;i<size;i++)
            {
                String fromRate=data.select("p.result__ConvertedText-sc-1bsijpp-0.gwvOOF")
                        .eq(i)
                        .text();
                String toRate=data.select("p.result__BigRate-sc-1bsijpp-1.iGrAod")
                        .eq(i).text();



                str=fromRate+toRate;
//                return  rate;
                Log.e("from=",fromRate);
                Log.e("toRate=",toRate);
            }
            String fromOne=data.select("div.unit-rates___StyledDiv-sc-1dk593y-0.dEqdnx")
                    .select("p")
                    .eq(0)
                    .text();
            Log.e("toCountry=",fromOne);

        }
        catch (Exception e)
        {
            Log.e("exception", e.toString());
        }
        return null;
    }

}
