package net.blay09.mods.waystones.client.gui.widget;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.waystones.api.IWaystone;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class RemoveWaystoneButton extends Button implements ITooltipProvider {

    private static final ResourceLocation BEACON = new ResourceLocation("textures/gui/container/beacon.png");

    private final List<Component> tooltip;
    private final List<Component> activeTooltip;
    private final int visibleRegionStart;
    private final int visibleRegionHeight;
    private static boolean shiftGuard;

    public RemoveWaystoneButton(int x, int y, int visibleRegionStart, int visibleRegionHeight, IWaystone waystone, OnPress pressable) {
        super(x, y, 13, 13, new TextComponent(""), pressable);
        this.visibleRegionStart = visibleRegionStart;
        this.visibleRegionHeight = visibleRegionHeight;
        tooltip = Lists.newArrayList(new TranslatableComponent("gui.waystones.waystone_selection.hold_shift_to_delete"));
        activeTooltip = Lists.newArrayList(new TranslatableComponent("gui.waystones.waystone_selection.click_to_delete"));
        if (waystone.isGlobal()) {
            TranslatableComponent component = new TranslatableComponent("gui.waystones.waystone_selection.deleting_global_for_all");
            component.withStyle(ChatFormatting.DARK_RED);
            tooltip.add(component);
            activeTooltip.add(component);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            shiftGuard = true;
            return true;
        }

        return false;
    }

    @Override
    public void renderButton(PoseStack matrixStack, int mouseX, int mouseY, float partial) {
        boolean shiftDown = Screen.hasShiftDown();
        if (!shiftDown) {
            shiftGuard = false;
        }
        active = !shiftGuard && shiftDown;

        if (mouseY >= visibleRegionStart && mouseY < visibleRegionStart + visibleRegionHeight) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.setShaderTexture(0, BEACON);
            if (isHovered && active) {
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            } else {
                RenderSystem.setShaderColor(0.5f, 0.5f, 0.5f, 0.5f);
            }
            blit(matrixStack, x, y, 114, 223, 13, 13);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
    }

    @Override
    public boolean shouldShowTooltip() {
        return isHovered;
    }

    @Override
    public List<Component> getTooltip() {
        return active ? activeTooltip : tooltip;
    }
}
