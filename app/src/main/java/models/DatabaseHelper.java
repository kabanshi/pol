package models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "TaskManager.db";
    private static final int DATABASE_VERSION = 3; // Увеличена версия для миграции

    // Таблица задач
    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_TASK_ID = "id";
    private static final String COLUMN_TASK_TITLE = "title";
    private static final String COLUMN_TASK_DESCRIPTION = "description";
    private static final String COLUMN_TASK_DUE_DATE = "due_date";
    private static final String COLUMN_TASK_PRIORITY = "priority";

    // Таблица настроек
    private static final String TABLE_SETTINGS = "settings";
    private static final String COLUMN_SETTING_KEY = "setting_key";
    private static final String COLUMN_SETTING_VALUE = "setting_value";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTasksTable(db);
        createSettingsTable(db);
        initializeDefaultSettings(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {  // Убедитесь, что версия увеличена
            createSettingsTable(db);  // Это пересоздаст таблицу с новыми именами
            initializeDefaultSettings(db);
        }
    }

    private void createTasksTable(SQLiteDatabase db) {
        String CREATE_TASKS_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
                + COLUMN_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TASK_TITLE + " TEXT NOT NULL,"
                + COLUMN_TASK_DESCRIPTION + " TEXT,"
                + COLUMN_TASK_DUE_DATE + " INTEGER NOT NULL,"
                + COLUMN_TASK_PRIORITY + " INTEGER NOT NULL DEFAULT 3"
                + ")";
        db.execSQL(CREATE_TASKS_TABLE);
        Log.d(TAG, "Таблица задач создана");
    }

    private void createSettingsTable(SQLiteDatabase db) {
        // Удаляем старую таблицу, если она существует
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SETTINGS);

        // Создаем новую таблицу с правильными именами столбцов
        String CREATE_SETTINGS_TABLE = "CREATE TABLE " + TABLE_SETTINGS + "("
                + COLUMN_SETTING_KEY + " TEXT PRIMARY KEY,"
                + COLUMN_SETTING_VALUE + " TEXT"
                + ")";
        db.execSQL(CREATE_SETTINGS_TABLE);
        Log.d(TAG, "Таблица настроек создана с новыми именами столбцов");
    }

    private void initializeDefaultSettings(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SETTING_KEY, "language");
        values.put(COLUMN_SETTING_VALUE, "ru");

        try {
            db.insertWithOnConflict(TABLE_SETTINGS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            Log.d(TAG, "Настройки по умолчанию инициализированы");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка инициализации настроек", e);
        }
    }

    // Методы для работы с задачами
    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_TITLE, task.getTitle());
        values.put(COLUMN_TASK_DESCRIPTION, task.getDescription());
        values.put(COLUMN_TASK_DUE_DATE, task.getDueDate().getTime());
        values.put(COLUMN_TASK_PRIORITY, task.getPriority());

        try {
            return db.insert(TABLE_TASKS, null, values);
        } finally {
            db.close();
        }
    }

    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_TASKS,
                    new String[]{COLUMN_TASK_ID, COLUMN_TASK_TITLE, COLUMN_TASK_DESCRIPTION,
                            COLUMN_TASK_DUE_DATE, COLUMN_TASK_PRIORITY},
                    null, null, null, null,
                    COLUMN_TASK_PRIORITY + " ASC, " + COLUMN_TASK_DUE_DATE + " ASC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Task task = new Task();
                    task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_ID)));
                    task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_TITLE)));
                    task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TASK_DESCRIPTION)));
                    task.setDueDate(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TASK_DUE_DATE))));
                    task.setPriority(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_PRIORITY)));

                    taskList.add(task);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при получении задач", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return taskList;
    }

    // Методы для работы с настройками
    public String getSetting(String key) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Проверяем существование таблицы
            if (!tableExists(db, TABLE_SETTINGS)) {
                createSettingsTable(db);
                initializeDefaultSettings(db);
            }

            cursor = db.query(TABLE_SETTINGS,
                    new String[]{COLUMN_SETTING_VALUE},
                    COLUMN_SETTING_KEY + " = ?",
                    new String[]{key},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                return cursor.getString(0);
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при получении настройки: " + key, e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }

    public boolean updateSetting(String key, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SETTING_VALUE, value);

        try {
            int rowsAffected = db.update(TABLE_SETTINGS, values,
                    COLUMN_SETTING_KEY + " = ?", new String[]{key});

            if (rowsAffected == 0) {
                // Если настройка не существует, вставляем новую
                values.put(COLUMN_SETTING_KEY, key);
                return db.insert(TABLE_SETTINGS, null, values) != -1;
            }
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при обновлении настройки", e);
            return false;
        } finally {
            db.close();
        }
    }

    private boolean tableExists(SQLiteDatabase db, String tableName) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(
                    "SELECT name FROM sqlite_master WHERE type='table' AND name=?",
                    new String[]{tableName});
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Другие методы работы с базой данных...
    public int deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            return db.delete(TABLE_TASKS, COLUMN_TASK_ID + " = ?",
                    new String[]{String.valueOf(taskId)});
        } finally {
            db.close();
        }
    }

    public void deleteAllTasks() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_TASKS, null, null);
        } finally {
            db.close();
        }
    }
}