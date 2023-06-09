package com.example.homecontrol;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    String path = "HomeControl/Members/";
    ImageView logout;
    FirebaseDatabase database;
    DatabaseReference refEmail,refName, refEmail02, refEmail03;
    TextView tName, tEmail;
    String uid;
    CardView configCard, shareCard, changePasscard, infoCard;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        logout = view.findViewById(R.id.Logout_profile);
        tName = view.findViewById(R.id.text_name_Profile);
        tEmail = view.findViewById(R.id.text_email_Profile);

        configCard = view.findViewById(R.id.card_icon_Config_profile);
        shareCard = view.findViewById(R.id.card_icon_share_profile);
        changePasscard = view.findViewById(R.id.card_icon_Password_profile);
        infoCard = view.findViewById(R.id.card_icon_Info_profile);

        database = FirebaseDatabase.getInstance();

        FirebaseUser userUID = FirebaseAuth.getInstance().getCurrentUser();
        assert userUID != null;
        uid = userUID.getUid();

        refEmail = database.getReference(path + "Users/" + uid + "/email");
        refName = database.getReference(path + "Users/" + uid + "/name");
        refEmail02 = database.getReference("HomeControl/ESP8266/Users/UID-02/email");
        refEmail03 = database.getReference("HomeControl/ESP8266/Users/UID-03/email");
        loadUser();

        logout();

        config();

        share();

        Role();

        changePass();

        infoCard();

        return view;
    }

    private void infoCard() {

        infoCard.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AppInformation.class);
            startActivity(intent);
        });

    }

    private void changePass() {

        changePasscard.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ChangePassword.class);
            startActivity(intent);
        });

    }

    private void Role() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String email = user.getEmail();
        refEmail02.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String gEmail = dataSnapshot.getValue(String.class);
                assert email != null;
                if(email.equals(gEmail)){
                    configCard.setClickable(false);
                    shareCard.setClickable(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        refEmail03.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String gEmail = dataSnapshot.getValue(String.class);
                assert email != null;
                if(email.equals(gEmail)){
                    configCard.setClickable(false);
                    shareCard.setClickable(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void share() {

        shareCard.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ShareDevice.class);
            startActivity(intent);
        });

    }

    private void config() {

        configCard.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), SelectBoardConfig.class);
            startActivity(intent);
        });

    }

    private void loadUser() {
        refName.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String gName = dataSnapshot.getValue(String.class);
                tName.setText(gName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        refEmail.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String gEmail = dataSnapshot.getValue(String.class);
                tEmail.setText(gEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void logout() {
        logout.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), Login.class);
            startActivity(intent);
            requireActivity().finishAffinity();
        });
    }
}