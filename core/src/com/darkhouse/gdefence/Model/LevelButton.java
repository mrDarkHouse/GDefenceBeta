package com.darkhouse.gdefence.Model;


import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.darkhouse.gdefence.GDefence;
import com.darkhouse.gdefence.Helpers.AssetLoader;
import com.darkhouse.gdefence.InventorySystem.inventory.LevelToolip;
import com.darkhouse.gdefence.InventorySystem.inventory.SlotTooltip;
import com.darkhouse.gdefence.InventorySystem.inventory.TooltipListener;
import com.darkhouse.gdefence.Screens.AbstractCampainScreen;
import com.darkhouse.gdefence.Screens.BottomPanel.Arsenal;
import com.darkhouse.gdefence.Screens.CampainMap;
import com.darkhouse.gdefence.Screens.LevelPreparationScreen;

public class LevelButton extends ImageButton {
    private int number;

    public int getNumber() {
        return number;
    }

    public boolean isLocked = false;

    public LevelButton(int number) {
        super(AssetLoader.getCampainLevelSkin(number));
        this.number = number;
        load();
    }

    private void load(){
        LevelToolip tooltip = new LevelToolip(this, AssetLoader.cellSkin);
        tooltip.setTouchable(Touchable.disabled);
        //CampainMap.getStage().addActor(tooltip);
        ((AbstractCampainScreen)GDefence.getInstance().getScreen()).getStage().addActor(tooltip);
        addListener(new TooltipListener(tooltip, true));


        addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!isLocked) {
                    GDefence.getInstance().setScreen(new LevelPreparationScreen(number));

                }
                return true;
            }
        });



    }






    public void lock(){
        isLocked = true;
        getStyle().up = new TextureRegionDrawable(AssetLoader.getLevelLockTexture(number));
        getStyle().over = new TextureRegionDrawable(AssetLoader.getLevelLockTexture(number));
        getStyle().down = new TextureRegionDrawable(AssetLoader.getLevelLockTexture(number));
    }
}
