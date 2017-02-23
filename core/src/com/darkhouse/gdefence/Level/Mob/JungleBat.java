package com.darkhouse.gdefence.Level.Mob;


import com.badlogic.gdx.graphics.Texture;
import com.darkhouse.gdefence.GDefence;
import com.darkhouse.gdefence.Level.Path.MapTile;

public class JungleBat extends Mob{
    public JungleBat() {
        super();
        setName("Jungle Bat");
        setHealth(85);
        setArmor(2);
        setDmg(3);
        setBounty(3);
        setMoveType(MoveType.ground);
        setSpeed(110);
//        setTextureDrawable(GDefence.getInstance().assetLoader.get("Mobs/mob4.png", Texture.class));
        setRegion(GDefence.getInstance().assetLoader.get("Mobs/mob4.png", Texture.class));
    }

    @Override
    public void update() {

    }
}
