package com.martynaroj.traveljournal.viewmodels;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.martynaroj.traveljournal.services.models.Address;
import com.martynaroj.traveljournal.services.respositories.AddressRepository;

public class AddressViewModel extends AndroidViewModel {

    private AddressRepository addressRepository;
    private MutableLiveData<String> statusLiveData;
    private MutableLiveData<Address> addressLiveData;
    private MutableLiveData<FindCurrentPlaceResponse> detectedAddressLiveData;

    public AddressViewModel(Application application) {
        super(application);
        addressRepository = new AddressRepository();
    }

    public LiveData<String> getStatus() {
        return statusLiveData;
    }

    public void saveAddress(Address address, String reference) {
        statusLiveData = addressRepository.saveAddress(address, reference);
    }

    public LiveData<Address> getAddressData() {
        return addressLiveData;
    }

    public void getAddress(String reference) {
        addressLiveData = addressRepository.getAddress(reference);
    }

    public LiveData<FindCurrentPlaceResponse> getDetectedAddress() {
        return detectedAddressLiveData;
    }

    public void detectAddress(PlacesClient placesClient, FindCurrentPlaceRequest request) {
        detectedAddressLiveData = addressRepository.detectAddress(placesClient, request);
    }
}