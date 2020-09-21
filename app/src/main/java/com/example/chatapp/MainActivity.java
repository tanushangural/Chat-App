package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout= findViewById(R.id.tabMain);
        viewPager = findViewById(R.id.vpMain);
        setViewPager();
    }

    class Adapter extends FragmentPagerAdapter{

        public Adapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    chatFragment chatFRagment = new chatFragment();
                    return chatFRagment;
                case 1:
                    requestsFragment requestsFRagment = new requestsFragment();
                    return requestsFRagment;
                case 2:
                    findFriendsFragment findFriendsFRagment = new findFriendsFragment();
                    return findFriendsFRagment;

            }
            return null;
        }

        @Override
        public int getCount() {
          return tabLayout.getTabCount();
        }
    }

    private void setViewPager(){
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.tab_view));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.tab_request));
        tabLayout.addTab(tabLayout.newTab().setCustomView(R.layout.tab_findfriends));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        Adapter adapter = new Adapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        viewPager.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile,menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.menuProfile){
            startActivity(new Intent(MainActivity.this,ProfileActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean doubleBackPressed = false;

    @Override
    public void onBackPressed() {
        if(tabLayout.getSelectedTabPosition()>0){
            tabLayout.selectTab(tabLayout.getTabAt(0));
        }
        else{
            if(doubleBackPressed){
                finishAffinity();
            }
            else{
                doubleBackPressed=true;
                Toast.makeText(this, "pressed again to exit", Toast.LENGTH_SHORT).show();

                android.os.Handler handler = new android.os.Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackPressed=false;
                    }
                },2000);
            }

        }
        //super.onBackPressed();
    }
}
