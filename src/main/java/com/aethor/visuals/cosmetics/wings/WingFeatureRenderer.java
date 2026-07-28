package com.aethor.visuals.cosmetics.wings;

import com.aethor.visuals.cosmetics.CosmeticsManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class WingFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {

    private static final Identifier WING_TEXTURE =
            Identifier.of("aethor", "textures/cosmetics/wings/angel.png");

    public WingFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context) {
        super(context);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vcp, int light,
                        PlayerEntityRenderState state, float limbAngle, float limbDistance) {

        // достаём аниматор по игроку (у тебя уже есть маппинг state -> player.uuid)
        WingAnimator animator = CosmeticsManager.getWingsByState(state);
        if (animator == null) return;

        matrices.push();
        matrices.translate(0, 0.3, 0.2); // позиция крепления крыльев на спине

        VertexConsumer buffer = vcp.getBuffer(RenderLayer.getEntityTranslucent(WING_TEXTURE));

        renderWing(matrices, buffer, animator.getLeftWing(), light, false);
        renderWing(matrices, buffer, animator.getRightWing(), light, true);

        matrices.pop();
    }

    // рисуем крыло как ленту quad'ов между костями (сегментами)
    private void renderWing(MatrixStack matrices, VertexConsumer buffer, List<WingBone> bones, int light, boolean mirror) {
        var entry = matrices.peek();
        float mirrorMul = mirror ? -1f : 1f;

        for (int i = 0; i < bones.size() - 1; i++) {
            Vec3d p1 = bones.get(i).getRenderPos();
            Vec3d p2 = bones.get(i + 1).getRenderPos();

            float width = 0.4f - i * 0.05f; // крыло сужается к концу
            float u0 = (float) i / bones.size();
            float u1 = (float) (i + 1) / bones.size();

            float x1 = (float) p1.x * mirrorMul, y1 = (float) p1.y, z1 = (float) p1.z;
            float x2 = (float) p2.x * mirrorMul, y2 = (float) p2.y, z2 = (float) p2.z;

            // quad: верхний край - нижний край сегмента
            vertex(buffer, entry, x1, y1 + width, z1, u0, 0, light);
            vertex(buffer, entry, x1, y1 - width, z1, u0, 1, light);
            vertex(buffer, entry, x2, y2 - width, z2, u1, 1, light);
            vertex(buffer, entry, x2, y2 + width, z2, u1, 0, light);
        }
    }

    private void vertex(VertexConsumer buffer, MatrixStack.Entry entry, float x, float y, float z,
                         float u, float v, int light) {
        buffer.vertex(entry.getPositionMatrix(), x, y, z)
                .color(255, 255, 255, 255)
                .texture(u, v)
                .overlay(net.minecraft.client.render.OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(entry, 0, 1, 0);
    }
}
