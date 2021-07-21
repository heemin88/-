package com.example.narae;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import static java.lang.Short.valueOf;

public class MainActivity2 extends AppCompatActivity {
    static final int PERMISSIONS_REQUEST = 0x0000001;
    private String name;
    private String sid;
    private String major;
    private Button button1;
    private int number;
    private int number1=1;
    private String pplace;
    private String In;
    private String Out;

    private ListView listView;
    private ListViewAdapter listViewAdapter;

    Intent serviceIntent;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        TextView textView_name = (TextView) findViewById(R.id.textView11) ;
        TextView textView_id = (TextView) findViewById(R.id.textView12) ;
        TextView textView_major = (TextView) findViewById(R.id.textView13) ;
        listView = (ListView) findViewById(R.id.listview);
        listViewAdapter = new ListViewAdapter();
        button1 = findViewById(R.id.button);
        final FirebaseFirestore db= FirebaseFirestore.getInstance();

        serviceIntent = new Intent(MainActivity2.this,MyService.class);
        startService(serviceIntent);
        ArrayList<Integer> arr = new ArrayList<Integer>();


        DocumentReference docRef = db.collection("users").document("asdf"); //정보 가져와서 나타내
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            name = document.get("name").toString();
                            sid = document.get("학번").toString();
                            major = document.get("전공").toString();
                            textView_id.setText(sid);
                            textView_name.setText(name);
                            textView_major.setText(major);
                            listView.setAdapter(listViewAdapter);
                            number = valueOf(document.get("count").toString());

                            //Out=("12:00");

                            for (int i = 0; i < number; i++) {
                                pplace = document.get("place" + (i)).toString();
                                In = document.get("intime" + (i)).toString();
                                Out = document.get("outtime" + (i)).toString();
                                listViewAdapter.addItem(pplace, In, Out);
                            }
                            listView.setAdapter(listViewAdapter);
                        }
                    }
                }
        });
        OnCheckPermission();
        button1.setOnClickListener(new View.OnClickListener(){ //로그아
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this,MyService.class);
                stopService(intent);
                Intent intent2 = new Intent(MainActivity2.this, LoginActivity.class);
                startActivity(intent2);
                SharedPreferences auto = getSharedPreferences("loginInformation", 0);
                SharedPreferences.Editor editor = auto.edit();
                //editor.clear()는 auto에 들어있는 모든 정보를 기기에서 지웁니다.
                editor.clear();
                editor.commit();
                Toast.makeText(MainActivity2.this, "로그아웃.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }
    public void OnCheckPermission() {
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);


        if (permissionCheck2 != PackageManager.PERMISSION_GRANTED) { //백그라운드 위치 권한 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                Toast.makeText(this,"앱 실행을 위해서는 권한을 설정해야 합니다!",Toast.LENGTH_LONG).show();
                //ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION/*,Manifest.permission.ACCESS_BACKGROUND_LOCATION*/},PERMISSIONS_REQUEST);
                setting();
            } else {
                //위치 권한 요청
                Toast.makeText(this, "앱 실행을 위해 위치접근 항상 허용을 눌러주세요.", Toast.LENGTH_LONG).show();
                //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION/*,Manifest.permission.ACCESS_BACKGROUND_LOCATION*/}, PERMISSIONS_REQUEST);
                setting();
            }

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceIntent!=null) {
            stopService(serviceIntent);
            serviceIntent = null;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "앱 실행을 위한 권한이 설정 되었습니다.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "앱 실행을 위한 권한이 없습니다. 위치 접근을 허용해주세요.", Toast.LENGTH_LONG).show();
                    setting();
                }
                break;
        }
    }

    public void setting() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 0:
                // 할일 작성
                OnCheckPermission();
                break;

        }

        super.onActivityResult(requestCode, resultCode, data);

    }

}
