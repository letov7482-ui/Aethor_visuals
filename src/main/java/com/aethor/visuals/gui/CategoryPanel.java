package com.aethor.visuals.gui;

import com.aethor.visuals.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.List;

public class CategoryPanel {

    private final String title;
    private final int x, y;
    private final int width = 120;
    private final int headerHeight = 20;
    private final int moduleHeight = 16;
    private final List<Module> modules;

    // цветовая тема (можно вынести в ThemeManager позже)
    private static final int HEADER_COLOR = 0xFF1A1A22;
    private static final int PANEL_COLOR = 0xE60F0F14;
    private static final int ACCENT_COLOR = 0xFF6C4CE0; // фиолетовый акцент, современно
    private static final int TEXT_COLOR = 0xFFDDDDDD;

    public CategoryPanel(String title, int x, int y, List<Module> modules) {
        this.title = title;
        this.x = x;
        this.y = y;
        this.modules = modules;
    }

    public void render(DrawContext context, int mouseX, int mouseY, float eased, String search) {
        int panelX = (int) (x - (1 - eased) * 40); // лёгкое смещение при анимации появления
        int alpha = (int) (255 * eased) << 24;

        // хедер категории
        context.fill(panelX, y, panelX + width, y + headerHeight, (HEADER_COLOR & 0x00FFFFFF) | alpha);
        context.drawText(MinecraftClient.getInstance().textRenderer, Text.literal(title),
                panelX + 6, y + 6, (TEXT_COLOR & 0x00FFFFFF) | alpha, false);

        int currentY = y + headerHeight;

        for (Module module : modules) {
            if (!search.isEmpty() && !module.getName().toLowerCase().contains(search.toLowerCase())) {
                continue; // фильтр поиска
            }

            boolean hovered = mouseX >= panelX && mouseX <= panelX + width
                    && mouseY >= currentY && mouseY <= currentY + moduleHeight;

            int bg = module.isEnabled() ? ACCENT_COLOR : (hovered ? 0xFF2A2A33 : PANEL_COLOR);
            context.fill(panelX, currentY, panelX + width, currentY + moduleHeight, (bg & 0x00FFFFFF) | alpha);

            int textColor = module.isEnabled() ? 0xFFFFFFFF : (TEXT_COLOR & 0x00FFFFFF) | alpha;
            context.drawText(MinecraftClient.getInstance().textRenderer, Text.literal(module.getName()),
                    panelX + 6, currentY + 4, textColor, false);

            currentY += moduleHeight;
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int currentY = y + headerHeight;

        for (Module module : modules) {
            boolean hovered = mouseX >= x && mouseX <= x + width
                    && mouseY >= currentY && mouseY <= currentY + moduleHeight;

            if (hovered && button == 0) {
                module.toggle();
                return true;
            }
            currentY += moduleHeight;
        }
        return false;
    }
}
