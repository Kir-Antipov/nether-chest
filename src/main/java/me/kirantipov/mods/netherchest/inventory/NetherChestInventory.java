package me.kirantipov.mods.netherchest.inventory;

import me.kirantipov.mods.netherchest.NetherChest;
import me.kirantipov.mods.netherchest.block.entity.NetherChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NetherChestInventory extends SimpleInventory implements SidedInventory {
    private static final int SIZE = 27;
    private static final int[] AVAILABLE_SLOTS = new int[SIZE];
    private static final int[] EMPTY_SLOTS = new int[0];

    private final ConcurrentMap<PlayerEntity, NetherChestBlockEntity> activeBlockEntities;


    static {
        for (int i = 0; i < SIZE; ++i) {
            AVAILABLE_SLOTS[i] = i;
        }
    }

    public NetherChestInventory() {
        super(SIZE);
        this.activeBlockEntities = new ConcurrentHashMap<>();
    }

    public void setActiveBlockEntity(PlayerEntity player, NetherChestBlockEntity blockEntity) {
        this.activeBlockEntities.put(player, blockEntity);
    }

    @Override
    public void readTags(ListTag tags) {
        for(int i = 0; i < this.size(); ++i) {
            this.setStack(i, ItemStack.EMPTY);
        }

        for(int i = 0; i < tags.size(); ++i) {
            CompoundTag compoundTag = tags.getCompound(i);
            int slot = compoundTag.getByte("Slot");
            if (slot < this.size()) {
                this.setStack(slot, ItemStack.fromTag(compoundTag));
            }
        }
    }

    @Override
    public ListTag getTags() {
        ListTag listTag = new ListTag();

        for(int i = 0; i < this.size(); ++i) {
            ItemStack itemStack = this.getStack(i);
            if (!itemStack.isEmpty()) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putByte("Slot", (byte)i);
                itemStack.toTag(compoundTag);
                listTag.add(compoundTag);
            }
        }

        return listTag;
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        NetherChestBlockEntity netherChestBlockEntity = this.activeBlockEntities.getOrDefault(player, null);
        if (netherChestBlockEntity == null) {
            return false;
        }

        return netherChestBlockEntity.canPlayerUse(player) && super.canPlayerUse(player);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        NetherChestBlockEntity netherChestBlockEntity = this.activeBlockEntities.getOrDefault(player, null);
        if (netherChestBlockEntity != null) {
            netherChestBlockEntity.onOpen();
        }

        super.onOpen(player);
    }

    @Override
    public void onClose(PlayerEntity player) {
        NetherChestBlockEntity netherChestBlockEntity = this.activeBlockEntities.getOrDefault(player, null);
        if (netherChestBlockEntity != null) {
            netherChestBlockEntity.onClose();
        }

        super.onClose(player);
        this.activeBlockEntities.remove(player);
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return NetherChest.getConfig().allowRedstoneIntegration ? AVAILABLE_SLOTS : EMPTY_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return NetherChest.getConfig().allowRedstoneIntegration;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return NetherChest.getConfig().allowRedstoneIntegration;
    }
}