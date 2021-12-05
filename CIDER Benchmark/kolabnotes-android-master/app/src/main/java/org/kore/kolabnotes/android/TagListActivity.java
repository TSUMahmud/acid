package org.kore.kolabnotes.android;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.kore.kolabnotes.android.fragment.OnFragmentCallback;
import org.kore.kolabnotes.android.fragment.TagListFragment;

/**
 * Created by yaroslav on 09.01.16.
 */
public class TagListActivity extends AppCompatActivity implements OnFragmentCallback {
    private TagListFragment tagListFragment;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_list);

        toolbar = (Toolbar) findViewById(R.id.toolbar_tag_list);
        if (toolbar != null){
            toolbar.setTitle(R.string.title_activity_tag_list);
        }

        tagListFragment = (TagListFragment)getFragmentManager().findFragmentById(R.id.tag_list_fragment);
    }

    @Override
    public void fragementAttached(Fragment fragment) {
        //nothing at the moment
    }

    @Override
    public void fragmentFinished(Intent resultIntent, ResultCode code) {
        if(ResultCode.OK == code || ResultCode.SAVED == code || ResultCode.DELETED == code) {
            Utils.setReloadDataAfterDetail(this, true);
            setResult(RESULT_OK, resultIntent);
        }else{
            setResult(RESULT_CANCELED, resultIntent);
        }
        finish();
    }

    @Override
    public void fileSelected() {
        /* Nothing */
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        tagListFragment.onBackPressed();
    }
}
