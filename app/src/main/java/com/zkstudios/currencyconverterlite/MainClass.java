package com.zkstudios.currencyconverterlite;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.zkstudios.currencyconverterlite.Fragments.HomeFragment;
import com.zkstudios.currencyconverterlite.Fragments.PastRateFragment;
import com.zkstudios.currencyconverterlite.Fragments.TableFragment;

public class MainClass extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setSelectedItemId(R.id.navHome);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new HomeFragment()).commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment=null;
                switch (item.getItemId())
                {
                    case R.id.navHome:selectedFragment=new HomeFragment();break;
                    case R.id.pastRate:selectedFragment=new PastRateFragment();break;
                    case R.id.tableFrom:selectedFragment=new TableFragment();break;
                    case R.id.tableTo:selectedFragment=new TableFragment();break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,selectedFragment).commit();
                return true;
                }
            };
}
