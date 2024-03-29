package thaumcraft.api.crafting;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ShapedArcaneRecipeBuilder implements RecipeBuilder {
    private final Item result;
    private final int count;
    private final List<String> rows = Lists.newArrayList();
    private final Map<Character, Ingredient> key = Maps.newLinkedHashMap();
    private final Advancement.Builder advancement = Advancement.Builder.advancement();
    @Nullable
    private String group;
    private String research;
    private int vis;
    private AspectList crystals;

    public ShapedArcaneRecipeBuilder(ItemLike pResult, int pCount) {
        this.result = pResult.asItem();
        this.count = pCount;
    }

    public static ShapedArcaneRecipeBuilder shaped(ItemLike pResult) {
        return shaped(pResult, 1);
    }

    public static ShapedArcaneRecipeBuilder shaped(ItemLike pResult, int pCount) {
        return new ShapedArcaneRecipeBuilder(pResult, pCount);
    }

    public ShapedArcaneRecipeBuilder research(String research) {
        this.research = research;
        return this;
    }

    public ShapedArcaneRecipeBuilder vis(int vis) {
        this.vis = vis;
        return this;
    }

    public ShapedArcaneRecipeBuilder crystals(AspectList crystals) {
        this.crystals = crystals;
        return this;
    }

    public ShapedArcaneRecipeBuilder define(Character pSymbol, ItemLike pItem) {
        return this.define(pSymbol, Ingredient.of(pItem));
    }

    public ShapedArcaneRecipeBuilder define(Character pSymbol, Ingredient pIngredient) {
        if (this.key.containsKey(pSymbol)) {
            throw new IllegalArgumentException("Symbol '" + pSymbol + "' is already defined!");
        } else if (pSymbol == ' ') {
            throw new IllegalArgumentException("Symbol ' ' (whitespace) is reserved and cannot be defined");
        } else {
            this.key.put(pSymbol, pIngredient);
            return this;
        }
    }

    public ShapedArcaneRecipeBuilder pattern(String pPattern) {
        if (!this.rows.isEmpty() && pPattern.length() != this.rows.get(0).length()) {
            throw new IllegalArgumentException("Pattern must be the same width on every line!");
        } else {
            this.rows.add(pPattern);
            return this;
        }
    }

    @Override
    public ShapedArcaneRecipeBuilder unlockedBy(String pCriterionName, CriterionTriggerInstance pCriterionTrigger) {
        this.advancement.addCriterion(pCriterionName, pCriterionTrigger);
        return this;
    }

    @Override
    public ShapedArcaneRecipeBuilder group(@Nullable String pGroupName) {
        this.group = pGroupName;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result;
    }

    @Override
    public void save(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        pFinishedRecipeConsumer.accept(new Result(pRecipeId, this.result, this.count, this.group == null ? "" : this.group, this.rows, this.key, this.advancement, new ResourceLocation(pRecipeId.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + pRecipeId.getPath()), this.research, this.vis, this.crystals));
    }

    public static class Result implements FinishedRecipe {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final String group;
        private final List<String> pattern;
        private final Map<Character, Ingredient> key;
        private final Advancement.Builder advancement;
        private final ResourceLocation advancementId;
        private final String research;
        private final int vis;
        private final AspectList crystals;

        public Result(ResourceLocation pId, Item pResult, int pCount, String pGroup, List<String> pPattern, Map<Character, Ingredient> pKey, Advancement.Builder pAdvancement, ResourceLocation pAdvancementId, String research, int vis, AspectList crystals) {
            this.id = pId;
            this.result = pResult;
            this.count = pCount;
            this.group = pGroup;
            this.pattern = pPattern;
            this.key = pKey;
            this.advancement = pAdvancement;
            this.advancementId = pAdvancementId;
            this.research = research;
            this.vis = vis;
            this.crystals = crystals;
        }

        @Override
        public void serializeRecipeData(JsonObject pJson) {
            if (!this.group.isEmpty()) {
                pJson.addProperty("group", this.group);
            }

            JsonArray jsonarray = new JsonArray();

            for (String s : this.pattern) {
                jsonarray.add(s);
            }

            pJson.add("pattern", jsonarray);
            JsonObject jsonobject = new JsonObject();

            for (Map.Entry<Character, Ingredient> entry : this.key.entrySet()) {
                jsonobject.add(String.valueOf(entry.getKey()), entry.getValue().toJson());
            }

            pJson.add("key", jsonobject);
            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.addProperty("item", Registry.ITEM.getKey(this.result).toString());
            if (this.count > 1) {
                jsonobject1.addProperty("count", this.count);
            }

            pJson.add("result", jsonobject1);

            pJson.addProperty("research", this.research);
            pJson.addProperty("vis", this.vis);
            JsonObject crystalsObject = new JsonObject();
            for (Aspect aspect : this.crystals.getAspects()) {
                crystalsObject.addProperty(aspect.getTag(),this.crystals.getAmount(aspect));
            }
            pJson.add("crystals", crystalsObject);
        }

        @Override
        public ResourceLocation getId() {
            return this.id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ForgeRegistries.RECIPE_SERIALIZERS.getValue(new ResourceLocation(Thaumcraft.MODID, "arcane_shaped"));
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return this.advancement.serializeToJson();
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return this.advancementId;
        }
    }
}
