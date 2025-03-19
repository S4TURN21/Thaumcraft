package thaumcraft.api.crafting;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.util.function.Consumer;

public class CrucibleRecipeBuilder implements RecipeBuilder {
    private final Item result;
    private final int count;
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    private String research;
    private Ingredient catalyst;
    private AspectList aspects;
    private String group;

    public CrucibleRecipeBuilder(ItemLike pResult, int pCount) {
        this.result = pResult.asItem();
        this.count = pCount;
    }

    public static CrucibleRecipeBuilder smelting(ItemLike pResult) {
        return smelting(pResult, 1);
    }

    public static CrucibleRecipeBuilder smelting(ItemLike pResult, int pCount) {
        return new CrucibleRecipeBuilder(pResult, pCount);
    }

    public CrucibleRecipeBuilder research(String pResearch) {
        this.research = pResearch;
        return this;
    }

    public CrucibleRecipeBuilder catalyst(TagKey<Item> pCatalyst) {
        this.catalyst = Ingredient.of(pCatalyst);
        return this;
    }

    public CrucibleRecipeBuilder catalyst(ItemLike pCatalyst) {
        this.catalyst = Ingredient.of(pCatalyst);
        return this;
    }

    public CrucibleRecipeBuilder aspects(AspectList pAspects) {
        this.aspects = pAspects;
        return this;
    }

    @Override
    public CrucibleRecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        return this;
    }

    @Override
    public CrucibleRecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result;
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        pFinishedRecipeConsumer.accept(new Result(pRecipeId, this.result, this.count, this.group == null ? "" : this.group, this.research, this.catalyst, this.aspects, this.advancement, new ResourceLocation(pRecipeId.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + pRecipeId.getPath())));
    }

    public static class Result implements FinishedRecipe {
        private final String group;
        private ResourceLocation id;
        private final Item result;
        private final int count;
        private final String research;
        private final Ingredient catalyst;
        private final AspectList aspects;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;

        public Result(ResourceLocation pId, Item pResult, int pCount, String pGroup, String pResearch, Ingredient pCatalyst, AspectList pAspects, Advancement.Builder pAdvancement, ResourceLocation pAdvancementId) {
            this.id = pId;
            this.result = pResult;
            this.count = pCount;
            this.group = pGroup;
            this.research = pResearch;
            this.catalyst = pCatalyst;
            this.aspects = pAspects;
            this.advancement = pAdvancement;
            this.advancementId = pAdvancementId;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            if (!this.group.isEmpty()) {
                pJson.addProperty("group", this.group);
            }

            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.addProperty("item", Registry.ITEM.getKey(this.result).toString());
            if (this.count > 1) {
                jsonobject1.addProperty("count", this.count);
            }
            pJson.add("result", jsonobject1);

            pJson.addProperty("research", this.research);

            JsonObject crystalsObject = new JsonObject();
            for (Aspect aspect : this.aspects.getAspects()) {
                crystalsObject.addProperty(aspect.getTag(), this.aspects.getAmount(aspect));
            }
            pJson.add("aspects", crystalsObject);
            pJson.add("catalyst", this.catalyst.toJson());
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public @NotNull RecipeSerializer<?> getType() {
            return CrucibleRecipe.Serializer.INSTANCE;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return advancement.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return advancementId;
        }
    }
}
