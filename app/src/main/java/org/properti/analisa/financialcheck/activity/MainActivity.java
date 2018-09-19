package org.properti.analisa.financialcheck.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
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
import org.properti.analisa.financialcheck.firebase.FirebaseApplication;
import org.properti.analisa.financialcheck.model.Common;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String UID_USER = "UID_USER";

    CardView cdIncome, cdSpending, cdMonthSpending, cdPassiveIncome,cdSetting,cdProfil;
    Button btnLogout;
    TextView txtFinancialCondition, txtFinancialTotal;

    List<Common> listSpendingMonth = new ArrayList<>();
    List<Common> listSpending = new ArrayList<>();
    List<Common> listPassiveIncome = new ArrayList<>();
    List<Common> listActiveIncome = new ArrayList<>();

    DatabaseReference dbUser, dbSpendingMonth, dbSpending, dbPassiveIncome, dbActiveIncome;

    private FirebaseAuth mAuth;

    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

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
                                            kondisi = "NOT GOOD";
                                        }
                                        else if(finalTotalSpendingMonth + finalTotalSpending == finalTotalPassiveIncome + totalActiveIncome){
                                            kondisi = "GOOD";
                                        }
                                        else if(finalTotalSpendingMonth + finalTotalSpending < finalTotalPassiveIncome + totalActiveIncome){
                                            kondisi = "VERY GOOD";
                                        }
                                        else if(finalTotalPassiveIncome > finalTotalSpendingMonth + finalTotalSpending){
                                            kondisi = "FINANCIAL INDEPENDENT";
                                        }

                                        txtFinancialCondition.setText(kondisi);
                                        txtFinancialTotal.setText(" "+(finalTotalPassiveIncome + totalActiveIncome - finalTotalSpendingMonth - finalTotalSpending));

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

    public void checkCondition(long a, long b, long c, long d){

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
        cdSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this,SettingActivity.class);
                i.putExtra(UID_USER, id);
                startActivity(i);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
    }

    public void init() {
        cdIncome =  findViewById(R.id.income_aktif);
        cdMonthSpending = findViewById(R.id.cv_month_spending);
        cdSpending = findViewById(R.id.cv_spending);
        cdPassiveIncome = findViewById(R.id.cv_passive_income);
        btnLogout = findViewById(R.id.btn_logout);
        txtFinancialCondition = findViewById(R.id.txt_financial_condition);
        txtFinancialTotal = findViewById(R.id.txt_financial_total);
        cdSetting = findViewById(R.id.cv_setting);
        cdProfil = findViewById(R.id.cv_user_profile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_home, menu);

        // return true so that the menu pop up is opened
        return true;
    }
}
