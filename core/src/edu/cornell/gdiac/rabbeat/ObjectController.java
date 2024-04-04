package edu.cornell.gdiac.rabbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.rabbeat.obstacles.*;
import edu.cornell.gdiac.rabbeat.obstacles.enemies.BearEnemy;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import edu.cornell.gdiac.rabbeat.obstacles.platforms.MovingPlatform;
import edu.cornell.gdiac.rabbeat.obstacles.platforms.WeightedPlatform;
import edu.cornell.gdiac.util.PooledList;
import java.util.ArrayList;
import java.util.HashMap;

public class ObjectController {
    /** All the objects in the world. */
    public PooledList<GameObject> objects = new PooledList<>();
    /** All objects that are genre-dependent */
    public PooledList<IGenreObject> genreObjects = new PooledList<>();
    /** Queue for adding objects */
    public PooledList<GameObject> addQueue = new PooledList<>();

    /** Reference to the character avatar */
    public Player player;

    /** Physics constants for initialization
     * TODO: constants has some relevant information for game controller and this class does not care
     * */
    public JsonValue defaultConstants;
    /** The Json data for the level, generated by Tiled */
    public JsonValue levelJson;

    /** The font for giving messages to the player */
    protected BitmapFont displayFont;

    /** The texture for walls */
    protected TextureRegion blackTile;
    /** The texture for regular platforms */
    protected TextureRegion platformTile;
    /** The texture for regular platform art */
    protected TextureRegion platformTileArt;
    /** The texture for end platforms */
    protected TextureRegion endPlatform;
    /** The texture for wire platforms */
    protected TextureRegion wirePlatform;
    /** The texture for radio platforms */
    protected TextureRegion radioPlatform;
    /** The texture for guitar platforms */
    protected TextureRegion guitarPlatform;

    /** The texture for weighted platforms in Synth mode */
    protected TextureRegion weightedSynth;
    /** The texture for weighted platforms in Jazz mode */
    protected TextureRegion weightedJazz;

    /** The texture for moving platforms in Synth mode */
    protected TextureRegion movingSynth;
    /** The texture for moving platforms in Jazz mode */
    protected TextureRegion movingJazz;

    /** The texture for bullets */
    public TextureRegion bulletTexture;
    /** The texture for the exit condition */
    protected TextureRegion goalTile;
    /** The texture for the checkpoint when it is in default stage/has not been reached */
    protected TextureRegion checkpointDefault;
    /** The texture for the checkpoint when it is active has already been reached */
    protected TextureRegion checkpointActive;
    /** The texture for the background*/
    public TextureRegion backgroundTexture;
    /** The texture for the background overlay*/
    public TextureRegion backgroundOverlayTexture;
    private TextureRegion enemyDefaultTexture;

    /** Reference to the goalDoor (for collision detection) */
    public BoxGameObject goalDoor;

    /** Reference to all the checkpoints */
    public ArrayList<Checkpoint> checkpoints = new ArrayList<>();
    public float[] firstCheckpoint = new float[2];

    /** The player scale for synth */
    private float playerScale = 3/8f*2.5f;

    private TextureRegion synthDefaultTexture;
    private TextureRegion synthJazzTexture;

    //  PLAYER ANIMATIONS

    //  SYNTH
    /** The synth genre idle atlas for the player */
    public TextureAtlas synthIdleAtlas;
    /** The synth genre idle animation for the player */
    public Animation<TextureRegion> synthIdleAnimation;
    /** The synth genre walking atlas for the player */
    public TextureAtlas synthWalkAtlas;
    /** The synth genre walking animation for the player */
    public Animation<TextureRegion> synthWalkAnimation;
    /** The synth genre jumping atlas for the player */
    public TextureAtlas synthJumpAtlas;
    /** The synth genre jumping animation for the player */
    public Animation<TextureRegion> synthJumpAnimation;

    //  JAZZ
    /** The jazz genre idle atlas for the player */
    public TextureAtlas jazzIdleAtlas;
    /** The jazz genre idle animation for the player */
    public Animation<TextureRegion> jazzIdleAnimation;
    /** The jazz genre walking atlas for the player */
    public TextureAtlas jazzWalkAtlas;
    /** The jazz genre walking animation for the player */
    public Animation<TextureRegion> jazzWalkAnimation;
    /** The jazz genre jumping atlas for the player */
    public TextureAtlas jazzJumpAtlas;
    /** The jazz genre jumping animation for the player */
    public Animation<TextureRegion> jazzJumpAnimation;

