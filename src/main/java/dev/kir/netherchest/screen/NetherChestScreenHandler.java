package dev.kir.netherchest.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class NetherChestScreenHandler extends ScreenHandler {
    private static final int ROWS = 3;
    private static final int COLUMNS = 9;
    private static final int INVENTORY_SIZE = ROWS * COLUMNS + 1;

    private final Inventory inventory;

    public NetherChestScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(INVENTORY_SIZE));
    }

    public NetherChestScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(NetherChestScreenHandlerTypes.NETHER_CHEST, syncId);
        checkSize(inventory, INVENTORY_SIZE);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        for (int i = 0; i < ROWS; ++i) {
            for(int j = 0; j < COLUMNS; ++j) {
                this.addSlot(new Slot(inventory, j + i * COLUMNS, 8 + j * 18, 18 + i * 18));
            }
        }
        if (isClient(playerInventory)) {
            this.addSlot(new Slot(inventory, INVENTORY_SIZE - 1, 188, 18));
        } else {
            this.addSlot(new NetherChestChannelSlot(inventory, INVENTORY_SIZE - 1, 188, 18));
        }

        for (int i = 0; i < ROWS; ++i) {
            for(int j = 0; j < COLUMNS; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * COLUMNS + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int index) {
        ItemStack transferredItemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            transferredItemStack = slotStack.copy();
            if (index < this.inventory.size()) {
                if (!this.insertItem(slotStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(slotStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return transferredItemStack;
    }

    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }

    private static boolean isClient(PlayerInventory playerInventory) {
        return (
            playerInventory.player == null ||
            playerInventory.player.getWorld() == null ||
            playerInventory.player.getWorld().isClient
        );
    }
}
