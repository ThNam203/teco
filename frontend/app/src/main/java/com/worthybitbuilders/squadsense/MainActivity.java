package com.worthybitbuilders.squadsense;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.worthybitbuilders.squadsense.fragments.HomeFragment;
import com.worthybitbuilders.squadsense.fragments.MoreFragment;
import com.worthybitbuilders.squadsense.fragments.NotificationFragment;
import com.worthybitbuilders.squadsense.fragments.WorkFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    public enum OptionViewProject {ALLPROJECT, RECENT, MYPROJECT}

    private static OptionViewProject homeFragmentOptionViewProject = OptionViewProject.RECENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        bottomNavigationView = findViewById(R.id.nav_bottom);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();

        bottomNavigationView.setOnItemSelectedListener(item -> {
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
        });
    }

    public static OptionViewProject getHomeFragmentOptionViewProject()
    {
        return homeFragmentOptionViewProject;
    }

    public static void setHomeFragmentOptionViewProject(OptionViewProject optionViewProject)
    {
        homeFragmentOptionViewProject = optionViewProject;
    }

}