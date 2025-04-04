package com.example.ig;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import controllers.SettingsController;

public class SettingsActivity extends AppCompatActivity {
    private SettingsController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        controller = new SettingsController(this);

        // Инициализация элементов интерфейса
        Spinner languageSpinner = findViewById(R.id.languageSpinner);
        Button resetDatabaseButton = findViewById(R.id.resetDatabaseButton);
        Button homeButton = findViewById(R.id.homeButton);

        // Настройка спиннера языка
        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(
                this, R.array.languages, android.R.layout.simple_spinner_item);
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);

        // Установка текущего языка
        String currentLanguage = controller.getLanguage();
        if (currentLanguage != null) {
            int position = currentLanguage.equals("ru") ? 0 : 1;
            languageSpinner.setSelection(position);
        }

        // Обработчик выбора языка
        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String language = position == 0 ? "ru" : "en";
                controller.setLanguage(language);
                setLocale(language);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Обработчики кнопок
        resetDatabaseButton.setOnClickListener(v -> {
            controller.resetDatabase();
            Toast.makeText(SettingsActivity.this, "База данных очищена", Toast.LENGTH_SHORT).show();
        });

        homeButton.setOnClickListener(v -> finish());
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        // Перезапуск активности для применения изменений
        Intent refresh = new Intent(this, SettingsActivity.class);
        startActivity(refresh);
        finish();
    }

    @Override
    protected void onDestroy() {
        controller.close();
        super.onDestroy();
    }
}
