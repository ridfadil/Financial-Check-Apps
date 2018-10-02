package org.properti.analisa.financialcheck.activity.common;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.properti.analisa.financialcheck.R;
import org.properti.analisa.financialcheck.adapter.PassiveIncomeAdapter;
import org.properti.analisa.financialcheck.model.Common;
import org.properti.analisa.financialcheck.utils.CurrencyEditText;
import org.properti.analisa.financialcheck.utils.LocalizationUtils;

import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.properti.analisa.financialcheck.activity.MainActivity.UID_USER;

public class PassiveIncomeActivity extends AppCompatActivity {

    private final LinkedList<Common> listMenu = new LinkedList<>();
    String idUser;

    @BindView(R.id.txt_jumlah_transaksi)
    TextView txtJumlahTransaksi;
    @BindView(R.id.txt_total_biaya)
    TextView txtTotalBiaya;

    @BindView(R.id.ad_bottom)
    AdView bottomAds;

    DatabaseReference dbPassiveIncome;

    private RecyclerView mRecyclerView;
    private PassiveIncomeAdapter mAdapter;

    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passive_income);
        SharedPreferences pref = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        LocalizationUtils.setLocale(pref.getString("language", ""), getBaseContext());
        ButterKnife.bind(this);
        toolbar();

        initAd();

        idUser = getIntent().getStringExtra(UID_USER);
        dbPassiveIncome = FirebaseDatabase.getInstance().getReference("passive_income").child(idUser);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_passive_income);
        mAdapter = new PassiveIncomeAdapter(PassiveIncomeActivity.this, listMenu, idUser);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initAd() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        bottomAds.loadAd(adRequest);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getString(R.string.ad_id_interstitial));
        interstitialAd.loadAd(new AdRequest.Builder().build());
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Intent i = new Intent(PassiveIncomeActivity.this, SpendingActivity.class);
                i.putExtra(UID_USER, idUser);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        });
    }

    @OnClick({R.id.btn_next, R.id.btn_back})
    public void onViewClicked(View v){
        switch (v.getId()){
            case R.id.btn_back : {
                if (interstitialAd != null && interstitialAd.isLoaded()) {
                    interstitialAd.show();
                }
                else {
                    Intent i = new Intent(PassiveIncomeActivity.this, SpendingActivity.class);
                    i.putExtra(UID_USER, idUser);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                }
                break;
            }
            case R.id.btn_next : {
                Intent i = new Intent(PassiveIncomeActivity.this, IncomeActivity.class);
                i.putExtra(UID_USER, idUser);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            }
        }
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
                mAdapter.notifyDataSetChanged();
                txtJumlahTransaksi.setText(listMenu.size()+" "+getString(R.string.transaksi));
                txtTotalBiaya.setText(CurrencyEditText.currencyFormat(total));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.btn_clear)
    public void clearData(){
        new AlertDialog.Builder(PassiveIncomeActivity.this)
                .setTitle(getString(R.string.yakin_ingin_mereset))
                .setPositiveButton(getString(R.string.ya), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete all data
                        dbPassiveIncome = FirebaseDatabase.getInstance().getReference("passive_income").child(idUser);
                        dbPassiveIncome.removeValue();
                        listMenu.clear();

                        //init new data
                        final String baseImage = "gs://test-financial.appspot.com/icon/";
                        listMenu.add(new Common( "House's rent / Kost", "0", baseImage+"rumahsewa.PNG"));
                        listMenu.add(new Common( "Business", "0", baseImage+"usaha.PNG"));
                        listMenu.add(new Common( "Deposit / MutualFund", "0", baseImage+"deposito.PNG"));
                        listMenu.add(new Common( "Book Royalties", "0", baseImage+"pasifincome.PNG"));
                        listMenu.add(new Common( "Cassete Royalties", "0", baseImage+"lainlain.PNG"));
                        listMenu.add(new Common( "Royaties' System", "0", baseImage+"pengeluaranbulanan.PNG"));

                        //set data
                        for (int i = 0; i < listMenu.size(); i++) {
                            String idPassive = dbPassiveIncome.push().getKey();
                            listMenu.get(i).setId(idPassive);
                            dbPassiveIncome.child(idPassive).setValue(listMenu.get(i));
                        }

                    }
                })
                .setNegativeButton(getString(R.string.tidak), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(false)
                .show();
    }

    @OnClick(R.id.btn_add)
    public void addData(){
        LayoutInflater layoutInflater = LayoutInflater.from(PassiveIncomeActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PassiveIncomeActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText etKeterangan = (EditText) promptView.findViewById(R.id.et_keterangan);
        final EditText etNominal = (EditText) promptView.findViewById(R.id.et_nominal);

        new CurrencyEditText(etNominal);

        alertDialogBuilder.setCancelable(true)
                .setPositiveButton(getString(R.string.simpan), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String idNew = dbPassiveIncome.push().getKey();
                        Common activeIncome = new Common(etKeterangan.getText().toString(), etNominal.getText().toString().replace(".", ""), "");
                        activeIncome.setId(idNew);
                        dbPassiveIncome = FirebaseDatabase.getInstance().getReference("passive_income").child(idUser).child(idNew);
                        dbPassiveIncome.setValue(activeIncome);

                        listMenu.add(activeIncome);
                        mAdapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton(getString(R.string.batal), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public void toolbar(){
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.passive_income));
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
