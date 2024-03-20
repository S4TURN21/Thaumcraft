package thaumcraft.common.container.slot;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.tiles.crafting.BlockEntityArcaneWorkbench;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

public class SlotCraftingArcaneWorkbench extends Slot {
    private final Player player;
    private final CraftingContainer craftMatrix;
    private int amountCrafted;
    private final BlockEntityArcaneWorkbench blockEntity;

    public SlotCraftingArcaneWorkbench(BlockEntityArcaneWorkbench te, Player player, CraftingContainer inventory, ResultContainer resultContainer, int pSlot, int pX, int pY) {
        super(resultContainer, pSlot, pX, pY);
        this.player = player;
        this.craftMatrix = inventory;
        this.blockEntity = te;
    }

    public boolean mayPlace(ItemStack pStack) {
        return false;
    }

    @Override
    public ItemStack remove(int pAmount) {
        if (this.hasItem()) {
            this.amountCrafted += Math.min(pAmount, this.getItem().getCount());
        }
        return super.remove(pAmount);
    }

    @Override
    protected void onQuickCraft(ItemStack pStack, int pAmount) {
        this.amountCrafted += pAmount;
        this.checkTakeAchievements(pStack);
    }

    @Override
    protected void onSwapCraft(int pNumItemsCrafted) {
        this.amountCrafted += pNumItemsCrafted;
    }

    @Override
    protected void checkTakeAchievements(ItemStack pStack) {
        if (this.amountCrafted > 0) {
            pStack.onCraftedBy(this.player.level, this.player, this.amountCrafted);
            net.minecraftforge.event.ForgeEventFactory.firePlayerCraftingEvent(this.player, pStack, this.craftMatrix);
        }
        amountCrafted = 0;
        ResultContainer inventorycraftresult = (ResultContainer) container;
        Recipe irecipe = inventorycraftresult.getRecipeUsed();
        if (irecipe != null) {
            ((RecipeHolder) this.container).awardUsedRecipes(this.player);
            inventorycraftresult.setRecipeUsed(null);
        }

        this.amountCrafted = 0;
    }

    @Override
    public void onTake(Player pPlayer, ItemStack pStack) {
        IArcaneRecipe recipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(craftMatrix, pPlayer);
        CraftingContainer ic = craftMatrix;
        ForgeHooks.setCraftingPlayer(pPlayer);
        NonNullList<ItemStack> nonnulllist;
        if (recipe != null) {
            nonnulllist = pPlayer.level.getRecipeManager().getRemainingItemsFor(ShapedArcaneRecipe.Type.INSTANCE, craftMatrix, pPlayer.level);
        } else {
            ic = new CraftingContainer(new ContainerDummy(), 3, 3);
            for (int a = 0; a < 9; ++a) {
                ic.setItem(a, craftMatrix.getItem(a));
            }
            nonnulllist = pPlayer.level.getRecipeManager().getRemainingItemsFor(RecipeType.CRAFTING, ic, pPlayer.level);
        }
        ForgeHooks.setCraftingPlayer(null);
        int vis = 0;
        AspectList crystals = null;
        if (recipe != null) {
            vis = recipe.getVis();
            crystals = recipe.getCrystals();
            if (vis > 0) {
                blockEntity.getAura();
                blockEntity.spendAura(vis);
            }
        }
        for (int i = 0; i < Math.min(9, nonnulllist.size()); ++i) {
            ItemStack itemstack = ic.getItem(i);
            ItemStack itemstack2 = nonnulllist.get(i);
            if (!itemstack.isEmpty()) {
                craftMatrix.removeItem(i, 1);
                itemstack = ic.getItem(i);
            }
            if (!itemstack2.isEmpty()) {
                if (itemstack.isEmpty()) {
                    craftMatrix.setItem(i, itemstack2);
                } else if (ItemStack.matches(itemstack, itemstack2) && ItemStack.tagMatches(itemstack, itemstack2)) {
                    itemstack2.grow(itemstack.getCount());
                    craftMatrix.setItem(i, itemstack2);
                } else if (!player.getInventory().add(itemstack2)) {
                    player.drop(itemstack2, false);
                }
            }
        }
        if (crystals != null) {
            for (Aspect aspect : crystals.getAspects()) {
                ItemStack cs = ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect));
                for (int j = 0; j < 6; ++j) {
                    ItemStack itemstack3 = craftMatrix.getItem(9 + j);
                    if (itemstack3.getItem() == ItemsTC.crystalEssence && ItemStack.tagMatches(cs, itemstack3)) {
                        craftMatrix.removeItem(9 + j, cs.getCount());
                    }
                }
            }
        }
    }
}
