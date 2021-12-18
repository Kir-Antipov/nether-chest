package dev.kir.netherchest.mixin;

import dev.kir.netherchest.server.ServerShutdownListener;
import dev.kir.netherchest.server.ServerShutdownListeners;
import dev.kir.netherchest.server.ServerStartListener;
import dev.kir.netherchest.server.ServerStartListeners;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    @Inject(method = "loadWorld", at = @At("HEAD"))
    private void onServerStart(CallbackInfo ci) {
        for (ServerStartListener listener : ServerStartListeners.getListeners()) {
            listener.onServerStart((MinecraftServer)(Object)this);
        }
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void onServerShutdown(CallbackInfo ci) {
        for (ServerShutdownListener listener : ServerShutdownListeners.getListeners()) {
            listener.onServerShutdown((MinecraftServer)(Object)this);
        }
    }
}