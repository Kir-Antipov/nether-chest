package dev.kir.netherchest.screen.client;

import dev.kir.netherchest.NetherChest;
import dev.kir.netherchest.screen.NetherChestScreenHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.anti_ad.mc.ipn.api.IPNIgnore;

@IPNIgnore
@Environment(EnvType.CLIENT)
public class NetherChestScreen extends HandledScreen<NetherChestScreenHandler> implements ScreenHandlerProvider<NetherChestScreenHandler> {
    private static final Identifier TEXTURE = NetherChest.locate("textures/gui/container/nether_chest.png");

    public NetherChestScreen(NetherChestScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 212;
        this.backgroundHeight = 168;
        this.playerInventoryTitleY = this.backgroundHeight - 95;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }
}
