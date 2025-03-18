package thaumcraft.api.crafting;

import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemsTC;

import java.util.Map;

public class ShapedArcaneRecipe extends ShapedRecipe implements IArcaneRecipe {
    private String research;
    private int vis;
    private AspectList crystals;

    public ShapedArcaneRecipe(ResourceLocation pId, String pGroup, String res, int vis, AspectList crystals, int pWidth, int pHeight, NonNullList<Ingredient> pRecipeItems, ItemStack pResult) {
        super(pId, pGroup, pWidth, pHeight, pRecipeItems, pResult);
        this.research = res;
        this.vis = vis;
        this.crystals = crystals;
    }

    @Override
    public boolean matches(CraftingContainer pInv, Level pLevel) {
        if (pInv.getContainerSize() < 15) {
            return false;
        }

        CraftingContainer dummy = new CraftingContainer(new ContainerDummy(), 3, 3);
        for (int a = 0; a < 9; a++) {
            dummy.setItem(a, pInv.getItem(a));
        }

        if (crystals != null && pInv.getContainerSize() >= 15) {
            for (Aspect aspect : crystals.getAspects()) {
                ItemStack cs = ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect));
                boolean b = false;
                for (int i = 0; i < 6; ++i) {
                    ItemStack itemstack1 = pInv.getItem(9 + i);
                    if (itemstack1 != null && itemstack1.getItem() == ItemsTC.crystalEssence && itemstack1.getCount() >= cs.getCount() && ItemStack.tagMatches(cs, itemstack1)) {
                        b = true;
                    }
                }
                if (!b) return false;
            }
        }

        return super.matches(dummy, pLevel);
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    @Override
    public int getVis() {
        return this.vis;
    }

    @Override
    public String getResearch() {
        return this.research;
    }

    @Override
    public AspectList getCrystals() {
        return this.crystals;
    }

    public static class Type implements RecipeType<ShapedArcaneRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final String ID = "arcane_shaped";
    }

    public static class Serializer implements RecipeSerializer<ShapedArcaneRecipe> {
        public static final ShapedArcaneRecipe.Serializer INSTANCE = new ShapedArcaneRecipe.Serializer();
        private static final ResourceLocation NAME = new ResourceLocation("thaumcraft", "arcane_shaped");

        @Override
        public ShapedArcaneRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
            String s = GsonHelper.getAsString(pJson, "group", "");
            Map<String, Ingredient> map = ShapedRecipe.keyFromJson(GsonHelper.getAsJsonObject(pJson, "key"));
            String[] astring = ShapedRecipe.shrink(ShapedRecipe.patternFromJson(GsonHelper.getAsJsonArray(pJson, "pattern")));
            int i = astring[0].length();
            int j = astring.length;

            NonNullList<Ingredient> nonnulllist = ShapedRecipe.dissolvePattern(astring, map, i, j);

            ItemStack itemstack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "result"));

            String res = GsonHelper.getAsString(pJson, "research", "");
            int vis = GsonHelper.getAsInt(pJson, "vis", 0);
            AspectList crystals = new AspectList();
            var crystalsObject = GsonHelper.getAsJsonObject(pJson, "crystals");
            var aspects = crystalsObject.keySet();
            for (var aspect : aspects) {
                crystals.add(Aspect.getAspect(aspect), GsonHelper.getAsInt(crystalsObject, aspect));
            }

            return new ShapedArcaneRecipe(pRecipeId, s, res, vis, crystals, i, j, nonnulllist, itemstack);
        }

        @Override
        public @Nullable ShapedArcaneRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            int i = pBuffer.readVarInt();
            int j = pBuffer.readVarInt();
            String s = pBuffer.readUtf();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);

            for (int k = 0; k < nonnulllist.size(); ++k) {
                nonnulllist.set(k, Ingredient.fromNetwork(pBuffer));
            }

            ItemStack itemstack = pBuffer.readItem();

            String res = pBuffer.readUtf();
            int vis = pBuffer.readVarInt();
            int count = pBuffer.readVarInt();

            AspectList crystals = new AspectList();

            for (int k = 0; k < count; k++) {
                String tag = pBuffer.readUtf();
                int amount = pBuffer.readVarInt();
                crystals.add(Aspect.getAspect(tag), amount);
            }

            return new ShapedArcaneRecipe(pRecipeId, s, res, vis, crystals, i, j, nonnulllist, itemstack);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, ShapedArcaneRecipe pRecipe) {
            pBuffer.writeVarInt(pRecipe.getRecipeWidth());
            pBuffer.writeVarInt(pRecipe.getRecipeHeight());
            pBuffer.writeUtf(pRecipe.getGroup());

            for (Ingredient ingredient : pRecipe.getIngredients()) {
                ingredient.toNetwork(pBuffer);
            }

            pBuffer.writeItem(pRecipe.getResultItem());

            pBuffer.writeUtf(pRecipe.research);
            pBuffer.writeVarInt(pRecipe.vis);
            pBuffer.writeVarInt(pRecipe.crystals.size());
            for (Aspect aspect : pRecipe.crystals.getAspects()) {
                pBuffer.writeUtf(aspect.getTag());
                pBuffer.writeVarInt(pRecipe.crystals.getAmount(aspect));
            }
        }
    }
}