    //ENEMY ANIMATIONS
    /** The idle atlas for the bear enemy */
    public TextureAtlas bearIdleAtlas;
    /** The idle animation for the bear enemy */
    public Animation<TextureRegion> bearIdleAnimation;

    private float synthSpeed;
    private float jazzSpeed;

    public BearEnemy enemy;

    /** The enemy scale for the enemy */
    private float enemyScale = 3/8f*2;

    /**
     * Gather the assets for this controller.
     *
     * This method extracts the asset variables from the given asset directory. It
     * should only be called after the asset directory is completed.
     *
     * @param directory	Reference to global asset manager.
     */
    public void gatherAssets(AssetDirectory directory) {
        levelJson = directory.getEntry("example", JsonValue.class);

        backgroundTexture = new TextureRegion(directory.getEntry("backgrounds:test-bg",Texture.class));
        backgroundOverlayTexture = new TextureRegion(directory.getEntry("backgrounds:overlay",Texture.class));
        enemyDefaultTexture = new TextureRegion(directory.getEntry("player:synth",Texture.class)); //CHANGE FOR ENEMY!

        defaultConstants = directory.getEntry( "defaultConstants", JsonValue.class );
        synthSpeed =  defaultConstants.get("player").get("max_speed").getFloat("synth");
        jazzSpeed = defaultConstants.get("player").get("max_speed").getFloat("jazz");
        synthDefaultTexture = new TextureRegion(directory.getEntry("player:synth",Texture.class));
        synthJazzTexture = new TextureRegion(directory.getEntry("player:synth-jazz",Texture.class));

        // Allocating player animations
        //  Synth
        synthIdleAtlas = new TextureAtlas(Gdx.files.internal("player/synthIdle.atlas"));
        synthIdleAnimation = new Animation<TextureRegion>(0.1f, synthIdleAtlas.findRegions("synthIdle"), Animation.PlayMode.LOOP);

        synthWalkAtlas = new TextureAtlas(Gdx.files.internal("player/synthWalk.atlas"));
        synthWalkAnimation = new Animation<TextureRegion>(0.06f, synthWalkAtlas.findRegions("synthWalk"), Animation.PlayMode.LOOP);

        synthJumpAtlas = new TextureAtlas(Gdx.files.internal("player/synthJump.atlas"));
        synthJumpAnimation = new Animation<TextureRegion>(0.08f, synthJumpAtlas.findRegions("synthJump"), Animation.PlayMode.NORMAL);

        //  Jazz
        jazzIdleAtlas = new TextureAtlas(Gdx.files.internal("player/jazzIdle.atlas"));
        jazzIdleAnimation = new Animation<TextureRegion>(0.11f, jazzIdleAtlas.findRegions("jazzIdle"), Animation.PlayMode.LOOP);

        jazzWalkAtlas = new TextureAtlas(Gdx.files.internal("player/jazzWalk.atlas"));
        jazzWalkAnimation = new Animation<TextureRegion>(0.08f, jazzWalkAtlas.findRegions("jazzWalk"), Animation.PlayMode.LOOP);

        jazzJumpAtlas = new TextureAtlas(Gdx.files.internal("player/jazzJump.atlas"));
        jazzJumpAnimation = new Animation<TextureRegion>(0.08f, jazzJumpAtlas.findRegions("jazzJump"), Animation.PlayMode.NORMAL);

        //  Allocating enemy animations
        //  Bear
        bearIdleAtlas = new TextureAtlas(Gdx.files.internal("enemies/bearIdle.atlas"));
        bearIdleAnimation = new Animation<TextureRegion>(0.1f, bearIdleAtlas.findRegions("bearIdle"), Animation.PlayMode.LOOP);

        // Allocate the tiles
        blackTile = new TextureRegion(directory.getEntry( "world:platforms:blackTile", Texture.class ));
        platformTile = new TextureRegion(directory.getEntry( "world:platforms:platform", Texture.class ));
        platformTileArt = new TextureRegion(directory.getEntry( "world:platforms:platformArt", Texture.class ));
        endPlatform = new TextureRegion(directory.getEntry( "world:platforms:endPlatform", Texture.class ));
        wirePlatform = new TextureRegion(directory.getEntry( "world:platforms:wirePlatform", Texture.class ));
        radioPlatform = new TextureRegion(directory.getEntry( "world:platforms:radioPlatform", Texture.class ));
        guitarPlatform = new TextureRegion(directory.getEntry( "world:platforms:guitarPlatform", Texture.class ));

        weightedSynth = new TextureRegion((directory.getEntry("world:platforms:weightedSynth", Texture.class)));
        weightedJazz = new TextureRegion((directory.getEntry("world:platforms:weightedJazz", Texture.class)));

        movingSynth = new TextureRegion((directory.getEntry("world:platforms:movingSynth", Texture.class)));
        movingJazz = new TextureRegion((directory.getEntry("world:platforms:movingJazz", Texture.class)));

        bulletTexture = new TextureRegion(directory.getEntry("world:bullet", Texture.class));
        goalTile  = new TextureRegion(directory.getEntry( "world:goal", Texture.class ));
        checkpointDefault  = new TextureRegion(directory.getEntry( "checkpoint:checkDefault", Texture.class ));
        checkpointActive  = new TextureRegion(directory.getEntry( "checkpoint:checkActive", Texture.class ));
        displayFont = directory.getEntry( "fonts:retro" ,BitmapFont.class);
    }

