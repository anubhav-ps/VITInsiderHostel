package com.anubhav.vitinsiderhostel.interfaces;

import com.google.firebase.Timestamp;

public interface iOnOutingHistoryCardClicked {
    
    void outingHistoryCardLongPressed(int pos);

    void outingHistoryViewQRCodeClicked(String code, Timestamp visitDate, String registerNumber);

    void outingHistoryCardPressed(int pos);

}
