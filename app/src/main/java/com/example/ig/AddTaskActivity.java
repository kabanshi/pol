package com.example.ig;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Date;

import controllers.AddTaskController;

public class AddTaskActivity extends AppCompatActivity {
    private AddTaskController controller;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private DatePicker datePicker;
    private Spinner prioritySpinner;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        controller = new AddTaskController(this);
        calendar = Calendar.getInstance();

        // Инициализация элементов интерфейса
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        datePicker = findViewById(R.id.datePicker);
        prioritySpinner = findViewById(R.id.prioritySpinner);
        Button saveButton = findViewById(R.id.saveButton);
        Button homeButton = findViewById(R.id.homeButton);

        // Настройка спиннера приоритетов
        ArrayAdapter<CharSequence> priorityAdapter = ArrayAdapter.createFromResource(
                this, R.array.priority_levels, android.R.layout.simple_spinner_item);
        priorityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prioritySpinner.setAdapter(priorityAdapter);

        // Установка текущей даты по умолчанию
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), null);

        // Обработчики кнопок
        saveButton.setOnClickListener(v -> saveTask());
        homeButton.setOnClickListener(v -> finish());
    }

    private void saveTask() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Введите название задачи", Toast.LENGTH_SHORT).show();
            return;
        }

        // Получение даты из DatePicker
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        Date dueDate = calendar.getTime();

        // Получение приоритета (1-3)
        int priority = prioritySpinner.getSelectedItemPosition() + 1;

        // Сохранение задачи
        long result = controller.addTask(title, description, dueDate, priority);

        if (result != -1) {
            Toast.makeText(this, "Задача успешно сохранена", Toast.LENGTH_SHORT).show();
            titleEditText.setText("");
            descriptionEditText.setText("");
        } else {
            Toast.makeText(this, "Ошибка при сохранении задачи", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        controller.close();
        super.onDestroy();
    }
}