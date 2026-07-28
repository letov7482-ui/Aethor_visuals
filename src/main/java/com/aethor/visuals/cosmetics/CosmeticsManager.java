package com.aethor.visuals.cosmetics;

import com.aethor.visuals.cosmetics.wings.WingAnimator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CosmeticsManager {

    private static final Map<UUID, WingAnimator> wingAnimators = new HashMap<>();
    private static final Map<UUID, Boolean> wasOnGround = new HashMap<>();

    public static WingAnimator getWings(PlayerEntity player) {
        return wingAnimators.computeIfAbsent(player.getUuid(), id -> new WingAnimator());
    }

    // вызывать каждый клиентский тик для игрока с активной косметикой
    public static void tick(PlayerEntity player) {
        WingAnimator animator = getWings(player);
        float deltaTime = 1f / 20f; // фиксированный tick, партиал тики добавляй в рендере

        animator.tick(player, deltaTime);

        boolean onGround = player.isOnGround();
        boolean prevOnGround = wasOnGround.getOrDefault(player.getUuid(), true);

        // прыжок = момент отрыва от земли -> триггерим взмах
        if (prevOnGround && !onGround && player.getVelocity().y > 0) {
            animator.triggerFlap();
        }

        wasOnGround.put(player.getUuid(), onGround);
    }
}
