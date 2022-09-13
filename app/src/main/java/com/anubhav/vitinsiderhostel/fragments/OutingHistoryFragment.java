package com.anubhav.vitinsiderhostel.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.adapters.OutingHistoryRecyclerAdapter;
import com.anubhav.vitinsiderhostel.enums.Mod;
import com.anubhav.vitinsiderhostel.interfaces.iOnOutingFormDownloaded;
import com.anubhav.vitinsiderhostel.interfaces.iOnOutingHistoryCardClicked;
import com.anubhav.vitinsiderhostel.models.AlertDisplay;
import com.anubhav.vitinsiderhostel.models.OutingForm;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class OutingHistoryFragment extends Fragment implements iOnOutingHistoryCardClicked, iOnOutingFormDownloaded {


    //firebase fire-store declaration
    private final FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private final CollectionReference outingFormSection = dB.collection(Mod.OUSEC.toString());
    private final ArrayList<OutingForm> outingForms = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout linearLayoutLoading;
    private LinearLayout linearLayoutEmpty;
    private OutingHistoryRecyclerAdapter outingHistoryRecyclerAdapter;
    private View rootView;
    private Dialog dialog;

    private iOnOutingFormDownloaded onOutingFormDownloaded;

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
        rootView = inflater.inflate(R.layout.fragment_outing_history, container, false);

        progressBar = rootView.findViewById(R.id.outingHistoryProgressBar);
        linearLayoutLoading = rootView.findViewById(R.id.outingHistoryLoadingLinearLayout);
        linearLayoutEmpty = rootView.findViewById(R.id.outingHistoryEmptyLinearLayout);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        dialog = new Dialog(getContext());

        recyclerView = rootView.findViewById(R.id.outingHistoryRecyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);


        linearLayoutLoading.setVisibility(View.VISIBLE);
        linearLayoutEmpty.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        onOutingFormDownloaded = this;

        downloadOutingForms();

        return rootView;
    }

    private void downloadOutingForms() {
        outingForms.clear();
        outingFormSection
                .document(Mod.OFORM.toString())
                .collection(Mod.DET.toString())
                .whereEqualTo("studentMailId", User.getInstance().getUserMailID())
                .get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                callSnackBar(Objects.requireNonNull(task.getException()).getMessage());
                linearLayoutLoading.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                return;
            }
            if (task.getResult().isEmpty()) {
                linearLayoutEmpty.setVisibility(View.VISIBLE);
                linearLayoutLoading.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                return;
            }
            linearLayoutLoading.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                OutingForm outingForm = documentSnapshot.toObject(OutingForm.class);
                outingForms.add(outingForm);
                System.out.println("Ticket number : " + outingForm.getFormId());
                onOutingFormDownloaded.outingFormDownloaded();
            }
        }).addOnFailureListener(e -> {
            linearLayoutLoading.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }


    private void processRecyclerAdapter() {
        outingHistoryRecyclerAdapter = new OutingHistoryRecyclerAdapter(outingForms, this);
        recyclerView.setAdapter(outingHistoryRecyclerAdapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void outingHistoryCardLongPressed(int pos) {
        OutingForm outingForm = outingForms.get(pos);

        if (outingForm.getStatus().equalsIgnoreCase("APPROVED")) {
                AlertDisplay alertDisplay = new AlertDisplay("Cannot Delete", "Outing application with approved status cannot be deleted.", getContext());
                alertDisplay.getBuilder().setPositiveButton("Ok", null);
                alertDisplay.display();
                return;
        }
        AlertDisplay alertDisplay = new AlertDisplay("Delete Outing Form ? ", "Are you sure you want to delete this outing form ? ", getContext());
        alertDisplay.getBuilder().setPositiveButton("Cancel", null);
        alertDisplay.getBuilder().setNegativeButton("Yes", (dialog, which) -> outingFormSection.document(Mod.OFORM.toString()).collection(Mod.DET.toString()).document(outingForm.getFormId()).delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                outingForms.remove(pos);
                if (outingForms.isEmpty()) {
                    linearLayoutEmpty.setVisibility(View.VISIBLE);
                } else {
                    processRecyclerAdapter();
                }
            }
        }));
        alertDisplay.display();
    }

    @Override
    public void outingHistoryViewQRCodeClicked(String code, String visitDate, String registerNumber) {
        if (code != null) {
            dialog.setContentView(R.layout.dialog_qr_code);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            MaterialTextView registerNumberTxt = dialog.findViewById(R.id.dialogQRRegisterNumberTxt);
            MaterialTextView visitDateTxt = dialog.findViewById(R.id.dialogQRVisitDateTxt);
            ImageView imageView = dialog.findViewById(R.id.dialogQRCodeImg);

            registerNumberTxt.setText(registerNumber);
            SimpleDateFormat formatToString = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            visitDateTxt.setText(formatToString.format(convertDateString(visitDate)));

            MultiFormatWriter writer = new MultiFormatWriter();
            final String verifyLink = "https://us-central1-vitinsiderhostel.cloudfunctions.net/insider_hostel/verifyouting/" + code;
            try {
                BitMatrix matrix = writer.encode(verifyLink, BarcodeFormat.QR_CODE, 500, 500);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(matrix);
                imageView.setImageBitmap(bitmap);
                dialog.show();
            } catch (WriterException exception) {
                dialog.dismiss();
                Toast.makeText(getContext(), "There was an error generating QR Code", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void outingFormDownloaded() {
        outingForms.sort((o1, o2) -> Long.compare(o2.getTimestamp().getSeconds(), o1.getTimestamp().getSeconds()));
        processRecyclerAdapter();
    }

   /* private boolean getVisitDate(String visitDate) {
        Date visitDay = convertDateString(visitDate);
        Date today = new Date();
        return !(today.compareTo(visitDay) < 0 || today.compareTo(visitDay) == 0);
    }*/

    private Date convertDateString(String stringDate) {
        SimpleDateFormat formatToString = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = null;
        try {
            date = formatToString.parse(stringDate);
        } catch (ParseException parseException) {
            parseException.printStackTrace();
        }
        return date;
    }


    // snack bar method
    private void callSnackBar(String message) {
        Snackbar snackbar = Snackbar
                .make(requireContext(), rootView.findViewById(R.id.outingHistoryFragment), message, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.WHITE);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.navy_blue));
        snackbar.show();
    }
}