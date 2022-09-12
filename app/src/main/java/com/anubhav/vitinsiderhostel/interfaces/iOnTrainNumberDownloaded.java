package com.anubhav.vitinsiderhostel.interfaces;

import com.anubhav.vitinsiderhostel.models.PNR_Result;
import com.anubhav.vitinsiderhostel.models.TravelNetworkList;
import com.google.android.material.internal.ViewOverlayImpl;

public interface iOnTrainNumberDownloaded {
    void hideAllViews();
    void showViews(PNR_Result result);
    void checkForTrainNoExistence(String trainNo,String date);
    void downloadPublicProfile(TravelNetworkList travelNetworkList);
    void publicProfileDownloaded();
}
