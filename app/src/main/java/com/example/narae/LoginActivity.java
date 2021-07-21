package com.example.narae;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    Button mLoginBtn;
    EditText mIdText, mPasswordText;
    Context mContext;

    private FirebaseFirestore firestore ;

    SharedPreferences loginInformation ;
    SharedPreferences.Editor editor ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        firestore = FirebaseFirestore.getInstance();
        mContext = this;
        //버튼 등록하기
        mLoginBtn = findViewById(R.id.btn_login);
        mIdText = findViewById(R.id.et_id);
        mPasswordText = findViewById(R.id.et_pwd);
        loginInformation = getSharedPreferences("loginInformation", 0);
        editor = loginInformation.edit();
        String loginId, loginPwd;
        loginId = loginInformation.getString("id",null);
        loginPwd = loginInformation.getString("password",null);
        if(loginId !=null && loginPwd != null) {
                Toast.makeText(LoginActivity.this, loginId +"님 자동로그인 입니다.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                startActivity(intent);
                finish();
        }


        //로그인 버튼이 눌리면
        mLoginBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String id = mIdText.getText().toString().trim();
                String pwd = mPasswordText.getText().toString().trim();
                DocumentReference docRef = firestore.collection("users").document("asdf");
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                if(id.equals(document.get("id"))&& pwd.equals(document.get("pw"))) {
                                    editor.putBoolean("bool",true);
                                    editor.putString("id",id);
                                    editor.putString("password",pwd);
                                    editor.commit();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity2.class);
                                    startActivity(intent);
                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                    Toast.makeText(LoginActivity.this,"로그인에 성공하였습니다.",Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(LoginActivity.this,"로그인을 실패했습니다.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(LoginActivity.this,"데이터가져오기 실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });
    }
}

