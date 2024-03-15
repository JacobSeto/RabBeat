package edu.cornell.gdiac.rabbeat;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Queue;
import edu.cornell.gdiac.assets.AssetDirectory;
import edu.cornell.gdiac.rabbeat.obstacles.BoxGameObject;
import edu.cornell.gdiac.rabbeat.obstacles.PolygonGameObject;
import edu.cornell.gdiac.rabbeat.obstacles.enemies.BearEnemy;
import edu.cornell.gdiac.rabbeat.obstacles.enemies.SyncedProjectile;
import edu.cornell.gdiac.rabbeat.obstacles.platforms.WeightedPlatform;
import edu.cornell.gdiac.util.Pair;

public class ObjectController {
    /** Physics constants for initialization
     * TODO: constants has some relevant information for game controller and this class does not care
     * */
    public JsonValue constants;
    /** Reference to the character avatar */
    public Player player;


    /** The font for giving messages to the player */
    protected BitmapFont displayFont;

    /** The texture for walls */
    protected TextureRegion blackTile;
    /** The texture for regular platforms */
    protected TextureRegion platformTile;
    /** The texture for weighted platforms */
    protected TextureRegion weightedPlatform;
    /** The texture for bullets */
    public TextureRegion bulletTexture;
    /** The texture for the exit condition */
    protected TextureRegion goalTile;
    public TextureRegion backgroundTexture;
    private TextureRegion enemyDefaultTexture;

    /** Reference to the goalDoor (for collision detection) */
    public BoxGameObject goalDoor;

    /** Reference to all the checkpoints */
    public Queue<Pair<BoxGameObject, Integer>> checkpoints = new Queue<Pair<BoxGameObject, Integer>>();;

    public Array<SyncedProjectile> bullets = new Array<>();

    /** The player scale for synth */
    private float playerScale = 3/8f;

    private TextureRegion synthDefaultTexture;
    private TextureRegion synthJazzTexture;

    private float synthSpeed;
    private float jazzSpeed;

    public BearEnemy enemy;

    /** The enemy scale for the enemy */
    private float enemyScale = 3/8f;



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
        enemyDefaultTexture = new TextureRegion(directory.getEntry("player:synth",Texture.class)); //CHANGE FOR ENEMY!

        constants = directory.getEntry( "constants", JsonValue.class );
        synthSpeed =  constants.get("bunny").get("max_speed").getFloat("synth");
        jazzSpeed = constants.get("bunny").get("max_speed").getFloat("jazz");
        synthDefaultTexture = new TextureRegion(directory.getEntry("player:synth",Texture.class));
        synthJazzTexture = new TextureRegion(directory.getEntry("player:synth-jazz",Texture.class));