    /**
     * Populates all objects into the game.
     *
     * @param scale The draw scale
     */
    public void populateObjects(Vector2 scale){
        if (levelJson.has("layers")) {
            int levelHeight = levelJson.getInt("height");
            int tileSize = levelJson.getInt("tileheight");
            for (JsonValue layer : levelJson.get("layers")) {
                String layerName = layer.getString("name", "");
                switch (layerName){
                    case "background":
                        //TODO: change background image depending on tiled info – may do this when we have more levels
                        break;
                    case "walls":
                        int[] data = layer.get("data").asIntArray();
                        int width = layer.getInt("width");
                        int height = layer.getInt("height");
                        for (int i=0; i<data.length; i++){
                            int tileTypeID = data[i];
                            // tileTypeID == 0 means there is no tile there
                            if (tileTypeID != 0){
                                // Get x and y coordinates from where it is in the array
                                int x = i % width;
                                int y = height - (i / width) - 1;
                                createWall(scale, x, y);
                            }
                        }
                        break;
                    case "weightedPlatforms":
                        //  Sort the synth/jazz weighted platform coordinates into arrays corresponding to the num value
                        //  The index is used to identify a specific platform's synth and jazz position and its speed
                        float[][] synthCoord = new float[layer.get("objects").size][2];
                        float[][] jazzCoord = new float[layer.get("objects").size][2];
                        float[] wpSpeed = new float[layer.get("objects").size];
                        for (JsonValue wp : layer.get("objects")) {
                            int num = 0;
                            String genre = "";
                            float speed = 0;
                            for (JsonValue prop : wp.get("properties")){
                                switch(prop.getString("name")){
                                    case "num":
                                        num = prop.getInt("value");
                                        break;
                                    case "genre":
                                        genre = prop.getString("value");
                                        break;
                                    case "speed":
                                        speed = prop.getFloat("value");
                                        break;
                                }
                            }
                            switch(genre){
                                case "synth":
                                    synthCoord[num] = new float[] {wp.getFloat("x"), wp.getFloat("y")};
                                    break;
                                case "jazz":
                                    jazzCoord[num] = new float[] {wp.getFloat("x"), wp.getFloat("y")};
                                    break;
                            }
                            wpSpeed[num] = speed;
                        }
                        //  Now actually create weighted platforms using synthCoord, jazzCoord, wpSpeed
                        for (int i=0; i<layer.get("objects").size/2; i++){
                            createWeightedPlatform(scale, synthCoord[i], jazzCoord[i], wpSpeed[i], levelHeight, tileSize);
                        }
                        break;
                    case "movingPlatforms":
                        HashMap<Integer, Vector2[]> positionNodes = new HashMap<>();
                        HashMap<Integer, Float> mpSpeed = new HashMap<>();
                        for (JsonValue mp : layer.get("objects")) {
                            int num = 0;
                            int pos = 0;
                            float speed = 0;
                            int totalPos = 1;   //number of positions in this moving platform
                            for (JsonValue prop : mp.get("properties")){
                                switch(prop.getString("name")){
                                    case "num":
                                        num = prop.getInt("value");
                                        break;
                                    case "pos":
                                        pos = prop.getInt("value");
                                        break;
                                    case "speed":
                                        speed = prop.getFloat("value");
                                        break;
                                    case "totalPos":
                                        totalPos = prop.getInt("value");
                                        break;
                                }
                            }
                            //  Store coordinates
                            final int numOfNodes = totalPos;    // need to be final to be used in computeIfAbsent
                            positionNodes.computeIfAbsent(num, key -> new Vector2[numOfNodes]);
                            Vector2 coord = new Vector2(mp.getFloat("x"), mp.getFloat("y"));
                            positionNodes.get(num)[pos] = coord;

                            //  Store speed
                            mpSpeed.put(num, speed);
                        }
                        //  Now actually create moving platforms
                        for (int i=0; i<positionNodes.size(); i++){
                            createMovingPlatform(scale, positionNodes.get(i), mpSpeed.get(i), levelHeight, tileSize);
                        }
                        break;
                    case "platforms":
                        for (JsonValue platform : layer.get("objects")) {
                            float x = platform.getFloat("x");
                            float y = platform.getFloat("y");
                            createPlatform(scale, platform.getString("type"), x, y, levelHeight, tileSize);
                        }
                        break;
                    case "platformArt":
                        for (JsonValue a : layer.get("objects")) {
                            float x = a.getFloat("x");
                            float y = a.getFloat("y");
                            createPlatformArt(scale, a.getString("type"), x, y, levelHeight, tileSize);
                        }
                        break;
                    case "player":
                        if (layer.get("objects").size > 0){
                            JsonValue player = layer.get("objects").get(0);
                            float x = player.getInt("x");
                            float y = player.getInt("y");
                            createPlayer(scale, x, y, levelHeight, tileSize);
                        }
                        break;
                    case "enemies":
                        for (JsonValue enemy : layer.get("objects")) {
                            String enemyType = enemy.getString("type");
                            switch (enemyType){
                                case "bear":
                                    float x = enemy.getFloat("x");
                                    float y = enemy.getFloat("y");
                                    createEnemyBear(scale, x, y, levelHeight, tileSize);
                            }
                        }
                        break;
                    case "checkpoints":
                        for (JsonValue checkpoint : layer.get("objects")) {
                            float x = checkpoint.getFloat("x");
                            float y = checkpoint.getFloat("y");
                            int id = 0;
                            for (JsonValue prop : checkpoint.get("properties")){
                                if (prop.getString("name").equals("num")) {
                                    id = prop.getInt("value");
                                }
                            }
                            createCheckpoint(scale, x, y, id, levelHeight, tileSize);
                        }
                        break;
                    case "goal":
                        if (layer.get("objects").size > 0){
                            JsonValue goal = layer.get("objects").get(0);
                            float x = goal.getInt("x");
                            float y = goal.getInt("y");
                            createGoal(scale, x, y, levelHeight, tileSize);
                        }
                        break;
                }

            }
        }






//        // Repopulate current checkpoints
//        float checkpointWidth  = checkpointDefault.getRegionWidth()/scale.x;
//        float checkpointHeight = checkpointDefault.getRegionHeight()/scale.y;
//
//        Queue<Pair<BoxGameObject, Integer>> newCheckpoints = new Queue<>();
//        System.out.println(checkpoints);
//        for (Pair<BoxGameObject, Integer> pair : checkpoints) {
//            String cname = "checkpoint";
//            JsonValue checkpoint = constants.get("checkpoints").get(pair.snd);
//            JsonValue checkpointPos = checkpoint.get("pos");
//            BoxGameObject obj = new BoxGameObject(checkpointPos.getFloat(0), checkpointPos.getFloat(1), checkpointWidth, checkpointHeight);
//            obj.setBodyType(BodyDef.BodyType.StaticBody);
//            obj.setDensity(checkpoint.getFloat("density", 0));
//            obj.setFriction(checkpoint.getFloat("friction", 0));
//            obj.setRestitution(checkpoint.getFloat("restitution", 0));
//            obj.setSensor(true);
//            obj.setDrawScale(scale);
//            obj.setTexture(checkpointDefault);
//            obj.setName(cname + pair.snd);
//            GameController.getInstance().instantiate(obj);
//            newCheckpoints.addLast(new Pair<>(obj, pair.snd));
//        }
//        checkpoints.clear();
//        checkpoints = newCheckpoints;
//
//        // Add level goal
//        float dwidth  = goalTile.getRegionWidth()/scale.x;
//        float dheight = goalTile.getRegionHeight()/scale.y;
//
//        JsonValue goal = constants.get("goal");
//        JsonValue goalpos = goal.get("pos");
//        goalDoor = new BoxGameObject(goalpos.getFloat(0),goalpos.getFloat(1),dwidth,dheight);
//        goalDoor.setBodyType(BodyDef.BodyType.StaticBody);
//        goalDoor.setDensity(goal.getFloat("density", 0));
//        goalDoor.setFriction(goal.getFloat("friction", 0));
//        goalDoor.setRestitution(goal.getFloat("restitution", 0));
//        goalDoor.setSensor(true);
//        goalDoor.setDrawScale(scale);
//        goalDoor.setTexture(goalTile);
//        goalDoor.setName("goal");
//        GameController.getInstance().instantiate(goalDoor);
//
//        String wpname = "wplatform";
//        JsonValue wplatjv = constants.get("wplatforms");
//        for (int ii = 0; ii < wplatjv.size; ii++) {
//            JsonValue currentWP = wplatjv.get(ii);
//            WeightedPlatform obj;
//            obj = new WeightedPlatform(currentWP.get("pos").asFloatArray(), currentWP.get("synthPos").asFloatArray(),
//                    currentWP.get("jazzPos").asFloatArray(), currentWP.getFloat("speed"));
//            obj.setBodyType(BodyDef.BodyType.StaticBody);
//            obj.setDensity(defaults.getFloat("density", 0.0f));
//            obj.setFriction(defaults.getFloat("friction", 1.0f));
//            obj.setRestitution(defaults.getFloat("restitution", 0.0f));
//            obj.setDrawScale(scale);
//            obj.setTexture(weightedPlatform);
//            obj.setName(wpname + ii);
//            GameController.getInstance().instantiate(obj);
//        }
//        /** moving platform instantiation*/
//        String mpname = "mplatform";
//        JsonValue mplatjv = constants.get("mplatforms");
//        for (int ii = 0; ii < mplatjv.size; ii++) {
//            JsonValue currentWP = mplatjv.get(ii);
//            MovingPlatform obj;
//            obj = new MovingPlatform(currentWP.get("pos").asFloatArray(), currentWP.get("nodes").asFloatArray(),
//                    currentWP.getFloat("speed"));
//            obj.setBodyType(BodyDef.BodyType.StaticBody);
//            obj.setDensity(defaults.getFloat("density", 0.0f));
//            obj.setFriction(defaults.getFloat("friction", 10.0f));
//            obj.setRestitution(defaults.getFloat("restitution", 0.0f));
//            obj.setDrawScale(scale);
//            obj.setTexture(weightedPlatform);
//            obj.setName(mpname + ii);
//            GameController.getInstance().instantiate(obj);
//        }
//
//        //TODO: Load enemies
//        dwidth  = enemyDefaultTexture.getRegionWidth()/scale.x;
//        dheight = enemyDefaultTexture.getRegionHeight()/scale.y;
//
//        String ename = "enemy";
//        JsonValue enemiesjv = constants.get("enemies");
//        for (int ii = 0; ii < enemiesjv.size; ii++){
//            JsonValue currentEnemy = enemiesjv.get(ii);
//            BearEnemy obj;
//            obj = new BearEnemy(currentEnemy, dwidth*enemyScale,
//                    dheight*enemyScale, enemyScale, false, bearIdleAnimation);
//            obj.setBodyType(BodyDef.BodyType.StaticBody);
//            obj.setDrawScale(scale);
//            obj.setTexture(enemyDefaultTexture);
//            obj.setName(ename + ii);
//            GameController.getInstance().instantiate(obj);
//        }

    }

