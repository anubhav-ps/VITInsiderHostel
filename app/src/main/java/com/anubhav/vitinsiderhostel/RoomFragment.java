package com.anubhav.vitinsiderhostel;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.anubhav.vitinsiderhostel.database.LocalSqlDatabase;
import com.anubhav.vitinsiderhostel.models.RoomService;
import com.anubhav.vitinsiderhostel.models.Tenant;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


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

    private Dialog dialog ;

    private final CollectionReference userSection = db.collection("Users");
    private final CollectionReference tenantSection = db.collection("Tenants");
    private final CollectionReference tenantsBioSection = db.collection("TenantsBio");


    //firebase declarations
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser firebaseUser;
    private LocalSqlDatabase localSqlDatabase;
    private List<Tenant> tenantList;
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
        localSqlDatabase = new LocalSqlDatabase(getContext());
        tenantList = localSqlDatabase.getTenants();

        dialog = new Dialog(getContext());

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

        roomDetailTxt.setText(roomDetail);
        bedsTxt.setText(beds);
        typeTxt.setText(typeStr);

        // assign names to the avatar
        if (beds.equalsIgnoreCase("2")) {
            secondRow.setVisibility(View.GONE);
            p4.setVisibility(View.GONE);

            Tenant t1 = tenantList.get(0);
            if (t1.getTenantUserName() != null) {
                p1UserNameCard.setText(t1.getTenantUserName());
            }

            Tenant t2 = tenantList.get(1);
            if (t2.getTenantUserName() != null) {
                p2UserNameCard.setText(t2.getTenantUserName());
            }

        } else if (beds.equalsIgnoreCase("3")) {
            secondRow.setVisibility(View.VISIBLE);
            p4.setVisibility(View.GONE);

            Tenant t1 = tenantList.get(0);
            if (t1.getTenantUserName() != null) {
                p1UserNameCard.setText(t1.getTenantUserName());
            }
            Tenant t2 = tenantList.get(1);
            if (t2.getTenantUserName() != null) {
                p2UserNameCard.setText(t2.getTenantUserName());
            }

            Tenant t3 = tenantList.get(2);
            if (t3.getTenantUserName() != null) {
                p3UserNameCard.setText(t3.getTenantUserName());
            }

        } else if (beds.equalsIgnoreCase("4")) {
            secondRow.setVisibility(View.VISIBLE);
            p4.setVisibility(View.VISIBLE);

            Tenant t1 = tenantList.get(0);
            if (t1.getTenantUserName() != null) {
                p1UserNameCard.setText(t1.getTenantUserName());
            }
            Tenant t2 = tenantList.get(1);
            if (t2.getTenantUserName() != null) {
                p2UserNameCard.setText(t2.getTenantUserName());
            }

            Tenant t3 = tenantList.get(2);
            if (t3.getTenantUserName() != null) {
                p3UserNameCard.setText(t3.getTenantUserName());
            }

            Tenant t4 = tenantList.get(3);
            if (t4.getTenantUserName() != null) {
                p4UserNameCard.setText(t4.getTenantUserName());
            }

        }

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
            if (tenantList.get(0).getTenantUserName() != null) {
                callTenantDialog(0);
            } else {
                //todo invite user
                inviteUser(0);
            }
        } else if (id == R.id.roomPgeP2Avatar) {
            if (tenantList.get(1).getTenantUserName() != null) {
                callTenantDialog(1);
            } else {
                //todo invite user
                inviteUser(1);
            }
        } else if (id == R.id.roomPgeP3Avatar) {
            if (tenantList.get(2).getTenantUserName() != null) {
                callTenantDialog(2);
            } else {
                //todo invite user
                inviteUser(2);
            }
        } else if (id == R.id.roomPgeP4Avatar) {
            if (tenantList.get(3).getTenantUserName() != null) {
                callTenantDialog(3);
            } else {
                //todo invite user
                inviteUser(3);
            }
        }
    }

    private void inviteUser(int pos){
        dialog.setContentView(R.layout.invite_user_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        MaterialButton inviteUser = dialog.findViewById(R.id.inviteUserBtn);
        inviteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                final String  mailTo = tenantList.get(pos).getTenantMailID();
                final String  mailSubject = "Invite to VIT Insider Hostel Edition";
                final String  mailContent = "Your roommate "+User.getInstance().getUserName()+" has invited you to VIT Insider Hostel Edition. Install the application from playstore \n Link -> ";
                if (mailTo!=null) {
                    intent.putExtra(Intent.EXTRA_EMAIL,new String[]{mailTo});
                    intent.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
                    intent.putExtra(Intent.EXTRA_TEXT, mailContent);
                }
                intent.setType("message/rfc822");
                v.getContext().startActivity(Intent.createChooser(intent, "Choose an Email client :"));
            }
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

}