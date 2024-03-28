package edu.cornell.gdiac.rabbeat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Queue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.rabbeat.obstacles.*;
import edu.cornell.gdiac.rabbeat.obstacles.enemies.BearEnemy;
import edu.cornell.gdiac.rabbeat.obstacles.platforms.MovingPlatform;
import edu.cornell.gdiac.rabbeat.obstacles.platforms.WeightedPlatform;
import edu.cornell.gdiac.util.Pair;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import edu.cornell.gdiac.util.PooledList;

public class ObjectController {
    /** All the objects in the world. */
    public PooledList<GameObject> objects = new PooledList<>();
    /** All objects that are genre-dependent */
    public PooledList<IGenreObject> genreObjects = new PooledList<>();
    /** Queue for adding objects */
    public PooledList<GameObject> addQueue = new PooledList<>();
    /** Physics constants for initialization
     * TODO: constants has some relevant information for game controller and this class does not care
     * */
    public JsonValue defaultConstants;
    /** Reference to the character avatar */
    public Player player;

    public JsonValue levelJson;

    /** The font for giving messages to the player */
    protected BitmapFont displayFont;

    /** The texture for walls */
    protected TextureRegion blackTile;
    /** The texture for regular platforms */
    protected TextureRegion platformTile;
    /** The texture for end platforms */
    protected TextureRegion endPlatform;
    /** The texture for wire platforms */
    protected TextureRegion wirePlatform;
    /** The texture for radio platforms */
    protected TextureRegion radioPlatform;
    /** The texture for guitar platforms */
    protected TextureRegion guitarPlatform;
    /** The texture for weighted platforms */
    protected TextureRegion weightedPlatform;
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
    public Queue<Pair<BoxGameObject, Integer>> checkpoints = new Queue<Pair<BoxGameObject, Integer>>();

