package dev.kir.netherchest.mixin;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import dev.kir.netherchest.inventory.NetherChestInventory;
import dev.kir.netherchest.inventory.NetherChestInventoryHolder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
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
public class LevelPropertiesMixin implements NetherChestInventoryHolder {
    private static final String NETHER_CHEST_TAG_NAME = "NetherItems";

    private NetherChestInventory netherChestInventory;

    public NetherChestInventory getNetherChestInventory() {
        if (this.netherChestInventory == null) {
            this.netherChestInventory = new NetherChestInventory();
        }

        return this.netherChestInventory;
    }

    public void setNetherChestInventory(NetherChestInventory netherChestInventory) {
        this.netherChestInventory = netherChestInventory;
    }

    @SuppressWarnings("deprecation")
    @Inject(method = "readProperties", at = @At("RETURN"))
    private static void onReadProperties(Dynamic<NbtElement> dynamic, DataFixer dataFixer, int dataVersion, NbtCompound playerData, LevelInfo levelInfo, SaveVersionInfo saveVersionInfo, LevelProperties.SpecialProperty specialProperty, GeneratorOptions generatorOptions, Lifecycle lifecycle, CallbackInfoReturnable<LevelProperties> cir) {
        NetherChestInventory inventory = new NetherChestInventory();
        Optional<Dynamic<NbtElement>> optionalNetherItemsTag = dynamic.get(NETHER_CHEST_TAG_NAME).result();
        if (optionalNetherItemsTag.isPresent()) {
            NbtElement netherItemsTag = optionalNetherItemsTag.get().getValue();
            if (netherItemsTag instanceof NbtList) {
                inventory.readNbtList((NbtList)netherItemsTag);
            }
        }

        LevelProperties properties = cir.getReturnValue();
        ((NetherChestInventoryHolder)properties).setNetherChestInventory(inventory);
    }

    @Inject(method = "updateProperties", at = @At("RETURN"))
    private void onUpdateProperties(DynamicRegistryManager registryManager, NbtCompound levelNbt, NbtCompound playerNbt, CallbackInfo ci) {
        NetherChestInventory netherChestInventory = this.getNetherChestInventory();
        levelNbt.put(NETHER_CHEST_TAG_NAME, netherChestInventory.toNbtList());
    }
}