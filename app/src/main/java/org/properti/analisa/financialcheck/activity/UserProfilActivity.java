package org.properti.analisa.financialcheck.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.properti.analisa.financialcheck.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserProfilActivity extends AppCompatActivity {

    @BindView(R.id.iv_foto_profil)
    ImageView ivFotoProfil;
    @BindView(R.id.tv_profil_nama_user)
    TextView tvProfilNamaUser;
    @BindView(R.id.tv_profil_no_hp)
    TextView tvProfilNoHp;
    @BindView(R.id.tv_profil_email)
    TextView tvProfilEmail;
    @BindView(R.id.btn_edit_profil)
    Button btnEditProfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profil);
        ButterKnife.bind(this);
        toolbar();
    }

    @OnClick(R.id.btn_edit_profil)
    public void onViewClicked() {
        Intent i = new Intent(UserProfilActivity.this,EditProfileActivity.class);
        startActivity(i);
    }

    public void toolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar); //Inisialisasi dan Implementasi id Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("User Profile");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
