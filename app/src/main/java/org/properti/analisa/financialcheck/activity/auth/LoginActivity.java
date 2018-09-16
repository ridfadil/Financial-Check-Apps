package org.properti.analisa.financialcheck.activity.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;

import org.properti.analisa.financialcheck.R;
import org.properti.analisa.financialcheck.activity.utils.DialogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_login_facebook)
    LoginButton btnLoginFacebook;

    String email, password;

    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        loading = DialogUtils.showProgressDialog(this, "Loading", "Checking Data");
    }

    @OnClick(R.id.btn_login)
    public void login(){
        loading.show();
        if(TextUtils.isEmpty(etEmail.getText().toString()) || TextUtils.isEmpty(etPassword.getText().toString())){
            Toast.makeText(this, getString(R.string.data_kosong), Toast.LENGTH_SHORT).show();
            loading.dismiss();
        }
        else{
            email = etEmail.getText().toString();
            password = etPassword.getText().toString();
//            checkLogin(email, password);
            Toast.makeText(this, "Login Success"+email+" - "+password, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_to_register)
    public void toRegister(){
        Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @OnClick(R.id.btn_to_forgot_password)
    public void toForgotPassword(){
        startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}
