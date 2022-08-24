package com.anubhav.vitinsiderhostel.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.activities.RegisterActivity;
import com.anubhav.vitinsiderhostel.adapters.OutingHistoryRecyclerAdapter;
import com.anubhav.vitinsiderhostel.interfaces.iOnDopClicked;
import com.anubhav.vitinsiderhostel.interfaces.iOnOutingHistoryCardClicked;
import com.anubhav.vitinsiderhostel.interfaces.iOnOutingHistoryDownloaded;
import com.anubhav.vitinsiderhostel.models.AlertDisplay;
import com.anubhav.vitinsiderhostel.models.LinkEnds;
import com.anubhav.vitinsiderhostel.models.ORApp;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class OutingHistoryFragment extends Fragment implements iOnOutingHistoryDownloaded, iOnOutingHistoryCardClicked {


    private iOnOutingHistoryDownloaded onOutingHistoryDownloaded;
    private LinkEnds linkEnds = new LinkEnds();
    private List<String> links;
    private List<ORApp> orApps;
    private String studentMailId;
    private String studentBlock;
    private LinearLayout emptyHistory;
    private LinearLayout loadingLinearLayout;
    private ProgressBar loading;
    private RecyclerView recyclerView;
    private OutingHistoryRecyclerAdapter adapter;
    private String onSearchDocId;
    private Dialog dialog;
    private iOnDopClicked onDopClicked;

    public OutingHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_outing_history, container, false);

        onOutingHistoryDownloaded = this;
        orApps = new ArrayList<>();
        dialog = new Dialog(getContext());

        if (User.getInstance() != null) {
            studentMailId = User.getInstance().getUserMailID();
            studentBlock = User.getInstance().getStudentBlock();
        }

        emptyHistory = view.findViewById(R.id.outingHistoryEmptyLinearLayout);
        loadingLinearLayout = view.findViewById(R.id.outingHistoryLoadingLinearLayout);
        loading = view.findViewById(R.id.outingHistoryProgressBar);
        recyclerView = view.findViewById(R.id.outingHistoryRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView = view.findViewById(R.id.outingHistoryRecyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);

        retrieveAllStudentLink();

        return view;
    }

    private void retrieveAllStudentLink() {
        links = new ArrayList<>();
        linkEnds.readStudentLinkId(studentMailId)
                .get().addOnCompleteListener(task -> {
            boolean isPresent = false;
            if (task.isSuccessful()) {
                final int size = task.getResult().size();
                if (size == 0) {
                    loading.setVisibility(View.GONE);
                    loadingLinearLayout.setVisibility(View.GONE);
                    emptyHistory.setVisibility(View.VISIBLE);
                } else {
                    isPresent = true;
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        links.add(documentSnapshot.getId());
                    }
                }
            } else {
                //todo report couldn't read
                loading.setVisibility(View.GONE);
                loadingLinearLayout.setVisibility(View.GONE);
                emptyHistory.setVisibility(View.VISIBLE);
            }
            onOutingHistoryDownloaded.studentLinksFetched(isPresent);
        });
    }

    @Override
    public void studentLinksFetched(boolean flag) {
        if (flag == true) {
            for (String id : links) {
                String[] splitValues = id.split("\\|");
                DocumentReference oraDocRef = linkEnds.readOREKDocs(splitValues);
                oraDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                ORApp doc = documentSnapshot.toObject(ORApp.class);
                                assert doc != null;
                                orApps.add(doc);
                            } else {
                                //todo no such document exists
                                //todo report couldn't read
                                loading.setVisibility(View.GONE);
                                loadingLinearLayout.setVisibility(View.GONE);
                                emptyHistory.setVisibility(View.VISIBLE);
                            }
                        } else {
                            //todo failed to fetch
                            //todo report couldn't read
                            loading.setVisibility(View.GONE);
                            loadingLinearLayout.setVisibility(View.GONE);
                        }
                        onOutingHistoryDownloaded.orAppsFetched();
                    }
                });
            }
        } else {

        }
    }

    @Override
    public void orAppsFetched() {
        orApps.sort((o1, o2) -> Long.compare(o2.getUploadTimestamp().getSeconds(), o1.getUploadTimestamp().getSeconds()));
        loading.setVisibility(View.GONE);
        loadingLinearLayout.setVisibility(View.GONE);
        processRecyclerAdapter();
    }

    @Override
    public void oRekFetchedForDeletion(int pos) {
        final String[] splitValue = onSearchDocId.split("\\|");
        DocumentReference orekDocRef = linkEnds.readOREKDocs(splitValue);
        orekDocRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "OREK Deleted Successfully", Toast.LENGTH_LONG).show();
                    linkEnds.getStudentOREKLink(studentMailId).document(onSearchDocId).delete();
                    orApps.remove(pos);
                    processRecyclerAdapter();
                } else {
                    Toast.makeText(getContext(), "OREK couldn't be deleted", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void processRecyclerAdapter() {
        adapter = new OutingHistoryRecyclerAdapter(orApps, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void outingHistoryCardClicked(int pos) {
        dialog.setContentView(R.layout.dialog_view_orek);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        MaterialTextView visitDate = dialog.findViewById(R.id.dialogViewOrekVisitDate);
        MaterialTextView visitPurpose = dialog.findViewById(R.id.dialogViewOrekVisitPurpose);
        MaterialTextView studentName = dialog.findViewById(R.id.dialogViewOrekStudentName);
        MaterialTextView studentMailId = dialog.findViewById(R.id.dialogViewOrekStudentMailId);
        MaterialTextView studentRoomDetail = dialog.findViewById(R.id.dialogViewOrekStudentRoomDetail);
        MaterialTextView studentContactNumber = dialog.findViewById(R.id.dialogViewOrekStudentNumber);
        MaterialTextView parentContactNumber = dialog.findViewById(R.id.dialogViewOrekParentNumber);

        visitDate.setText(orApps.get(pos).getVisitDate());
        visitPurpose.setText(orApps.get(pos).getVisitPurpose().trim());
        studentName.setText(orApps.get(pos).getStudentName());
        studentMailId.setText(orApps.get(pos).getStudentMailId());
        studentRoomDetail.setText(orApps.get(pos).getStudentRoomDetails());
        studentContactNumber.setText(orApps.get(pos).getStudentContactNumber());
        parentContactNumber.setText(orApps.get(pos).getParentNumber());

        dialog.show();

    }

    @Override
    public void outingHistoryCardLongPressed(int pos) {

        AlertDisplay alertDisplay = new AlertDisplay("Delete OREK", "Are you sure you want to delete this OREK ?", getContext());
        alertDisplay.getBuilder().setPositiveButton("Cancel",null);
        alertDisplay.getBuilder().setNegativeButton("Delete",(dialogInterface, i) -> deleteOREK(pos));
        alertDisplay.display();

    }

    private void deleteOREK(int pos) {
        final String docId = orApps.get(pos).getOraDocId();
        CollectionReference studentOREKLinkColRef = linkEnds.getStudentOREKLink(studentMailId);
        studentOREKLinkColRef.whereEqualTo("oraDocId", docId).limit(1)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        onSearchDocId = documentSnapshot.getId();
                    }
                } else {
                    //todo error getting documents
                }
                onOutingHistoryDownloaded.oRekFetchedForDeletion(pos);
            }
        });
    }

    @Override
    public void outingHistoryViewDopClicked(String docId, int pos) {
        final String date = orApps.get(pos).getVisitDate();
        final String[] splitValue = date.split("-");
        onDopClicked.viewDopClicked(studentBlock, splitValue[2], splitValue[1], splitValue[0], docId);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
            this.onDopClicked = (iOnDopClicked) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "is not implementing on iOnDopClicked");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (this.onDopClicked != null) {
            this.onDopClicked = null;
        }

    }
}