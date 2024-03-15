package thaumcraft.client.gui;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.api.research.*;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.config.ConfigRecipes;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncProgressToServer;
import thaumcraft.common.lib.utils.InventoryUtils;

import java.util.*;

public class GuiResearchPage extends Screen {
    public static LinkedList<ResourceLocation> history = new LinkedList<>();
    static ResourceLocation shownRecipe;
    static boolean cycleMultiblockLines = false;
    private static final PageImage PILINE = PageImage.parse("thaumcraft:textures/gui/gui_researchbook.png:24:184:95:6:1");
    private static final PageImage PIDIV = PageImage.parse("thaumcraft:textures/gui/gui_researchbook.png:28:192:140:6:1");
    ArrayList<List> reference = new ArrayList<>();
    protected int paneWidth = 256;
    protected int paneHeight = 181;
    protected double guiMapX;
    protected double guiMapY;
    private ResearchEntry research;
    private int currentStage = 0;
    int lastStage = 0;
    boolean hold = false;
    private int page = 0;
    private int maxPages = 0;
    IPlayerKnowledge playerKnowledge;
    int rhash = 0;
    float transX = 0.0f;
    float transY = 0.0f;
    long lastCheck = 0L;
    float pt;
    ResourceLocation tex1 = new ResourceLocation("thaumcraft", "textures/gui/gui_researchbook.png");
    ResourceLocation tex2 = new ResourceLocation("thaumcraft", "textures/gui/gui_researchbook_overlay.png");
    ResourceLocation tex4 = new ResourceLocation("thaumcraft", "textures/gui/paper.png");
    int hrx = 0;
    int hry = 0;
    int recipePage = 0;
    int recipePageMax = 0;
    private long lastCycle;
    private boolean showingAspects = false;
    private boolean showingKnowledge = false;
    LinkedHashMap<ResourceLocation, ArrayList> recipeLists = new LinkedHashMap<>();
    LinkedHashMap<ResourceLocation, ArrayList> recipeOutputs = new LinkedHashMap<>();
    LinkedHashMap<ResourceLocation, ArrayList> drilldownLists = new LinkedHashMap<>();
    boolean hasRecipePages = false;
    private int cycle = -1;
    boolean allowWithPagePopup = false;
    List<String> tipText = null;
    private ArrayList<Page> pages = new ArrayList<>();
    boolean isComplete = false;
    boolean hasAllRequisites = false;
    boolean[] hasCraft = null;
    public HashMap<Integer, String> keyCache = new HashMap<>();

    public GuiResearchPage(ResearchEntry researchEntry, ResourceLocation recipe, double x, double y) {
        super(GameNarrator.NO_TITLE);
        this.research = researchEntry;
        guiMapX = x;
        guiMapY = y;
        this.minecraft = Minecraft.getInstance();
        this.playerKnowledge = ThaumcraftCapabilities.getKnowledge(this.minecraft.player);
        parsePages();
        if (recipe != null) {
            GuiResearchPage.shownRecipe = recipe;
        }
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        hasRecipePages = false;
        long nano = System.nanoTime();
        if (nano > lastCheck) {
            parsePages();
            if (hold) {
                lastCheck = nano + 250000000L;
            } else {
                lastCheck = nano + 2000000000L;
            }
            if (currentStage > lastStage) {
                hold = false;
            }
        }
        pt = pPartialTick;
        this.renderBackground(pPoseStack);
        this.genResearchBackground(pPoseStack, pMouseX, pMouseY, pPartialTick);
        int sw = (width - paneWidth) / 2;
        int sh = (height - paneHeight) / 2;
        if (!GuiResearchPage.history.isEmpty()) {
            int mx = pMouseX - (sw + 118);
            int my = pMouseY - (sh + 190);
            if (mx >= 0 && my >= 0 && mx < 20 && my < 12) {
                minecraft.font.drawShadow(pPoseStack, I18n.get("recipe.return"), (float) pMouseX, (float) pMouseY, 0XFFFFFF);
            }
        }
    }

