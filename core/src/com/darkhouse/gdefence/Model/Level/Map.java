package com.darkhouse.gdefence.Model.Level;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.utils.Array;
import com.darkhouse.gdefence.GDefence;
import com.darkhouse.gdefence.Level.Ability.Mob.SpellImmune;
import com.darkhouse.gdefence.Level.Ability.Tower.Ability;
import com.darkhouse.gdefence.Level.Loader.MapLoader;
import com.darkhouse.gdefence.Level.Path.*;
import com.darkhouse.gdefence.Level.Mob.Mob;
import com.darkhouse.gdefence.Level.Mob.Way;
import com.darkhouse.gdefence.Level.Tower.Projectile;
import com.darkhouse.gdefence.Level.Tower.Tower;
import com.darkhouse.gdefence.Level.Wave;
import com.darkhouse.gdefence.Model.Effectable;
import com.darkhouse.gdefence.Objects.TowerObject;
import com.darkhouse.gdefence.Screens.LevelMap;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Map {
    private boolean isBuild = false;

    private ShapeRenderer shape;


    private TowerObject rangeTower;
    private DragAndDrop.Payload payload;

    public enum MapType{
        CLASSIC, INVASION, KILLMADNESS, TIME
    }

    public void setBuild(boolean build, TowerObject rangeTower, DragAndDrop.Payload payload) {//
        isBuild = build;
        this.rangeTower = rangeTower;
        this.payload = payload;
    }

    private MapTile[][] tiles;
    public MapTile[][] getTiles() {
        return tiles;
    }
//    private HashMap<Mob.MoveType, Array<Array<Array<MapTile>>>> paths;//moveType, 1.Each spawn 2.Each possible path 3.Each maptile in path
    private HashMap<Mob.MoveType, Array<Path>> paths; //array of possible paths

    public HashMap<Mob.MoveType, Array<Path>> getPaths() {
        return paths;
    }

    //    public HashMap<Mob.MoveType, Array<Array<Array<MapTile>>>> getPaths() {
//        return paths;
//    }

    public static List<Projectile> projectiles;

    private int x;
    private int y;
    private int cellSize;
    private ArrayList<Spawn> spawner;
    private ArrayList<MapTile> castle;

    public ArrayList<Spawn> getSpawner() {
        return spawner;
    }//need only size
    public ArrayList<MapTile> getCastle() {
        return castle;
    }

//    public static Way checkSpawnerWay(MapTile spawner){
//        switch (spawner.getLogic()){
//            case spawnerR:
//                return Way.RIGHT;
//            case spawnerL:
//                return Way.LEFT;
//            case spawnerU:
//                return Way.UP;
//            case spawnerD:
//                return Way.DOWN;
//            default:
//                return null;
//        }
//    }

//    public static Way checkTurnWay(MapTile tile){
//        switch (tile.getLogic()){
//            case turnR:
//                return Way.RIGHT;
//            case turnL:
//                return Way.LEFT;
//            case turnU:
//                return Way.UP;
//            case turnD:
//                return Way.DOWN;
//            default:
//                return null;
//        }
//    }
    public boolean inMapBounds(float x, float y){
        return (this.x <= x && x <= this.x + (tiles.length * tiles[0][0].getWidth())) && (this.y >= y && y >= this.y - tiles[0].length * tiles[0][0].getHeight());
    }

    public static Mob getNearestMob(Mob startSearch, int range){
        float currentF = Float.MAX_VALUE;//infinity
        Mob currentMob = null;
        for (int i = 0; i < Wave.mobs.size; i++){
            Mob m = Wave.mobs.get(i);
            float dst = m.getCenter().dst(startSearch.getCenter());
            if(startSearch != m && dst < range && dst < currentF){
                currentF = dst;
                currentMob = m;
            }
        }
        return currentMob;
    }

    public static Array<Mob> getMobsInRange(Mob searchMob, int range, boolean ignoreImmunity){
        Array<Mob> found = new Array<Mob>();
        for (int i = 0; i < Wave.mobs.size; i++){
            Mob m = Wave.mobs.get(i);
            float dst = m.getCenter().dst(searchMob.getCenter());
            if(dst <= range && (ignoreImmunity || targetMob(m))){
                found.add(m);
            }
        }
        return found;
    }
    public static Array<Mob> getMobsInRange(Vector2 searchPoint, int range, boolean ignoreImmunity){
        Array<Mob> found = new Array<Mob>();
        for (int i = 0; i < Wave.mobs.size; i++){
            Mob m = Wave.mobs.get(i);
            float dst = m.getCenter().dst(searchPoint);
            if(dst <= range && (ignoreImmunity || targetMob(m))){
                found.add(m);
            }
        }
        return found;
    }

    public static Array<Mob> getMobsMaskInRange(Vector2 searchPoint, int range, boolean ignoreImmunity){
        Array<Mob> found = new Array<Mob>();
        for (int i = 0; i < Wave.mobs.size; i++){
            Mob m = Wave.mobs.get(i);
            float dst = m.getCenter().dst(searchPoint) - m.getWidth()/2;
            if(dst <= range && (ignoreImmunity || targetMob(m))){
                found.add(m);
            }
        }
        return found;
    }
    private static boolean targetMob(Mob m){//mob can target to use spell on him (target/aoe/non target)
        return !m.haveEffect(SpellImmune.SpellImmuneBuff.class);
    }

    public Array<Tower> getTowersInRange(Vector2 searchPoint, float range){
        Array<Tower> found = new Array<Tower>();
        for (int x = 0; x < tiles.length; x++){
            for (int y = 0; y < tiles[0].length; y++){
                if (tiles[x][y].getBuildedTower() != null){
                    Tower t = tiles[x][y].getBuildedTower();
                    float dst = tiles[x][y].getCenter().dst(searchPoint);//can do tower.getCenter();
                    if(dst <= range){
                        found.add(t);
                    }
                }
            }
        }
        return found;

    }
    public Array<Tower> getTowersOnMap(){
        Array<Tower> found = new Array<Tower>();
        for (int x = 0; x < tiles.length; x++){
            for (int y = 0; y < tiles[0].length; y++){
                if(tiles[x][y].getBuildedTower() != null) found.add(tiles[x][y].getBuildedTower());
            }
        }
        return found;
    }
    public Array<Effectable> getUnitsInRange(Vector2 searchPoint, int range, Array<Class<? extends Effectable>> affected, boolean centerSearch, boolean ignoreImmunity){
        Array<Effectable> tmp = new Array<Effectable>();
        if(affected.contains(Mob.class, true)) {
            if(centerSearch) tmp.addAll(getMobsInRange(searchPoint, range, ignoreImmunity));
            else             tmp.addAll(getMobsMaskInRange(searchPoint, range, ignoreImmunity));
        }
        if(affected.contains(Tower.class, true)){
            tmp.addAll(getTowersInRange(searchPoint, range));
        }
        return tmp;
    }
    public Array<Effectable> getAllUnitsOnMap(Array<Class<? extends Effectable>> affected, boolean ignoreImmunity){
        Array<Effectable> tmp = new Array<Effectable>();
        if(affected.contains(Mob.class, true)) {
            for (int i = 0; i < Wave.mobs.size; i++) {
                if (ignoreImmunity || targetMob(Wave.mobs.get(i))) {
                    tmp.add(Wave.mobs.get(i));
                }
            }
        }
        if(affected.contains(Tower.class, true)){
            tmp.addAll(getTowersOnMap());
        }
        return tmp;
    }

    public Effectable getTargetUnit(Vector2 searchPoint, Array<Class<? extends Effectable>> affected){//those all method very bad need rework// TODO
        if(affected.contains(Mob.class, true)) {
            for (int i = 0; i < Wave.mobs.size; i++){
                Mob m = Wave.mobs.get(i);
                if(targetMob(m) && m.contains(searchPoint)) return m;
            }
        }
        if(affected.contains(Tower.class, true)){
            for (int x = 0; x < tiles.length; x++){
                for (int y = 0; y < tiles[0].length; y++){
                    if (tiles[x][y].getBuildedTower() != null){
                        Tower t = tiles[x][y].getBuildedTower();
                        if(tiles[x][y].contains(searchPoint)) return t;
                    }
                }
            }
        }
        return null;
    }


    public WalkableMapTile getTileContainMob(Mob mob){
        for (int x = 0; x < tiles.length; x++){
            for (int y = 0; y < tiles[0].length; y++){
                if (tiles[x][y].contains(mob.getBoundingRectangle())){//
                    //if(tiles[x][y].getLogic() == MapTile.TileLogic.turnR) {
                        //System.out.println(tiles[x][y].getX() + " " + tiles[x][y].getY());
                        //System.out.println(mob.getX() + " " + mob.getY());
                    //}
                    return ((WalkableMapTile) tiles[x][y]);
                }
            }
        }
        return null;
    }

    public MapTile getFirstTile(MapTile mapTile1, MapTile mapTile2, Mob.MoveType prefType/*, int spawner*/){

        //meaning that all tiles are Walkable
//        Walkable w1 = ((Walkable) mapTile1);
//        Walkable w2 = ((Walkable) mapTile2);

//        if(!w1.getApplyMobs().haveSame(w2.getApplyMobs()))throw new IllegalArgumentException(mapTile1 + " and " + mapTile2 +
//                " cant be in one path(applyMobs fields haven't same moveType)");
//
//        if(!Arrays.asList(w1.getApplyMobs().getSame(w2.getApplyMobs())).contains(prefType)) throw  new
//                IllegalArgumentException("prefType cannot exist in this types " + prefType);

//        for (Mob.MoveType mt:w1.getApplyMobs().getSame(w2.getApplyMobs())){
        int[] index = new int[2];
//            for (Array<Array<MapTile>> spawn:paths.get(prefType)){//can be shit//worked when was 2 dimension in paths hashmap
//                for (Array<MapTile> path:spawn) {
        for (Path path:paths.get(prefType)) {
            if (path.contains(mapTile1, true)) index[0] = path.indexOf(mapTile1, true);
            else index[0] = 99;//very very big
            if (path.contains(mapTile2, true)) index[1] = path.indexOf(mapTile2, true);
            else index[1] = 99;

//            System.out.println("index " + index[0] + " " + index[1]);
//                    if(path.indexOf(mapTile1, true) < path.indexOf(mapTile2, true)) return mapTile1;
//                    else return mapTile2;//meaning that they different
            if (index[0] == index[1] || index[0] == 99 || index[1] == 99) {
                continue;
//                throw new IllegalArgumentException("Tiles identy or dont exist on way");
            }
            return index[0] < index[1] ? mapTile1 : mapTile2;
//                }
//            }
//        }
        }
        throw new IllegalArgumentException("tiles dont cross");
    }

    private Array<MapTile> generatePath(Spawn spawner, Mob.MoveType type){
        GDefence.getInstance().log("Start generate path for " + type.name());
        Array<MapTile> path = new Array<MapTile>();
        path.add(spawner);

        Way manipulatedWay = null;
//        Way prevWay = null;
        while (!(path.peek() instanceof Castle)) {
            MapTile currentTile = path.peek();
//            System.out.println(currentTile);
            WalkableMapTile thisTile = ((WalkableMapTile) currentTile);
//            Way tmp = thisTile.manipulatePath(type, manipulatedWay);
//            if(tmp != null) manipulatedWay = tmp;

            Point p = null;
            if (path.size > 1 && !(path.get(path.size - 2) instanceof Portal)) {//disable teleport to portal, which teleport to this (infinity tp)
//                System.out.println(thisTile);
                p = thisTile.manipulateMob();
            }
            int[] coord;
            if(p != null) {
                coord = new int[]{p.y, p.x};//portals
            } else {
                Way tmp = thisTile.manipulatePath(type, manipulatedWay);
                if(tmp != null) manipulatedWay = tmp;
                coord = Way.getCoordOffset(manipulatedWay, currentTile.getIndexX(), currentTile.getIndexY());//simply way

            }

//            manipulatedWay = thisTile.manipulatePath(type, manipulatedWay);
//            if(manipulatedWay != null) prevWay = manipulatedWay;
//            int i = 1;//
//            while(manipulatedWay == null){
//                System.out.println(prevWay + " " + currentTile);
//                manipulatedWay = ((WalkableMapTile) path.get(path.size - 1 - i)).manipulatePath(type, prevWay);//last - i
//                i++;
//            }

            MapTile checkTile = tiles[coord[0]][coord[1]];
            if (checkTile instanceof WalkableMapTile) path.add(checkTile);
            else throw new IllegalArgumentException("Path logic is wrong, bad tile is " + "x " + coord[0] + " y " + coord[1]);
        }
        GDefence.getInstance().log("End generate path for " + type.name());
        return path;
    }
    private void initPaths(Array<Mob.MoveType> types){
        GDefence.getInstance().log("Start init paths");
//        paths = new HashMap<Mob.MoveType, Array<Array<Array<MapTile>>>>();
        paths = new HashMap<Mob.MoveType, Array<Path>>();
        for (Mob.MoveType moveType:types){
//            Array<Array<Array<MapTile>>> forSpawner = new Array<Array<Array<MapTile>>>();
//            Array<Array<MapTile>> possiblePaths = new Array<Array<MapTile>>();
//            Array<MapTile> currentPath; //= new Array<MapTile>();
            Array<Path> possiblePaths = new Array<Path>();
            Path currentPath;
            for (int i = 0; i < spawner.size(); i++){
                while (true) {//rework with deleting break and infinity loop//TODO
                    currentPath = new Path(generatePath(spawner.get(i), moveType), i);
                    if (!possiblePaths.contains(currentPath, false)) {
                        possiblePaths.add(currentPath);//override contains
                    }
                    else break;
                }
            }
            paths.put(moveType, possiblePaths);
        }
        GDefence.getInstance().log("End init paths");
    }

    public void setIndexTiles(){
        for (int y = 0; y < tiles[0].length; y++){
            for (int x = 0; x < tiles.length; x++){
                tiles[x][y].setIndex(x, y);
                tiles[x][y].setOwner(this);
            }
        }
    }


    public Map(final int number, int x, int y, int cellSize) {
        projectiles = new ArrayList<Projectile>();//dirty code
        initMap(number);
        this.x = x;
        this.y = y;
        this.cellSize = cellSize;
        initCells();

        shape = new ShapeRenderer();
    }
    private void initCells(){
        for (int i = 0; i < tiles.length; i++){
            for (int j = 0; j < tiles[0].length; j++){
                tiles[i][j].setBounds(x + cellSize*i, y - cellSize - cellSize*j , cellSize, cellSize);
//                tiles[i][j].initTexture();/////////
            }
        }
    }

    private void initMap(final int number){
        MapLoader ml = new MapLoader(number);
        tiles = ml.loadMap();

        ml.loadProperties(ml.getSpawnersNumber(), false);//may do outside (in Level class)
        MapType type = ml.getMapType();
        setIndexTiles();//
        searchSpawner();
        initPortals();
        initBaseTextures();
        if(type == MapType.CLASSIC) {
            searchCastle();
            initPaths(ml.getMoveTypesInLevel());
        }



//        normalizeTextures();
//        normalizeBlocks();



    }

    public void initBaseTextures(){
        for (MapTile[] ma:tiles){//can map.getTiles()
            for (MapTile m: ma) m.initTexture();
        }
    }

//    private void normalizeTextures(){
//        for (int y = 0; y < tiles[0].length; y++){
//            for (int x = 0; x < tiles.length; x++){
//                if(tiles[x][y] instanceof Turn){
//                    Texture texturePath = null;
//                    Turn turn = ((Turn) tiles[x][y]);
//                    Way startWay = turn.getStartWay();
//                    Way resultWay = turn.getResultWay();
//                    Way[] leastWays = Way.getLeastWays(new Way[]{Way.invertWay(startWay), resultWay});
//                    for (Way leastWay : leastWays) {
//                        int[] coord = Way.getCoordOffset(leastWay, x, y);
//                        if (coord[0] >= 0 && coord[0] < tiles.length && coord[1] >= 0 && coord[1] < tiles[0].length) {
//                            MapTile checkTile = tiles[coord[0]][coord[1]];
//                            if (checkTile != null && checkTile instanceof Walkable) {
//                                if (checkTile instanceof Road) {
//                                    if(turn.getApplyMobs() == TargetType.WATER_ONLY) {
//                                        texturePath = GDefence.getInstance().assetLoader.get("Path/Turn/turnWaterGround" +
//                                                Turn.getTurnCode(startWay, resultWay) + leastWay.getShortName() + ".png", Texture.class);
////                                        texturePath.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
//                                        break;
//                                    }else if(turn.getApplyMobs() == TargetType.GROUND_ONLY || turn.getApplyMobs() == TargetType.GROUND_WATER){
//                                        texturePath = GDefence.getInstance().assetLoader.get("Path/Turn/bridge" +
//                                                Turn.getTripleTurnCode(startWay, resultWay, leastWay) + "noArrows.png", Texture.class);
//                                        break;
//                                    }
//                                } else /*if (checkTile instanceof WaterRoad)*/ {
//                                    if(turn.getApplyMobs() == TargetType.WATER_ONLY) {
//                                        texturePath = GDefence.getInstance().assetLoader.get("Path/Turn/waterBridge" +
//                                                Turn.getTripleTurnCode(startWay, resultWay, leastWay) + "noArrows.png", Texture.class);
//                                        break;
//                                    }else if(turn.getApplyMobs() == TargetType.GROUND_ONLY || turn.getApplyMobs() == TargetType.GROUND_WATER){
//                                        texturePath = GDefence.getInstance().assetLoader.get("Path/Turn/turnGroundWater" +
//                                                Turn.getTurnCode(startWay, resultWay) + leastWay.getShortName() + ".png", Texture.class);
////                                        texturePath.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
//                                        break;
//                                    }
//                                }
//
//                            }
//                        }
//                    }
//                    if(texturePath!= null) turn.setRegion(texturePath);
//
//                }
//            }
//        }
//    }

    public void normalizeBlocks(){
//        System.out.println(GDefence.getInstance().assetLoader.get("Path/Turn/turnLU.png", Texture.class).getMinFilter());
        for (int y = 0; y < tiles[0].length; y++) {
            for (int x = 0; x < tiles.length; x++) {

                if(tiles[x][y] instanceof Road || tiles[x][y] instanceof Turn || tiles[x][y] instanceof MultiTurn) {/*&& !(tiles[x][y] instanceof Castle)*/
                    Texture texture = null;
                    WalkableMapTile road = ((WalkableMapTile) tiles[x][y]);
                    Way[] leastWays = Way.getLeastWays(new Way[]{});
                    Array<WalkableMapTile> rightTiles = new Array<WalkableMapTile>();
                    for (Way leastWay : leastWays) {
                        int[] coord = Way.getCoordOffset(leastWay, x, y);
                        if (coord[0] >= 0 && coord[0] < tiles.length && coord[1] >= 0 && coord[1] < tiles[0].length) {
                            MapTile checkTile = tiles[coord[0]][coord[1]];
                            if (checkTile != null && checkTile instanceof WalkableMapTile) {
                                rightTiles.add(((WalkableMapTile) checkTile));//TODO must check and dont connect roads like ||||
                            }
                        }
                    }
                    switch (rightTiles.size){
                        case 2:
//                            if(rightTiles.get(0).getIndexX() == rightTiles.get(1).getIndexX() ||
//                                    rightTiles.get(0).getIndexY() == rightTiles.get(1).getIndexY()) {//check for line road (--- or -/n-/n- )
////                                break;
//
//                            }
                            if(rightTiles.get(0).getIndexX() == rightTiles.get(1).getIndexX()){
                                if(road.isSwimmable()) texture = GDefence.getInstance().assetLoader.get("Path/waterVertical.png", Texture.class);
                                else texture = GDefence.getInstance().assetLoader.get("Path/roadVertical.png", Texture.class);
                                break;
                            }
                            if(rightTiles.get(0).getIndexY() == rightTiles.get(1).getIndexY()){
                                if(road.isSwimmable()) texture = GDefence.getInstance().assetLoader.get("Path/waterHorizontal.png", Texture.class);
                                else texture = GDefence.getInstance().assetLoader.get("Path/roadHorizontal.png", Texture.class);
                                break;
                            }
//                            if(rightTiles.get(0).isSwimmable() && rightTiles.get(1).isSwimmable() && road.isSwimmable()){
//                                String turnCode = Turn.getTurnCode(Way.invertWay(Way.getNearBlockWay(road, rightTiles.get(0))),
//                                        Way.getNearBlockWay(road, rightTiles.get(1)));//different names
//                                texturePath = GDefence.getInstance().assetLoader.get("Path/Turn/turnWater" +
//                                        turnCode + ".png", Texture.class);
//                                break;
//                            }
//                            if(!rightTiles.get(0).isSwimmable() && !rightTiles.get(1).isSwimmable() && !road.isSwimmable()){
//                                String turnCode = Turn.getTurnCode(Way.invertWay(Way.getNearBlockWay(road, rightTiles.get(0))),
//                                        Way.getNearBlockWay(road, rightTiles.get(1)));//different names
//                                texturePath = GDefence.getInstance().assetLoader.get("Path/Turn/turn" +
//                                        turnCode + ".png", Texture.class);
//                                break;
//                            }
                            if(road.isSwimmable()){
                                String turnCode = Turn.getTurnCode(Way.invertWay(Way.getNearBlockWay(road, rightTiles.get(0))),
                                        Way.getNearBlockWay(road, rightTiles.get(1)));//different names
                                texture = GDefence.getInstance().assetLoader.getTurn("Path/Turn/turnWater" +
                                        turnCode + ".png");
                            }
                            if(!road.isSwimmable()){
//                                System.out.println(rightTiles.get(1));
                                String turnCode = Turn.getTurnCode(Way.invertWay(Way.getNearBlockWay(road, rightTiles.get(0))),
                                        Way.getNearBlockWay(road, rightTiles.get(1)));//different names
                                texture = GDefence.getInstance().assetLoader.getTurn("Path/Turn/turn" +
                                        turnCode + ".png");
                            }
                            break;
                        case 3://tripleTurn
                            WalkableMapTile tile[] = {rightTiles.get(0), rightTiles.get(1), rightTiles.get(2)};
                            if(tile[0].isSwimmable() && tile[1].isSwimmable() && tile[2].isSwimmable() && road.isSwimmable()){
                                String turnCode = Turn.getTripleTurnCode(Way.invertWay(Way.getNearBlockWay(road, tile[0])),//invert need to do same names
                                        Way.getNearBlockWay(road, tile[1]), Way.getNearBlockWay(road, tile[2]));
                                texture = GDefence.getInstance().assetLoader.getTurn("Path/Turn/waterBridge" +
                                        turnCode + "noArrows.png");
                                break;
                            }
                            if(!tile[0].isSwimmable() && !tile[1].isSwimmable() && !tile[2].isSwimmable() && !road.isSwimmable()){
                                String turnCode = Turn.getTripleTurnCode(Way.invertWay(Way.getNearBlockWay(road, tile[0])),//invert need to do same names
                                        Way.getNearBlockWay(road, tile[1]), Way.getNearBlockWay(road, tile[2]));
                                texture = GDefence.getInstance().assetLoader.getTurn("Path/Turn/bridge" +
                                        turnCode + "noArrows.png");
                                break;
                            }
                            if(road.isSwimmable()){
                                String turnCode = null;

                                Array<MapTile> swimmable = new Array<MapTile>();
                                Array<MapTile> noSwimmable = new Array<MapTile>();
                                for (WalkableMapTile mt:tile){
                                    if(mt.isSwimmable())swimmable.add(mt);
                                    else noSwimmable.add(mt);
                                }
                                switch (swimmable.size){
                                    case 2:
                                        turnCode = Turn.getTurnCode(Way.invertWay(Way.getNearBlockWay(road, swimmable.get(0))),
                                                Way.getNearBlockWay(road, swimmable.get(1))) +
                                                Way.getNearBlockWay(road, noSwimmable.get(0)).getShortName();
                                        break;
                                    case 1:
                                        turnCode = Turn.getTurnCode(Way.invertWay(Way.getNearBlockWay(road, noSwimmable.get(0))),
                                                Way.getNearBlockWay(road, noSwimmable.get(1))) +
                                                Way.getNearBlockWay(road, swimmable.get(0)).getShortName();
//                                        MapTile firstNoSwimmable = getFirstTile(noSwimmable.get(0), noSwimmable.get(1), Mob.MoveType.ground);
//                                        noSwimmable.removeValue(firstNoSwimmable, true);//or false
//                                        MapTile secondNoSwimmable = noSwimmable.get(0);
//                                        System.out.println(firstNoSwimmable + " " +  secondNoSwimmable);
//                                        turnCode =
//                                                Turn.getTurnCode(Way.invertWay(Way.getNearBlockWay(road, firstNoSwimmable)), Way.getNearBlockWay(road, swimmable.get(0))) +
//                                                Way.getNearBlockWay(road, secondNoSwimmable).getShortName();
//                                        System.out.println(turnCode);
////                                        for (int i = 0; i < noSwimmable.size; i++){
////                                            if(Way.getNearBlockWay(road, noSwimmable.get(i)) != Way.invertWay(startWay)){
////                                                turnCode += Way.getNearBlockWay(road, noSwimmable.get(i)).getShortName();
////                                            }
////                                        }
                                        break;
                                }
                                if(turnCode!= null) {
                                    if(swimmable.size < noSwimmable.size) texture = GDefence.getInstance().assetLoader.getTurn("Path/Turn/turnGroundWater" + turnCode + ".png");//incorrect
                                    else texture = GDefence.getInstance().assetLoader.getTurn("Path/Turn/turnWaterGround" + turnCode + ".png");
                                    break;
                                }
                            }else/*if(!road.isSwimmable())*/ {
                                String turnCode = null;

                                Array<MapTile> noSwimmable = new Array<MapTile>();
                                Array<MapTile> swimmable = new Array<MapTile>();
                                for (WalkableMapTile mt:tile){
                                    if(mt.isSwimmable())swimmable.add(mt);
                                    else noSwimmable.add(mt);
                                }
                                switch (noSwimmable.size){
                                    case 2:
//                                        System.out.println(noSwimmable.get(0));
                                        turnCode = Turn.getTurnCode(Way.invertWay(Way.getNearBlockWay(road, noSwimmable.get(0))),
                                                Way.getNearBlockWay(road, noSwimmable.get(1))) +
                                                Way.getNearBlockWay(road, swimmable.get(0)).getShortName();
                                        break;
                                    case 1:
                                        turnCode = Turn.getTurnCode(Way.invertWay(Way.getNearBlockWay(road, swimmable.get(0))),//need check
                                                Way.getNearBlockWay(road, swimmable.get(1))) +
                                                Way.getNearBlockWay(road, noSwimmable.get(0)).getShortName();
//                                        MapTile firstSwimmable = getFirstTile(swimmable.get(0), swimmable.get(1), Mob.MoveType.ground);
//                                        noSwimmable.removeValue(firstSwimmable, true);//or false
//                                        MapTile secondSwimmable = swimmable.get(0);
//                                        turnCode = Turn.getTurnCode(Way.invertWay(Way.getNearBlockWay(road, firstSwimmable)),
//                                                Way.getNearBlockWay(road, noSwimmable.get(0))) + Way.getNearBlockWay(road, secondSwimmable).getShortName();
////                                        for (int i = 0; i < noSwimmable.size; i++){
////                                            if(Way.getNearBlockWay(road, noSwimmable.get(i)) != Way.invertWay(startWay)){
////                                                turnCode += Way.getNearBlockWay(road, noSwimmable.get(i)).getShortName();
////                                            }
////                                        }
                                        break;
                                }
                                if(turnCode!= null) {
                                    if(swimmable.size < noSwimmable.size) texture = GDefence.getInstance().assetLoader.getTurn("Path/Turn/turnGroundWater" + turnCode + ".png");//incorrect
                                    else texture = GDefence.getInstance().assetLoader.getTurn("Path/Turn/turnWaterGround" + turnCode + ".png");
                                    break;
                                }
                            }



                            break;
                        case 4://cross
                            texture = GDefence.getInstance().assetLoader.getTurn("Path/Turn/cross.png");
                            break;
                    }
                    if(texture != null){
//                        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                        road.setRegion(texture);
                    }
                }

                if(tiles[x][y].getLogic() == /* instanceof Portal*/MapTile.Logic.Portal){//combine with roads
                    Texture texture = null;
                    Portal portal = ((Portal) tiles[x][y].getInstance());
                    Way[] leastWays = Way.getLeastWays(new Way[]{});
                    Array<WalkableMapTile> rightTiles = new Array<WalkableMapTile>();
                    for (Way leastWay : leastWays) {
                        int[] coord = Way.getCoordOffset(leastWay, x, y);
                        if (coord[0] >= 0 && coord[0] < tiles.length && coord[1] >= 0 && coord[1] < tiles[0].length) {
                            MapTile checkTile = tiles[coord[0]][coord[1]];
                            if (checkTile != null && checkTile instanceof WalkableMapTile) {
                                rightTiles.add(((WalkableMapTile) checkTile));//TODO must check and dont connect roads like ||||
                            }
                        }
                    }
                    switch (rightTiles.size){
                        case 1:
                            texture = GDefence.getInstance().assetLoader.get("Path/Portal/portal" +
                                    Way.invertWay(Way.getNearBlockWay(portal, rightTiles.get(0))).getShortName() + portal.id  + ".png");
                    }
                    if(texture != null)portal.setRegion(texture);
                }

//                if (tiles[x][y] instanceof Bridge){  //xz kak eto delat po moemu eto immossible
//                    Texture texture = null;
//                    Bridge bridge = ((Bridge) tiles[x][y]);
//                    Way[] leastWays = Way.getLeastWays(new Way[]{});
//                    Array<WalkableMapTile> rightTiles = new Array<WalkableMapTile>();
//                    for (Way leastWay : leastWays) {
//                        int[] coord = Way.getCoordOffset(leastWay, x, y);
//                        if (coord[0] >= 0 && coord[0] < tiles.length && coord[1] >= 0 && coord[1] < tiles[0].length) {
//                            MapTile checkTile = tiles[coord[0]][coord[1]];
//                            if (checkTile != null && checkTile instanceof WalkableMapTile) {
//                                rightTiles.add(((WalkableMapTile) checkTile));
//                            }
//                        }
//                    }
//
//                    if (rightTiles.size < 3) continue;//something wrong //throw new RuntimeException();//size may be 4
//                    WalkableMapTile tile[] = {rightTiles.get(0), rightTiles.get(1), rightTiles.get(2)};
//                    Array<MapTile> swimmable = new Array<MapTile>();
//                    Array<MapTile> noSwimmable = new Array<MapTile>();
//                    for (WalkableMapTile mt:tile){
//                        if(mt.isSwimmable())swimmable.add(mt);
//                        else noSwimmable.add(mt);
//                    }
////                    Way inputWay = Way.getNearBlockWay(bridge, t)
//
//                    switch (swimmable.size){
//                        case 0:
//                            bridge.texture1 = GDefence.getInstance().assetLoader.get("Path/Bridge/bridge" + inputWay.getShortName() +//Textures from identical bridges//TODO
//                            endWay1.getShortName() + endWay2.getShortName() + "1.png", Texture.class);
//
//                        case 3:
//
//                    }
//
//
//
//                }
            }
        }


    }

    private void initPortals(){
        GDefence.getInstance().log("Init portals");
        ArrayList<Portal> portals = new ArrayList<Portal>();
        for (int y = 0; y < tiles[0].length; y++){
            for (int x = 0; x < tiles.length; x++){
                if(tiles[x][y].getLogic() == MapTile.Logic.Portal){
                    portals.add((Portal) tiles[x][y].getInstance());
                }
            }
        }
        if(portals.isEmpty()) return;
        Collections.sort(portals);
        ArrayList<Portal> tmp = new ArrayList<Portal>();
        for (int i = 0; i < portals.size() - 1; i++){
            Portal p1 = portals.get(i);
            Portal p2 = portals.get(i + 1);
            if(p1.id == p2.id){
                if(!tmp.contains(p1)) tmp.add(p1);
                if(!tmp.contains(p2)) tmp.add(p2);
            }else {
                for (Portal p:tmp){
                    p.init(tmp);
                }
                tmp.clear();
            }
        }
        Portal pLast = portals.get(portals.size() - 1);
        if(!tmp.contains(pLast)) tmp.add(pLast);
        for (Portal p:tmp){
            p.init(tmp);
        }

        tmp.clear();
//        for (int i = 0; i < portals.size(); i++){//22 map portals debug
//            System.out.println("S " + portals.get(i).toString() + "|" + portals.get(i).seconds.size);
//        }
    }

    private void searchSpawner(){
        spawner = new ArrayList<Spawn>();
        for (int y = 0; y < tiles[0].length; y++){
            for (int x = 0; x < tiles.length; x++){
                if(/*tiles[x][y].getLogic() *//*== MapTile.Logic.Spawner*/tiles[x][y] instanceof Spawn){
                    spawner.add(((Spawn) tiles[x][y]));
                }
            }
        }
    }

    private void searchCastle(){//dont need yet
        castle = new ArrayList<MapTile>();
        for (int y = 0; y < tiles[0].length; y++){
            for (int x = 0; x < tiles.length; x++){
                if(tiles[x][y].getLogic() == MapTile.Logic.Castle){
                    castle.add(tiles[x][y]);
                }
            }
        }
    }



    public void draw(SpriteBatch batch){
        for (int y = 0; y < tiles[0].length; y++){
            for (int x = 0; x < tiles.length; x++){
                tiles[x][y].draw(batch, 1f);
            }
        }
        batch.end();
        shape.setColor(Color.BLACK);
        Gdx.gl.glLineWidth(1);
        shape.begin(ShapeRenderer.ShapeType.Line);
        for (int y = 0; y < tiles[0].length; y++){
            for (int x = 0; x < tiles.length; x++){
                if(tiles[x][y].getBuildedTower() != null) {
                    tiles[x][y].getBuildedTower().drawRange(shape/*, delta*/);
                }
            }
        }
        shape.end();
//        Gdx.gl.glLineWidth(1);
        batch.begin();
        List<Projectile> tmp = new CopyOnWriteArrayList<Projectile>(projectiles);

        for (Projectile p:tmp){
            p.draw(batch, 1);
        }

        if(isBuild){
            drawBuildGrid(batch);
            batch.end();
            shape.setColor(Color.BLACK);
            Gdx.gl.glLineWidth(1);
            shape.begin(ShapeRenderer.ShapeType.Line);
            drawTowerRange(shape);
            shape.end();
//            Gdx.gl.glLineWidth(1);
            batch.begin();
        }

    }
    public void physic(float delta){
        // Iterator<Projectile> it = projectiles.iterator();
        List<Projectile> tmp = new CopyOnWriteArrayList<Projectile>(projectiles);

//        while(it.hasNext()){
//            Projectile p = it.next();
//            p.act(delta);
//            p.draw(batch, 1);
//        }
        for (Projectile p:tmp){
            p.act(delta);
        }
    }

    public void drawBuildGrid(SpriteBatch batch){
        Texture linePixel = GDefence.getInstance().assetLoader.get("buildGridLinePixel.png", Texture.class);
        for (int y = 0; y < tiles[0].length; y++){
            for (int x = 0; x < tiles.length; x++){
                for (int i = 0; i < tiles[x][y].getWidth(); i++){
                    batch.draw(linePixel, tiles[x][y].getX() + i, tiles[x][y].getY() + tiles[x][y].getHeight());
                }
                for (int i = 0; i < tiles[x][y].getHeight(); i++){
                    batch.draw(linePixel, tiles[x][y].getX(), tiles[x][y].getY() + i);
                }
            }
        }
        for (int x = 0; x < tiles.length; x++) {//draw last x lane
            for (int i = 0; i < tiles[0][0].getWidth(); i++){
                batch.draw(linePixel, tiles[x][tiles[0].length - 1].getX() + i, tiles[x][tiles[0].length - 1].getY());
            }
        }
        for (int y = 0; y < tiles[0].length; y++) {//draw last y lane
            for (int i = 0; i < tiles[0][0].getHeight(); i++){
                batch.draw(linePixel, tiles[tiles.length - 1][y].getX() + tiles[0][0].getWidth(),
                        tiles[tiles.length - 1][y].getY() + i);
            }
        }
    }


    private void drawTowerRange(/*SpriteBatch batch*/ShapeRenderer shape){//rework with draw in payload
        shape.setProjectionMatrix(LevelMap.levelMap.getStage().getCamera().combined);//need local levelMap
        float x = payload.getValidDragActor().getX();
        float y = payload.getValidDragActor().getY();
        float width = payload.getValidDragActor().getWidth();
        float height = payload.getValidDragActor().getHeight();

//        Circle attackRange = new Circle(x + width/2, y + height/2, rangeTower.getRange());
//        batch.draw(GDefence.getInstance().assetLoader.get("towerRangeTexture.png", Texture.class), attackRange.x - attackRange.radius, attackRange.y - attackRange.radius,
//                attackRange.radius*2, attackRange.radius*2);
        shape.circle(x + width/2, y + height/2, rangeTower.getRange());
    }
}
