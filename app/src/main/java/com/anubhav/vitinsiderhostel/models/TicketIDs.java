package com.anubhav.vitinsiderhostel.models;

public class TicketIDs {
    private String ticketId;
    private String ticketHistoryId;

    public TicketIDs() {
    }

    public TicketIDs(String ticketId, String ticketHistoryId) {
        this.ticketId = ticketId;
        this.ticketHistoryId = ticketHistoryId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketHistoryId() {
        return ticketHistoryId;
    }

    public void setTicketHistoryId(String ticketHistoryId) {
        this.ticketHistoryId = ticketHistoryId;
    }

}
