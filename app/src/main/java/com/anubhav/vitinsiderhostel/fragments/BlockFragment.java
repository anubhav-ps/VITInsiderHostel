package com.anubhav.vitinsiderhostel.fragments;

import android.app.Dialog;
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
import com.anubhav.vitinsiderhostel.adapters.FeaturedMenuAdapter;
import com.anubhav.vitinsiderhostel.adapters.NoticeAdapter;
import com.anubhav.vitinsiderhostel.adapters.OutingAdapter;
import com.anubhav.vitinsiderhostel.enums.OutingStatus;
import com.anubhav.vitinsiderhostel.interfaces.iOnNoticeReceived;
import com.anubhav.vitinsiderhostel.interfaces.iOnOutingStatusReceived;
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


public class BlockFragment extends Fragment implements OutingAdapter.RecyclerOutingCardViewClickListener, iOnOutingStatusReceived, iOnNoticeReceived {


    private final List<Outing> outingList = new ArrayList<>();
    private final List<Notice> noticeList = new ArrayList<>();
    private final List<Featured> featuredMenu = new ArrayList<>() {
        {
            add(new Featured(R.drawable.mess_icon, "Mess Food"));
            add(new Featured(R.drawable.lost_found_icon, "Lost & Found"));
            add(new Featured(R.drawable.ride_share_icon, "Ride Share"));
            add(new Featured(R.drawable.buy_sell_icon, "Buy & Sell"));
            add(new Featured(R.drawable.outing_request_icon, "Outing Request"));
            add(new Featured(R.drawable.hostel_rules_icon, "Hostel Rules"));
        }
    };
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference outingStatusCR = db.collection("OutingStatus");
    private final CollectionReference noticeCR = db.collection("Notice");
    iOnOutingStatusReceived onOutingStatusReceived;
    iOnNoticeReceived onNoticeReceived;
    private View rootView;
    private RecyclerView outingRecyclerView;
    private ViewPager noticeViewPager;
    private NoticeAdapter noticeAdapter;
    private OutingAdapter outingAdapter;
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

        noticeViewPager = rootView.findViewById(R.id.noticePager);

        generateOutingDays();
        fetchNoticeData();
        initialiseFeaturedMenu(rootView);

        dialog = new Dialog(getContext());


        return rootView;
    }


    private void fetchNoticeData() {

        noticeCR.document(User.getInstance().getStudentBlock())
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

        featuredMenuAdapter = new FeaturedMenuAdapter(featuredMenu, getContext());
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

}