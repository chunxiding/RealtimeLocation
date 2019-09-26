package com.example.realtimelocation;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realtimelocation.Interface.IFirebaseLoadDone;
import com.example.realtimelocation.Interface.IRecyclerItemClickListener;
import com.example.realtimelocation.Model.MyResponse;
import com.example.realtimelocation.Model.Request;
import com.example.realtimelocation.Model.User;
import com.example.realtimelocation.Remote.IFCMService;
import com.example.realtimelocation.Utils.Common;
import com.example.realtimelocation.ViewHolder.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class AllPeopleActivity extends AppCompatActivity implements IFirebaseLoadDone {

    FirebaseRecyclerAdapter<User, UserViewHolder> adapter, searchAdapter;
    RecyclerView recycler_all_user;
    IFirebaseLoadDone firebaseLoadDone;

    MaterialSearchBar searchBar;
    List<String> suggestList = new ArrayList<>();

    IFCMService ifcmService;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_people);

        ifcmService = Common.getIfcmService();

        searchBar = findViewById(R.id.material_search_bar);
        searchBar.setCardViewElevation(10);
        searchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> suggest = new ArrayList<>();

                for (String search: suggestList) {
                    if (search.toLowerCase().contains(searchBar.getText().toLowerCase())) {
                        suggest.add(search);
                    }
                }
                searchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if (!enabled) {
                    if (adapter != null) {
                        recycler_all_user.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        recycler_all_user = findViewById(R.id.recycler_all_people);
        recycler_all_user.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_all_user.setLayoutManager(layoutManager);
        recycler_all_user.addItemDecoration(new DividerItemDecoration(this, ((LinearLayoutManager) layoutManager).getOrientation()));

        firebaseLoadDone = this;

        loadUserList();
        loadSearchData();
    }

    private void loadSearchData() {
        final List<String> lstUserEmail = new ArrayList<>();
        DatabaseReference userList = FirebaseDatabase.getInstance().getReference(Common.USER_INFO);
        userList.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot: dataSnapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    lstUserEmail.add(user.getEmail());
                }
                firebaseLoadDone.onFirebaseLoadUserNameDone(lstUserEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                firebaseLoadDone.onFirebaseLoadFailed(databaseError.getMessage());
            }
        });
    }

    private void loadUserList() {
        Query query = FirebaseDatabase.getInstance().getReference().child(Common.USER_INFO);
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i, @NonNull final User user) {
                // logged user is me
                if (user.getEmail().equals(Common.loggedUser.getEmail())) {
                    userViewHolder.txt_user_email.setText(new StringBuilder(user.getEmail()).append(" (me)"));
                    userViewHolder.txt_user_email.setTypeface(userViewHolder.txt_user_email.getTypeface(), Typeface.ITALIC);

                } else {
                    // other user
                    userViewHolder.txt_user_email.setText(new StringBuilder(user.getEmail()));
                }

                // onClick event
                userViewHolder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        showDialogReuqest(user);
                    }
                });
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user, parent, false);
                return new UserViewHolder(itemView);
            }
        };
        adapter.startListening();
        recycler_all_user.setAdapter(adapter);
    }

    private void showDialogReuqest(final User user) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyRequestDialog);
        alertDialog.setTitle("Send Friend Request");
        alertDialog.setMessage("Do you want to send friend request to" + user.getEmail());
        alertDialog.setIcon(R.drawable.ic_account_circle_black_24dp);
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.setPositiveButton("SEND", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseReference acceptList = FirebaseDatabase.getInstance().getReference()
                        .child(Common.loggedUser.getUid())
                        .child(Common.ACCEPT_LIST);
                acceptList.orderByKey().equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // if not in the friend list
                        if (dataSnapshot.getValue() == null) {
                            sendFriendRequest(user);
                        } else {
                            Toast.makeText(AllPeopleActivity.this, "You and " + user.getEmail() + " are already friends.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        alertDialog.show();
    }

    private void sendFriendRequest(final User user) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference(Common.TOKENS);
        tokens.orderByKey().equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //if no token
                if (dataSnapshot.getValue() == null) {
                    Toast.makeText(AllPeopleActivity.this, "Invalid Token", Toast.LENGTH_SHORT).show();
                } else {
                    Request request = new Request();
                    request.setTo(dataSnapshot.child(user.getUid()).getValue(String.class));

                    Map<String, String> dataSend = new HashMap<>();
                    dataSend.put(Common.FROM_UID, Common.loggedUser.getUid());
                    dataSend.put(Common.FROM_NAME, Common.loggedUser.getEmail());
                    dataSend.put(Common.TO_UID, user.getUid());
                    dataSend.put(Common.TO_NAME, user.getEmail());
                    request.setData(dataSend);

                    compositeDisposable.add(ifcmService.sendFriendRequestToUser(request)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<MyResponse>() {
                        @Override
                        public void accept(MyResponse myResponse) throws Exception {
                            if (myResponse.success == 1) {
                                Toast.makeText(AllPeopleActivity.this, "Friend Reuqest Sent!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(AllPeopleActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStop() {
        if (adapter != null) {
            adapter.stopListening();
        }
        if (searchAdapter != null) {
            searchAdapter.stopListening();
        }
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.startListening();
        }
        if (searchAdapter != null) {
            searchAdapter.startListening();
        }
    }

    private void startSearch(String txt_search) {
        Query query = FirebaseDatabase.getInstance().getReference(Common.USER_INFO)
                .orderByChild("name")
                .startAt(txt_search);
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        searchAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i, @NonNull final User user) {
                // logged user is me
                if (user.getEmail().equals(Common.loggedUser.getEmail())) {
                    userViewHolder.txt_user_email.setText(new StringBuilder(user.getEmail()).append(" (me)"));
                    userViewHolder.txt_user_email.setTypeface(userViewHolder.txt_user_email.getTypeface(), Typeface.ITALIC);

                } else {
                    // other user
                    userViewHolder.txt_user_email.setText(new StringBuilder(user.getEmail()));
                }

                // onClick event
                userViewHolder.setiRecyclerItemClickListener(new IRecyclerItemClickListener() {
                    @Override
                    public void onItemClickListener(View view, int position) {
                        showDialogReuqest(user);
                    }
                });
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user, parent, false);
                return new UserViewHolder(itemView);
            }
        };
        searchAdapter.startListening();
        recycler_all_user.setAdapter(searchAdapter);
    }

    @Override
    public void onFirebaseLoadUserNameDone(List<String> lstEmail) {
        searchBar.setLastSuggestions(lstEmail);
    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
