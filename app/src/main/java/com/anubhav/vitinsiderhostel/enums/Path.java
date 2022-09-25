package com.anubhav.vitinsiderhostel.enums;

public enum Path {


    FILES("FILES"),

    HOSTEL_STUDENTS("HOSTEL_STUDENTS"),
    ROOM_MATES("ROOM_MATES"),
    ROOM_MATE_DETAILS("ROOM_MATE_DETAILS"),
    ROOM_DETAILS("ROOM_DETAILS"),

    ACCOUNTS("ACCOUNTS"),
    ACCOUNT_Q("ACCOUNT_Q"),
    STUDENTS("STUDENTS"),

    FEEDBACKS("FEEDBACKS"),
    ISSUES("ISSUES"),
    SUGGESTIONS("SUGGESTIONS"),
    BUGS("BUGS"),

    NOTICE("NOTICE"),

    OUTING_BASE("OUTING_BASE"),
    CLIENT_OUTING_Q("LEVEL_1"),   //Security -> allow create,read , deny update and delete  // done
    OUTING_FORM("LEVEL_2"),       //Security -> allow read , deny write                     // done
    OUTING_RECORDS("LEVEL_3"),    //Security -> allow read , deny write                     // done
    PROCTOR_AUTH_CODE("LEVEL_4"), //Security -> deny read and write                         // done
    PARENT_AUTH_CODE("LEVEL_5"),  //Security -> deny read and write                         // done
    VERIFY_PARENT_AUTH("LEVEL_6"), //Security -> allow create , deny read , update , delete  // done
    FORM_AUTH_CODE("LEVEL_7"),
    CHIEF_STATUS_Q("LEVEL_8"),

    PUBLIC_USERS("PUBLIC_USERS"),


    FCM_TOKEN("FCM_TOKEN");


    final String path;

    Path(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
