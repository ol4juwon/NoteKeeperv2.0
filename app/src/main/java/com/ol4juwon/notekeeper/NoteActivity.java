package com.ol4juwon.notekeeper;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;


public class NoteActivity extends AppCompatActivity {
    public static final String NOTE_POSITION = "com.ol4juwon.notekeeper.NOTE_POSITION";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo mNote;
    private boolean mIsNewNote;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private int mNotePosition;
    private boolean mIsCancelling;
    private NoteActivityViewModel mViewlModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        saveOriginalNoteValues();

        mTextNoteTitle = findViewById(R.id.text_note_title);
        mTextNoteText = findViewById(R.id.text_note_text);

        if(!mIsNewNote)
            displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);


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
                DataManager.getInstance().removeNote(mNotePosition);
            }else{
                storeOriginalNoteValues();
            }
        }else {
            saveNote();
        }
    }

    private void storeOriginalNoteValues() {
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
        prevItem.setEnabled(mNotePosition > 0);
        nextItem.setEnabled(mNotePosition < lastIndexOf);

        return super.onPrepareOptionsMenu(menu);
    }

    private void saveNote() {
        mNote.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
        mNote.setTitle(mTextNoteTitle.getText().toString());
        mNote.setText(mTextNoteText.getText().toString());

    }

    private void displayNote(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(mNote.getCourse());
        spinnerCourses.setSelection(courseIndex);
        textNoteTitle.setText(mNote.getTitle());
        textNoteText.setText(mNote.getText());
        spinnerCourses.getSelectedItem();

    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        int position  = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
        mIsNewNote = position == POSITION_NOT_SET;
        if(mIsNewNote){
         createNewNote();
        }else {
            mNote = DataManager.getInstance().getNotes().get(position);
        }


    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNotePosition = dm.createNewNote();
        mNote = dm.getNotes().get(mNotePosition);

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
        --mNotePosition;
        mNote = DataManager.getInstance().getNotes().get(mNotePosition);

        saveOriginalNoteValues();
        displayNote(mSpinnerCourses,mTextNoteTitle,mTextNoteText);
        invalidateOptionsMenu();
    }

    private void moveNext() {
        saveNote();
        ++mNotePosition;
        mNote = DataManager.getInstance().getNotes().get(mNotePosition);

        saveOriginalNoteValues();
        displayNote(mSpinnerCourses,mTextNoteTitle,mTextNoteText);
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