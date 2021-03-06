package com.darkhouse.gdefence.Model.Level;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.darkhouse.gdefence.GDefence;
import com.darkhouse.gdefence.Helpers.AssetLoader;
import com.darkhouse.gdefence.Level.Mob.Mob;
import com.darkhouse.gdefence.Level.Wave;
import com.darkhouse.gdefence.Screens.LevelMap;

public class CurrentWaveInfoPanel extends Table{
    private Label currWaveL;
    private Label mobsCount;// = new Label("Mobs left: " + Wave.mobs.size, GDefence.getInstance().assetLoader.getCurrentInfoPanelSkin());
    private NextWaveTimer timer;
    private String mobsLeft;
    private String currWave;


    public CurrentWaveInfoPanel(NextWaveTimer t) {
        this.timer = t;
        AssetLoader l = GDefence.getInstance().assetLoader;
        mobsLeft = l.getWord("mobsLeft");
        currWave = l.getWord("currentWave") + " ";//TODO same for multiwave levels;
        mobsCount = new Label(mobsLeft + ": " + Wave.mobs.size, GDefence.getInstance().assetLoader.getCurrentInfoPanelSkin());
        currWaveL = new Label(currWave, GDefence.getInstance().assetLoader.getCurrentInfoPanelSkin());
        init();
    }

    private void init() {
        pad(20);
        setBackground(GDefence.getInstance().assetLoader.getSkin().getDrawable("info-panel"));
        add(currWaveL).row();
        add(mobsCount);

        pack();


    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
//        if(timer.getTimeLimit() <= 0) {
            super.draw(batch, parentAlpha);
//        }
    }


    @Override
    public void act(float delta) {
        super.act(delta);
        currWaveL.setText(currWave + (LevelMap.getLevel().currentWave + 1) + "/" + LevelMap.getLevel().numberWaves);
        mobsCount.setText(mobsLeft + ": " + (LevelMap.getLevel().getCurrentWave().getMobsToSpawn().size + Wave.mobs.size));

    }
}
