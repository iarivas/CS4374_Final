package net.blay09.mods.waystones.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.mixin.ScreenAccessor;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.client.gui.widget.ITooltipProvider;
import net.blay09.mods.waystones.client.gui.widget.RemoveWaystoneButton;
import net.blay09.mods.waystones.client.gui.widget.SortWaystoneButton;
import net.blay09.mods.waystones.client.gui.widget.WaystoneButton;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.blay09.mods.waystones.core.PlayerWaystoneManager;
import net.blay09.mods.waystones.core.WaystoneEditPermissions;
import net.blay09.mods.waystones.network.message.RemoveWaystoneMessage;
import net.blay09.mods.waystones.network.message.RequestEditWaystoneMessage;
import net.blay09.mods.waystones.network.message.SelectWaystoneMessage;
import net.blay09.mods.waystones.network.message.SortWaystoneMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class WaystoneSelectionScreenBase extends AbstractContainerScreen<WaystoneSelectionMenu> {

    private final List<IWaystone> waystones;
    private final List<ITooltipProvider> tooltipProviders = new ArrayList<>();

    private Button btnPrevPage;
    private Button btnNextPage;
    private int pageOffset;
    private boolean isLocationHeaderHovered;
    private int buttonsPerPage;

    private static final int headerHeight = 40;
    private static final int footerHeight = 25;
    private static final int entryHeight = 25;

    public WaystoneSelectionScreenBase(WaystoneSelectionMenu container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
        waystones = container.getWaystones();
        imageWidth = 270;
        imageHeight = 200;
    }

    @Override
    public void init() {
        final int maxContentHeight = (int) (height * 0.6f);
        final int maxButtonsPerPage = (maxContentHeight - headerHeight - footerHeight) / entryHeight;
        buttonsPerPage = Math.max(4, Math.min(maxButtonsPerPage, waystones.size()));
        final int contentHeight = headerHeight + buttonsPerPage * entryHeight + footerHeight;

        // Leave no space for JEI!
        imageWidth = width;
        imageHeight = contentHeight;

        super.init();

        tooltipProviders.clear();
        btnPrevPage = new Button(width / 2 - 100, height / 2 + 40, 95, 20, new TranslatableComponent("gui.waystones.waystone_selection.previous_page"), button -> {
            pageOffset = Screen.hasShiftDown() ? 0 : pageOffset - 1;
            updateList();
        });
        addRenderableWidget(btnPrevPage);

        btnNextPage = new Button(width / 2 + 5, height / 2 + 40, 95, 20, new TranslatableComponent("gui.waystones.waystone_selection.next_page"), button -> {
            pageOffset = Screen.hasShiftDown() ? (waystones.size() - 1) / buttonsPerPage : pageOffset + 1;
            updateList();
        });
        addRenderableWidget(btnNextPage);

        updateList();
    }

    @Override
    protected <T extends GuiEventListener & Widget & NarratableEntry> T addRenderableWidget(T widget) {
        if (widget instanceof ITooltipProvider) {
            tooltipProviders.add((ITooltipProvider) widget);
        }
        return super.addRenderableWidget(widget);
    }

    private void updateList() {
        int headerY = 0;

        btnPrevPage.active = pageOffset > 0;
        btnNextPage.active = pageOffset < (waystones.size() - 1) / buttonsPerPage;

        tooltipProviders.clear();

        Predicate<Object> removePredicate = button -> button instanceof WaystoneButton || button instanceof SortWaystoneButton || button instanceof RemoveWaystoneButton;
        ((ScreenAccessor) this).getChildren().removeIf(removePredicate);
        ((ScreenAccessor) this).getNarratables().removeIf(removePredicate);
        ((ScreenAccessor) this).getRenderables().removeIf(removePredicate);

        int y = topPos + headerHeight + headerY;
        for (int i = 0; i < buttonsPerPage; i++) {
            int entryIndex = pageOffset * buttonsPerPage + i;
            if (entryIndex >= 0 && entryIndex < waystones.size()) {
                IWaystone waystone = waystones.get(entryIndex);

                addRenderableWidget(createWaystoneButton(y, waystone));

                if (allowSorting()) {
                    SortWaystoneButton sortUpButton = new SortWaystoneButton(width / 2 + 108, y + 2, -1, y, 20, it -> sortWaystone(entryIndex, -1));
                    if (entryIndex == 0) {
                        sortUpButton.active = false;
                    }
                    addRenderableWidget(sortUpButton);

                    SortWaystoneButton sortDownButton = new SortWaystoneButton(width / 2 + 108, y + 13, 1, y, 20, it -> sortWaystone(entryIndex, 1));
                    if (entryIndex == waystones.size() - 1) {
                        sortDownButton.active = false;
                    }
                    addRenderableWidget(sortDownButton);
                }

                if (allowDeletion()) {
                    RemoveWaystoneButton removeButton = new RemoveWaystoneButton(width / 2 + 122, y + 4, y, 20, waystone, button -> {
                        Player player = Minecraft.getInstance().player;
                        PlayerWaystoneManager.deactivateWaystone(Objects.requireNonNull(player), waystone);
                        Balm.getNetworking().sendToServer(new RemoveWaystoneMessage(waystone.getWaystoneUid()));
                        updateList();
                    });
                    // Only show the remove button for non-global waystones, or if the player is in creative mode
                    if (!waystone.isGlobal() || Minecraft.getInstance().player.getAbilities().instabuild) {
                        addRenderableWidget(removeButton);
                    }
                }

                y += 22;
            }
        }

        btnPrevPage.y = topPos + headerY + headerHeight + buttonsPerPage * 22 + (waystones.size() > 0 ? 10 : 0);
        btnNextPage.y = topPos + headerY + headerHeight + buttonsPerPage * 22 + (waystones.size() > 0 ? 10 : 0);
    }

    private WaystoneButton createWaystoneButton(int y, final IWaystone waystone) {
        IWaystone waystoneFrom = menu.getWaystoneFrom();
        Player player = Minecraft.getInstance().player;
        int xpLevelCost = Math.round(PlayerWaystoneManager.getExperienceLevelCost(Objects.requireNonNull(player), waystone, menu.getWarpMode(), waystoneFrom));
        WaystoneButton btnWaystone = new WaystoneButton(width / 2 - 100, y, waystone, xpLevelCost, button -> onWaystoneSelected(waystone));
        if (waystoneFrom != null && waystone.getWaystoneUid().equals(waystoneFrom.getWaystoneUid())) {
            btnWaystone.active = false;
        }
        return btnWaystone;
    }

    protected void onWaystoneSelected(IWaystone waystone) {
        Balm.getNetworking().sendToServer(new SelectWaystoneMessage(waystone.getWaystoneUid()));
    }

    private void sortWaystone(int index, int sortDir) {
        if (index < 0 || index >= waystones.size()) {
            return;
        }

        int otherIndex;
        if (Screen.hasShiftDown()) {
            otherIndex = sortDir == -1 ? -1 : waystones.size();
        } else {
            otherIndex = index + sortDir;
            if (otherIndex < 0 || otherIndex >= waystones.size()) {
                return;
            }
        }

        PlayerWaystoneManager.swapWaystoneSorting(Minecraft.getInstance().player, index, otherIndex);
        Balm.getNetworking().sendToServer(new SortWaystoneMessage(index, otherIndex));
        updateList();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (isLocationHeaderHovered && menu.getWaystoneFrom() != null) {
            Balm.getNetworking().sendToServer(new RequestEditWaystoneMessage(menu.getWaystoneFrom().getWaystoneUid()));
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    protected boolean allowSorting() {
        return true;
    }

    protected boolean allowDeletion() {
        return true;
    }
}