    /**
     * Convert Tiled coordinates to world coordinates.
     *
     * @param x The x Tiled coordinate.
     * @param y The y Tiled coordinate.
     * @param levelHeight The height of the screen (in number of tiles).
     * @param tileSize The size of the tiles (in pixels).
     * @return A Vector2 object where the x and y attributes are the converted world coordinates.
     */
    private Vector2 convertTiledCoord(float x, float y, int levelHeight, int tileSize){
        return(new Vector2(x / tileSize, levelHeight - y / tileSize));
    }
    /**
     * Create a checkpoint
     */
    private void createCheckpoint(Vector2 scale, float x, float y, int id, int levelHeight, int tileSize) {
        // Adjust and Convert coordinates to world coordinates
        y -= checkpointDefault.getRegionHeight()/2.5;
        Vector2 convertedCoord = convertTiledCoord(x, y, levelHeight, tileSize);

        if (id == 0){
            // Set first checkpoint as spawn point
            firstCheckpoint[0] = convertedCoord.x;
            firstCheckpoint[1] = convertedCoord.y;
        }
        float cWidth  = checkpointDefault.getRegionWidth()/scale.x;
        float cHeight = checkpointDefault.getRegionHeight()/scale.y;

        JsonValue defaults = defaultConstants.get("defaults");
        Checkpoint obj = new Checkpoint(id, checkpointActive, convertedCoord.x, convertedCoord.y, cWidth, cHeight);
        obj.setBodyType(BodyDef.BodyType.StaticBody);
        obj.setDensity(defaults.getFloat("density", 0.0f));
        obj.setFriction(defaults.getFloat("friction", 1.0f));
        obj.setRestitution(defaults.getFloat("restitution", 0.0f));
        obj.setSensor(true);
        obj.setDrawScale(scale);
        obj.setTexture(checkpointDefault);
        GameController.getInstance().instantiate(obj);
        checkpoints.add(obj);
    }

