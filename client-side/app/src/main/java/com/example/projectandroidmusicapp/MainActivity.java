package com.example.projectandroidmusicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.projectandroidmusicapp.activities.AlbumFragment;
import com.example.projectandroidmusicapp.activities.HomeFragment;
import com.example.projectandroidmusicapp.activities.ProfileFragment;
import com.example.projectandroidmusicapp.activities.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

public class MainActivity extends AppCompatActivity implements BottomAddToPlaylist.BottomSheetListener {
    private SlidingUpPanelLayout slideUpPanelLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new HomeFragment()).commit();
        }
        slideUpPanelLayout = findViewById(R.id.sliderView);
        slideUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        slideUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
                if(newState == SlidingUpPanelLayout.PanelState.EXPANDED){
                    ObjectAnimator animation = ObjectAnimator.ofFloat(navigationView, "translationY", (float) navigationView.getHeight());
                    animation.setDuration(400);
                    animation.start();
                }
                else{
                    ObjectAnimator animation = ObjectAnimator.ofFloat(navigationView, "translationY", (float) 0);
                    animation.setDuration(100);
                    animation.start();

                }
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.navigation_search:
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.navigation_album:
                            selectedFragment = new AlbumFragment();
                            break;
                        case R.id.navigation_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                    }

                    if(item.getItemId() == R.id.navigation_home ) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                    }
                    else{
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).addToBackStack(null).commit();
                    }

                    return true;
                }
            };
    @Override
    public void onBackPressed() {
        if (slideUpPanelLayout != null &&
                (slideUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || slideUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            slideUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
  }

    @Override
    public void onButtonClicked(String text) {

    }

}