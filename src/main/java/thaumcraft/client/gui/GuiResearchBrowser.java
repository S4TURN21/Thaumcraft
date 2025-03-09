package thaumcraft.client.gui;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncProgressToServer;
import thaumcraft.common.lib.research.ResearchManager;

import java.util.*;

import static net.minecraftforge.client.gui.ScreenUtils.drawTexturedModalRect;

@OnlyIn(Dist.CLIENT)
public class GuiResearchBrowser extends Screen {
    private static int guiBoundsLeft;
    private static int guiBoundsTop;
    private static int guiBoundsRight;
    private static int guiBoundsBottom;
    protected int mouseX;
    protected int mouseY;
    protected float screenZoom;
    protected double curMouseX;
    protected double curMouseY;
    protected double guiMapX;
    protected double guiMapY;
    protected double tempMapX;
    protected double tempMapY;
    private int isMouseButtonDown;
    public static double lastX = -9999.0;
    public static double lastY = -9999.0;
    private int screenX;
    private int screenY;
    private int startX;
    private int startY;
    private LinkedList<ResearchEntry> research;
    static String selectedCategory;
    private ResearchEntry currentHighlight;
    private Player player;
    long popuptime;
    String popupmessage;
    private EditBox searchField;
    private static boolean searching;
    private ArrayList<String> categoriesTC;
    private ArrayList<String> categoriesOther;
    static int catScrollPos;
    static int catScrollMax;
    public int addonShift;
    private ArrayList<String> invisible;
    private boolean isButtonDown;
    ArrayList<Pair<String, SearchResult>> searchResults;
    ResourceLocation tx1;

    public GuiResearchBrowser() {
        super(GameNarrator.NO_TITLE);
        this.mouseX = 0;
        this.mouseY = 0;
        this.screenZoom = 1.0f;
        this.isMouseButtonDown = 0;
        this.startX = 16;
        this.startY = 16;
        this.research = new LinkedList<>();
        this.currentHighlight = null;
        this.player = null;
        this.popuptime = 0L;
        this.popupmessage = "";
        this.categoriesTC = new ArrayList<>();
        this.categoriesOther = new ArrayList<>();
        this.addonShift = 0;
        this.invisible = new ArrayList<>();
        this.searchResults = new ArrayList<>();
        this.tx1 = new ResourceLocation("thaumcraft", "textures/gui/gui_research_browser.png");
        double lastX = GuiResearchBrowser.lastX;
        this.tempMapX = lastX;
        this.guiMapX = lastX;
        this.curMouseX = lastX;
        double lastY = GuiResearchBrowser.lastY;
        this.tempMapY = lastY;
        this.guiMapY = lastY;
        this.curMouseY = lastY;
        this.player = Minecraft.getInstance().player;
    }

    public GuiResearchBrowser(double x, double y) {
        super(GameNarrator.NO_TITLE);
        mouseX = 0;
        mouseY = 0;
        screenZoom = 1.0f;
        isMouseButtonDown = 0;
        startX = 16;
        startY = 16;
        research = new LinkedList<ResearchEntry>();
        currentHighlight = null;
        player = null;
        popuptime = 0L;
        popupmessage = "";
        categoriesTC = new ArrayList<String>();
        categoriesOther = new ArrayList<String>();
        addonShift = 0;
        invisible = new ArrayList<String>();
        searchResults = new ArrayList<Pair<String, SearchResult>>();
        tx1 = new ResourceLocation("thaumcraft", "textures/gui/gui_research_browser.png");
        tempMapX = x;
        guiMapX = x;
        curMouseX = x;
        tempMapY = y;
        guiMapY = y;
        curMouseY = y;
        player = Minecraft.getInstance().player;
    }

    public void updateResearch() {
        this.clearWidgets();
        this.addRenderableWidget(new GuiSearchButton(1, this.height - 17, 16, 16, Component.translatable("tc.search")));
        (this.searchField = new EditBox(this.font, 20, 20, 89, font.lineHeight, Component.empty())).setMaxLength(15);
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.searchField.setBordered(true);
        this.searchField.setVisible(false);
        this.searchField.setTextColor(0xFFFFFF);
        if (GuiResearchBrowser.searching) {
            this.searchField.setVisible(true);
            this.searchField.setCanLoseFocus(false);
            this.searchField.setFocus(true);
            this.searchField.setValue("");
            this.updateSearch();
        }
        this.screenX = this.width - 32;
        this.screenY = this.height - 32;
        this.research.clear();
        if (GuiResearchBrowser.selectedCategory == null) {
            Collection<String> cats = ResearchCategories.researchCategories.keySet();
            GuiResearchBrowser.selectedCategory = cats.iterator().next();
        }
        int limit = (int) Math.floor((this.screenY - 28) / 24.0f);
        this.addonShift = 0;
        int count = 0;
        this.categoriesTC.clear();
        this.categoriesOther.clear();
        Label_0283:
        for (String rcl : ResearchCategories.researchCategories.keySet()) {
            int rt = 0;
            int rco = 0;
            Collection col = ResearchCategories.getResearchCategory(rcl).research.values();
            for (final Object res : col) {
                if (((ResearchEntry) res).hasMeta(ResearchEntry.EnumResearchMeta.AUTOUNLOCK)) {
                    continue;
                }
                ++rt;
                if (!ThaumcraftCapabilities.knowsResearch(this.player, ((ResearchEntry) res).getKey())) {
                    continue;
                }
                ++rco;
            }
            int v = (int) (rco / (float) rt * 100.0f);
            ResearchCategory rc = ResearchCategories.getResearchCategory(rcl);
            if (rc.researchKey != null && !ThaumcraftCapabilities.knowsResearchStrict(this.player, rc.researchKey)) {
                continue;
            }
            for (String tcc : ConfigResearch.TCCategories) {
                if (tcc.equals(rcl)) {
                    this.categoriesTC.add(rcl);
                    this.addRenderableWidget(new GuiCategoryButton(rc, rcl, false, 20 + this.categoriesTC.size(), 1, 10 + this.categoriesTC.size() * 24, 16, 16, Component.translatable("tc.research_category." + rcl), v));
                    continue Label_0283;
                }
            }
            if (++count > limit + GuiResearchBrowser.catScrollPos) {
                continue;
            }
            if (count - 1 < GuiResearchBrowser.catScrollPos) {
                continue;
            }
            this.categoriesOther.add(rcl);
            this.addRenderableWidget(new GuiCategoryButton(rc, rcl, true, 50 + this.categoriesOther.size(), this.width - 17, 10 + this.categoriesOther.size() * 24, 16, 16, Component.translatable("tc.research_category." + rcl), v));
        }
        if (count > limit || count < GuiResearchBrowser.catScrollPos) {
            this.addonShift = (this.screenY - 28) % 24 / 2;
            this.addRenderableWidget(new GuiScrollButton(false, 3, this.width - 14, 20, 10, 11, Component.empty()));
            this.addRenderableWidget(new GuiScrollButton(true, 4, this.width - 14, this.screenY + 1, 10, 11, Component.empty()));
        }
        GuiResearchBrowser.catScrollMax = count - limit;
        if (GuiResearchBrowser.selectedCategory == null || GuiResearchBrowser.selectedCategory.equals("")) {
            return;
        }
        Collection<ResearchEntry> col2 = ResearchCategories.getResearchCategory(GuiResearchBrowser.selectedCategory).research.values();
        for (ResearchEntry res : col2) {
            this.research.add(res);
        }
        GuiResearchBrowser.guiBoundsLeft = 99999;
        GuiResearchBrowser.guiBoundsTop = 99999;
        GuiResearchBrowser.guiBoundsRight = -99999;
        GuiResearchBrowser.guiBoundsBottom = -99999;

        for (ResearchEntry res : this.research) {
            if (res != null && this.isVisible(res)) {
                if (res.getDisplayColumn() * 24 - this.screenX + 48 < GuiResearchBrowser.guiBoundsLeft) {
                    GuiResearchBrowser.guiBoundsLeft = res.getDisplayColumn() * 24 - this.screenX + 48;
                }
                if (res.getDisplayColumn() * 24 - 24 > GuiResearchBrowser.guiBoundsRight) {
                    GuiResearchBrowser.guiBoundsRight = res.getDisplayColumn() * 24 - 24;
                }
                if (res.getDisplayRow() * 24 - this.screenY + 48 < GuiResearchBrowser.guiBoundsTop) {
                    GuiResearchBrowser.guiBoundsTop = res.getDisplayRow() * 24 - this.screenY + 48;
                }
                if (res.getDisplayRow() * 24 - 24 <= GuiResearchBrowser.guiBoundsBottom) {
                    continue;
                }
                GuiResearchBrowser.guiBoundsBottom = res.getDisplayRow() * 24 - 24;
            }
        }
    }

