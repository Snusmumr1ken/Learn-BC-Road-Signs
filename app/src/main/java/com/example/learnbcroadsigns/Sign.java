package com.example.learnbcroadsigns;

import androidx.annotation.NonNull;

public class Sign {
    final String id;
    final String description;
    final String type;

    public Sign(String id, String description, String type) {
        this.id = id;
        this.description = description;
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        return "Sign{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                '}';
    }
}
