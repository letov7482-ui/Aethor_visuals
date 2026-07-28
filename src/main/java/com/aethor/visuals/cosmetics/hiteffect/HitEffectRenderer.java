package com.aethor.visuals.cosmetics.hiteffect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Quaternionf;
import net.minecraft.util.math.Vec3d;

public class HitEffectRenderer {

    private static final Identifier STARS_TEX = Identifier.of("aethor-visuals", "textures/particle/star.png");
    private static final Identifier SPARKS_TEX = Identifier.of("aethor-visuals", "textures/particle/spark.png");
    private static final Identifier HEARTS_TEX = Identifier.of("aethor-visuals", "textures/particle/heart.png");
    private static final Identifier SNOWFLAKE_TEX = Identifier.of("aethor-visuals", "textures/particle/snowflake.png");
    private static final Identifier EXPLOSION_TEX = Identifier.of("aethor-visuals", "textures/particle/spark.png");
    private static final Identifier RUNES_TEX = Identifier.of("aethor-visuals", "textures/particle/rune.png");

    // вызывать в WorldRenderEvents.AFTER_ENTITIES
    public static void render(MatrixStack matrices, VertexConsumerProvider vcp, Vec3d cameraPos) {
        for (HitParticle p : HitEffectManager.getParticles()) {
            renderParticle(matrices, vcp, p, cameraPos);
        }
    }

    private static void renderParticle(MatrixStack matrices, VertexConsumerProvider vcp, HitParticle p, Vec3d cameraPos) {
        matrices.push();

        // переводим в координаты относительно камеры (world space -> render space)
        matrices.translate(p.pos.x - cameraPos.x, p.pos.y - cameraPos.y, p.pos.z - cameraPos.z);

        // billboard — разворачиваем спрайт лицом к камере
        matrices.multiply(MinecraftClient.getInstance().gameRenderer.getCamera().getRotation());
        matrices.multiply(new Quaternionf().rotationZ((float) Math.toRadians(p.rotation)));

        float scale = p.getScale();
        float alpha = p.getAlpha();

        Identifier texture = getTexture(p.mode);
        VertexConsumer buffer = vcp.getBuffer(RenderLayer.getEntityTranslucentEmissive(texture));

        var entry = matrices.peek();
        int light = 15728880; // full bright — частицы светятся сами по себе

        // цвет тонировки зависит от режима (для рун — фиолетовый tint, для остальных — белый = натуральные цвета текстуры)
        float r = 1f, g = 1f, b = 1f;
        if (p.mode == HitEffectMode.RUNES) { r = 0.6f; g = 0.3f; b = 1f; }

        quad(buffer, entry, scale, r, g, b, alpha, light);

        matrices.pop();
    }

    private static void quad(VertexConsumer buffer, MatrixStack.Entry entry, float scale,
                              float r, float g, float b, float alpha, int light) {
        buffer.vertex(entry.getPositionMatrix(), -scale, -scale, 0).color(r, g, b, alpha).texture(0, 1).light(light).normal(entry, 0, 1, 0);
        buffer.vertex(entry.getPositionMatrix(), scale, -scale, 0).color(r, g, b, alpha).texture(1, 1).light(light).normal(entry, 0, 1, 0);
        buffer.vertex(entry.getPositionMatrix(), scale, scale, 0).color(r, g, b, alpha).texture(1, 0).light(light).normal(entry, 0, 1, 0);
        buffer.vertex(entry.getPositionMatrix(), -scale, scale, 0).color(r, g, b, alpha).texture(0, 0).light(light).normal(entry, 0, 1, 0);
    }

    private static Identifier getTexture(HitEffectMode mode) {
        return switch (mode) {
            case STARS -> STARS_TEX;
            case SPARKS -> SPARKS_TEX;
            case HEARTS -> HEARTS_TEX;
            case SNOWFLAKES -> SNOWFLAKE_TEX;
            case EXPLOSION -> EXPLOSION_TEX;
            case RUNES -> RUNES_TEX;
        };
    }
}
