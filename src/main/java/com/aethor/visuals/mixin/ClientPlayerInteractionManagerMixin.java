package com.aethor.visuals.mixin;

import com.aethor.visuals.AethorVisuals;
import com.aethor.visuals.cosmetics.hiteffect.HitEffectManager;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    // вызывается когда клиент отправляет пакет атаки по entity — идеальное место для триггера
    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void onAttackEntity(net.minecraft.entity.player.PlayerEntity player, Entity target, CallbackInfo ci) {
        if (!AethorVisuals.hitEffect.isEnabled()) return;

        boolean isCrit = player.fallDistance > 0.0f && !player.isOnGround() && !player.isClimbing();
        if (AethorVisuals.hitEffect.onlyOnCrit && !isCrit) return;

        HitEffectManager.spawnHit(target.getBoundingBox().getCenter(), AethorVisuals.hitEffect);
    }
}
