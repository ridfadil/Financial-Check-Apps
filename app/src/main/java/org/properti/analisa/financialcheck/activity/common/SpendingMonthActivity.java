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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.properti.analisa.financialcheck.R;
import org.properti.analisa.financialcheck.adapter.MonthlySpendingAdapter;
import org.properti.analisa.financialcheck.model.Common;
import org.properti.analisa.financialcheck.utils.CurrencyEditText;
import org.properti.analisa.financialcheck.utils.LocalizationUtils;

import java.util.LinkedList;

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
        SharedPreferences pref = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        LocalizationUtils.setLocale(pref.getString("language", ""), getBaseContext());
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
        new AlertDialog.Builder(SpendingMonthActivity.this)
                .setTitle(getString(R.string.yakin_ingin_mereset))
                .setPositiveButton(getString(R.string.ya), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //delete all data
                        dbSpendingMonth = FirebaseDatabase.getInstance().getReference("spending_month").child(idUser);
                        dbSpendingMonth.removeValue();
                        listMenu.clear();

                        //init new data
                        final String baseImage = "gs://test-financial.appspot.com/icon/";
                        listMenu.add(new Common( "Eating at home", "0", baseImage+"profesi.PNG"));
                        listMenu.add(new Common( "Electricity, Gas, Water", "0", baseImage+"listrikgas.PNG"));
                        listMenu.add(new Common( "House's Phone", "0", baseImage+"belihandphone.PNG"));
                        listMenu.add(new Common( "Phone, mobile phone", "0", baseImage+"belihandphone.PNG"));
                        listMenu.add(new Common( "School / Children's course", "0", baseImage+"pengeluaranbulanan.PNG"));
                        listMenu.add(new Common( "House's Instalment Debt", "0", baseImage+"rumahsewa.PNG"));
                        listMenu.add(new Common( "Transportation's Instalment", "0", baseImage+"servicemobil.PNG"));
                        listMenu.add(new Common( "Credit Card's Instalment", "0", baseImage+"deposito.PNG"));
                        listMenu.add(new Common( "Insurance", "0", baseImage+"pengeluaranbulanan.PNG"));
                        listMenu.add(new Common( "Servant", "0", baseImage+"pengeluaranlainnya.PNG"));
                        listMenu.add(new Common( "Car (Maintenance and Gasoline)", "0", baseImage+"servicemobil.PNG"));
                        listMenu.add(new Common( "Clothes", "0", baseImage+"pengeluaranlainnya.PNG"));

                        //set data
                        for (int i = 0; i < listMenu.size(); i++) {
                            String idSpendingMonth = dbSpendingMonth.push().getKey();
                            listMenu.get(i).setId(idSpendingMonth);
                            dbSpendingMonth.child(idSpendingMonth).setValue(listMenu.get(i));
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
        LayoutInflater layoutInflater = LayoutInflater.from(SpendingMonthActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SpendingMonthActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText etKeterangan = (EditText) promptView.findViewById(R.id.et_keterangan);
        final EditText etNominal = (EditText) promptView.findViewById(R.id.et_nominal);

        new CurrencyEditText(etNominal);

        alertDialogBuilder.setCancelable(true)
                .setPositiveButton(getString(R.string.simpan), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String idNew = dbSpendingMonth.push().getKey();
                        Common spendingMonth = new Common(etKeterangan.getText().toString(), etNominal.getText().toString().replace(".", ""), "");
                        spendingMonth.setId(idNew);
                        dbSpendingMonth = FirebaseDatabase.getInstance().getReference("spending_month").child(idUser).child(idNew);
                        dbSpendingMonth.setValue(spendingMonth);

                        listMenu.add(spendingMonth);
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

    @OnClick({R.id.btn_next, R.id.btn_back})
    public void onViewClicked(View v){
        switch (v.getId()){
            case R.id.btn_back : {
                finish();
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                break;
            }
            case R.id.btn_next : {
                Intent i = new Intent(SpendingMonthActivity.this, org.properti.analisa.financialcheck.activity.common.SpendingActivity.class);
                i.putExtra(UID_USER, idUser);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            }
        }
    }

    public void toolbar(){
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