    protected void genResearchBackground(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        int sw = (width - paneWidth) / 2;
        int sh = (height - paneHeight) / 2;
        float centerX = (this.width - this.paneWidth * 1.3f) / 2.0f;
        float centerY = (this.height - this.paneHeight * 1.3f) / 2.0f;
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, this.tex1);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        pPoseStack.pushPose();
        pPoseStack.translate(centerX, centerY, 0.0f);
        pPoseStack.scale(1.3f, 1.3f, 1.0f);
        this.blit(pPoseStack, 0, 0, 0, 0, this.paneWidth, this.paneHeight);
        pPoseStack.popPose();
        reference.clear();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        int current = 0;
        for (int a = 0; a < this.pages.size(); ++a) {
            if ((current == this.page || current == this.page + 1) && current < this.maxPages) {
                drawPage(pPoseStack, this.pages.get(a), current % 2, sw, sh - 10, pMouseX, pMouseY);
            }
            if (++current > page + 1) {
                break;
            }
        }
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, tex1);
        float bob = (float) (Math.sin(minecraft.player.tickCount / 3.0f) * 0.2f + 0.1f);
        if (!GuiResearchPage.history.isEmpty()) {
            drawTexturedModalRectScaled(pPoseStack, sw + 118, sh + 190, 38, 202, 20, 12, bob);
        }
        if (page > 0 && GuiResearchPage.shownRecipe == null) {
            drawTexturedModalRectScaled(pPoseStack, sw - 16, sh + 190, 0, 184, 12, 8, bob);
        }
        if (page < maxPages - 2 && GuiResearchPage.shownRecipe == null) {
            drawTexturedModalRectScaled(pPoseStack, sw + 262, sh + 190, 12, 184, 12, 8, bob);
        }
        if (tipText != null) {
            UtilsFX.drawCustomTooltip(pPoseStack, this, this.minecraft.font, tipText, pMouseX, pMouseY + 12, 11);
            tipText = null;
        }
    }

    private void drawPage(PoseStack pPoseStack, Page pageParm, int side, int x, int y, int mx, int my) {
        if (this.lastCycle < System.currentTimeMillis()) {
            ++cycle;
            this.lastCycle = System.currentTimeMillis() + 1000L;
        }
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        if (page == 0 && side == 0) {
            this.blit(pPoseStack, x + 4, y - 7, 24, 184, 96, 4);
            this.blit(pPoseStack, x + 4, y + 10, 24, 184, 96, 4);
            int offset = this.minecraft.font.width(research.getLocalizedName());
            if (offset <= 140) {
                this.minecraft.font.draw(pPoseStack, research.getLocalizedName(), x - 15 + 140 / 2 - offset / 2, y, 0x202020);
            } else {
                float vv = 140.0f / offset;
                pPoseStack.pushPose();
                pPoseStack.translate(x - 15 + 140 / 2 - offset / 2 * vv, y + 1.0f * vv, 0.0f);
                pPoseStack.scale(vv, vv, vv);
                this.minecraft.font.draw(pPoseStack, research.getLocalizedName(), 0, 0, 0x202020);
                pPoseStack.popPose();
            }
            y += 28;
        }
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        for (Object content : pageParm.contents) {
            if (content instanceof String) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                String ss = ((String) content).replace("~B", "");
                this.minecraft.font.draw(pPoseStack, ss, x - 15 + side * 152, y - 6, 0);
                y += this.minecraft.font.lineHeight;
                if (!((String) content).endsWith("~B")) {
                    continue;
                }
                y += (int) (this.minecraft.font.lineHeight * 0.66);
            } else {
                if (!(content instanceof PageImage)) {
                    continue;
                }
                PageImage pi = (PageImage) content;
                pPoseStack.pushPose();
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                RenderSystem.setShaderTexture(0, pi.loc);
                RenderSystem.enableBlend();
                int pad = (140 - pi.aw) / 2;
                pPoseStack.translate((float) (x - 15 + side * 152 + pad), (float) (y - 5), 0.0f);
                pPoseStack.scale(pi.scale, pi.scale, pi.scale);
                this.blit(pPoseStack, 0, 0, pi.x, pi.y, pi.w, pi.h);
                pPoseStack.popPose();
                y += pi.ah + 2;
            }
        }
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        if (playerKnowledge.isResearchComplete("FIRSTSTEPS")) {
            y = (height - paneHeight) / 2 + 9;
            RenderSystem.setShaderTexture(0, this.tex1);
            int le = mouseInside(x - 48, y, 25, 16, mx, my) ? 0 : 3;
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            this.drawPopupAt(x - 48, y, 25, 16, mx, my, "tc.aspect.name");
            this.blit(pPoseStack, x - 48 + le, y, 76, 232, 24 - le, 16);
            this.blit(pPoseStack, x - 28, y, 100, 232, 4, 16);
        }
        if (playerKnowledge.isResearchComplete("KNOWLEDGETYPES") && !research.getKey().equals("KNOWLEDGETYPES")) {
            y = (height - paneHeight) / 2 + 32;
            RenderSystem.setShaderTexture(0, this.tex1);
            int le = mouseInside(x - 48, y, 25, 16, mx, my) ? 0 : 3;
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            drawPopupAt(x - 48, y, 25, 16, mx, my, "tc.knowledge.name");
            this.blit(pPoseStack, x - 49 + le, y, 44, 232, 24 - le, 16);
            this.blit(pPoseStack, x - 29, y, 100, 232, 4, 16);
        }
        ResearchStage stage = this.research.getStages()[this.currentStage];
        if (stage.getRecipes() != null) {
            this.drawRecipeBookmarks(pPoseStack, x, mx, my, stage);
        }
        if (this.page == 0 && side == 0 && !this.isComplete) {
            this.drawRequirements(pPoseStack, x, mx, my, stage);
        }
        if (GuiResearchPage.shownRecipe != null) {
            drawRecipe(pPoseStack, mx, my);
        }
    }

    private void drawRequirements(PoseStack pPoseStack, int x, int mx, int my, ResearchStage stage) {
        int y = (this.height - this.paneHeight) / 2 - 16 + 210;
        pPoseStack.pushPose();
        boolean b = false;
        if (stage.getCraft() != null) {
            y -= 18;
            b = true;
            int shift = 24;
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.25f);
            RenderSystem.setShaderTexture(0, this.tex1);
            RenderSystem.enableBlend();
            this.blit(pPoseStack, x - 12, y - 1, 200, 200, 56, 16);
            this.drawPopupAt(x - 15, y, mx, my, "tc.need.craft");
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            if (this.hasCraft != null) {
                if (this.hasCraft.length != stage.getCraft().length) {
                    this.hasCraft = new boolean[stage.getCraft().length];
                }
                int ss2 = 18;
                if (stage.getCraft().length > 6) {
                    ss2 = 110 / stage.getCraft().length;
                }
                for (int idx2 = 0; idx2 < stage.getCraft().length; ++idx2) {
                    ItemStack stack = InventoryUtils.cycleItemStack(stage.getCraft()[idx2], idx2);
                    this.drawStackAt(pPoseStack, stack, x - 15 + shift, y, mx, my, true);
                    if (hasCraft[idx2]) {
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                        RenderSystem.setShaderTexture(0, this.tex1);
                        RenderSystem.disableDepthTest();
                        this.blit(pPoseStack, x - 15 + shift + 8, y, 159, 207, 10, 10);
                        RenderSystem.enableDepthTest();
                    }
                }
                shift += ss2;
            }
        }
        if (b) {
            y -= 12;
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, this.tex1);
            RenderSystem.enableBlend();
            this.blit(pPoseStack, x + 4, y - 2, 24, 184, 96, 8);
            if (this.hasAllRequisites) {
                this.hrx = x + 20;
                this.hry = y - 6;
                if (this.hold) {
                    String s2 = I18n.get("tc.stage.hold");
                    int m = this.minecraft.font.width(s2);
                    this.minecraft.font.drawShadow(pPoseStack, s2, x + 52 - m / 2.0f, (float) (y - 4), 0XFFFFFF);
                } else {
                    if (mouseInside(hrx, hry, 64, 12, mx, my)) {
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    } else {
                        RenderSystem.setShaderColor(0.8f, 0.8f, 0.9f, 1.0f);
                    }
                    RenderSystem.setShaderTexture(0, this.tex1);
                    this.blit(pPoseStack, hrx, hry, 84, 216, 64, 12);
                    String s2 = I18n.get("tc.stage.complete");
                    int m = this.minecraft.font.width(s2);
                    this.minecraft.font.drawShadow(pPoseStack, s2, x + 52 - m / 2.0f, (float) (y - 4), 0XFFFFFF);
                }
            }
        }
        pPoseStack.popPose();
    }

    private void drawRecipeBookmarks(PoseStack pPoseStack, int x, int mx, int my, ResearchStage stage) {
        Random rng = new Random(this.rhash);
        pPoseStack.pushPose();
        int y = (this.height - this.paneHeight) / 2 - 8;
        this.allowWithPagePopup = true;
        if (this.recipeOutputs.size() > 0) {
            int space = Math.min(25, 200 / recipeOutputs.size());
            for (ResourceLocation rk : recipeOutputs.keySet()) {
                List list = recipeOutputs.get(rk);
                if (list != null && list.size() > 0) {
                    int i = cycle % list.size();
                    if (list.get(i) == null) {
                        continue;
                    }
                    int sh = rng.nextInt(3);
                    int le = rng.nextInt(3) + (mouseInside(x + 280, y - 1, 30, 16, mx, my) ? 0 : 3);
                    RenderSystem.setShaderTexture(0, this.tex1);
                    if (rk.equals(GuiResearchPage.shownRecipe)) {
                        RenderSystem.setShaderColor(1.0f, 0.5f, 0.5f, 1.0f);
                    } else {
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    }
                    this.blit(pPoseStack, x + 280 + sh, y - 1, 120 + le, 232, 28, 16);
                    this.blit(pPoseStack, x + 280 + sh, y - 1, 116, 232, 4, 16);
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    UtilsFX.hideStackOverlay = true;
                    if (list.get(i) instanceof ItemStack) {
                        drawStackAt(pPoseStack, (ItemStack) list.get(i), x + 287 + sh - le, y - 1, mx, my, false);
                    }
                    UtilsFX.hideStackOverlay = false;
                    y += space;
                }
            }
        }
        this.allowWithPagePopup = false;
        pPoseStack.popPose();
    }

    private void generateRecipesLists(ResearchStage stage, ResearchAddendum[] addenda) {
        recipeLists.clear();
        recipeOutputs.clear();
        if (stage == null || stage.getRecipes() == null) {
            return;
        }
        for (ResourceLocation rk : stage.getRecipes()) {
            addRecipesToList(rk, recipeLists, recipeOutputs, rk);
        }
        if (addenda == null) {
            return;
        }
        for (ResearchAddendum addendum : addenda) {
            if (addendum.getRecipes() != null && ThaumcraftCapabilities.knowsResearchStrict(this.minecraft.player, addendum.getResearch())) {
                for (ResourceLocation rk2 : addendum.getRecipes()) {
                    addRecipesToList(rk2, recipeLists, recipeOutputs, rk2);
                }
            }
        }
    }

    private void addRecipesToList(ResourceLocation rk, LinkedHashMap<ResourceLocation, ArrayList> recipeLists2, LinkedHashMap<ResourceLocation, ArrayList> recipeOutputs2, ResourceLocation rkey) {
        Object recipe = CommonInternals.getCatalogRecipe(rk);
        if (recipe == null) {
            recipe = CommonInternals.getCatalogRecipeFake(rk);
        }
        if (recipe == null) {
            recipe = this.minecraft.level.getRecipeManager().byKey(rk);
        }
        if (recipe == null) {
            return;
        }
        if (recipe instanceof ArrayList) {
            for (ResourceLocation rl : (ArrayList<ResourceLocation>) recipe) {
                addRecipesToList(rl, recipeLists2, recipeOutputs2, rk);
            }
        } else {
            if (!recipeLists2.containsKey(rkey)) {
                recipeLists2.put(rkey, new ArrayList());
                recipeOutputs2.put(rkey, new ArrayList());
            }
            ArrayList list = recipeLists2.get(rkey);
            ArrayList outputs = recipeOutputs2.get(rkey);
            if (recipe instanceof CraftingRecipe) {
                CraftingRecipe re4 = (CraftingRecipe) recipe;
                list.add(re4);
                outputs.add(re4.getResultItem());
            }
        }

    }

    private void drawRecipe(PoseStack pPoseStack, int mx, int my) {
        allowWithPagePopup = true;
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, tex4);
        int x = (width - 256) / 2;
        int y = (height - 256) / 2;
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        this.blit(pPoseStack, x, y, 0, 0, 255, 255);
        RenderSystem.enableDepthTest();
        List list = recipeLists.get(GuiResearchPage.shownRecipe);
        if (list == null || list.size() == 0) {
            list = drilldownLists.get(GuiResearchPage.shownRecipe);
        }
        if (list != null && list.size() > 0) {
            hasRecipePages = (list.size() > 1);
            recipePageMax = list.size() - 1;
            if (recipePage > recipePageMax) {
                recipePage = recipePageMax;
            }
            Object recipe = list.get(recipePage % list.size());
            if (recipe != null) {
                if (recipe instanceof CraftingRecipe) {
                    drawCraftingPage(pPoseStack, x + 128, y + 128, mx, my, (CraftingRecipe) recipe);
                }
            }
            if (hasRecipePages) {
                RenderSystem.setShaderTexture(0, tex1);
                float bob = (float) (Math.sin(minecraft.player.tickCount / 3.0f) * 0.2f + 0.1f);
                if (recipePage > 0) {
                    drawTexturedModalRectScaled(pPoseStack, x + 40, y + 232, 0, 184, 12, 8, bob);
                }
                if (recipePage < recipePageMax) {
                    drawTexturedModalRectScaled(pPoseStack, x + 204, y + 232, 12, 184, 12, 8, bob);
                }
            }
        }
        allowWithPagePopup = false;
    }

    private void drawCraftingPage(PoseStack pPoseStack, int x, int y, int mx, int my, CraftingRecipe recipe) {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        if (recipe == null) {
            return;
        }
        pPoseStack.pushPose();
        RenderSystem.setShaderTexture(0, tex2);
        pPoseStack.pushPose();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        pPoseStack.translate((float) x, (float) y, 0.0f);
        pPoseStack.scale(2.0f, 2.0f, 1.0f);
        this.blit(pPoseStack, -26, -26, 60, 15, 51, 52);
        this.blit(pPoseStack, -8, -46, 20, 3, 16, 16);
        pPoseStack.popPose();
        drawStackAt(pPoseStack, InventoryUtils.cycleItemStack(recipe.getResultItem(), 0), x - 8, y - 84, mx, my, false);
        if (recipe != null && recipe instanceof ShapelessRecipe) {
            String text = I18n.get("recipe.type.workbenchshapeless");
            int offset = minecraft.font.width(text);
            minecraft.font.draw(pPoseStack, text, x - offset / 2, y - 104, 5263440);
            NonNullList<Ingredient> items2 = recipe.getIngredients();
            for (int k = 0; k < items2.size() && k < 9; ++k) {
                if (items2.get(k) != null) {
                    drawStackAt(pPoseStack, InventoryUtils.cycleItemStack(items2.get(k), k), x - 40 + k % 3 * 32, y - 40 + k / 3 * 32, mx, my, true);
                }
            }
        }
        pPoseStack.popPose();
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (pKeyCode == minecraft.options.keyInventory.getKey().getValue() || pKeyCode == 256) {
            if (GuiResearchPage.shownRecipe != null || showingAspects || showingKnowledge) {
                GuiResearchPage.shownRecipe = null;
            } else {
                minecraft.setScreen(new GuiResearchBrowser(guiMapX, guiMapY));
            }
            return true;
        } else {
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int centerX = (width - paneWidth) / 2;
        int centerY = (height - paneHeight) / 2;
        double mx = pMouseX - hrx;
        double my = pMouseY - hry;
        if (GuiResearchPage.shownRecipe == null && !hold && hasAllRequisites && mx >= 0 && my >= 0 && mx < 64 && my < 12) {
            PacketHandler.INSTANCE.sendToServer(new PacketSyncProgressToServer(research.getKey(), false, true, true));
            Minecraft.getInstance().player.playSound(SoundsTC.write, 0.66f, 1.0f);
            lastCheck = 0L;
            lastStage = currentStage;
            hold = true;
            keyCache.clear();
            drilldownLists.clear();
        }
        if (recipeLists.size() > 0) {
            int aa = 0;
            int space = Math.min(25, 200 / recipeLists.size());
            for (ResourceLocation rk : recipeLists.keySet()) {
                mx = pMouseX - (centerX + 280);
                my = pMouseY - (centerY - 8 + aa * space);
                if (mx >= 0 && my >= 0 && mx < 30 && my < 16) {
                    if (rk.equals(GuiResearchPage.shownRecipe)) {
                        GuiResearchPage.shownRecipe = null;
                    } else {
                        GuiResearchPage.shownRecipe = rk;
                    }
                    showingAspects = false;
                    showingKnowledge = false;
                    GuiResearchPage.history.clear();
                    Minecraft.getInstance().player.playSound(SoundsTC.page, 0.7f, 0.9f);
                    break;
                }
                ++aa;
            }
        }
        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    private void drawPopupAt(int x, int y, int mx, int my, String text) {
        if ((GuiResearchPage.shownRecipe == null || this.allowWithPagePopup) && mx >= x && my >= y && mx < x + 16 && my < y + 16) {
            ArrayList<String> s = new ArrayList<>();
            s.add(Component.translatable(text).getString());
            this.tipText = s;
        }
    }

    void drawPopupAt(int x, int y, int w, int h, int mx, int my, String text) {
        if ((GuiResearchPage.shownRecipe == null || allowWithPagePopup) && mx >= x && my >= y && mx < x + w && my < y + h) {
            ArrayList<String> s = new ArrayList<>();
            s.add(Component.translatable(text).getString());
            tipText = s;
        }
    }

    private boolean mouseInside(int x, int y, int w, int h, int mx, int my) {
        return mx >= x && my >= y && mx < x + w && my < y + h;
    }

    void drawStackAt(PoseStack pPoseStack, ItemStack itemstack, int x, int y, int mx, int my, boolean clickthrough) {
        UtilsFX.renderItemStack(pPoseStack, this.minecraft, itemstack, x, y, null);
        if ((GuiResearchPage.shownRecipe == null || allowWithPagePopup) && mx >= x && my >= y && mx < x + 16 && my < y + 16 && itemstack != null && !itemstack.isEmpty() && itemstack.getItem() != null) {
            if (clickthrough) {
                ArrayList<String> addtext = new ArrayList<>(itemstack.getTooltipLines(this.minecraft.player, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL).stream().map(Component::getString).toList());
                String ref = getCraftingRecipeKey(this.minecraft.player, itemstack);
                if (ref != null) {
                    String[] sr = ref.split(";", 2);
                    if (sr != null && sr.length > 1) {
                        ResourceLocation res = new ResourceLocation(sr[0]);
                        if (res.getPath().equals("UNKNOWN")) {
                            addtext.add(ChatFormatting.DARK_RED + "" + ChatFormatting.ITALIC + I18n.get("recipe.unknown"));
                        } else {
                            addtext.add(ChatFormatting.BLUE + "" + ChatFormatting.ITALIC + I18n.get("recipe.clickthrough"));
                            reference.add(Arrays.asList(mx, my, (Comparable) res, sr[1]));
                        }
                    }
                }
                tipText = addtext;
            } else {
                tipText = itemstack.getTooltipLines(this.minecraft.player, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL).stream().map(Component::getString).toList();
            }
        }
    }

    public void drawTexturedModalRectScaled(PoseStack pPoseStack, int pX, int pY, int pUOffset, int pVOffset, int pUWidth, int pVHeight, float scale) {
        pPoseStack.pushPose();
        float var7 = 0.00390625f;
        float var8 = 0.00390625f;
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        pPoseStack.translate(pX + pUWidth / 2.0f, pY + pVHeight / 2.0f, 0.0f);
        pPoseStack.scale(1.0f + scale, 1.0f + scale, 1.0f);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.vertex(-pUWidth / 2.0f, pVHeight / 2.0f, this.getBlitOffset()).uv((int) ((pUOffset + 0) * var7), (int) ((pVOffset + pVHeight) * var8)).endVertex();
        bufferbuilder.vertex(pUWidth / 2.0f, pVHeight / 2.0f, this.getBlitOffset()).uv2((int) ((pUOffset + pUWidth) * var7), (int) ((pVOffset + pVHeight) * var8)).endVertex();
        bufferbuilder.vertex(pUWidth / 2.0f, -pVHeight / 2.0f, this.getBlitOffset()).uv2((int) ((pUOffset + pUWidth) * var7), (int) ((pVOffset + 0) * var8)).endVertex();
        bufferbuilder.vertex(-pUWidth / 2.0f, -pVHeight / 2.0f, this.getBlitOffset()).uv2((int) ((pUOffset + 0) * var7), (int) ((pVOffset + 0) * var8)).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
        pPoseStack.popPose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void parsePages() {
        this.checkRequisites();
        this.pages.clear();
        if (this.research.getStages() == null) {
            return;
        }
        boolean complete = false;
        this.currentStage = ThaumcraftCapabilities.getKnowledge(this.minecraft.player).getResearchStage(this.research.getKey()) - 1;
        while (this.currentStage >= this.research.getStages().length) {
            --this.currentStage;
            complete = true;
        }
        if (currentStage < 0) {
            this.currentStage = 0;
        }
        ResearchStage stage = this.research.getStages()[this.currentStage];
        ResearchAddendum[] addenda = null;
        if (research.getAddenda() != null && complete) {
            addenda = research.getAddenda();
        }
        this.generateRecipesLists(stage, addenda);
        String rawText = stage.getTextLocalized();
        if (addenda != null) {
            int ac = 0;
            for (ResearchAddendum addendum : addenda) {
                if (ThaumcraftCapabilities.knowsResearchStrict(this.minecraft.player, addendum.getResearch())) {
                    ++ac;
                    Component text = Component.translatable("tc.addendumtext", ac);
                    rawText = rawText + "<PAGE>" + text.getString() + "<BR>" + addendum.getTextLocalized();
                }
            }
        }
        rawText = rawText.replaceAll("<BR>", "~B\n\n");
        rawText = rawText.replaceAll("<BR/>", "~B\n\n");
        rawText = rawText.replaceAll("<LINE>", "~L");
        rawText = rawText.replaceAll("<LINE/>", "~L");
        rawText = rawText.replaceAll("<DIV>", "~D");
        rawText = rawText.replaceAll("<DIV/>", "~D");
        rawText = rawText.replaceAll("<PAGE>", "~P");
        rawText = rawText.replaceAll("<PAGE/>", "~P");
        ArrayList<PageImage> images = new ArrayList<>();
        String[] split;
        String[] imgSplit = split = rawText.split("<IMG>");
        for (String s : split) {
            int i = s.indexOf("</IMG>");
            if (i >= 0) {
                String clean = s.substring(0, i);
                PageImage pi = PageImage.parse(clean);
                if (pi == null) {
                    rawText = rawText.replaceFirst(clean, "\n");
                } else {
                    images.add(pi);
                    rawText = rawText.replaceFirst(clean, "~I");
                }
            }
        }
        rawText = rawText.replaceAll("<IMG>", "");
        rawText = rawText.replaceAll("</IMG>", "");
        List<String> firstPassText = new ArrayList<>();
        String[] temp = rawText.split("~P");
        for (int a = 0; a < temp.length; ++a) {
            String t = temp[a];
            String[] temp2 = t.split("~D");
            for (int x = 0; x < temp2.length; ++x) {
                String t2 = temp2[x];
                String[] temp3 = t2.split("~L");
                for (int b = 0; b < temp3.length; ++b) {
                    String t3 = temp3[b];
                    String[] temp4 = t3.split("~I");
                    for (int c = 0; c < temp4.length; ++c) {
                        String t4 = temp4[c];
                        firstPassText.add(t4);
                        if (c != temp4.length - 1) {
                            firstPassText.add("~I");
                        }
                    }
                    if (b != temp3.length - 1) {
                        firstPassText.add("~L");
                    }
                }
                if (x != temp2.length - 1) {
                    firstPassText.add("~D");
                }
            }
            if (a != temp.length - 1) {
                firstPassText.add("~P");
            }
        }

        List<String> parsedText = new ArrayList<>();
        for (String s2 : firstPassText) {
            List<String> pt1 = this.minecraft.font.getSplitter().splitLines(s2, 140, Style.EMPTY).stream().map(FormattedText::getString).toList();
            for (String ln : pt1) {
                parsedText.add(ln);
            }
        }
        int lineHeight = this.minecraft.font.lineHeight;
        int heightRemaining = 182;
        int dividerSpace = 0;
        if (research.getKey().equals("KNOWLEDGETYPES")) {
            heightRemaining -= 2;
            int tc = 0;
            int amt = 0;
            for (IPlayerKnowledge.EnumKnowledgeType type : IPlayerKnowledge.EnumKnowledgeType.values()) {
                for (ResearchCategory category : ResearchCategories.researchCategories.values()) {
                    if (!type.hasFields() && category != null) {
                        continue;
                    }
                    amt = playerKnowledge.getKnowledgeRaw(type, category);
                    if (amt > 0) {
                        ++tc;
                        break;
                    }
                }
            }
            heightRemaining -= 20 * tc;
            dividerSpace = 12;
        }
        if (!isComplete) {
            if (stage.getCraft() != null) {
                heightRemaining -= 18;
                dividerSpace = 15;
            }
        }
        heightRemaining -= dividerSpace;
        Page page1 = new Page();
        ArrayList<PageImage> tempImages = new ArrayList<>();
        for (String line : parsedText) {
            if (line.contains("~I")) {
                if (!images.isEmpty()) {
                    tempImages.add(images.remove(0));
                }
                line = "";
            }
            if (line.contains("~L")) {
                tempImages.add(GuiResearchPage.PILINE);
                line = "";
            }
            if (line.contains("~D")) {
                tempImages.add(GuiResearchPage.PIDIV);
                line = "";
            }
            if (line.contains("~P")) {
                this.pages.add(page1.copy());
                page1 = new Page();
                line = "";
            }
            if (line.contains("~P")) {
                heightRemaining = 210;
                this.pages.add(page1.copy());
                page1 = new Page();
                line = "";
            }
            if (!line.isEmpty()) {
                line = line.trim();
                page1.contents.add(line);
                heightRemaining -= lineHeight;
                if (line.endsWith("~B")) {
                    heightRemaining -= (int) (lineHeight * 0.66);
                }
            }
            while (!tempImages.isEmpty() && heightRemaining >= tempImages.get(0).ah + 2) {
                heightRemaining -= tempImages.get(0).ah + 2;
                page1.contents.add(tempImages.remove(0));
            }
            if (heightRemaining < lineHeight && !page1.contents.isEmpty()) {
                heightRemaining = 210;
                this.pages.add(page1.copy());
                page1 = new Page();
            }
        }
        if (!page1.contents.isEmpty()) {
            this.pages.add(page1.copy());
        }
        page1 = new Page();
        heightRemaining = 210;
        while (!tempImages.isEmpty()) {
            if (heightRemaining < tempImages.get(0).ah + 2) {
                heightRemaining = 210;
                pages.add(page1.copy());
                page1 = new Page();
            } else {
                heightRemaining -= tempImages.get(0).ah + 2;
                page1.contents.add(tempImages.remove(0));
            }
        }
        if (!page1.contents.isEmpty()) {
            this.pages.add(page1.copy());
        }
        this.rhash = research.getKey().hashCode() + currentStage * 50;
        this.maxPages = pages.size();
    }

    private void checkRequisites() {
        if (this.research.getStages() != null) {
            this.isComplete = this.playerKnowledge.isResearchComplete(this.research.getKey());
            while (currentStage >= research.getStages().length) {
                --currentStage;
            }
            if (currentStage < 0) {
                return;
            }
            hasAllRequisites = true;
            hasCraft = null;
            ResearchStage stage = this.research.getStages()[this.currentStage];
            Object[] c = stage.getCraft();
            if (c != null) {
                this.hasCraft = new boolean[c.length];
                for (int a2 = 0; a2 < c.length; ++a2) {
                    if (!this.playerKnowledge.isResearchKnown("[#]" + stage.getCraftReference()[a2])) {
                        this.hasAllRequisites = false;
                        this.hasCraft[a2] = false;
                    } else {
                        this.hasCraft[a2] = true;
                    }
                }
            }
        }
    }

    private int findRecipePage(ResourceLocation rk, ItemStack stack, int start) {
        Object recipe = CommonInternals.getCatalogRecipe(rk);
        if (recipe == null) {
            recipe = CommonInternals.getCatalogRecipeFake(rk);
        }
        if (recipe == null) {
            var optional = this.minecraft.level.getRecipeManager().byKey(rk);
            if (optional.isPresent()) {
                recipe = optional.get();
            }
        }
        if (recipe == null) {
            recipe = ConfigRecipes.recipeGroups.get(rk.toString());
        }
        if (recipe == null) {
            return -1;
        }
        if (recipe instanceof ArrayList) {
            int g = 0;
            for (ResourceLocation rl : (ArrayList<ResourceLocation>) recipe) {
                int q = findRecipePage(rl, stack, g);
                if (q >= 0) {
                    return q;
                }
                ++g;
            }
        }
        if (recipe instanceof CraftingRecipe && ((CraftingRecipe) recipe).getResultItem().equals(stack, true)) {
            if (recipe instanceof IArcaneRecipe && !ThaumcraftCapabilities.knowsResearchStrict(minecraft.player, ((IArcaneRecipe)recipe).getResearch())) {
                return -99;
            }
            return start;
        } else {
            return -1;
        }
    }

    private String getCraftingRecipeKey(Player player, ItemStack stack) {
        int key = stack.serializeNBT().toString().hashCode();
        if (keyCache.containsKey(key)) {
            return keyCache.get(key);
        }
        for (ResearchCategory rcl : ResearchCategories.researchCategories.values()) {
            for (ResearchEntry ri : rcl.research.values()) {
                if (ri.getStages() == null) {
                    continue;
                }
                for (int a = 0; a < ri.getStages().length; ++a) {
                    ResearchStage stage = ri.getStages()[a];
                    if (stage.getRecipes() != null) {
                        for (ResourceLocation rec : stage.getRecipes()) {
                            int result = findRecipePage(rec, stack, 0);
                            if (result != -1) {
                                String s = rec.toString();
                                if (result == -99) {
                                    s = new ResourceLocation("UNKNOWN").toString();
                                } else {
                                    s = s + ";" + result;
                                }
                                keyCache.put(key, s);
                                return s;
                            }
                        }
                    }
                }
            }
        }
        keyCache.put(key, null);
        return null;
    }

    private class Page {
        ArrayList contents = new ArrayList();

        public Page copy() {
            Page p = new Page();
            p.contents.addAll(this.contents);
            return p;
        }
    }

    private static class PageImage {
        int x;
        int y;
        int w;
        int h;
        int aw;
        int ah;
        float scale;
        ResourceLocation loc;

        public static PageImage parse(String text) {
            String[] s = text.split(":");
            if (s.length != 7) {
                return null;
            }
            try {
                PageImage pi = new PageImage();
                pi.loc = new ResourceLocation(s[0], s[1]);
                pi.x = Integer.parseInt(s[2]);
                pi.y = Integer.parseInt(s[3]);
                pi.w = Integer.parseInt(s[4]);
                pi.h = Integer.parseInt(s[5]);
                pi.scale = Float.parseFloat(s[6]);
                pi.aw = (int) (pi.w * pi.scale);
                pi.ah = (int) (pi.h * pi.scale);
                if (pi.ah > 208 || pi.aw > 140) {
                    return null;
                }
                return pi;
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
