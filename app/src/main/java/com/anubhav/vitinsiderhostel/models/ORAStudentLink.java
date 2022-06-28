package com.anubhav.vitinsiderhostel.models;

import com.google.firebase.Timestamp;

public class ORAStudentLink {


    private String oraDocId;
    private String status;
    private String issueDocId;
    private Timestamp timestamp;


    public ORAStudentLink() {
    }

    public ORAStudentLink(String oraDocId, String status, String issueDocId, Timestamp timestamp) {
        this.oraDocId = oraDocId;
        this.status = status;
        this.issueDocId = issueDocId;
        this.timestamp = timestamp;
    }

    public ORAStudentLink(String oraDocId, String status, Timestamp timestamp) {
        this.oraDocId = oraDocId;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getOraDocId() {
        return oraDocId;
    }

    public void setOraDocId(String oraDocId) {
        this.oraDocId = oraDocId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIssueDocId() {
        return issueDocId;
    }

    public void setIssueDocId(String issueDocId) {
        this.issueDocId = issueDocId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
