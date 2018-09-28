package org.properti.analisa.financialcheck.firebase;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.properti.analisa.financialcheck.activity.MainActivity;
import org.properti.analisa.financialcheck.activity.auth.LoginActivity;

public class FirebaseApplication extends Application {

    private static final String TAG = FirebaseApplication.class.getSimpleName();

    public FirebaseAuth firebaseAuth;

    public FirebaseAuth.AuthStateListener mAuthListener;

    public FirebaseAuth getFirebaseAuth(){
        return firebaseAuth = FirebaseAuth.getInstance();
    }

    public String getFirebaseUserAuthenticateId() {
        String userId = null;
        if(firebaseAuth.getCurrentUser() != null){
            userId = firebaseAuth.getCurrentUser().getUid();
        }
        return userId;
    }

    public void checkUserLogin(final Context context){
        if(firebaseAuth.getCurrentUser() != null){
            Intent profileIntent = new Intent(context, MainActivity.class);
            context.startActivity(profileIntent);
            ((Activity) context).finish();
        }
    }

    public void isUserCurrentlyLogin(final Context context){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(null != user){
                    Intent profileIntent = new Intent(context, MainActivity.class);
                    context.startActivity(profileIntent);
                    Log.e("SESSION", "ada");
                }
                else{
                    Intent loginIntent = new Intent(context, LoginActivity.class);
                    context.startActivity(loginIntent);
                    Log.e("SESSION", "gak ada");
                }
            }
        };
    }

    public void createNewUser(final Context context, String email, String password){
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Toast.makeText(context, "Failed to login. Invalid user", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(context, "Registration successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void loginAUser(final Context context, String email, String password){
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity)context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail", task.getException());
                            Toast.makeText(context, "Failed to login", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent profileIntent = new Intent(context, MainActivity.class);
                            context.startActivity(profileIntent);
                            ((Activity) context).finish();
                        }
                    }
                });
    }

    public void resetPassword(final Context context, String email){
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
