package com.anubhav.vitinsiderhostel.models;

public enum ErrorCode {
    RA001,
    RA002,
    RA003,
    RA004,
    RA005,
    RA006,
    EPF001,
    EPF002,
    EPF003,
    DF001,
    DF002,
    DF003,
    DF004,
    RF001,
    RF002,
    RF003
}




       /* Error-EPF001
        -> Updating user section failed
        Error-EPF002
        -> Updating tenant section failed
        Error-EPF003
        -> Downloading user and tenant details failed

        Error-RA001
        -> User record not found in hosteler section
        Error-RA002
        -> Account couldn't be created
        Error-RA003
        -> Room structure could not be found
        Error-RA004
        -> Failed to upload user details to user section
        Error-RA005
        -> Failed to upload data to tenant section
        Error-RA006
        -> Failed to send authentication mail

        Error-DF001
        -> couldn't delete user section user data
        Error-DF002
        -> couldn't delete user block record data
        Error-DF003
        -> couldn't delete user account id
        Error-DF004
        -> couldn't delete user data from tenant table
        ERROR - RF001
        -> Error in booking ticket
        ERROR - RF002
        -> Ticket raised couldn't be linked to the specific room
        ERROR - RF003
        -> Ticket couldn't be saved to history
        */