package org.properti.analisa.financialcheck.activity.profile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.properti.analisa.financialcheck.R;
import org.properti.analisa.financialcheck.model.User;
import org.properti.analisa.financialcheck.utils.LocalizationUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.properti.analisa.financialcheck.activity.MainActivity.UID_USER;

public class UserProfilActivity extends AppCompatActivity {

    @BindView(R.id.tv_profil_nama_user)
    TextView tvProfilNamaUser;
    @BindView(R.id.tv_profil_no_hp)
    TextView tvProfilNoHp;
    @BindView(R.id.tv_profil_email)
    TextView tvProfilEmail;
    @BindView(R.id.btn_edit_profil)
    Button btnEditProfil;

    DatabaseReference dbUser;
    User user;
    String id, nama, email, noHp, passwordBaru;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        LocalizationUtils.setLocale(pref.getString("language", ""), getBaseContext());
        setContentView(R.layout.activity_user_profil);
        ButterKnife.bind(this);
        toolbar();

        id = getIntent().getStringExtra(UID_USER);
        dbUser = FirebaseDatabase.getInstance().getReference("users").child(id);
    }

    public void showInputDialog(View view) {

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog_profile, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);

        final EditText etNama = promptView.findViewById(R.id.et_nama);
        final EditText etEmail = promptView.findViewById(R.id.et_email);
        final EditText etNoHp = promptView.findViewById(R.id.et_no_hp);
        final EditText etPasswordBaru = promptView.findViewById(R.id.et_password_baru);

        dbUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                etNama.setText(user.getNama());
                etEmail.setText(user.getEmail());
                if(user.getEmail().equalsIgnoreCase("")){
                    etNoHp.setText("-");
                }
                else{
                    etNoHp.setText(user.getPhone());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(getString(R.string.simpan), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if(!TextUtils.isEmpty(etNama.getText().toString())){
                            nama = etNama.getText().toString();
                        }
                        else{
                            nama = "";
                        }
                        if(!TextUtils.isEmpty(etEmail.getText().toString())){
                            email = etEmail.getText().toString();
                            user.updateEmail(email);
                        }
                        else{
                            Toast.makeText(UserProfilActivity.this, R.string.email_is_empty, Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                        if(!TextUtils.isEmpty(etNoHp.getText().toString())){
                            noHp = etNoHp.getText().toString();
                        }
                        else{
                            noHp = "";
                        }
                        dbUser.setValue(new User(nama, email, noHp));
                        if(!TextUtils.isEmpty(etPasswordBaru.getText().toString())){
                            passwordBaru = etPasswordBaru.getText().toString();
                            user.updatePassword(passwordBaru.trim());
                        }

                        tvProfilNamaUser.setText(nama);
                        tvProfilEmail.setText(email);
                        tvProfilNoHp.setText(noHp);
                    }
                })
                .setNegativeButton(getString(R.string.batal),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        dbUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);

                tvProfilNamaUser.setText(user.getNama());
                tvProfilEmail.setText(user.getEmail());
//                if(user.getPhone().equalsIgnoreCase("")){
//                    tvProfilNoHp.setText("-");
//                }
//                else{
//                    tvProfilNoHp.setText(user.getPhone());
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.btn_edit_profil)
    public void onViewClicked(View v) {
        showInputDialog(v);
    }

    public void toolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.user_profil));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