    /**
     * Sets the checkpoint with num = 0 as the spawn.
     *
     * @param scale Vector 2 scale used to draw
     */
    public void setFirstCheckpointAsSpawn(Vector2 scale){
        GameController.getInstance().setSpawn(new Vector2(firstCheckpoint[0], firstCheckpoint[1]));
    }

    /**
     * Create wall tiles
     * @param scale Scale to draw
     * @param x x coordinate (world coordinates) of tile
     * @param y y coordinate (world coordinates) of tile
     */
    private void createWall(Vector2 scale, float x, float y){
        String wname = "wall";
        JsonValue defaults = defaultConstants.get("defaults");
        BoxGameObject obj;
        float dwidth  = blackTile.getRegionWidth()/scale.x;
        float dheight = blackTile.getRegionHeight()/scale.y;
        obj = new BoxGameObject(x, y, dwidth, dheight);
        obj.setBodyType(BodyDef.BodyType.StaticBody);
        obj.setDensity(defaults.getFloat( "density", 0.0f ));
        obj.setFriction(defaults.getFloat( "friction", 0.0f ));
        obj.setRestitution(defaults.getFloat( "restitution", 0.0f ));
        obj.setDrawScale(scale);
        obj.setTexture(blackTile);
        obj.setName(wname);
        GameController.getInstance().instantiate(obj);
    }
    /**
     * Create a platform.
     *
     * @param scale The Vector2 draw scale
     * @param type A string used to determine the texture, either "default", "defaultEnd", "wire", "radio", "guitar"
     * @param x x coordinate (pixels) for the platform
     * @param y y coordinate (pixels) for the platform
     * @param levelHeight Height of level in number of tiles
     * @param tileSize Height of tile in pixels
     */
    private void createPlatform(Vector2 scale, String type, float x, float y, int levelHeight, int tileSize){
        TextureRegion textureRegion;
        switch(type){
            default:
                textureRegion = platformTile;
                break;
            case "defaultEnd":
                textureRegion = endPlatform;
                break;
            case "wire":
                textureRegion = wirePlatform;
                break;
            case "radio":
                textureRegion = radioPlatform;
                break;
            case "guitar":
                textureRegion = guitarPlatform;
                break;
        }
        //  Adjust coordinates + Convert coordinates to world coordinates
        y -= textureRegion.getRegionHeight()/2-4;
        Vector2 convertedCoord = convertTiledCoord(x, y, levelHeight, tileSize);
        convertedCoord.set(convertedCoord.x+1, convertedCoord.y);

        JsonValue defaults = defaultConstants.get("defaults");
        float dwidth  = textureRegion.getRegionWidth()/scale.x;
        float dheight = textureRegion.getRegionHeight()/scale.y;
        BoxGameObject platform;
        platform = new BoxGameObject(convertedCoord.x, convertedCoord.y, dwidth, dheight);
        platform.setBodyType(BodyDef.BodyType.StaticBody);
        platform.setDensity(defaults.getFloat( "density", 0.0f ));
        platform.setFriction(defaults.getFloat( "friction", 0.0f ));
        platform.setRestitution(defaults.getFloat( "restitution", 0.0f ));
        platform.setDrawScale(scale);
        platform.setTexture(textureRegion);
        platform.setName(type);
        GameController.getInstance().instantiate(platform);
    }

