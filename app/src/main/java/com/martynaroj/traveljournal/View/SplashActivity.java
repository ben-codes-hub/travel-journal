package com.martynaroj.traveljournal.View;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.martynaroj.traveljournal.Services.Models.User;
import com.martynaroj.traveljournal.ViewModels.SplashViewModel;

import static com.martynaroj.traveljournal.View.Others.Interfaces.Constants.USER;

public class SplashActivity extends AppCompatActivity {

    private SplashViewModel splashViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSplashViewModel();
        checkCurrentUserAuth();
    }


    private void initSplashViewModel() {
        splashViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
    }


    private void checkCurrentUserAuth() {
        splashViewModel.checkCurrentUserAuth();
        splashViewModel.getIsUserAuthLiveData().observe(this, user -> {
            if (user.isAuthenticated()) {
                getUserFromDatabase(user.getData().getUid());
            } else {
                startMainActivity(null);
                finish();
            }
        });
    }


    private void getUserFromDatabase(String uid) {
        splashViewModel.setUid(uid);
        splashViewModel.getUserLiveData().observe(this, user -> {
            startMainActivity(user.getData());
            finish();
        });
    }


    private void startMainActivity(User user) {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra(USER, user);
        startActivity(intent);
    }
}