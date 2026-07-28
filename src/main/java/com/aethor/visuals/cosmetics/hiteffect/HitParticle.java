package com.aethor.visuals.cosmetics.hiteffect;

import net.minecraft.util.math.Vec3d;

public class HitParticle {

    public Vec3d pos;
    public Vec3d velocity;
    public final HitEffectMode mode;
    public final long spawnTime;
    public final int lifetimeMs;
    public float rotation;
    public final float rotationSpeed;
    public final float scale;

    public HitParticle(Vec3d pos, Vec3d velocity, HitEffectMode mode, int lifetimeMs, float scale) {
        this.pos = pos;
        this.velocity = velocity;
        this.mode = mode;
        this.spawnTime = System.currentTimeMillis();
        this.lifetimeMs = lifetimeMs;
        this.rotation = (float) (Math.random() * 360);
        this.rotationSpeed = (float) (Math.random() * 8 - 4);
        this.scale = scale;
    }

    // вызывать каждый рендер-тик, deltaTime в секундах
    public void update(float deltaTime) {
        pos = pos.add(velocity.multiply(deltaTime));

        // гравитация/поведение зависит от режима
        switch (mode) {
            case STARS, HEARTS -> velocity = velocity.add(0, -0.015 * deltaTime * 20, 0); // лёгкая гравитация
            case SNOWFLAKES -> {
                // снежинки кружатся при падении
                double sway = Math.sin(System.currentTimeMillis() * 0.003 + pos.x) * 0.01;
                velocity = new Vec3d(velocity.x + sway, -0.02, velocity.z);
            }
            case SPARKS -> velocity = velocity.multiply(0.92); // быстро затухают по скорости
            case EXPLOSION -> velocity = velocity.multiply(0.88);
            case RUNES -> { /* руны неподвижны, только вращаются и тают */ }
        }

        rotation += rotationSpeed * deltaTime * 20;
    }

    public boolean isDead() {
        return System.currentTimeMillis() - spawnTime > lifetimeMs;
    }

    // 1.0 = полностью видна, 0.0 = исчезла
    public float getAlpha() {
        long age = System.currentTimeMillis() - spawnTime;
        float t = 1f - (age / (float) lifetimeMs);
        return Math.max(0f, easeIn(t));
    }

    // scale растёт в начале (spawn pop) и падает к концу
    public float getScale() {
        long age = System.currentTimeMillis() - spawnTime;
        float progress = age / (float) lifetimeMs;

        if (progress < 0.15f) {
            // быстрый pop-in scale 0 -> 1
            return scale * easeOutBack(progress / 0.15f);
        }
        return scale * (1f - easeIn((progress - 0.15f) / 0.85f) * 0.3f);
    }

    private float easeIn(float t) { return t * t; }

    private float easeOutBack(float t) {
        float c1 = 1.70158f, c3 = c1 + 1;
        return 1 + c3 * (float) Math.pow(t - 1, 3) + c1 * (float) Math.pow(t - 1, 2);
    }
  }
