package org.properti.analisa.financialcheck.activity.auth;

import android.app.ProgressDialog;
import android.content.Intent;
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

        ButterKnife.bind(this);
        loading = DialogUtils.showProgressDialog(this, "Loading", "Registering your account");

        mAuth = FirebaseAuth.getInstance();
    }

    @OnClick(R.id.btn_register)
    public void register(){
        if(TextUtils.isEmpty(etNama.getText().toString()) || TextUtils.isEmpty(etEmail.getText().toString()) || TextUtils.isEmpty(etPhoneNumber.getText().toString()) || TextUtils.isEmpty(etPassword.getText().toString())){
            Toast.makeText(this, getString(R.string.data_belum_lengkap), Toast.LENGTH_SHORT).show();
        }
        else{
            nama = etNama.getText().toString();
            email = etEmail.getText().toString();
            phone = etPhoneNumber.getText().toString();
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
                        }
                    }
                });

        loading.dismiss();

    }

    private void initData() {
        listSpendingMonth.add(new Common( "Makan Dalam Rumah", "0", ""));
        listSpendingMonth.add(new Common( "Listrik Gas Air", "0", ""));
        listSpendingMonth.add(new Common( "Telepon Rumah", "0", ""));
        listSpendingMonth.add(new Common( "Telepon HP", "0", ""));
        listSpendingMonth.add(new Common( "Sekolah + Les Anak", "0", ""));
        listSpendingMonth.add(new Common( "Cicilan Hutang Rumah", "0", ""));
        listSpendingMonth.add(new Common( "Cicilan Kendaraan", "0", ""));
        listSpendingMonth.add(new Common( "Cicilan Kartu Kredit", "0", ""));
        listSpendingMonth.add(new Common( "Asuransi", "0", ""));
        listSpendingMonth.add(new Common( "Pembantu", "0", ""));
        listSpendingMonth.add(new Common( "Mobil (Bensin dan Maintenance)", "0", ""));
        listSpendingMonth.add(new Common( "Pakaian", "0", ""));

        listSpending.add(new Common( "Makan Luar Rumah", "0", ""));
        listSpending.add(new Common( "Beli Luxury", "0", ""));
        listSpending.add(new Common( "Piknik", "0", ""));

        listPassiveIncome.add(new Common( "Rumah Sewa / Kos", "0", ""));
        listPassiveIncome.add(new Common( "Usaha", "0", ""));
        listPassiveIncome.add(new Common( "Deposito / Reksadana", "0", ""));
        listPassiveIncome.add(new Common( "Royalti Buku", "0", ""));
        listPassiveIncome.add(new Common( "Royalti Kaset", "0", ""));
        listPassiveIncome.add(new Common( "Royalti Sistem", "0", ""));

        listActiveIncome.add(new Common( "Profesi", "0", ""));
        listActiveIncome.add(new Common( "Trading", "0", ""));
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