package com.anubhav.vitinsiderhostel.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.adapters.TravelCompanionAdapter;
import com.anubhav.vitinsiderhostel.enums.Mod;
import com.anubhav.vitinsiderhostel.interfaces.iOnTrainNumberDownloaded;
import com.anubhav.vitinsiderhostel.models.AlertDisplay;
import com.anubhav.vitinsiderhostel.models.IRCTC;
import com.anubhav.vitinsiderhostel.models.PNR_Result;
import com.anubhav.vitinsiderhostel.models.PublicProfile;
import com.anubhav.vitinsiderhostel.models.TravelNetworkList;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TravelCompanionHomeFragment extends Fragment implements View.OnClickListener, iOnTrainNumberDownloaded {


    //firebase fireStore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference travelSection = db.collection(Mod.TRAVEL.toString());
    private final CollectionReference publicSection = db.collection(Mod.PUBL.toString());

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private final List<PublicProfile> allCompanions = new ArrayList<>();
    private View rootView;
    private RadioGroup chooseTravelMode;
    private RadioButton trainBtn, flightBtn;
    private TextInputEditText trainPnrET;
    private MaterialButton searchBtn, joinNetworkBtn, createNJoinNetworkBtn, exitNetworkBtn;
    private int travelMode = 0;  // 0 - train , 1 - flight
    private MaterialTextView trainNameTitleTxt, trainNameTxt;







    private ProgressBar progressBar;
    private PNR_Result PNR_RESULT;
    private RecyclerView recyclerView;
    private TravelCompanionAdapter travelCompanionAdapter;
    private iOnTrainNumberDownloaded onTrainNumberDownloaded;


    public TravelCompanionHomeFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_travel_companion_home, container, false);

        chooseTravelMode = rootView.findViewById(R.id.travelPgeRadioGrp);
        trainBtn = rootView.findViewById(R.id.travelPgeTrainRadioBtn);
        flightBtn = rootView.findViewById(R.id.travelPgeFlightRadioBtn);

        trainPnrET = rootView.findViewById(R.id.travelPgeTrainPnrNumberEt);

        searchBtn = rootView.findViewById(R.id.travelPgeSearchBtn);
        joinNetworkBtn = rootView.findViewById(R.id.travelPgeJoinNetworkBtn);
        createNJoinNetworkBtn = rootView.findViewById(R.id.travelPgeCreateNJoinNetworkBtn);
        exitNetworkBtn = rootView.findViewById(R.id.travelPgeExitNetworkBtn);

        trainNameTitleTxt = rootView.findViewById(R.id.travelPgeTrainNameTitleTxt);
        trainNameTxt = rootView.findViewById(R.id.travelPgeTrainNameTxt);
        progressBar = rootView.findViewById(R.id.travelPgeProgressBar);

        recyclerView = rootView.findViewById(R.id.travelPgeRecyclerView);

        onTrainNumberDownloaded = this;

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        exitNetworkBtn.setVisibility(View.GONE);
        joinNetworkBtn.setVisibility(View.GONE);
        createNJoinNetworkBtn.setVisibility(View.GONE);
        trainNameTitleTxt.setVisibility(View.GONE);
        trainNameTxt.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        trainBtn.setChecked(true);
        travelMode = 0;

        chooseTravelMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.travelPgeTrainRadioBtn) {
                travelMode = 0;
            } else if (checkedId == R.id.travelPgeFlightRadioBtn) {
                travelMode = 1;
            }
        });

        searchBtn.setOnClickListener(this);
        joinNetworkBtn.setOnClickListener(this);
        createNJoinNetworkBtn.setOnClickListener(this);
        exitNetworkBtn.setOnClickListener(this);


        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.travelPgeSearchBtn) {
            processSearch(travelMode);
        } else if (id == R.id.travelPgeCreateNJoinNetworkBtn) {
            if (!User.getInstance().isHasPublicProfile()) {
                AlertDisplay alertDisplay = new AlertDisplay("Enable Public Profile", "To Join the network enable public profile.Open Account Page -> Public Profile.", getContext());
                alertDisplay.displayAlert();
                return;
            }
            createNJoinNetwork(PNR_RESULT.getData().getTrain_number(), PNR_RESULT.getData().getJourney_date());
        } else if (id == R.id.travelPgeJoinNetworkBtn) {
            if (!User.getInstance().isHasPublicProfile()) {
                AlertDisplay alertDisplay = new AlertDisplay("Enable Public Profile", "To Join the network enable public profile.Open Account Page -> Public Profile.", getContext());
                alertDisplay.displayAlert();
                return;
            }
            joinNetwork(PNR_RESULT.getData().getTrain_number(), PNR_RESULT.getData().getJourney_date());
        } else if (id == R.id.travelPgeExitNetworkBtn) {
            exitNetwork(PNR_RESULT.getData().getTrain_number(), PNR_RESULT.getData().getJourney_date());
        }
    }


    private void processRecyclerAdapter() {
        travelCompanionAdapter = new TravelCompanionAdapter(allCompanions, getContext());
        recyclerView.setAdapter(travelCompanionAdapter);
    }

    private void processSearch(int travelMode) {
        exitNetworkBtn.setVisibility(View.GONE);
        joinNetworkBtn.setVisibility(View.GONE);
        createNJoinNetworkBtn.setVisibility(View.GONE);
        if (travelMode == 0) {

            String pnrInput = Objects.requireNonNull(trainPnrET.getText()).toString().trim();

            if (TextUtils.isEmpty(pnrInput)) {
                trainPnrET.setError("Enter the PNR Number");
                trainPnrET.requestFocus();
                return;
            }

            final String pattern = "[0-9]{10}";
            Pattern p = Pattern.compile(pattern);
            Matcher m = p.matcher(pnrInput);

            if (!m.matches()) {
                trainPnrET.setError("Invalid PNR Format");
                trainPnrET.requestFocus();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            TravelNetworkList travelNetworkList = new TravelNetworkList(new ArrayList<>(Arrays.asList("anubhav.purusottam2019@vitstudent.ac.in", "gowtham.bh2021@vitstudent.ac.in", "harsh.pandey2021f@vitstudent.ac.in")));
            onTrainNumberDownloaded.downloadPublicProfile(travelNetworkList);
            progressBar.setVisibility(View.GONE);

            //todo hard-edit
            //downloadPNRStatus(pnrInput);
        }


    }

    private void downloadPNRStatus(String pnrNum) {
        Request request = new Request.Builder()
                .url(IRCTC.API_SITE + pnrNum)
                .addHeader("X-RapidAPI-Key", IRCTC.API_KEY)
                .addHeader("X-RapidAPI-Host", IRCTC.API_HOST)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                requireActivity().runOnUiThread(() -> {
                    onTrainNumberDownloaded.hideAllViews();
                });
                callSnackBar("Network Failure,Try again later");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                if (!response.isSuccessful()) {
                    requireActivity().runOnUiThread(() -> {
                        onTrainNumberDownloaded.hideAllViews();
                    });
                    callSnackBar("Invalid PNR Number");
                } else {
                    PNR_Result pnr_result = gson.fromJson(response.body().charStream(), PNR_Result.class);
                    if (!pnr_result.getStatus()) {
                        requireActivity().runOnUiThread(() -> {
                            onTrainNumberDownloaded.hideAllViews();
                        });
                        callSnackBar("The PNR number entered is either dead or not yet generated");
                    } else {
                        requireActivity().runOnUiThread(() -> {
                            PNR_RESULT = pnr_result;
                            showViews(pnr_result);
                            onTrainNumberDownloaded.checkForTrainNoExistence(pnr_result.getData().getTrain_number(), pnr_result.getData().getJourney_date());
                        });
                    }

                }

            }

        });
    }

    // snack bar method
    private void callSnackBar(String message) {
        Snackbar snackbar = Snackbar
                .make(requireContext(), rootView.findViewById(R.id.travelCompanionFragment), message, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.WHITE);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.navy_blue));
        snackbar.show();
    }

    @Override
    public void hideAllViews() {
        progressBar.setVisibility(View.GONE);
        trainNameTitleTxt.setVisibility(View.GONE);
        trainNameTxt.setVisibility(View.GONE);
    }

    @Override
    public void showViews(PNR_Result result) {
        progressBar.setVisibility(View.GONE);
        trainNameTitleTxt.setVisibility(View.VISIBLE);
        trainNameTxt.setText(result.getData().getTrain_name());
        trainNameTxt.setVisibility(View.VISIBLE);
    }

    @Override
    public void checkForTrainNoExistence(String trainNo, String date) {
        String newDate = date.replace("/", "");
        travelSection
                .document(Mod.TRAIN.toString())
                .collection(newDate).document(trainNo).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                callSnackBar(Objects.requireNonNull(task.getException()).getMessage());
            }
            if (task.getResult().exists()) {
                //companions are there
                createNJoinNetworkBtn.setVisibility(View.GONE);
                TravelNetworkList travelNetworkList = task.getResult().toObject(TravelNetworkList.class);
                if (travelNetworkList != null)
                    if (travelNetworkList.getList().contains(User.getInstance().getUserMailID())) {
                        joinNetworkBtn.setVisibility(View.GONE);
                        exitNetworkBtn.setVisibility(View.VISIBLE);
                    } else {
                        exitNetworkBtn.setVisibility(View.GONE);
                        joinNetworkBtn.setVisibility(View.VISIBLE);
                    }
                //todo display recycler view
                onTrainNumberDownloaded.downloadPublicProfile(travelNetworkList);
            } else {
                //todo no companions yet
                //display panda
                joinNetworkBtn.setVisibility(View.GONE);
                createNJoinNetworkBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void downloadPublicProfile(TravelNetworkList travelNetworkList) {
        allCompanions.clear();
        progressBar.setVisibility(View.VISIBLE);
        final String userMailID = User.getInstance().getUserMailID();
        for (String mailId : travelNetworkList.getList()) {
            if (mailId.equalsIgnoreCase(userMailID)) {
                continue;
            }
            publicSection.document(mailId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    PublicProfile publicProfile = task.getResult().toObject(PublicProfile.class);
                    allCompanions.add(publicProfile);
                }
                onTrainNumberDownloaded.publicProfileDownloaded();
            });
        }
    }

    @Override
    public void publicProfileDownloaded() {
        if (!allCompanions.isEmpty()) {
            processRecyclerAdapter();
        }
    }


    private void joinNetwork(String trainNo, String date) {

        AlertDisplay alertDisplay = new AlertDisplay("Join the Network ?", "Other users in the network will be able to find you and ping.Lets you ping other users in the network", getContext());
        alertDisplay.getBuilder().setPositiveButton("Cancel", null);
        alertDisplay.getBuilder().setNegativeButton("Join Network", (dialog, which) -> {

            String newDate = date.replace("/", "");
            travelSection
                    .document(Mod.TRAIN.toString())
                    .collection(newDate).document(trainNo).update("list", FieldValue.arrayUnion(User.getInstance().getUserMailID())).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    createNJoinNetworkBtn.setVisibility(View.GONE);
                    joinNetworkBtn.setVisibility(View.GONE);
                    exitNetworkBtn.setVisibility(View.VISIBLE);
                    callSnackBar("You have been added to the network");
                }
            }).addOnFailureListener(e -> callSnackBar(e.getMessage()));

        });
        alertDisplay.display();


    }

    private void createNJoinNetwork(String trainNo, String date) {

        AlertDisplay alertDisplay = new AlertDisplay("Join the Network ?", "Other users in the network will be able to find you and ping.Lets you ping other users in the network", getContext());
        alertDisplay.getBuilder().setPositiveButton("Cancel", null);
        alertDisplay.getBuilder().setNegativeButton("Join Network", (dialog, which) -> {
            String newDate = date.replace("/", "");
            String[] mails = new String[]{User.getInstance().getUserMailID()};
            Map<String, Object> list = new HashMap<>();
            list.put("list", Arrays.asList(mails));
            travelSection
                    .document(Mod.TRAIN.toString())
                    .collection(newDate).document(trainNo).set(list).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    createNJoinNetworkBtn.setVisibility(View.GONE);
                    joinNetworkBtn.setVisibility(View.GONE);
                    exitNetworkBtn.setVisibility(View.VISIBLE);
                    callSnackBar("You have been added to the network");
                }
            }).addOnFailureListener(e -> callSnackBar(e.getMessage()));
        });
        alertDisplay.display();
    }

    private void exitNetwork(String trainNo, String date) {
        System.out.println(trainNo + " - " + date);
        AlertDisplay alertDisplay = new AlertDisplay("Leave the Network ?", "Other users will not be able to find or ping you anymore in the network.", getContext());
        alertDisplay.getBuilder().setPositiveButton("Cancel", null);
        alertDisplay.getBuilder().setNegativeButton("Leave Network", (dialog, which) -> {
            String newDate = date.replace("/", "");
            travelSection
                    .document(Mod.TRAIN.toString())
                    .collection(newDate).document(trainNo).update("list", FieldValue.arrayRemove(User.getInstance().getUserMailID())).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    exitNetworkBtn.setVisibility(View.GONE);
                    callSnackBar("You have left the network");
                }
            }).addOnFailureListener(e -> callSnackBar(e.getMessage()));
        });
        alertDisplay.display();


    }


}