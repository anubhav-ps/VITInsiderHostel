package com.anubhav.vitinsiderhostel.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.activities.LoginActivity;
import com.anubhav.vitinsiderhostel.adapters.BlockServiceRecyclerAdapter;
import com.anubhav.vitinsiderhostel.adapters.FeaturedMenuAdapter;
import com.anubhav.vitinsiderhostel.adapters.NoticeAdapter;
import com.anubhav.vitinsiderhostel.adapters.OutingAdapter;
import com.anubhav.vitinsiderhostel.database.LocalSqlDatabase;
import com.anubhav.vitinsiderhostel.enums.Path;
import com.anubhav.vitinsiderhostel.enums.ServiceType;
import com.anubhav.vitinsiderhostel.interfaces.iOnBlockServiceCardClicked;
import com.anubhav.vitinsiderhostel.interfaces.iOnFeaturedActivityCalled;
import com.anubhav.vitinsiderhostel.interfaces.iOnFeaturedMenuClicked;
import com.anubhav.vitinsiderhostel.interfaces.iOnNoticeActivityCalled;
import com.anubhav.vitinsiderhostel.interfaces.iOnNoticeCardClicked;
import com.anubhav.vitinsiderhostel.interfaces.iOnNoticeDownloaded;
import com.anubhav.vitinsiderhostel.interfaces.iOnOutingCardClicked;
import com.anubhav.vitinsiderhostel.interfaces.iOnOutingStatusDownloaded;
import com.anubhav.vitinsiderhostel.models.BlockService;
import com.anubhav.vitinsiderhostel.models.Featured;
import com.anubhav.vitinsiderhostel.models.Notice;
import com.anubhav.vitinsiderhostel.models.Outing;
import com.anubhav.vitinsiderhostel.models.User;
import com.anubhav.vitinsiderhostel.notifications.AppNotification;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class BlockFragment extends Fragment implements iOnOutingCardClicked, iOnBlockServiceCardClicked, iOnOutingStatusDownloaded, iOnNoticeDownloaded, iOnFeaturedMenuClicked, iOnNoticeCardClicked {


    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference noticeSection = db.collection(Path.NOTICE.getPath());

    //recycler lists
    private final List<Outing> outingList = new ArrayList<>();
    private final List<Notice> noticeList = new ArrayList<>();
    private final List<Featured> featuredMenu = new ArrayList<>() {
        {
            add(new Featured(R.drawable.mess_icon, "Mess Food"));
            add(new Featured(R.drawable.lost_found_icon, "Lost & Found"));
            add(new Featured(R.drawable.travel_companion_icon, "Travel Companion"));
            add(new Featured(R.drawable.ride_share_icon, "Ride Share"));
            add(new Featured(R.drawable.outing_request_icon, "Outing Request"));
            add(new Featured(R.drawable.hostel_rules_icon, "Hostel Rules"));
        }
    };
    private final List<BlockService> blockServiceList = new ArrayList<>() {
        {
            add(new BlockService(R.drawable.cleaning_icon, ServiceType.CLEANING.toString()));
            add(new BlockService(R.drawable.potable_water_icon, ServiceType.WATER.toString()));
            add(new BlockService(R.drawable.wifi_icon, ServiceType.WIFI.toString()));
            add(new BlockService(R.drawable.mess_service_icon, ServiceType.MESS.toString()));
            add(new BlockService(R.drawable.tv_icon, ServiceType.TV.toString()));
            add(new BlockService(R.drawable.restroom_icon, ServiceType.RESTROOM.toString()));
            add(new BlockService(R.drawable.pest_icon, ServiceType.PEST.toString()));
            add(new BlockService(R.drawable.washing_machine_icon, ServiceType.LAUNDRY.toString()));
            add(new BlockService(R.drawable.other_icon, ServiceType.OTHERS.toString()));
        }
    };
    //listeners
    private iOnOutingStatusDownloaded onOutingStatusDownloaded;
    private iOnNoticeDownloaded onNoticeDownloaded;
    private iOnNoticeActivityCalled onNoticeActivityCalled;
    private iOnFeaturedActivityCalled onFeaturedActivityCalled;

    // firebase declaration
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    //views
    private View rootView;
    private RecyclerView outingRecyclerView;
    private ViewPager noticeViewPager;
    private ProgressBar noticeProgress;
    private Dialog dialog;
    //objects
    private Outing outingData;

    //local database
    private LocalSqlDatabase localSqlDatabase;


    public BlockFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //firebase instantiation
        firebaseAuth = FirebaseAuth.getInstance();

        //firebase authState listener definition
        authStateListener = firebaseAuth -> user = firebaseAuth.getCurrentUser();

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_block, container, false);
        outingRecyclerView = rootView.findViewById(R.id.blockOutingRecyclerView);
        noticeProgress = rootView.findViewById(R.id.blockNoticeProgressBar);
        noticeViewPager = rootView.findViewById(R.id.noticePager);

        localSqlDatabase = new LocalSqlDatabase(getContext());

        onOutingStatusDownloaded = this;
        onNoticeDownloaded = this;

        outingData = new Outing();
        dialog = new Dialog(getContext());

        initialiseOutingStatus();
        generateOutingDays();
        fetchNoticeData();
        initialiseFeaturedMenu(rootView);
        initialiseBlockServiceAdapter(rootView);

        return rootView;
    }


    private void fetchNoticeData() {
        noticeList.clear();
        noticeSection
                .document(User.getInstance().getStudentBlock())
                .collection(Path.FILES.getPath())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            noticeList.add(documentSnapshot.toObject(Notice.class));
                        }
                        onNoticeDownloaded.noticeDownloaded();
                    } else {
                        //todo display No notice
                        callSnackBar("No Notice Has Been Posted");
                        noticeProgress.setVisibility(View.GONE);
                    }
                }).addOnFailureListener(e -> {
                    noticeProgress.setVisibility(View.GONE);
                    callSnackBar("Failed To Fetch Notice Data - " + e.getMessage());
                });


    }

    private void initialiseOutingStatus() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        outingRecyclerView.setLayoutManager(linearLayoutManager);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        linearLayoutManager.scrollToPosition(cal.get(Calendar.DATE) - 1);
    }

    private void initialiseFeaturedMenu(View view) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.blockFeaturedMenuRecyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);

        FeaturedMenuAdapter featuredMenuAdapter;

        featuredMenuAdapter = new FeaturedMenuAdapter(featuredMenu, getContext(), this);
        recyclerView.setAdapter(featuredMenuAdapter);

    }

    @Override
    public void noticeDownloaded() {
        noticeProgress.setVisibility(View.GONE);
        noticeList.sort((o1, o2) -> Long.compare(o2.getPostedOn().getSeconds(), o1.getPostedOn().getSeconds()));
        processNoticeViewPager();
    }

    private void processNoticeViewPager() {
        NoticeAdapter noticeAdapter = new NoticeAdapter(getContext(), noticeViewPager, noticeList, this);
        noticeViewPager.setAdapter(noticeAdapter);
    }


    private void generateOutingDays() {


        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.DAY_OF_MONTH, 1);

        int thisMonth = cal.get(Calendar.MONTH);

        while (thisMonth == cal.get(Calendar.MONTH)) {
            Date date = cal.getTime();
            Outing outing = new Outing();
            SimpleDateFormat formatToString = new SimpleDateFormat("dd", Locale.getDefault());
            final String dateText = formatToString.format(date);
            outing.setDate(dateText);

            formatToString = new SimpleDateFormat("EEE", Locale.getDefault());
            String dayText = formatToString.format(date);
            dayText = dayText.toLowerCase(Locale.ROOT);
            char[] dayArr = dayText.toCharArray();
            String firstLet = String.valueOf(dayArr[0]);
            firstLet = firstLet.toUpperCase(Locale.ROOT);
            dayArr[0] = firstLet.toCharArray()[0];
            dayText = String.valueOf(dayArr);
            outing.setDay(dayText);

            formatToString = new SimpleDateFormat("MMM", Locale.getDefault());
            final String monthText = formatToString.format(date);
            outing.setMonth(monthText);

            formatToString = new SimpleDateFormat("yyyy", Locale.getDefault());
            final String yearText = formatToString.format(date);
            outing.setYear(yearText);
            outing.setTimeStamp(date);
            outingList.add(outing);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        initialiseOutingAdapter(outingList);
    }

    private void initialiseBlockServiceAdapter(View view) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.blockServiceRecyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        BlockServiceRecyclerAdapter blockServiceAdapter;

        blockServiceAdapter = new BlockServiceRecyclerAdapter(blockServiceList, this);
        recyclerView.setAdapter(blockServiceAdapter);
    }

    private void initialiseOutingAdapter(List<Outing> outingList) {
        OutingAdapter outingAdapter = new OutingAdapter(outingList, getContext(), this);
        outingRecyclerView.setAdapter(outingAdapter);
    }

    @Override
    public void outingCardClicked(int pos) {

       /* outingStatusCR
                .document(outingList.get(pos).getYear())
                .collection(outingList.get(pos).getMonth())
                .document(outingList.get(pos).getDate())
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                outingData = task.getResult().toObject(Outing.class);
            }
            onOutingStatusDownloaded.outingStatusDownloaded();
        });*/
        String message = "Feature will be available in upcoming app updates";
        callSnackBar(message);
    }

    @Override
    public void outingStatusDownloaded() {

        String date = outingData.getDate();
        String month = outingData.getMonth();
        String status = outingData.getStatus();
        String duration = outingData.getDuration() + " hrs";

        dialog.setContentView(R.layout.dialog_outing_status);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        MaterialTextView dateTxt = dialog.findViewById(R.id.dialogOutingDateText);
        MaterialTextView monthTxt = dialog.findViewById(R.id.dialogOutingMonthText);
        ImageView image = dialog.findViewById(R.id.dialogOutingImage);
        MaterialTextView statusTxt = dialog.findViewById(R.id.dialogOutingStatusTxt);
        MaterialTextView durationTxt = dialog.findViewById(R.id.dialogOutingDurationText);

        dateTxt.setText(date);
        monthTxt.setText(month);
        statusTxt.setText(status);
        durationTxt.setText(duration);


        //todo outing status enum changed
     /*   if (status.equalsIgnoreCase(OutingStatus.NOT_ALLOWED.toString()) || status.equalsIgnoreCase(OutingStatus.NOT_DECIDED.toString())) {
            image.setImageResource(R.drawable.sad);
        } else {
            image.setImageResource(R.drawable.vacation);
        }*/

        dialog.show();
    }

    @Override
    public void blockServiceCardClickListener(int pos) {
        String message = "Feature will be available in upcoming app updates";
        callSnackBar(message);
    }


    @Override
    public void messFoodClicked() {
        String message = "Feature will be available in upcoming app updates";
        callSnackBar(message);
    }

    @Override
    public void outingRequestClicked() {
        if (User.getInstance().getUserContactNumber() == null || User.getInstance().getUserContactNumber().isEmpty() || User.getInstance().getUserContactNumber().equalsIgnoreCase("N/A")) {
            callSnackBar("Update contact number to apply for outing");
            return;
        }
        onFeaturedActivityCalled.requestOutingSectionFragment();
    }

    @Override
    public void travelCompanionClicked() {
        onFeaturedActivityCalled.requestTravelCompanion();
    }

    @Override
    public void rideShareClicked() {
        String message = "Feature will be available in upcoming app updates";
        callSnackBar(message);
    }

    @Override
    public void hostelRulesClicked() {
        String message = "Feature will be available in upcoming app updates";
        callSnackBar(message);
    }

    @Override
    public void lostNFoundClicked() {
        String message = "Feature will be available in upcoming app updates";
        callSnackBar(message);
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
            this.onFeaturedActivityCalled = (iOnFeaturedActivityCalled) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + "is not implementing on iOnFeaturedActivityCalled");
        }
        try {
            this.onNoticeActivityCalled = (iOnNoticeActivityCalled) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity + "is not implementing iOnNoticeActivityCalled");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();

        if (this.onFeaturedActivityCalled != null) {
            this.onFeaturedActivityCalled = null;
        }
        if (this.onNoticeActivityCalled != null) {
            this.onNoticeActivityCalled = null;
        }

    }

    @Override
    public void onStop() {
        super.onStop();

        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);

        if (User.getInstance() == null) {
            User user = null;
            user = localSqlDatabase.getCurrentUser();
        }

        if (user == null) {
            AppNotification.getInstance().unSubscribeAllTopics();
            FirebaseAuth.getInstance().signOut();
            localSqlDatabase.deleteCurrentUser();
            localSqlDatabase.deleteAllTenants();
            logOutUser("Logging Out Abruptly :(");
        }
    }

    private void logOutUser(String message) {
        if (message != null) {
            callSnackBar(message);
        }
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        requireActivity().startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        requireActivity().finish();
    }

    // snack bar method
    private void callSnackBar(String message) {
        Snackbar snackbar = Snackbar
                .make(requireContext(), rootView.findViewById(R.id.blockFragment), message, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.WHITE);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.navy_blue));
        snackbar.show();
    }

    @Override
    public void noticeCardClicked(int pos) {
        onNoticeActivityCalled.noticeActivityCalled(noticeList.get(pos).getNoticeDocID());

    }

}