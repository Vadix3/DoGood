package com.example.dogood.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.dogood.Dialogs.NewAccountDialog;
import com.example.dogood.Dialogs.NewAccountDialogListener;
import com.example.dogood.MainActivity;
import com.example.dogood.R;
import com.example.dogood.objects.User;
import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Activity_login extends AppCompatActivity implements NewAccountDialogListener {

    private static final String TAG = "Activity_login";

    // Views
    private TextInputLayout login_EDT_mail;
    private TextInputLayout login_EDT_password;
    private TextView        login_LBL_forgot;
    private MaterialButton  login_BTN_login;
    private ImageView       login_IMG_facebookLogin;
    private ImageView       login_IMG_googleLogin;
    private TextView        login_LBL_createCount;

    // Firebase
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Google
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 100;

    //Facebook
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // change color top bar
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorBackroundLogin));

        Log.d(TAG, "onCreate: ");

        findViews();
        initFacebookLogin();
        initGoogleLogin();
        initPasswordLogin();

        login_BTN_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity_login.this, MainActivity.class);
                startActivity(intent);
            }
        });

        login_IMG_googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        login_LBL_createCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });


    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        // Check if sign in before
        if (account != null){
            Log.d(TAG, "GOOGLE: User already signed in: " + account.toString());
           // goToMain(account);
        }else{
            Log.d(TAG, "GOOGLE: User no existe ");
        }

        /**Firebase*/
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Log.d(TAG, "onStart: FIREBASE: User not signed in yet");
        } else {
            Log.d(TAG, "onStart: FIREBASE: User logged in");
            int check = getIntent().getIntExtra("LOGGED_OUT", 0);
            if (check != 1) {
                Log.d(TAG, "onStart: Did not came from main, logging in");
                Intent intent = new Intent(Activity_login.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Log.d(TAG, "onStart: Came from main, not logging in");
                mAuth.signOut();
            }
        }
    }

    private void openDialog() {
        NewAccountDialog newAccountDialog = new NewAccountDialog(this);
        newAccountDialog.show();
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.8);
        int width = (int) (getResources().getDisplayMetrics().widthPixels* 0.9);
        newAccountDialog.getWindow().setLayout(width,height);
        newAccountDialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        newAccountDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        newAccountDialog.getWindow().setDimAmount(1f);
    }


    private void goToMain(GoogleSignInAccount account) {
        Log.d(TAG, "goToMain: ");

        String displayName = account.getDisplayName();
        String email = account.getEmail();

        Intent intent = new Intent(Activity_login.this, MainActivity.class);
        intent.putExtra("email",email);
        intent.putExtra("name",displayName);
        startActivity(intent);
    }

    private void initFacebookLogin() {
        Log.d(TAG, "initFacebookLogin: ");

        callbackManager = CallbackManager.Factory.create();
    }

    private void initGoogleLogin() {
        Log.d(TAG, "initGoogleLogin: ");
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        // Build a GoogleSignInClient with the options specified by gso.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void initPasswordLogin() {
        Log.d(TAG, "initPasswordLogin: ");


    }





    private void findViews() {

        mAuth = FirebaseAuth.getInstance();

        login_EDT_mail = findViewById(R.id.login_EDT_mail);
        login_EDT_password = findViewById(R.id.login_EDT_password);
        login_LBL_forgot = findViewById(R.id.login_LBL_forgot);
        login_BTN_login = findViewById(R.id.login_BTN_login);
        login_IMG_facebookLogin = findViewById(R.id.login_IMG_facebookLogin);
        login_IMG_googleLogin = findViewById(R.id.login_IMG_googleLogin);
        login_LBL_createCount = findViewById(R.id.login_LBL_createCount);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        callbackManager.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        Log.d(TAG, "handleSignInResult: Sign in from google");
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            Log.d(TAG, "handleSignInResult: Sign in successful: " + account.toString());
            String displayName = account.getDisplayName();
            String email = account.getEmail();

            Intent intent = new Intent(Activity_login.this, MainActivity.class);
            intent.putExtra("email",email);
            intent.putExtra("name",displayName);
            startActivity(intent);
            finish();

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG, "handleSignInResult: Exception: " + e.toString());
        }
    }

    @Override
    public void getInfoUser(User newUser) {
        Log.d(TAG, "getInfoUser: ") ;

    }
}
