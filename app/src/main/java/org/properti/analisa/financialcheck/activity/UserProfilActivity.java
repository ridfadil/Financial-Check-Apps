package org.properti.analisa.financialcheck.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.properti.analisa.financialcheck.R;
import org.properti.analisa.financialcheck.model.Common;
import org.properti.analisa.financialcheck.model.User;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.properti.analisa.financialcheck.activity.MainActivity.UID_USER;

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
    DatabaseReference dbUser;
    String idUser;
    private final LinkedList<User> listMenu = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profil);
        ButterKnife.bind(this);
        //toolbar();
        idUser = getIntent().getStringExtra(UID_USER);
        dbUser = FirebaseDatabase.getInstance().getReference("users").child(idUser);

        String nama = (listMenu.get(0).getNama());
        String noTelp = (listMenu.get(0).getPhone());
        String email = (listMenu.get(0).getEmail());

        tvProfilNamaUser.setText(nama);
        tvProfilNoHp.setText(noTelp);
        tvProfilEmail.setText(email);

    }

    @Override
    protected void onStart() {
        super.onStart();

        dbUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               listMenu.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User userProfile = postSnapshot.getValue(User.class);
                    listMenu.add(userProfile);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
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
