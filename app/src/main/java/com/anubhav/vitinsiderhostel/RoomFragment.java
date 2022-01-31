package com.anubhav.vitinsiderhostel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anubhav.vitinsiderhostel.adapters.RoomServiceRecyclerAdapter;
import com.anubhav.vitinsiderhostel.models.RoomService;
import com.anubhav.vitinsiderhostel.models.Tenant;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;


public class RoomFragment extends Fragment implements View.OnClickListener {

    //firebase fire store declaration
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();


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
            add(new RoomService(R.drawable.ac_card_bg, "A/C"));
        }
    };
    private final CollectionReference userSection = db.collection("Users");
    private final CollectionReference tenantSection = db.collection("Tenants");
    private final CollectionReference tenantsBioSection = db.collection("TenantsBio");


    //firebase declarations
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser;

    private String roomNo, roomType, block, userMail;

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
        View view = inflater.inflate(R.layout.fragment_room, container, false);

        MaterialTextView bedsTxt = view.findViewById(R.id.roomPgeBeds);
        MaterialTextView typeTxt = view.findViewById(R.id.roomPgeType);
        MaterialTextView roomDetailTxt = view.findViewById(R.id.roomPgeRoomNo);

        MaterialTextView p1UserNameCard = view.findViewById(R.id.roomPgeP1Name);
        MaterialTextView p2UserNameCard = view.findViewById(R.id.roomPgeP2Name);
        MaterialTextView p3UserNameCard = view.findViewById(R.id.roomPgeP3Name);
        MaterialTextView p4UserNameCard = view.findViewById(R.id.roomPgeP4Name);

        ImageButton avatar1 = view.findViewById(R.id.roomPgeP1Avatar);
        ImageButton avatar2 = view.findViewById(R.id.roomPgeP2Avatar);
        ImageButton avatar3 = view.findViewById(R.id.roomPgeP3Avatar);
        ImageButton avatar4 = view.findViewById(R.id.roomPgeP4Avatar);

        avatar1.setOnClickListener(this);
        avatar2.setOnClickListener(this);
        avatar3.setOnClickListener(this);
        avatar4.setOnClickListener(this);

        LinearLayout secondRow = view.findViewById(R.id.roomPgeRow2LinearLayout);
        LinearLayout p4 = view.findViewById(R.id.roomPgeP4LinearLayout);

        ImageView typeIcon = view.findViewById(R.id.roomPgeTypeIcon);

        if (User.getInstance() != null) {
            roomNo = User.getInstance().getRoomNo();
            roomType = User.getInstance().getRoomType();
            block = User.getInstance().getStudentBlock();
            userMail = User.getInstance().getUserMailID();
        }

        DocumentReference tenantsDocumentReference = tenantSection.document(block).collection(roomNo).document("Tenants");

        final String beds = roomType.split("\\|")[0];
        final boolean isAc = roomType.split("\\|")[1].equalsIgnoreCase("AC");
        final String roomDetail = "Room " + roomNo + "-" + block;
        String typeStr = "NON-A/C";
        typeIcon.setImageResource(R.drawable.non_ac_icon);


        if (isAc) {
            initialiseRoomServices(view, true);
            typeIcon.setImageResource(R.drawable.ac_icon);
            typeStr = "A/C";
        } else {
            initialiseRoomServices(view, false);
        }


        if (beds.equalsIgnoreCase("2")) {

            secondRow.setVisibility(View.GONE);
            p4.setVisibility(View.GONE);

            // find all the tenants of the room
            tenantsDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    if (documentSnapshot.exists()) {

                        // get the mail-Id's of the tenants
                        final String p1Mail = Objects.requireNonNull(documentSnapshot.get("1")).toString();
                        final String p2Mail = Objects.requireNonNull(documentSnapshot.get("2")).toString();

                        // collect the details of the tenants
                        tenantsBioSection
                                .document(p1Mail)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            Tenant tenantData = documentSnapshot.toObject(Tenant.class);
                                            assert tenantData != null;
                                            p1UserNameCard.setText(tenantData.getTenantUserName());
                                        } else {
                                            // todo invite user with mail id
                                        }
                                    }
                                });


                        tenantsBioSection
                                .document(p2Mail)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            Tenant tenantData = documentSnapshot.toObject(Tenant.class);
                                            assert tenantData != null;
                                            p2UserNameCard.setText(tenantData.getTenantUserName());
                                        } else {
                                            // todo invite user with mail id
                                        }
                                    }
                                });

                    } else {
                        //todo report error
                    }
                }
            });

        } else if (beds.equalsIgnoreCase("3")) {
            secondRow.setVisibility(View.VISIBLE);
            p4.setVisibility(View.GONE);

            // find all the tenants of the room
            tenantsDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    if (documentSnapshot.exists()) {

                        // get the mail-Id's of the tenants
                        final String p1Mail = Objects.requireNonNull(documentSnapshot.get("1")).toString();
                        final String p2Mail = Objects.requireNonNull(documentSnapshot.get("2")).toString();
                        final String p3Mail = Objects.requireNonNull(documentSnapshot.get("3")).toString();

                        // collect the details of the tenants
                        tenantsBioSection
                                .document(p1Mail)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            Tenant tenantData = documentSnapshot.toObject(Tenant.class);
                                            assert tenantData != null;
                                            p1UserNameCard.setText(tenantData.getTenantUserName());
                                        } else {
                                            // todo invite user with mail id
                                        }
                                    }
                                });


                        tenantsBioSection
                                .document(p2Mail)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            Tenant tenantData = documentSnapshot.toObject(Tenant.class);
                                            assert tenantData != null;
                                            p2UserNameCard.setText(tenantData.getTenantUserName());
                                        } else {
                                            // todo invite user with mail id
                                        }
                                    }
                                });


                        tenantsBioSection
                                .document(p3Mail)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            Tenant tenantData = documentSnapshot.toObject(Tenant.class);
                                            assert tenantData != null;
                                            p3UserNameCard.setText(tenantData.getTenantUserName());
                                        } else {
                                            // todo invite user with mail id
                                        }
                                    }
                                });

                    } else {
                        //todo report error
                    }
                }
            });

        } else if (beds.equalsIgnoreCase("4")) {
            secondRow.setVisibility(View.VISIBLE);
            p4.setVisibility(View.VISIBLE);

            // find all the tenants of the room
            tenantsDocumentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    if (documentSnapshot.exists()) {

                        // get the mail-Id's of the tenants
                        final String p1Mail = Objects.requireNonNull(documentSnapshot.get("1")).toString();
                        final String p2Mail = Objects.requireNonNull(documentSnapshot.get("2")).toString();
                        final String p3Mail = Objects.requireNonNull(documentSnapshot.get("3")).toString();
                        final String p4Mail = Objects.requireNonNull(documentSnapshot.get("4")).toString();

                        // collect the details of the tenants
                        tenantsBioSection
                                .document(p1Mail)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            Tenant tenantData = documentSnapshot.toObject(Tenant.class);
                                            assert tenantData != null;
                                            p1UserNameCard.setText(tenantData.getTenantUserName());
                                        } else {
                                            // todo invite user with mail id
                                        }
                                    }
                                });


                        tenantsBioSection
                                .document(p2Mail)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            Tenant tenantData = documentSnapshot.toObject(Tenant.class);
                                            assert tenantData != null;
                                            p2UserNameCard.setText(tenantData.getTenantUserName());
                                        } else {
                                            // todo invite user with mail id
                                        }
                                    }
                                });


                        tenantsBioSection
                                .document(p3Mail)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            Tenant tenantData = documentSnapshot.toObject(Tenant.class);
                                            assert tenantData != null;
                                            p3UserNameCard.setText(tenantData.getTenantUserName());
                                        } else {
                                            // todo invite user with mail id
                                        }
                                    }
                                });

                        tenantsBioSection
                                .document(p4Mail)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            Tenant tenantData = documentSnapshot.toObject(Tenant.class);
                                            assert tenantData != null;
                                            p4UserNameCard.setText(tenantData.getTenantUserName());
                                        } else {
                                            // todo invite user with mail id
                                        }
                                    }
                                });

                    } else {
                        //todo report error
                    }
                }
            });

        }


        roomDetailTxt.setText(roomDetail);
        bedsTxt.setText(beds);
        typeTxt.setText(typeStr);

        return view;
    }

    private void initialiseRoomServices(View view, boolean type) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.roomPgeRecyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        RoomServiceRecyclerAdapter roomServiceRecyclerAdapter;

        if (type) {
            roomServiceRecyclerAdapter = new RoomServiceRecyclerAdapter(roomServices2, getActivity());
            recyclerView.setAdapter(roomServiceRecyclerAdapter);
            return;
        }

        roomServiceRecyclerAdapter = new RoomServiceRecyclerAdapter(roomServices1, getActivity());
        recyclerView.setAdapter(roomServiceRecyclerAdapter);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.roomPgeP1Avatar) {
            ViewTenantDialog viewTenantDialog = new ViewTenantDialog();
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            Fragment prev = getParentFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            viewTenantDialog.show(ft, "dialog");
        } else if (id == R.id.roomPgeP2Avatar) {

        } else if (id == R.id.roomPgeP3Avatar) {

        } else if (id == R.id.roomPgeP4Avatar) {

        }
    }

}