package org.properti.analisa.financialcheck.activity.common;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

import org.properti.analisa.financialcheck.R;
import org.properti.analisa.financialcheck.adapter.PassiveIncomeAdapter;
import org.properti.analisa.financialcheck.model.Common;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.properti.analisa.financialcheck.activity.MainActivity.UID_USER;

public class PassiveIncomeActivity extends AppCompatActivity {

    private final LinkedList<Common> listMenu = new LinkedList<>();
    String idUser;

    @BindView(R.id.txt_jumlah_transaksi)
    TextView txtJumlahTransaksi;
    @BindView(R.id.txt_total_biaya)
    TextView txtTotalBiaya;

    DatabaseReference dbPassiveIncome;

    private RecyclerView mRecyclerView;
    private PassiveIncomeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passive_income);
        ButterKnife.bind(this);
        toolbar();

        idUser = getIntent().getStringExtra(UID_USER);
        dbPassiveIncome = FirebaseDatabase.getInstance().getReference("passive_income").child(idUser);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_passive_income);
        mAdapter = new PassiveIncomeAdapter(PassiveIncomeActivity.this, listMenu, idUser);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        dbPassiveIncome.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listMenu.clear();
                long total = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Common passiveIncome = postSnapshot.getValue(Common.class);
                    listMenu.add(passiveIncome);
                    total = total + Long.parseLong(passiveIncome.getHarga());
                }
                txtJumlahTransaksi.setText(listMenu.size()+" Transaksi");
                txtTotalBiaya.setText(""+total);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void toolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar); //Inisialisasi dan Implementasi id Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Passive Income");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
