package net.blay09.mods.waystones.block.entity;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.container.ImplementedContainer;
import net.blay09.mods.balm.api.menu.BalmMenuProvider;
import net.blay09.mods.waystones.api.IAttunementItem;
import net.blay09.mods.waystones.api.IMutableWaystone;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.block.WarpPlateBlock;
import net.blay09.mods.waystones.menu.WarpPlateContainer;
import net.blay09.mods.waystones.core.*;
import net.blay09.mods.waystones.item.AttunedShardItem;
import net.blay09.mods.waystones.item.ModItems;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerationMode;
import net.blay09.mods.waystones.worldgen.namegen.NameGenerator;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class WarpPlateBlockEntity extends WaystoneBlockEntityBase implements ImplementedContainer {

    private final WeakHashMap<Entity, Integer> ticksPassedPerEntity = new WeakHashMap<>();

    private final Random random = new Random();
    private final ContainerData dataAccess;

    private final NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);

    private int attunementTicks;
    private boolean readyForAttunement;
    private boolean completedFirstAttunement;
    private int lastAttunementSlot;

    public WarpPlateBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntities.warpPlate.get(), blockPos, blockState);
        dataAccess = new ContainerData() {
            @Override
            public int get(int i) {
                return attunementTicks;
            }

            @Override
            public void set(int i, int j) {
                attunementTicks = j;
            }

            @Override
            public int getCount() {
                return 1;
            }
        };
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public ItemStack removeItem(int slot, int count) {
        if (!completedFirstAttunement) {
            return ItemStack.EMPTY;
        }
        return ImplementedContainer.super.removeItem(slot, count);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (!completedFirstAttunement) {
            return ItemStack.EMPTY;
        }

        return ImplementedContainer.super.removeItemNoUpdate(slot);
    }

    @Override
    public void initializeFromExisting(ServerLevelAccessor world, Waystone existingWaystone, ItemStack itemStack) {
        super.initializeFromExisting(world, existingWaystone, itemStack);

        CompoundTag tag = itemStack.getTag();
        completedFirstAttunement = tag != null && tag.getBoolean("CompletedFirstAttunement");

        if (!completedFirstAttunement) {
            initializeInventory();
        }
    }

    @Override
    public void initializeWaystone(ServerLevelAccessor world, @Nullable LivingEntity player, boolean wasGenerated) {
        super.initializeWaystone(world, player, wasGenerated);

        // Warp Plates generate a name on placement always
        IWaystone waystone = getWaystone();
        if (waystone instanceof IMutableWaystone) {
            String name = NameGenerator.get(world.getServer()).getName(waystone, world.getRandom(), NameGenerationMode.RANDOM_ONLY);
            ((IMutableWaystone) waystone).setName(name);
        }

        WaystoneSyncManager.sendWaystoneUpdateToAll(world.getServer(), waystone);

        initializeInventory();
    }

    private void initializeInventory() {
        setItem(0, new ItemStack(Items.FLINT));
        setItem(1, new ItemStack(ModItems.warpDust));
        setItem(2, new ItemStack(ModItems.warpDust));
        setItem(3, new ItemStack(ModItems.warpDust));
        setItem(4, new ItemStack(ModItems.warpDust));
    }

    @Override
    protected ResourceLocation getWaystoneType() {
        return WaystoneTypes.WARP_PLATE;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        super.save(compound);

        ContainerHelper.saveAllItems(compound, items);
        compound.putBoolean("ReadyForAttunement", readyForAttunement);
        compound.putBoolean("CompletedFirstAttunement", completedFirstAttunement);
        compound.putInt("LastAttunementSlot", lastAttunementSlot);

        return compound;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);

        ContainerHelper.loadAllItems(compound, items);
        readyForAttunement = compound.getBoolean("ReadyForAttunement");
        completedFirstAttunement = compound.getBoolean("CompletedFirstAttunement");
        lastAttunementSlot = compound.getInt("LastAttunementSlot");
    }

    @Override
    public BalmMenuProvider getMenuProvider() {
        return new BalmMenuProvider() {
            @Override
            public Component getDisplayName() {
                return new TranslatableComponent("container.waystones.warp_plate");
            }

            @Override
            public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player player) {
                return new WarpPlateContainer(i, WarpPlateBlockEntity.this, dataAccess, playerInventory);
            }

            @Override
            public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
                buf.writeBlockPos(worldPosition);
            }
        };
    }

    @Override
    public MenuProvider getSettingsMenuProvider() {
        return null;
    }

    public void onEntityCollision(Entity entity) {
        Integer ticksPassed = ticksPassedPerEntity.putIfAbsent(entity, 0);
        if (ticksPassed == null || ticksPassed != -1) {
            level.setBlock(worldPosition, getBlockState().setValue(WarpPlateBlock.ACTIVE, true), 3);
        }
    }

    private boolean isEntityOnWarpPlate(Entity entity) {
        return entity.getX() >= worldPosition.getX() && entity.getX() < worldPosition.getX() + 1
                && entity.getY() >= worldPosition.getY() && entity.getY() < worldPosition.getY() + 1
                && entity.getZ() >= worldPosition.getZ() && entity.getZ() < worldPosition.getZ() + 1;
    }

    public void serverTick() {
        if (isReadyForAttunement()) {
            attunementTicks++;

            if (attunementTicks >= getMaxAttunementTicks()) {
                attunementTicks = 0;
                ItemStack attunedShard = new ItemStack(ModItems.attunedShard);
                AttunedShardItem.setWaystoneAttunedTo(attunedShard, getWaystone());
                setItem(0, attunedShard);
                for (int i = 1; i <= 4; i++) {
                    setItem(i, ItemStack.EMPTY);
                }
                completedFirstAttunement = true;
            }
        } else {
            attunementTicks = 0;
        }

        if (getBlockState().getValue(WarpPlateBlock.ACTIVE)) {
            AABB boundsAbove = new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + 1, worldPosition.getZ() + 1);
            List<Entity> entities = level.getEntities((Entity) null, boundsAbove, EntitySelector.ENTITY_STILL_ALIVE);
            if (entities.isEmpty()) {
                level.setBlock(worldPosition, getBlockState().setValue(WarpPlateBlock.ACTIVE, false), 3);
                ticksPassedPerEntity.clear();
            }
        }

        Iterator<Map.Entry<Entity, Integer>> iterator = ticksPassedPerEntity.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Entity, Integer> entry = iterator.next();
            Entity entity = entry.getKey();
            Integer ticksPassed = entry.getValue();
            if (!entity.isAlive() || !isEntityOnWarpPlate(entity)) {
                iterator.remove();
            } else if (ticksPassed > 20) {
                IWaystone targetWaystone = getTargetWaystone();
                if (targetWaystone != null && targetWaystone.isValid()) {
                    teleportToWarpPlate(entity, targetWaystone);
                }

                if (entity instanceof Player) {
                    if (targetWaystone == null) {
                        TranslatableComponent chatComponent = new TranslatableComponent("chat.waystones.warp_plate_has_no_target");
                        chatComponent.withStyle(ChatFormatting.DARK_RED);
                        ((Player) entity).displayClientMessage(chatComponent, true);
                    } else if (!targetWaystone.isValid()) {
                        TranslatableComponent chatComponent = new TranslatableComponent("chat.waystones.warp_plate_has_invalid_target");
                        chatComponent.withStyle(ChatFormatting.DARK_RED);
                        ((Player) entity).displayClientMessage(chatComponent, true);
                    }
                }

                iterator.remove();
            } else if (ticksPassed != -1) {
                entry.setValue(ticksPassed + 1);
            }
        }

        if (getItem(0).getItem() != Items.FLINT) {
            completedFirstAttunement = true;
        }
    }

    private void teleportToWarpPlate(Entity entity, IWaystone targetWaystone) {
        if (PlayerWaystoneManager.tryTeleportToWaystone(entity, targetWaystone, WarpMode.WARP_PLATE, getWaystone())) {
            int fireSeconds = 0;
            int poisonSeconds = 0;
            int blindSeconds = 0;
            int featherFallSeconds = 0;
            int fireResistanceSeconds = 0;
            float potency = 1;
            List<ItemStack> curativeItems = new ArrayList<>();
            for (int i = 0; i < getContainerSize(); i++) {
                ItemStack itemStack = getItem(i);
                if (itemStack.getItem() == Items.BLAZE_POWDER) {
                    fireSeconds += 2;
                } else if (itemStack.getItem() == Items.POISONOUS_POTATO) {
                    poisonSeconds += 2;
                } else if (itemStack.getItem() == Items.INK_SAC) {
                    blindSeconds += 2;
                } else if (itemStack.getItem() == Items.MILK_BUCKET || itemStack.getItem() == Items.HONEY_BLOCK) {
                    curativeItems.add(itemStack);
                } else if (itemStack.getItem() == Items.DIAMOND) {
                    potency += 1f;
                } else if (itemStack.getItem() == Items.FEATHER) {
                    featherFallSeconds += 2;
                } else if (itemStack.getItem() == Items.MAGMA_CREAM) {
                    fireResistanceSeconds += 2;
                }
            }
            if (entity instanceof LivingEntity) {
                if (fireSeconds > 0) {
                    entity.setSecondsOnFire((int) (fireSeconds * potency));
                }
                if (poisonSeconds > 0) {
                    ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.POISON, (int) (poisonSeconds * potency * 20)));
                }
                if (blindSeconds > 0) {
                    ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, (int) (blindSeconds * potency * 20)));
                }
                if (featherFallSeconds > 0) {
                    ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, (int) (featherFallSeconds * potency * 20)));
                }
                if (fireResistanceSeconds > 0) {
                    ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, (int) (fireResistanceSeconds * potency * 20)));
                }
                for (ItemStack curativeItem : curativeItems) {
                    Balm.getHooks().curePotionEffects((LivingEntity) entity, curativeItem);
                }
            }
        }
    }

    private boolean isReadyForAttunement() {
        return readyForAttunement
                && getItem(0).getItem() == Items.FLINT
                && getItem(1).getItem() == ModItems.warpDust
                && getItem(2).getItem() == ModItems.warpDust
                && getItem(3).getItem() == ModItems.warpDust
                && getItem(4).getItem() == ModItems.warpDust;
    }

    @Nullable
    public IWaystone getTargetWaystone() {
        boolean useRoundRobin = false;
        List<ItemStack> attunedShards = new ArrayList<>();
        for (int i = 0; i < getContainerSize(); i++) {
            ItemStack itemStack = getItem(i);
            if (itemStack.getItem() instanceof IAttunementItem) {
                IWaystone waystoneAttunedTo = ((IAttunementItem) itemStack.getItem()).getWaystoneAttunedTo(level.getServer(), itemStack);
                if (waystoneAttunedTo != null && !waystoneAttunedTo.getWaystoneUid().equals(getWaystone().getWaystoneUid())) {
                    attunedShards.add(itemStack);
                }
            } else if (itemStack.getItem() == Items.QUARTZ) {
                useRoundRobin = true;
            }
        }

        if (!attunedShards.isEmpty()) {
            lastAttunementSlot = (lastAttunementSlot + 1) % attunedShards.size();
            ItemStack itemStack = useRoundRobin ? attunedShards.get(lastAttunementSlot) : attunedShards.get(random.nextInt(attunedShards.size()));
            return ((IAttunementItem) itemStack.getItem()).getWaystoneAttunedTo(level.getServer(), itemStack);
        }

        return null;
    }

    public int getMaxAttunementTicks() {
        return 30;
    }

    public void markReadyForAttunement() {
        readyForAttunement = true;
    }

    public void markEntityForCooldown(Entity entity) {
        ticksPassedPerEntity.put(entity, -1);
    }

    public boolean isCompletedFirstAttunement() {
        return completedFirstAttunement;
    }

    public ContainerData getContainerData() {
        return dataAccess;
    }
}
