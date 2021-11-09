package net.blay09.mods.waystones.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.WaystoneBlock;
import net.blay09.mods.waystones.block.entity.WaystoneBlockEntity;
import net.blay09.mods.waystones.client.ModRenderers;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.Objects;

public class WaystoneRenderer implements BlockEntityRenderer<WaystoneBlockEntity> {

    private static final Material MATERIAL = new Material(Sheets.SIGN_SHEET, new ResourceLocation(Waystones.MOD_ID, "entity/waystone_active"));

    private final SharestoneModel model;

    public WaystoneRenderer(BlockEntityRendererProvider.Context context) {
        model = new SharestoneModel(context.bakeLayer(ModRenderers.waystoneModel));
    }

    @Override
    public void render(WaystoneBlockEntity tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
        BlockState state = tileEntity.getBlockState();
        if (state.getValue(WaystoneBlock.HALF) != DoubleBlockHalf.LOWER) {
            return;
        }

        float angle = state.getValue(WaystoneBlock.FACING).toYRot();
        matrixStack.pushPose();
        matrixStack.translate(0.5f, 0f, 0.5f);
        matrixStack.mulPose(new Quaternion(0f, angle, 0f, true));
        matrixStack.mulPose(new Quaternion(-180f, 0f, 0f, true));
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        Player player = Minecraft.getInstance().player;
        boolean isActivated = PlayerWaystoneManager.isWaystoneActivated(Objects.requireNonNull(player), tileEntity.getWaystone());
        if (isActivated) {
            matrixStack.scale(1.05f, 1.05f, 1.05f);
            VertexConsumer vertexBuilder = MATERIAL.buffer(buffer, RenderType::entityCutout);
            int light = WaystonesConfig.getActive().disableTextGlow() ? combinedLightIn : 15728880;
            int overlay = WaystonesConfig.getActive().disableTextGlow() ? combinedOverlayIn : OverlayTexture.NO_OVERLAY;
            model.renderToBuffer(matrixStack, vertexBuilder, light, overlay, 1f, 1f, 1f, 1f);
        }
        matrixStack.popPose();
    }
}
