package com.anubhav.vitinsiderhostel.interfaces;

import com.anubhav.vitinsiderhostel.models.AppError;

public interface iOnAppErrorCreated {
    void checkIfAlreadyReported(AppError appError,String message);
    void getQueryResult(AppError appError,String message,boolean flag);
    void IssueReported(String message);

}
