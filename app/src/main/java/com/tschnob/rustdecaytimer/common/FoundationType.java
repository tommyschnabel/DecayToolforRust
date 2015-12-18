package com.tschnob.rustdecaytimer.common;

public enum FoundationType {
    STICK("Stick"),
    WOOD("Wood"),
    STONE("Stone"),
    SHEET_METAL("Sheet Metal"),
    ARMORED("Armored");

    private String prettyName;

    FoundationType(String prettyName) {
        this.prettyName = prettyName;
    }

    @Override
    public String toString() {
        return prettyName;
    }
}
