package com.aethor.visuals.cosmetics.hiteffect;

import com.aethor.visuals.module.Module;

public class TargetHitEffectModule extends Module {

    public HitEffectMode mode = HitEffectMode.STARS;
    public int particleCount = 12; // сколько частиц спавнить за удар
    public boolean onlyOnCrit = false; // спавнить только на криты, если false — на каждый удар

    public TargetHitEffectModule() {
        super("HitEffect", "cosmetics");
    }

    public void nextMode() {
        HitEffectMode[] modes = HitEffectMode.values();
        int next = (mode.ordinal() + 1) % modes.length;
        mode = modes[next];
    }
}
