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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

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
                Intent intent = new Intent(CloudFireStoreDemo.this, EditUserActivity.class);
                intent.putExtra(EditUserActivity.EXTRA_NAME_USER, user);
                startActivityForResult(intent, REQUEST_CODE_EDIT_USER);
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

        //監聽資料更新
        mFireStore.collection(userCollection).addSnapshotListener((value, error) -> {
            if (value != null) {
                for (DocumentChange document : value.getDocumentChanges()
                ) {
                    User user = document.getDocument().toObject(User.class);
                    if (document.getType() == DocumentChange.Type.ADDED) {
                        Log.d(TAG, "有資料新增");
                        mUserList.add(user);
                    } else if (document.getType() == DocumentChange.Type.REMOVED) {
                        Log.d(TAG, "有資料刪除");
                        for (int i = 0; i < mUserList.size(); i++) {
                            User oldUser = mUserList.get(i);
                            if (oldUser.userId.equals(user.userId)) {
                                mUserList.remove(i);
                                break;
                            }
                        }
                    } else if (document.getType() == DocumentChange.Type.MODIFIED) {
                        Log.d(TAG, "有資料更新");
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
        //新增User
        //String id = UUID.randomUUID().toString();
        mFireStore.collection(userCollection)
                //將文件以userId命名，若沒有指定則由系統產生
                .document(user.userId)
                .set(user)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "新增成功"))
                .addOnFailureListener(e -> Log.d(TAG, e.getMessage()));
    }

    private void getUsers() {
        //取得Users
        mUserList.clear();
        mFireStore.collection(userCollection)
                .whereEqualTo("userId", "user001")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "取得Users成功");
                        for (QueryDocumentSnapshot document : task.getResult()
                        ) {
                            mUserList.add(document.toObject(User.class));
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.d(TAG, "取得Users失敗");
                    }
                });
    }

    private void delete(String userId) {
        //刪除使用者
        mFireStore.collection("Users")
                .document(userId)
                .delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "刪除成功");
                    } else {
                        Log.d(TAG, "刪除失敗");
                    }
                });

        //刪除單一欄位
        mFireStore.collection("Users")
                .document(userId)
                .update("age", FieldValue.delete());
    }

    private void update() {
        //更新特定欄位
        mFireStore.collection(userCollection)
                .document("user001")
                .update("age", 11);

        //更新多個欄位
        mFireStore.collection(userCollection)
                .document("user001")
                .update("age", 66, "userName", "測試");

        //更新多個document
        //更新和刪除是資料庫常見的操作，其中Firestore只能針對整個文件或部分欄位
        //與SQL不同的地方在於，只可針對單一文件操作，無法一口氣更新多筆符合條件的文件
    }

    private void where() {
        //透過條件篩選
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
        //排序
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
        //限制資料筆數
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addUserBtn:
                //新增User
                Intent intent = new Intent(this, EditUserActivity.class);
                startActivityForResult(intent, REQUEST_CODE_EDIT_USER);

//                update();
//                delete();

//                where();
//                order();
//                limit();
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