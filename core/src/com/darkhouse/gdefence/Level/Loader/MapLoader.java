package com.darkhouse.gdefence.Level.Loader;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;
import com.badlogic.gdx.utils.Array;
import com.darkhouse.gdefence.InventorySystem.inventory.ItemEnum;
import com.darkhouse.gdefence.Level.Level;
import com.darkhouse.gdefence.Level.Mob.Mob;
import com.darkhouse.gdefence.Level.Path.MapTile;
import com.darkhouse.gdefence.Level.Path.Spawn;
import com.darkhouse.gdefence.Level.Wave;
import com.darkhouse.gdefence.Objects.GameObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;


public class MapLoader {
    private int spawnersNumber;

    public int getSpawnersNumber() {
        return spawnersNumber;
    }

    private File loadFile;
    private int expFromLvl;
    private int goldFromLvl;
    private float startEnergyPercent;
    private float startHpPercent;
    private int numberWaves;
    private float[] timeBetweenWaves = new float[20];
    private ArrayList<Wave> waves;
    private Array<Mob.MoveType> moveTypesInLevel;
    private String dropList;
    private String penaltyDropList;

    public int getExpFromLvl() {
        return expFromLvl;
    }
    public int getGoldFromLvl() {
        return goldFromLvl;
    }
    public float getStartEnergyPercent() {
        return startEnergyPercent;
    }
    public float getStartHpPercent() {
        return startHpPercent;
    }
    public int getNumberWaves() {
        return numberWaves;
    }
    public float[] getTimeBetweenWaves() {
        return timeBetweenWaves;
    }
    public ArrayList<Wave> getWaves() {
        return waves;
    }
    public Array<Mob.MoveType> getMoveTypesInLevel() {
        return moveTypesInLevel;
    }
    public String getDropList() {
        return dropList;
    }
    public String getPenaltyDropList() {
        return penaltyDropList;
    }

    public MapLoader(int map) {
        try{
            loadFile = new File("Maps/Map" + map + ".properties");
        }catch (Exception e){
            Gdx.app.log("Error", e.toString());
        }

    }

    public MapTile[][] loadMap(){
        try {
            Properties prop = new Properties();
            FileInputStream fs = new FileInputStream(loadFile);
            prop.load(fs);

            String[] sizeStr = prop.getProperty("mapSize").split(":");
            int size[] = {Integer.parseInt(sizeStr[0]), Integer.parseInt(sizeStr[1])};
            MapTile mapTile[][] = new MapTile[size[0]][size[1]];



            String mapCode = prop.getProperty("blocks");
            String[] blocksY = mapCode.split("/");
            String[][] blocks = new String[size[0]][size[1]];

            for(int i = 0; i < blocksY.length; i++){
                String[] tmp = blocksY[i].split("-");
                for (int j = 0; j < tmp.length; j++){
                    blocks[j][i] = tmp[j].replaceAll(" ", "");//replace allow to use spacing in map files
                }
            }
            //
//

//            for (int y = 0; y < mapTile[0].length; y++){
//                for (int x = 0; x < mapTile.length; x++){
//                    blocks[x][y] = blocksY[x].split("-");
//                }
//            }
            for (int y = 0; y < mapTile[0].length; y++){//.lenght
                for (int x = 0; x < mapTile.length; x++){
                    mapTile[x][y] = MapTile.generateTile(blocks[x][y]);
                    if(mapTile[x][y] instanceof Spawn) spawnersNumber++;
                }
            }

//            int sizeX, sizeY;
//            Scanner loadScanner = new Scanner(loadFile);
//            sizeX = loadScanner.nextInt();
//            sizeY = loadScanner.nextInt();
//            mapTile = new MapTile[sizeX][sizeY];


            //while(loadScanner.hasNext()){
//            for (int y = 0; y < sizeY; y++){
//                for (int x = 0; x < sizeX; x++){
//                    mapTile[x][y] = new MapTile(MapTile.getTypeById(loadScanner.nextInt()));
//                    //System.out.println(MapTile.getTypeById(loadScanner.nextInt()));
//                }
//            }
//            for (int y = 0; y < sizeY; y++){
//                for (int x = 0; x < sizeX; x++){
//                    mapTile[x][y].setLogic(MapTile.getLogicById(loadScanner.nextInt()));
//
//                    if(mapTile[x][y].getLogic() == MapTile.TileLogic.spawnerR ||
//                       mapTile[x][y].getLogic() == MapTile.TileLogic.spawnerL ||
//                       mapTile[x][y].getLogic() == MapTile.TileLogic.spawnerU ||
//                       mapTile[x][y].getLogic() == MapTile.TileLogic.spawnerD){
//                        spawnersNumber++;
//                    }
//                }
//            }

            //System.out.println(mapTile[4][9].getLogic());

            //}


//            loadScanner.close();
            return mapTile;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public void loadProperties(int spawners, boolean forRealMap){
        try {
            if(spawners == 0){//empty maps
                return;
            }
            Properties prop = new Properties();
            FileInputStream fs = new FileInputStream(loadFile);
            prop.load(fs);


//            Scanner loadScanner = new Scanner(loadFile);
            waves = new ArrayList<Wave>();
            moveTypesInLevel = new Array<Mob.MoveType>();

//            expFromLvl = loadScanner.nextInt();
//            goldFromLvl = loadScanner.nextInt();
//            startEnergyPercent = loadScanner.nextFloat();
//            startHpPercent = loadScanner.nextFloat();
            expFromLvl = Integer.parseInt(prop.getProperty("expFromLvl"));
            goldFromLvl = Integer.parseInt(prop.getProperty("goldFromLvl"));
            startEnergyPercent = Float.parseFloat(prop.getProperty("startEnegryPercent"));
            startHpPercent = Float.parseFloat(prop.getProperty("startHpPercent"));
            dropList = prop.getProperty("drop");
            penaltyDropList = prop.getProperty("penaltyDrop");

            String[] wavesCode = prop.getProperty("waves").split("/");

//            while (loadScanner.hasNext()){
            int j = 0;
            for (int i = 0; i < wavesCode.length/*/spawners*/; i++) {
                String[] info = wavesCode[i].split(":");
//                for (int j = 0; j < spawners; j++) {
//                    String[] info2 = info[i].split("-");
                waves.add(new Wave(Integer.parseInt(info[0]), Integer.parseInt(info[1]), Float.parseFloat(info[2])));
                Mob.MoveType mt = Mob.getMobById(waves.get(waves.size() - 1).getMobID()).getMoveType();//
                if(!moveTypesInLevel.contains(mt, true))moveTypesInLevel.add(mt);
                timeBetweenWaves[waves.size() - 1] = Integer.parseInt(info[3]);//float
                if (forRealMap) {
                    waves.get(i/* + j*/).setSpawner(Level.getMap().getSpawner().get(j/*0*/));//invertion//i - spawners + j
                }
                if(j + 1 == spawners)j = 0;
                else j++;
//                }
            }
//            }
            numberWaves = waves.size();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
