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
            Toast.makeText(this, "Register berhasil !", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerData(String nama, String email, String phone, String password) {
        FirebaseApplication.createNewUser(this, email, password, mAuth);

        databaseUser = FirebaseDatabase.getInstance().getReference("users");

        String id = databaseUser.push().getKey();
        String userId = nama+"-"+String.valueOf(Math.random());

        databaseUser.child(id).setValue(new User(id, userId, nama, email, phone));

        databaseSpendingMonth = FirebaseDatabase.getInstance().getReference("spending_month").child(id);
        databaseSpending = FirebaseDatabase.getInstance().getReference("spending").child(id);
        databasePassiveIncome = FirebaseDatabase.getInstance().getReference("passive_income").child(id);
        databaseActiveIncome = FirebaseDatabase.getInstance().getReference("active_income").child(id);

        databaseSpendingMonth.setValue(new Common( "Makan Dalam Rumah", "0", ""));
        databaseSpendingMonth.setValue(new Common( "Listrik Gas Air", "0", ""));
        databaseSpendingMonth.setValue(new Common( "Telepon Rumah", "0", ""));
        databaseSpendingMonth.setValue(new Common( "Telepon HP", "0", ""));
        databaseSpendingMonth.setValue(new Common( "Sekolah + Les Anak", "0", ""));
        databaseSpendingMonth.setValue(new Common( "Cicilan Hutang Rumah", "0", ""));
        databaseSpendingMonth.setValue(new Common( "Cicilan Kendaraan", "0", ""));
        databaseSpendingMonth.setValue(new Common( "Cicilan Kartu Kredit", "0", ""));
        databaseSpendingMonth.setValue(new Common( "Asuransi", "0", ""));
        databaseSpendingMonth.setValue(new Common( "Pembantu", "0", ""));
        databaseSpendingMonth.setValue(new Common( "Mobil (Bensin dan Maintenance)", "0", ""));
        databaseSpendingMonth.setValue(new Common( "Pakaian", "0", ""));

        databaseSpending.setValue(new Common( "Makan Luar Rumah", "0", ""));
        databaseSpending.setValue(new Common( "Beli Luxury", "0", ""));
        databaseSpending.setValue(new Common( "Piknik", "0", ""));

        databasePassiveIncome.setValue(new Common( "Rumah Sewa / Kos", "0", ""));
        databasePassiveIncome.setValue(new Common( "Usaha", "0", ""));
        databasePassiveIncome.setValue(new Common( "Deposito / Reksadana", "0", ""));
        databasePassiveIncome.setValue(new Common( "Royalti Buku", "0", ""));
        databasePassiveIncome.setValue(new Common( "Royalti Kaset", "0", ""));
        databasePassiveIncome.setValue(new Common( "Royalti Sistem", "0", ""));

        databaseActiveIncome.setValue(new Common( "Profesi", "0", ""));
        databaseActiveIncome.setValue(new Common( "Trading", "0", ""));

        loading.dismiss();

        //TODO: masukkin data dalem list, terus looping masukkin ke dalem firebase
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