    /**
     * Create platform art which is not interact-able/collide-able with players.
     *
     * @param scale The Vector2 draw scale
     * @param type A string used to determine the texture, either "default", or "radio"
     * @param x x coordinate (pixels) for the platform art
     * @param y y coordinate (pixels) for the platform art
     * @param levelHeight Height of level in number of tiles
     * @param tileSize Height of tile in pixels
     */
    private void createPlatformArt(Vector2 scale, String type, float x, float y, int levelHeight, int tileSize){
        TextureRegion textureRegion;
        switch(type){
            default:
                textureRegion = platformTileArt;
                break;
            case "radio":
                textureRegion = platformTileArt;
                break;
        }
        //  Adjust coordinates + Convert coordinates to world coordinates
        y -= textureRegion.getRegionHeight()/2;
        Vector2 convertedCoord = convertTiledCoord(x, y, levelHeight, tileSize);
        convertedCoord.set(convertedCoord.x+1, convertedCoord.y);

        ArtObject platformArt = new ArtObject(textureRegion, convertedCoord.x, convertedCoord.y);
        platformArt.setBodyType(BodyDef.BodyType.StaticBody);
        platformArt.setDrawScale(scale);
        GameController.getInstance().instantiate(platformArt);
    }

    /**
     * Create a weighted platform.
     *
     * @param scale The Vector2 draw scale
     * @param synthCoord A float array which holds the weighted platform's x and y coordinates in synth mode
     * @param jazzCoord A float array which holds the weighted platform's x and y coordinates in jazz mode
     * @param speed The speed of the weighted platform
     * @param levelHeight Height of level in number of tiles
     * @param tileSize Height of tile in pixels
     */
    private void createWeightedPlatform(Vector2 scale, float[] synthCoord, float[] jazzCoord, float speed, int levelHeight, int tileSize){
        //  Adjust coordinates + Convert coordinates to world coordinates
        synthCoord[1] -= weightedSynth.getRegionHeight()/2-4;
        Vector2 convertedSynthCoord = convertTiledCoord(synthCoord[0], synthCoord[1], levelHeight, tileSize);
        convertedSynthCoord.set(convertedSynthCoord.x+1, convertedSynthCoord.y);
        jazzCoord[1] -= weightedSynth.getRegionHeight()/2-4;
        Vector2 convertedJazzCoord = convertTiledCoord(jazzCoord[0], jazzCoord[1], levelHeight, tileSize);
        convertedJazzCoord.set(convertedJazzCoord.x+1, convertedJazzCoord.y);

        JsonValue defaults = defaultConstants.get("defaults");
        float dwidth  = weightedSynth.getRegionWidth()/scale.x;
        float dheight = weightedSynth.getRegionHeight()/scale.y;
        WeightedPlatform weightedPlatform;
        weightedPlatform = new WeightedPlatform(dwidth, dheight,
                new float[] {convertedSynthCoord.x, convertedSynthCoord.y},
                new float[] {convertedJazzCoord.x, convertedJazzCoord.y},
                speed,
                weightedSynth, weightedJazz);
        weightedPlatform.setBodyType(BodyDef.BodyType.StaticBody);
        weightedPlatform.setDensity(defaults.getFloat("density", 0.0f));
        weightedPlatform.setFriction(defaults.getFloat("friction", 1.0f));
        weightedPlatform.setRestitution(defaults.getFloat("restitution", 0.0f));
        weightedPlatform.setDrawScale(scale);
        GameController.getInstance().instantiate(weightedPlatform);
    }

