package com.worthybitbuilders.squadsense;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.worthybitbuilders.squadsense.fragments12.HomeFragment;
import com.worthybitbuilders.squadsense.fragments12.MoreFragment;
import com.worthybitbuilders.squadsense.fragments12.NotificationFragment;
import com.worthybitbuilders.squadsense.fragments12.WorkFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        bottomNavigationView = findViewById(R.id.nav_bottom);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.item_home:
                    {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                        break;
                    }
                    case R.id.item_work:
                    {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WorkFragment()).commit();
                        break;
                    }
                    case R.id.item_notification:
                    {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotificationFragment()).commit();
                        break;
                    }
                    case R.id.item_more:
                    {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MoreFragment()).commit();
                        break;
                    }
                }
                return true;
            }
        });
    }
}