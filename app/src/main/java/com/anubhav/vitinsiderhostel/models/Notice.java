package com.anubhav.vitinsiderhostel.models;

import com.google.firebase.Timestamp;

public class Notice {

    private String title;
    private String body;
    private String postedBy;
    private Timestamp postedOn;
    private String imageUri;
    private String siteLink;
    private String noticeDocID;
    private String urgency;

    public Notice() {
    }

    public Notice(String noticeDocID) {
        this.noticeDocID = noticeDocID;
    }

    public Notice(String title, String body, String postedBy, Timestamp postedOn, String siteLink, String imageUri, String noticeDocID, String urgency) {
        this.title = title;
        this.body = body;
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

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public Timestamp getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(Timestamp postedOn) {
        this.postedOn = postedOn;
    }

    public String getSiteLink() {
        return siteLink;
    }

    public void setSiteLink(String siteLink) {
        this.siteLink = siteLink;
    }


    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getNoticeDocID() {
        return noticeDocID;
    }

    public void setNoticeDocID(String noticeDocID) {
        this.noticeDocID = noticeDocID;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }
}
