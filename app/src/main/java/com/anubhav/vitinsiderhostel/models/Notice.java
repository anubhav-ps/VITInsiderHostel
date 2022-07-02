package com.anubhav.vitinsiderhostel.models;

import com.anubhav.vitinsiderhostel.enums.Urgency;
import com.google.firebase.Timestamp;

public class Notice {

    private String title;
    private String shortDescription;
    private String brief;
    private String postedBy;
    private Timestamp postedOn;
    private String siteLink;
    private String imageUri;
    private String noticeDocID;
    private String urgency;

    public Notice() {
    }

    public Notice(String title, String shortDescription, String brief, String postedBy, Timestamp postedOn, String siteLink, String imageUri, String noticeDocID, String urgency) {
        this.title = title;
        this.shortDescription = shortDescription;
        this.brief = brief;
        this.postedBy = postedBy;
        this.postedOn = postedOn;
        this.siteLink = siteLink;
        this.imageUri = imageUri;
        this.noticeDocID = noticeDocID;
        this.urgency = urgency;
    }

    public String getTitle() {
        return title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getBrief() {
        return brief;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public Timestamp getPostedOn() {
        return postedOn;
    }

    public String getSiteLink() {
        return siteLink;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getNoticeDocID() {
        return noticeDocID;
    }

    public String getUrgency() {
        return urgency;
    }
}
