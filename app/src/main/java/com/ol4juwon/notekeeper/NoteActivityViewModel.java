package com.ol4juwon.notekeeper;

import android.os.Bundle;

import androidx.lifecycle.ViewModel;

public class NoteActivityViewModel extends ViewModel {

    public static final String ORIGINAL_NOTE_COURSE_ID = "com.ol4juwon.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.ol4juwon.notekeeper.ORIGINAL_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.ol4juwon.notekeeper.ORIGINAL_TEXT";

    public String mOriginalCourseId;
    public String mOriginalNoteTitle;
    public String mOriginalNoteText;
    public Boolean mIsNewlyCreated = true;

    public void saveState(Bundle outState) {
        outState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginalCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, mOriginalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, mOriginalNoteText);

    }

    public void restoreState(Bundle instate){
        mOriginalCourseId = instate.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginalNoteTitle = instate.getString(ORIGINAL_NOTE_TITLE);
        mOriginalNoteText = instate.getString(ORIGINAL_NOTE_TEXT);
    }
}
