package org.properti.analisa.financialcheck.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import org.properti.analisa.financialcheck.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditProfileActivity extends AppCompatActivity {

    @BindView(R.id.et_nama)
    EditText etNama;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_no_hp)
    EditText etNoHp;
    @BindView(R.id.et_password_lama)
    EditText etPasswordLama;
    @BindView(R.id.et_password_baru)
    EditText etPasswordBaru;
    @BindView(R.id.et_password_baru_2)
    EditText etPasswordBaru2;
    @BindView(R.id.btn_simpan)
    Button btnSimpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_simpan)
    public void onViewClicked() {
    }
}
