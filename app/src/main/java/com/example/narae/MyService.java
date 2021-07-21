package com.example.narae;import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.content.ContentValues.TAG;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.example.narae.NotyActivity.NOTIFICATION_CHANNEL_ID;
import static java.lang.Integer.parseInt;

public class MyService extends Service {
    int count=0;
    int count2=0;
    int check=0;
    int check2=0;
    Calendar saveTime;
    Calendar outTime;
    String buildingName;
    long time1;
    long time2;
    private FirebaseFirestore firestore ;
    CheckLocationThread thread;
    public int counter = 0;
    NotificationCompat.Builder Notifi;
    NotificationManager Notifi_M;
    private GpsTracker gpsTracker;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) { //백그라운드에서 실행되는 동작들이 들어가는 곳
        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        firestore= FirebaseFirestore.getInstance();

        thread = new CheckLocationThread(handler);
        thread.start();
        return START_STICKY;
    }
    //서비스가 종료될 때 할 작업

    public void onDestroy() {
        thread.stopForever();
        thread = null;//쓰레기 값을 만들어서 빠르게 회수하라고 null을 넣어줌.
    }

    private class CheckLocationThread extends Thread {
        boolean isRun = true;
        Handler handler;

        public CheckLocationThread(Handler handler) {
            this.handler = handler;
        }

        public void stopForever() {
            synchronized (this) {
                this.isRun = false;
            }
        }

        public void run() {
            while (isRun) {
                handler.sendEmptyMessage(0);//쓰레드에 있는 핸들러에게 메세지를 보냄
                try {
                    Thread.sleep(5000); //5초씩 쉰다.
                } catch (Exception e) {
                    Toast.makeText(MyService.this, "예외발생 ", Toast.LENGTH_SHORT).show();
                }
            }


        }
    }

    class myServiceHandler extends Handler {

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void handleMessage(android.os.Message msg) {
            ArrayList<Building> buildinglist = new ArrayList<>();
            buildinglist.add(new Building("융복합관",35.88819,35.88785,128.61201,128.61142));
            buildinglist.add(new Building("공대9호관",35.88714,35.88670,128.60906,128.60791));
            buildinglist.add(new Building("IT 4호관",35.88873,35.88785,128.61129,128.61065));
            buildinglist.add(new Building("공대 12호관",35.88874,35.88823,128.61037,128.60928));
            buildinglist.add(new Building("희진이 언니 집",35.88573,35.88560,128.6109,128.610791));
            Intent intent = new Intent(MyService.this, MainActivity2.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationChannel channel = new NotificationChannel("channel", "play!!",
                    NotificationManager.IMPORTANCE_DEFAULT);
            // Notification과 채널 연걸
            NotificationManager mNotificationManager = ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE));
            mNotificationManager.createNotificationChannel(channel);

            // Notification 세팅
            Notifi = new NotificationCompat.Builder(getApplicationContext(), "channel")
                    .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                    .setContentTitle("위치 확인 중")
                    .setContentIntent(pendingIntent)
                    .setContentText("");

            // id 값은 0보다 큰 양수가 들어가야 한다.
            mNotificationManager.notify(1, Notifi.build());
            // foreground에서 시작
            startForeground(1, Notifi.build());




            if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),"not access",Toast.LENGTH_LONG).show();
            } else {
                gpsTracker = new GpsTracker(MyService.this);
                if (gpsTracker != null) {
                    double longitude = gpsTracker.getLongitude(); //128.~~
                    double latitude = gpsTracker.getLatitude(); //35.~~

                    Toast.makeText(getApplicationContext(), "경도 : " + longitude + "\n" + "위도 : " + latitude + "\n", Toast.LENGTH_SHORT).show();


                    if ((checkRangeIn(buildinglist, latitude, longitude))) { //(35.~~, 128.~~)

                        if (count == 0) {
                            Toast.makeText(MyService.this, "캘린더 초기", Toast.LENGTH_SHORT).show();
                            buildingName = judgeLocation(buildinglist, latitude, longitude);
                            saveTime = Calendar.getInstance();
                            count++;
                            time1 = System.currentTimeMillis();
                            check = 1;
                            check2 =0;
                        }

                        time2 = System.currentTimeMillis();
                        if ((((time2 - time1) / 1000.0) > 300) && (count2 == 0)) {
                            Toast.makeText(MyService.this, "10초 지", Toast.LENGTH_SHORT).show();
                            addInUser(buildingName, saveTime);
                            count2++;
                            check = 2;
                        }
                    }
                    if (((checkRangeIn(buildinglist, latitude, longitude)) == false)) {
                        if ((((time2 - time1) / 1000.0) <= 300)&&(check==1)&& (check2 == 0)){ //한번 들어갔지만 일정 시간 내에 나간 상태
                            outTime = Calendar.getInstance();
                            pushNotion();
                            check2++;
                        } else if(check==2) { //일정 시간 이상 건물 안에 들어갔다가 나간 상태
                            Toast.makeText(MyService.this, "범위에서 나감. ", Toast.LENGTH_SHORT).show();
                            outTime = Calendar.getInstance();
                            addOutUser();
                            check=0;
                        }

                        count = 0;
                        count2 = 0;
                        // onBackPressed();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "not access", Toast.LENGTH_SHORT).show();
                }
            }

        }

        public boolean checkRangeIn(ArrayList<Building> a,double latitude,double longitude){
            int max = a.size();
            for(int i = 0;i<max;i++) {
                if ((latitude < a.get(i).getRightlatitude()) && (latitude > a.get(i).getLeftlatitude()) && (longitude < a.get(i).getUppderlongitude() )&& (longitude > a.get(i).getBottomlongitude())){
                    return true;
                }
            }

            return false;
        }
    }
    public void addInUser(String buildingName, Calendar savetime ){ //15분 이상일 시 확인 누르면 정보 저장함.
        if(buildingName != null) {
            DocumentReference docRef = this.firestore.collection("users").document("asdf");
            Task<DocumentSnapshot> documentSnapshotTask = docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Toast.makeText(MyService.this, "addInUser안으로 들어옴 !!", Toast.LENGTH_SHORT).show();
                            Map<String, Object> space = new HashMap<>(); // 여기안에 정보 적으면 됨 .
                            space.put("place" + document.get("count").toString(), buildingName);
                            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String strDate = fm.format(savetime.getTime()).toString();
                            space.put("intime" + document.get("count").toString(), strDate);
                            Task<Void> voidTask = firestore.collection("users").document("asdf")
                                    .set(space, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(MyService.this, "장소와 들어가는 시간 저장완료", Toast.LENGTH_SHORT).show();
                                            // onBackPressed();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                        }
                                    });

                        } else {
                            Toast.makeText(MyService.this, " 실패", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MyService.this, "데이터가져오기 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(MyService.this, "빌딩 네임 널임", Toast.LENGTH_SHORT).show();
        }
    }
    public void addOutUser() {
        DocumentReference docRef = this.firestore.collection("users").document("asdf");
        Task<DocumentSnapshot> documentSnapshotTask = docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> space = new HashMap<>(); // 여기안에 정보 적으면 됨 .
                        Calendar saveTime = Calendar.getInstance();
                        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String strDate = fm.format(saveTime.getTime()).toString();
                        space.put("outtime"+document.get("count").toString(), strDate);
                        space.put("count",String.valueOf((parseInt(document.get("count").toString())+1)));
                        Task<Void> voidTask = firestore.collection("users").document("asdf")
                                .set(space, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(MyService.this, "나가는 시간이 저장되었습니다.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MyService.this, " 실패", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MyService.this, "데이터가져오기 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void pushNotion() { //push알람
        Intent intent = new Intent(MyService.this, NotyActivity.class);

        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = fm.format(saveTime.getTime());
        String strDate2 = fm.format(outTime.getTime());
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("saveTime", strDate);
        intent.putExtra("outTime", strDate2);
        intent.putExtra("buildingname", buildingName);
        PendingIntent mPendingIntent = PendingIntent.getActivity(MyService.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyService.this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.star_on)
                .setContentTitle(buildingName+" 방문 확인")
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
    public String judgeLocation(ArrayList<Building> a, double latitude, double longitude){ //(35.~ , 128.~ )
        int size = a.size();
        double rl; double ll; double ul; double bl;
        for(int i=0;i<size;i++)
        {
            ul = a.get(i).getUppderlongitude();
            bl = a.get(i).getBottomlongitude();
            rl = a.get(i).getRightlatitude();
            ll = a.get(i).getLeftlatitude();
            if((latitude>=ll)&&(latitude<=rl)&&(longitude<=ul)&&(longitude>=bl))
            {
                return a.get(i).getName();
            }
        }
        return null;
    }
}
