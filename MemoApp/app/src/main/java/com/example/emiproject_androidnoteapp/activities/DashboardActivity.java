package com.example.emiproject_androidnoteapp.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.emiproject_androidnoteapp.R;
import com.example.emiproject_androidnoteapp.adapters.NotesAdapter;
import com.example.emiproject_androidnoteapp.models.Note;
import com.example.emiproject_androidnoteapp.utils.Constants;
import com.example.emiproject_androidnoteapp.utils.RealmFacade;
import com.example.emiproject_androidnoteapp.utils.RecyclerViewItemListener;
import com.example.emiproject_androidnoteapp.utils.SpaceItemDecoration;
import com.example.emiproject_androidnoteapp.utils.Utils;

import io.realm.RealmResults;

/**
 * @author Raee, Mulham (mulham.raee@gmail.com)
 */
public class DashboardActivity extends CameraActivity implements RecyclerViewItemListener{

    private RecyclerView notesRecyclerView;
    private NotesAdapter adapter;
    private RealmFacade realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        realm = new RealmFacade();
        initRecyclerView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardActivity.this, NoteActivity.class);
                intent.setAction(NoteActivity.ACTION_NEW);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void initRecyclerView() {
        notesRecyclerView = (RecyclerView) findViewById(R.id.notesRecyclerView);
        int spanCount = 3;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            spanCount = 2;
        }
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL));
        //notesRecyclerView.addItemDecoration(new SpaceItemDecoration((int)Utils.dpToPx(8, this), SpaceItemDecoration.HORIZONTAL_LIST, false));
        //notesRecyclerView.setHasFixedSize(true);

        RealmResults<Note> notes = realm.allObjects(Note.class);
        adapter = new NotesAdapter(this, notes, this);
        adapter.setHasStableIds(true);
        notesRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK){
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClicked(View callerView, int position) {
        Intent intent = new Intent(this, NoteActivity.class);
        intent.setAction(NoteActivity.ACTION_EDIT);
        intent.putExtra(Constants.EXTRA_NOTE_ID, adapter.getItemId(position));
        startActivityForResult(intent, 1);
    }

    @Override
    public boolean onItemLongClick(View callerView, int position) {
        return false;
    }
}
