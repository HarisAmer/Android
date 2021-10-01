package com.zkstudios.currencyconverterlite;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends AppCompatActivity {
    ImageView swapImage;
    Spinner fromSpinner,toSpinner;
    static String from="usd",to="pkr";
    static ProgressBar progressBar;
    static int val1=0;
    static int val2=1;
    static TextView information;
    static SharedPreferences preferences;
    static String keyVal1="fromValue",keyVal2="toValue",keyVal3="pastRate",
            keyVal4="fromCurren",keyVal5="toCurren";
    static String pastRate="";
    static boolean displayed=false;
    static boolean showNotification;
    static String intervalValue;
    AdView adView;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.showmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTheme(R.style.Theme_CurrencyChecker);
        setContentView(R.layout.activity_main);
        if(JobClass.context==null)
        {
            JobClass.context=this;
        }
        //Instantiating SavedInstances
        preferences= PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        //Getting the saved values
        getValues();
        adView=findViewById(R.id.adView0);
        fromSpinner=findViewById(R.id.from);
        swapImage=findViewById(R.id.swap);
        toSpinner=findViewById(R.id.to);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        fromSpinner.setAdapter(setSpinnerData());
        toSpinner.setAdapter(setSpinnerData());
        fromSpinner.setSelection(val1);
        toSpinner.setSelection(val2);
        information=findViewById(R.id.information);
        information.setText(pastRate);
        if(from.equals(""))
        {
            from="(USD)";
        }
        if(to.equals(""))
        {
            to="(PKR)";
        }
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                from=parent.getItemAtPosition(position).toString();
                val1=position;
                setValue(val1,val2,pastRate);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, "Nothing Changed", Toast.LENGTH_SHORT).show();
            }
        });
        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                to=parent.getItemAtPosition(position).toString();
                val2=position;
                setValue(val1,val2,pastRate);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, "Nothing Changed", Toast.LENGTH_SHORT).show();
            }
        });
        swapImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempVal=val1;
                val1=val2;
                val2=tempVal;
                fromSpinner.setSelection(val1);
                toSpinner.setSelection(val2);
                String temp=from;
                from=to;
                to=temp;
                refreshButton(null);
            }
        });
        refreshButton(null);
        displayed=true;
        if(showNotification)
        {
            jobScheduler(intervalValue);
        }
        try {

            MobileAds.initialize(this, initializationStatus -> {
            });
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
        catch (Exception xx)
        {

        }

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void jobScheduler(String val)
    {
        int time;
        long timeValues;
        String tempTime;
        if(!val.equals(""))
        {
            tempTime=val.substring(0,val.indexOf(" "));
            time=Integer.parseInt(tempTime);
            if(time==15 ||time ==30)
            {
                timeValues=time * 60 * 1000;
            }
            else
            {
                timeValues=time* 60 * 60 * 1000;
            }
            ComponentName componentName = new ComponentName(this, JobClass.class);
            JobInfo info = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                info = new JobInfo.Builder(96843, componentName)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setPersisted(true)
                        .setPeriodic(timeValues,5*60*1000)
                        .build();
            }
            else
            {
                info = new JobInfo.Builder(96843, componentName)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setPersisted(true)
                        .setPeriodic(timeValues)
                        .build();
            }
            JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            scheduler.schedule(info);
        }

    }

    @Override
    protected void onPause() {
        displayed=false;
        super.onPause();
    }

    @Override
    protected void onStart() {
        displayed=true;
        super.onStart();
    }

    public static void refreshButton(View view)
    {
        if(from.equals(to))
        {
            Toast.makeText(information.getContext(), "Same Currency", Toast.LENGTH_SHORT).show();
        }
        else
        {
            FetchData fetchData=new FetchData();
            fetchData.execute(extractValue(from)+"+to+"+extractValue(to));
        }
    }
    public static String extractValue(String str)
    {
        int int1=str.indexOf("(");
        int int2=str.lastIndexOf(")");
        String value=str.substring(int1+1,int2);
        return  value.toLowerCase();
    }

    public ArrayAdapter setSpinnerData()
    {
        ArrayAdapter arrayAdapter= ArrayAdapter.createFromResource(this,
                R.array.countryNames, android.R.layout.simple_spinner_item); {
    };
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return  arrayAdapter;
    }
    public static void setValue(int val1, int val2,String pastRate) {
        preferences.edit().putInt(keyVal1, val1).apply();
        preferences.edit().putInt(keyVal2, val2).apply();
        preferences.edit().putString(keyVal3, pastRate).apply();
        preferences.edit().putString(keyVal4, from).apply();
        preferences.edit().putString(keyVal5, to).apply();
    }
    public static void getValues()
    {
        //Setting the values in Internal Storage
        val1=preferences.getInt(keyVal1,0);
        val2=preferences.getInt(keyVal2,0);
        pastRate=preferences.getString(keyVal3,"");
        from=preferences.getString(keyVal4,"");
        to=preferences.getString(keyVal5,"");
        showNotification=preferences.getBoolean("showNotification",true);
        intervalValue=preferences.getString("interval","1 hour");
    }

    @Override
    protected void onResume() {
        super.onResume();
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        getValues();
        if(showNotification)
        {
            jobScheduler(intervalValue);
        }
        else
        {
            JobScheduler  jobScheduler = (JobScheduler)this.getSystemService(Context.JOB_SCHEDULER_SERVICE );
            jobScheduler.cancel(96843);
        }
    }
}