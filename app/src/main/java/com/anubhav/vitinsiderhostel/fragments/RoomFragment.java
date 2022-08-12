package com.anubhav.vitinsiderhostel.fragments;

import android.annotation.SuppressLint;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.activities.LoginActivity;
import com.anubhav.vitinsiderhostel.adapters.RoomMateAdapter;
import com.anubhav.vitinsiderhostel.adapters.RoomServiceRecyclerAdapter;
import com.anubhav.vitinsiderhostel.database.LocalSqlDatabase;
import com.anubhav.vitinsiderhostel.dialogViews.ViewTenantDialog;
import com.anubhav.vitinsiderhostel.enums.Mod;
import com.anubhav.vitinsiderhostel.enums.ServiceType;
import com.anubhav.vitinsiderhostel.interfaces.iOnNotifyDbProcess;
import com.anubhav.vitinsiderhostel.interfaces.iOnRoomMateCardClicked;
import com.anubhav.vitinsiderhostel.interfaces.iOnRoomServiceCardClicked;
import com.anubhav.vitinsiderhostel.interfaces.iOnRoomTenantListDownloaded;
import com.anubhav.vitinsiderhostel.models.RoomService;
import com.anubhav.vitinsiderhostel.models.RoomTenants;
import com.anubhav.vitinsiderhostel.models.Scramble;
import com.anubhav.vitinsiderhostel.models.Tenant;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class RoomFragment extends Fragment implements View.OnClickListener, iOnRoomServiceCardClicked, iOnRoomMateCardClicked, iOnRoomTenantListDownloaded, iOnNotifyDbProcess {

    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference roomTickets = db.collection("RoomTickets");
    private final CollectionReference hostelDetailsSection = db.collection(Mod.HOD.toString());

    /* private final CollectionReference allTickets = db.collection("AllTickets");
     private final CollectionReference ticketHistory = db.collection("TicketHistory");*/


    // adapter for non ac student
    private final ArrayList<RoomService> roomServices1 = new ArrayList<>() {
        {
            add(new RoomService(R.drawable.furniture_icon, ServiceType.FURNITURE.toString()));
            add(new RoomService(R.drawable.light_icon, ServiceType.LIGHTING.toString()));
            add(new RoomService(R.drawable.fan_icon, ServiceType.CEILING_FAN.toString()));
            add(new RoomService(R.drawable.pest_icon, ServiceType.PEST.toString()));
            add(new RoomService(R.drawable.other_icon, ServiceType.OTHERS.toString()));
        }
    };

    // adapter for ac student
    private final ArrayList<RoomService> roomServices2 = new ArrayList<>() {
        {
            add(new RoomService(R.drawable.furniture_icon, ServiceType.FURNITURE.toString()));
            add(new RoomService(R.drawable.light_icon, ServiceType.LIGHTING.toString()));
            add(new RoomService(R.drawable.fan_icon, ServiceType.CEILING_FAN.toString()));
            add(new RoomService(R.drawable.ac_ser_icon, ServiceType.AC.toString()));
            add(new RoomService(R.drawable.pest_icon, ServiceType.PEST.toString()));
            add(new RoomService(R.drawable.other_icon, ServiceType.OTHERS.toString()));
        }
    };


    //roomMate Hash List
    private List<String> roomMatesHashList;

    //roomMate details List
    private List<Tenant> roomMates;

    //local database
    private LocalSqlDatabase localSqlDatabase;

    // listeners
    private iOnRoomTenantListDownloaded onRoomTenantListDownloaded;


    //views
    private View rootView;
    private MaterialTextView usernameTxt, regNumTxt, bedsTxt, typeTxt, roomDetailTxt;
    private ProgressBar roomMateProgressBar;
    private ImageButton roomMateRefreshBtn;
    private Dialog dialog;
    private RecyclerView roomMateRecyclerView, roomServiceRecyclerView;

    private RoomMateAdapter roomMateAdapter;
    private RoomServiceRecyclerAdapter roomServiceRecyclerAdapter;


    //String objects
    private String roomNo, roomType, block, userMail, username, userRegNum, scrambleMailValue;

    //flags
    private boolean ac = false;

    //
    private long setTime=0;


    // firebase auth  declaration
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;


    public RoomFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        //firebase instantiation
        firebaseAuth = FirebaseAuth.getInstance();
        //firebase authState listener definition
        authStateListener = firebaseAuth -> user = firebaseAuth.getCurrentUser();


        // Inflate the layout for this fragment
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_room, container, false);
        } else {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }

        //view initialization
        usernameTxt = rootView.findViewById(R.id.roomPgeUserName);
        regNumTxt = rootView.findViewById(R.id.roomPgeUserRegNum);
        bedsTxt = rootView.findViewById(R.id.roomPgeBeds);
        typeTxt = rootView.findViewById(R.id.roomPgeType);
        roomDetailTxt = rootView.findViewById(R.id.roomPgeRoomNo);
        roomMateProgressBar = rootView.findViewById(R.id.roomPgeRoomMateProgressBar);
        roomMateRefreshBtn = rootView.findViewById(R.id.roomPgeRoomMateRefreshBtn);
        roomMateRecyclerView = rootView.findViewById(R.id.roomPgeRoomMateRecyclerView);
        roomServiceRecyclerView = rootView.findViewById(R.id.roomPgeRecyclerView);

        roomMateProgressBar.setVisibility(View.INVISIBLE);


        //
        localSqlDatabase = new LocalSqlDatabase(getContext(), this);
        onRoomTenantListDownloaded = this;
        dialog = new Dialog(getContext());

        // retrieve user data from user instance
        if (User.getInstance() != null) {
            roomNo = User.getInstance().getRoomNo();
            roomType = User.getInstance().getRoomType();
            block = User.getInstance().getStudentBlock();
            username = User.getInstance().getUserName();
            userRegNum = User.getInstance().getStudentRegisterNumber();
            userMail = User.getInstance().getUserMailID();
        }
        usernameTxt.setText(username);
        regNumTxt.setText(userRegNum);


        // get bed type
        final String totalBeds = roomType.split("\\|")[0];
        final String finalBed = totalBeds + " BED";
        bedsTxt.setText(finalBed);

        // get room type
        final boolean isAc = roomType.split("\\|")[1].equalsIgnoreCase("AC");
        ac = isAc;
        String typeStr = "NON-A/C";
        typeTxt.setText(typeStr);
        //set adapter
        if (isAc) {
            initialiseRoomServices(true);
            typeStr = "A/C";
            typeTxt.setText(typeStr);
        } else {
            initialiseRoomServices(false);
        }

        // get room details
        final String roomDetail = roomNo + "-" + block;
        roomDetailTxt.setText(roomDetail);


        //retrieve roommate from database
        retrieveRoomTenantsFromDB();

        //initialise recycler views
        initialiseRoomMates();

        //clicks
        roomMateRefreshBtn.setOnClickListener(this);

        return rootView;
    }

    private void initialiseRoomMates() {
        roomMateRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        roomMateAdapter = new RoomMateAdapter(roomMates, getContext(), this);
        roomMateRecyclerView.setAdapter(roomMateAdapter);
    }


    private void initialiseRoomServices(boolean type) {

        roomServiceRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        if (type) {
            roomServiceRecyclerAdapter = new RoomServiceRecyclerAdapter(roomServices2, this);
            roomServiceRecyclerView.setAdapter(roomServiceRecyclerAdapter);
            return;
        }

        roomServiceRecyclerAdapter = new RoomServiceRecyclerAdapter(roomServices1, this);
        roomServiceRecyclerView.setAdapter(roomServiceRecyclerAdapter);

    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.roomPgeRoomMateRefreshBtn) {
            refreshRoomMates();
        }
    }

    @Override
    public void inviteUser(int pos) {
        dialog.setContentView(R.layout.invite_user_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        MaterialButton inviteUser = dialog.findViewById(R.id.inviteUserBtn);
        MaterialTextView messageTxt = dialog.findViewById(R.id.inviteUserMessage);
        MaterialTextView mailNameTxt = dialog.findViewById(R.id.inviteUserMailName);
        final Tenant tenant = roomMates.get(pos);

        String mail = tenant.getTenantMailID();
        String name;

        String delimiter = "[^a-zA-Z]+";
        String[] splitPhrase = mail.split("@")[0].split(delimiter);

        if (splitPhrase.length > 1) {
            name = firstCaps(splitPhrase[0]) + " " + firstCaps(splitPhrase[1]);
        } else {
            name = firstCaps(splitPhrase[0]);
        }

        final String message = "Your  roommate  from " + tenant.getTenantBranch() + " has  not  installed  the  app.Invite  your  roommate  via  mail  to  get  their  details.";
        mailNameTxt.setText(name);
        messageTxt.setText(message);
        inviteUser.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            final String mailTo = tenant.getTenantMailID();
            final String mailSubject = "Invite to VIT Insider Hostel Edition";
            final String mailContent = "Your roommate " + User.getInstance().getUserName() + " from " + User.getInstance().getStudentBranch() + " has invited you to VIT Insider Hostel Edition. Install the application from google playstore \n Link -> https://play.google.com/store/apps/details?id=com.anubhav.vitinsiderhostel";
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

    //make the first letter caps
    private String firstCaps(String s) {
        char[] arr = s.toCharArray();
        arr[0] = String.valueOf(arr[0]).toUpperCase(Locale.ROOT).charAt(0);
        return String.valueOf(arr);
    }

    //show the tenant dialog
    @Override
    public void callTenantDialog(int pos) {
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

    //get the tenant details in bundle
    private Bundle getTenantBundle(int pos) {
        Bundle args = new Bundle();
        final Tenant tenant = roomMates.get(pos);
        args.putString("tenantName", tenant.getTenantUserName());
        args.putString("tenantMailId", tenant.getTenantMailID());
        args.putString("tenantContactNumber", tenant.getTenantContactNumber());
        args.putString("tenantNativeLanguage", tenant.getTenantNativeLanguage());
        args.putString("tenantBranch", tenant.getTenantBranch());
        return args;
    }

    @Override
    public void roomServiceCardClicked(int pos) {
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
                                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
                                builder.setTitle("Ticket Already Raised !");
                                builder.setMessage("Ticket has been raised for this service already by you or your roommate.\n" +
                                        "New Tickets under the same category cannot be raised until the existing ticket is closed.\n\n" +
                                        "You can check the status and details of the existing ticket in ticket history.\n");
                                builder.setPositiveButton("Ok", (dialogInterface, i) -> {
                                });
                                builder.show();
                            } else {
                                collectUploadTicket(documentReferenceToRoomTicket, serviceName);
                            }

                        }));

    }

    private void collectUploadTicket(DocumentReference documentReferenceToRoomTicket, String serviceName) {
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

            Toast.makeText(getContext(), "Will be available from next update", Toast.LENGTH_SHORT).show();

          /*  final String serviceDescription = descriptionEt.getText().toString();
            final Timestamp timestamp = new Timestamp(new Date());
            final String uploader = userMail;

            DocumentReference documentReferenceToAllTicket = allTickets
                    .document("Room")
                    .collection(block)
                    .document();

            final String docIdForTicket = documentReferenceToAllTicket.getId();

            final Ticket ticket = new Ticket(docIdForTicket, roomNo, block, serviceName, serviceDescription, uploader, TicketStatus.BOOKED, timestamp);*/

          /*  documentReferenceToAllTicket
                    .set(ticket)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {
                            Map<String, String> map = new HashMap<>();
                            map.put("doc_ID", docIdForTicket);
                            documentReferenceToRoomTicket
                                    .collection("RoomService")
                                    .document(serviceName)
                                    .set(map)
                                    .addOnCompleteListener(task1 -> {

                                        if (task1.isSuccessful()) {
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
                    });*/
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




    private void logOutUser(String message) {
        if (message != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        requireActivity().startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        requireActivity().finish();
    }


    private void refreshRoomMates() {
        if (System.currentTimeMillis()>setTime){
            setTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(2);
            roomMateProgressBar.setVisibility(View.VISIBLE);
            downloadRoomMates();
        }
    }

    private void downloadRoomMates() {
        roomMatesHashList = new ArrayList<>();
        String scrambleRoomValue = "";
        try {
            scrambleRoomValue = Scramble.getScramble(User.getInstance().getRoomNo());
            scrambleMailValue = Scramble.getScramble(User.getInstance().getUserMailID());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }


        hostelDetailsSection
                .document(Mod.HOT.toString())
                .collection(Mod.getBlock(User.getInstance().getStudentBlock()))
                .document(scrambleRoomValue)
                .get()
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if (documentSnapshot.exists()) {
                                    final RoomTenants tenantHashMailList = documentSnapshot.toObject(RoomTenants.class);
                                    assert tenantHashMailList != null;
                                    localSqlDatabase.setTotalTenants(tenantHashMailList.getList().size() - 1);
                                    for (String t : tenantHashMailList.getList()) {
                                        if (t.equals(scrambleMailValue)) continue;
                                        roomMatesHashList.add(t);
                                    }
                                }
                            }
                            onRoomTenantListDownloaded.notifyCompleteListDownload();
                        }
                ).addOnFailureListener(e -> {
            roomMateProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Failed To Download Latest Room Mates Details", Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void notifyCompleteListDownload() {
        for (String tenantHashMail : roomMatesHashList) {
            hostelDetailsSection
                    .document(Mod.TED.toString())
                    .collection(Mod.DET.toString())
                    .document(tenantHashMail)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                Tenant tenant = documentSnapshot.toObject(Tenant.class);
                                localSqlDatabase.addTenantInBackground(tenant);
                            }
                        }
                    });
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void notifyCompleteDataDownload() {
        roomMateProgressBar.setVisibility(View.INVISIBLE);
        String message = "Room Mate Details Refreshed";
        callSnackBar(message);
        retrieveRoomTenantsFromDB();
        roomMateAdapter.setRoomMateList(roomMates);
        roomMateAdapter.notifyDataSetChanged();
        rootView.findViewById(R.id.roomPgeRoomMateRefreshBtn).setClickable(true);
    }

    // get tenant data from db
    private void retrieveRoomTenantsFromDB() {
        roomMates = localSqlDatabase.getTenants();
    }


    @Override
    public void notifyUserUpdated() {

    }

    // snack bar method
    private void callSnackBar(String message) {
        Snackbar snackbar = Snackbar
                .make(requireContext(), rootView.findViewById(R.id.roomFragment), message, Snackbar.LENGTH_SHORT);
        snackbar.setTextColor(Color.WHITE);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.navy_blue));
        snackbar.show();
    }


    //process 0 and process 1 functions
    @Override
    public void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        if (!LocalSqlDatabase.getExecutors().isTerminated()) {
            LocalSqlDatabase.stopExecutors();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (User.getInstance() == null) {
            User user = null;
            user = localSqlDatabase.getCurrentUser();
        }

        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
        if (user == null) {
            FirebaseAuth.getInstance().signOut();
            localSqlDatabase.deleteCurrentUser();
            localSqlDatabase.deleteAllTenants();
            logOutUser("Logging Out Abruptly :(");
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
                        localSqlDatabase.deleteCurrentUser();
                        localSqlDatabase.deleteAllTenants();
                        logOutUser("User details updated,Login again!");
                    }
                } else {
                    FirebaseAuth.getInstance().signOut();
                    localSqlDatabase.deleteCurrentUser();
                    localSqlDatabase.deleteAllTenants();
                    logOutUser("Logging Out Abruptly :(");
                }
            });
        }

    }


}