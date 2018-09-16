package org.properti.analisa.financialcheck.activity.common;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.LinkedList;

import org.properti.analisa.financialcheck.R;
import org.properti.analisa.financialcheck.adapter.PassiveIncomeAdapter;
import org.properti.analisa.financialcheck.model.ModelMenu;

public class SpendingActivity extends AppCompatActivity {
    private final LinkedList<ModelMenu> listMenu = new LinkedList<>();

    private RecyclerView mRecyclerView;
    private PassiveIncomeAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spending);
        toolbar();

        listMenu.addLast(new ModelMenu(getString(R.string.makan_luar_rumah),getString(R.string.harga), R.drawable.rumahsewa, R.drawable.bluepen));
        listMenu.addLast(new ModelMenu(getString(R.string.beli_luxury),getString(R.string.harga), R.drawable.usaha, R.drawable.redpen));
        listMenu.addLast(new ModelMenu(getString(R.string.piknik),getString(R.string.harga), R.drawable.deposito, R.drawable.greenpen));


        mRecyclerView = (RecyclerView) findViewById(R.id.rv_spending);

        mAdapter = new PassiveIncomeAdapter(SpendingActivity.this, listMenu);

        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_menu, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    public void toolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar); //Inisialisasi dan Implementasi id Toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Spending");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
