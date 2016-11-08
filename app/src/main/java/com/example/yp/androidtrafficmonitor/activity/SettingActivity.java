package com.example.yp.androidtrafficmonitor.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.yp.androidtrafficmonitor.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yp on 2016/9/25.
 */
public class SettingActivity extends AppCompatActivity {

    @Bind(R.id.backBtn4)
    Button backBtn4;
    @Bind(R.id.spinner)
    Spinner spinner;
    @Bind(R.id.alarmTrafficET)
    EditText alarmTrafficET;
    @Bind(R.id.genTrafficET)
    EditText genTrafficET;
    @Bind(R.id.freeTrafficET)
    EditText freeTrafficET;
    @Bind(R.id.surBtn)
    Button surBtn;
    @Bind(R.id.monthDayET)
    EditText monthDayET;
    //private String []operator = {"中国移动","中国联通","中国电信"};
    private ArrayList<String> arrayList;
    private ArrayAdapter arrayAdapter;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private String st;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parameter_setting_layout);
        ButterKnife.bind(this);
        sp = getSharedPreferences("settingInfor", Context.MODE_PRIVATE);
        initView();
        arrayList = new ArrayList<String>();
        arrayList.add("中国移动");
        arrayList.add("中国联通");
        arrayList.add("中国电信");
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                st = arrayList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    void initView(){
        monthDayET.setText(""+sp.getInt("月节日",1));
        alarmTrafficET.setText(""+sp.getInt("流量警戒值",100));
        genTrafficET.setText(""+sp.getLong("通用流量",0)/1024/1024);
        freeTrafficET.setText(""+sp.getInt("闲时流量",0));
    }

    void saveSettingInfor() {
        int mothday = Integer.parseInt(monthDayET.getText().toString().trim());
        int alarm = Integer.parseInt(alarmTrafficET.getText().toString().trim());
        int free = Integer.parseInt(freeTrafficET.getText().toString().trim());

        //Log.v("seeting", "" + mothday + " " + alarm + " " + free);
        editor = sp.edit();
        editor.putString("运营商", st);
        editor.putInt("月结日", mothday);
        editor.putInt("流量警戒值", alarm);
        editor.putLong("通用流量", 1024 * 1024 * Long.valueOf(genTrafficET.getText().toString().trim()));
        editor.putInt("闲时流量", free);
        editor.commit();
        Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
    }

    @OnClick({R.id.backBtn4, R.id.surBtn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.backBtn4:
                this.finish();
                break;
            case R.id.surBtn:
                saveSettingInfor();
                break;
        }
    }
}
