package com.ol4juwon.notekeeper;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.ol4juwon.notekeeper.NoteKeeperDatabaseContract.NoteInfoEntry;

import java.util.List;


public class NoteActivity extends AppCompatActivity {
    public static final String NOTE_ID = "com.ol4juwon.notekeeper.NOTE_POSITION";
    public static final int ID_NOT_SET = -1;
    private static final String TAG = "";
    private NoteInfo mNote;
    private boolean mIsNewNote;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private int mNoteId;
    private boolean mIsCancelling;
    private NoteActivityViewModel mViewlModel;
    private NoteKeeperOpenHelper mDbOpenhelper;
    private Cursor mNoteCursor;
    private int mCourseIdPos;
    private int mNoteTitlePos;
    private int mNoteTextPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDbOpenhelper = new NoteKeeperOpenHelper(this);

        ViewModelProvider viewModelProvider = new ViewModelProvider(getViewModelStore(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()));
        mViewlModel = viewModelProvider.get(NoteActivityViewModel.class);

        if(mViewlModel.mIsNewlyCreated && savedInstanceState !=null) {
            mViewlModel.restoreState(savedInstanceState);
        }
        mViewlModel.mIsNewlyCreated = false;


        mSpinnerCourses = findViewById(R.id.spinner_courses);
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourse =
                new ArrayAdapter<>( this,android.R.layout.simple_spinner_item,courses);
        adapterCourse.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCourses.setAdapter(adapterCourse);

        readDisplayStateValues();
        if(savedInstanceState == null) {
            saveOriginalNoteValues();
        }else {
            restoreOriginalNoteValues( );
        }

        mTextNoteTitle = findViewById(R.id.text_note_title);
        mTextNoteText = findViewById(R.id.text_note_text);

        if(!mIsNewNote)
            loadNoteData();
        Log.d(TAG,"oncreate");

    }

    private void loadNoteData() {
        SQLiteDatabase db = mDbOpenhelper.getReadableDatabase();

        String courseID = "android_intents";
        String titleStart = "Dynamic";


        String selection = NoteInfoEntry._ID+ " = ?";
        String[] selectionArgs = {Integer.toString(mNoteId)};

        String[] noteColumns = {
                NoteInfoEntry.COLUMN_COURSE_ID,
                NoteInfoEntry.COLUMN_NOTE_TITLE,
                NoteInfoEntry.COLUMN_NOTE_TEXT
        };
        mNoteCursor = db.query(NoteInfoEntry.TABLE_NAME,noteColumns,selection,selectionArgs,null,null,null);

        mCourseIdPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_COURSE_ID);
        mNoteTitlePos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TITLE);
        mNoteTextPos = mNoteCursor.getColumnIndex(NoteInfoEntry.COLUMN_NOTE_TEXT);

        mNoteCursor.moveToNext();
        displayNote();


    }

    @Override
    protected void onDestroy() {
        mDbOpenhelper.close();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState != null)
            mViewlModel.saveState(outState);
    }

    private void saveOriginalNoteValues() {
        if(mIsNewNote) {
            return;
        }
        mViewlModel.mOriginalCourseId = mNote.getCourse().getCourseId();
        mViewlModel.mOriginalNoteTitle = mNote.getTitle();
        mViewlModel.mOriginalNoteText = mNote.getText();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIsCancelling){
            if(mIsNewNote) {
                DataManager.getInstance().removeNote(mNoteId);
            }else{
                restoreOriginalNoteValues();
            }
        }else {
            saveNote();
        }
    }

    private void restoreOriginalNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mViewlModel.mOriginalCourseId);
        mNote.setCourse(course);
        mNote.setTitle(mViewlModel.mOriginalNoteTitle);
        mNote.setText(mViewlModel.mOriginalNoteText);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem nextItem  = menu.findItem(R.id.action_next);
        MenuItem prevItem = menu.findItem(R.id.action_previous);
        int lastIndexOf = DataManager.getInstance().getNotes().size() - 1 ;
        prevItem.setEnabled(mNoteId > 0);
        nextItem.setEnabled(mNoteId < lastIndexOf);

        return super.onPrepareOptionsMenu(menu);
    }

    private void saveNote() {
        mNote.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
        mNote.setTitle(mTextNoteTitle.getText().toString());
        mNote.setText(mTextNoteText.getText().toString());

    }

    private void displayNote() {
        String courseId = mNoteCursor.getString(mCourseIdPos);
        String noteTitle = mNoteCursor.getString(mNoteTitlePos);
        String noteText = mNoteCursor.getString(mNoteTextPos);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        CourseInfo course = DataManager.getInstance().getCourse(courseId);
        int courseIndex = courses.indexOf(course);
        mSpinnerCourses.setSelection(courseIndex);
        mTextNoteTitle.setText(noteTitle);
        mTextNoteText.setText(noteText);
        mSpinnerCourses.getSelectedItem();

    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
         mNoteId = intent.getIntExtra(NOTE_ID, ID_NOT_SET);
        mIsNewNote = mNoteId == ID_NOT_SET;
        if(mIsNewNote){
         createNewNote();
        }
//        else {
//            mNote = DataManager.getInstance().getNotes().get(mNoteId);
//        }

mNote = DataManager.getInstance().getNotes().get(mNoteId);
    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNoteId = dm.createNewNote();
//        mNote = dm.getNotes().get(mNoteId);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();

            return true;
        }else if(id == R.id.action_cancel){
            cancelNote();
        }else if(id == R.id.action_next){
            moveNext();
        }else if (id == R.id.action_previous){
            movePrevious();
        }

        return super.onOptionsItemSelected(item);
    }

    private void movePrevious() {
        saveNote();
        --mNoteId;
        mNote = DataManager.getInstance().getNotes().get(mNoteId);

        saveOriginalNoteValues();
        displayNote();
        invalidateOptionsMenu();
    }

    private void moveNext() {
        saveNote();
        ++mNoteId;
        mNote = DataManager.getInstance().getNotes().get(mNoteId);

        saveOriginalNoteValues();
        displayNote();
        invalidateOptionsMenu();

    }

    private void cancelNote() {
        mIsCancelling = true;
        finish();
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) mSpinnerCourses.getSelectedItem();
        String subject = mTextNoteTitle.getText().toString();
        String text = "Check out what i learnt \""+course.getTitle()+"\" \n"+mTextNoteText.getText();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT,subject);
        intent.putExtra(Intent.EXTRA_TEXT,text);
        startActivity(intent);



    }
}