    private void createMovingPlatform(Vector2 scale, Vector2[] positionNodes, float speed, int levelHeight, int tileSize){
        //  Adjust coordinates + Convert coordinates to world coordinates
        Vector2[] convertedPos = new Vector2[positionNodes.length];
        for(int i=0; i<positionNodes.length; i++){
            positionNodes[i].y -= movingSynth.getRegionHeight()/2-4;
            convertedPos[i] = convertTiledCoord(positionNodes[i].x, positionNodes[i].y, levelHeight, tileSize);
        }

        JsonValue defaults = defaultConstants.get("defaults");
        float dwidth  = movingSynth.getRegionWidth()/scale.x;
        float dheight = movingSynth.getRegionHeight()/scale.y;
        MovingPlatform movingPlatform;
        movingPlatform = new MovingPlatform(dwidth, dheight, convertedPos, speed, weightedSynth, weightedJazz);
        movingPlatform.setBodyType(BodyDef.BodyType.StaticBody);
        movingPlatform.setDensity(defaults.getFloat("density", 0.0f));
        movingPlatform.setFriction(defaults.getFloat("friction", 1.0f));
        movingPlatform.setRestitution(defaults.getFloat("restitution", 0.0f));
        movingPlatform.setDrawScale(scale);
        GameController.getInstance().instantiate(movingPlatform);
    }

