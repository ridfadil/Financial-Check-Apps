package org.properti.analisa.financialcheck.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.properti.analisa.financialcheck.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {

    @BindView(R.id.tv_setting_bahasa)
    TextView tvSettingBahasa;
    @BindView(R.id.tv_setting_tentang)
    TextView tvSettingTentang;
    @BindView(R.id.tv_setting_bantuan)
    TextView tvSettingBantuan;
    @BindView(R.id.tv_like)
    TextView tvLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        toolbar();
    }

    @OnClick({R.id.tv_setting_bahasa, R.id.tv_setting_tentang, R.id.tv_setting_bantuan, R.id.tv_like})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_setting_bahasa:
                break;
            case R.id.tv_setting_tentang:
                break;
            case R.id.tv_setting_bantuan:
                break;
            case R.id.tv_like:
                break;
        }
    }

    public void toolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar); //Inisialisasi dan Implementasi id Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Setting");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
