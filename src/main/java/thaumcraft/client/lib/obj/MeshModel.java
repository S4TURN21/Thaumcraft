package thaumcraft.client.lib.obj;

import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.client.model.IQuadTransformer.*;

public class MeshModel {
    public List<Vector3f> positions;
    public List<Vector3f> normals;
    public List<Vec2> texCoords;
    public List<MeshPart> parts;

    public MeshModel() {
        this.parts = new ArrayList<>();
    }

    public Vec2 copy(Vec2 v) {
        return new Vec2(v.x, v.y);
    }

    public MeshModel clone() {
        MeshModel mm = new MeshModel();
        mm.parts = new ArrayList<>();
        for (MeshPart mp : this.parts) {
            mm.parts.add(mp);
        }
        if (this.positions != null) {
            mm.positions = new ArrayList<Vector3f>();
            for (Vector3f mp2 : this.positions) {
                mm.positions.add(mp2.copy());
            }
        }
        if (this.normals != null) {
            mm.normals = new ArrayList<Vector3f>();
            for (Vector3f mp2 : this.normals) {
                mm.normals.add(mp2.copy());
            }
        }
        if (this.texCoords != null) {
            mm.texCoords = new ArrayList<>();
            for (Vec2 mp3 : this.texCoords) {
                mm.texCoords.add(copy(mp3));
            }
        }
        return mm;
    }

    public void rotate(double d, Vector3 axis, Vector3 offset) {
        Rotation r = new Rotation(d, axis);
        List<Vector3f> p = new ArrayList<Vector3f>();
        for (Vector3f v : this.positions) {
            Vector3 vec = new Vector3(v.x(), v.y(), v.z());
            r.apply(vec);
            vec = vec.add(offset);
            p.add(new Vector3f((float) vec.x, (float) vec.y, (float) vec.z));
        }
        this.positions = p;
    }

    public void addPosition(float x, float y, float z) {
        if (this.positions == null) {
            this.positions = new ArrayList<>();
        }
        this.positions.add(new Vector3f(x, y, z));
    }

    public void addNormal(float x, float y, float z) {
        if (this.normals == null) {
            this.normals = new ArrayList<>();
        }
        this.normals.add(new Vector3f(x, y, z));
    }

    public void addTexCoords(float x, float y) {
        if (this.texCoords == null) {
            this.texCoords = new ArrayList<>();
        }
        this.texCoords.add(new Vec2(x, y));
    }

    public void addPart(MeshPart part) {
        this.parts.add(part);
    }

    public List<BakedQuad> bakeModel(TextureAtlasSprite sprite) {
        List<BakedQuad> bakeList = new ArrayList<BakedQuad>();
        for (int j = 0; j < this.parts.size(); ++j) {
            MeshPart part = this.parts.get(j);
            int color = -1;
            for (int i = 0; i < part.indices.size(); i += 4) {
                BakedQuad quad = this.bakeQuad(part, i, sprite, color);
                bakeList.add(quad);
            }
        }
        return bakeList;
    }

    private BakedQuad bakeQuad(MeshPart part, int startIndex, TextureAtlasSprite sprite, int color) {
        int[] faceData = new int[STRIDE * 4];
        for (int i = 0; i < 4; ++i) {
            Vector3f position = new Vector3f(0.0f, 0.0f, 0.0f);
            Vec2 texCoord = new Vec2(0.0f, 0.0f);
            int p = 0;
            int[] indices = part.indices.get(startIndex + i);
            if (this.positions != null) {
                position = this.positions.get(indices[p++]);
            }
            if (this.normals != null) {
                ++p;
            }
            if (this.texCoords != null) {
                texCoord = this.texCoords.get(indices[p++]);
            }
            storeVertexData(faceData, i, position, texCoord, sprite, color);
        }
        return new BakedQuad(faceData, part.name.contains("focus") ? 1 : part.tintIndex, FaceBakery.calculateFacing(faceData), sprite, false);
    }

    private static void storeVertexData(int[] faceData, int vertexIndex, Vector3f position, Vec2 faceUV, TextureAtlasSprite sprite, int shadeColor) {
        int offset = vertexIndex * STRIDE + POSITION;
        faceData[offset] = Float.floatToRawIntBits(position.x());
        faceData[offset + 1] = Float.floatToRawIntBits(position.y());
        faceData[offset + 2] = Float.floatToRawIntBits(position.z());

        offset = vertexIndex * STRIDE + COLOR;
        faceData[offset] = shadeColor;

        offset = vertexIndex * STRIDE + UV0;
        if (sprite != null) {
            faceData[offset] = Float.floatToRawIntBits(sprite.getU(faceUV.x * 16.0f));
            faceData[offset + 1] = Float.floatToRawIntBits(sprite.getV(faceUV.y * 16.0f));
        } else {
            faceData[offset] = Float.floatToRawIntBits(faceUV.x);
            faceData[offset + 1] = Float.floatToRawIntBits(faceUV.y);
        }

        offset = vertexIndex * STRIDE + UV2;
        faceData[offset] = 0;
    }
}
