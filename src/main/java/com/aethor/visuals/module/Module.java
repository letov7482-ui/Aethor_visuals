package com.aethor.visuals.module;

public class Module {

    private final String name;
    private final String category;
    private boolean enabled = false;

    public Module(String name, String category) {
        this.name = name;
        this.category = category;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }

    public boolean isEnabled() { return enabled; }

    public void toggle() {
        enabled = !enabled;
        if (enabled) onEnable();
        else onDisable();
    }

    public void setEnabled(boolean value) {
        if (value == enabled) return;
        enabled = value;
        if (enabled) onEnable();
        else onDisable();
    }

    // переопределяются в наследниках при желании
    protected void onEnable() {}
    protected void onDisable() {}
}