    /**
     * Create the player object.
     *
     * @param scale The Vector2 draw scale
     * @param startX  The player's starting x coordinate (pixels)
     * @param startY The player's starting y coordinate (pixels)
     * @param levelHeight Height of level in number of tiles
     * @param tileSize Height of tile in pixels
     */
    private void createPlayer(Vector2 scale, float startX, float startY, int levelHeight, int tileSize){
        //  Convert coordinates to world coordinate
        Vector2 convertedCoord = convertTiledCoord(startX, startY, levelHeight, tileSize);

        //TODO: Figure out if having 2 references for player fields is okay
        float dwidth  = synthDefaultTexture.getRegionWidth()/scale.x;
        float dheight = synthDefaultTexture.getRegionHeight()/scale.y;
        player = new Player(defaultConstants.get("player"), convertedCoord.x, convertedCoord.y,
                dwidth*playerScale, dheight*playerScale, playerScale);
        player.setDrawScale(scale);

        // Set animations: Synth
        player.synthIdleAnimation = synthIdleAnimation;
        player.synthWalkAnimation = synthWalkAnimation;
        player.synthJumpAnimation = synthJumpAnimation;
        // Set animations: Jazz
        player.jazzIdleAnimation =  jazzIdleAnimation;
        player.jazzWalkAnimation = jazzWalkAnimation;
        player.jazzJumpAnimation = jazzJumpAnimation;

        player.setAnimation(synthWalkAnimation);
        player.synthSpeed = synthSpeed;
        player.jazzSpeed = jazzSpeed;
        player.setTexture(synthDefaultTexture);
        GameController.getInstance().instantiate(player);
    }

    /**
     * Create a bear enemy.
     *
     * @param scale The Vector2 draw scale
     * @param x  The bear's x coordinate (in pixels)
     * @param y The bear's y coordinate (in pixels)
     * @param levelHeight Height of level in number of tiles
     * @param tileSize Height of tile in pixels
     */
    private void createEnemyBear(Vector2 scale, float x, float y, int levelHeight, int tileSize){
        float dwidth  = enemyDefaultTexture.getRegionWidth()/scale.x;
        float dheight = enemyDefaultTexture.getRegionHeight()/scale.y;
        BearEnemy bear;
//        bear = new BearEnemy(, dwidth*enemyScale,
//                dheight*enemyScale, enemyScale, false, bearIdleAnimation);
//        bear.setBodyType(BodyDef.BodyType.StaticBody);
//        bear.setDrawScale(scale);
//        bear.setTexture(enemyDefaultTexture);
//        bear.setName(ename + ii);
//        GameController.getInstance().instantiate(bear);
    }

    private void createGoal(Vector2 scale, float x, float y, int levelHeight, int tileSize){
        //  Adjust and Convert coordinates to world coordinate
        y -= goalTile.getRegionHeight()/2.5;
        Vector2 convertedCoord = convertTiledCoord(x, y, levelHeight, tileSize);

        float dwidth  = goalTile.getRegionWidth()/scale.x;
        float dheight = goalTile.getRegionHeight()/scale.y;

        JsonValue data = defaultConstants.get("goal");
        goalDoor = new BoxGameObject(convertedCoord.x,convertedCoord.y,dwidth,dheight);
        goalDoor.setBodyType(BodyDef.BodyType.StaticBody);
        goalDoor.setDensity(data.getFloat("density", 0));
        goalDoor.setFriction(data.getFloat("friction", 0));
        goalDoor.setRestitution(data.getFloat("restitution", 0));
        goalDoor.setSensor(true);
        goalDoor.setDrawScale(scale);
        goalDoor.setTexture(goalTile);
        goalDoor.setName("goal");
        GameController.getInstance().instantiate(goalDoor);
    }
}
