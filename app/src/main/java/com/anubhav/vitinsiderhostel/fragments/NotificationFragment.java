package com.anubhav.vitinsiderhostel.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.firebase.messaging.FirebaseMessaging;


public class NotificationFragment extends Fragment {


    private View rootView;
    private SwitchCompat enableAll, notifyAppUpdates, notifyRoommateJoined, notifyRoomTicketRaised, notifyRoomNumberChanged, notifyNoticeShared, notifyOutingSuspended, notifyOutingRequestStatus;

    public NotificationFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        enableAll = rootView.findViewById(R.id.notifyAllSwitch);
        notifyAppUpdates = rootView.findViewById(R.id.notifyAppUpdateSwitch);
        notifyRoommateJoined = rootView.findViewById(R.id.notifyRoommateJoinedSwitch);
        notifyRoomTicketRaised = rootView.findViewById(R.id.notifyRoomServiceTicketRaisedSwitch);
        notifyRoomNumberChanged = rootView.findViewById(R.id.notifyRoomNumberChangedSwitch);
        notifyNoticeShared = rootView.findViewById(R.id.notifyBlockNoticeSharedSwitch);
        notifyOutingSuspended = rootView.findViewById(R.id.notifyOutingSuspendedSwitch);
        notifyOutingRequestStatus = rootView.findViewById(R.id.notifyOutingRequestSwitch);

        enableAll.setChecked(true);
        notifyAppUpdates.setChecked(true);
        notifyRoommateJoined.setChecked(true);
        notifyRoomNumberChanged.setChecked(true);
        notifyNoticeShared.setChecked(true);

        enableAll.setClickable(false);
        notifyAppUpdates.setClickable(false);
        notifyRoommateJoined.setClickable(false);
        notifyRoomNumberChanged.setClickable(false);
        notifyNoticeShared.setClickable(false);

        notifyRoomTicketRaised.setClickable(false);
        notifyOutingSuspended.setClickable(false);
        notifyOutingRequestStatus.setClickable(false);

        callSnackBar("Notifications are required to be in the default state for Beta Testing");

        enableAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                notifyAppUpdates.setChecked(false);
                notifyRoommateJoined.setChecked(false);
                notifyRoomTicketRaised.setChecked(false);
                notifyRoomNumberChanged.setChecked(false);
                notifyNoticeShared.setChecked(false);
                notifyOutingSuspended.setChecked(false);
                notifyOutingRequestStatus.setChecked(false);
            }
        });


        notifyAppUpdates.setOnCheckedChangeListener((buttonView, isChecked) -> {
           /* if (isChecked) if (!enableAll.isChecked()) enableAll.setChecked(true);
            subscribeToAppUpdates(isChecked);*/
        });

        notifyRoommateJoined.setOnCheckedChangeListener((buttonView, isChecked) -> {
          /*  if (isChecked) if (!enableAll.isChecked()) enableAll.setChecked(true);
            subscribeToRoomMateJoined(isChecked);*/
        });

        notifyRoomTicketRaised.setOnCheckedChangeListener((buttonView, isChecked) -> {
           /* if (isChecked) if (!enableAll.isChecked()) enableAll.setChecked(true);
            subscribeToRoomServiceTicketRaised(isChecked);*/
        });

        notifyRoomNumberChanged.setOnCheckedChangeListener((buttonView, isChecked) -> {
         /*   if (isChecked)  if (!enableAll.isChecked()) enableAll.setChecked(true);
            subscribeToRoomNumberChanged(isChecked);*/
        });

        notifyNoticeShared.setOnCheckedChangeListener((buttonView, isChecked) -> {
          /*  if (isChecked) if (!enableAll.isChecked()) enableAll.setChecked(true);
            subscribeToNoticeShared(isChecked);*/
        });

        notifyOutingSuspended.setOnCheckedChangeListener((buttonView, isChecked) -> {
           /* if (isChecked)if (!enableAll.isChecked()) enableAll.setChecked(true);
            subscribeToOutingSuspension(isChecked);*/

        });

        notifyOutingRequestStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
           /* if (isChecked) if (!enableAll.isChecked()) enableAll.setChecked(true);
            subscribeToOutingRequestStatus(isChecked);*/
        });


        return rootView;
    }

    private void subscribeToAppUpdates(boolean val) {
        Toast.makeText(getContext(), "Topic change", Toast.LENGTH_SHORT).show();
        if (val) {
            FirebaseMessaging.getInstance().subscribeToTopic("APP_UPDATES");
            return;
        }
        FirebaseMessaging.getInstance().unsubscribeFromTopic("APP_UPDATES");
    }

    private void subscribeToRoomMateJoined(boolean val) {
        final String topic = "JOINED_" + User.getInstance().getRoomNo();
        if (val) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic);
            return;
        }
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }

    private void subscribeToRoomServiceTicketRaised(boolean val) {
        final String topic = "ROOM_TICKET_RAISED_" + User.getInstance().getRoomNo();
        if (val) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic);
            return;
        }
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }

    private void subscribeToRoomNumberChanged(boolean val) {
        //use token
    }

    private void subscribeToOutingRequestStatus(boolean val) {
        //use token
    }

    private void subscribeToOutingSuspension(boolean val) {
        //use token
    }

    private void subscribeToNoticeShared(boolean val) {
        final String topic = "NOTICE_" + User.getInstance().getStudentBlock();
        if (val) {
            FirebaseMessaging.getInstance().subscribeToTopic(topic);
            return;
        }
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }


    // snack bar method
    private void callSnackBar(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }


}