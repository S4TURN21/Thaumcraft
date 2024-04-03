package thaumcraft.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.client.gui.plugins.GuiImageButton;
import thaumcraft.common.blockentities.crafting.BlockEntityResearchTable;
import thaumcraft.common.container.ContainerResearchTable;

import java.util.HashSet;
import java.util.Set;

public class GuiResearchTable extends AbstractContainerScreen<ContainerResearchTable> {
    private BlockEntityResearchTable table;
    Player player;
    ResourceLocation txBackground = new ResourceLocation("thaumcraft", "textures/gui/gui_research_table.png");
    ResourceLocation txBase = new ResourceLocation("thaumcraft", "textures/gui/gui_base.png");
    long nextCheck;
    int dummyInspirationStart;
    Set<String> selectedAids;
    GuiImageButton buttonCreate;

    public GuiResearchTable(ContainerResearchTable pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
        this.nextCheck = 0;
        this.dummyInspirationStart = 0;
        this.selectedAids = new HashSet<>();
        this.buttonCreate = new GuiImageButton(leftPos + 128, topPos + 22, 49, 11, Component.translatable("button.create.theory"), Button.NO_TOOLTIP, txBase, 37, 66, 51, 13, 0x88FFAA, (b) -> {});
        this.table = pMenu.blockEntity;
        this.imageWidth = 255;
        this.imageHeight = 255;
        this.player = pPlayerInventory.player;
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY) {

    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.renderBackground(pPoseStack);
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        this.renderTooltip(pPoseStack, pMouseX, pMouseY);
    }

    @Override
    protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
        checkButtons();
        int xx = leftPos;
        int yy = topPos;
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, txBackground);
        blit(pPoseStack, xx, yy, 0, 0, 255, 255);
        if (table.data == null) {
            if (nextCheck < player.tickCount) {
                dummyInspirationStart = ResearchTableData.getAvailableInspiration(player);
            }
            RenderSystem.setShaderTexture(0, txBase);
            pPoseStack.pushPose();
            pPoseStack.translate(xx + 128 - dummyInspirationStart * 5, yy + 55, 0.0);
            pPoseStack.scale(0.5f, 0.5f, 0.0f);
            for (int a = 0; a < dummyInspirationStart; ++a) {
                blit(pPoseStack, 20 * a, 0, (dummyInspirationStart - selectedAids.size() <= a) ? 48 : 32, 96, 16, 16);
            }
            pPoseStack.popPose();
        }
    }

    @Override
    protected void init() {
        super.init();
        addRenderableWidget(buttonCreate);
        buttonCreate.x = leftPos + 128;
        buttonCreate.y = topPos + 22;
    }

    private void checkButtons() {
        if (table.data != null) {
            buttonCreate.active = false;
            buttonCreate.visible = false;
        }
        else {
            buttonCreate.visible = true;
            if (table.getItem(1) == null || table.getItem(0) == null || table.getItem(0).getDamageValue() == table.getItem(0).getMaxDamage()) {
                buttonCreate.active = false;
            }
            else {
                buttonCreate.active = true;
            }
        }
    }
}
