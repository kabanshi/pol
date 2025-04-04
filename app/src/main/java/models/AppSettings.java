package models;

public class AppSettings {
    private String language;

    public AppSettings() {
        this.language = "ru"; // По умолчанию русский
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
