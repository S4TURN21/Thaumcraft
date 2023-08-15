package thaumcraft.common.lib.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.resources.ItemCrystalEssence;

import java.util.ArrayList;

public class RecipeMagicDust extends ShapelessRecipe {
    private final ResourceLocation id;
    private final String group;
    private final ItemStack result;
    private final NonNullList<Ingredient> ingredients;

    public RecipeMagicDust(ResourceLocation pId, String pGroup, ItemStack pResult, NonNullList<Ingredient> pIngredients) {
        super(pId, pGroup, pResult, pIngredients);

        this.id = pId;
        this.group = pGroup;
        this.result = pResult;
        this.ingredients = pIngredients;
    }

    @Override
    public boolean matches(CraftingContainer pInv, Level pLevel) {
        boolean bowl = false;
        boolean flint = false;
        boolean redstone = false;
        final ArrayList<String> crystals = new ArrayList<String>();
        for (int i = 0; i < pInv.getContainerSize(); ++i) {
            if (!pInv.getItem(i).isEmpty()) {
                final ItemStack stack = pInv.getItem(i).copy();
                if (stack.getItem() == Items.BOWL && bowl) {
                    return false;
                }
                if (stack.getItem() == Items.BOWL && !bowl) {
                    bowl = true;
                } else {
                    if (stack.getItem() == Items.FLINT && flint) {
                        return false;
                    }
                    if (stack.getItem() == Items.FLINT && !flint) {
                        flint = true;
                    } else {
                        if (stack.getItem() == Items.REDSTONE && redstone) {
                            return false;
                        }
                        if (stack.getItem() == Items.REDSTONE && !redstone) {
                            redstone = true;
                        } else {
                            if (stack.getItem() != ItemsTC.crystalEssence) {
                                return false;
                            }
                            ItemCrystalEssence ice = (ItemCrystalEssence) stack.getItem();
                            if (crystals.contains(ice.getAspects(stack).getAspects()[0].getTag()) || crystals.size() >= 3) {
                                return false;
                            }
                            crystals.add(ice.getAspects(stack).getAspects()[0].getTag());
                        }
                    }
                }
            }
        }
        return bowl && redstone && flint && crystals.size() == 3;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingContainer pContainer) {
        return new ItemStack(ItemsTC.salisMundus);
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth * pHeight >= 6;
    }

    @Override
    public ItemStack getResultItem() {
        return new ItemStack(ItemsTC.salisMundus);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer pContainer) {
        NonNullList<ItemStack> ret = NonNullList.withSize(pContainer.getContainerSize(), ItemStack.EMPTY);
        for (int i = 0; i < ret.size(); ++i) {
            ItemStack itemstack = pContainer.getItem(i);
            if (!itemstack.isEmpty() && (itemstack.getItem() == Items.FLINT || itemstack.getItem() == Items.BOWL)) {
                ItemStack is = itemstack.copy();
                is.setCount(1);
                itemstack = is;
            }
            ret.set(i, itemstack);
        }
        return ret;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements RecipeSerializer<RecipeMagicDust> {
        public static final Serializer INSTANCE = new Serializer();

        private static NonNullList<Ingredient> itemsFromJson(JsonArray pIngredientArray) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();

            for (int i = 0; i < pIngredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(pIngredientArray.get(i));
                if (true || !ingredient.isEmpty()) {
                    nonnulllist.add(ingredient);
                }
            }

            return nonnulllist;
        }

        public RecipeMagicDust fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
            String s = GsonHelper.getAsString(pJson, "group", "");
            NonNullList<Ingredient> nonnulllist = itemsFromJson(GsonHelper.getAsJsonArray(pJson, "ingredients"));
            if (nonnulllist.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless recipe");
            } else {
                ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "result"));
                return new RecipeMagicDust(pRecipeId, s, itemstack, nonnulllist);
            }
        }

        public RecipeMagicDust fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            String s = pBuffer.readUtf();
            int i = pBuffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);

            for (int j = 0; j < nonnulllist.size(); ++j) {
                nonnulllist.set(j, Ingredient.fromNetwork(pBuffer));
            }

            ItemStack itemstack = pBuffer.readItem();
            return new RecipeMagicDust(pRecipeId, s, itemstack, nonnulllist);
        }

        public void toNetwork(FriendlyByteBuf pBuffer, RecipeMagicDust pRecipe) {
            pBuffer.writeUtf(pRecipe.group);
            pBuffer.writeVarInt(pRecipe.ingredients.size());

            for (Ingredient ingredient : pRecipe.ingredients) {
                ingredient.toNetwork(pBuffer);
            }

            pBuffer.writeItem(pRecipe.result);
        }
    }
}
