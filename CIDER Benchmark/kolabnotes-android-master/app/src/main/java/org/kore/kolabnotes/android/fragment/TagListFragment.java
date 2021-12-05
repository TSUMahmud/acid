package org.kore.kolabnotes.android.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.kore.kolab.notes.Colors;
import org.kore.kolab.notes.Tag;
import org.kore.kolabnotes.android.R;
import org.kore.kolabnotes.android.SimpleDividerItemDecoration;
import org.kore.kolabnotes.android.adapter.TagAdapter;
import org.kore.kolabnotes.android.content.ActiveAccount;
import org.kore.kolabnotes.android.content.ActiveAccountRepository;
import org.kore.kolabnotes.android.content.TagRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yuku.ambilwarna.AmbilWarnaDialog;

/**
 * Created by yaroslav on 09.01.16.
 */

/**
 * Fragment which displays, edits and deletes tags
 */

public class TagListFragment extends Fragment implements TagAdapter.ViewHolder.ClickListener{

    private static final String TAG_ACTION_MODE = "ActionMode";
    private static final String TAG_SELECTED_TAGS = "SelectedTags";
    private static final String TAG_SELECTABLE_ADAPTER = "SelectableAdapter";

    private AppCompatActivity activity;
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private FloatingActionButton mAddTagButton;
    private Snackbar mSnackbarDelete;

    private TagRepository tagRepository;
    private ActiveAccountRepository activeAccountRepository;
    private TagAdapter mAdapter;

