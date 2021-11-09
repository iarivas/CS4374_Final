package net.blay09.mods.waystones.item;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.waystones.api.IFOVOnUse;
import net.blay09.mods.waystones.api.IResetUseOnDamage;
import net.blay09.mods.waystones.compat.Compat;
import net.blay09.mods.waystones.config.WaystonesConfig;
import net.blay09.mods.waystones.core.WarpMode;
import net.blay09.mods.waystones.menu.WaystoneSelectionMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class WarpScrollItem extends Item implements IResetUseOnDamage, IFOVOnUse {

    private static final BalmMenuProvider containerProvider = new BalmMenuProvider() {
        @Override
        public Component getDisplayName() {
            return new TranslatableComponent("container.waystones.waystone_selection");
        }

        @Override
        public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
            return WaystoneSelectionMenu.createWaystoneSelection(i, playerEntity, WarpMode.WARP_SCROLL, null);
        }

        @Override
        public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
            buf.writeByte(WarpMode.WARP_SCROLL.ordinal());
        }
    };

    public WarpScrollItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return WaystonesConfig.getActive().scrollUseTime();
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        if (Compat.isVivecraftInstalled) {
            return UseAnim.NONE;
        }

        return UseAnim.BOW;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack itemStack, Level world, LivingEntity entity) {
        if (!world.isClientSide && entity instanceof ServerPlayer) {
            Balm.getNetworking().openGui(((ServerPlayer) entity), containerProvider);
        }
        return itemStack;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!player.isUsingItem() && !world.isClientSide) {
            world.playSound(null, player, SoundEvents.PORTAL_TRIGGER, SoundSource.PLAYERS, 0.1f, 2f);
        }
        if (Compat.isVivecraftInstalled) {
            finishUsingItem(itemStack, world, player);
        } else {
            player.startUsingItem(hand);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemStack);
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }


}
