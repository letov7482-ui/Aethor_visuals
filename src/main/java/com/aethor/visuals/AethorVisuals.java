package com.aethor.visuals;

import com.aethor.visuals.cosmetics.CosmeticsManager;
import com.aethor.visuals.cosmetics.hiteffect.HitEffectManager;
import com.aethor.visuals.cosmetics.hiteffect.HitEffectRenderer;
import com.aethor.visuals.cosmetics.hiteffect.TargetHitEffectModule;
import com.aethor.visuals.gui.ClickGui;
import com.aethor.visuals.gui.Watermark;
import com.aethor.visuals.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AethorVisuals implements ClientModInitializer {

    public static final String MOD_ID = "aethor-visuals";

    // менеджер всех модулей — доступ отовсюду через AethorVisuals.moduleManager
    public static ModuleManager moduleManager;

    // косметика: эффект частиц при ударе по цели (звёзды/искры/сердечки и т.д.)
    public static TargetHitEffectModule hitEffect;

    private static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        moduleManager = new ModuleManager();

        // регистрация модулей
        hitEffect = new TargetHitEffectModule();
        moduleManager.register(hitEffect);

        // бинд открытия ClickGUI — Right Shift по дефолту
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.aethor-visuals.open-gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "category.aethor-visuals"
        ));

        // тик каждый клиентский тик (20 раз в секунду)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            CosmeticsManager.tick(client.player);
            HitEffectManager.tick(1f / 20f);

            while (openGuiKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new ClickGui());
                }
            }
        });

        // рендер HUD-элементов поверх экрана (watermark и т.д.)
        HudRenderCallback.EVENT.register((context, tickDelta) -> {
            Watermark.render(context);
        });

        // рендер частиц косметики в мире (billboard-спрайты)
        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            HitEffectRenderer.render(context.matrixStack(), context.consumers(),
                    context.camera().getPos());
        });

        System.out.println("[Aethor Visuals] Loaded successfully!");
    }
                                                         }
