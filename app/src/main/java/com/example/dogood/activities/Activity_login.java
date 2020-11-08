package com.example.dogood.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.dogood.Dialogs.NewAccountDialog;
import com.example.dogood.interfaces.NewAccountDialogListener;
import com.example.dogood.MainActivity;
import com.example.dogood.R;
import com.example.dogood.objects.User;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Activity_login extends AppCompatActivity implements NewAccountDialogListener {

    private static final String TAG = "Dogood";
    private static final String LOGIN_USER_EXTRA = "loginUser";

    private User myUser = new User(); // A user to move with to the main page

    // Views
    private TextInputLayout login_EDT_mail;
    private TextInputLayout login_EDT_password;
    private TextView login_LBL_forgot;
    private MaterialButton login_BTN_login;
    private ImageView login_IMG_facebookLogin;
    private ImageView login_IMG_googleLogin;
    private TextView login_LBL_createCount;

    // Firebase
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Google
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 100;

    //Facebook
    private CallbackManager callbackManager;

    private boolean testing = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // change color top bar
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorBackroundLogin));

        Log.d(TAG, "onCreate: ");

        findViews();
        initFacebookLogin();
        initGoogleLogin();
        initPasswordLogin();

        login_BTN_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_BTN_login.setClickable(false);
                checkForValidLoginInfo();
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

    /**
     * A method to check for valid user login information
     */
    private void checkForValidLoginInfo() {
        Log.d(TAG, "checkForValidLoginInfo: ");

        //Checking valid text
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(login_EDT_mail.getEditText().getText().toString()).matches()) {
            Log.d(TAG, "checkForValidInputs: Email invalid");
            login_EDT_mail.setError(getString(R.string.enter_email_error));
            login_BTN_login.setClickable(true);
            return;
        }
        if (login_EDT_password.getEditText().getText().toString().equals("")
                || login_EDT_password.getEditText().getText().toString().length() < 6) {
            if (login_EDT_password.getEditText().getText().toString().length() < 6) {
                Log.d(TAG, "checkForValidInputs: short password");
                login_EDT_password.setError(getString(R.string.six_chars_password_error));
                login_BTN_login.setClickable(true);
                return;
            } else {
                Log.d(TAG, "checkForValidInputs: invalid password");
                login_EDT_password.setError(getString(R.string.null_password));
                login_BTN_login.setClickable(true);
                return;
            }
        }
        // After the credentials are well formatted, check for validity
        loginUserWithEmailAndPassword();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        // Check if sign in before
        if (account != null) {
            Log.d(TAG, "GOOGLE: User already signed in: " + account.getDisplayName());
        } else {
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
//                Intent intent = new Intent(Activity_login.this, MainActivity.class);
//                startActivity(intent);
//                finish();
                //TODO: RETURN AUTO LOGIN
                mAuth.signOut();
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
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        newAccountDialog.getWindow().setLayout(width, height);
        newAccountDialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        newAccountDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        newAccountDialog.getWindow().setDimAmount(1f);
    }

    private void initFacebookLogin() {
        //TODO: Problem with test accounts login, can login only with my account

        Log.d(TAG, "initFacebookLogin: ");
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "onSuccess: Facebook success: " + loginResult.toString());
                //Request the users info
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken()
                        , new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.d(TAG, "onCompleted: response: " + response.toString());
                                Log.d(TAG, "onCompleted: json: " + object.toString());
                                try {
                                    String name = ""+object.get("name");
                                    String email = ""+object.get("email");
                                    User temp = new User(name,email);
                                    Gson gson = new Gson();
                                    String userJson = gson.toJson(temp);
                                    Intent loginIntent = new Intent(Activity_login.this,MainActivity.class);
                                    loginIntent.putExtra(LOGIN_USER_EXTRA,userJson);
                                    startActivity(loginIntent);
                                    finish();
                                    //TODO: remove logout
                                    LoginManager.getInstance().logOut();
                                } catch (JSONException e) {
                                    Log.d(TAG, "onCompleted: Exception: " + e.getMessage());
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel: Facebook cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d(TAG, "onError: Exception: " + exception.getMessage());
            }
        });


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
        //TODO: Remove the signout
        mGoogleSignInClient.signOut();
    }

    private void initPasswordLogin() {
        Log.d(TAG, "initPasswordLogin: ");


    }


    private void findViews() {

        mAuth = FirebaseAuth.getInstance();

        login_EDT_mail = findViewById(R.id.login_EDT_mail);
        login_EDT_mail.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                login_EDT_mail.setErrorEnabled(false); // disable error
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        login_EDT_password = findViewById(R.id.login_EDT_password);
        login_EDT_password.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                login_EDT_password.setErrorEnabled(false); // disable error
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        login_LBL_forgot = findViewById(R.id.login_LBL_forgot);
        login_BTN_login = findViewById(R.id.login_BTN_login);
        login_IMG_facebookLogin = findViewById(R.id.login_IMG_facebookLogin);
        login_IMG_facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(Activity_login.this, Arrays.asList("public_profile", "user_friends", "email"));
            }
        });
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
            Gson gson = new Gson();
            User temp = new User(displayName, email);
            String userJson = gson.toJson(temp);
            intent.putExtra(LOGIN_USER_EXTRA, userJson);
            startActivity(intent);
            finish();


        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(TAG, "handleSignInResult: Exception: " + e.toString());
        }
    }

    /**
     * A callback method to get the user from the new user dialog
     */
    @Override
    public void getInfoUser(User newUser) {
        Log.d(TAG, "getInfoUser: Got user: " + newUser.toString());
        myUser = newUser;
        afterGotNewUser();
    }

    /**
     * A method to deal with new user details
     */
    private void afterGotNewUser() {
        Log.d(TAG, "afterGotNewUser: Dealing with new user: " + myUser.toString());
        mAuth.createUserWithEmailAndPassword(myUser.getEmail(), myUser.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            setFirebaseUserParams(user);
                            login_EDT_mail.getEditText().setText(myUser.getEmail());
                            login_EDT_password.getEditText().setText(myUser.getPassword());
                            Toast.makeText(Activity_login.this, "User Created Successfully!"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // If sign in fails, display a message to the user.
                Log.d(TAG, "createUserWithEmail:failure" + e.getMessage());
                login_EDT_mail.getEditText().setText("");
                login_EDT_password.getEditText().setText("");
                if (e.getMessage().equalsIgnoreCase("The email address is already in use by another account.")) {
                    Toast.makeText(Activity_login.this, "There is an account with that email!"
                            , Toast.LENGTH_SHORT).show();
                    login_EDT_mail.getEditText().setText(myUser.getEmail());
                }
                if (e.getMessage().equalsIgnoreCase("The email address is badly formatted.")) {
                    Log.d(TAG, "createUserWithEmail: Failure: email badly formatted");
                    Toast.makeText(Activity_login.this, "Please enter a valid email address"
                            , Toast.LENGTH_SHORT).show();
                    login_BTN_login.setClickable(true);
                }

            }
        });
    }

    /**
     * A method to set the firebase users parameters
     */
    private void setFirebaseUserParams(FirebaseUser user) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(myUser.getName())
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile updated, Name: " + user.getDisplayName());
                            fireBasePost();
                        }
                    }
                });
    }

    /**
     * A method to post new user to firebase firestore
     */
    private void fireBasePost() {
        Log.d(TAG, "fireBasePost: Posting user to firebase");
        String emailID = myUser.getEmail();
        db.collection("users")
                .document(emailID)
                .set(myUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: User saved successfully!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Exception: " + e.getMessage());
            }
        });
    }

    /**
     * A Method to log in user with email and password
     */
    private void loginUserWithEmailAndPassword() {
        Log.d(TAG, "loginUserWithEmailAndPassword: Logging in user with email and password");
        mAuth.signInWithEmailAndPassword(login_EDT_mail.getEditText().getText().toString()
                , login_EDT_password.getEditText().getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(Activity_login.this, MainActivity.class);
                            Gson gson = new Gson();
                            User temp = new User(user.getDisplayName(), user.getEmail());
                            String jsonUser = gson.toJson(temp);
                            Log.d(TAG, "onComplete: Sending string user to main: " + jsonUser);
                            intent.putExtra(LOGIN_USER_EXTRA, jsonUser);
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            Exception e = task.getException();
                            String errorMsg = e.getMessage();
                            Log.w(TAG, "signInWithEmail:failure " + errorMsg);
                            login_BTN_login.setClickable(true);
                            if(e.getMessage().equalsIgnoreCase(
                                    "An internal error has occurred. [ Unable to resolve" +
                                            " host \"www.googleapis.com\":No address associated with hostname ]")){
                                Toast.makeText(Activity_login.this, "Connection Timeout", Toast.LENGTH_SHORT).show();
                            }
                            if (e.getMessage().equalsIgnoreCase(
                                    "The password is invalid or the user does not have a password.")) {
                                login_EDT_password.setError("Wrong password");
                            }
                            if (e.getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                login_EDT_mail.setError("Email address does not exist!");
                            }
                        }
                    }
                });
    }

}
