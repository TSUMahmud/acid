package org.kore.kolabnotes.android.widget;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;

import org.kore.kolab.notes.Notebook;
import org.kore.kolab.notes.SharedNotebook;
import org.kore.kolabnotes.android.R;
import org.kore.kolabnotes.android.Utils;
import org.kore.kolabnotes.android.content.DatabaseHelper;
import org.kore.kolabnotes.android.content.NoteSorting;
import org.kore.kolabnotes.android.content.NotebookRepository;
import org.kore.kolabnotes.android.content.TagRepository;
import org.kore.kolabnotes.android.security.AuthenticatorActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * The configuration screen for the {@link ListWidget ListWidget} AppWidget.
 */
public class ListWidgetConfigureActivity extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    public static final String PREFS_NAME = "org.kore.kolabnotes.android.widget.ListWidget";
    public static final String PREF_PREFIX_KEY_NOTEBOOK = "appwidget_notebook_";
    public static final String PREF_PREFIX_KEY_TAG = "appwidget_tag_";
    public static final String PREF_PREFIX_KEY_DIRECTION = "appwidget_direction_";
    public static final String PREF_PREFIX_KEY_COLUMN = "appwidget_column_";
    public static final String PREF_PREFIX_KEY_ACCOUNT = "appwidget_account_";

    private TagRepository tagRepository = new TagRepository(this);
    private NotebookRepository notebookRepository = new NotebookRepository(this);
    private AccountManager mAccountManager;
    private Spinner accountSpinner;
    private Spinner notebookSpinner;
    private Spinner tagSpinner;
    private Spinner columnSpinner;
    private RadioButton ascButton;
    private RadioButton descButton;

    private Account selectedAccount;
    private String selectedNotebook;
    private String selectedTag;
    private NoteSorting.Direction selectedDirection;
    private String selectedColumn;

    private String localAccountName;

    public ListWidgetConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.list_widget_configure);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        accountSpinner = (Spinner) findViewById(R.id.spinner_account);
        notebookSpinner = (Spinner) findViewById(R.id.spinner_notebook);
        tagSpinner = (Spinner) findViewById(R.id.spinner_tag);
        columnSpinner = (Spinner) findViewById(R.id.spinner_sort_column);
        ascButton = (RadioButton) findViewById(R.id.radio_asc);
        descButton = (RadioButton) findViewById(R.id.radio_desc);

        localAccountName = getResources().getString(R.string.drawer_account_local);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        mAccountManager = AccountManager.get(this);
        initSpinners();
        descButton.toggle();
        selectedColumn = DatabaseHelper.COLUMN_MODIFICATIONDATE;
        selectedDirection = NoteSorting.Direction.DESC;

        descButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDirection = NoteSorting.Direction.DESC;
            }
        });

        ascButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDirection = NoteSorting.Direction.ASC;
            }
        });
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = ListWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            if(selectedAccount == null){
                saveListWidgetPref(context, mAppWidgetId, "local", selectedNotebook, selectedTag, new NoteSorting(Utils.SortingColumns.findValue(selectedColumn),selectedDirection));
            }else{
                saveListWidgetPref(context, mAppWidgetId, mAccountManager.getUserData(selectedAccount, AuthenticatorActivity.KEY_ACCOUNT_NAME), selectedNotebook, selectedTag, new NoteSorting(Utils.SortingColumns.findValue(selectedColumn),selectedDirection));
            }

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ListWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    static void saveListWidgetPref(Context context, int appWidgetId,String accountName, String notebook,String tag, NoteSorting noteSorting) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY_ACCOUNT + appWidgetId, accountName);
        prefs.putString(PREF_PREFIX_KEY_NOTEBOOK + appWidgetId, notebook);
        prefs.putString(PREF_PREFIX_KEY_TAG + appWidgetId, tag);
        prefs.putString(PREF_PREFIX_KEY_DIRECTION + appWidgetId, noteSorting.getDirection().toString());
        prefs.putString(PREF_PREFIX_KEY_COLUMN + appWidgetId, noteSorting.getColumnName());
        prefs.commit();
    }

    static String loadListWidgetAccountPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(PREF_PREFIX_KEY_ACCOUNT + appWidgetId, null);
    }

    static String loadListWidgetNotebookPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(PREF_PREFIX_KEY_NOTEBOOK + appWidgetId, null);
    }

    static String loadListWidgetTagPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return prefs.getString(PREF_PREFIX_KEY_TAG + appWidgetId, null);
    }

    static NoteSorting loadListWidgetOrderingPref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String direction = prefs.getString(PREF_PREFIX_KEY_DIRECTION + appWidgetId, null);
        String column = prefs.getString(PREF_PREFIX_KEY_COLUMN + appWidgetId, null);

        if(TextUtils.isEmpty(direction) || TextUtils.isEmpty(column)){
            return new NoteSorting();
        }
        return new NoteSorting(Utils.SortingColumns.findValue(column), NoteSorting.Direction.valueOf(direction));
    }

    static void deleteListWidgetPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY_ACCOUNT + appWidgetId);
        prefs.remove(PREF_PREFIX_KEY_NOTEBOOK + appWidgetId);
        prefs.remove(PREF_PREFIX_KEY_TAG + appWidgetId);
        prefs.remove(PREF_PREFIX_KEY_DIRECTION + appWidgetId);
        prefs.remove(PREF_PREFIX_KEY_COLUMN + appWidgetId);
        prefs.commit();
    }

    void initSpinners(){
        initAccountSpinner();
        updateNotebookSpinner();
        updateTagSpinner();
        Utils.initColumnSpinner(this, columnSpinner,R.layout.widget_config_spinner_item, new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedColumn = Utils.getColumnNameOfSelection(adapterView.getSelectedItemPosition());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //nothing
            }
        });
    }

    void initAccountSpinner(){
        Account[] accounts = mAccountManager.getAccountsByType(AuthenticatorActivity.ARG_ACCOUNT_TYPE);

        String[] accountNames = new String[accounts.length+1];

        accountNames[0] = localAccountName;
        for(int i=0; i< accounts.length;i++){
            accountNames[i+1] = mAccountManager.getUserData(accounts[i],AuthenticatorActivity.KEY_ACCOUNT_NAME);
        }

        Arrays.sort(accountNames,1,accountNames.length);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,R.layout.widget_config_spinner_item,accountNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSpinner.setAdapter(adapter);
        accountSpinner.setOnItemSelectedListener(new OnAccountItemClicked());
        accountSpinner.setSelection(0);
    }

    class OnAccountItemClicked implements AdapterView.OnItemSelectedListener{

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String name = parent.getSelectedItem().toString();

            if(localAccountName.equalsIgnoreCase(name)){
                selectedAccount = null;
            }else{
                Account[] accounts = mAccountManager.getAccountsByType(AuthenticatorActivity.ARG_ACCOUNT_TYPE);

                for(Account account : accounts){
                    if(name.equals(mAccountManager.getUserData(account, AuthenticatorActivity.KEY_ACCOUNT_NAME))){
                        selectedAccount = account;
                        break;
                    }
                }
            }

            updateNotebookSpinner();
            updateTagSpinner();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //nothing
        }
    }

    void updateNotebookSpinner(){
        String rootFolder;
        String email;
        if(selectedAccount == null){
            rootFolder = "Notes";
            email = "local";
        }else{
            rootFolder = mAccountManager.getUserData(selectedAccount,AuthenticatorActivity.KEY_ROOT_FOLDER);
            email = mAccountManager.getUserData(selectedAccount,AuthenticatorActivity.KEY_EMAIL);
        }

        List<Notebook> notebooks = new ArrayList<>(notebookRepository.getAll(email, rootFolder));

        Collections.sort(notebooks);

        String[] notebookArr = new String[notebooks.size()+1];
        notebookArr[0] = getResources().getString(R.string.no_selection);
        for(int i=0; i<notebooks.size();i++){
            String summary = notebooks.get(i).getSummary();

            if(notebooks.get(i).isShared()){
                summary = ((SharedNotebook)notebooks.get(i)).getShortName();
            }

            notebookArr[i+1] = summary;
        }

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, R.layout.widget_config_spinner_item, notebookArr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notebookSpinner.setAdapter(adapter);
        notebookSpinner.setSelection(0);
        notebookSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedNotebook = parent.getSelectedItem().toString();

                if(getResources().getString(R.string.no_selection).equals(selectedNotebook)){
                    selectedNotebook = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //nothing
            }
        });
    }

    void updateTagSpinner(){
        String rootFolder;
        String email;
        if(selectedAccount == null){
            rootFolder = "Notes";
            email = "local";
        }else{
            rootFolder = mAccountManager.getUserData(selectedAccount,AuthenticatorActivity.KEY_ROOT_FOLDER);
            email = mAccountManager.getUserData(selectedAccount,AuthenticatorActivity.KEY_EMAIL);
        }

        List<String> tags = tagRepository.getAllTagNames(email,rootFolder);

        Log.d("updateTagSpinner","email:"+email);
        Log.d("updateTagSpinner","rootFolder:"+rootFolder);
        Log.d("updateTagSpinner","tags:"+tags);

        Collections.sort(tags);

        String[] tagArr = new String[tags.size()+1];
        tagArr[0] = getResources().getString(R.string.no_selection);
        for(int i=0;i<tags.size();i++){
            tagArr[i+1] = tags.get(i);
        }

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, R.layout.widget_config_spinner_item, tagArr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tagSpinner.setAdapter(adapter);
        tagSpinner.setSelection(0);
        tagSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTag = parent.getSelectedItem().toString();

                if(getResources().getString(R.string.no_selection).equals(selectedTag)){
                    selectedTag = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //nothing
            }
        });
    }
}



