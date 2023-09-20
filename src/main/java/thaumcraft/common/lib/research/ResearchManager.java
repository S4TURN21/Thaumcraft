package thaumcraft.common.lib.research;

import com.google.gson.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import thaumcraft.Thaumcraft;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

public class ResearchManager {
    public static boolean doesPlayerHaveRequisites(Player player, String key) {
        ResearchEntry ri = ResearchCategories.getResearch(key);
        if (ri == null) {
            return true;
        }
        String[] parents = ri.getParentsStripped();
        return parents == null || ThaumcraftCapabilities.knowsResearchStrict(player, parents);
    }

    public static void parseAllResearch() {
        JsonParser parser = new JsonParser();
        for (ResourceLocation loc : CommonInternals.jsonLocs.values()) {
            String s = "/assets/" + loc.getNamespace() + "/" + loc.getPath();
            if (!s.endsWith(".json")) {
                s += ".json";
            }
            InputStream stream = ResearchManager.class.getResourceAsStream(s);
            if (stream != null) {
                try {
                    InputStreamReader reader = new InputStreamReader(stream);
                    JsonObject obj = parser.parse((Reader) reader).getAsJsonObject();
                    JsonArray entries = obj.get("entries").getAsJsonArray();
                    for (JsonElement element : entries) {
                        try {
                            JsonObject entry = element.getAsJsonObject();
                            ResearchEntry researchEntry = parseResearchJson(entry);
                            addResearchToCategory(researchEntry);
                        } catch (Exception e) {

                        }
                    }
                } catch (Exception e) {
                    Thaumcraft.log.warn("Invalid research file: " + loc.toString());
                }
            }
        }
    }

    private static ResearchEntry parseResearchJson(JsonObject obj) throws Exception {
        ResearchEntry entry = new ResearchEntry();
        entry.setKey(obj.getAsJsonPrimitive("key").getAsString());
        if (entry.getKey() == null) {
            throw new Exception("Invalid key in research JSon");
        }
        entry.setName(obj.getAsJsonPrimitive("name").getAsString());
        entry.setCategory(obj.getAsJsonPrimitive("category").getAsString());
        if (entry.getCategory() == null) {
            throw new Exception("Invalid category in research JSon");
        }
        if (obj.has("icons")) {
            String[] icons = arrayJsonToString(obj.get("icons").getAsJsonArray());
            if (icons != null && icons.length > 0) {
                Object[] ir = new Object[icons.length];
                for (int a = 0; a < icons.length; ++a) {
                    ItemStack stack = parseJSONtoItemStack(icons[a]);
                    if (stack != null && !stack.isEmpty()) {
                        ir[a] = stack;
                    } else if (icons[a].startsWith("focus")) {
                        ir[a] = icons[a];
                    } else {
                        ir[a] = new ResourceLocation(icons[a]);
                    }
                }
                entry.setIcons(ir);
            }
        }
        if (obj.has("parents")) {
            entry.setParents(arrayJsonToString(obj.get("parents").getAsJsonArray()));
        }
        if (obj.has("siblings")) {
            entry.setSiblings(arrayJsonToString(obj.get("siblings").getAsJsonArray()));
        }
        if (obj.has("meta")) {
            String[] meta = arrayJsonToString(obj.get("meta").getAsJsonArray());
            if (meta != null && meta.length > 0) {
                ArrayList<ResearchEntry.EnumResearchMeta> metas = new ArrayList<ResearchEntry.EnumResearchMeta>();
                for (String s : meta) {
                    ResearchEntry.EnumResearchMeta en = ResearchEntry.EnumResearchMeta.valueOf(s.toUpperCase());
                    if (en == null) {
                        throw new Exception("Illegal metadata in research JSon");
                    }
                    metas.add(en);
                }
                entry.setMeta(metas.toArray(new ResearchEntry.EnumResearchMeta[metas.size()]));
            }
        }
        if (obj.has("location")) {
            final Integer[] location = arrayJsonToInt(obj.get("location").getAsJsonArray());
            if (location != null && location.length == 2) {
                entry.setDisplayColumn(location[0]);
                entry.setDisplayRow(location[1]);
            }
        }
        JsonArray stagesJson = obj.get("stages").getAsJsonArray();
        ArrayList<ResearchStage> stages = new ArrayList<ResearchStage>();
        for (JsonElement element : stagesJson) {
            JsonObject stageObj = element.getAsJsonObject();
            ResearchStage stage = new ResearchStage();
            stage.setText(stageObj.getAsJsonPrimitive("text").getAsString());
            if (stage.getText() == null) {
                throw new Exception("Illegal stage text in research JSon");
            }
            stages.add(stage);
        }
        if (stages.size() > 0) {
            entry.setStages(stages.toArray(new ResearchStage[stages.size()]));
        }
        return entry;
    }

    private static String[] arrayJsonToString(final JsonArray jsonArray) {
        final ArrayList<String> out = new ArrayList<String>();
        for (final JsonElement element : jsonArray) {
            out.add(element.getAsString());
        }
        return (String[]) ((out.size() == 0) ? null : ((String[]) out.toArray(new String[out.size()])));
    }

    private static Integer[] arrayJsonToInt(final JsonArray jsonArray) {
        final ArrayList<Integer> out = new ArrayList<Integer>();
        for (final JsonElement element : jsonArray) {
            out.add(element.getAsInt());
        }
        return (Integer[]) ((out.size() == 0) ? null : ((Integer[]) out.toArray(new Integer[out.size()])));
    }

    public static ItemStack parseJSONtoItemStack(String entry) {
        if (entry == null) {
            return null;
        }
        String[] split = entry.split(";");
        String name = split[0];
        int num = -1;
        int dam = -1;
        String nbt = null;
        for (int a = 1; a < split.length; ++a) {
            if (split[a].startsWith("{")) {
                nbt = split[a];
                nbt.replaceAll("'", "\"");
                break;
            }
            int q = -1;
            try {
                q = Integer.parseInt(split[a]);
            } catch (NumberFormatException e) {
                continue;
            }
            if (q >= 0 && num < 0) {
                num = q;
            } else if (q >= 0 && dam < 0) {
                dam = q;
            }
        }
        if (num < 0) {
            num = 1;
        }
        if (dam < 0) {
            dam = 0;
        }
        ItemStack stack = ItemStack.EMPTY;
        try {

            Item it = Registry.ITEM.get(new ResourceLocation(name));
            if (it != null) {
                stack = new ItemStack(it, num);
//                if (nbt != null) {
//                    Tag.TagType.
//                    stack.setTag(Gson JsonToNBT.getTagFromJson(nbt));
//                }
            }
        } catch (Exception ex) {
        }
        return stack;
    }

    private static void addResearchToCategory(ResearchEntry ri) {
        ResearchCategory rl = ResearchCategories.getResearchCategory(ri.getCategory());
        rl.research.put(ri.getKey(), ri);
    }
}
