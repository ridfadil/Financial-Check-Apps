package org.properti.analisa.financialcheck.activity.auth;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.properti.analisa.financialcheck.R;
import org.properti.analisa.financialcheck.activity.utils.DialogUtils;
import org.properti.analisa.financialcheck.firebase.FirebaseApplication;
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

        mAuth = ((FirebaseApplication)getApplication()).getFirebaseAuth();
    }

    @OnClick(R.id.btn_register)
    public void register(){
        loading.show();
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

    private void registerData(String nama, String email, String phone, String password) {
        ((FirebaseApplication)getApplication()).createNewUser(this, email, password);

        initData();

        databaseUser = FirebaseDatabase.getInstance().getReference("users");

        String id = databaseUser.push().getKey();
        String userId = nama+"-"+String.valueOf(Math.random());

        databaseUser.child(id).setValue(new User(userId, nama, email, phone));

        databaseSpendingMonth = FirebaseDatabase.getInstance().getReference("spending_month").child(id);
        databaseSpending = FirebaseDatabase.getInstance().getReference("spending").child(id);
        databasePassiveIncome = FirebaseDatabase.getInstance().getReference("passive_income").child(id);
        databaseActiveIncome = FirebaseDatabase.getInstance().getReference("active_income").child(id);

        int i;
        for(i=0; i<listSpendingMonth.size(); i++){
            String idSpendingMonth = databaseSpendingMonth.push().getKey();
            databaseSpendingMonth.child(idSpendingMonth).setValue(listSpendingMonth.get(i));
        }

        for(i=0; i<listSpending.size(); i++){
            String idSpending = databaseSpending.push().getKey();
            databaseSpending.child(idSpending).setValue(listSpending.get(i));
        }

        for(i=0; i<listPassiveIncome.size(); i++){
            String idPassive = databasePassiveIncome.push().getKey();
            databasePassiveIncome.child(idPassive).setValue(listPassiveIncome.get(i));
        }

        for(i=0; i<listActiveIncome.size(); i++){
            String idActive = databaseActiveIncome.push().getKey();
            databaseActiveIncome.child(idActive).setValue(listActiveIncome.get(i));
        }

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
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}