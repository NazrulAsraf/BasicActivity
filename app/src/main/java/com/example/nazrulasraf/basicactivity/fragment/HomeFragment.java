package com.example.nazrulasraf.basicactivity.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nazrulasraf.basicactivity.R;
import com.example.nazrulasraf.basicactivity.other.PostsData;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String clubJoined, userID;

    private View homeView;
    private RecyclerView homePostsLists;
    private DatabaseReference postRef, userRef;
    private FirebaseAuth mAuth;


    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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
        homeView = inflater.inflate(R.layout.fragment_home, container, false);

        homePostsLists = homeView.findViewById(R.id.recyclerView);
        //To show the post from the recent to the last.
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        homePostsLists.setLayoutManager(layoutManager);

        mAuth = FirebaseAuth.getInstance();
        userID = mAuth.getUid();

        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return homeView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //Get the club joined by the user.
        userRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                clubJoined = dataSnapshot.child("clubJoined").getValue().toString();
                //Retrieve post.
                //Had to be done in this because the value of clubJoined is null when done outside this method.
                FirebaseRecyclerOptions options =
                        new FirebaseRecyclerOptions.Builder<PostsData>()
                                .setQuery(postRef.child(clubJoined), PostsData.class)
                                .build();

                FirebaseRecyclerAdapter<PostsData, PostsViewHolder> adapter
                        = new FirebaseRecyclerAdapter<PostsData, PostsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final PostsViewHolder holder, int position, @NonNull PostsData model) {

                        String postID = getRef(position).getKey();
                        Query query = postRef.child(clubJoined).child(postID).orderByChild("timestamp");

                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                String profileName = dataSnapshot.child("username").getValue().toString();
                                String homePostTitle = dataSnapshot.child("title").getValue(String.class);
                                String homePostContent = dataSnapshot.child("content").getValue(String.class);
                                Long homePostTimestamp = dataSnapshot.child("timestamp").getValue(Long.class);
                                String postUserID = dataSnapshot.child("uid").getValue(String.class);

                                Date longDate = new Date(homePostTimestamp);
                                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a EEE, dd MMM yyyy");
                                sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));

                                userRef.child(postUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String profileName = dataSnapshot.child("username").getValue(String.class);
                                        holder.userName.setText(profileName);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                                holder.postTimestamp.setText(sdf.format(longDate));
                                holder.postTitle.setText(homePostTitle);
                                holder.postContent.setText(homePostContent);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_items, parent, false);
                        PostsViewHolder viewHolder = new PostsViewHolder(view);

                        return viewHolder;
                    }
                };

                homePostsLists.setAdapter(adapter);
                adapter.startListening();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        TextView userName, postTitle, postContent, postTimestamp;

        public PostsViewHolder(View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.textViewUser);
            postTitle = itemView.findViewById(R.id.textViewTitle);
            postContent = itemView.findViewById(R.id.textViewPostContent);
            postTimestamp = itemView.findViewById(R.id.textViewTimeStamp);
        }
    }
}