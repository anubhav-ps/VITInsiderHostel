package com.anubhav.vitinsiderhostel.models;

import com.anubhav.vitinsiderhostel.enums.Urgency;

public class Notice {

    private String title;
    private String shortDescription;
    private String brief;
    private String postedBy;
    private String postedOn;
    private String siteLink;
    private int autoClose;
    private String docID;
    private String urgency;

    public Notice() {
    }

    public Notice(String title, String shortDescription, String brief, String postedBy, String postedOn, String siteLink, int autoClose, String docID, String urgency) {
        this.title = title;
        this.shortDescription = shortDescription;
        this.brief = brief;
        this.postedBy = postedBy;
        this.postedOn = postedOn;
        this.siteLink = siteLink;
        this.autoClose = autoClose;
        this.docID = docID;
        this.urgency = urgency;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getBrief() {
        return brief;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(String postedOn) {
        this.postedOn = postedOn;
    }

    public String getSiteLink() {
        return siteLink;
    }

    public void setSiteLink(String siteLink) {
        this.siteLink = siteLink;
    }

    public int getAutoClose() {
        return autoClose;
    }

    public void setAutoClose(int autoClose) {
        this.autoClose = autoClose;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }
}
