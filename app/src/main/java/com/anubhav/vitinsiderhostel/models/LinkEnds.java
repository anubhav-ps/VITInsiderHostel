package com.anubhav.vitinsiderhostel.models;

import com.anubhav.vitinsiderhostel.enums.ORAStatus;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LinkEnds {

    private final FirebaseFirestore firebaseFirestore;
    private final String BE7825AM04CE = "IYR864A2S3";  // FOR Outing
    private final String NH4793YN275D = "YAWE6123C9M2";  // FOR Users

    private final String HR485BRW359L = "i4ZTXJbRjhRBKqlzWR8M";  // For Issue 1
    private final String JKEB123BFN5H = "UQXEUX2hT0NBRLx2jAWD";  // For Issue 2

    private final String GER693LH3FNR = "7F8AVIutZHMkA4dVhiI1";  // For Req 1
    private final String MRT46920TL358 = "2fdwWGQRuI4rv3LowjI1";  // For Req 2


    public LinkEnds() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public CollectionReference insertOREK(String b, String y, String m, String d) {
        String block = "X";
        if (b.equalsIgnoreCase("A")) {
            block = "E5696";
        } else if (b.equalsIgnoreCase("B")) {
            block = "J6697";
        } else if (b.equalsIgnoreCase("C")) {
            block = "G8698";
        }
        return this.firebaseFirestore.collection(BE7825AM04CE).document(block).collection(y).document(m).collection(d).document(GER693LH3FNR).collection(MRT46920TL358);
    }

    public DocumentReference insertLinkStudentOREK(String mailId, String b, String y, String m, String d, String reqDocId) {

        String block = "X";
        if (b.equalsIgnoreCase("A")) {
            block = "E5696";
        } else if (b.equalsIgnoreCase("B")) {
            block = "J6697";
        } else if (b.equalsIgnoreCase("C")) {
            block = "G8698";
        }

        final String studentLinkId = block + "|" + y + "|" + m + "|" + d + "|" + reqDocId;

        return this.firebaseFirestore.collection(BE7825AM04CE).document(NH4793YN275D).collection(mailId).document(studentLinkId);

    }

    public ORAStudentLink getStudentLinkId(String reqDocId, Timestamp timestamp) {

        // final String linkId = block + "|" + y + "|" + m + "|" + d + "|" + reqDocId;
        return new ORAStudentLink(reqDocId, ORAStatus.APPLIED.toString(), timestamp);

    }

    public CollectionReference readStudentLinkId(String mailId) {

        return this.firebaseFirestore.collection(BE7825AM04CE).document(NH4793YN275D).collection(mailId);

    }

    public DocumentReference readOREKDocs(String[] values) {
        return this.firebaseFirestore.collection(BE7825AM04CE).document(values[0]).collection(values[1]).document(values[2]).collection(values[3]).document(GER693LH3FNR).collection(MRT46920TL358).document(values[4]);
    }


    public CollectionReference getStudentOREKLink(String mailId) {

        return this.firebaseFirestore.collection(BE7825AM04CE).document(NH4793YN275D).collection(mailId);

    }

    public CollectionReference getIssuedDoc(String b, String y, String m, String d) {
        String block = "X";
        if (b.equalsIgnoreCase("A")) {
            block = "E5696";
        } else if (b.equalsIgnoreCase("B")) {
            block = "J6697";
        } else if (b.equalsIgnoreCase("C")) {
            block = "G8698";
        }
        return this.firebaseFirestore.collection(BE7825AM04CE).document(block).collection(y).document(m).collection(d).document(HR485BRW359L).collection(JKEB123BFN5H);

    }
}
