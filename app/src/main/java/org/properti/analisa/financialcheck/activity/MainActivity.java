package org.properti.analisa.financialcheck.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    CardView cdIncome,cdSpending,cdMonthSpending,cdPassiveIncome;
    Button btnLogout;

    List<Common> listSpendingMonth = new ArrayList<>();
    List<Common> listSpending = new ArrayList<>();
    List<Common> listPassiveIncome = new ArrayList<>();
    List<Common> listActiveIncome = new ArrayList<>();

    DatabaseReference dbUser, dbSpendingMonth, dbSpending, dbPassiveIncome, dbActiveIncome;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbUser = FirebaseDatabase.getInstance().getReference("users").child("");

        mAuth = ((FirebaseApplication)getApplication()).getFirebaseAuth();

        init();
        actionClicked();
    }
    public void actionClicked(){
        cdIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,IncomeActivity.class);
                startActivity(i);
            }
        });

        cdPassiveIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,PassiveIncomeActivity.class);
                startActivity(i);
            }
        });

        cdSpending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SpendingActivity.class);
                startActivity(i);
            }
        });

        cdMonthSpending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,SpendingMonthActivity.class);
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

    public  void init() {
        cdIncome =  findViewById(R.id.income_aktif);
        cdMonthSpending = findViewById(R.id.cv_month_spending);
        cdSpending = findViewById(R.id.cv_spending);
        cdPassiveIncome = findViewById(R.id.cv_passive_income);
        btnLogout = findViewById(R.id.btn_logout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu_home, menu);

        // return true so that the menu pop up is opened
        return true;
    }
}
