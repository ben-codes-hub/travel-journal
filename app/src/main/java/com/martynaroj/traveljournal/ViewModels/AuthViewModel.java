package com.martynaroj.traveljournal.ViewModels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.google.firebase.auth.AuthCredential;
import com.martynaroj.traveljournal.Services.Models.DataWrapper;
import com.martynaroj.traveljournal.Services.Models.User;
import com.martynaroj.traveljournal.Services.Respositories.AuthRepository;

public class AuthViewModel extends AndroidViewModel {

    private AuthRepository authRepository;
    private LiveData<DataWrapper<User>> userLiveData;

    public AuthViewModel(Application application) {
        super(application);
        authRepository = new AuthRepository();
    }

    public void signInWithGoogle(AuthCredential googleAuthCredential) {
        userLiveData = authRepository.firebaseSignInWithGoogle(googleAuthCredential);
    }

    public LiveData<DataWrapper<User>> getUserLiveData() {
        return userLiveData;
    }
}
