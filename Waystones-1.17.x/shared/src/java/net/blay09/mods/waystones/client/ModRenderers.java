package net.blay09.mods.waystones.client;

import net.blay09.mods.balm.api.client.rendering.BalmRenderers;
import net.blay09.mods.waystones.Waystones;
import net.blay09.mods.waystones.block.ModBlocks;
import net.blay09.mods.waystones.block.SharestoneBlock;
import net.blay09.mods.waystones.block.entity.ModBlockEntities;
import net.blay09.mods.waystones.client.render.*;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

public class ModRenderers {
    public static ModelLayerLocation portstoneModel;
    public static ModelLayerLocation sharestoneModel;
    public static ModelLayerLocation waystoneModel;

    public static void initialize(BalmRenderers renderers) {
        portstoneModel = renderers.registerModel(new ResourceLocation(Waystones.MOD_ID, "portstone"), () -> PortstoneModel.createLayer(CubeDeformation.NONE));
        sharestoneModel = renderers.registerModel(new ResourceLocation(Waystones.MOD_ID, "sharestone"), () -> SharestoneModel.createLayer(CubeDeformation.NONE));
        waystoneModel = renderers.registerModel(new ResourceLocation(Waystones.MOD_ID, "waystone"), () -> WaystoneModel.createLayer(CubeDeformation.NONE));

        renderers.registerBlockEntityRenderer(ModBlockEntities.waystone::get, WaystoneRenderer::new);
        renderers.registerBlockEntityRenderer(ModBlockEntities.sharestone::get, SharestoneRenderer::new);
        renderers.registerBlockEntityRenderer(ModBlockEntities.portstone::get, PortstoneRenderer::new);

        renderers.registerBlockColorHandler((state, view, pos, tintIndex) -> Objects.requireNonNull(((SharestoneBlock) state.getBlock()).getColor()).getTextColor(), () -> ModBlocks.scopedSharestones);
        renderers.registerItemColorHandler((stack, tintIndex) -> Objects.requireNonNull(((SharestoneBlock) Block.byItem((stack.getItem()))).getColor()).getTextColor(), () -> ModBlocks.scopedSharestones);

        renderers.setBlockRenderType(() -> ModBlocks.sharestone, RenderType.cutout());
    }

}
