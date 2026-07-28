package com.aethor.visuals.cosmetics.wings;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class WingAnimator {

    private static final int SEGMENTS = 4; // сегменты на каждое крыло
    private final List<WingBone> leftWing = new ArrayList<>();
    private final List<WingBone> rightWing = new ArrayList<>();

    private float flapTimer = 0f;
    private boolean flapping = false;

    public WingAnimator() {
        for (int i = 0; i < SEGMENTS; i++) {
            leftWing.add(new WingBone(Vec3d.ZERO, 0.15f - i * 0.02f, 0.85f));
            rightWing.add(new WingBone(Vec3d.ZERO, 0.15f - i * 0.02f, 0.85f));
        }
    }

    public void tick(PlayerEntity player, float deltaTime) {
        // idle покачивание через синус — "дыхание" крыльев
        float idleWave = (float) Math.sin(player.age * 0.05f) * 0.05f;

        // если игрок прыгнул недавно — полный взмах
        if (flapping) {
            flapTimer += deltaTime * 8f;
            if (flapTimer >= 1f) {
                flapping = false;
                flapTimer = 0f;
            }
        }

        float flapOffset = flapping ? easeOutBack(flapTimer) * 0.4f : 0f;

        // задаём target-позиции по сегментам — каждый следующий сегмент
        // получает волновую задержку относительно предыдущего
        for (int i = 0; i < SEGMENTS; i++) {
            float segmentDelay = i * 0.15f;
            float wave = (float) Math.sin((player.age * 0.05f) - segmentDelay) * (0.05f + i * 0.02f);

            Vec3d target = new Vec3d(-i * 0.2, -i * 0.1 + wave + idleWave + flapOffset, i * 0.05);
            leftWing.get(i).targetPos = target;
            rightWing.get(i).targetPos = new Vec3d(-target.x, target.y, target.z);

            leftWing.get(i).update(deltaTime);
            rightWing.get(i).update(deltaTime);
        }
    }

    public void triggerFlap() {
        flapping = true;
        flapTimer = 0f;
    }

    // easing с лёгким "перехлёстом" — даёт пружинистость взмаху
    private float easeOutBack(float t) {
        float c1 = 1.70158f;
        float c3 = c1 + 1;
        return 1 + c3 * (float) Math.pow(t - 1, 3) + c1 * (float) Math.pow(t - 1, 2);
    }

    public List<WingBone> getLeftWing() { return leftWing; }
    public List<WingBone> getRightWing() { return rightWing; }
}
