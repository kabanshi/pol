package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ig.R;

import java.util.List;

import models.Task;

public class TaskAdapter extends ArrayAdapter<Task> {
    private Context context;
    private List<Task> tasks;

    public TaskAdapter(Context context, List<Task> tasks) {
        super(context, R.layout.task_list_item, tasks);
        this.context = context;
        this.tasks = tasks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.task_list_item, parent, false);
        }

        Task task = tasks.get(position);

        TextView titleTextView = convertView.findViewById(R.id.taskTitleTextView);
        TextView dateTextView = convertView.findViewById(R.id.taskDateTextView);
        TextView priorityTextView = convertView.findViewById(R.id.taskPriorityTextView);

        titleTextView.setText(task.getTitle());
        dateTextView.setText(task.getFormattedDate());
        priorityTextView.setText(task.getPriorityText());

        // Установка цвета приоритета
        int priorityColor;
        switch (task.getPriority()) {
            case 1:
                priorityColor = context.getResources().getColor(R.color.high_priority);
                break;
            case 2:
                priorityColor = context.getResources().getColor(R.color.medium_priority);
                break;
            case 3:
                priorityColor = context.getResources().getColor(R.color.low_priority);
                break;
            default:
                priorityColor = context.getResources().getColor(R.color.default_priority);
        }
        priorityTextView.setTextColor(priorityColor);

        return convertView;
    }
}