    private boolean isVisible(ResearchEntry res) {
        if (ThaumcraftCapabilities.knowsResearch(this.player, res.getKey())) {
            return true;
        }
        if (this.invisible.contains(res.getKey()) || (res.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN) && !this.canUnlockResearch(res))) {
            return false;
        }
        if (res.getParents() == null && res.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN)) {
            return false;
        }
        if (res.getParents() != null) {
            for (final String r : res.getParents()) {
                final ResearchEntry ri = ResearchCategories.getResearch(r);
                if (ri != null && !this.isVisible(ri)) {
                    this.invisible.add(r);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean canUnlockResearch(ResearchEntry res) {
        return ResearchManager.doesPlayerHaveRequisites(this.player, res.getKey());
    }

    @Override
    public void onClose() {
        GuiResearchBrowser.lastX = this.guiMapX;
        GuiResearchBrowser.lastY = this.guiMapY;
        this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
        super.onClose();
    }

    @Override
    protected void init() {
        this.updateResearch();
        if (GuiResearchBrowser.lastX == -9999.0 || this.guiMapX > GuiResearchBrowser.guiBoundsRight || this.guiMapX < GuiResearchBrowser.guiBoundsLeft) {
            double n = (GuiResearchBrowser.guiBoundsLeft + GuiResearchBrowser.guiBoundsRight) / 2;
            this.tempMapX = n;
            this.guiMapX = n;
        }
        if (GuiResearchBrowser.lastY == -9999.0 || this.guiMapY > GuiResearchBrowser.guiBoundsBottom || this.guiMapY < GuiResearchBrowser.guiBoundsTop) {
            double n2 = (GuiResearchBrowser.guiBoundsBottom + GuiResearchBrowser.guiBoundsTop) / 2;
            this.tempMapY = n2;
            this.guiMapY = n2;
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (GuiResearchBrowser.searching && this.searchField.keyPressed(pKeyCode, pScanCode, pModifiers)) {
            this.updateSearch();
        } else if (pKeyCode == this.minecraft.options.keyInventory.getKey().getValue()) {
            this.minecraft.setScreen((Screen) null);
            return true;
        }

        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    private void updateSearch() {
        this.searchResults.clear();
        this.invisible.clear();
        String s1 = this.searchField.getValue().toLowerCase();
        for (String cat : this.categoriesTC) {
            if (cat.toLowerCase().contains(s1)) {
                this.searchResults.add(Pair.of(I18n.get("tc.research_category." + cat), new SearchResult(cat, null, true)));
            }
        }
        for (String cat : this.categoriesOther) {
            if (cat.toLowerCase().contains(s1)) {
                this.searchResults.add(Pair.of(I18n.get("tc.research_category." + cat), new SearchResult(cat, null, true)));
            }
        }
        ArrayList<ResourceLocation> dupCheck = new ArrayList<>();
        for (String pre : ThaumcraftCapabilities.getKnowledge(this.player).getResearchList()) {
            ResearchEntry ri = ResearchCategories.getResearch(pre);
            if (ri != null) {
                if (ri.getLocalizedName() == null) {
                    continue;
                }
                if (ri.getLocalizedName().toLowerCase().contains(s1)) {
                    this.searchResults.add(Pair.of(ri.getLocalizedName(), new SearchResult(pre, null)));
                }
                int stage = ThaumcraftCapabilities.getKnowledge(this.player).getResearchStage(pre);
                if (ri.getStages() == null) {
                    continue;
                }
                int s2 = (ri.getStages().length - 1 < stage + 1) ? (ri.getStages().length - 1) : (stage + 1);
                ResearchStage page = ri.getStages()[s2];
                if (page == null || page.getRecipes() == null) {
                    continue;
                }
                for (ResourceLocation rec : page.getRecipes()) {
                    if (!dupCheck.contains(rec)) {
                        dupCheck.add(rec);
                        Object recipeObject = CommonInternals.getCatalogRecipe(rec);
                        if (recipeObject == null) {
                            recipeObject = CommonInternals.getCatalogRecipeFake(rec);
                        }
                    }
                }
            }
        }
        Collections.sort(this.searchResults);
    }

    @Override
    public void render(PoseStack pPoseStack, int mx, int my, float pPartialTick) {
        if (!GuiResearchBrowser.searching) {
            if (isButtonDown) {
                if ((this.isMouseButtonDown == 0 || this.isMouseButtonDown == 1) && mx >= this.startX && mx < this.startX + this.screenX && my >= this.startY && my < this.startY + this.screenY) {
                    if (this.isMouseButtonDown == 0) {
                        this.isMouseButtonDown = 1;
                    } else {
                        this.guiMapX -= (mx - this.mouseX) * (double) this.screenZoom;
                        this.guiMapY -= (my - this.mouseY) * (double) this.screenZoom;
                        double guiMapX = this.guiMapX;
                        this.curMouseX = guiMapX;
                        this.tempMapX = guiMapX;
                        double guiMapY = this.guiMapY;
                        this.curMouseY = guiMapY;
                        this.tempMapY = guiMapY;
                    }
                    this.mouseX = mx;
                    this.mouseY = my;
                }
                if (this.tempMapX < GuiResearchBrowser.guiBoundsLeft * (double) this.screenZoom) {
                    this.tempMapX = GuiResearchBrowser.guiBoundsLeft * (double) this.screenZoom;
                }
                if (this.tempMapY < GuiResearchBrowser.guiBoundsTop * (double) this.screenZoom) {
                    this.tempMapY = GuiResearchBrowser.guiBoundsTop * (double) this.screenZoom;
                }
                if (this.tempMapX >= GuiResearchBrowser.guiBoundsRight * (double) this.screenZoom) {
                    this.tempMapX = GuiResearchBrowser.guiBoundsRight * this.screenZoom - 1.0f;
                }
                if (this.tempMapY >= GuiResearchBrowser.guiBoundsBottom * (double) this.screenZoom) {
                    this.tempMapY = GuiResearchBrowser.guiBoundsBottom * this.screenZoom - 1.0f;
                }
            } else {
                this.isMouseButtonDown = 0;
            }
        }
        this.renderBackground(pPoseStack);
        int locX = Mth.floor(this.curMouseX + (this.guiMapX - this.curMouseX) * pPartialTick);
        int locY = Mth.floor(this.curMouseY + (this.guiMapY - this.curMouseY) * pPartialTick);
        if (locX < GuiResearchBrowser.guiBoundsLeft * this.screenZoom) {
            locX = (int) (GuiResearchBrowser.guiBoundsLeft * this.screenZoom);
        }
        if (locY < GuiResearchBrowser.guiBoundsTop * this.screenZoom) {
            locY = (int) (GuiResearchBrowser.guiBoundsTop * this.screenZoom);
        }
        if (locX >= GuiResearchBrowser.guiBoundsRight * this.screenZoom) {
            locX = (int) (GuiResearchBrowser.guiBoundsRight * this.screenZoom - 1.0f);
        }
        if (locY >= GuiResearchBrowser.guiBoundsBottom * this.screenZoom) {
            locY = (int) (GuiResearchBrowser.guiBoundsBottom * this.screenZoom - 1.0f);
        }
        this.genResearchBackgroundFixedPre(pPoseStack);
        if (!GuiResearchBrowser.searching) {
            pPoseStack.pushPose();
            pPoseStack.scale(1.0f / this.screenZoom, 1.0f / this.screenZoom, 1.0f);
            this.genResearchBackgroundZoomable(pPoseStack, mx, my, pPartialTick, locX, locY);
            pPoseStack.popPose();
        } else {
            this.searchField.render(pPoseStack, mx, my, pPartialTick);
            int q = 0;
            for (Pair p : this.searchResults) {
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                SearchResult sr = (SearchResult) p.getRight();
                int color = sr.cat ? 0xDDAAAA : ((sr.recipe == null) ? 0xDDDDDD : 0xAAAADD);
                if (sr.recipe != null) {
                    RenderSystem.setShaderTexture(0, this.tx1);
                    pPoseStack.pushPose();
                    pPoseStack.scale(0.5f, 0.5f, 0.5f);
                    this.blit(pPoseStack, 44, (32 + q * 10) * 2, 224, 48, 16, 16);
                    pPoseStack.popPose();
                }
                if (mx > 22 && mx < 18 + this.screenX && my >= 32 + q * 10 && my < 40 + q * 10) {
                    color = ((sr.recipe == null) ? 0xFFFFFF : (sr.cat ? 0xFFCCCC : 0xCCCCFF));
                }
                this.font.draw(pPoseStack, (String) p.getLeft(), 32, 32 + q * 10, color);
                ++q;
                if (32 + (q + 1) * 10 > this.screenY) {
                    this.font.draw(pPoseStack, I18n.get("tc.search.more"), 22, 34 + q * 10, 0xAAAAAA);
                    break;
                }
            }
        }
        this.genResearchBackgroundFixedPost(pPoseStack, mx, my, pPartialTick);
        if (popuptime > System.currentTimeMillis()) {
            ArrayList<String> text = new ArrayList<String>();
            text.add(popupmessage);
            UtilsFX.drawCustomTooltip(pPoseStack, this, this.font, text, 10, 34, -99);
        }
    }

    @Override
    public void tick() {
        this.curMouseX = this.guiMapX;
        this.curMouseY = this.guiMapY;
        final double var1 = this.tempMapX - this.guiMapX;
        final double var2 = this.tempMapY - this.guiMapY;
        if (var1 * var1 + var2 * var2 < 4.0) {
            this.guiMapX += var1;
            this.guiMapY += var2;
        } else {
            this.guiMapX += var1 * 0.85;
            this.guiMapY += var2 * 0.85;
        }
        this.searchField.tick();
    }

    private void genResearchBackgroundFixedPre(PoseStack pPoseStack) {
        this.setBlitOffset(0);
        RenderSystem.depthFunc(GlConst.GL_GEQUAL);
        pPoseStack.pushPose();
        pPoseStack.translate(0.0f, 0.0f, -200.0f);
        RenderSystem.enableTexture();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
    }

    protected void genResearchBackgroundZoomable(PoseStack pPoseStack, int mx, int my, float par3, int locX, int locY) {
        pPoseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, ResearchCategories.getResearchCategory(GuiResearchBrowser.selectedCategory).background);
        this.drawTexturedModalRectWithDoubles(pPoseStack, (startX - 2) * screenZoom, (startY - 2) * screenZoom, locX / 2.0, locY / 2.0, (screenX + 4) * screenZoom, (screenY + 4) * screenZoom);
        if (ResearchCategories.getResearchCategory(GuiResearchBrowser.selectedCategory).background2 != null) {
            RenderSystem.setShaderTexture(0, ResearchCategories.getResearchCategory(GuiResearchBrowser.selectedCategory).background2);
            drawTexturedModalRectWithDoubles(pPoseStack, (startX - 2) * screenZoom, (startY - 2) * screenZoom, locX / 1.5, locY / 1.5, (screenX + 4) * screenZoom, (screenY + 4) * screenZoom);
        }
        RenderSystem.disableBlend();
        pPoseStack.popPose();
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GlConst.GL_LEQUAL);
        RenderSystem.setShaderTexture(0, tx1);
        enableScissor(16, 16, width-16, height-16);
        if (ThaumcraftCapabilities.getKnowledge(this.player).getResearchList() != null) {
            for (int index = 0; index < this.research.size(); ++index) {
                ResearchEntry source = this.research.get(index);
                if (source.getParents() != null && source.getParents().length > 0) {
                    for (int a = 0; a < source.getParents().length; ++a) {
                        if (source.getParents()[a] != null && ResearchCategories.getResearch(source.getParentsClean()[a]) != null && ResearchCategories.getResearch(source.getParentsClean()[a]).getCategory().equals(GuiResearchBrowser.selectedCategory)) {
                            ResearchEntry parent = ResearchCategories.getResearch(source.getParentsClean()[a]);
                            if (parent.getSiblings() == null || !Arrays.asList(parent.getSiblings()).contains(source.getKey())) {
                                boolean knowsParent = ThaumcraftCapabilities.knowsResearchStrict(this.player, source.getParents()[a]);
                                final boolean b = this.isVisible(source) && !source.getParents()[a].startsWith("~");
                                if (b) {
                                    if (knowsParent) {
                                        this.drawLine(pPoseStack, source.getDisplayColumn(), source.getDisplayRow(), parent.getDisplayColumn(), parent.getDisplayRow(), 0.6f, 0.6f, 0.6f, locX, locY, 3.0f, true, source.hasMeta(ResearchEntry.EnumResearchMeta.REVERSE));
                                    } else if (this.isVisible(parent)) {
                                        this.drawLine(pPoseStack, source.getDisplayColumn(), source.getDisplayRow(), parent.getDisplayColumn(), parent.getDisplayRow(), 0.2f, 0.2f, 0.2f, locX, locY, 2.0f, true, source.hasMeta(ResearchEntry.EnumResearchMeta.REVERSE));
                                    }
                                }
                            }
                        }
                    }
                }
                if (source.getSiblings() != null && source.getSiblings().length > 0) {
                    for (int a = 0; a < source.getSiblings().length; ++a) {
                        if (source.getSiblings()[a] != null && ResearchCategories.getResearch(source.getSiblings()[a]) != null && ResearchCategories.getResearch(source.getSiblings()[a]).getCategory().equals(GuiResearchBrowser.selectedCategory)) {
                            final ResearchEntry sibling = ResearchCategories.getResearch(source.getSiblings()[a]);
                            final boolean knowsSibling = ThaumcraftCapabilities.knowsResearchStrict(this.player, sibling.getKey());
                            if (this.isVisible(source)) {
                                if (!source.getSiblings()[a].startsWith("~")) {
                                    if (knowsSibling) {
                                        this.drawLine(pPoseStack, sibling.getDisplayColumn(), sibling.getDisplayRow(), source.getDisplayColumn(), source.getDisplayRow(), 0.3f, 0.3f, 0.4f, locX, locY, 1.0f, false, source.hasMeta(ResearchEntry.EnumResearchMeta.REVERSE));
                                    } else if (this.isVisible(sibling)) {
                                        this.drawLine(pPoseStack, sibling.getDisplayColumn(), sibling.getDisplayRow(), source.getDisplayColumn(), source.getDisplayRow(), 0.1875f, 0.1875f, 0.25f, locX, locY, 0.0f, false, source.hasMeta(ResearchEntry.EnumResearchMeta.REVERSE));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        this.currentHighlight = null;
        for (int i = 0; i < research.size(); ++i) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
            ResearchEntry iconResearch = research.get(i);
            boolean hasWarp = false;
            if (iconResearch.getStages() != null) {
                for (final ResearchStage stage : iconResearch.getStages()) {
                    if (stage.getWarp() > 0) {
                        hasWarp = true;
                        break;
                    }
                }
            }
            int col = iconResearch.getDisplayColumn() * 24 - locX;
            int row = iconResearch.getDisplayRow() * 24 - locY;
            if (col >= -24 && row >= -24 && col <= screenX * screenZoom && row <= screenY * screenZoom) {
                int iconX = startX + col;
                int iconY = startY + row;
                if (isVisible(iconResearch)) {
                    // TODO: drawForbidden
//                    if (hasWarp) {
//                        drawForbidden(iconX + 8, iconY + 8);
//                    }
                    if (ThaumcraftCapabilities.getKnowledge(this.player).isResearchComplete(iconResearch.getKey())) {
                        float color = 1.0f;
                        RenderSystem.setShaderColor(color, color, color, 1.0f);
                    } else if (this.canUnlockResearch(iconResearch)) {
                        float color = (float) Math.sin(Blaze3D.getTime() * 1000f % 600L / 600.0 * Mth.PI * 2.0) * 0.25f + 0.75f;
                        RenderSystem.setShaderColor(color, color, color, 1.0f);
                    } else {
                        float color = 0.3f;
                        RenderSystem.setShaderColor(color, color, color, 1.0f);
                    }
                    RenderSystem.setShaderTexture(0, tx1);
                    RenderSystem.enableCull();
                    RenderSystem.enableBlend();
                    RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
                    if (iconResearch.hasMeta(ResearchEntry.EnumResearchMeta.ROUND)) {
                        drawTexturedModalRect(pPoseStack, iconX - 8, iconY - 8, 144, 48 + (iconResearch.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN) ? 32 : 0), 32, 32, getBlitOffset());
                    } else {
                        int ix = 80;
                        int iy = 48;
                        if (iconResearch.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN)) {
                            iy += 32;
                        }
                        if (iconResearch.hasMeta(ResearchEntry.EnumResearchMeta.HEX)) {
                            ix += 32;
                        }
                        drawTexturedModalRect(pPoseStack, iconX - 8, iconY - 8, ix, iy, 32, 32, getBlitOffset());
                    }
                    if (iconResearch.hasMeta(ResearchEntry.EnumResearchMeta.SPIKY)) {
                        drawTexturedModalRect(pPoseStack, iconX - 8, iconY - 8, 176, 48 + (iconResearch.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN) ? 32 : 0), 32, 32, getBlitOffset());
                    }
                    boolean bw = false;
                    if (!this.canUnlockResearch(iconResearch)) {
                        final float color = 0.1f;
                        RenderSystem.setShaderColor(color, color, color, 1.0f);
                        bw = true;
                    }
                    if (ThaumcraftCapabilities.getKnowledge(this.player).hasResearchFlag(iconResearch.getKey(), IPlayerKnowledge.EnumResearchFlag.RESEARCH)) {
                        pPoseStack.pushPose();
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                        pPoseStack.translate((float) (iconX - 9), (float) (iconY - 9), 0.0f);
                        pPoseStack.scale(0.5f, 0.5f, 1.0f);
                        drawTexturedModalRect(pPoseStack, 0, 0, 176, 16, 32, 32, getBlitOffset());
                        pPoseStack.popPose();
                    }
                    if (ThaumcraftCapabilities.getKnowledge(this.player).hasResearchFlag(iconResearch.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE)) {
                        pPoseStack.pushPose();
                        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                        pPoseStack.translate((float) (iconX - 9), (float) (iconY + 9), 0.0f);
                        pPoseStack.scale(0.5f, 0.5f, 1.0f);
                        drawTexturedModalRect(pPoseStack, 0, 0, 208, 16, 32, 32, getBlitOffset());
                        pPoseStack.popPose();
                    }
                    drawResearchIcon(pPoseStack, iconResearch, iconX, iconY, getBlitOffset(), bw);
                    if (!this.canUnlockResearch(iconResearch)) {
                        bw = false;
                    }
                    if (mx >= this.startX && my >= this.startY && mx < this.startX + this.screenX && my < this.startY + this.screenY && mx >= (iconX - 2) / this.screenZoom && mx <= (iconX + 18) / this.screenZoom && my >= (iconY - 2) / this.screenZoom && my <= (iconY + 18) / this.screenZoom) {
                        this.currentHighlight = iconResearch;
                    }
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                }
            }
        }
        RenderSystem.disableScissor();
        RenderSystem.disableDepthTest();
    }

    public static void drawResearchIcon(PoseStack pPoseStack, ResearchEntry iconResearch, int iconX, int iconY, float zLevel, boolean bw) {
        if (iconResearch.getIcons() != null && iconResearch.getIcons().length > 0) {
            int idx = (int) (System.currentTimeMillis() / 1000L % iconResearch.getIcons().length);
            pPoseStack.pushPose();
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
            if (iconResearch.getIcons()[idx] instanceof ResourceLocation) {
                RenderSystem.setShaderTexture(0, (ResourceLocation) iconResearch.getIcons()[idx]);
                if (bw) {
                    RenderSystem.setShaderColor(0.2f, 0.2f, 0.2f, 1.0f);
                }
                int w = GL11.glGetTexLevelParameteri(GlConst.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
                int h = GL11.glGetTexLevelParameteri(GlConst.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
                if (h > w && h % w == 0) {
                    int m = h / w;
                    float q = 16.0f / m;
                    float idx2 = System.currentTimeMillis() / 150L % m * q;
                    UtilsFX.drawTexturedQuadF(pPoseStack, (float) iconX, (float) iconY, 0.0f, idx2, 16.0f, q, zLevel);
                } else if (w > h && w % h == 0) {
                    int m = w / h;
                    float q = 16.0f / m;
                    float idx2 = System.currentTimeMillis() / 150L % m * q;
                    UtilsFX.drawTexturedQuadF(pPoseStack, (float) iconX, (float) iconY, idx2, 0.0f, q, 16.0f, zLevel);
                } else {
                    UtilsFX.drawTexturedQuadFull(pPoseStack, (float) iconX, (float) iconY, zLevel);
                }
            } else if (iconResearch.getIcons()[idx] instanceof ItemStack) {
//                this.itemRenderer.renderAndDecorateItem(InventoryUtils.cycleItemStack(iconResearch.getIcons()[idx]), iconX, iconY);
            }
            RenderSystem.disableBlend();
            pPoseStack.popPose();
        }
    }

    private void genResearchBackgroundFixedPost(PoseStack pPoseStack, int mx, int my, float pPartialTick) {
        RenderSystem.setShaderTexture(0, GuiResearchBrowser.this.tx1);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        for (int c = 16; c < this.width - 16; c += 64) {
            int p = 64;
            if (c + p > this.width - 16) {
                p = this.width - 16 - c;
            }
            if (p > 0) {
                this.blit(pPoseStack, c, -2, 48, 13, p, 22);
                this.blit(pPoseStack, c, this.height - 20, 48, 13, p, 22);
            }
        }
        for (int c = 16; c < this.height - 16; c += 64) {
            int p = 64;
            if (c + p > this.height - 16) {
                p = this.height - 16 - c;
            }
            if (p > 0) {
                this.blit(pPoseStack, -2, c, 13, 48, 22, p);
                this.blit(pPoseStack, this.width - 20, c, 13, 48, 22, p);
            }
        }
        this.blit(pPoseStack, -2, -2, 13, 13, 22, 22);
        this.blit(pPoseStack, -2, this.height - 20, 13, 13, 22, 22);
        this.blit(pPoseStack, this.width - 20, -2, 13, 13, 22, 22);
        this.blit(pPoseStack, this.width - 20, this.height - 20, 13, 13, 22, 22);
        pPoseStack.popPose();
        this.setBlitOffset(0);
        RenderSystem.depthFunc(GlConst.GL_LEQUAL);
        RenderSystem.disableDepthTest();
        RenderSystem.enableTexture();
        super.render(pPoseStack, mx, my, pPartialTick);
        if (this.currentHighlight != null) {
            ArrayList<String> text = new ArrayList<String>();
            text.add("§6" + this.currentHighlight.getLocalizedName());
            if (this.canUnlockResearch(this.currentHighlight)) {
                if (!ThaumcraftCapabilities.getKnowledge(this.player).isResearchComplete(this.currentHighlight.getKey()) && this.currentHighlight.getStages() != null) {
                    int stage = ThaumcraftCapabilities.getKnowledge(this.player).getResearchStage(this.currentHighlight.getKey());
                    if (stage > 0) {
                        text.add("@@" + ChatFormatting.AQUA + I18n.get("tc.research.stage") + " " + stage + "/" + this.currentHighlight.getStages().length + ChatFormatting.RESET);
                    } else {
                        text.add("@@" + ChatFormatting.GREEN + I18n.get("tc.research.begin") + ChatFormatting.RESET);
                    }
                }
            } else {
                text.add("@@§c" + I18n.get("tc.researchmissing"));
                int a = 0;
                for (String p2 : this.currentHighlight.getParents()) {
                    if (!ThaumcraftCapabilities.knowsResearchStrict(this.player, p2)) {
                        String s = "?";
                        try {
                            s = ResearchCategories.getResearch(this.currentHighlight.getParentsClean()[a]).getLocalizedName();
                        } catch (Exception ex) {
                        }
                        text.add("@@§e - " + s);
                    }
                    ++a;
                }
            }
            if (ThaumcraftCapabilities.getKnowledge(this.player).hasResearchFlag(this.currentHighlight.getKey(), IPlayerKnowledge.EnumResearchFlag.RESEARCH)) {
                text.add("@@" + I18n.get("tc.research.newresearch"));
            }
            if (ThaumcraftCapabilities.getKnowledge(this.player).hasResearchFlag(this.currentHighlight.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE)) {
                text.add("@@" + I18n.get("tc.research.newpage"));
            }
            UtilsFX.drawCustomTooltip(pPoseStack, this, this.font, text, mx + 3, my - 3, 0xFFFFFF9D);
        }
        RenderSystem.enableDepthTest();
    }

    @Override
    public boolean mouseClicked(double mx, double my, int pButton) {
        if (!GuiResearchBrowser.searching && this.currentHighlight != null && !ThaumcraftCapabilities.knowsResearch(this.player, this.currentHighlight.getKey()) && this.canUnlockResearch(this.currentHighlight)) {
            this.updateResearch();
            PacketHandler.INSTANCE.sendToServer(new PacketSyncProgressToServer(this.currentHighlight.getKey(), true));
            Minecraft.getInstance().setScreen(new GuiResearchPage(currentHighlight, null, guiMapX, guiMapY));
            this.popuptime = System.currentTimeMillis() + 3000L;
            this.popupmessage = Component.translatable("tc.research.popup", this.currentHighlight.getLocalizedName()).getString();
        } else if (currentHighlight != null && ThaumcraftCapabilities.knowsResearch(player, currentHighlight.getKey())) {
            ThaumcraftCapabilities.getKnowledge(player).clearResearchFlag(currentHighlight.getKey(), IPlayerKnowledge.EnumResearchFlag.RESEARCH);
            ThaumcraftCapabilities.getKnowledge(player).clearResearchFlag(currentHighlight.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE);
//            PacketHandler.INSTANCE.sendToServer(new PacketSyncResearchFlagsToServer(mc.player, currentHighlight.getKey()));
            int stage = ThaumcraftCapabilities.getKnowledge(player).getResearchStage(currentHighlight.getKey());
            if (stage > 1 && stage >= currentHighlight.getStages().length) {
                PacketHandler.INSTANCE.sendToServer(new PacketSyncProgressToServer(currentHighlight.getKey(), false, true, false));
            }
            Minecraft.getInstance().setScreen(new GuiResearchPage(currentHighlight, null, guiMapX, guiMapY));
        } else if (GuiResearchBrowser.searching) {
            int q = 0;
            for (Pair p : this.searchResults) {
                SearchResult sr = (SearchResult) p.getRight();
                if (mx > 22 && mx < 18 + this.screenX && my >= 32 + q * 10 && my < 40 + q * 10) {
                    if (this.categoriesTC.contains(sr.key) || this.categoriesOther.contains(sr.key)) {
                        GuiResearchBrowser.searching = false;
                        this.searchField.setVisible(false);
                        this.searchField.setCanLoseFocus(true);
                        this.searchField.setFocus(false);
                        GuiResearchBrowser.selectedCategory = sr.key;
                        this.updateResearch();
                        double n = (GuiResearchBrowser.guiBoundsLeft + GuiResearchBrowser.guiBoundsRight) / 2;
                        this.tempMapX = n;
                        this.guiMapX = n;
                        double n2 = (GuiResearchBrowser.guiBoundsBottom + GuiResearchBrowser.guiBoundsTop) / 2;
                        this.tempMapY = n2;
                        this.guiMapY = n2;
                        break;
                    }
                }
                ++q;
                if (32 + (q + 1) * 10 > this.screenY) {
                    break;
                }
            }
        }

        if (pButton == 0) {
            this.isButtonDown = true;
        }
        return super.mouseClicked(mx, my, pButton);
    }

    @Override
    public boolean mouseReleased(double pMouseX, double pMouseY, int pButton) {
        if (pButton == 0) {
            this.isButtonDown = false;
        }

        return super.mouseReleased(pMouseX, pMouseY, pButton);
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pDelta) {
        if (pDelta < 0) {
            this.screenZoom += 0.25f;
        } else if (pDelta > 0) {
            this.screenZoom -= 0.25f;
        }

        this.screenZoom = Mth.clamp(this.screenZoom, 1.0f, 2.0f);

        return super.mouseScrolled(pMouseX, pMouseY, pDelta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void drawLine(PoseStack pose, int x, int y, int x2, int y2, float r, float g, float b, int locX, int locY, float zMod, boolean arrow, boolean flipped) {
        int zt = getBlitOffset();
        this.setBlitOffset((int) (this.getBlitOffset() + zMod));
        boolean bigCorner = false;
        int xd;
        int yd;
        int xm;
        int ym;
        int xx;
        int yy;
        if (flipped) {
            xd = Math.abs(x2 - x);
            yd = Math.abs(y2 - y);
            xm = ((xd == 0) ? 0 : ((x2 - x > 0) ? -1 : 1));
            ym = ((yd == 0) ? 0 : ((y2 - y > 0) ? -1 : 1));
            if (xd > 1 && yd > 1) {
                bigCorner = true;
            }
            xx = x2 * 24 - 4 - locX + this.startX;
            yy = y2 * 24 - 4 - locY + this.startY;
        } else {
            xd = Math.abs(x - x2);
            yd = Math.abs(y - y2);
            xm = ((xd == 0) ? 0 : ((x - x2 > 0) ? -1 : 1));
            ym = ((yd == 0) ? 0 : ((y - y2 > 0) ? -1 : 1));
            if (xd > 1 && yd > 1) {
                bigCorner = true;
            }
            xx = x * 24 - 4 - locX + this.startX;
            yy = y * 24 - 4 - locY + this.startY;
        }
        pose.pushPose();
        // GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569f);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(r, g, b, 1.0f);
        if (arrow) {
            if (flipped) {
                final int xx2 = x * 24 - 8 - locX + this.startX;
                final int yy2 = y * 24 - 8 - locY + this.startY;
                if (xm < 0) {
                    drawTexturedModalRect(pose, xx2, yy2, 160, 112, 32, 32, getBlitOffset());
                } else if (xm > 0) {
                    drawTexturedModalRect(pose, xx2, yy2, 128, 112, 32, 32, getBlitOffset());
                } else if (ym > 0) {
                    drawTexturedModalRect(pose, xx2, yy2, 64, 112, 32, 32, getBlitOffset());
                } else if (ym < 0) {
                    drawTexturedModalRect(pose, xx2, yy2, 96, 112, 32, 32, getBlitOffset());
                }
            } else if (ym < 0) {
                drawTexturedModalRect(pose, xx - 4, yy - 4, 64, 112, 32, 32, getBlitOffset());
            } else if (ym > 0) {
                drawTexturedModalRect(pose, xx - 4, yy - 4, 96, 112, 32, 32, getBlitOffset());
            } else if (xm > 0) {
                drawTexturedModalRect(pose, xx - 4, yy - 4, 160, 112, 32, 32, getBlitOffset());
            } else if (xm < 0) {
                drawTexturedModalRect(pose, xx - 4, yy - 4, 128, 112, 32, 32, getBlitOffset());
            }
        }
        int v = 1;
        int h = 0;
        while (v < yd - (bigCorner ? 1 : 0)) {
            drawTexturedModalRect(pose, xx + xm * 24 * h, yy + ym * 24 * v, 0, 228, 24, 24, getBlitOffset());
            ++v;
        }
        if (bigCorner) {
            if (xm < 0 && ym > 0) {
                drawTexturedModalRect(pose, xx + xm * 24 * h - 24, yy + ym * 24 * v, 0, 180, 48, 48, getBlitOffset());
            }
            if (xm > 0 && ym > 0) {
                drawTexturedModalRect(pose, xx + xm * 24 * h, yy + ym * 24 * v, 48, 180, 48, 48, getBlitOffset());
            }
            if (xm < 0 && ym < 0) {
                drawTexturedModalRect(pose, xx + xm * 24 * h - 24, yy + ym * 24 * v - 24, 96, 180, 48, 48, getBlitOffset());
            }
            if (xm > 0 && ym < 0) {
                drawTexturedModalRect(pose, xx + xm * 24 * h, yy + ym * 24 * v - 24, 144, 180, 48, 48, getBlitOffset());
            }
        } else {
            if (xm < 0 && ym > 0) {
                drawTexturedModalRect(pose, xx + xm * 24 * h, yy + ym * 24 * v, 48, 228, 24, 24, getBlitOffset());
            }
            if (xm > 0 && ym > 0) {
                drawTexturedModalRect(pose, xx + xm * 24 * h, yy + ym * 24 * v, 72, 228, 24, 24, getBlitOffset());
            }
            if (xm < 0 && ym < 0) {
                drawTexturedModalRect(pose, xx + xm * 24 * h, yy + ym * 24 * v, 96, 228, 24, 24, getBlitOffset());
            }
            if (xm > 0 && ym < 0) {
                drawTexturedModalRect(pose, xx + xm * 24 * h, yy + ym * 24 * v, 120, 228, 24, 24, getBlitOffset());
            }
        }
        v += (bigCorner ? 1 : 0);
        for (h += (bigCorner ? 2 : 1); h < xd; ++h) {
            drawTexturedModalRect(pose, xx + xm * 24 * h, yy + ym * 24 * v, 24, 228, 24, 24, this.getBlitOffset());
        }
        RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.disableBlend();
        pose.popPose();
        this.setBlitOffset(zt);
    }

    public void drawTexturedModalRectWithDoubles(PoseStack pose, float xCoord, float yCoord, double minU, double minV, double maxU, double maxV) {
        float uScale = 1f / 256;
        float vScale = 1f / 256;

        Matrix4f matrix = pose.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix, xCoord, (float) (yCoord + maxV), getBlitOffset()).uv((float) (minU * uScale), (float) ((minV + maxV) * vScale)).endVertex();
        bufferBuilder.vertex(matrix, (float) (xCoord + maxU), (float) (yCoord + maxV), getBlitOffset()).uv((float) ((minU + maxU) * uScale), (float) (((minV + maxV) * vScale))).endVertex();
        bufferBuilder.vertex(matrix, (float) (xCoord + maxU), yCoord, getBlitOffset()).uv((float) ((minU + maxU) * uScale), (float) ((minV * vScale))).endVertex();
        bufferBuilder.vertex(matrix, xCoord, yCoord, getBlitOffset()).uv((float) (minU * uScale), (float) ((minV * vScale))).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
    }

    private class SearchResult implements Comparable<SearchResult> {
        String key;
        ResourceLocation recipe;
        boolean cat;

        private SearchResult(String key, ResourceLocation rec) {
            this.key = key;
            this.recipe = rec;
            this.cat = false;
        }

        private SearchResult(String key, ResourceLocation recipe, boolean cat) {
            this.key = key;
            this.recipe = recipe;
            this.cat = cat;
        }

        @Override
        public int compareTo(SearchResult arg0) {
            SearchResult arg = (SearchResult) arg0;
            int k = this.key.compareTo(arg.key);
            return (k == 0 && this.recipe != null && arg.recipe != null) ? this.recipe.compareTo(arg.recipe) : k;
        }
    }

    private class GuiCategoryButton extends Button {
        ResearchCategory rc;
        String key;
        boolean flip;
        int completion;

        public GuiCategoryButton(ResearchCategory rc, String key, boolean flip, int buttonId, int pX, int pY, int pWidth, int pHeight, Component pMessage, int completion) {
            super(pX, pY, pWidth, pHeight, pMessage, button -> {
                GuiResearchBrowser.searching = false;
                searchField.setVisible(false);
                searchField.setCanLoseFocus(true);
                searchField.setFocus(false);
                GuiResearchBrowser.selectedCategory = ((GuiCategoryButton) button).key;
                updateResearch();
                double n = (GuiResearchBrowser.guiBoundsLeft + GuiResearchBrowser.guiBoundsRight) / 2;
                tempMapX = n;
                guiMapX = n;
                double n2 = (GuiResearchBrowser.guiBoundsBottom + GuiResearchBrowser.guiBoundsTop) / 2;
                tempMapY = n2;
                guiMapY = n2;
            });
            this.rc = rc;
            this.key = key;
            this.flip = flip;
            this.completion = completion;
        }

        @Override
        public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            if (visible) {
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderTexture(0, GuiResearchBrowser.this.tx1);
                pPoseStack.pushPose();
                if (!GuiResearchBrowser.selectedCategory.equals(this.key)) {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                } else {
                    RenderSystem.setShaderColor(0.6f, 1.0f, 1.0f, 1.0f);
                }
                this.blit(pPoseStack, this.x - 3, this.y - 3 + GuiResearchBrowser.this.addonShift, 13, 13, 22, 22);
                pPoseStack.popPose();
                pPoseStack.pushPose();
                RenderSystem.setShaderTexture(0, this.rc.icon);
                if (GuiResearchBrowser.selectedCategory.equals(this.key) || this.isHovered) {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                } else {
                    RenderSystem.setShaderColor(0.66f, 0.66f, 0.66f, 0.8f);
                }
                UtilsFX.drawTexturedQuadFull(pPoseStack, (float) this.x, (float) (this.y + GuiResearchBrowser.this.addonShift), -80.0);
                pPoseStack.popPose();
                RenderSystem.setShaderTexture(0, tx1);
                boolean nr = false;
                boolean np = false;
                for (String rk : rc.research.keySet()) {
                    if (ThaumcraftCapabilities.knowsResearch(player, rk)) {
                        if (!nr && ThaumcraftCapabilities.getKnowledge(player).hasResearchFlag(rk, IPlayerKnowledge.EnumResearchFlag.RESEARCH)) {
                            nr = true;
                        }
                        if (!np && ThaumcraftCapabilities.getKnowledge(player).hasResearchFlag(rk, IPlayerKnowledge.EnumResearchFlag.PAGE)) {
                            np = true;
                        }
                        if (nr && np) {
                            break;
                        }
                        continue;
                    }
                }
                if (nr) {
                    pPoseStack.pushPose();
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.7f);
                    pPoseStack.translate(x - 2, y + addonShift - 2, 0.0);
                    pPoseStack.scale(0.25f, 0.25f, 1.0f);
                    drawTexturedModalRect(pPoseStack, 0, 0, 176, 16, 32, 32, getBlitOffset());
                    pPoseStack.popPose();
                }
                if (np) {
                    pPoseStack.pushPose();
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.7f);
                    pPoseStack.translate(x - 2, y + addonShift + 9, 0.0);
                    pPoseStack.scale(0.25f, 0.25f, 1.0f);
                    drawTexturedModalRect(pPoseStack, 0, 0, 208, 16, 32, 32, getBlitOffset());
                    pPoseStack.popPose();
                }
                if (isHovered) {
                    String dp = this.getMessage().getString() + " (" + this.completion + "%)";
                    drawString(pPoseStack, minecraft.font, dp, flip ? (screenX + 9 - minecraft.font.width(dp)) : (x + 22), y + 4 + addonShift, 0xFFFFFF);
                    int t = 9;
                    if (nr) {
                        drawString(pPoseStack, minecraft.font, I18n.get("tc.research.newresearch"), flip ? (screenX + 9 - minecraft.font.width(I18n.get("tc.research.newresearch"))) : (x + 22), y + 4 + t + addonShift, 0xFFFFFF);
                        t += 9;
                    }
                    if (np) {
                        drawString(pPoseStack, minecraft.font, I18n.get("tc.research.newpage"), flip ? (screenX + 9 - minecraft.font.width(I18n.get("tc.research.newpage"))) : (x + 22), y + 4 + t + addonShift, 0xFFFFFF);
                    }
                }
            }
        }
    }

    private class GuiScrollButton extends Button {
        boolean flip;

        public GuiScrollButton(boolean flip, int buttonId, int x, int y, int widthIn, int heightIn, Component buttonText) {
            super(x, y, widthIn, heightIn, buttonText, button -> {
            });
            this.flip = flip;
        }

        @Override
        public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            if (this.visible) {
                this.isHovered = (mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height);
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderTexture(0, GuiResearchBrowser.this.tx1);
                pPoseStack.pushPose();
                if (this.isHovered) {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                } else {
                    RenderSystem.setShaderColor(0.7f, 0.7f, 0.7f, 1.0f);
                }
                this.blit(pPoseStack, this.x, this.y, 51, this.flip ? 71 : 55, 10, 11);
                pPoseStack.popPose();
            }
        }
    }

    private class GuiSearchButton extends Button {
        public GuiSearchButton(int pX, int pY, int pWidth, int pHeight, Component pMessage) {
            super(pX, pY, pWidth, pHeight, pMessage, button -> {
                GuiResearchBrowser.selectedCategory = "";
                GuiResearchBrowser.searching = true;
                searchField.setVisible(true);
                searchField.setCanLoseFocus(false);
                searchField.setFocus(true);
                searchField.setValue("");
                updateSearch();
            });
        }

        @Override
        public void renderButton(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
            if (this.visible) {
                RenderSystem.enableBlend();
                RenderSystem.blendFuncSeparate(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
                RenderSystem.blendFunc(GlConst.GL_SRC_ALPHA, GlConst.GL_ONE_MINUS_SRC_ALPHA);
                RenderSystem.setShaderTexture(0, tx1);
                pPoseStack.pushPose();
                if (this.isHovered) {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                } else {
                    RenderSystem.setShaderColor(0.8f, 0.8f, 0.8f, 1.0f);
                }
                drawTexturedModalRect(pPoseStack, this.x, this.y, 160, 16, 16, 16, getBlitOffset());
                pPoseStack.popPose();
            }
        }
    }
}
