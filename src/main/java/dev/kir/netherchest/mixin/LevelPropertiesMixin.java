package dev.kir.netherchest.mixin;

import com.mojang.serialization.Dynamic;
import com.mojang.serialization.Lifecycle;
import dev.kir.netherchest.NetherChest;
import dev.kir.netherchest.inventory.NetherChestInventory;
import dev.kir.netherchest.inventory.NetherChestInventoryHolder;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.level.LevelInfo;
import net.minecraft.world.level.LevelProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
    private static <T> void onReadProperties(Dynamic<T> dynamic, LevelInfo info, LevelProperties.SpecialProperty specialProperty, GeneratorOptions generatorOptions, Lifecycle lifecycle, CallbackInfoReturnable<LevelProperties> cir) {
        LevelProperties properties = cir.getReturnValue();
        Dynamic<T> dynamicItems = dynamic.get(NETHER_CHEST_TAG_NAME).orElseEmptyList();
        NetherChestInventory inventory = NetherChestInventory.CODEC.decode(dynamicItems).getOrThrow(false, NetherChest.LOGGER::error).getFirst();

        ((NetherChestInventoryHolder)properties).setNetherChestInventory(inventory);
    }

    @Inject(method = "updateProperties", at = @At("RETURN"))
    private void onUpdateProperties(DynamicRegistryManager registryManager, NbtCompound levelNbt, NbtCompound playerNbt, CallbackInfo ci) {
        NetherChestInventory inventory = this.getNetherChestInventory();
        NbtList list = (NbtList)NetherChestInventory.CODEC.encodeStart(NbtOps.INSTANCE, inventory).getOrThrow(false, NetherChest.LOGGER::error);
        levelNbt.put(NETHER_CHEST_TAG_NAME, list);
    }
}