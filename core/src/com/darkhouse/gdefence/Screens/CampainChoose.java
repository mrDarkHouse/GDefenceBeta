package com.darkhouse.gdefence.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.darkhouse.gdefence.GDefence;
import com.darkhouse.gdefence.Helpers.AssetLoader;
import com.darkhouse.gdefence.User;

public class CampainChoose extends AbstractMenuScreen{
    //private GDefence mainclass;
    //private TextButton newCamp;
    //private TextButton loadCamp;

    public CampainChoose(GDefence mainClass) {
        super(mainClass, true);
        //this.mainclass = mainclass;
        loadButtons();
    }

    private void loadButtons(){
        Table table = new Table();
        //ImageButton backButton = new ImageButton();
        TextButton newCamp = new TextButton("New Campain", mainClass.getSkin());
        newCamp.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                newCampDialog();
                //mainclass.setScreen(new CampainMap(mainclass));
                return true;
            }
        });
        //newCamp.setSize();
        TextButton loadCamp = new TextButton("Load Campain", mainClass.getSkin());
        loadCamp.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                //mainClass.user = new User();
                if(mainClass.user.load()) {
                    mainClass.setScreen(new CampainMap(mainClass));
                }
                return true;
            }
        });

        table.setWidth(stage.getWidth());
        table.align(Align.center|Align.top);
        table.setPosition(0, Gdx.graphics.getHeight());


        table.padTop(200);
        table.add(newCamp).width(200).height(50).spaceBottom(30).row();
        table.add(loadCamp).width(200).height(50).row();
        //table.bottom();


        stage.addActor(table);
    }

    private void newCampDialog(){
        Dialog d = new Dialog("", AssetLoader.uiSkin){

            {
                text("Are you sure to start new campain?").padTop(250);
                button("Yes", true);
                button("No", false);
                //getButtonTable();//defaults().width(200);
                Array<Cell> cells = getButtonTable().getCells();
                Cell cell = cells.get(0);
                cell.width(50).padBottom(360);
                cell.height(40);
                //cell.padBottom(Gdx.graphics.getHeight()/2);
                //cell.center().top();

                cell = cells.get(1);
                cell.width(50).padBottom(360);
                cell.height(40);
                //cell.padBottom(Gdx.graphics.getHeight()/2);

            }

            @Override
            protected void result(Object object) {
                if(object.equals(true)){
                    mainClass.user = new User();
                    mainClass.user.save();
                    mainClass.setScreen(new CampainMap(mainClass));
                }else{
                    hide();
                }
            }
        };
        d.setBackground(new TextureRegionDrawable(new TextureRegion(AssetLoader.transparent)));
        d.getBackground().setMinWidth(Gdx.graphics.getWidth());
        d.getBackground().setMinHeight(Gdx.graphics.getHeight());
        d.show(stage);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        //stage.act(delta);
        //stage.draw();
    }

    @Override
    public void show() {
        super.show();
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

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void dispose() {

    }
}
