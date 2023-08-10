package thaumcraft.client.lib.obj;

import java.util.ArrayList;
import java.util.List;

public class MeshPart {
    public String name;
    public Material material;
    public List<int[]> indices;
    public int tintIndex;

    public MeshPart() {
        this.tintIndex = -1;
        this.indices = new ArrayList<int[]>();
    }

    public MeshPart(MeshPart p, int ti) {
        this.tintIndex = -1;
        this.indices = new ArrayList<int[]>();
        this.name = p.name;
        this.material = p.material;
        this.indices = p.indices;
        this.tintIndex = ti;
    }

    public void addTriangleFace(int[] a, int[] b, int[] c) {
        this.indices.add(a);
        this.indices.add(b);
        this.indices.add(c);
        this.indices.add(c);
    }

    public void addQuadFace(int[] a, int[] b, int[] c, int[] d) {
        this.indices.add(a);
        this.indices.add(b);
        this.indices.add(c);
        this.indices.add(d);
    }
}
