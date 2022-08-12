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
import com.anubhav.vitinsiderhostel.enums.Mod;
import com.anubhav.vitinsiderhostel.enums.OutingStatus;
import com.anubhav.vitinsiderhostel.enums.ServiceType;
import com.anubhav.vitinsiderhostel.interfaces.iOnBlockServiceCardClicked;
import com.anubhav.vitinsiderhostel.interfaces.iOnFeaturedMenuClicked;
import com.anubhav.vitinsiderhostel.interfaces.iOnNoticeDownloaded;
import com.anubhav.vitinsiderhostel.interfaces.iOnOutingCardClicked;
import com.anubhav.vitinsiderhostel.interfaces.iOnOutingSectionClicked;
import com.anubhav.vitinsiderhostel.interfaces.iOnOutingStatusDownloaded;
import com.anubhav.vitinsiderhostel.models.BlockService;
import com.anubhav.vitinsiderhostel.models.Featured;
import com.anubhav.vitinsiderhostel.models.Notice;
import com.anubhav.vitinsiderhostel.models.Outing;
import com.anubhav.vitinsiderhostel.models.Scramble;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class BlockFragment extends Fragment implements iOnOutingCardClicked, iOnBlockServiceCardClicked, iOnOutingStatusDownloaded, iOnNoticeDownloaded, iOnFeaturedMenuClicked {


    //firebase fireStore
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference hostelDetailsSection = db.collection(Mod.HOD.toString());
    private final CollectionReference outingStatusCR = db.collection("OutingStatus");
    private final CollectionReference noticeSection = db.collection(Mod.NOS.toString());
    //recycler lists
    private final List<Outing> outingList = new ArrayList<>();
    private final List<Notice> noticeList = new ArrayList<>();
    private final List<Featured> featuredMenu = new ArrayList<>() {
        {
            add(new Featured(R.drawable.mess_icon, "Mess Food"));
            add(new Featured(R.drawable.lost_found_icon, "Lost & Found"));
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
    iOnOutingStatusDownloaded onOutingStatusDownloaded;
    iOnNoticeDownloaded onNoticeDownloaded;
    iOnOutingSectionClicked onOutingSectionClicked;
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

       /* noticeSection
                .document(Mod.getBlock(User.getInstance().getStudentBlock()))
                .collection(Mod.DET.toString())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() == 0) {
                        //todo display No notice
                        noticeProgress.setVisibility(View.GONE);
                    } else {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            noticeList.add(documentSnapshot.toObject(Notice.class));
                        }
                        onNoticeReceived.onNoticeReceived();
                    }
                }
            }
        });
*/

    }

    private void initialiseOutingStatus() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        outingRecyclerView.setLayoutManager(linearLayoutManager);
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
        NoticeAdapter noticeAdapter = new NoticeAdapter(getContext(), noticeViewPager, noticeList);
        noticeViewPager.setAdapter(noticeAdapter);
    }

    private void generateOutingDays() {

        Date today = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(today);

        for (int i = 0; i < 7; i++) {
            cal.setTime(today);
            cal.add(Calendar.DATE, i);
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

            outingList.add(outing);

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
        String from = outingData.getOpenTime();
        String till = outingData.getCloseTime();
        String duration = outingData.getDuration() + " hrs";

        dialog.setContentView(R.layout.dialog_outing_status);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        MaterialTextView dateTxt = dialog.findViewById(R.id.dialogOutingDateText);
        MaterialTextView monthTxt = dialog.findViewById(R.id.dialogOutingMonthText);
        ImageView image = dialog.findViewById(R.id.dialogOutingImage);
        MaterialTextView statusTxt = dialog.findViewById(R.id.dialogOutingStatusTxt);
        MaterialTextView fromTxt = dialog.findViewById(R.id.dialogOutingFromTxt);
        MaterialTextView tillTxt = dialog.findViewById(R.id.dialogOutingTillTxt);
        MaterialTextView durationTxt = dialog.findViewById(R.id.dialogOutingDurationText);

        dateTxt.setText(date);
        monthTxt.setText(month);
        statusTxt.setText(status);
        fromTxt.setText(from);
        tillTxt.setText(till);
        durationTxt.setText(duration);

        if (status.equalsIgnoreCase(OutingStatus.NOT_ALLOWED.toString()) || status.equalsIgnoreCase(OutingStatus.NOT_DECIDED.toString())) {
            image.setImageResource(R.drawable.sad);
        } else {
            image.setImageResource(R.drawable.vacation);
        }

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
        dialog.setContentView(R.layout.choose_outing_section_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        MaterialTextView applyORA = dialog.findViewById(R.id.chooseApplyORASection);
        MaterialTextView oraHistory = dialog.findViewById(R.id.chooseORAHistorySection);

        applyORA.setOnClickListener(v -> {
            dialog.dismiss();
            openApplyOraFragments();
        });

        oraHistory.setOnClickListener(v -> {
            dialog.dismiss();
            openOraHistoryFragment();
        });

        dialog.show();

    }

    private void openOraHistoryFragment() {
        onOutingSectionClicked.oraHistorySectionClicked();
    }

    private void openApplyOraFragments() {
        onOutingSectionClicked.applyOraSectionClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
            this.onOutingSectionClicked = (iOnOutingSectionClicked) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "is not implementing on iOnOutingSectionChosen");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (this.onOutingSectionClicked != null) {
            this.onOutingSectionClicked = null;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        noticeList.clear();
        fetchNoticeData();

        if (User.getInstance() == null) {
            User user = null;
            user = localSqlDatabase.getCurrentUser();
        }

        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);

        if (user == null) {
            FirebaseAuth.getInstance().signOut();
            logOutUser("Logging Out Abruptly :(");
            localSqlDatabase.deleteCurrentUser();
            localSqlDatabase.deleteAllTenants();
        } else {
            String scrambleMailValue = "";
            try {
                scrambleMailValue = Scramble.getScramble(Objects.requireNonNull(user.getEmail()).toLowerCase(Locale.ROOT));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            hostelDetailsSection
                    .document(Mod.HOS.toString())
                    .collection(Mod.DET.toString())
                    .document(scrambleMailValue)
                    .get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String roomNo = Objects.requireNonNull(documentSnapshot.get("roomNo")).toString();
                    String studentBlock = Objects.requireNonNull(documentSnapshot.get("studentBlock")).toString();
                    String registerNum = Objects.requireNonNull(documentSnapshot.get("studentRegisterNumber")).toString();

                    if (!roomNo.equals(User.getInstance().getRoomNo()) || !studentBlock.equals(User.getInstance().getStudentBlock()) || !registerNum.equals(User.getInstance().getStudentRegisterNumber())) {
                        FirebaseAuth.getInstance().signOut();
                        logOutUser("Updates in room details, Login again!");
                        localSqlDatabase.deleteCurrentUser();
                        localSqlDatabase.deleteAllTenants();
                    }

                } else {
                    FirebaseAuth.getInstance().signOut();
                    logOutUser("Logging Out Abruptly :(");
                    localSqlDatabase.deleteCurrentUser();
                    localSqlDatabase.deleteAllTenants();
                }
            });
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

}