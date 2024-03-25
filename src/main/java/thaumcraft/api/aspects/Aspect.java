package thaumcraft.api.aspects;

import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.text.WordUtils;
import thaumcraft.api.research.ScanAspect;
import thaumcraft.api.research.ScanningManager;

import java.util.LinkedHashMap;

public class Aspect {
    public static LinkedHashMap<String, Aspect> aspects = new LinkedHashMap<String, Aspect>();

    public static final Aspect AIR = new Aspect("aer", 0XFFFF7E);
    public static final Aspect EARTH = new Aspect("terra", 0X56C000);
    public static final Aspect FIRE = new Aspect("ignis", 0XFF5A01);
    public static final Aspect WATER = new Aspect("aqua", 0X3CD4FC);
    public static final Aspect ORDER = new Aspect("ordo", 0XD5D4EC);
    public static final Aspect ENTROPY = new Aspect("perditio", 0X404040);
    public static final Aspect VOID = new Aspect("vacuos", 0X888888, new Aspect[]{Aspect.AIR, Aspect.ENTROPY});
    public static final Aspect LIGHT = new Aspect("lux", 0XFFFFC0, new Aspect[]{Aspect.AIR, Aspect.FIRE});
    public static final Aspect MOTION = new Aspect("motus", 0XCDCCF4, new Aspect[]{Aspect.AIR, Aspect.ORDER});
    public static final Aspect COLD = new Aspect("gelum", 0XE1FFFF, new Aspect[]{Aspect.FIRE, Aspect.ENTROPY});
    public static final Aspect CRYSTAL = new Aspect("vitreus", 0X80FFFF, new Aspect[]{Aspect.EARTH, Aspect.AIR});
    public static final Aspect METAL = new Aspect("metallum", 0XB5B5CD, new Aspect[]{Aspect.EARTH, Aspect.ORDER});
    public static final Aspect LIFE = new Aspect("victus", 0XDE0005, new Aspect[]{Aspect.EARTH, Aspect.WATER});
    public static final Aspect DEATH = new Aspect("mortuus", 0X6A0005, new Aspect[]{Aspect.WATER, Aspect.ENTROPY});
    public static final Aspect ENERGY = new Aspect("potentia", 0XC0FFFF, new Aspect[]{Aspect.ORDER, Aspect.FIRE});
    public static final Aspect EXCHANGE = new Aspect("permutatio", 0X578357, new Aspect[]{Aspect.ENTROPY, Aspect.ORDER});
    public static final Aspect MAGIC = new Aspect("praecantatio", 0XCF00FF, new Aspect[]{Aspect.ENERGY, Aspect.AIR});
    public static final Aspect AURA = new Aspect("auram", 0XFFC0FF, new Aspect[]{Aspect.MAGIC, Aspect.AIR});
    public static final Aspect ALCHEMY = new Aspect("alkimia", 0X23AC9D, new Aspect[]{Aspect.MAGIC, Aspect.WATER});
    public static final Aspect FLUX = new Aspect("vitium", 0X800080, new Aspect[]{Aspect.ENTROPY, Aspect.MAGIC});
    public static final Aspect DARKNESS = new Aspect("tenebrae", 0X222222, new Aspect[]{Aspect.VOID, Aspect.LIGHT});
    public static final Aspect ELDRITCH = new Aspect("alienis", 0X805080, new Aspect[]{Aspect.VOID, Aspect.DARKNESS});
    public static final Aspect FLIGHT = new Aspect("volatus", 0XE7E7D7, new Aspect[]{Aspect.AIR, Aspect.MOTION});
    public static final Aspect PLANT = new Aspect("herba", 0X1AC00, new Aspect[]{Aspect.LIFE, Aspect.EARTH});
    public static final Aspect TOOL = new Aspect("instrumentum", 0X4040EE, new Aspect[]{Aspect.METAL, Aspect.ENERGY});
    public static final Aspect CRAFT = new Aspect("fabrico", 0X809D80, new Aspect[]{Aspect.EXCHANGE, Aspect.TOOL});
    public static final Aspect MECHANISM = new Aspect("machina", 0X8080A0, new Aspect[]{Aspect.MOTION, Aspect.TOOL});
    public static final Aspect TRAP = new Aspect("vinculum", 0X9A8080, new Aspect[]{Aspect.MOTION, Aspect.ENTROPY});
    public static final Aspect SOUL = new Aspect("spiritus", 0XEBEBFB, new Aspect[]{Aspect.LIFE, Aspect.DEATH});
    public static final Aspect MIND = new Aspect("cognitio", 0XF9967F, new Aspect[]{Aspect.FIRE, Aspect.SOUL});
    public static final Aspect SENSES = new Aspect("sensus", 0XC0FFC0, new Aspect[]{Aspect.AIR, Aspect.SOUL});
    public static final Aspect AVERSION = new Aspect("aversio", 0XC05050, new Aspect[]{Aspect.SOUL, Aspect.ENTROPY});
    public static final Aspect PROTECT = new Aspect("praemunio", 0XC0C0, new Aspect[]{Aspect.SOUL, Aspect.EARTH});
    public static final Aspect DESIRE = new Aspect("desiderium", 0XE6BE44, new Aspect[]{Aspect.SOUL, Aspect.VOID});
    public static final Aspect UNDEAD = new Aspect("exanimis", 0X3A4000, new Aspect[]{Aspect.MOTION, Aspect.DEATH});
    public static final Aspect BEAST = new Aspect("bestia", 0X9F6409, new Aspect[]{Aspect.MOTION, Aspect.LIFE});
    public static final Aspect MAN = new Aspect("humanus", 0XFFD7C0, new Aspect[]{Aspect.SOUL, Aspect.LIFE});

    String tag;
    Aspect[] components;
    int color;
    ResourceLocation image;

    public Aspect(String tag, int color, Aspect[] components, ResourceLocation image) {
        if (Aspect.aspects.containsKey(tag)) {
            throw new IllegalArgumentException(tag + " already registered!");
        }
        this.tag = tag;
        this.components = components;
        this.color = color;
        this.image = image;
        Aspect.aspects.put(tag, this);
        ScanningManager.addScannableThing(new ScanAspect("!"+tag,this));
    }

    public Aspect(String tag, int color, Aspect[] components) {
        this(tag, color, components, new ResourceLocation("thaumcraft", "textures/aspects/" + tag.toLowerCase() + ".png"));
    }

    public Aspect(String tag, int color) {
        this(tag, color, (Aspect[]) null);
    }

    public int getColor() {
        return this.color;
    }

    public String getName() {
        return WordUtils.capitalizeFully(this.tag);
    }

    public String getTag() {
        return this.tag;
    }

    public Aspect[] getComponents() {
        return components;
    }

    public static Aspect getAspect(String tag) {
        return Aspect.aspects.get(tag);
    }

    public boolean isPrimal() {
        return getComponents() == null || getComponents().length != 2;
    }
}
