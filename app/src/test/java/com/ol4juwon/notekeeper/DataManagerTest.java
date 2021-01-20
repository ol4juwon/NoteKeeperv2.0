package com.ol4juwon.notekeeper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataManagerTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getInstance() {
    }

    @Test
    public void getCurrentUserName() {
    }

    @Test
    public void getCurrentUserEmail() {
    }

    @Test
    public void getNotes() {
    }

    @Test
    public void createNewNote() {
        DataManager dm = DataManager.getInstance();
        final CourseInfo courseInfo = dm.getCourse("android_async");
        final String notetitle = "Test note title" ;
        final String noteText = "Test note Text";

        int indexnote = dm.createNewNote();
        NoteInfo newNote = dm.getNotes().get(indexnote-1);
        newNote.setCourse(courseInfo);
        newNote.setTitle(notetitle);
        newNote.setText(noteText);

        NoteInfo compareNote = dm.getNotes().get(indexnote);
        assertEquals(courseInfo,compareNote.getCourse());
        assertSame(notetitle,compareNote.getTitle());
        assertSame(noteText,compareNote.getText());
        //assertEquals(newNote,compareNote);



    }

    @Test
    public void findNote() {
    }

    @Test
    public void removeNote() {
    }

    @Test
    public void getCourses() {
    }
}