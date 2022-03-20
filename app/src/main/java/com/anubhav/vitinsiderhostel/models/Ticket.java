package com.anubhav.vitinsiderhostel.models;

import com.anubhav.vitinsiderhostel.enums.TicketStatus;
import com.google.firebase.Timestamp;

public class Ticket {

    private String docId;
    private String roomNo;
    private String block;
    private String serviceName;
    private String serviceDescription;
    private String uploaderMailId;
    private String status;
    private Timestamp itemTimeStamp;

    public Ticket() {
    }

    public Ticket(String docId, String roomNo, String block, String serviceName, String serviceDescription, String uploaderMailId, TicketStatus status, Timestamp itemTimeStamp) {
        this.docId = docId;
        this.roomNo = roomNo;
        this.block = block;
        this.serviceName = serviceName;
        this.serviceDescription = serviceDescription;

        this.uploaderMailId = uploaderMailId;
        this.status = status.toString();
        this.itemTimeStamp = itemTimeStamp;


    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(String roomNo) {
        this.roomNo = roomNo;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getUploaderMailId() {
        return uploaderMailId;
    }

    public void setUploaderMailId(String uploaderMailId) {
        this.uploaderMailId = uploaderMailId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getItemTimeStamp() {
        return itemTimeStamp;
    }

    public void setItemTimeStamp(Timestamp itemTimeStamp) {
        this.itemTimeStamp = itemTimeStamp;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }
}
