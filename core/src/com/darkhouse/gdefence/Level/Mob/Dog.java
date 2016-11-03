package com.darkhouse.gdefence.Level.Mob;


import com.darkhouse.gdefence.Helpers.AssetLoader;
import com.darkhouse.gdefence.Level.MapTile;

public class Dog extends Mob{
    public Dog() {
        super();
        setName("Dog");
        setHealth(50);
        setArmor(1);
        setDmg(2);
        setBounty(4);
        setMoveType(MapTile.TileType.ground);
        setSpeed(100);
        setTextureDrawable(AssetLoader.dog);

    }
}
