package org.properti.analisa.financialcheck.activity;

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
import org.properti.analisa.financialcheck.adapter.IncomeAdapter;
import org.properti.analisa.financialcheck.adapter.PassiveIncomeAdapter;
import org.properti.analisa.financialcheck.model.ModelMenu;

public class PassiveIncomeActivity extends AppCompatActivity {

    private final LinkedList<ModelMenu> listMenu = new LinkedList<>();

    private RecyclerView mRecyclerView;
    private PassiveIncomeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passive_income);
        toolbar();

        listMenu.addLast(new ModelMenu(getString(R.string.passive_rumahsewa),getString(R.string.harga), R.drawable.rumahsewa, R.drawable.bluepen));
        listMenu.addLast(new ModelMenu(getString(R.string.passive_usaha),getString(R.string.harga), R.drawable.usaha, R.drawable.redpen));
        listMenu.addLast(new ModelMenu(getString(R.string.passive_Deposito),getString(R.string.harga), R.drawable.deposito, R.drawable.greenpen));
        listMenu.addLast(new ModelMenu(getString(R.string.royalti_buku),getString(R.string.harga), R.drawable.deposito, R.drawable.greenpen));
        listMenu.addLast(new ModelMenu(getString(R.string.royalti_kaset),getString(R.string.harga), R.drawable.deposito, R.drawable.greenpen));
        listMenu.addLast(new ModelMenu(getString(R.string.royalti_sistem),getString(R.string.harga), R.drawable.deposito, R.drawable.greenpen));
        listMenu.addLast(new ModelMenu(getString(R.string.lainlain),getString(R.string.harga), R.drawable.lainlain, R.drawable.bluepen));

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_passive_income);

        mAdapter = new PassiveIncomeAdapter(PassiveIncomeActivity.this, listMenu);

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
