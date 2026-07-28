package com.aethor.visuals.module.categories.render;

import com.aethor.visuals.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class TargetEspModule extends Module {

    public boolean onlyCrosshair = true;
    public boolean playersOnly = false;
    public double range = 5.0;

    private LivingEntity currentTarget;
    private float smoothHealth = -1f; // для плавной анимации убывания HP

    public TargetEspModule() {
        super("TargetESP", "render");
    }

    // вызывать каждый клиентский тик
    public void tick() {
        if (!isEnabled()) {
            currentTarget = null;
            return;
        }

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null) return;

        currentTarget = raycastTarget(mc);

        if (currentTarget != null) {
            float targetHealth = currentTarget.getHealth();
            if (smoothHealth < 0) {
                smoothHealth = targetHealth; // первая инициализация без анимации
            } else {
                // плавно догоняем реальное HP (easeOut через lerp)
                smoothHealth += (targetHealth - smoothHealth) * 0.15f;
            }
        } else {
            smoothHealth = -1f;
        }
    }

    private LivingEntity raycastTarget(MinecraftClient mc) {
        Vec3d start = mc.player.getCameraPosVec(1f);
        Vec3d look = mc.player.getRotationVec(1f);
        Vec3d end = start.add(look.multiply(range));

        Box searchBox = mc.player.getBoundingBox().stretch(look.multiply(range)).expand(1.0);

        EntityHitResult result = net.minecraft.entity.projectile.ProjectileUtil.raycast(
                mc.player, start, end, searchBox,
                entity -> {
                    if (!(entity instanceof LivingEntity)) return false;
                    if (entity == mc.player) return false;
                    if (playersOnly && !(entity instanceof PlayerEntity)) return false;
                    return true;
                },
                range * range
        );

        if (result != null && result.getEntity() instanceof LivingEntity living) {
            return living;
        }
        return null;
    }

    public LivingEntity getCurrentTarget() {
        return currentTarget;
    }

    public float getSmoothHealth() {
        return smoothHealth;
    }
}
