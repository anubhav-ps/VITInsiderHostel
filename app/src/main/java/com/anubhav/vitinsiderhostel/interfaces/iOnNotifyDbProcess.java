package com.anubhav.vitinsiderhostel.interfaces;

import com.anubhav.vitinsiderhostel.enums.NotifyStatus;

public interface iOnNotifyDbProcess {
    void onNotifyCompleteListDownload(NotifyStatus notifyStatus);
    void onNotifyCompleteDataDownload(NotifyStatus notifyStatus);
}
