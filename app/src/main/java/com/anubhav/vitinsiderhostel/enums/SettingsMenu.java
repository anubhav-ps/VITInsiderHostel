package com.anubhav.vitinsiderhostel.enums;

public enum SettingsMenu {
    PROFILE("User Profile"),
    PUBLIC_PROFILE("Public Profile"),
    NOTIFICATIONS("Notification"),
    ABOUT("About"),
    REPORT("Report"),
    BUG("Bugs"),
    SUGGESTION("SUGGEST US");

    final String value;

    SettingsMenu(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}
