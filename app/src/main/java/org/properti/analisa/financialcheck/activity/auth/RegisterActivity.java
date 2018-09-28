package org.properti.analisa.financialcheck.activity.auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.properti.analisa.financialcheck.R;
import org.properti.analisa.financialcheck.activity.MainActivity;
import org.properti.analisa.financialcheck.utils.DialogUtils;
import org.properti.analisa.financialcheck.model.Common;
import org.properti.analisa.financialcheck.model.User;
import org.properti.analisa.financialcheck.utils.LocalizationUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.et_nama)
    EditText etNama;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_no_hp)
    EditText etPhoneNumber;
    @BindView(R.id.et_password)
    EditText etPassword;

    String nama, email, phone, password;

    List<Common> listSpendingMonth = new ArrayList<>();
    List<Common> listSpending = new ArrayList<>();
    List<Common> listPassiveIncome = new ArrayList<>();
    List<Common> listActiveIncome = new ArrayList<>();

    private ProgressDialog loading;

    private FirebaseAuth mAuth;

    DatabaseReference databaseUser, databaseSpendingMonth, databaseSpending, databasePassiveIncome, databaseActiveIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        SharedPreferences pref = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        LocalizationUtils.setLocale(pref.getString("language", ""), getBaseContext());

        ButterKnife.bind(this);
        loading = DialogUtils.showProgressDialog(this, "Loading", "Registering your account");

        mAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.btn_register)
    public void register(){
        if(TextUtils.isEmpty(etNama.getText().toString()) || TextUtils.isEmpty(etEmail.getText().toString()) || TextUtils.isEmpty(etPassword.getText().toString())){
            Toast.makeText(this, getString(R.string.data_belum_lengkap), Toast.LENGTH_SHORT).show();
        }
        else{
            nama = etNama.getText().toString();
            email = etEmail.getText().toString();
            phone = "";
            password = etPassword.getText().toString();
            registerData(nama, email, phone, password);
        }
    }

    private void registerData(final String nama, final String email, final String phone, String password) {
        loading.show();
        mAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String id = user.getUid();

                            databaseUser = FirebaseDatabase.getInstance().getReference("users");
                            databaseUser.child(id).setValue(new User(nama, email, phone));

                            initData();

                            databaseSpendingMonth = FirebaseDatabase.getInstance().getReference("spending_month").child(id);
                            databaseSpending = FirebaseDatabase.getInstance().getReference("spending").child(id);
                            databasePassiveIncome = FirebaseDatabase.getInstance().getReference("passive_income").child(id);
                            databaseActiveIncome = FirebaseDatabase.getInstance().getReference("active_income").child(id);

                            int i;
                            for(i=0; i<listSpendingMonth.size(); i++){
                                String idSpendingMonth = databaseSpendingMonth.push().getKey();
                                listSpendingMonth.get(i).setId(idSpendingMonth);
                                databaseSpendingMonth.child(idSpendingMonth).setValue(listSpendingMonth.get(i));
                            }

                            for(i=0; i<listSpending.size(); i++){
                                String idSpending = databaseSpending.push().getKey();
                                listSpending.get(i).setId(idSpending);
                                databaseSpending.child(idSpending).setValue(listSpending.get(i));
                            }

                            for(i=0; i<listPassiveIncome.size(); i++){
                                String idPassive = databasePassiveIncome.push().getKey();
                                listPassiveIncome.get(i).setId(idPassive);
                                databasePassiveIncome.child(idPassive).setValue(listPassiveIncome.get(i));
                            }

                            for(i=0; i<listActiveIncome.size(); i++){
                                String idActive = databaseActiveIncome.push().getKey();
                                listActiveIncome.get(i).setId(idActive);
                                databaseActiveIncome.child(idActive).setValue(listActiveIncome.get(i));
                            }

                            Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                            loading.dismiss();
                        }
                    }
                });

    }

    private void initData() {
        final String baseImage = "gs://test-financial.appspot.com/icon/";

        listSpendingMonth.add(new Common( "Eating at home", "0", baseImage+"profesi.PNG"));
        listSpendingMonth.add(new Common( "Electricity, Gas, Water", "0", baseImage+"listrikgas.PNG"));
        listSpendingMonth.add(new Common( "House's Phone", "0", baseImage+"belihandphone.PNG"));
        listSpendingMonth.add(new Common( "Phone, mobile phone", "0", baseImage+"belihandphone.PNG"));
        listSpendingMonth.add(new Common( "School / Children's course", "0", baseImage+"pengeluaranbulanan.PNG"));
        listSpendingMonth.add(new Common( "House's Instalment Debt", "0", baseImage+"rumahsewa.PNG"));
        listSpendingMonth.add(new Common( "Transportation's Instalment", "0", baseImage+"servicemobil.PNG"));
        listSpendingMonth.add(new Common( "Credit Card's Instalment", "0", baseImage+"deposito.PNG"));
        listSpendingMonth.add(new Common( "Insurance", "0", baseImage+"pengeluaranbulanan.PNG"));
        listSpendingMonth.add(new Common( "Servant", "0", baseImage+"pengeluaranlainnya.PNG"));
        listSpendingMonth.add(new Common( "Car (Maintenance and Gasoline)", "0", baseImage+"servicemobil.PNG"));
        listSpendingMonth.add(new Common( "Clothes", "0", baseImage+"pengeluaranlainnya.PNG"));

        listSpending.add(new Common( "Eating Outside's House", "0", baseImage+"profesi.PNG"));
        listSpending.add(new Common( "Luxurious Buying", "0", baseImage+"aktifincome.PNG"));
        listSpending.add(new Common( "Picnic", "0", baseImage+"rekreasi.PNG"));

        listPassiveIncome.add(new Common( "House's rent / Kost", "0", baseImage+"rumahsewa.PNG"));
        listPassiveIncome.add(new Common( "Business", "0", baseImage+"usaha.PNG"));
        listPassiveIncome.add(new Common( "Deposit / MutualFund", "0", baseImage+"deposito.PNG"));
        listPassiveIncome.add(new Common( "Book Royalties", "0", baseImage+"pasifincome.PNG"));
        listPassiveIncome.add(new Common( "Cassete Royalties", "0", baseImage+"lainlain.PNG"));
        listPassiveIncome.add(new Common( "Royaties' System", "0", baseImage+"pengeluaranbulanan.PNG"));

        listActiveIncome.add(new Common( "Occupation", "0", baseImage+"usaha.PNG"));
        listActiveIncome.add(new Common( "Trading", "0", baseImage+"trading.PNG"));
    }

    @OnClick(R.id.btn_to_login)
    public void toLogin(){
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}