package com.martynaroj.traveljournal.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.martynaroj.traveljournal.Services.Models.DataWrapper;
import com.martynaroj.traveljournal.Services.Models.User;
import com.martynaroj.traveljournal.Services.Respositories.SplashRepository;

public class SplashViewModel extends AndroidViewModel {

    private SplashRepository splashRepository;
    private LiveData<DataWrapper<User>> isUserAuthLiveData;
    private LiveData<DataWrapper<User>> userLiveData;

    public SplashViewModel(Application application) {
        super(application);
        splashRepository = new SplashRepository();
    }

    public void checkCurrentUserAuth() {
        isUserAuthLiveData = splashRepository.checkUserIsAuth();
    }

    public void setUid(String uid) {
        userLiveData = splashRepository.getUserFromDatabase(uid);
    }

    public LiveData<DataWrapper<User>> getIsUserAuthLiveData() {
        return isUserAuthLiveData;
    }

    public void setIsUserAuthLiveData(LiveData<DataWrapper<User>> isUserAuthLiveData) {
        this.isUserAuthLiveData = isUserAuthLiveData;
    }

    public LiveData<DataWrapper<User>> getUserLiveData() {
        return userLiveData;
    }

    public void setUserLiveData(LiveData<DataWrapper<User>> userLiveData) {
        this.userLiveData = userLiveData;
    }
}