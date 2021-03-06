package com.darkhouse.gdefence.Model.Panels;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.darkhouse.gdefence.GDefence;
import com.darkhouse.gdefence.Helpers.AssetLoader;

public class ExpBar extends ProgressBar{
    public ExpBar(int width, int height, int x, int y) {
        this(width, height);
        setPosition(x, y);

    }

    public ExpBar(int width, int height) {
        super(0, GDefence.getInstance().user.getMaxExpThisLvl(), 0.5f, false, /*GDefence.getInstance().assetLoader.getExpBarSkin()*/ GDefence.getInstance().assetLoader.getSkin(), "exp-bar");

        //ProgressBar bar = new ProgressBar(0, 10, 0.5f, false, AssetLoader.getExpBarSkin());
        int expBarSize[] = {width, height};
        //setPosition(x, y);
        //setPosition(Gdx.graphics.getWidth() - expBarSize[0], userlevelButton.getY() - expBarSize[1] - 4);
        setSize(expBarSize[0], expBarSize[1]);
//        getStyle().knobBefore.setMinWidth(0.1f);
        getStyle().background.setMinHeight(height);
        getStyle().knobBefore.setMinHeight(height - 2);
        // bar.setAnimateDuration(5);

        //setValue(7.8f);
        setValue(GDefence.getInstance().user.getCurrentExp());

        pack();
//
//        System.out.println(GDefence.getInstance().user.getMaxExpThisLvl() + "|" + GDefence.getInstance().user.getCurrentExp());
//        System.out.println(getMaxValue() + "|" + getValue());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
//        setValue(GDefence.getInstance().user.getCurrentExp());
    }
    public void update(){

//        setValue(GDefence.getInstance().user.getCurrentExp());
//        System.out.println(GDefence.getInstance().user.getTotalExp());
//        System.out.println(getMaxValue() + "|" + getValue());
    }



}
