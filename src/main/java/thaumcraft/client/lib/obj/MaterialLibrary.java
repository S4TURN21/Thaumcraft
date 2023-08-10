package thaumcraft.client.lib.obj;

import com.google.common.base.Charsets;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class MaterialLibrary extends Dictionary<String, Material> {
    static Set<String> unknownCommands = new HashSet<>();
    private final Dictionary<String, Material> materialLibrary;
    private Material currentMaterial;

    public MaterialLibrary() {
        this.materialLibrary = new Hashtable<>();
    }

    @Override
    public int size() {
        return this.materialLibrary.size();
    }

    @Override
    public boolean isEmpty() {
        return this.materialLibrary.isEmpty();
    }

    @Override
    public Enumeration<String> keys() {
        return this.materialLibrary.keys();
    }

    @Override
    public Enumeration<Material> elements() {
        return this.materialLibrary.elements();
    }

    @Override
    public Material get(Object key) {
        return this.materialLibrary.get(key);
    }

    @Override
    public Material put(String key, Material value) {
        return this.materialLibrary.put(key, value);
    }

    @Override
    public Material remove(Object key) {
        return this.materialLibrary.remove(key);
    }

    public void loadFromStream(ResourceLocation loc) throws IOException {
        Resource res = Minecraft.getInstance().getResourceManager().getResource(loc).orElseThrow();
        InputStreamReader lineStream = new InputStreamReader(res.open(), Charsets.UTF_8);
        BufferedReader lineReader = new BufferedReader(lineStream);
        while (true) {
            String currentLine = lineReader.readLine();
            if (currentLine == null) {
                break;
            }
            if (currentLine.length() == 0) {
                continue;
            }
            if (currentLine.startsWith("#")) {
                continue;
            }
            String[] fields = currentLine.strip().split(" ", 2);
            String keyword = fields[0];
            String data = fields[1];
            if (keyword.equalsIgnoreCase("newmtl")) {
                this.pushMaterial(data);
            } else if (keyword.equalsIgnoreCase("Ka")) {
                this.currentMaterial.AmbientColor = this.parseVector3f(data);
            } else if (keyword.equalsIgnoreCase("Kd")) {
                this.currentMaterial.DiffuseColor = this.parseVector3f(data);
            } else if (keyword.equalsIgnoreCase("Ks")) {
                this.currentMaterial.SpecularColor = this.parseVector3f(data);
            } else if (keyword.equalsIgnoreCase("Ns")) {
                this.currentMaterial.SpecularCoefficient = this.parseFloat(data);
            } else if (keyword.equalsIgnoreCase("Tr")) {
                this.currentMaterial.Transparency = this.parseFloat(data);
            } else if (keyword.equalsIgnoreCase("illum")) {
                this.currentMaterial.IlluminationModel = this.parseInt(data);
            } else if (keyword.equalsIgnoreCase("map_Ka")) {
                this.currentMaterial.AmbientTextureMap = data;
            } else if (keyword.equalsIgnoreCase("map_Kd")) {
                this.currentMaterial.DiffuseTextureMap = data;
            } else if (keyword.equalsIgnoreCase("map_Ks")) {
                this.currentMaterial.SpecularTextureMap = data;
            } else if (keyword.equalsIgnoreCase("map_Ns")) {
                this.currentMaterial.SpecularHighlightTextureMap = data;
            } else if (keyword.equalsIgnoreCase("map_d")) {
                this.currentMaterial.AlphaTextureMap = data;
            } else if (keyword.equalsIgnoreCase("map_bump")) {
                this.currentMaterial.BumpMap = data;
            } else if (keyword.equalsIgnoreCase("bump")) {
                this.currentMaterial.BumpMap = data;
            } else if (keyword.equalsIgnoreCase("disp")) {
                this.currentMaterial.DisplacementMap = data;
            } else if (keyword.equalsIgnoreCase("decal")) {
                this.currentMaterial.StencilDecalMap = data;
            } else {
                if (MaterialLibrary.unknownCommands.contains(keyword)) {
                    continue;
                }
                MaterialLibrary.unknownCommands.add(keyword);
            }
        }
    }

    private float parseFloat(String data) {
        return Float.parseFloat(data);
    }

    private int parseInt(String data) {
        return Integer.parseInt(data);
    }

    private Vector3f parseVector3f(String data) {
        String[] parts = data.split(" ");
        return new Vector3f(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]));
    }

    private void pushMaterial(String materialName) {
        this.currentMaterial = new Material(materialName);
        this.materialLibrary.put(this.currentMaterial.Name, this.currentMaterial);
    }
}
