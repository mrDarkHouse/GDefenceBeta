package com.darkhouse.gdefence.Screens.BottomPanel;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.darkhouse.gdefence.GDefence;
import com.darkhouse.gdefence.InventorySystem.inventory.InventoryActor;
import com.darkhouse.gdefence.InventorySystem.inventory.OverallInventory;
import com.darkhouse.gdefence.InventorySystem.inventory.SlotActor;
import com.darkhouse.gdefence.InventorySystem.inventory.Target.SlotTarget;
import com.darkhouse.gdefence.Model.Panels.TowerCraftPanel;
import com.darkhouse.gdefence.Screens.AbstractCampainScreen;
import com.darkhouse.gdefence.User;

public class Arsenal extends AbstractCampainScreen{
    private OverallInventory inventoryActor;
    private TowerMap towerMap;
    private TowerCraftPanel towerCraftPanel;

    public TowerMap getTowerMap() {
        return towerMap;
    }

    public Arsenal() {
        super("arsenal");
        //init();
    }

    @Override
    public void show() {
        super.show();
        towerMap.updateTypes();//
        inventoryActor.notifyListeners();

        GDefence.getInstance().user.save();
//        inventoryActor.remove();
//        inventoryActor = null;
//        inventoryActor = new InventoryActor(User.getTowerInventory(), new DragAndDrop(),
//                GDefence.getInstance().assetLoader.get("skins/uiskin.json", Skin.class));
//        stage.addActor(inventoryActor);
//        inventoryActor.init();

//        inventoryActor.setTowerInventory(User.getTowerInventory());
        //init();
    }

//    @Override
//    public void hide() {
//        super.hide();
//
//    }


    @Override
    public void hide() {
        towerCraftPanel.clearPanel();
    }

    public void init(){
        //Skin skin = LibgdxUtils.assets.get("skins/uiskin.json", Skin.class);
//        inventoryActor = new InventoryActor(User.getTowerInventory(), new DragAndDrop(),
//                GDefence.getInstance().assetLoader.get("skins/uiskin.json", Skin.class));
        inventoryActor = new OverallInventory();
        stage.addActor(inventoryActor);
        inventoryActor.init();
        inventoryActor.setPosition(100, 250);
        towerMap = new TowerMap(GDefence.getInstance().assetLoader.getSkin());
        GDefence.getInstance().user.setTowerMap(towerMap);
        stage.addActor(towerMap);
        final TextButton towerMapButton = new TextButton(GDefence.getInstance().assetLoader.getWord("tower_map"), GDefence.getInstance().assetLoader.getSkin(), "description");
        towerMapButton.setPosition(Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 100);
        towerMapButton.setSize(140, 40);
        towerMapButton.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                towerMap.setVisible(true);
                towerMap.toFront();
                return true;
            }
        });
        stage.addActor(towerMapButton);
        towerCraftPanel = new TowerCraftPanel(new DragAndDrop(), inventoryActor, GDefence.getInstance().assetLoader.getSkin());
        stage.addActor(towerCraftPanel);
        towerCraftPanel.init();

//        inventoryActor.addTarget(towerCraftPanel.getRecipeSlot());
//        inventoryActor.addSlotAsSourceTarget(towerCraftPanel.getDragAndDrop());


    }

//    @Override
//    public void resize(int width, int height) {
//        stage.getViewport().update(width, height, true);
//    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void render(float delta) {
        super.render(delta);
    }
}
