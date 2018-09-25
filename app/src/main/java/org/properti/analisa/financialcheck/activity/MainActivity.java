package org.properti.analisa.financialcheck.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.properti.analisa.financialcheck.R;
import org.properti.analisa.financialcheck.activity.auth.LoginActivity;
import org.properti.analisa.financialcheck.activity.common.IncomeActivity;
import org.properti.analisa.financialcheck.activity.common.PassiveIncomeActivity;
import org.properti.analisa.financialcheck.activity.common.SpendingActivity;
import org.properti.analisa.financialcheck.activity.common.SpendingMonthActivity;
import org.properti.analisa.financialcheck.activity.profile.UserProfilActivity;
import org.properti.analisa.financialcheck.activity.setting.AboutActivity;
import org.properti.analisa.financialcheck.firebase.FirebaseApplication;
import org.properti.analisa.financialcheck.model.Common;
import org.properti.analisa.financialcheck.utils.CurrencyEditText;
import org.properti.analisa.financialcheck.utils.LocalizationUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final String UID_USER = "UID_USER";

    CardView cdIncome, cdSpending, cdMonthSpending, cdPassiveIncome, cdProfil;
    TextView txtFinancialCondition, txtFinancialTotal;

    List<Common> listSpendingMonth = new ArrayList<>();
    List<Common> listSpending = new ArrayList<>();
    List<Common> listPassiveIncome = new ArrayList<>();
    List<Common> listActiveIncome = new ArrayList<>();

    DatabaseReference dbUser, dbSpendingMonth, dbSpending, dbPassiveIncome, dbActiveIncome;

    private FirebaseAuth mAuth;

    String id;

    @BindView(R.id.ad_bottom)
    AdView bottomAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences pref = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        LocalizationUtils.setLocale(pref.getString("language", ""), getBaseContext());

        init();
        ButterKnife.bind(this);

        AdRequest adRequest = new AdRequest.Builder()
                .build();
        bottomAds.loadAd(adRequest);

        mAuth = ((FirebaseApplication)getApplication()).getFirebaseAuth();
        id = mAuth.getCurrentUser().getUid();

        dbUser = FirebaseDatabase.getInstance().getReference("users").child(id);
        dbSpendingMonth = FirebaseDatabase.getInstance().getReference("spending_month").child(id);
        dbSpending = FirebaseDatabase.getInstance().getReference("spending").child(id);
        dbPassiveIncome = FirebaseDatabase.getInstance().getReference("passive_income").child(id);
        dbActiveIncome = FirebaseDatabase.getInstance().getReference("active_income").child(id);

        actionClicked();
    }

    @Override
    protected void onStart() {
        super.onStart();

        dbSpendingMonth.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listSpendingMonth.clear();
                long totalSpendingMonth = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Common spendingMonth = postSnapshot.getValue(Common.class);
                    listSpendingMonth.add(spendingMonth);
                    totalSpendingMonth = totalSpendingMonth + Long.parseLong(spendingMonth.getHarga());
                }

                final long finalTotalSpendingMonth = totalSpendingMonth;
                dbSpending.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        listSpending.clear();
                        long totalSpending = 0;
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Common spending = postSnapshot.getValue(Common.class);
                            listSpending.add(spending);
                            totalSpending = totalSpending + Long.parseLong(spending.getHarga());
                        }

                        final long finalTotalSpending = totalSpending;
                        dbPassiveIncome.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                listPassiveIncome.clear();
                                long totalPassiveIncome = 0;
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    Common passiveIncome = postSnapshot.getValue(Common.class);
                                    listPassiveIncome.add(passiveIncome);
                                    totalPassiveIncome = totalPassiveIncome + Long.parseLong(passiveIncome.getHarga());
                                }

                                final long finalTotalPassiveIncome = totalPassiveIncome;
                                dbActiveIncome.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        listActiveIncome.clear();
                                        long totalActiveIncome = 0;
                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            Common activeIncome = postSnapshot.getValue(Common.class);
                                            listActiveIncome.add(activeIncome);
                                            totalActiveIncome = totalActiveIncome + Long.parseLong(activeIncome.getHarga());
                                        }

                                        //pengecekan
                                        String kondisi = "";
                                        if(finalTotalSpendingMonth + finalTotalSpending > finalTotalPassiveIncome + totalActiveIncome){
                                            kondisi = getString(R.string.not_good);
                                        }
                                        else if(finalTotalSpendingMonth + finalTotalSpending == finalTotalPassiveIncome + totalActiveIncome){
                                            kondisi = getString(R.string.good);
                                        }
                                        else if(finalTotalSpendingMonth + finalTotalSpending < finalTotalPassiveIncome + totalActiveIncome){
                                            kondisi = getString(R.string.very_good);
                                        }
                                        else if(finalTotalPassiveIncome > finalTotalSpendingMonth + finalTotalSpending){
                                            kondisi = getString(R.string.financial_independent);
                                        }

                                        txtFinancialCondition.setText(kondisi);
                                        txtFinancialTotal.setText(CurrencyEditText.currencyFormatterLong(finalTotalPassiveIncome + totalActiveIncome - finalTotalSpendingMonth - finalTotalSpending));

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void actionClicked(){
        cdIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,IncomeActivity.class);
                i.putExtra(UID_USER, id);
                startActivity(i);
            }
        });

        cdPassiveIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,PassiveIncomeActivity.class);
                i.putExtra(UID_USER, id);
                startActivity(i);
            }
        });

        cdSpending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SpendingActivity.class);
                i.putExtra(UID_USER, id);
                startActivity(i);
            }
        });

        cdMonthSpending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SpendingMonthActivity.class);
                i.putExtra(UID_USER, id);
                startActivity(i);
            }
        });
        cdProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,UserProfilActivity.class);
                i.putExtra(UID_USER, id);
                startActivity(i);
            }
        });
    }

    public void init() {
        cdIncome =  findViewById(R.id.income_aktif);
        cdMonthSpending = findViewById(R.id.cv_month_spending);
        cdSpending = findViewById(R.id.cv_spending);
        cdPassiveIncome = findViewById(R.id.cv_passive_income);
        txtFinancialCondition = findViewById(R.id.txt_financial_condition);
        txtFinancialTotal = findViewById(R.id.txt_financial_total);
        cdProfil = findViewById(R.id.cv_user_profile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_home, menu);

        return true;
    }

    public void setLangPref(String lang){
        SharedPreferences.Editor editor = getSharedPreferences("setting", MODE_PRIVATE).edit();
        editor.putString("language", lang);
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        switch(item.getItemId())
        {
            case R.id.nav_indonesia:
                LocalizationUtils.setLocale("in", getBaseContext());
                setLangPref("in");
                startActivity(i);
                break;
            case R.id.nav_inggris:
                LocalizationUtils.setLocale("en", getBaseContext());
                setLangPref("en");
                startActivity(i);
                break;
            case R.id.nav_tentang:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.nav_share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyText = "Hitung anggaran belanjamu agar lebih teratur hanya di aplikasi ini. Dapatkan sekarang : http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();

                sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Financial Check");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Financial Check"));
                break;
            case R.id.nav_suka:
                Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                }
                catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName())));
                }
                break;
            case R.id.nav_logout:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.yakin_ingin_keluar))
                        .setPositiveButton(getString(R.string.ya), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //logout akun google
                                mAuth.signOut();
                                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(i);
                            }
                        })
                        .setNegativeButton(getString(R.string.tidak), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setCancelable(false)
                        .show();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
