package com.example.narae;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity {
    static final int PERMISSIONS_REQUEST = 0x0000001;

    private String name;
    private String sid;
    private String major;
    private Button button1;
    //private Intent serviceIntent=new Intent(getApplicationContext(),BackGPS.class);

    private ListView listView;
    private ListViewAdapter listViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        listView = (ListView) findViewById(R.id.listview);
        listViewAdapter = new ListViewAdapter();

        OnCheckPermission();

        listViewAdapter.addItem("융복학", "2021-7-19-10:00", "2021-7-19-12:00");
        listViewAdapter.addItem("공대9호관", "2021-7-20-12:00", "2021-7-22-11:00");

        for (int i = 0; i < 3; i++) {
            listViewAdapter.addItem(i + "-", i + "ㅇ", "ㄱ");
        }
        listView.setAdapter(listViewAdapter);

    }

    public void OnCheckPermission() {
        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);


        if (permissionCheck2 != PackageManager.PERMISSION_GRANTED) { //백그라운드 위치 권한 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                //위치 권한 요청
                Toast.makeText(this, "앱 실행을 위해서는 권한을 설정해야 합니다!", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, PERMISSIONS_REQUEST);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "앱 실행을 위한 권한이 설정 되었습니다.", Toast.LENGTH_LONG).show();
                    //startService(serviceIntent);
                } else {
                    Toast.makeText(this, "앱 실행을 위한 권한이 없습니다. 위치 접근을 허용해주세요.", Toast.LENGTH_LONG).show();
                    //stopService(serviceIntent);
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