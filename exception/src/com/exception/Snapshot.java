package com.exception;

import java.io.Serializable;

public class Snapshot implements Serializable {
    private final String name;
    private final String value;

    public Snapshot(String name, String value) {
        this.name=name;
        this.value=value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("[%s=%s]",name,value);
    }
}
