package dev.kir.netherchest.inventory;

import dev.kir.netherchest.NetherChest;
import dev.kir.netherchest.block.entity.NetherChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class NetherChestInventory extends SimpleInventory implements SidedInventory {
    private static final int SIZE = 27;
    private static final int[] AVAILABLE_SLOTS = new int[SIZE];
    private static final int[] EMPTY_SLOTS = new int[0];

    private int comparatorOutput;
    private final ConcurrentMap<PlayerEntity, NetherChestBlockEntity> activeBlockEntities = new ConcurrentHashMap<>();

    public NetherChestInventory() {
        super(SIZE);
    }

    public void setActiveBlockEntity(PlayerEntity player, NetherChestBlockEntity blockEntity) {
        this.activeBlockEntities.put(player, blockEntity);
    }

    @Override
    public void readNbtList(NbtList tags) {
        for (int i = 0; i < this.size(); ++i) {
            this.setStack(i, ItemStack.EMPTY);
        }

        for (int i = 0; i < tags.size(); ++i) {
            NbtCompound compoundTag = tags.getCompound(i);
            int slot = compoundTag.getByte("Slot");
            if (slot < this.size()) {
                this.setStack(slot, ItemStack.fromNbt(compoundTag));
            }
        }

        this.markDirty();
    }

    @Override
    public NbtList toNbtList() {
        NbtList listTag = new NbtList();

        for (int i = 0; i < this.size(); ++i) {
            ItemStack itemStack = this.getStack(i);
            if (!itemStack.isEmpty()) {
                NbtCompound compoundTag = new NbtCompound();
                compoundTag.putByte("Slot", (byte)i);
                itemStack.writeNbt(compoundTag);
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
            netherChestBlockEntity.onOpen(player);
        }

        super.onOpen(player);
    }

    @Override
    public void onClose(PlayerEntity player) {
        NetherChestBlockEntity netherChestBlockEntity = this.activeBlockEntities.getOrDefault(player, null);
        if (netherChestBlockEntity != null) {
            netherChestBlockEntity.onClose(player);
        }

        super.onClose(player);
        this.activeBlockEntities.remove(player);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        this.comparatorOutput = ScreenHandler.calculateComparatorOutput(this);
    }

    public int getComparatorOutput() {
        return this.comparatorOutput;
    }

    public boolean isActiveBlockEntity(PlayerEntity player, NetherChestBlockEntity blockEntity) {
        return activeBlockEntities.getOrDefault(player, null) == blockEntity;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        return NetherChest.getConfig().allowHoppers ? AVAILABLE_SLOTS : EMPTY_SLOTS;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return NetherChest.getConfig().allowHoppers;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return NetherChest.getConfig().allowHoppers;
    }

    static {
        for (int i = 0; i < SIZE; ++i) {
            AVAILABLE_SLOTS[i] = i;
        }
    }
}