package nova.committee.atom.ess.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import nova.committee.atom.ess.Static;
import nova.committee.atom.ess.common.menu.RewardMenu;
import nova.committee.atom.ess.common.slot.AccessRewardSlot;
import nova.committee.atom.ess.common.slot.RewardSlot;
import nova.committee.atom.ess.core.reward.ConfigRewards;
import nova.committee.atom.ess.init.registry.ModItems;
import org.jetbrains.annotations.NotNull;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/6/25 14:44
 * Version: 1.0
 */
public class RewardScreen extends AbstractContainerScreen<RewardMenu> {


    private ResourceLocation texture =
            new ResourceLocation(Static.MOD_ID, "textures/gui/reward/reward_screen.png");

    private MutableComponent rewardScreenTitle;

    public RewardScreen(RewardMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    public void rendererTakeableRewardSlot(PoseStack poseStack, int x, int y) {
        RenderSystem.setShaderTexture(0, this.texture);
        poseStack.pushPose();
        this.blit(poseStack, x + 11, y - 4, 430, 17, 16, 16);
        poseStack.popPose();
    }

    public void renderRewardSlot(PoseStack poseStack, int x, int y, int blitOffset) {
        RenderSystem.disableDepthTest();
        RenderSystem.colorMask(true, true, true, false);
        fillGradient(poseStack, x, y, x + 16, y + 16, -2130706433, 0, blitOffset);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();
    }

    @Override
    public void init() {
        super.init();

        // Default stats
        this.imageWidth = 171;
        this.imageHeight = 247;

        // Set Title with already rewarded days.
        int rewardedDays = this.menu.getRewardedDays();
        rewardScreenTitle =
                new TranslatableComponent(Static.REWARD_PREFIX + "reward_screen", rewardedDays);

        // Set background according the number or days for the current month.
        switch (ConfigRewards.getDaysCurrentMonth()) {
            case 28 -> texture =
                    new ResourceLocation(Static.MOD_ID, "textures/container/reward_screen_28_days.png");
            case 29 -> texture =
                    new ResourceLocation(Static.MOD_ID, "textures/container/reward_screen_29_days.png");
            case 30 -> texture =
                    new ResourceLocation(Static.MOD_ID, "textures/container/reward_screen_30_days.png");
            case 31 -> texture =
                    new ResourceLocation(Static.MOD_ID, "textures/container/reward_screen_31_days.png");
            default -> texture = new ResourceLocation(Static.MOD_ID, "textures/container/reward_screen.png");
        }

        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.topPos = (this.height - this.imageHeight) / 2;
        this.inventoryLabelX = 6;
        this.inventoryLabelY = this.imageHeight - 90;
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int x, int y, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, x, y, partialTicks);

        // Additional styling for the different kind of slots and slot states.
        for (int k = 0; k < this.menu.slots.size(); ++k) {
            Slot slot = this.menu.slots.get(k);
            if (slot instanceof AccessRewardSlot && !slot.getItem().is(ModItems.rewardItem)) {
                rendererTakeableRewardSlot(poseStack, leftPos + slot.x, topPos + slot.y);
            } else if (slot instanceof RewardSlot) {
                renderRewardSlot(poseStack, leftPos + slot.x, topPos + slot.y, this.getBlitOffset());
            }
        }

        this.renderTooltip(poseStack, x, y);
    }

    @Override
    protected void renderLabels(@NotNull PoseStack poseStack, int x, int y) {
        this.font.draw(poseStack, rewardScreenTitle, this.titleLabelX, this.titleLabelY, 4210752);
        this.font.draw(poseStack, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY,
                4210752);
    }

    @Override
    protected void renderBg(@NotNull PoseStack poseStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, this.texture);

        // Main screen
        this.blit(poseStack, leftPos, topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
}
