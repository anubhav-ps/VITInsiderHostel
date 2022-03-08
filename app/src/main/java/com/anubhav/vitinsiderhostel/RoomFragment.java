package com.anubhav.vitinsiderhostel;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anubhav.vitinsiderhostel.adapters.RoomServiceRecyclerAdapter;
import com.anubhav.vitinsiderhostel.database.LocalSqlDatabase;
import com.anubhav.vitinsiderhostel.models.AppError;
import com.anubhav.vitinsiderhostel.models.ErrorCode;
import com.anubhav.vitinsiderhostel.models.RoomService;
import com.anubhav.vitinsiderhostel.models.Tenant;
import com.anubhav.vitinsiderhostel.models.Ticket;
import com.anubhav.vitinsiderhostel.models.TicketStatus;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class RoomFragment extends Fragment implements View.OnClickListener, RoomServiceRecyclerAdapter.RecyclerCardViewClickListener {

    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference roomTickets = db.collection("RoomTickets");
    private final CollectionReference allTickets = db.collection("AllTickets");
    private final CollectionReference ticketHistory = db.collection("TicketHistory");
    private final CollectionReference reportSection = db.collection("Reports");


    private final ArrayList<RoomService> roomServices1 = new ArrayList<>() {
        {
            add(new RoomService(R.drawable.furniture_card_bg, "Furniture"));
            add(new RoomService(R.drawable.lighting_card_bg, "Lighting"));
            add(new RoomService(R.drawable.fan_card_bg, "Ceiling Fan"));
        }
    };

    private final ArrayList<RoomService> roomServices2 = new ArrayList<>() {
        {
            add(new RoomService(R.drawable.furniture_card_bg, "Furniture"));
            add(new RoomService(R.drawable.lighting_card_bg, "Lighting"));
            add(new RoomService(R.drawable.fan_card_bg, "Ceiling Fan"));
            add(new RoomService(R.drawable.ac_card_bg, "AC"));
        }
    };

    private LocalSqlDatabase localSqlDatabase;

    private View rootView;

    private MaterialTextView p1UserNameCardTxt;
    private MaterialTextView p2UserNameCardTxt;
    private MaterialTextView p3UserNameCardTxt;
    private MaterialTextView p4UserNameCardTxt;

    private ImageButton avatar1Btn;
    private ImageButton avatar2Btn;
    private ImageButton avatar3Btn;
    private ImageButton avatar4Btn;
    private ImageView typeIcon;

    private MaterialTextView bedsTxt;
    private MaterialTextView typeTxt;
    private MaterialTextView roomDetailTxt;

    private LinearLayout secondRow;
    private LinearLayout p4;
    private String beds;

    private List<Tenant> tenantList;

    private Dialog dialog;
    private String roomNo, roomType, block, userMail;

    private boolean ac = false;


    public RoomFragment() {
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
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_room, container, false);
        } else {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }

        localSqlDatabase = new LocalSqlDatabase(getContext());
        tenantList = localSqlDatabase.getTenants();

        // initialise dialog
        dialog = new Dialog(getContext());

        // retrieve user data from user instance
        if (User.getInstance() != null) {
            roomNo = User.getInstance().getRoomNo();
            roomType = User.getInstance().getRoomType();
            block = User.getInstance().getStudentBlock();
            userMail = User.getInstance().getUserMailID();
        }


        bedsTxt = rootView.findViewById(R.id.roomPgeBeds);
        typeTxt = rootView.findViewById(R.id.roomPgeType);
        roomDetailTxt = rootView.findViewById(R.id.roomPgeRoomNo);
        typeIcon = rootView.findViewById(R.id.roomPgeTypeIcon);

        p1UserNameCardTxt = rootView.findViewById(R.id.roomPgeP1Name);
        p2UserNameCardTxt = rootView.findViewById(R.id.roomPgeP2Name);
        p3UserNameCardTxt = rootView.findViewById(R.id.roomPgeP3Name);
        p4UserNameCardTxt = rootView.findViewById(R.id.roomPgeP4Name);

        avatar1Btn = rootView.findViewById(R.id.roomPgeP1Avatar);
        avatar2Btn = rootView.findViewById(R.id.roomPgeP2Avatar);
        avatar3Btn = rootView.findViewById(R.id.roomPgeP3Avatar);
        avatar4Btn = rootView.findViewById(R.id.roomPgeP4Avatar);

        secondRow = rootView.findViewById(R.id.roomPgeRow2LinearLayout);
        p4 = rootView.findViewById(R.id.roomPgeP4LinearLayout);

        beds = roomType.split("\\|")[0];

        final boolean isAc = roomType.split("\\|")[1].equalsIgnoreCase("AC");
        ac = isAc;
        typeIcon.setImageResource(R.drawable.non_ac_icon);

        final String roomDetail = "Room " + roomNo + "-" + block;

        String typeStr = "NON-A/C";

        if (isAc) {
            initialiseRoomServices(rootView, true);
            typeIcon.setImageResource(R.drawable.ac_icon);
            typeStr = "A/C";
        } else {
            initialiseRoomServices(rootView, false);
        }

        roomDetailTxt.setText(roomDetail);
        bedsTxt.setText(beds);
        typeTxt.setText(typeStr);

        // assign names to the avatar
        if (beds.equalsIgnoreCase("2")) {
            secondRow.setVisibility(View.GONE);
            p4.setVisibility(View.GONE);

            Tenant t1 = tenantList.get(0);
            if (t1.getTenantUserName() != null) {
                p1UserNameCardTxt.setText(t1.getTenantUserName());
            }

            Tenant t2 = tenantList.get(1);
            if (t2.getTenantUserName() != null) {
                p2UserNameCardTxt.setText(t2.getTenantUserName());
            }

        } else if (beds.equalsIgnoreCase("3")) {
            secondRow.setVisibility(View.VISIBLE);
            p4.setVisibility(View.GONE);

            Tenant t1 = tenantList.get(0);
            if (t1.getTenantUserName() != null) {
                p1UserNameCardTxt.setText(t1.getTenantUserName());
            }
            Tenant t2 = tenantList.get(1);
            if (t2.getTenantUserName() != null) {
                p2UserNameCardTxt.setText(t2.getTenantUserName());
            }

            Tenant t3 = tenantList.get(2);
            if (t3.getTenantUserName() != null) {
                p3UserNameCardTxt.setText(t3.getTenantUserName());
            }

        } else if (beds.equalsIgnoreCase("4")) {
            secondRow.setVisibility(View.VISIBLE);
            p4.setVisibility(View.VISIBLE);

            Tenant t1 = tenantList.get(0);
            if (t1.getTenantUserName() != null) {
                p1UserNameCardTxt.setText(t1.getTenantUserName());
            }
            Tenant t2 = tenantList.get(1);
            if (t2.getTenantUserName() != null) {
                p2UserNameCardTxt.setText(t2.getTenantUserName());
            }

            Tenant t3 = tenantList.get(2);
            if (t3.getTenantUserName() != null) {
                p3UserNameCardTxt.setText(t3.getTenantUserName());
            }

            Tenant t4 = tenantList.get(3);
            if (t4.getTenantUserName() != null) {
                p4UserNameCardTxt.setText(t4.getTenantUserName());
            }

        }

        avatar1Btn.setOnClickListener(this);
        avatar2Btn.setOnClickListener(this);
        avatar3Btn.setOnClickListener(this);
        avatar4Btn.setOnClickListener(this);


        return rootView;
    }


    private void initialiseRoomServices(View view, boolean type) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.roomPgeRecyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        RoomServiceRecyclerAdapter roomServiceRecyclerAdapter;

        if (type) {
            roomServiceRecyclerAdapter = new RoomServiceRecyclerAdapter(roomServices2, this);
            recyclerView.setAdapter(roomServiceRecyclerAdapter);
            return;
        }

        roomServiceRecyclerAdapter = new RoomServiceRecyclerAdapter(roomServices1, this);
        recyclerView.setAdapter(roomServiceRecyclerAdapter);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.roomPgeP1Avatar) {
            if (tenantList.get(0).getTenantUserName() != null) {
                callTenantDialog(0);
            } else if (!tenantList.get(0).getTenantMailID().equalsIgnoreCase(userMail)) {
                //todo invite user
                inviteUser(0);
            }
        } else if (id == R.id.roomPgeP2Avatar) {
            if (tenantList.get(1).getTenantUserName() != null) {
                callTenantDialog(1);
            } else if (!tenantList.get(1).getTenantMailID().equalsIgnoreCase(userMail)) {
                //todo invite user
                inviteUser(1);
            }
        } else if (id == R.id.roomPgeP3Avatar) {
            if (tenantList.get(2).getTenantUserName() != null) {
                callTenantDialog(2);
            } else if (!tenantList.get(2).getTenantMailID().equalsIgnoreCase(userMail)) {
                //todo invite user
                inviteUser(2);
            }
        } else if (id == R.id.roomPgeP4Avatar) {
            if (tenantList.get(3).getTenantUserName() != null) {
                callTenantDialog(3);
            } else if (!tenantList.get(3).getTenantMailID().equalsIgnoreCase(userMail)) {
                //todo invite user
                inviteUser(3);
            }
        }
    }

    private void inviteUser(int pos) {
        dialog.setContentView(R.layout.invite_user_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        MaterialButton inviteUser = dialog.findViewById(R.id.inviteUserBtn);
        MaterialTextView mailNameTxt = dialog.findViewById(R.id.inviteUserMailName);
        final String mailName = tenantList.get(pos).getTenantMailID().split("@")[0];
        mailNameTxt.setText(mailName);
        inviteUser.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            final String mailTo = tenantList.get(pos).getTenantMailID();
            final String mailSubject = "Invite to VIT Insider Hostel Edition";
            final String mailContent = "Your roommate " + User.getInstance().getUserName() + " has invited you to VIT Insider Hostel Edition. Install the application from playstore \n Link -> ";
            if (mailTo != null) {
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{mailTo});
                intent.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
                intent.putExtra(Intent.EXTRA_TEXT, mailContent);
            }
            intent.setType("message/rfc822");
            v.getContext().startActivity(Intent.createChooser(intent, "Choose an Email client :"));
        });
        dialog.show();
    }

    private Bundle getTenantBundle(int pos) {
        Bundle args = new Bundle();
        args.putString("tenantName", tenantList.get(pos).getTenantUserName());
        args.putString("tenantMailId", tenantList.get(pos).getTenantMailID());
        args.putString("tenantContactNumber", tenantList.get(pos).getTenantContactNumber());
        args.putString("tenantNativeLanguage", tenantList.get(pos).getTenantNativeLanguage());
        args.putString("tenantBranch", tenantList.get(pos).getTenantBranch());
        return args;
    }

    private void callTenantDialog(int pos) {
        Bundle args = getTenantBundle(pos);
        ViewTenantDialog viewTenantDialog = new ViewTenantDialog();
        viewTenantDialog.setArguments(args);
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        Fragment prev = getParentFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        viewTenantDialog.show(ft, "dialog");
    }

    @Override
    public void onCardItemClickListener(int pos) {
        final DocumentReference documentReferenceToRoomTicket = roomTickets.document(block)
                .collection("RoomTickets").document(roomNo);

        if (ac) {
            final String serviceName = roomServices2.get(pos).getServiceName();
            checkIfTicketExists(documentReferenceToRoomTicket, serviceName);
            return;
        }

        final String serviceName = roomServices1.get(pos).getServiceName();
        checkIfTicketExists(documentReferenceToRoomTicket, serviceName);

    }

    private void checkIfTicketExists(DocumentReference documentReferenceToRoomTicket, String serviceName) {
        documentReferenceToRoomTicket
                .get()
                .addOnSuccessListener(documentSnapshot -> documentReferenceToRoomTicket.collection("RoomService").document(serviceName)
                        .get()
                        .addOnSuccessListener(documentSnapshot1 -> {
                            if (documentSnapshot1.exists()) {
                                //todo already complaint exists
                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
                                builder.setTitle("Ticket Already Raised !");
                                builder.setMessage("Ticket has been raised for this service already by you or your roommate.\n" +
                                        "New Tickets under the same category cannot be raised until the existing ticket is closed.\n\n" +
                                        "You can check the status and details of the existing ticket in ticket history.\n");
                                builder.setPositiveButton("Ok", (dialogInterface, i) -> {
                                });
                                builder.show();
                            } else {
                                //todo collect and upload ticket
                                collectUploadTicket(documentReferenceToRoomTicket, serviceName);
                            }

                        }));

    }

    private void collectUploadTicket(DocumentReference documentReferenceToRoomTicket, String serviceName) {
        //todo display entering of ticket description
        dialog.setContentView(R.layout.raise_ticket_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        MaterialTextView serviceNameTxt = dialog.findViewById(R.id.ticketViewServiceName);
        MaterialTextView captchaTxt = dialog.findViewById(R.id.ticketViewCaptchaText);
        EditText descriptionEt = dialog.findViewById(R.id.ticketViewDescribeIssue);
        EditText captchaInputEt = dialog.findViewById(R.id.ticketViewInputCaptcha);
        MaterialButton raiseTicketBtn = dialog.findViewById(R.id.ticketViewRaiseTicket);
        raiseTicketBtn.setEnabled(false);

        final String ser = serviceName + " service";
        serviceNameTxt.setText(ser);
        final String genCaptcha = generateCaptcha();
        captchaTxt.setText(genCaptcha);

        captchaInputEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (captchaInputEt.getText().toString().equals(genCaptcha)) {
                    raiseTicketBtn.setEnabled(true);
                    captchaInputEt.setTextColor(Color.parseColor("#FFFF4444"));
                } else {
                    raiseTicketBtn.setEnabled(false);
                    captchaInputEt.setTextColor(Color.parseColor("#E6626161"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        raiseTicketBtn.setOnClickListener(v -> {

            if (TextUtils.isEmpty(descriptionEt.getText().toString())) {
                descriptionEt.setError("Description is needed");
                return;
            }

            final String serviceDescription = descriptionEt.getText().toString();
            final Timestamp timestamp = new Timestamp(new Date());
            final String uploader = userMail;

            DocumentReference documentReferenceToAllTicket = allTickets
                    .document("Room")
                    .collection(block)
                    .document();

            final String docIdForTicket = documentReferenceToAllTicket.getId();

            final Ticket ticket = new Ticket(docIdForTicket, roomNo, block, serviceName, serviceDescription, uploader, TicketStatus.BOOKED, timestamp);

            documentReferenceToAllTicket
                    .set(ticket)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            //todo push ticket into the room-ticket table
                            Map<String, String> map = new HashMap<>();
                            map.put("doc_ID", docIdForTicket);
                            documentReferenceToRoomTicket
                                    .collection("RoomService")
                                    .document(serviceName)
                                    .set(map)
                                    .addOnCompleteListener(task1 -> {

                                        if (task1.isSuccessful()) {
                                            // todo push ticket into ticket history table
                                            DocumentReference documentReferenceToTicketHistory = ticketHistory
                                                    .document("Room")
                                                    .collection(block)
                                                    .document(roomNo)
                                                    .collection("TicketsRaised")
                                                    .document();

                                            final String docIdToTicketHistory = documentReferenceToTicketHistory.getId();

                                            Map<String, String> ticketHistoryMap = new HashMap<>();
                                            ticketHistoryMap.put("doc_Id", docIdToTicketHistory);
                                            ticketHistoryMap.put("all_ticket_Doc_Id", docIdForTicket);

                                            documentReferenceToTicketHistory
                                                    .set(ticketHistoryMap)
                                                    .addOnCompleteListener(task11 -> {
                                                        if (task11.isSuccessful()) {
                                                            Toast.makeText(getContext(), "Ticket has been successfully raised", Toast.LENGTH_LONG).show();
                                                        } else {
                                                            AppError appError = new AppError(
                                                                    ErrorCode.RF003,
                                                                    userMail,
                                                                    "S",
                                                                    new Timestamp(new Date()));
                                                            reportSection.document().set(appError);
                                                        }
                                                        dialog.dismiss();
                                                    });
                                        } else {
                                            AppError appError = new AppError(
                                                    ErrorCode.RF002,
                                                    userMail,
                                                    "S",
                                                    new Timestamp(new Date()));
                                            reportSection.document().set(appError);
                                            dialog.dismiss();
                                        }
                                    });
                        } else {
                            Toast.makeText(getContext(), "Ticket Booking Failed", Toast.LENGTH_LONG).show();
                            AppError appError = new AppError(
                                    ErrorCode.RF001,
                                    userMail,
                                    "S",
                                    new Timestamp(new Date()));
                            reportSection.document().set(appError);
                            dialog.dismiss();
                        }
                    });
        });
        dialog.show();
    }

    private String generateCaptcha() {
        final int hr = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        final int min = Calendar.getInstance().get(Calendar.MINUTE);
        Random r = new Random();
        char c1 = (char) (r.nextInt(25) + 'a');
        char c2 = (char) (r.nextInt(25) + 'a');
        return String.valueOf(hr) + c1 + min + c2;
    }


}