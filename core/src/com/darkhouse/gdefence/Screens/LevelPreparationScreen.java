package com.darkhouse.gdefence.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.darkhouse.gdefence.GDefence;
import com.darkhouse.gdefence.Helpers.AssetLoader;
import com.darkhouse.gdefence.InventorySystem.inventory.InventoryActor;
import com.darkhouse.gdefence.User;

public class LevelPreparationScreen extends AbstractCampainScreen{
    private int level;
    private InventoryActor inventoryActor;

    public LevelPreparationScreen(int level) {
        super("" + level);
        this.level = level;

    }

    @Override
    public void show() {
        super.show();
        load(level);
    }

    private void load(final int level){
        TextButton startButton = new TextButton("Start", AssetLoader.getSkin());
        startButton.setSize(150, 70);
        startButton.setPosition(Gdx.graphics.getWidth() - 200, 30);
        startButton.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                GDefence.getInstance().setScreen(new LevelMap(level));
                return true;
            }
        });
        stage.addActor(startButton);

        inventoryActor = new InventoryActor(User.getInventory(), new DragAndDrop(), AssetLoader.cellSkin);
        stage.addActor(inventoryActor);
        inventoryActor.setPosition(100, 50);


    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }
}
