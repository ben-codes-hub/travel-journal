package com.martynaroj.traveljournal.services.models.weatherAPI;

import android.widget.ImageView;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.google.gson.annotations.SerializedName;
import com.martynaroj.traveljournal.BR;

import java.io.Serializable;

public class Weather extends BaseObservable implements Serializable {

    @SerializedName("id")
    public Integer id;

    @SerializedName("main")
    public String main;

    @SerializedName("description")
    public String description;

    @SerializedName("icon")
    public String icon;


    @Bindable
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
        notifyPropertyChanged(BR.main);
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    @Bindable
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
        notifyPropertyChanged(BR.icon);
    }

    @BindingAdapter("iconUrl")
    public static void loadIcon(ImageView v, String imgUrl) {
        Glide.with(v.getContext())
                .load("http://openweathermap.org/img/w/" + imgUrl + ".png")
                //.placeholder(R.drawable.default_avatar)
                .into(v);
    }
}