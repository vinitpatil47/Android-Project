package com.example.vinit.farmzone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class ProviderHomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private RenterHomeFragment renterHomeFragment;
    private RenterRequestFragment renterRequestFragment;
    private RenterAcceptedFragment renterAcceptedFragment;
    private ChatFragment chatFragment;
    private Bundle b;
    private String fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider_home);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Farm Zone");

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.navigationbar);
        renterAcceptedFragment = new RenterAcceptedFragment();
        renterHomeFragment = new RenterHomeFragment();
        renterRequestFragment = new RenterRequestFragment();
        chatFragment = new ChatFragment();

        fragment = getIntent().getStringExtra("fragment");

        if(fragment!=null && fragment.equals("chatFragment"))
        {
            b = new Bundle();
            b.putString("who","Provider");
            chatFragment.setArguments(b);
            setnavigationbar(chatFragment);
        }
        else {
            b = new Bundle();
            b.putString("who","Provider");
            renterHomeFragment.setArguments(b);
            setnavigationbar(renterHomeFragment);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId())
                {
                    case R.id.home:
                        b = new Bundle();
                        b.putString("who","Provider");
                        renterHomeFragment.setArguments(b);
                        setnavigationbar(renterHomeFragment);
                        return true;

                    case R.id.requests:
                        b = new Bundle();
                        b.putString("who","Provider");
                        renterRequestFragment.setArguments(b);
                        setnavigationbar(renterRequestFragment);
                        return true;

                    case R.id.accepted_requests:
                        b = new Bundle();
                        b.putString("who","Provider");
                        renterAcceptedFragment.setArguments(b);
                        setnavigationbar(renterAcceptedFragment);
                        return true;

                    case R.id.chats:
                        b = new Bundle();
                        b.putString("who","Provider");
                        chatFragment.setArguments(b);
                        setnavigationbar(chatFragment);
                        return true;

                        default:
                            return false;
                }

            }
        });
    }

    private void setnavigationbar(Fragment fragment)
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.frame,fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.provider_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.action_profile)
        {
            Intent i = new Intent(ProviderHomeActivity.this,ProfileActivity.class);
            i.putExtra("who","Provider");
            startActivity(i);
        }
        if(item.getItemId() == R.id.action_add)
        {
            Intent i = new Intent(ProviderHomeActivity.this,AddEquipmentActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
        if(item.getItemId() == R.id.action_search)
        {

        }
        return super.onOptionsItemSelected(item);
    }

    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
