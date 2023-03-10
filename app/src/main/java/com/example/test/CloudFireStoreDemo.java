package com.example.test;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.test.activity.EditUserActivity;
import com.example.test.adapter.UserListAdapter;
import com.example.test.vo.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Firebase Cloud FireStore Demo
 */
public class CloudFireStoreDemo extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = CloudFireStoreDemo.class.getSimpleName();
    public static final String EXTRA_NAME_ADD_USER = "EXTRA_NAME_ADD_USER";
    public static final int REQUEST_CODE_EDIT_USER = 1000;
    private final String userCollection = "Users";
    private FloatingActionButton mAddUserBtn;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mUserListRv;

    private FirebaseFirestore mFireStore;
    private UserListAdapter mAdapter;
    private List<User> mUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_fire_store_demo);

        mFireStore = FirebaseFirestore.getInstance();

        mUserList = new ArrayList<>();

        mAddUserBtn = findViewById(R.id.addUserBtn);
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mUserListRv = findViewById(R.id.userListRv);
        mAddUserBtn.setOnClickListener(this);

        mAdapter = new UserListAdapter(this, new UserListAdapter.OnItemClickedListener() {
            @Override
            public void onDeleteBtnClicked(User user) {
                delete(user.userId);
            }

            @Override
            public void onEditBtnClicked(User user) {
//                //?????????
//                Intent intent = new Intent(CloudFireStoreDemo.this, EditUserActivity.class);
//                intent.putExtra(EditUserActivity.EXTRA_NAME_USER, user);
//                startActivityForResult(intent, REQUEST_CODE_EDIT_USER);
                update();
            }
        });
        mUserListRv.setHasFixedSize(true);
        mUserListRv.setLayoutManager(new LinearLayoutManager(this));
        mUserListRv.setAdapter(mAdapter);
        mAdapter.setData(mUserList);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            getUsers();
            mSwipeRefreshLayout.setRefreshing(false);
        });

        //??????????????????
        mFireStore.collection("Users").addSnapshotListener((value, error) -> {
            if (value != null) {
                for (DocumentChange document : value.getDocumentChanges()
                ) {
                    User user = document.getDocument().toObject(User.class);

                    if (document.getType() == DocumentChange.Type.ADDED) {
                        Log.d(TAG, "???????????????");
                        mUserList.add(user);
                    } else if (document.getType() == DocumentChange.Type.REMOVED) {
                        Log.d(TAG, "???????????????");
                        for (int i = 0; i < mUserList.size(); i++) {
                            User oldUser = mUserList.get(i);
                            if (oldUser.userId.equals(user.userId)) {
                                mUserList.remove(i);
                                break;
                            }
                        }
                    } else if (document.getType() == DocumentChange.Type.MODIFIED) {
                        Log.d(TAG, "???????????????");
                        for (int i = 0; i < mUserList.size(); i++) {
                            User oldUser = mUserList.get(i);
                            if (oldUser.userId.equals(user.userId)) {
                                mUserList.set(i, user);
                                break;
                            }
                        }
                    }
                }

                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void addUser(User user) {
        //??????Object??????User
//        mFireStore.collection("Users")
//                //????????????userId??????????????????????????????????????????
//                .document(user.userId)
//                .set(user)
//                .addOnSuccessListener(aVoid -> Log.d(TAG, "????????????"))
//                .addOnFailureListener(e -> Log.d(TAG, e.getMessage()));

        //??????HashMap??????User
        String id = UUID.randomUUID().toString();
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", "user001");
        map.put("userName", "Tony");
        map.put("age", 15);

        mFireStore.collection("Users")
                .document()
                .set(map);


        //merge????????????-???????????????????????????????????????????????????
//        mFireStore.collection("Users")
//                .document("user001")
//                .set(map, SetOptions.merge());
    }

    private void getUsers() {
        //??????Users
        mUserList.clear();
        mFireStore.collection("Users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "??????Users??????");
                        for (QueryDocumentSnapshot document : task.getResult()
                        ) {
                            mUserList.add(document.toObject(User.class));
                        }
                    } else {
                        Log.d(TAG, task.getException().toString());
                    }
                    mAdapter.notifyDataSetChanged();
                });
    }

    private void update() {
        //??????????????????
        mFireStore.collection("Users")
                .document("user001")
                .update("age", 11);

        //??????????????????
        mFireStore.collection("Users")
                .document("user001")
                .update("age", 66, "userName", "??????");

        //????????????document
        //???????????????????????????????????????????????????Firestore???????????????????????????????????????
        //???SQL?????????????????????????????????????????????????????????????????????????????????????????????????????????

//        WriteBatch batch = mFireStore.batch();//????????????
//        mFireStore.collection("Users")
//                .whereLessThan("age", 26)
//                .get()
//                .addOnCompleteListener(task -> {
//                    if (task.isSuccessful()) {
//                        for (QueryDocumentSnapshot document : task.getResult()
//                        ) {
//                            User user = document.toObject(User.class);
//                            DocumentReference ref = mFireStore.collection("Users").document(user.userId);
//                            batch.update(ref,"age", 11);
//                        }
//                        batch.commit();
//                    }
//                });
    }

    private void delete(String userId) {
        //???????????????
        mFireStore.collection("Users")
                .document(userId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "????????????");
                    } else {
                        Log.d(TAG, "????????????");
                    }
                });

        //??????????????????
        mFireStore.collection("Users")
                .document(userId)
                .update("age", FieldValue.delete());
    }

    private void where() {
        //??????????????????
        mUserList.clear();
        mFireStore.collection("Users")
                .whereEqualTo("age", 11)
                .whereEqualTo("userId", "user001")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()
                        ) {
                            mUserList.add(document.toObject(User.class));
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                });
    }

    private void order() {
        //??????
        mUserList.clear();
        mFireStore.collection("Users")
                .orderBy("age", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()
                        ) {
                            mUserList.add(document.toObject(User.class));
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                });
    }

    private void limit() {
        //??????????????????
        mUserList.clear();
        mFireStore.collection("Users")
                .orderBy("age", Query.Direction.DESCENDING)
                .limit(2)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()
                        ) {
                            mUserList.add(document.toObject(User.class));
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                });
    }

    private void startAt() {
        //????????????25???(??????)??????
        mUserList.clear();
        mFireStore.collection("Users")
                .orderBy("age", Query.Direction.ASCENDING)
                .startAt(25)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()
                        ) {
                            mUserList.add(document.toObject(User.class));
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addUserBtn:
                //??????User
                Intent intent = new Intent(this, EditUserActivity.class);
                startActivityForResult(intent, REQUEST_CODE_EDIT_USER);

//                addUser(new User());
//                update();
//                delete();

//                where();
//                order();
//                limit();
//                startAt();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_EDIT_USER:
                if (resultCode == Activity.RESULT_OK) {
                    User user = (User) data.getSerializableExtra(EXTRA_NAME_ADD_USER);
                    addUser(user);
                }
                break;
        }
    }
}