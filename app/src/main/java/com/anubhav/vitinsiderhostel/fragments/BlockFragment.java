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
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.adapters.BlockServiceRecyclerAdapter;
import com.anubhav.vitinsiderhostel.adapters.FeaturedMenuAdapter;
import com.anubhav.vitinsiderhostel.adapters.NoticeAdapter;
import com.anubhav.vitinsiderhostel.adapters.OutingAdapter;
import com.anubhav.vitinsiderhostel.enums.OutingStatus;
import com.anubhav.vitinsiderhostel.enums.ServiceType;
import com.anubhav.vitinsiderhostel.interfaces.iOnFeaturedMenuClicked;
import com.anubhav.vitinsiderhostel.interfaces.iOnNoticeReceived;
import com.anubhav.vitinsiderhostel.interfaces.iOnOutingSectionChosen;
import com.anubhav.vitinsiderhostel.interfaces.iOnOutingStatusReceived;
import com.anubhav.vitinsiderhostel.interfaces.iOnTicketSectionChosen;
import com.anubhav.vitinsiderhostel.interfaces.iOnUserProfileClicked;
import com.anubhav.vitinsiderhostel.models.BlockService;
import com.anubhav.vitinsiderhostel.models.Featured;
import com.anubhav.vitinsiderhostel.models.Notice;
import com.anubhav.vitinsiderhostel.models.Outing;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class BlockFragment extends Fragment implements OutingAdapter.RecyclerOutingCardViewClickListener, BlockServiceRecyclerAdapter.RecyclerBlockServiceCardClickListener, iOnOutingStatusReceived, iOnNoticeReceived ,iOnFeaturedMenuClicked{


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

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference outingStatusCR = db.collection("OutingStatus");
    private final CollectionReference noticeCR = db.collection("Notice");

    iOnOutingStatusReceived onOutingStatusReceived;
    iOnNoticeReceived onNoticeReceived;
    iOnOutingSectionChosen onOutingSectionChosen;

    private View rootView;
    private RecyclerView outingRecyclerView;
    private ViewPager noticeViewPager;
    private NoticeAdapter noticeAdapter;
    private OutingAdapter outingAdapter;
    private BlockServiceRecyclerAdapter blockServiceAdapter;
    private String studentBlock;

    private Outing outingData;
    private Dialog dialog;

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
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_block, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        outingRecyclerView = rootView.findViewById(R.id.blockOutingRecyclerView);
        outingRecyclerView.setLayoutManager(linearLayoutManager);

        onOutingStatusReceived = this;
        onNoticeReceived = this;

        outingData = new Outing();
        dialog = new Dialog(getContext());

        noticeViewPager = rootView.findViewById(R.id.noticePager);

        if (User.getInstance()!=null){
            studentBlock = User.getInstance().getStudentBlock();
        }

        generateOutingDays();
        fetchNoticeData();
        initialiseFeaturedMenu(rootView);
        initialiseBlockServiceAdapter(rootView);

        return rootView;
    }


    private void fetchNoticeData() {

        noticeCR.document(studentBlock)
                .collection("Notice")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().size() == 0) {
                        //todo display No notice
                    } else {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            noticeList.add(documentSnapshot.toObject(Notice.class));
                        }
                        onNoticeReceived.onNoticeReceived();
                    }
                }
            }
        });

    }

    private void initialiseFeaturedMenu(View view) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.blockFeaturedMenuRecyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);

        FeaturedMenuAdapter featuredMenuAdapter;

        featuredMenuAdapter = new FeaturedMenuAdapter(featuredMenu, getContext(),this);
        recyclerView.setAdapter(featuredMenuAdapter);

    }

    @Override
    public void onNoticeReceived() {
        noticeList.sort((o1, o2) -> Long.compare(o2.getPostedOn().getSeconds(), o1.getPostedOn().getSeconds()));
        processNoticeViewPager();
    }

    private void processNoticeViewPager() {
        noticeAdapter = new NoticeAdapter(getContext(), noticeViewPager, noticeList);
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
        outingAdapter = new OutingAdapter(outingList, getContext(), this);
        outingRecyclerView.setAdapter(outingAdapter);
    }

    @Override
    public void onOutingCardItemClickListener(int pos) {

        outingStatusCR
                .document(outingList.get(pos).getYear())
                .collection(outingList.get(pos).getMonth())
                .document(outingList.get(pos).getDate())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    outingData = task.getResult().toObject(Outing.class);
                }
                onOutingStatusReceived.onOutingStatusReceived();
            }
        });
    }

    @Override
    public void onOutingStatusReceived() {

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
    public void onBlockServiceCardClickListener(int pos) {

    }


    @Override
    public void onMessFoodClicked() {

    }

    @Override
    public void onOutingRequestClicked() {
        dialog.setContentView(R.layout.choose_outing_section_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        MaterialTextView applyORA = dialog.findViewById(R.id.chooseApplyORASection);
        MaterialTextView oraHistory = dialog.findViewById(R.id.chooseORAHistorySection);

        applyORA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openApplyOraFragments();
            }
        });

        oraHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openOraHistoryFragment();
            }
        });

        dialog.show();

    }

    private void openOraHistoryFragment() {
        onOutingSectionChosen.onOraHistorySectionClicked();
    }

    private void openApplyOraFragments() {
        onOutingSectionChosen.onApplyOraSectionClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = (Activity) context;
        try {
            this.onOutingSectionChosen = (iOnOutingSectionChosen) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "is not implementing on iOnOutingSectionChosen");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (this.onOutingSectionChosen != null) {
            this.onOutingSectionChosen = null;
        }

    }
}