        // Allocate the tiles
        blackTile = new TextureRegion(directory.getEntry( "world:platforms:blackTile", Texture.class ));
        platformTile = new TextureRegion(directory.getEntry( "world:platforms:platformTile", Texture.class ));
        weightedPlatform = new TextureRegion((directory.getEntry("world:platforms:weightedPlatform", Texture.class)));
        bulletTexture = new TextureRegion(directory.getEntry("world:bullet", Texture.class));
        goalTile  = new TextureRegion(directory.getEntry( "world:goal", Texture.class ));
        displayFont = directory.getEntry( "fonts:retro" ,BitmapFont.class);
    }
    public void populateObjects(Vector2 scale){
        // Repopulate current checkpoints
        float checkpointWidth  = goalTile.getRegionWidth()/scale.x;
        float checkpointHeight = goalTile.getRegionHeight()/scale.y;

        Queue<Pair<BoxGameObject, Integer>> newCheckpoints = new Queue<>();
        for (Pair<BoxGameObject, Integer> pair : checkpoints) {
            String cname = "checkpoint";
            JsonValue checkpoint = constants.get("checkpoints").get(pair.snd);
            JsonValue checkpointPos = checkpoint.get("pos");
            BoxGameObject obj = new BoxGameObject(checkpointPos.getFloat(0), checkpointPos.getFloat(1), checkpointWidth, checkpointHeight);
            obj.setBodyType(BodyDef.BodyType.StaticBody);
            obj.setDensity(checkpoint.getFloat("density", 0));
            obj.setFriction(checkpoint.getFloat("friction", 0));
            obj.setRestitution(checkpoint.getFloat("restitution", 0));
            obj.setSensor(true);
            obj.setDrawScale(scale);
            obj.setTexture(goalTile);
            obj.setName(cname + pair.snd);
            GameController.getInstance().instantiate(obj);
            newCheckpoints.addLast(new Pair<BoxGameObject, Integer>(obj, pair.snd));
        }
        checkpoints.clear();
        checkpoints = newCheckpoints;

        // Add level goal
        float dwidth  = goalTile.getRegionWidth()/scale.x;
        float dheight = goalTile.getRegionHeight()/scale.y;

        JsonValue goal = constants.get("goal");
        JsonValue goalpos = goal.get("pos");
        goalDoor = new BoxGameObject(goalpos.getFloat(0),goalpos.getFloat(1),dwidth,dheight);
        goalDoor.setBodyType(BodyDef.BodyType.StaticBody);
        goalDoor.setDensity(goal.getFloat("density", 0));
        goalDoor.setFriction(goal.getFloat("friction", 0));
        goalDoor.setRestitution(goal.getFloat("restitution", 0));
        goalDoor.setSensor(true);
        goalDoor.setDrawScale(scale);
        goalDoor.setTexture(goalTile);
        goalDoor.setName("goal");
        GameController.getInstance().instantiate(goalDoor);

        String wname = "wall";
        JsonValue walljv = constants.get("walls");
        JsonValue defaults = constants.get("defaults");
        for (int ii = 0; ii < walljv.size; ii++) {
            PolygonGameObject obj;
            obj = new PolygonGameObject(walljv.get(ii).asFloatArray(), 0, 0);
            obj.setBodyType(BodyDef.BodyType.StaticBody);
            obj.setDensity(defaults.getFloat( "density", 0.0f ));
            obj.setFriction(defaults.getFloat( "friction", 0.0f ));
            obj.setRestitution(defaults.getFloat( "restitution", 0.0f ));
            obj.setDrawScale(scale);
            obj.setTexture(blackTile);
            obj.setName(wname+ii);
            GameController.getInstance().instantiate(obj);
        }

        String pname = "platform";
        JsonValue platjv = constants.get("platforms");
        for (int ii = 0; ii < platjv.size; ii++) {
            PolygonGameObject obj;
            obj = new PolygonGameObject(platjv.get(ii).asFloatArray(), 0, 0);
            obj.setBodyType(BodyDef.BodyType.StaticBody);
            obj.setDensity(defaults.getFloat( "density", 0.0f ));
            obj.setFriction(defaults.getFloat( "friction", 0.0f ));
            obj.setRestitution(defaults.getFloat( "restitution", 0.0f ));
            obj.setDrawScale(scale);
            obj.setTexture(platformTile);
            obj.setName(pname+ii);
            GameController.getInstance().instantiate(obj);
        }

        String wpname = "wplatform";
        JsonValue wplatjv = constants.get("wplatforms");
        for (int ii = 0; ii < wplatjv.size; ii++) {
            JsonValue currentWP = wplatjv.get(ii);
            WeightedPlatform obj;
            obj = new WeightedPlatform(currentWP.get("pos").asFloatArray(), currentWP.get("synthPos").asFloatArray(),
                    currentWP.get("jazzPos").asFloatArray());
            obj.setBodyType(BodyDef.BodyType.StaticBody);
            obj.setDensity(defaults.getFloat("density", 0.0f));
            obj.setFriction(defaults.getFloat("friction", 0.0f));
            obj.setRestitution(defaults.getFloat("restitution", 0.0f));
            obj.setDrawScale(scale);
            obj.setTexture(weightedPlatform);
            obj.setName(wpname + ii);
            GameController.getInstance().instantiate(obj);
        }

        //TODO: Load enemies
        dwidth  = enemyDefaultTexture.getRegionWidth()/scale.x;
        dheight = enemyDefaultTexture.getRegionHeight()/scale.y;

        enemy = new BearEnemy(constants.get("enemy"), dwidth*enemyScale, dheight*enemyScale, enemyScale, false);
        enemy.setDrawScale(scale);
        enemy.setTexture(enemyDefaultTexture);
        GameController.getInstance().instantiate(enemy);

        // Create bunny

        //TODO: Figure out if having 2 refrences for player fields is okay
        dwidth  = synthDefaultTexture.getRegionWidth()/scale.x;
        dheight = synthDefaultTexture.getRegionHeight()/scale.y;
        player = new Player(constants.get("bunny"), dwidth*playerScale, dheight*playerScale, playerScale);
        player.setDrawScale(scale);
        player.synthDefaultTexture = synthJazzTexture;
        player.jazzDefaultTexture = synthJazzTexture;
        player.synthSpeed = synthSpeed;
        player.jazzSpeed = jazzSpeed;
        player.setTexture(synthDefaultTexture);
        GameController.getInstance().instantiate(player);

        //create checkpoints
        createCheckpoints(scale);
    }

    /**
     * Create the start tile and checkpoints
     */
    public void createCheckpoints(Vector2 scale) {
        float checkpointWidth  = goalTile.getRegionWidth()/scale.x;
        float checkpointHeight = goalTile.getRegionHeight()/scale.y;

        // Add the start tile as the current spawn point
        JsonValue start = constants.get("start");
        JsonValue startPos = start.get("pos");
        BoxGameObject startTile = new BoxGameObject(startPos.getFloat(0), startPos.getFloat(1), checkpointWidth, checkpointHeight);
        startTile.setBodyType(BodyDef.BodyType.StaticBody);
        startTile.setDensity(start.getFloat("density", 0));
        startTile.setFriction(start.getFloat("friction", 0));
        startTile.setRestitution(start.getFloat("restitution", 0));
        startTile.setSensor(true);
        startTile.setDrawScale(scale);
        startTile.setTexture(goalTile);
        startTile.setName("start");
        GameController.getInstance().instantiate(startTile);
        //set respawn point to position of respawnPoint
        GameController.getInstance().setSpawn(startTile);

        // Populate all checkpoints
        for (int i = 0; i < constants.get("checkpoints").size; i++) {
            String cname = "checkpoint";
            JsonValue checkpoint = constants.get("checkpoints").get(i);
            JsonValue checkpointPos = checkpoint.get("pos");
            BoxGameObject obj = new BoxGameObject(checkpointPos.getFloat(0), checkpointPos.getFloat(1), checkpointWidth, checkpointHeight);
            obj.setBodyType(BodyDef.BodyType.StaticBody);
            obj.setDensity(checkpoint.getFloat("density", 0));
            obj.setFriction(checkpoint.getFloat("friction", 0));
            obj.setRestitution(checkpoint.getFloat("restitution", 0));
            obj.setSensor(true);
            obj.setDrawScale(scale);
            obj.setTexture(goalTile);
            obj.setName(cname + i);
            GameController.getInstance().instantiate(obj);
        }
    }

    public void removeBullet(SyncedProjectile bullet) {
        bullet.markRemoved(true);
        bullets.removeValue(bullet, true);
    }

    public void removeBullets(Array<SyncedProjectile> obs){
        for (int i = 0; i < obs.size; i++){
            obs.get(i).markRemoved(true);
            bullets.removeIndex(i);
        }
    }

}
