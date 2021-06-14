package me.kirantipov.mods.netherchest.mixin;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import me.kirantipov.mods.netherchest.inventory.NetherChestInventory;
import me.kirantipov.mods.netherchest.inventory.NetherChestInventoryHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.registry.DynamicRegistryManager;
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
public class MixinLevelProperties implements NetherChestInventoryHolder {
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

    @Inject(method = "readProperties", at = @At("RETURN"))
    private static void onReadProperties(Dynamic<Tag> dynamic, DataFixer dataFixer, int dataVersion, CompoundTag playerData, LevelInfo levelInfo, SaveVersionInfo saveVersionInfo, GeneratorOptions generatorOptions, Lifecycle lifecycle, CallbackInfoReturnable<LevelProperties> cir) {
        NetherChestInventory inventory = new NetherChestInventory();
        Optional<Dynamic<Tag>> optionalNetherItemsTag = dynamic.get(NETHER_CHEST_TAG_NAME).result();
        if (optionalNetherItemsTag.isPresent()) {
            Tag netherItemsTag = optionalNetherItemsTag.get().getValue();
            if (netherItemsTag instanceof ListTag) {
                inventory.readTags((ListTag)netherItemsTag);
            }
        }

        LevelProperties properties = cir.getReturnValue();
        ((NetherChestInventoryHolder)properties).setNetherChestInventory(inventory);
    }

    @Inject(method = "updateProperties", at = @At("RETURN"))
    private void onUpdateProperties(DynamicRegistryManager dynamicRegistryManager, CompoundTag compoundTag, CompoundTag compoundTag2, CallbackInfo ci) {
        NetherChestInventory netherChestInventory = this.getNetherChestInventory();
        compoundTag.put(NETHER_CHEST_TAG_NAME, netherChestInventory.getTags());
    }
}