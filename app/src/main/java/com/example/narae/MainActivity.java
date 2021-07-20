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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.Integer.parseInt;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {

    public static final String NOTIFICATION_CHANNEL_ID = "channel";
    private FirebaseFirestore firestore ;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firestore = FirebaseFirestore.getInstance();
    }

    public void whenYes(View view) {
        addUser();
    }
    public void addUser(){ //15분 미만일 시 확인 누르면 정보 저장함.
        DocumentReference docRef = firestore.collection("users").document("asdf");
        Task<DocumentSnapshot> documentSnapshotTask = docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> space = new HashMap<>(); // 여기안에 정보 적으면 됨 .
                        space.put("place"+document.get("count").toString(), "공대9호");
                        space.put("intime"+document.get("count").toString(), "11:00");
                        space.put("count",String.valueOf((parseInt(document.get("count").toString())+1)));
                        Task<Void> voidTask = firestore.collection("users").document("asdf")
                                .set(space, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MainActivity.this, "감사합니다.", Toast.LENGTH_LONG).show();
                                        onBackPressed();
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
                        Toast.makeText(MainActivity.this, " 실패", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "데이터가져오기 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void whenNo(View view) {
        onBackPressed();
        Toast.makeText(MainActivity.this, "no", Toast.LENGTH_LONG).show();
    }

            public void pushNotion(View view) { //push알람
                PendingIntent mPendingIntent = PendingIntent.getActivity(MainActivity.this, 0, new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_alert)
                        .setContentTitle("방문 확인")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(mPendingIntent)
                        .setAutoCancel(true);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    CharSequence name = "NOTIFICATION_CHANNEL_NAME";
                    String description = "8.0 버전 이상을 위한 것임";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
                    channel.setDescription(description);
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);
                }
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                Random notification_id = new Random();
                // notificationId is a unique int for each notification that you must define
                notificationManager.notify(notification_id.nextInt(100), builder.build());

            }

        }