    /** The player scale for synth */
    private float playerScale = 3/8f*2f;

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
        endPlatform = new TextureRegion(directory.getEntry( "world:platforms:endPlatform", Texture.class ));
        wirePlatform = new TextureRegion(directory.getEntry( "world:platforms:wirePlatform", Texture.class ));
        radioPlatform = new TextureRegion(directory.getEntry( "world:platforms:radioPlatform", Texture.class ));
        guitarPlatform = new TextureRegion(directory.getEntry( "world:platforms:guitarPlatform", Texture.class ));
        weightedPlatform = new TextureRegion((directory.getEntry("world:platforms:weightedPlatform", Texture.class)));
        bulletTexture = new TextureRegion(directory.getEntry("world:bullet", Texture.class));
        goalTile  = new TextureRegion(directory.getEntry( "world:goal", Texture.class ));
        checkpointDefault  = new TextureRegion(directory.getEntry( "checkpoint:checkDefault", Texture.class ));
        checkpointActive  = new TextureRegion(directory.getEntry( "checkpoint:checkActive", Texture.class ));
        displayFont = directory.getEntry( "fonts:retro" ,BitmapFont.class);
    }
    public void populateObjects(Vector2 scale){
        if (levelJson.has("layers")) {
            for (JsonValue layer : levelJson.get("layers")) {
                String layerName = layer.getString("name", "");
                switch (layerName){
                    case "background":
                        //do something
                    case "walls":
                        int[] data = layer.get("data").asIntArray();
                        for (int i=0; i<data.length; i++){
                            //TODO: convert walls data to points
//                            createWalls(scale, points);
                        }
                    case "platforms":
                        for (JsonValue platform : layer.get("objects")) {
                            float x = Float.parseFloat(platform.get("x").toString());
                            float y = Float.parseFloat(platform.get("y").toString());
                            // TODO: convert x, y coord. to game/physics coord
                            createPlatform(scale, platform.get("type").toString(), x, y);
                        }
                    case "platformArt":
                        for (JsonValue a : layer.get("objects")) {
                            float x = Float.parseFloat(a.get("x").toString());
                            float y = Float.parseFloat(a.get("y").toString());
                            // TODO: convert x, y coord. to game/physics coord
                            createPlatformArt(scale, a.get("type").toString(), x, y);
                        }
                    case "player":
                        JsonValue player = layer.get("objects");
                        float x = Float.parseFloat(player.get("x").toString());
                        float y = Float.parseFloat(player.get("y").toString());
                        // TODO: convert x, y coord. to game/physics coord
                        createPlayer(scale, x, y);
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
//        String wname = "wall";
//        JsonValue walljv = constants.get("walls");
//        JsonValue defaults = constants.get("defaults");
//        for (int ii = 0; ii < walljv.size; ii++) {
//            PolygonGameObject obj;
//            obj = new PolygonGameObject(walljv.get(ii).asFloatArray(), 0, 0);
//            obj.setBodyType(BodyDef.BodyType.StaticBody);
//            obj.setDensity(defaults.getFloat( "density", 0.0f ));
//            obj.setFriction(defaults.getFloat( "friction", 0.0f ));
//            obj.setRestitution(defaults.getFloat( "restitution", 0.0f ));
//            obj.setDrawScale(scale);
//            obj.setTexture(blackTile);
//            obj.setName(wname+ii);
//            GameController.getInstance().instantiate(obj);
//        }
//
//        createPlatforms(scale, "default");
//        createPlatforms(scale, "defaultEnd");
//        createPlatforms(scale, "wire");
//        createPlatforms(scale, "radio");
//        createPlatforms(scale, "guitar");
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
     * Create the start tile and checkpoints
     */
    public void createCheckpoints(Vector2 scale) {
//        float checkpointWidth  = checkpointDefault.getRegionWidth()/scale.x;
//        float checkpointHeight = checkpointDefault.getRegionHeight()/scale.y;
//
//        // Add the start tile as the current spawn point
//        JsonValue start = constants.get("start");
//        JsonValue startPos = start.get("pos");
//        BoxGameObject startTile = new BoxGameObject(startPos.getFloat(0), startPos.getFloat(1), checkpointWidth, checkpointHeight);
//        startTile.setBodyType(BodyDef.BodyType.StaticBody);
//        startTile.setDensity(start.getFloat("density", 0));
//        startTile.setFriction(start.getFloat("friction", 0));
//        startTile.setRestitution(start.getFloat("restitution", 0));
//        startTile.setSensor(true);
//        startTile.setDrawScale(scale);
//        startTile.setTexture(checkpointDefault);
//        startTile.setName("start");
//        GameController.getInstance().instantiate(startTile);
//        //set respawn point to position of respawnPoint
//        GameController.getInstance().setSpawn(startTile.getPosition());
//
//        // Populate all checkpoints
//        for (int i = 0; i < constants.get("checkpoints").size; i++) {
//            String cname = "checkpoint";
//            JsonValue checkpoint = constants.get("checkpoints").get(i);
//            JsonValue checkpointPos = checkpoint.get("pos");
//            BoxGameObject obj = new BoxGameObject(checkpointPos.getFloat(0), checkpointPos.getFloat(1), checkpointWidth, checkpointHeight);
//            obj.setBodyType(BodyDef.BodyType.StaticBody);
//            obj.setDensity(checkpoint.getFloat("density", 0));
//            obj.setFriction(checkpoint.getFloat("friction", 0));
//            obj.setRestitution(checkpoint.getFloat("restitution", 0));
//            obj.setSensor(true);
//            obj.setDrawScale(scale);
//            obj.setTexture(checkpointDefault);
//            obj.setName(cname + i);
//            GameController.getInstance().instantiate(obj);
//            checkpoints.addLast(new Pair<>(obj, i));
//        }
    }
    public void createWalls(Vector2 scale, float[] points){
        String wname = "wall";
        JsonValue defaults = defaultConstants.get("defaults");
        PolygonGameObject obj;
        obj = new PolygonGameObject(points, 0, 0);
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
     * Create a platform using the scale and type given.
     *
     * @param scale The Vector2 draw scale
     * @param type A string, either "default", "defaultEnd", "wire", "radio", "guitar"
     */
    public void createPlatform(Vector2 scale, String type, float x, float y){
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
        JsonValue defaults = defaultConstants.get("defaults");
        float dwidth  = textureRegion.getRegionWidth()/scale.x;
        float dheight = textureRegion.getRegionHeight()/scale.y;
        BoxGameObject platform;
        platform = new BoxGameObject(x, y, dwidth, dheight);
        platform.setBodyType(BodyDef.BodyType.StaticBody);
        platform.setDensity(defaults.getFloat( "density", 0.0f ));
        platform.setFriction(defaults.getFloat( "friction", 0.0f ));
        platform.setRestitution(defaults.getFloat( "restitution", 0.0f ));
        platform.setDrawScale(scale);
        platform.setTexture(textureRegion);
        platform.setName(type);
        GameController.getInstance().instantiate(platform);
    }

    public void createPlatformArt(Vector2 scale, String type, float x, float y){
        TextureRegion textureRegion;
        switch(type){
            default:
                textureRegion = wirePlatform;
                break;
            case "radio":
                textureRegion = radioPlatform;
                break;
            case "guitar":
                textureRegion = guitarPlatform;
                break;
        }
        float dwidth  = textureRegion.getRegionWidth()/scale.x;
        float dheight = textureRegion.getRegionHeight()/scale.y;
        //TODO Art objects
    }
    public void createPlayer(Vector2 scale, float startX, float startY){
        //TODO: Figure out if having 2 refrences for player fields is okay
        float dwidth  = synthDefaultTexture.getRegionWidth()/scale.x;
        float dheight = synthDefaultTexture.getRegionHeight()/scale.y;
        player = new Player(defaultConstants.get("player"), startX, startY,
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
}
