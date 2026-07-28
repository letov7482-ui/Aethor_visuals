package com.aethor.visuals;

import com.aethor.visuals.cosmetics.CosmeticsManager;
import com.aethor.visuals.module.ModuleManager;
import com.aethor.visuals.module.categories.render.TargetEspModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class AethorVisuals implements ClientModInitializer {

    public static final String MOD_ID = "aethor-visuals";

    // синглтоны модулей — доступ к ним отовсюду через AethorVisuals.targetEsp и т.д.
    public static ModuleManager moduleManager;
    public static TargetEspModule targetEsp;

    @Override
    public void onInitializeClient() {
        moduleManager = new ModuleManager();

        targetEsp = new TargetEspModule();
        moduleManager.register(targetEsp);

        // тикаем модули и косметику каждый клиентский тик
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            targetEsp.tick();
            CosmeticsManager.tick(client.player);
        });

        System.out.println("[Aethor Visuals] Loaded successfully!");
    }
}
