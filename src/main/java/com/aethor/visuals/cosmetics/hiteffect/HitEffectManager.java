package com.aethor.visuals.cosmetics.hiteffect;

import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HitEffectManager {

    private static final List<HitParticle> particles = new ArrayList<>();
    private static final Random random = new Random();

    // вызывается когда клиент видит удар по entity (хук ниже)
    public static void spawnHit(Vec3d targetPos, TargetHitEffectModule module) {
        int count = module.particleCount;

        for (int i = 0; i < count; i++) {
            // случайное направление разлёта частицы (сфера)
            double angle = random.nextDouble() * Math.PI * 2;
            double upAngle = random.nextDouble() * Math.PI - Math.PI / 2;
            double speed = 0.05 + random.nextDouble() * 0.1;

            Vec3d velocity = new Vec3d(
                    Math.cos(angle) * Math.cos(upAngle) * speed,
                    Math.sin(upAngle) * speed + 0.05, // небольшой импульс вверх
                    Math.sin(angle) * Math.cos(upAngle) * speed
            );

            // небольшой рандомный оффсет спавна, чтобы не все из одной точки
            Vec3d spawnPos = targetPos.add(
                    (random.nextDouble() - 0.5) * 0.3,
                    (random.nextDouble() - 0.5) * 0.3,
                    (random.nextDouble() - 0.5) * 0.3
            );

            int lifetime = switch (module.mode) {
                case STARS -> 600;
                case SPARKS -> 300;
                case HEARTS -> 800;
                case SNOWFLAKES -> 1200;
                case EXPLOSION -> 400;
                case RUNES -> 1000;
            };

            float scale = 0.15f + random.nextFloat() * 0.1f;

            particles.add(new HitParticle(spawnPos, velocity, module.mode, lifetime, scale));
        }
    }

    // вызывать каждый клиентский рендер-тик
    public static void tick(float deltaTime) {
        particles.removeIf(HitParticle::isDead);
        for (HitParticle p : particles) {
            p.update(deltaTime);
        }
    }

    public static List<HitParticle> getParticles() {
        return particles;
    }
}
