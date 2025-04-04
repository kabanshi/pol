package controllers;

import android.content.Context;

import java.util.Date;

import models.DatabaseHelper;
import models.Task;

public class AddTaskController {
    private DatabaseHelper dbHelper;

    public AddTaskController(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long addTask(String title, String description, Date dueDate, int priority) {
        Task task = new Task(title, description, dueDate, priority);
        return dbHelper.addTask(task);
    }

    public void close() {
        dbHelper.close();
    }
}