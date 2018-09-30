package org.properti.analisa.financialcheck.activity.auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.properti.analisa.financialcheck.R;
import org.properti.analisa.financialcheck.activity.MainActivity;
import org.properti.analisa.financialcheck.firebase.FirebaseApplication;
import org.properti.analisa.financialcheck.model.Common;
import org.properti.analisa.financialcheck.model.User;
import org.properti.analisa.financialcheck.utils.DialogUtils;
import org.properti.analisa.financialcheck.utils.LocalizationUtils;

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
        SharedPreferences pref = getSharedPreferences("setting", Activity.MODE_PRIVATE);
        LocalizationUtils.setLocale(pref.getString("language", ""), getBaseContext());

        loading = DialogUtils.showProgressDialog(this, "Loading", "Checking Data");

        FacebookSdk.sdkInitialize(getApplicationContext());

        printKeyHash();

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = ((FirebaseApplication) getApplication()).getFirebaseAuth();
        initGoogleConf();
        initFacebookConf();

        ((FirebaseApplication) getApplication()).checkUserLogin(this);
    }

    private void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo("org.properti.analisa.financialcheck", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
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
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("response", response.toString());
                        try {
                            Log.e("TOKEN :", "" + loginResult.getAccessToken());
                            User user = new User(object.getString("name"), object.getString("email"), "");
                            firebaseAuthWithFacebook(loginResult.getAccessToken().getToken(), user);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields","id, name, email");
                request.setParameters(parameters);
                request.executeAsync();
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
    public void googleLogin() {
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
    public void login() {
        loading.show();
        if (TextUtils.isEmpty(etEmail.getText().toString()) || TextUtils.isEmpty(etPassword.getText().toString())) {
            Toast.makeText(this, getString(R.string.data_kosong), Toast.LENGTH_SHORT).show();
            loading.dismiss();
        } else {
            email = etEmail.getText().toString();
            password = etPassword.getText().toString();

            ((FirebaseApplication) getApplication()).loginAUser(this, email, password);

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

    private void firebaseAuthWithFacebook(String token, final User dataUser) {
        loading.show();
        AuthCredential credential = FacebookAuthProvider.getCredential(token);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            sendFacebookUserData(user, dataUser);
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        loading.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            sendGoogleUserData(user);
                        } else {
                            Toast.makeText(LoginActivity.this, "you are not able to log in to google", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void sendFacebookUserData(FirebaseUser user, User dataUser) {
        final String nama = dataUser.getNama();
        final String email = dataUser.getEmail();
        final String phone = "";
        final String id = user.getUid();

        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
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
                    for (i = 0; i < listSpendingMonth.size(); i++) {
                        String idSpendingMonth = databaseSpendingMonth.push().getKey();
                        listSpendingMonth.get(i).setId(idSpendingMonth);
                        databaseSpendingMonth.child(idSpendingMonth).setValue(listSpendingMonth.get(i));
                    }

                    for (i = 0; i < listSpending.size(); i++) {
                        String idSpending = databaseSpending.push().getKey();
                        listSpending.get(i).setId(idSpending);
                        databaseSpending.child(idSpending).setValue(listSpending.get(i));
                    }

                    for (i = 0; i < listPassiveIncome.size(); i++) {
                        String idPassive = databasePassiveIncome.push().getKey();
                        listPassiveIncome.get(i).setId(idPassive);
                        databasePassiveIncome.child(idPassive).setValue(listPassiveIncome.get(i));
                    }

                    for (i = 0; i < listActiveIncome.size(); i++) {
                        String idActive = databaseActiveIncome.push().getKey();
                        listActiveIncome.get(i).setId(idActive);
                        databaseActiveIncome.child(idActive).setValue(listActiveIncome.get(i));
                    }

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
                loading.dismiss();
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
                    if (dataSnapshot.exists()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
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
                        for (i = 0; i < listSpendingMonth.size(); i++) {
                            String idSpendingMonth = databaseSpendingMonth.push().getKey();
                            listSpendingMonth.get(i).setId(idSpendingMonth);
                            databaseSpendingMonth.child(idSpendingMonth).setValue(listSpendingMonth.get(i));
                        }

                        for (i = 0; i < listSpending.size(); i++) {
                            String idSpending = databaseSpending.push().getKey();
                            listSpending.get(i).setId(idSpending);
                            databaseSpending.child(idSpending).setValue(listSpending.get(i));
                        }

                        for (i = 0; i < listPassiveIncome.size(); i++) {
                            String idPassive = databasePassiveIncome.push().getKey();
                            listPassiveIncome.get(i).setId(idPassive);
                            databasePassiveIncome.child(idPassive).setValue(listPassiveIncome.get(i));
                        }

                        for (i = 0; i < listActiveIncome.size(); i++) {
                            String idActive = databaseActiveIncome.push().getKey();
                            listActiveIncome.get(i).setId(idActive);
                            databaseActiveIncome.child(idActive).setValue(listActiveIncome.get(i));
                        }

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void initData() {
        final String baseImage = "gs://test-financial.appspot.com/icon/";

        listSpendingMonth.add(new Common( "Eating at home", "0", baseImage+"profesi.PNG"));
        listSpendingMonth.add(new Common( "Electricity, Gas, Water", "0", baseImage+"listrikgas.PNG"));
        listSpendingMonth.add(new Common( "House's Phone", "0", baseImage+"belihandphone.PNG"));
        listSpendingMonth.add(new Common( "Phone, mobile phone", "0", baseImage+"belihandphone.PNG"));
        listSpendingMonth.add(new Common( "School / Children's course", "0", baseImage+"pengeluaranbulanan.PNG"));
        listSpendingMonth.add(new Common( "House's Instalment Debt", "0", baseImage+"rumahsewa.PNG"));
        listSpendingMonth.add(new Common( "Transportation's Instalment", "0", baseImage+"servicemobil.PNG"));
        listSpendingMonth.add(new Common( "Credit Card's Instalment", "0", baseImage+"deposito.PNG"));
        listSpendingMonth.add(new Common( "Insurance", "0", baseImage+"pengeluaranbulanan.PNG"));
        listSpendingMonth.add(new Common( "Servant", "0", baseImage+"pengeluaranlainnya.PNG"));
        listSpendingMonth.add(new Common( "Car (Maintenance and Gasoline)", "0", baseImage+"servicemobil.PNG"));
        listSpendingMonth.add(new Common( "Clothes", "0", baseImage+"pengeluaranlainnya.PNG"));

        listSpending.add(new Common( "Eating Outside's House", "0", baseImage+"profesi.PNG"));
        listSpending.add(new Common( "Luxurious Buying", "0", baseImage+"aktifincome.PNG"));
        listSpending.add(new Common( "Picnic", "0", baseImage+"rekreasi.PNG"));

        listPassiveIncome.add(new Common( "House's rent / Kost", "0", baseImage+"rumahsewa.PNG"));
        listPassiveIncome.add(new Common( "Business", "0", baseImage+"usaha.PNG"));
        listPassiveIncome.add(new Common( "Deposit / MutualFund", "0", baseImage+"deposito.PNG"));
        listPassiveIncome.add(new Common( "Book Royalties", "0", baseImage+"pasifincome.PNG"));
        listPassiveIncome.add(new Common( "Cassete Royalties", "0", baseImage+"lainlain.PNG"));
        listPassiveIncome.add(new Common( "Royaties' System", "0", baseImage+"pengeluaranbulanan.PNG"));

        listActiveIncome.add(new Common( "Occupation", "0", baseImage+"usaha.PNG"));
        listActiveIncome.add(new Common( "Trading", "0", baseImage+"trading.PNG"));
    }

    @OnClick(R.id.btn_to_register)
    public void toRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @OnClick(R.id.btn_to_forgot_password)
    public void toForgotPassword() {
        startActivity(new Intent(getApplicationContext(), ForgotPasswordActivity.class));
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}