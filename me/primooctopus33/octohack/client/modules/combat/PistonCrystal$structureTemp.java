package me.primooctopus33.octohack.client.modules.combat;

import java.util.List;
import net.minecraft.util.math.Vec3d;

class PistonCrystal$structureTemp {
    public double distance;
    public int supportBlock;
    public List<Vec3d> to_place;
    public int direction;
    public float offsetX;
    public float offsetZ;

    public PistonCrystal$structureTemp(double distance, int supportBlock, List<Vec3d> to_place) {
        this.distance = distance;
        this.supportBlock = supportBlock;
        this.to_place = to_place;
        this.direction = -1;
    }

    public void replaceValues(double distance, int supportBlock, List<Vec3d> to_place, int direction, float offsetX, float offsetZ) {
        this.distance = distance;
        this.supportBlock = supportBlock;
        this.to_place = to_place;
        this.direction = direction;
        this.offsetX = offsetX;
        this.offsetZ = offsetZ;
    }
}