    private ActionMode mActionMode;
    private ActionModeCallback mActionModeCallback = new ActionModeCallback();
    private boolean isInActionMode = false;
    private HashMap<Integer, String> mSelectedTags = new HashMap<Integer, String>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tag_list, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            this.activity = (AppCompatActivity)context;
        }
        this.tagRepository = new TagRepository(context);
        this.activeAccountRepository = new ActiveAccountRepository(context);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_tag_list);

        if(activity == null){
            activity = (AppCompatActivity)getActivity();
        }

        if(tagRepository == null){
            this.tagRepository = new TagRepository(activity);
        }
        if(activeAccountRepository == null){
            this.activeAccountRepository = new ActiveAccountRepository(activity);
        }

        activity.setSupportActionBar(toolbar);
        if(activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        setHasOptionsMenu(true);

        mAddTagButton = (FloatingActionButton) getActivity().findViewById(R.id.addTagButton);
        mAddTagButton.setOnClickListener(new CreateButtonListener());

        mRecyclerView = (RecyclerView) activity.findViewById(R.id.listTag);
        mEmptyView = (TextView) activity.findViewById(R.id.empty_view_tag_list);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(activity));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        ActiveAccount activeAccount = activeAccountRepository.getActiveAccount();
        List<Tag> tags = tagRepository.getAll(activeAccount.getAccount(), activeAccount.getRootFolder());
        mAdapter = new TagAdapter(tags, activity, R.layout.list_item_tag, this);
        setListState();

        mRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            if (savedInstanceState != null && savedInstanceState.getBoolean(TAG_ACTION_MODE, false)){
                mSelectedTags = (HashMap<Integer, String>)savedInstanceState.getSerializable(TAG_SELECTED_TAGS);
                mActionMode = activity.startActionMode(mActionModeCallback);
                mAdapter.setSelectedItems(savedInstanceState.getIntegerArrayList(TAG_SELECTABLE_ADAPTER));
                mActionMode.setTitle(String.valueOf(mAdapter.getSelectedItemCount()));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mSnackbarDelete != null) {
            mSnackbarDelete.dismiss();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putIntegerArrayList(TAG_SELECTABLE_ADAPTER, mAdapter.getSelectedItems());
        outState.putSerializable(TAG_SELECTED_TAGS, mSelectedTags);
        outState.putBoolean(TAG_ACTION_MODE, isInActionMode);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemClicked(int position, Tag tag) {
        if (mActionMode == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_text_input, null);
            AlertDialog newTagDialog = updateTagDialog(view, new UpdateTagButtonListener(
                    (EditText) view.findViewById(R.id.dialog_text_input_field), tag.getIdentification().getUid()));
            newTagDialog.show();
        } else {
            toggleSelection(position);
            mSelectedTags.put(position, tag.getIdentification().getUid());
        }
    }

    @Override
    public boolean onItemLongClicked(int position, Tag tag) {
        if (mActionMode == null) {
            mActionMode = activity.startActionMode(mActionModeCallback);
        }
        toggleSelection(position);
        mSelectedTags.put(position, tag.getIdentification().getUid());

        return true;
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            mActionMode.finish();
        } else {
            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();
        }
    }

    @Override
    public void onColorPickerClicked(int position, Tag tag) {
        mSelectedTags.put(position, tag.getIdentification().getUid());
        List<Integer> item = new ArrayList<Integer>(0);
        item.add(position);
        chooseColor(item);
    }

    public Context getContext(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            return super.getContext();
        }
        return activity;
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.list_item_tag_context, menu);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.theme_actionmode_dark));
                activity.getWindow().setNavigationBarColor(ContextCompat.getColor(getContext(), R.color.theme_actionmode));
            }
            isInActionMode = true;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            List<Integer> items = mAdapter.getSelectedItems();
            switch (item.getItemId()) {
                case R.id.delete_menu_context:
                    deleteTags(items);
                    mode.finish();
                    break;
                case R.id.choose_tag_color_menu_context:
                    chooseColor(items);
                    mode.finish();
            }

            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelection();
            mActionMode = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.theme_default_primary_dark));
                activity.getWindow().setNavigationBarColor(ContextCompat.getColor(getContext(), R.color.md_black_1000));
            }
            isInActionMode = false;
        }
    }

    void chooseColor(final List<Integer> items) {
        if (items != null) {
            final int initialColor = Color.WHITE;

            AmbilWarnaDialog dialog = new AmbilWarnaDialog(activity, initialColor, true, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    setColor(items, Colors.getColor(String.format("#%06X", (0xFFFFFF & color))));
                }

                @Override
                public void onRemove(AmbilWarnaDialog dialog) {
                    setColor(items, null);
                }

                @Override
                public void onCancel(AmbilWarnaDialog dialog) {
                    // do nothing
                }
            });
            dialog.show();
        }
    }

    void setColor(final List<Integer> items, org.kore.kolab.notes.Color newColor){
        final String account = activeAccountRepository.getActiveAccount().getAccount();
        final String rootFolder = activeAccountRepository.getActiveAccount().getRootFolder();

        for (int position : items) {
            final String uid = mSelectedTags.get(position);
            final Tag tag = tagRepository.getTagWithUID(account, rootFolder, uid);
            if (tag != null) {
                tag.setColor(newColor);
                tag.getAuditInformation().setLastModificationDate(System.currentTimeMillis());
                tagRepository.update(account, rootFolder, tag);
            }
        }
        mSelectedTags.clear();
        reloadData();
    }

    void deleteTags(final List<Integer> items) {
        if (items != null) {
            final String account = activeAccountRepository.getActiveAccount().getAccount();
            final String rootFolder = activeAccountRepository.getActiveAccount().getRootFolder();

            final ArrayList<Tag> tags = new ArrayList<Tag>();
            for (int position : items) {
                final String uid = mSelectedTags.get(position);
                final Tag tag = tagRepository.getTagWithUID(account, rootFolder, uid);
                tags.add(tag);
            }
            mAdapter.deleteTags(tags);
            setListState();
            mSelectedTags.clear();

            if (mSnackbarDelete != null) {
                mSnackbarDelete.dismiss();
            }

            mSnackbarDelete = Snackbar.make(activity.findViewById(R.id.tag_list_fragment), R.string.snackbar_delete_message, Snackbar.LENGTH_LONG)
                    .setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    switch(event) {
                        /* If undo button pressed */
                        case Snackbar.Callback.DISMISS_EVENT_ACTION:
                            mAdapter.clearTags();
                            mAdapter.addTags(tagRepository.getAll(account, rootFolder));
                            setListState();
                            break;
                        default:
                            for (Tag tag : tags) {
                                if (tag != null) {
                                    tagRepository.delete(account, rootFolder, tag);
                                }
                            }
                            reloadData();
                            break;
                    }
                }
                    }).setAction(R.string.snackbar_undo_delete, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /* Nothing */
                }
            });

            mSnackbarDelete.show();
        }
    }

    class CreateButtonListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            LayoutInflater inflater = activity.getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_text_input, null);
            AlertDialog newTagDialog = createTagDialog(view, new CreateTagButtonListener((EditText)view.findViewById(R.id.dialog_text_input_field)));
            newTagDialog.show();
        }
    }

    private AlertDialog createTagDialog(View view, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(R.string.dialog_input_text_tag);

        builder.setView(view);

        builder.setPositiveButton(R.string.ok, listener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /* Nothing */
            }
        });
        return builder.create();
    }

    private AlertDialog updateTagDialog(View view, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(R.string.dialog_input_update_tag_text);
        builder.setMessage(R.string.dialog_change_tag_warning);

        builder.setView(view);

        builder.setPositiveButton(R.string.ok, listener);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /* Nothing */
            }
        });
        return builder.create();
    }

    public class CreateTagButtonListener implements DialogInterface.OnClickListener{
        private final EditText textField;

        public CreateTagButtonListener(EditText textField) {
            this.textField = textField;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(textField == null || textField.getText() == null || textField.getText().toString().trim().length() == 0){
                return;
            }

            String value = textField.getText().toString();

            final String account = activeAccountRepository.getActiveAccount().getAccount();
            final String rootFolder = activeAccountRepository.getActiveAccount().getRootFolder();

            if(tagRepository.insert(account, rootFolder, Tag.createNewTag(value))) {
                reloadData();
            }
        }
    }

    public class UpdateTagButtonListener implements DialogInterface.OnClickListener{
        private final EditText textField;
        private final Tag tag;
        private final String account;
        private final String rootFolder;


        public UpdateTagButtonListener(EditText textField, String uid) {
            this.account = activeAccountRepository.getActiveAccount().getAccount();
            this.rootFolder = activeAccountRepository.getActiveAccount().getRootFolder();
            this.textField = textField;
            this.tag = tagRepository.getTagWithUID(account, rootFolder, uid);
            if(textField != null && tag != null){
                textField.setText(tag.getName());
            }
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            if(textField == null || textField.getText() == null || textField.getText().toString().trim().length() == 0){
                return;
            }

            String value = textField.getText().toString();
            tag.setName(value);
            tag.getAuditInformation().setLastModificationDate(System.currentTimeMillis());

            tagRepository.update(account, rootFolder, tag);
            reloadData();
        }
    }

    final synchronized void reloadData() {
        ActiveAccount activeAccount = activeAccountRepository.getActiveAccount();
        List<Tag> tags = tagRepository.getAll(activeAccount.getAccount(), activeAccount.getRootFolder());

        mAdapter.clearTags();
        if(tags.size() == 0) {
            mAdapter.notifyDataSetChanged();
        } else {
            mAdapter.addTags(tags);
        }
        setListState();
    }

    private void setListState() {
        if (mAdapter != null) {
            if (mAdapter.isEmpty()) {
                mRecyclerView.setVisibility(View.GONE);
                mEmptyView.setVisibility(View.VISIBLE);
            } else {
                mRecyclerView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onBackPressed() {
        ((OnFragmentCallback)activity).fragmentFinished(new Intent(), OnFragmentCallback.ResultCode.BACK);
    }
}
