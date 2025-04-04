package models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Task {
    private int id;
    private String title;
    private String description;
    private Date dueDate;
    private int priority;

    public Task() {}

    public Task(String title, String description, Date dueDate, int priority) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getDueDate() { return dueDate; }
    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    // Форматированная дата для отображения
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return sdf.format(dueDate);
    }

    // Приоритет в текстовом виде
    public String getPriorityText() {
        switch (priority) {
            case 1: return "Высокий";
            case 2: return "Средний";
            case 3: return "Низкий";
            default: return "Не указан";
        }
    }
}