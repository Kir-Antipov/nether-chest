package dev.kir.netherchest.mixin;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import dev.kir.netherchest.NetherChest;
import dev.kir.netherchest.inventory.NetherChestInventory;
import dev.kir.netherchest.inventory.NetherChestInventoryHolder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import net.minecraft.world.level.storage.SaveVersionInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LevelProperties.class)
public abstract class LevelPropertiesMixin implements NetherChestInventoryHolder {
    private static final String NETHER_CHEST_TAG_NAME = "NetherItems";

    private NetherChestInventory netherChestInventory;

    @Override
    public NetherChestInventory getNetherChestInventory() {
        if (this.netherChestInventory == null) {
            this.netherChestInventory = new NetherChestInventory();
        }

        return this.netherChestInventory;
    }

    @Override
    public void setNetherChestInventory(NetherChestInventory netherChestInventory) {
        this.netherChestInventory = netherChestInventory;
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "readProperties", at = @At("RETURN"))
    private static void onReadProperties(Dynamic<NbtElement> dynamic, DataFixer dataFixer, int dataVersion, NbtCompound playerData, LevelInfo levelInfo, SaveVersionInfo saveVersionInfo, LevelProperties.SpecialProperty specialProperty, GeneratorOptions generatorOptions, Lifecycle lifecycle, CallbackInfoReturnable<LevelProperties> cir) {
        NetherChestInventory inventory;
        Optional<Dynamic<NbtElement>> optionalNetherItemsTag = dynamic.get(NETHER_CHEST_TAG_NAME).result();

        if (optionalNetherItemsTag.isPresent()) {
            NbtElement netherItemsTag = optionalNetherItemsTag.get().getValue();
            inventory = NetherChestInventory.CODEC.decode(NbtOps.INSTANCE, netherItemsTag).getOrThrow(false, NetherChest.LOGGER::error).getFirst();
        } else {
            inventory = new NetherChestInventory();
        }

        LevelProperties properties = cir.getReturnValue();
        ((NetherChestInventoryHolder)properties).setNetherChestInventory(inventory);
    }

    @Inject(method = "updateProperties", at = @At("RETURN"))
    private void onUpdateProperties(DynamicRegistryManager registryManager, NbtCompound levelNbt, NbtCompound playerNbt, CallbackInfo ci) {
        NetherChestInventory inventory = this.getNetherChestInventory();
        NbtList list = (NbtList)NetherChestInventory.CODEC.encodeStart(NbtOps.INSTANCE, inventory).getOrThrow(false, NetherChest.LOGGER::error);
        levelNbt.put(NETHER_CHEST_TAG_NAME, list);
    }
}