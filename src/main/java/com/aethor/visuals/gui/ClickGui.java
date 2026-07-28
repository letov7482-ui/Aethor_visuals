package com.aethor.visuals.gui;

import com.aethor.visuals.AethorVisuals;
import com.aethor.visuals.module.Module;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClickGui extends Screen {

    private static final String[] CATEGORIES = {
            "Render", "Cosmetics", "Combat", "HUD", "World", "Utility", "Misc"
    };

    private final Map<String, CategoryPanel> panels = new LinkedHashMap<>();
    private String searchQuery = "";

    // анимация открытия окна (fade + scale)
    private float openProgress = 0f;

    public ClickGui() {
        super(Text.literal("Aethor Visuals"));
    }

    @Override
    protected void init() {
        panels.clear();
        int x = 20;
        int y = 20;

        for (String category : CATEGORIES) {
            List<Module> modules = AethorVisuals.moduleManager.getByCategory(category.toLowerCase());
            panels.put(category, new CategoryPanel(category, x, y, modules));
            x += 130; // следующая категория правее
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // easeOutCubic анимация появления
        openProgress = Math.min(1f, openProgress + delta * 0.15f);
        float eased = 1 - (float) Math.pow(1 - openProgress, 3);

        // тёмная подложка на весь экран
        context.fill(0, 0, this.width, this.height, (int) (0x60 * eased) << 24);

        for (CategoryPanel panel : panels.values()) {
            panel.render(context, mouseX, mouseY, eased, searchQuery);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (CategoryPanel panel : panels.values()) {
            if (panel.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        // ввод в поиск модулей (Ctrl+F стиль — просто печатаешь пока гуи открыт)
        searchQuery += chr;
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 259 && !searchQuery.isEmpty()) { // backspace
            searchQuery = searchQuery.substring(0, searchQuery.length() - 1);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false; // не ставим игру на паузу как ванильное меню
    }
}
