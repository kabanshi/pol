package controllers;

import android.content.Context;

import models.DatabaseHelper;


public class SettingsController {
    private DatabaseHelper dbHelper;

    public SettingsController(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public String getLanguage() {
        return dbHelper.getSetting("language");
    }

    public void setLanguage(String language) {
        dbHelper.updateSetting("language", language);
    }

    public void resetDatabase() {
        dbHelper.deleteAllTasks();
    }

    public void close() {
        dbHelper.close();
    }
}