package org.kore.kolabnotes.android.security;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import org.kore.kolab.notes.AccountInformation;
import org.kore.kolabnotes.android.MainActivity;
import org.kore.kolabnotes.android.R;
import org.kore.kolabnotes.android.Utils;
import org.kore.kolabnotes.android.content.ActiveAccount;
import org.kore.kolabnotes.android.content.ActiveAccountRepository;

/**
 * The Authenticator activity.
 *
 * Called by the Authenticator and in charge of identifing the user.
 *
 * It sends back to the Authenticator the result.
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity {

    public final static String ARG_ACCOUNT_TYPE = "kore.kolabnotes";

    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long MINUTES_PER_HOUR = 60L;
    public static final long HOURS_PER_DAY = 24L;

    public final static String KEY_ACCOUNT_NAME = "account_name";
    public final static String KEY_ROOT_FOLDER = "root_folder";
    public final static String KEY_SERVER = "server_url";
    public final static String KEY_EMAIL = "email";
    public final static String KEY_PORT = "port";
    public final static String KEY_SSL = "enablessl";
    public final static String KEY_KOLAB = "enablekolab";
    public final static String KEY_INTERVALL_TYPE = "intervalltype";
    public final static String KEY_INTERVALL = "intervall";
    public final static String KEY_ACCOUNT_TYPE = "accounttype";
    public final static String KEY_SHARED_FOLDERS = "shared_folders";

    public final static int ID_ACCOUNT_TYPE_KOLAB = 1;
    public final static int ID_ACCOUNT_TYPE_KOLABNOW = 0;
    public final static int ID_ACCOUNT_TYPE_IMAP = 2;


    private final String TAG = this.getClass().getSimpleName();

    private AccountManager mAccountManager;

    private Switch mExtendedOptions;
    private EditText mRootFolderView;
    private EditText mPortView;
    private CheckBox mEnableSSLView;
    private EditText mSyncView;
    private Switch mKolabView;
    private Switch mSharedFoldersView;
    private EditText mIMAPServerView;
    private Spinner mAccountType;
    private Spinner mIntervallType;

    private EditText mAccountNameView;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    private Intent startIntent;

    private Account accountToChange;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kolab_login);
        mAccountManager = AccountManager.get(getBaseContext());

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        mPortView = (EditText) findViewById(R.id.port_number);
        mEnableSSLView = (CheckBox) findViewById(R.id.enable_ssl);
        mSyncView = (EditText) findViewById(R.id.sync_intervall);
        mKolabView = (Switch) findViewById(R.id.enable_kolab);
        mSharedFoldersView = (Switch) findViewById(R.id.enable_shared_folders);
        mRootFolderView = (EditText)findViewById(R.id.imap_root_folder);
        mIMAPServerView = (EditText)findViewById(R.id.imap_server_url);
        mAccountType = (Spinner)findViewById(R.id.spinner_accountType);
        mIntervallType = (Spinner)findViewById(R.id.spinner_intervall);

        mAccountNameView = (EditText)findViewById(R.id.accountName);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.accountPassword);

        mExtendedOptions = (Switch) findViewById(R.id.enable_more_config);
        mExtendedOptions.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                   showExtendedOptions();
                }else{
                    hideExtendedOptions();
                }
            }
        });

        initAccountTypeSpinner();
        initIntervallSpinner();

        hideExtendedOptions();

        startIntent = getIntent();
        if(startIntent != null && startIntent.getBooleanExtra("changeAccount",false)){
            ActiveAccountRepository accountRepo = new ActiveAccountRepository(this);
            ActiveAccount activeAccount = accountRepo.getActiveAccount();
            if(activeAccount.getAccount().equals("local") && activeAccount.getRootFolder().equals("Notes")) {
                Toast.makeText(this,R.string.local_account_change,Toast.LENGTH_LONG).show();
                finish();
            }else {
                initViews(activeAccount.getAccount(), activeAccount.getRootFolder());
            }
        }
    }

    private void initViews(String email, String rootFolder){
       if((email == null || "local".equals(email)) && (rootFolder == null || "Notes".equals(rootFolder)) ){
           return;
       }

        Account[] accounts = mAccountManager.getAccountsByType(AuthenticatorActivity.ARG_ACCOUNT_TYPE);

        for (Account acc : accounts) {
            String pemail = mAccountManager.getUserData(acc,AuthenticatorActivity.KEY_EMAIL);
            String name = mAccountManager.getUserData(acc,AuthenticatorActivity.KEY_ACCOUNT_NAME);
            String prootFolder = mAccountManager.getUserData(acc,AuthenticatorActivity.KEY_ROOT_FOLDER);

            if(pemail.equals(email) && prootFolder.equals(rootFolder)){
                accountToChange = acc;

                String accType = mAccountManager.getUserData(acc,AuthenticatorActivity.KEY_ACCOUNT_TYPE);
                if(accType != null) {
                    mAccountType.setSelection(Integer.parseInt(accType));
                }
                mAccountType.setVisibility(View.GONE);

                mAccountNameView.setText(name);
                mRootFolderView.setText(prootFolder);
                mRootFolderView.setFocusable(false);
                mEmailView.setText(pemail);
                mEmailView.setFocusable(false);
                mPasswordView.setText(mAccountManager.getPassword(acc));

                final String isKolab = mAccountManager.getUserData(acc, AuthenticatorActivity.KEY_KOLAB);
                final String port = mAccountManager.getUserData(acc, AuthenticatorActivity.KEY_PORT);
                final String server = mAccountManager.getUserData(acc, AuthenticatorActivity.KEY_SERVER);
                final String isSSL = mAccountManager.getUserData(acc, AuthenticatorActivity.KEY_SSL);
                final String isSharedFolders = mAccountManager.getUserData(acc, AuthenticatorActivity.KEY_SHARED_FOLDERS);
                final String intervallType = mAccountManager.getUserData(acc, AuthenticatorActivity.KEY_INTERVALL_TYPE);
                final String intervall = mAccountManager.getUserData(acc, AuthenticatorActivity.KEY_INTERVALL);

                mIMAPServerView.setText(server);
                mPortView.setText(port);

                int type = intervallType == null || intervallType.trim().length() == 0 ? 1 : Integer.parseInt(intervallType);
                mIntervallType.setSelection(type);

                long intervalLength = intervall == null || intervall.trim().length() == 0 ? 24L : Long.parseLong(intervall);
                if(intervalLength == 24) {
                    mSyncView.setText(Long.toString(intervalLength));
                }else{
                    mSyncView.setText(Long.toString(divideIntervall(intervalLength)));
                }

                mKolabView.setChecked(Boolean.parseBoolean(isKolab));
                mEnableSSLView.setChecked(Boolean.parseBoolean(isSSL));
                mSharedFoldersView.setChecked(Boolean.parseBoolean(isSharedFolders));

                return;
            }
        }
    }

    void initAccountTypeSpinner(){
        String[] values = {"KolabNow","Kolab Server","IMAP Server"};

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, R.layout.accounttype_spinner_item, values);
        adapter.setDropDownViewResource(R.layout.accounttype_spinner_item);
        mAccountType.setAdapter(adapter);
        mAccountType.setSelection(0);
        mAccountType.setOnItemSelectedListener(new AccountTypeSelectedListener());
    }

    void initIntervallSpinner(){
        String[] values = getResources().getStringArray(R.array.sync_intervall_types);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, R.layout.intervall_spinner_item, values);
        adapter.setDropDownViewResource(R.layout.intervall_spinner_item);
        mIntervallType.setAdapter(adapter);
        mIntervallType.setSelection(1);
    }

    void showExtendedOptions(){
        mPortView.setVisibility(View.VISIBLE);
        mEnableSSLView.setVisibility(View.VISIBLE);
        mSyncView.setVisibility(View.VISIBLE);
        mKolabView.setVisibility(View.VISIBLE);
        mSharedFoldersView.setVisibility(View.VISIBLE);
        mRootFolderView.setVisibility(View.VISIBLE);
        mIntervallType.setVisibility(View.VISIBLE);
    }

    void hideExtendedOptions(){
        mPortView.setVisibility(View.INVISIBLE);
        mEnableSSLView.setVisibility(View.INVISIBLE);
        mSyncView.setVisibility(View.INVISIBLE);
        mKolabView.setVisibility(View.INVISIBLE);
        mSharedFoldersView.setVisibility(View.INVISIBLE);
        mRootFolderView.setVisibility(View.INVISIBLE);
        mIntervallType.setVisibility(View.INVISIBLE);
    }

    public void submit() {

        Log.d("kolabnotes", TAG + "> Started authenticating");

        Bundle data = new Bundle();
        try {

            // Reset errors.
            mEmailView.setError(null);
            mPasswordView.setError(null);
            mAccountNameView.setError(null);
            mRootFolderView.setError(null);
            mIMAPServerView.setError(null);
            mSyncView.setError(null);

            // Store values at the time of the login attempt.
            String email = mEmailView.getText().toString();
            String password = mPasswordView.getText().toString();
            String accountName = mAccountNameView.getText().toString();
            String rootFolder = mRootFolderView.getText() == null || mRootFolderView.getText().toString().trim().length() == 0 ? "Notes" : mRootFolderView.getText().toString();
            String imapServer = mIMAPServerView.getText().toString();
            int syncIntervall = mSyncView.getText() == null || mSyncView.getText().toString().trim().length() == 0 ? 24 : Integer.valueOf(mSyncView.getText().toString());
            int port = mPortView.getText() == null || mPortView.getText().toString().trim().length() == 0 ? 993 : Integer.valueOf(mPortView.getText().toString());

            boolean cancel = false;
            View focusView = null;

            // Check for a valid email address.
            if (TextUtils.isEmpty(accountName)) {
                mAccountNameView.setError(getString(R.string.error_field_required));
                focusView = mAccountNameView;
                cancel = true;
            }else if (TextUtils.isEmpty(imapServer)) {
                mIMAPServerView.setError(getString(R.string.error_field_required));
                focusView = mIMAPServerView;
                cancel = true;
            }else if (TextUtils.isEmpty(email)) {
                mEmailView.setError(getString(R.string.error_field_required));
                focusView = mEmailView;
                cancel = true;
            }else if (TextUtils.isEmpty(password)) {
                mPasswordView.setError(getString(R.string.error_field_required));
                focusView = mPasswordView;
                cancel = true;
            }else if (syncIntervall < 1 || syncIntervall > 90) {
                mSyncView.setError(getString(R.string.error_field_to_low));
                focusView = mSyncView;
                cancel = true;
            }


            if (cancel) {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else {

                AccountInformation.Builder builder = AccountInformation.createForHost(imapServer).username(email).password(password).port(port);

                if(!mEnableSSLView.isChecked()){
                    builder.disableSSL();
                }

                if(!mKolabView.isChecked()){
                    builder.disableFolderAnnotation();
                }

                if(mSharedFoldersView.isChecked()){
                    builder.enableSharedFolders();
                }

                AccountInformation accountInformation = builder.build();
                KolabAccount serverInfo = new KolabAccount(accountName,rootFolder,accountInformation);

                final long intervall = calculateIntervall(syncIntervall);

                if(accountToChange == null) {
                    Account account = new Account(accountName, ARG_ACCOUNT_TYPE);

                    Bundle userData = createAuthBundle(serverInfo,intervall);

                    if (mAccountManager.addAccountExplicitly(account, password, userData)) {
                        new ActiveAccountRepository(this).insertAccount(email, rootFolder);
                        Toast.makeText(getBaseContext(), R.string.signup_ok, Toast.LENGTH_SHORT).show();

                        ContentResolver.setIsSyncable(account, MainActivity.AUTHORITY, 1);
                        ContentResolver.setSyncAutomatically(account, MainActivity.AUTHORITY, true);


                        ContentResolver.addPeriodicSync(account,
                                MainActivity.AUTHORITY,
                                Bundle.EMPTY,
                                intervall
                        );

                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(getBaseContext(), R.string.error_duplicate_account, Toast.LENGTH_LONG).show();
                    }
                }else{
                    mAccountManager.setPassword(accountToChange, password);
                    setAuthBundle(accountToChange, serverInfo, intervall);

                    ContentResolver.addPeriodicSync(accountToChange,
                            MainActivity.AUTHORITY,
                            Bundle.EMPTY,
                            intervall
                    );

                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }
            }

        } catch (Exception e) {
            Toast.makeText(getBaseContext(), R.string.error_generic, Toast.LENGTH_LONG).show();
        }
    }

    private long calculateIntervall(long given){
        if(mIntervallType.getSelectedItemPosition() == 0){
            return given*SECONDS_PER_MINUTE;
        }else if(mIntervallType.getSelectedItemPosition() == 1){
            return given*MINUTES_PER_HOUR*SECONDS_PER_MINUTE;
        }
        return given*HOURS_PER_DAY*MINUTES_PER_HOUR*SECONDS_PER_MINUTE;
    }

    private long divideIntervall(long given){
        if(mIntervallType.getSelectedItemPosition() == 0){
            return given/SECONDS_PER_MINUTE;
        }else if(mIntervallType.getSelectedItemPosition() == 1){
            return given/MINUTES_PER_HOUR/SECONDS_PER_MINUTE;
        }
        return given/HOURS_PER_DAY/MINUTES_PER_HOUR/SECONDS_PER_MINUTE;
    }

    private Bundle createAuthBundle(KolabAccount kolabAccount, long intervall) {
        final String ssl = Boolean.toString(kolabAccount.getAccountInformation().isSSLEnabled());
        final String kolab = Boolean.toString(kolabAccount.getAccountInformation().isFolderAnnotationEnabled());
        final String shared = Boolean.toString(kolabAccount.getAccountInformation().isSharedFoldersEnabled());
        Bundle bundle = new Bundle();
        bundle.putString(KEY_ACCOUNT_NAME, kolabAccount.getAccountName());
        bundle.putString(KEY_ROOT_FOLDER, kolabAccount.getRootFolder());
        bundle.putString(KEY_SERVER, kolabAccount.getAccountInformation().getHost());
        bundle.putString(KEY_EMAIL, kolabAccount.getAccountInformation().getUsername());
        bundle.putString(KEY_PORT, Integer.toString(kolabAccount.getAccountInformation().getPort()));
        bundle.putString(KEY_SSL, ssl);
        bundle.putString(KEY_KOLAB, kolab);
        bundle.putString(KEY_SHARED_FOLDERS, shared);

        bundle.putString(KEY_ACCOUNT_TYPE,Integer.toString(mAccountType.getSelectedItemPosition()));
        bundle.putString(KEY_INTERVALL_TYPE,Integer.toString(mIntervallType.getSelectedItemPosition()));
        bundle.putString(KEY_INTERVALL,Long.toString(intervall));

        return bundle;
    }

    private void setAuthBundle(Account account, KolabAccount kolabAccount, long intervall) {
        final String ssl = Boolean.toString(kolabAccount.getAccountInformation().isSSLEnabled());
        final String kolab = Boolean.toString(kolabAccount.getAccountInformation().isFolderAnnotationEnabled());
        final String shared = Boolean.toString(kolabAccount.getAccountInformation().isSharedFoldersEnabled());

        mAccountManager.setUserData(account, KEY_ACCOUNT_NAME, kolabAccount.getAccountName());
        mAccountManager.setUserData(account, KEY_ROOT_FOLDER, kolabAccount.getRootFolder());
        mAccountManager.setUserData(account, KEY_SERVER, kolabAccount.getAccountInformation().getHost());
        mAccountManager.setUserData(account, KEY_EMAIL, kolabAccount.getAccountInformation().getUsername());
        mAccountManager.setUserData(account, KEY_PORT, Integer.toString(kolabAccount.getAccountInformation().getPort()));
        mAccountManager.setUserData(account, KEY_SSL, ssl);
        mAccountManager.setUserData(account, KEY_KOLAB, kolab);
        mAccountManager.setUserData(account, KEY_SHARED_FOLDERS, shared);
        mAccountManager.setUserData(account, KEY_ACCOUNT_TYPE, Integer.toString(mAccountType.getSelectedItemPosition()));
        mAccountManager.setUserData(account, KEY_INTERVALL_TYPE, Integer.toString(mIntervallType.getSelectedItemPosition()));
        mAccountManager.setUserData(account, KEY_INTERVALL, Long.toString(intervall));
    }

    class AccountTypeSelectedListener implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if(position == ID_ACCOUNT_TYPE_KOLABNOW){
                setKolabNowValues();
            }else if(position == ID_ACCOUNT_TYPE_KOLAB){
                setKolabValues();
            }else{
                setIMAPValues();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //nothing
        }

        private void setKolabNowValues(){
            mPortView.setText("993");
            mEnableSSLView.setChecked(true);
            mKolabView.setChecked(true);
            mSharedFoldersView.setChecked(false);
            mIMAPServerView.setText("imap.kolabnow.com");
        }

        private void setKolabValues(){
            mKolabView.setChecked(true);
            mSharedFoldersView.setChecked(false);
            mIMAPServerView.setText("");
        }

        private void setIMAPValues(){
            mKolabView.setChecked(false);
            mSharedFoldersView.setChecked(false);
            mIMAPServerView.setText("");
        }
    }
}