package com.aethor.visuals.cosmetics.trail;

import net.minecraft.util.math.Vec3d;

import java.util.ArrayDeque;
import java.util.Deque;

public class PlayerTrail {

    private static final int MAX_POINTS = 20;
    private final Deque<TrailPoint> points = new ArrayDeque<>();

    public void addPoint(Vec3d pos) {
        points.addFirst(new TrailPoint(pos, System.currentTimeMillis()));
        if (points.size() > MAX_POINTS) {
            points.removeLast();
        }
    }

    public void tick() {
        long now = System.currentTimeMillis();
        points.removeIf(p -> now - p.spawnTime > 800); // время жизни точки 800ms
    }

    public Deque<TrailPoint> getPoints() {
        return points;
    }

    public static class TrailPoint {
        public final Vec3d pos;
        public final long spawnTime;

        public TrailPoint(Vec3d pos, long spawnTime) {
            this.pos = pos;
            this.spawnTime = spawnTime;
        }

        // 1.0 = свежая точка, 0.0 = исчезла
        public float getAlpha() {
            long age = System.currentTimeMillis() - spawnTime;
            float t = 1f - (age / 800f);
            return Math.max(0f, easeIn(t));
        }

        private float easeIn(float t) {
            return t * t;
        }
    }
}
