package com.aethor.visuals.cosmetics.wings;

import net.minecraft.util.math.Vec3d;

// Одна "кость" крыла с verlet-физикой (инерция, лёгкое отставание)
public class WingBone {

    private Vec3d currentPos;
    private Vec3d oldPos;
    public Vec3d targetPos; // куда должна стремиться кость (анимация)

    private final float stiffness; // жёсткость (0.05 = мягко/тряско, 0.3 = жёстко)
    private final float damping;   // затухание колебаний

    public WingBone(Vec3d start, float stiffness, float damping) {
        this.currentPos = start;
        this.oldPos = start;
        this.targetPos = start;
        this.stiffness = stiffness;
        this.damping = damping;
    }

    public void update(float deltaTime) {
        // verlet: velocity = current - old
        Vec3d velocity = currentPos.subtract(oldPos).multiply(damping);

        // сила притяжения к target (пружина)
        Vec3d toTarget = targetPos.subtract(currentPos).multiply(stiffness);

        oldPos = currentPos;
        currentPos = currentPos.add(velocity).add(toTarget);
    }

    public Vec3d getRenderPos() {
        return currentPos;
    }
}
