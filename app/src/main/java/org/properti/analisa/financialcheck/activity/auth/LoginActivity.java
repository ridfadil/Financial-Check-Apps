package org.properti.analisa.financialcheck.activity.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.properti.analisa.financialcheck.R;
import org.properti.analisa.financialcheck.activity.MainActivity;
import org.properti.analisa.financialcheck.model.Common;
import org.properti.analisa.financialcheck.model.User;
import org.properti.analisa.financialcheck.utils.DialogUtils;
import org.properti.analisa.financialcheck.firebase.FirebaseApplication;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.btn_login_facebook)
    LoginButton btnLoginFacebook;

    String email, password;

    private FirebaseAuth mAuth;

    public static final int RC_SIGN_IN = 1;
    GoogleApiClient mGoogleSignInClient;
    CallbackManager callbackManager;

    DatabaseReference ref;
    DatabaseReference databaseUser, databaseSpendingMonth, databaseSpending, databasePassiveIncome, databaseActiveIncome;

    List<Common> listSpendingMonth = new ArrayList<>();
    List<Common> listSpending = new ArrayList<>();
    List<Common> listPassiveIncome = new ArrayList<>();
    List<Common> listActiveIncome = new ArrayList<>();

    private ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        printKeyHash();

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = ((FirebaseApplication)getApplication()).getFirebaseAuth();
        initGoogleConf();
        initFacebookConf();

        ((FirebaseApplication)getApplication()).checkUserLogin(this);

        loading = DialogUtils.showProgressDialog(this, "Loading", "Checking Data");
    }

    private void printKeyHash() {
        try{
            PackageInfo info = getPackageManager().getPackageInfo("org.properti.analisa.financialcheck", PackageManager.GET_SIGNATURES);
            for(Signature signature:info.signatures){
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void initFacebookConf() {
        callbackManager = CallbackManager.Factory.create();
        btnLoginFacebook.setReadPermissions("email", "public_profile");
        btnLoginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                firebaseAuthWithFacebook(loginResult.getAccessToken());
                Log.e("TOKEN :", ""+loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    @OnClick(R.id.btn_login_google)
    public void googleLogin(){
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void initGoogleConf() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @OnClick(R.id.btn_login)
    public void login(){
        loading.show();
        if(TextUtils.isEmpty(etEmail.getText().toString()) || TextUtils.isEmpty(etPassword.getText().toString())){
            Toast.makeText(this, getString(R.string.data_kosong), Toast.LENGTH_SHORT).show();
            loading.dismiss();
        }
        else{
            email = etEmail.getText().toString();
            password = etPassword.getText().toString();

            ((FirebaseApplication)getApplication()).loginAUser(this, email, password);

            loading.dismiss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithFacebook(AccessToken token){
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            sendFacebookUserData(user);
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            sendGoogleUserData(user);
                        }
                        else {
                            Toast.makeText(LoginActivity.this,"you are not able to log in to google",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendFacebookUserData(FirebaseUser user){
        final String nama = user.getDisplayName();
        final String email = user.getEmail();
        final String phone = user.getPhoneNumber();
        final String id = user.getUid();

        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
                else {
                    databaseUser = FirebaseDatabase.getInstance().getReference("users");
                    databaseUser.child(id).setValue(new User(nama, email, phone));

                    initData();

                    databaseSpendingMonth = FirebaseDatabase.getInstance().getReference("spending_month").child(id);
                    databaseSpending = FirebaseDatabase.getInstance().getReference("spending").child(id);
                    databasePassiveIncome = FirebaseDatabase.getInstance().getReference("passive_income").child(id);
                    databaseActiveIncome = FirebaseDatabase.getInstance().getReference("active_income").child(id);

                    int i;
                    for(i=0; i<listSpendingMonth.size(); i++){
                        String idSpendingMonth = databaseSpendingMonth.push().getKey();
                        listSpendingMonth.get(i).setId(idSpendingMonth);
                        databaseSpendingMonth.child(idSpendingMonth).setValue(listSpendingMonth.get(i));
                    }

                    for(i=0; i<listSpending.size(); i++){
                        String idSpending = databaseSpending.push().getKey();
                        listSpending.get(i).setId(idSpending);
                        databaseSpending.child(idSpending).setValue(listSpending.get(i));
                    }

                    for(i=0; i<listPassiveIncome.size(); i++){
                        String idPassive = databasePassiveIncome.push().getKey();
                        listPassiveIncome.get(i).setId(idPassive);
                        databasePassiveIncome.child(idPassive).setValue(listPassiveIncome.get(i));
                    }

                    for(i=0; i<listActiveIncome.size(); i++){
                        String idActive = databaseActiveIncome.push().getKey();
                        listActiveIncome.get(i).setId(idActive);
                        databaseActiveIncome.child(idActive).setValue(listActiveIncome.get(i));
                    }

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendGoogleUserData(FirebaseUser user) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (acct != null) {
            final String nama = acct.getDisplayName();
            final String email = acct.getEmail();
            final String phone = user.getPhoneNumber();
            final String id = user.getUid();

            ref = FirebaseDatabase.getInstance().getReference();
            ref.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                    else {
                        databaseUser = FirebaseDatabase.getInstance().getReference("users");
                        databaseUser.child(id).setValue(new User(nama, email, phone));

                        initData();

                        databaseSpendingMonth = FirebaseDatabase.getInstance().getReference("spending_month").child(id);
                        databaseSpending = FirebaseDatabase.getInstance().getReference("spending").child(id);
                        databasePassiveIncome = FirebaseDatabase.getInstance().getReference("passive_income").child(id);
                        databaseActiveIncome = FirebaseDatabase.getInstance().getReference("active_income").child(id);

                        int i;
                        for(i=0; i<listSpendingMonth.size(); i++){
                            String idSpendingMonth = databaseSpendingMonth.push().getKey();
                            listSpendingMonth.get(i).setId(idSpendingMonth);
                            databaseSpendingMonth.child(idSpendingMonth).setValue(listSpendingMonth.get(i));
                        }

                        for(i=0; i<listSpending.size(); i++){
                            String idSpending = databaseSpending.push().getKey();
                            listSpending.get(i).setId(idSpending);
                            databaseSpending.child(idSpending).setValue(listSpending.get(i));
                        }

                        for(i=0; i<listPassiveIncome.size(); i++){
                            String idPassive = databasePassiveIncome.push().getKey();
                            listPassiveIncome.get(i).setId(idPassive);
                            databasePassiveIncome.child(idPassive).setValue(listPassiveIncome.get(i));
                        }

                        for(i=0; i<listActiveIncome.size(); i++){
                            String idActive = databaseActiveIncome.push().getKey();
                            listActiveIncome.get(i).setId(idActive);
                            databaseActiveIncome.child(idActive).setValue(listActiveIncome.get(i));
                        }

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void initData() {
        listSpendingMonth.add(new Common( "Makan Dalam Rumah", "0", ""));
        listSpendingMonth.add(new Common( "Listrik Gas Air", "0", ""));
        listSpendingMonth.add(new Common( "Telepon Rumah", "0", ""));
        listSpendingMonth.add(new Common( "Telepon HP", "0", ""));
        listSpendingMonth.add(new Common( "Sekolah + Les Anak", "0", ""));
        listSpendingMonth.add(new Common( "Cicilan Hutang Rumah", "0", ""));
        listSpendingMonth.add(new Common( "Cicilan Kendaraan", "0", ""));
        listSpendingMonth.add(new Common( "Cicilan Kartu Kredit", "0", ""));
        listSpendingMonth.add(new Common( "Asuransi", "0", ""));
        listSpendingMonth.add(new Common( "Pembantu", "0", ""));
        listSpendingMonth.add(new Common( "Mobil (Bensin dan Maintenance)", "0", ""));
        listSpendingMonth.add(new Common( "Pakaian", "0", ""));

        listSpending.add(new Common( "Makan Luar Rumah", "0", ""));
        listSpending.add(new Common( "Beli Luxury", "0", ""));
        listSpending.add(new Common( "Piknik", "0", ""));

        listPassiveIncome.add(new Common( "Rumah Sewa / Kos", "0", ""));
        listPassiveIncome.add(new Common( "Usaha", "0", ""));
        listPassiveIncome.add(new Common( "Deposito / Reksadana", "0", ""));
        listPassiveIncome.add(new Common( "Royalti Buku", "0", ""));
        listPassiveIncome.add(new Common( "Royalti Kaset", "0", ""));
        listPassiveIncome.add(new Common( "Royalti Sistem", "0", ""));

        listActiveIncome.add(new Common( "Profesi", "0", ""));
        listActiveIncome.add(new Common( "Trading", "0", ""));
    }

    @OnClick(R.id.btn_to_register)
    public void toRegister(){
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @OnClick(R.id.btn_to_forgot_password)
    public void toForgotPassword(){
        startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}