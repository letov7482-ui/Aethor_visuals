package com.aethor.visuals.module.categories.render;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

public class TargetEspRenderer {

    // Рисуем плашку таргета в углу экрана (стиль как в топ-клиентах: слева сверху)
    public static void renderHud(DrawContext context, TargetEspModule module) {
        LivingEntity target = module.getCurrentTarget();
        if (target == null) return;

        int x = 10;
        int y = 40;
        int barWidth = 100;
        int barHeight = 4;

        float maxHealth = target.getMaxHealth();
        float health = module.getSmoothHealth();
        float healthPercent = MathHelper.clamp(health / maxHealth, 0f, 1f);

        // имя таргета
        context.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                Text.literal(target.getName().getString()), x, y, 0xFFFFFFFF, true);

        // фон бара
        context.fill(x, y + 12, x + barWidth, y + 12 + barHeight, 0x80000000);

        // цвет бара плавно меняется зелёный -> жёлтый -> красный по проценту HP
        int barColor = getHealthColor(healthPercent);
        int filledWidth = (int) (barWidth * healthPercent);
        context.fill(x, y + 12, x + filledWidth, y + 12 + barHeight, barColor);

        // текст HP
        String hpText = String.format("%.1f / %.1f", health, maxHealth);
        context.drawText(net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                Text.literal(hpText), x, y + 18, 0xFFAAAAAA, false);
    }

    private static int getHealthColor(float percent) {
        int r, g;
        if (percent > 0.5f) {
            // зелёный -> жёлтый
            r = (int) (255 * (1 - (percent - 0.5f) * 2));
            g = 255;
        } else {
            // жёлтый -> красный
            r = 255;
            g = (int) (255 * (percent * 2));
        }
        return 0xFF000000 | (r << 16) | (g << 8);
    }
}
