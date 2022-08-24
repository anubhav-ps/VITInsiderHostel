package com.anubhav.vitinsiderhostel.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.adapters.RoomTicketsAdapter;
import com.anubhav.vitinsiderhostel.enums.TicketStatus;
import com.anubhav.vitinsiderhostel.interfaces.iOnRoomTicketCardClicked;
import com.anubhav.vitinsiderhostel.interfaces.iOnTicketListDownloaded;
import com.anubhav.vitinsiderhostel.models.AlertDisplay;
import com.anubhav.vitinsiderhostel.models.Ticket;
import com.anubhav.vitinsiderhostel.models.TicketIDs;
import com.anubhav.vitinsiderhostel.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class RoomTicketFragment extends Fragment implements iOnTicketListDownloaded, iOnRoomTicketCardClicked {

    private final FirebaseFirestore dB = FirebaseFirestore.getInstance();
    private final CollectionReference collectionReferenceToTicketHistory = dB.collection("TicketHistory");
    private final CollectionReference collectionReferenceToAllTickets = dB.collection("AllTickets");
    iOnTicketListDownloaded onTicketListDownloaded;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private RecyclerView recyclerView;
    private RoomTicketsAdapter adapter;
    private View rootView;
    private ArrayList<Ticket> raisedTicketList;
    private ArrayList<TicketIDs> ticketIDsList;
    private Dialog dialog;

    public RoomTicketFragment() {
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
        rootView = inflater.inflate(R.layout.fragment_room_ticket, container, false);
        progressBar = rootView.findViewById(R.id.ticketHistoryRoomProgressBar);
        linearLayout = rootView.findViewById(R.id.ticketHistoryRoomLoadLinear);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView = rootView.findViewById(R.id.ticketHistoryRoomRecyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);

        dialog = new Dialog(getContext());
        linearLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);


        raisedTicketList = new ArrayList<>();
        ticketIDsList = new ArrayList<>();

        onTicketListDownloaded = this;

        //calling the recycler function
        processSearchTickets();
        return rootView;
    }

    private void processRecyclerAdapter() {
        adapter = new RoomTicketsAdapter(raisedTicketList, this);
        recyclerView.setAdapter(adapter);
    }


    //function to display the lost item cells in the recycler view
    private void processSearchTickets() {
        collectionReferenceToTicketHistory.
                document("Room").
                collection(User.getInstance().getStudentBlock()).
                document(User.getInstance().getRoomNo()).
                collection("TicketsRaised")
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (task.getResult().size() == 0) {
                    //todo display no tickets
                    progressBar.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.GONE);

                    AlertDisplay alertDisplay = new AlertDisplay("No Tickets Raised", "There are no tickets raised for your room", getContext());
                    alertDisplay.getBuilder().setPositiveButton("Ok", null);
                    alertDisplay.display();
                } else {
                    processGetTickets(task);
                }
            }
        });
    }

    private void processGetTickets(Task<QuerySnapshot> task) {


        for (QueryDocumentSnapshot document : task.getResult()) {

            TicketIDs ticketIDs = new TicketIDs();
            ticketIDs.setTicketId(Objects.requireNonNull(document.get("all_ticket_Doc_Id")).toString().trim());
            ticketIDs.setTicketHistoryId(Objects.requireNonNull(document.get("doc_Id")).toString().trim());
            ticketIDsList.add(ticketIDs);

            collectionReferenceToAllTickets.
                    document("Room")
                    .collection(User.getInstance().getStudentBlock())
                    .document(ticketIDs.getTicketId()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                Ticket ticket = task.getResult().toObject(Ticket.class);
                                raisedTicketList.add(ticket);
                                assert ticket != null;
                            }
                            onTicketListDownloaded.listReceived();
                        }
                    });


        }

    }

    @Override
    public void listReceived() {
        raisedTicketList.sort((o1, o2) -> Long.compare(o2.getItemTimeStamp().getSeconds(), o1.getItemTimeStamp().getSeconds()));
        progressBar.setVisibility(View.GONE);
        linearLayout.setVisibility(View.GONE);
        processRecyclerAdapter();
    }

    //process 0 and 1
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (ticketIDsList != null && adapter != null) {
            ticketIDsList.clear();
            adapter = null;
        }
    }

    @Override
    public void onRoomTicketCardLongPressed(int pos, String ticketStatus, String docID) {
        if (ticketStatus.equalsIgnoreCase(TicketStatus.BOOKED.toString()) || ticketStatus.equalsIgnoreCase(TicketStatus.IN_REVIEW.toString())) {

            AlertDisplay alertDisplay = new AlertDisplay("Ticket not closed yet", "This Ticket cannot be deleted until its solved", getContext());
            alertDisplay.getBuilder().setPositiveButton("Ok", null);
            alertDisplay.display();

        } else if (ticketStatus.equalsIgnoreCase(TicketStatus.SOLVED.toString())) {

            AlertDisplay alertDisplay = new AlertDisplay("Delete Ticket", "Are you sure you want to delete the ticket ?", getContext());
            alertDisplay.getBuilder().setPositiveButton("Cancel", null);
            alertDisplay.getBuilder().setNegativeButton("Delete", (dialogInterface, i) -> {
                deleteTicket(pos, docID);
            });
            alertDisplay.display();

        }
    }

    private void deleteTicket(int pos, String docID) {
        collectionReferenceToAllTickets.
                document("Room")
                .collection(User.getInstance().getStudentBlock())
                .document(docID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    collectionReferenceToTicketHistory.
                            document("Room").
                            collection(User.getInstance().getStudentBlock()).
                            document(User.getInstance().getRoomNo()).
                            collection("TicketsRaised").whereEqualTo("all_ticket_Doc_Id", docID).limit(1).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            final String id = Objects.requireNonNull(document.get("doc_Id")).toString();
                                            collectionReferenceToTicketHistory.
                                                    document("Room").
                                                    collection(User.getInstance().getStudentBlock()).
                                                    document(User.getInstance().getRoomNo()).
                                                    collection("TicketsRaised").document(id).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getContext(), "Ticket Deleted Successfully", Toast.LENGTH_LONG).show();
                                                        raisedTicketList.remove(pos);
                                                        processRecyclerAdapter();
                                                    } else {
                                                        //report couldn't delete
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        //todo couldnt delete ticket
                                    }
                                }
                            });
                }
            }
        });
    }
}