package com.aethor.visuals.cosmetics.trail;

import com.aethor.visuals.util.CatmullRom;
import net.minecraft.client.render.*;
import net.minecraft.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TrailRenderer {

    // hue циклится со временем для rainbow-режима
    private static float hue = 0f;

    public static void render(MatrixStack matrices, VertexConsumerProvider vcp, PlayerTrail trail, boolean rainbow, Color baseColor) {
        List<PlayerTrail.TrailPoint> pts = new ArrayList<>(trail.getPoints());
        if (pts.size() < 4) return;

        VertexConsumer buffer = vcp.getBuffer(RenderLayer.getEntityTranslucentEmissive(
                net.minecraft.util.Identifier.of("aethor", "textures/trail/particle.png")));

        if (rainbow) {
            hue += 0.005f;
            if (hue > 1f) hue -= 1f;
        }

        // проходим по сегментам, интерполируя Catmull-Rom для сглаживания
        for (int i = 1; i < pts.size() - 2; i++) {
            Vec3d p0 = pts.get(i - 1).pos;
            Vec3d p1 = pts.get(i).pos;
            Vec3d p2 = pts.get(i + 1).pos;
            Vec3d p3 = pts.get(i + 2).pos;

            float alpha = pts.get(i).getAlpha();
            if (alpha <= 0f) continue;

            Color color = rainbow
                    ? Color.getHSBColor(hue + (i * 0.02f), 1f, 1f)
                    : baseColor;

            // разбиваем сегмент на 5 под-точек для гладкости
            for (int s = 0; s < 5; s++) {
                double t = s / 5.0;
                Vec3d point = CatmullRom.interpolate(p0, p1, p2, p3, t);

                float width = 0.15f * alpha; // трейл сужается к хвосту
                addQuad(matrices, buffer, point, width, color, alpha);
            }
        }
    }

    private static void addQuad(MatrixStack matrices, VertexConsumer buffer, Vec3d pos, float width, Color color, float alpha) {
        var entry = matrices.peek();
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;

        buffer.vertex(entry.getPositionMatrix(), (float) pos.x - width, (float) pos.y, (float) pos.z)
                .color(r, g, b, alpha)
                .texture(0, 0)
                .light(15728880)
                .normal(entry, 0, 1, 0);

        buffer.vertex(entry.getPositionMatrix(), (float) pos.x + width, (float) pos.y, (float) pos.z)
                .color(r, g, b, alpha)
                .texture(1, 0)
                .light(15728880)
                .normal(entry, 0, 1, 0);
    }
}
