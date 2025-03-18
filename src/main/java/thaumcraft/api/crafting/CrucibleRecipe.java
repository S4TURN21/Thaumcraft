package thaumcraft.api.crafting;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.container.CrucibleContainer;

public class CrucibleRecipe implements IThaumcraftRecipe, Recipe<CrucibleContainer> {
    private final ResourceLocation recipeId;
    private final String group;
    private final ItemStack recipeOutput;
    private final String research;
    private Ingredient catalyst;
    private AspectList aspects;

    public CrucibleRecipe(ResourceLocation pRecipeId, String pGroup, String researchKey, ItemStack result, Ingredient catalyst, AspectList tags) {
        this.recipeId = pRecipeId;
        this.group = pGroup;
        this.research = researchKey;
        this.catalyst = catalyst;
        this.aspects = tags;
        this.recipeOutput = result;
    }

    @Override
    public boolean matches(CrucibleContainer pContainer, @NotNull Level pLevel) {
        var cat = pContainer.getItem(0);
        var itags = pContainer.aspects;

        if (!getCatalyst().test(cat)) return false;

        if (itags == null) return false;

        for (Aspect tag : getAspects().getAspects()) {
            if (itags.getAmount(tag) < getAspects().getAmount(tag)) return false;
        }

        return true;
    }

    public AspectList removeMatching(AspectList itags) {
        AspectList temptags = new AspectList();
        temptags.aspects.putAll(itags.aspects);
        for (Aspect tag : getAspects().getAspects()) {
            temptags.remove(tag, getAspects().getAmount(tag));
        }
        itags = temptags;
        return itags;
    }

    @Override
    public @NotNull ItemStack getResultItem() {
        return this.recipeOutput;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CrucibleContainer pContainer) {
        return this.recipeOutput.copy();
    }

    @Override
    public String getResearch() {
        return this.research;
    }

    public Ingredient getCatalyst() {
        return this.catalyst;
    }

    public void setCatalyst(Ingredient catalyst) {
        this.catalyst = catalyst;
    }

    public AspectList getAspects() {
        return this.aspects;
    }

    public void setAspects(AspectList aspects) {
        this.aspects = aspects;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return false;
    }

    @Override
    public @NotNull String getGroup() {
        return this.group;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return this.recipeId;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<CrucibleRecipe> {
        private Type() {
        }

        public static final CrucibleRecipe.Type INSTANCE = new CrucibleRecipe.Type();
        public static final String ID = "crucible";
    }

    public static class Serializer implements RecipeSerializer<CrucibleRecipe> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public @NotNull CrucibleRecipe fromJson(@NotNull ResourceLocation pRecipeId, @NotNull JsonObject pJson) {
            String group = GsonHelper.getAsString(pJson, "group", "");
            String research = GsonHelper.getAsString(pJson, "research", "");
            Ingredient catalyst = Ingredient.fromJson(GsonHelper.getAsJsonObject(pJson, "catalyst"));
            ItemStack result = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(pJson, "result"), true, true);
            AspectList aspects = new AspectList();
            var aspectsObject = GsonHelper.getAsJsonObject(pJson, "aspects");
            for (var aspect : aspectsObject.keySet()) {
                aspects.add(Aspect.getAspect(aspect), GsonHelper.getAsInt(aspectsObject, aspect));
            }
            return new CrucibleRecipe(pRecipeId, group, research, result, catalyst, aspects);
        }

        @Override
        public @Nullable CrucibleRecipe fromNetwork(@NotNull ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            String group = pBuffer.readUtf();
            String research = pBuffer.readUtf();
            ItemStack result = pBuffer.readItem();
            Ingredient catalyst = Ingredient.fromNetwork(pBuffer);

            int count = pBuffer.readVarInt();
            AspectList tags = new AspectList();

            for (int i = 0; i < count; i++) {
                String tag = pBuffer.readUtf();
                int amount = pBuffer.readVarInt();
                tags.add(Aspect.getAspect(tag), amount);
            }

            return new CrucibleRecipe(pRecipeId, group, research, result, catalyst, tags);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, CrucibleRecipe pRecipe) {
            pBuffer.writeUtf(pRecipe.getGroup());
            pBuffer.writeUtf(pRecipe.getResearch());
            pBuffer.writeItem(pRecipe.getResultItem());
            pRecipe.getCatalyst().toNetwork(pBuffer);
            AspectList tags = pRecipe.getAspects();
            pBuffer.writeInt(tags.size());
            for (Aspect aspect : tags.getAspects()) {
                pBuffer.writeUtf(aspect.getTag());
                pBuffer.writeVarInt(tags.getAmount(aspect));
            }
        }
    }
}