package dev.insilicon.genim.Generators;

import java.util.UUID;

public class GeneratorInstance {

    private GeneratorTypes type;
    private int x,y,z;
    private UUID owner;

    public GeneratorInstance(GeneratorTypes type, int x, int y, int z, UUID owner) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.owner = owner;
    }

    public GeneratorTypes getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public UUID getOwner() {
        return owner;
    }
}
