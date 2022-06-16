package nova.committee.atom.ess.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.common.menu.TrashcanMenu;
import nova.committee.atom.ess.common.net.ClearTrashPacket;
import nova.committee.atom.ess.init.handler.PacketHandler;
import nova.committee.atom.ess.util.text.I18Util;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/8 18:58
 * Version: 1.0
 */
public class TrashcanScreen extends AbstractContainerScreen<TrashcanMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Static.MOD_ID, "textures/gui/trashcan.png");


    public TrashcanScreen(TrashcanMenu p_97741_, Inventory p_97742_, Component p_97743_) {
        super(p_97741_, p_97742_, p_97743_);
        this.imageWidth = 175;
        this.imageHeight = 221;
        this.inventoryLabelY = 128;

    }

    @Override
    protected void renderBg(@NotNull PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }


    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new ExtendedButton((this.width - this.imageWidth) / 2 + 135, (this.height - this.imageHeight) / 2 + 17, 34, 34,
                I18Util.getContainerNameTextFromI18n(false, false, false,
                        I18Util.getTranslationKey("text", "clear")), button -> PacketHandler.INSTANCE.sendToServer(new ClearTrashPacket())));
    }

    @Override
    protected void renderLabels(@NotNull PoseStack p_97808_, int p_97809_, int p_97810_) {
        super.renderLabels(p_97808_, p_97809_, p_97810_);
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderLabels(matrixStack, mouseX, mouseY);
        drawString(matrixStack, this.font, I18Util.getContainerNameTextFromI18n(false, false, false,
                        I18Util.getTranslationKey("text", "trashcanTitle"), this.menu.getTrashcan().getNextCleanSeconds()),
                (this.width - this.imageWidth) / 2 + 65, (this.height - this.imageHeight) / 2 + this.titleLabelY, 0xfbfb54);
    }
}
