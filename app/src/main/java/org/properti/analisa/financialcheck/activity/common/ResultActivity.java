package org.properti.analisa.financialcheck.activity.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.properti.analisa.financialcheck.R;
import org.properti.analisa.financialcheck.firebase.FirebaseApplication;
import org.properti.analisa.financialcheck.model.Common;
import org.properti.analisa.financialcheck.model.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static org.properti.analisa.financialcheck.activity.MainActivity.UID_USER;

public class ResultActivity extends AppCompatActivity {

    @BindView(R.id.img_financial_condition)
    ImageView imageCondition;
    @BindView(R.id.txt_financial_condition)
    TextView txtCondition;
    @BindView(R.id.txt_total_monthly_spending)
    TextView txtMonthlySpending;
    @BindView(R.id.txt_total_spending)
    TextView txtSpending;
    @BindView(R.id.txt_total_passive_income)
    TextView txtPassiveIncome;
    @BindView(R.id.txt_total_active_income)
    TextView txtActiveIncome;
    @BindView(R.id.txt_nama_user)
    TextView txtNamaUser;

    String id;

    List<Common> listSpendingMonth = new ArrayList<>();
    List<Common> listSpending = new ArrayList<>();
    List<Common> listPassiveIncome = new ArrayList<>();
    List<Common> listActiveIncome = new ArrayList<>();

    DatabaseReference dbUser, dbSpendingMonth, dbSpending, dbPassiveIncome, dbActiveIncome;

    private FirebaseAuth mAuth;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);
        toolbar();

        mAuth = ((FirebaseApplication)getApplication()).getFirebaseAuth();
        id = getIntent().getStringExtra(UID_USER);
        dbUser = FirebaseDatabase.getInstance().getReference("users").child(id);
        dbSpendingMonth = FirebaseDatabase.getInstance().getReference("spending_month").child(id);
        dbSpending = FirebaseDatabase.getInstance().getReference("spending").child(id);
        dbPassiveIncome = FirebaseDatabase.getInstance().getReference("passive_income").child(id);
        dbActiveIncome = FirebaseDatabase.getInstance().getReference("active_income").child(id);
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
                txtMonthlySpending.setText(String.valueOf(finalTotalSpendingMonth));
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
                        txtSpending.setText(String.valueOf(finalTotalSpending));
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
                                txtPassiveIncome.setText(String.valueOf(finalTotalPassiveIncome));
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

                                        final long finalTotalActiveIncome = totalActiveIncome;
                                        txtActiveIncome.setText(String.valueOf(finalTotalActiveIncome));
                                        dbUser.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                user = dataSnapshot.getValue(User.class);
                                                //pengecekan
                                                String kondisi = "";
                                                if(finalTotalSpendingMonth + finalTotalSpending > finalTotalPassiveIncome + finalTotalActiveIncome){
                                                    kondisi = "NOT GOOD";
                                                }
                                                else if(finalTotalSpendingMonth + finalTotalSpending == finalTotalPassiveIncome + finalTotalActiveIncome){
                                                    kondisi = "GOOD";
                                                }
                                                else if(finalTotalSpendingMonth + finalTotalSpending < finalTotalPassiveIncome + finalTotalActiveIncome){
                                                    kondisi = "VERY GOOD";
                                                }
                                                else if(finalTotalPassiveIncome > finalTotalSpendingMonth + finalTotalSpending){
                                                    kondisi = "FINANCIAL INDEPENDENT";
                                                }

                                                txtNamaUser.setText("Hi, "+user.getNama());
                                                txtCondition.setText(kondisi);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @OnClick({R.id.btn_home_result, R.id.btn_edit_spending_month, R.id.btn_edit_spending, R.id.btn_edit_passive_income, R.id.btn_edit_active_income})
    public void onViewClicked(View v) {
        switch (v.getId()){
            case R.id.btn_home_result : {
                finish();
                break;
            }
            case R.id.btn_edit_spending_month : {
                Intent i = new Intent(this, SpendingMonthActivity.class);
                i.putExtra(UID_USER, id);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            }
            case R.id.btn_edit_spending : {
                Intent i = new Intent(this, SpendingActivity.class);
                i.putExtra(UID_USER, id);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            }
            case R.id.btn_edit_passive_income : {
                Intent i = new Intent(this, PassiveIncomeActivity.class);
                i.putExtra(UID_USER, id);
                startActivity(i);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
            }
            case R.id.btn_edit_active_income : {
                Intent i = new Intent(this, IncomeActivity.class);
                i.putExtra(UID_USER, id);
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
        getSupportActionBar().setTitle("Result");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
