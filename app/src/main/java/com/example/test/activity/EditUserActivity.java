package com.example.test.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.test.CloudFireStoreDemo;
import com.example.test.R;
import com.example.test.vo.User;

/**
 * 編輯User頁
 */
public class EditUserActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_NAME_USER = "EXTRA_NAME_USER";
    private EditText mUserIdEt;
    private EditText mUserNameEt;
    private EditText mAgeEt;
    private EditText mSexEt;
    private EditText mAddressEt;
    private Button mSendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        mUserIdEt = findViewById(R.id.userIdEt);
        mUserNameEt = findViewById(R.id.userNameEt);
        mAgeEt = findViewById(R.id.ageEt);
        mSexEt = findViewById(R.id.sexEt);
        mAddressEt = findViewById(R.id.addressEt);
        mSendBtn = findViewById(R.id.sendBtn);
        mSendBtn.setOnClickListener(this);

        onNewIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            User oldUser = (User) intent.getSerializableExtra(EXTRA_NAME_USER);
            if (oldUser != null) {
                mUserIdEt.setText(oldUser.userId);
                mUserNameEt.setText(oldUser.userName);
                mAgeEt.setText(String.valueOf(oldUser.age));
                mSexEt.setText(oldUser.sex);
                mAddressEt.setText(oldUser.address);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sendBtn:
                //新增User
                String age = mAgeEt.getText().toString();

                User user = new User();
                user.userId = mUserIdEt.getText().toString();
                user.userName = mUserNameEt.getText().toString();
                user.age = Integer.parseInt(TextUtils.isEmpty(age) ? "0" : age);
                user.sex = mSexEt.getText().toString();
                user.address = mAddressEt.getText().toString();
                Intent intent = new Intent(this, CloudFireStoreDemo.class);
                intent.putExtra(CloudFireStoreDemo.EXTRA_NAME_ADD_USER, user);
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
        }
    }
}