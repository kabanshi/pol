package com.example.ig;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

import adapters.TaskAdapter;
import controllers.MainController;
import models.Task;

public class MainActivity extends AppCompatActivity {
    private MainController controller;
    private ListView taskListView;
    private TextView selectedTaskTextView;
    private TaskAdapter adapter;
    private List<Task> taskList;
    private int selectedTaskId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        controller = new MainController(this);

        // Инициализация элементов интерфейса
        taskListView = findViewById(R.id.taskListView);
        selectedTaskTextView = findViewById(R.id.selectedTaskTextView);
        Button addTaskButton = findViewById(R.id.addTaskButton);
        Button deleteTaskButton = findViewById(R.id.deleteTaskButton);
        Button settingsButton = findViewById(R.id.settingsButton);
        Button exitButton = findViewById(R.id.exitButton);

        // Загрузка списка задач
        loadTasks();

        // Обработчик выбора задачи в списке
        taskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task selectedTask = taskList.get(position);
                selectedTaskId = selectedTask.getId();
                String taskInfo = "Название: " + selectedTask.getTitle() + "\n" +
                        "Описание: " + selectedTask.getDescription() + "\n" +
                        "Дата: " + selectedTask.getFormattedDate() + "\n" +
                        "Приоритет: " + selectedTask.getPriorityText();
                selectedTaskTextView.setText(taskInfo);
            }
        });

        // Обработчики кнопок
        addTaskButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });

        deleteTaskButton.setOnClickListener(v -> {
            if (selectedTaskId != -1) {
                boolean deleted = controller.deleteTask(selectedTaskId);
                if (deleted) {
                    Toast.makeText(MainActivity.this, "Задача удалена", Toast.LENGTH_SHORT).show();
                    loadTasks();
                    selectedTaskTextView.setText("");
                    selectedTaskId = -1;
                } else {
                    Toast.makeText(MainActivity.this, "Ошибка при удалении задачи", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Выберите задачу для удаления", Toast.LENGTH_SHORT).show();
            }
        });

        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        exitButton.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    @Override
    protected void onDestroy() {
        controller.close();
        super.onDestroy();
    }

    private void loadTasks() {
        taskList = controller.getAllTasks();
        adapter = new TaskAdapter(this, taskList);
        taskListView.setAdapter(adapter);
    }
}