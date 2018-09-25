package org.properti.analisa.financialcheck.activity.setting;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import org.properti.analisa.financialcheck.R;
import org.properti.analisa.financialcheck.utils.LocalizationUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.txt_tentang)
    TextView txtTentang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        LocalizationUtils.setLocale(pref.getString("language", ""), getBaseContext());
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        toolbar();

        txtTentang.setText(Html.fromHtml(getString(R.string.tentang)));
    }

    public void toolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar); //Inisialisasi dan Implementasi id Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.tentang_kami));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
