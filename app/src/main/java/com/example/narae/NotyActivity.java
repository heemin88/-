package com.example.narae;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.content.ContentValues.TAG;
import static java.lang.Integer.parseInt;

@RequiresApi(api = Build.VERSION_CODES.O)
public class NotyActivity extends AppCompatActivity {
    public static final String NOTIFICATION_CHANNEL_ID = "channel";
    private FirebaseFirestore firestore ;
    private static final String TAG = "MainActivity";
    String time;String time2;String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firestore = FirebaseFirestore.getInstance();
        Bundle notyintent = getIntent().getExtras();
        time = notyintent.getString("saveTime");
        String[] a = time.split(" ");
        String[] b = a[1].split(":");
        time2 = notyintent.getString("outTime");
        name = notyintent.getString("buildingname");
        TextView q = (TextView)findViewById(R.id.question);
        q.setText(b[0]+"시 "+b[1]+"분에\n"+name+"을(를)\n방문하셨습니까?");
    }

    public void whenYes(View view) {
        addInUser(time,name);
        addOutUser(time2);

        onBackPressed();
    }
    public void addInUser(String time,String name){ //15분 미만일 시 확인 누르면 정보 저장함.
        DocumentReference docRef = firestore.collection("users").document("asdf");
        Task<DocumentSnapshot> documentSnapshotTask = docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> space = new HashMap<>(); // 여기안에 정보 적으면 됨 .
                        space.put("place"+document.get("count").toString(), name);
                        space.put("intime"+document.get("count").toString(), time);
                        Task<Void> voidTask = firestore.collection("users").document("asdf")
                                .set(space, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(NotyActivity.this, "감사합니다.", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });

                    }
                    else{
                        Toast.makeText(NotyActivity.this, " 실패", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NotyActivity.this, "데이터가져오기 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void addOutUser(String time2) {
        DocumentReference docRef = this.firestore.collection("users").document("asdf");
        Task<DocumentSnapshot> documentSnapshotTask = docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> space = new HashMap<>(); // 여기안에 정보 적으면 됨 .
                        space.put("outtime"+document.get("count").toString(), time2);
                        space.put("count",String.valueOf((parseInt(document.get("count").toString())+1)));
                        Task<Void> voidTask = firestore.collection("users").document("asdf")
                                .set(space, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(NotyActivity.this, "나가는 시간이 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                        // onBackPressed();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });

                    }
                    else{
                        Toast.makeText(NotyActivity.this, " 실패", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NotyActivity.this, "데이터가져오기 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void whenNo(View view) {
        onBackPressed();
        Toast.makeText(NotyActivity.this, "no", Toast.LENGTH_SHORT).show();
    }

}

