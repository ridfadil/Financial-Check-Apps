package org.properti.analisa.financialcheck.activity.common;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

import org.properti.analisa.financialcheck.R;
import org.properti.analisa.financialcheck.adapter.MonthlySpendingAdapter;
import org.properti.analisa.financialcheck.model.Common;
import org.properti.analisa.financialcheck.utils.CurrencyEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.properti.analisa.financialcheck.activity.MainActivity.UID_USER;

public class SpendingMonthActivity extends AppCompatActivity {

    private final LinkedList<Common> listMenu = new LinkedList<>();
    String idUser;

    @BindView(R.id.txt_jumlah_transaksi)
    TextView txtJumlahTransaksi;
    @BindView(R.id.txt_total_biaya)
    TextView txtTotalBiaya;

    @BindView(R.id.ad_bottom)
    AdView bottomAds;

    DatabaseReference dbSpendingMonth;

    private RecyclerView mRecyclerView;
    private MonthlySpendingAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spending_moth);

        ButterKnife.bind(this);
        toolbar();

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        bottomAds.loadAd(adRequest);

        idUser = getIntent().getStringExtra(UID_USER);
        dbSpendingMonth = FirebaseDatabase.getInstance().getReference("spending_month").child(idUser);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_month_spending);
        mAdapter = new MonthlySpendingAdapter(SpendingMonthActivity.this, listMenu, idUser);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        dbSpendingMonth.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listMenu.clear();
                long total = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Common spendingMonth = postSnapshot.getValue(Common.class);
                    listMenu.add(spendingMonth);
                    total = total + Long.parseLong(spendingMonth.getHarga());
                }
                txtJumlahTransaksi.setText(listMenu.size()+" "+getString(R.string.transaksi));
                txtTotalBiaya.setText(CurrencyEditText.currencyFormatterLong(total));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @OnClick({R.id.btn_next, R.id.btn_back})
    public void onViewClicked(View v){
        switch (v.getId()){
            case R.id.btn_back : {
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            }
            case R.id.btn_next : {
                Intent i = new Intent(SpendingMonthActivity.this, SpendingActivity.class);
                i.putExtra(UID_USER, idUser);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            }
        }
    }

    public void toolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar); //Inisialisasi dan Implementasi id Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.monthly_spending));
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        return super.onOptionsItemSelected(item);
    }
}
