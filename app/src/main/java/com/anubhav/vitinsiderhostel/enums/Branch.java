package com.anubhav.vitinsiderhostel.enums;

import androidx.annotation.NonNull;

public enum Branch {


    BEE("Electrical & Electronics"),
    BEC("Electronics & Communication"),
    BCE("Computer Science Core"),
    BLC("Electronics & Computer"),
    BCL("Civil"),
    BME("Mechanical Core"),
    BPS("Cyber Physical System"),
    BAI("Computer Science AI-ML"),
    BMH("Mechatronics & Automation"),
    MIS("Integrated Software"),
    BRS("AI Robotics");


    private final String value;

    public static String getValue(String value){
        if (value.equalsIgnoreCase("BEE")){
            return BEE.toString();
        }else if (value.equalsIgnoreCase("BEC")){
            return BEC.toString();
        }else if (value.equalsIgnoreCase("BCE")){
            return BCE.toString();
        }else if (value.equalsIgnoreCase("BLC")){
            return BLC.toString();
        }else if (value.equalsIgnoreCase("BCL")){
            return BCL.toString();
        }else if (value.equalsIgnoreCase("BME")){
            return BME.toString();
        }else if (value.equalsIgnoreCase("BPS")){
            return BPS.toString();
        }else if (value.equalsIgnoreCase("BAI")){
            return BAI.toString();
        }else if (value.equalsIgnoreCase("BMH")){
            return BMH.toString();
        }else if (value.equalsIgnoreCase("MIS")){
            return MIS.toString();
        }else if (value.equalsIgnoreCase("BRS")){
            return BRS.toString();
        }
        return "null";
    }


    Branch(String value){
        this.value = value;
    }

    @NonNull
    @Override
    public String toString() {
        return this.value;
    }
}
