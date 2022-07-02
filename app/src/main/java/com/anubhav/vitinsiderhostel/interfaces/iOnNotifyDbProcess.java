package com.anubhav.vitinsiderhostel.interfaces;

import com.anubhav.vitinsiderhostel.enums.NotifyStatus;
import com.anubhav.vitinsiderhostel.models.Tenant;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface iOnNotifyDbProcess {
    void onNotifyCompleteListDownload() throws NoSuchAlgorithmException;
    void onNotifyCompleteDataDownload(NotifyStatus notifyStatus);
}
