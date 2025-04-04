package controllers;

import android.content.Context;

import java.util.List;

import models.DatabaseHelper;
import models.Task;

public class MainController {
    private DatabaseHelper dbHelper;

    public MainController(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public List<Task> getAllTasks() {
        return dbHelper.getAllTasks();
    }

    public boolean deleteTask(int taskId) {
        return dbHelper.deleteTask(taskId) > 0;
    }

    public void close() {
        dbHelper.close();
    }
}