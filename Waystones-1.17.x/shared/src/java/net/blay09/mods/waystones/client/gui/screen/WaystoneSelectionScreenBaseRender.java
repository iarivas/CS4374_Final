package shared.src.java.net.blay09.mods.waystones.client.gui.screen;

import java.util.Optional;

public class WaystoneSelectionScreenBaseRender extends WaystoneSelectionScreenBase {
    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderTooltip(matrixStack, mouseX, mouseY);
        for (net.blay09.mods.waystones.client.gui.widget.ITooltipProvider tooltipProvider : tooltipProviders) {
            if (tooltipProvider.shouldShowTooltip()) {
                renderTooltip(matrixStack, tooltipProvider.getTooltip(), Optional.empty(), mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderBg(PoseStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mouseX, int mouseY) {
        Font fontRenderer = Minecraft.getInstance().font;

        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        net.blay09.mods.waystones.api.IWaystone fromWaystone = menu.getWaystoneFrom();
        drawCenteredString(matrixStack, fontRenderer, getTitle(), imageWidth / 2, headerY + (fromWaystone != null ? 20 : 0), 0xFFFFFF);
        if (fromWaystone != null) {
            drawLocationHeader(matrixStack, fromWaystone, mouseX, mouseY, imageWidth / 2, headerY);
        }

        if (waystones.size() == 0) {
            drawCenteredString(matrixStack, fontRenderer, ChatFormatting.RED + I18n.get("gui.waystones.waystone_selection.no_waystones_activated"), imageWidth / 2, imageHeight / 2 - 20, 0xFFFFFF);
        }
    }

    private void drawLocationHeader(PoseStack matrixStack, net.blay09.mods.waystones.api.IWaystone waystone, int mouseX, int mouseY, int x, int y) {
        Font fontRenderer = Minecraft.getInstance().font;

        String locationPrefix = ChatFormatting.YELLOW + I18n.get("gui.waystones.waystone_selection.current_location") + " ";
        int locationPrefixWidth = fontRenderer.width(locationPrefix);

        String effectiveName = waystone.getName();
        if (effectiveName.isEmpty()) {
            effectiveName = I18n.get("gui.waystones.waystone_selection.unnamed_waystone");
        }
        int locationWidth = fontRenderer.width(effectiveName);

        int fullWidth = locationPrefixWidth + locationWidth;

        int startX = x - fullWidth / 2 + locationPrefixWidth;
        int startY = y + topPos;
        isLocationHeaderHovered = mouseX >= startX && mouseX < startX + locationWidth + 16
                && mouseY >= startY && mouseY < startY + fontRenderer.lineHeight;

        Player player = Minecraft.getInstance().player;
        net.blay09.mods.waystones.core.WaystoneEditPermissions waystoneEditPermissions = net.blay09.mods.waystones.core.PlayerWaystoneManager.mayEditWaystone(player, player.level, waystone);

        String fullText = locationPrefix + ChatFormatting.WHITE;
        if (isLocationHeaderHovered && waystoneEditPermissions == net.blay09.mods.waystones.core.WaystoneEditPermissions.ALLOW) {
            fullText += ChatFormatting.UNDERLINE;
        }
        fullText += effectiveName;

        drawString(matrixStack, fontRenderer, fullText, x - fullWidth / 2, y, 0xFFFFFF);

        if (isLocationHeaderHovered && waystoneEditPermissions == net.blay09.mods.waystones.core.WaystoneEditPermissions.ALLOW) {
            PoseStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushPose();
            modelViewStack.translate(x + fullWidth / 2f + 4, y, 0f);
            float scale = 0.5f;
            modelViewStack.scale(scale, scale, scale);
            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
            Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(new ItemStack(Items.WRITABLE_BOOK), 0, 0);
            modelViewStack.popPose();
        }
    }

}
