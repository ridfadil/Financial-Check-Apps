package org.properti.analisa.financialcheck.activity.auth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.properti.analisa.financialcheck.R;
import org.properti.analisa.financialcheck.activity.utils.DialogUtils;
import org.properti.analisa.financialcheck.firebase.FirebaseApplication;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends AppCompatActivity {

    @BindView(R.id.et_email)
    EditText etEmail;

    private ProgressDialog loading;

    private FirebaseAuth mAuth;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = ((FirebaseApplication)getApplication()).getFirebaseAuth();

        ButterKnife.bind(this);
        loading = DialogUtils.showProgressDialog(this, "Loading", "Reseting your password");
    }

    @OnClick(R.id.btn_kirim)
    public void resetPassword(){
        loading.show();
        if(TextUtils.isEmpty(etEmail.getText().toString())){
            Toast.makeText(getApplicationContext(), getString(R.string.data_belum_lengkap), Toast.LENGTH_SHORT).show();
            loading.dismiss();
        }
        else{
            new AlertDialog.Builder(ForgotPasswordActivity.this)
                    .setTitle(getString(R.string.yakin_ingin_reset_password))
                    .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            email = etEmail.getText().toString();
                            ((FirebaseApplication)getApplication()).resetPassword(ForgotPasswordActivity.this, email);
                            loading.dismiss();
                        }
                    })
                    .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    @OnClick(R.id.btn_to_login)
    public void toLogin(){
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
