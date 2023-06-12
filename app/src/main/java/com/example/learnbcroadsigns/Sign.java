package com.example.learnbcroadsigns;

import androidx.annotation.NonNull;

public class Sign {
    final String id;
    final String description;
    final Type type;

    public Sign(String id, String description, String type) {
        this.id = id;
        this.description = description;
        this.type = Type.valueOf(type);
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

    enum Type {
        REGULATORY,
        SCHOOL,
        LANE_USE,
        TURN_CONTROL,
        PARKING
    }
}
