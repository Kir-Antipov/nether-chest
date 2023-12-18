package dev.kir.netherchest.mixin;

import dev.kir.netherchest.NetherChest;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Identifier.class)
public class CraigslistIdDataFixerMixin {
    @ModifyVariable(method = "<init>(Ljava/lang/String;Ljava/lang/String;Lnet/minecraft/util/Identifier$ExtraData;)V", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static String fixOldNamespace(String namespace) {
        final String OLD_NAMESPACE = "netherchest";
        final String NEW_NAMESPACE = NetherChest.MOD_ID;

        if (OLD_NAMESPACE.equals(namespace)) {
            return NEW_NAMESPACE;
        }

        return namespace;
    }
}