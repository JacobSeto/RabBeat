/*
 * DudeModel.java
 *
 * You SHOULD NOT need to modify this file.  However, you may learn valuable lessons
 * for the rest of the lab by looking at it.
 *
 * Author: Walker M. White
 * Based on original PhysicsDemo Lab by Don Holden, 2007
 * Updated asset version, 2/6/2021
 */
package edu.cornell.gdiac.rabbeat;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.graphics.g2d.Animation;

import com.badlogic.gdx.utils.JsonValue;
import edu.cornell.gdiac.rabbeat.objects.*;
import edu.cornell.gdiac.rabbeat.sync.ISyncedAnimated;

/**
 * Player avatar for the plaform game.
 * Note that this class returns to static loading.  That is because there are
 * no other subclasses that we might loop through.
 */
public class Player extends CapsuleGameObject implements ISyncedAnimated, IGenreObject {
	/** The initializing data (to avoid magic numbers) */
	private final JsonValue data;

	/** The factor to multiply by the input */
	private final float force;
	/** The amount to slow the character down */
	private final float damping;
	/** The maximum character speed */
	private float maxspeed;
	/** speed of player in Synth mode*/
	public float synthSpeed;
	/** speed of player in Jazz mode*/
	public float jazzSpeed;
	/** The current jump force */
	private float jumpForce;
	/** The impulse for the character jump in Synth */
	private final float jumpForceSynth;
	/** The impulse for the character jump in Jazz */
	private final float jumpForceJazz;
	/** Cooldown (in animation frames) for jumping */
	private final int jumpLimit;

	/** The current horizontal movement of the character */
	private float   movement;
	/** Whether the character is facing right */
	private boolean faceRight;
	/** How long until we can jump again */
	private int jumpCooldown;
	/** Whether we are actively walking */
	private boolean isWalking;
	/** Whether we are actively jumping */
	private boolean isJumping;
	/** Whether our feet are on the ground */
	public boolean isGrounded;
	/** Whether we are dying */
	private boolean isDying;
	/** Identifier to allow us to track the sensor in ContactListener */
	private final String sensorName;

	/** The physics shape of this object */
	private PolygonShape sensorShape;
	
	/** Cache for internal force calculations */
	private final Vector2 forceCache = new Vector2();

	/** The synth genre idle animation for the player */
	public Animation<TextureRegion> synthIdleAnimation;
	/** The synth genre walking animation for the player */
	public Animation<TextureRegion> synthWalkAnimation;
	/** The synth genre jumping animation for the player */
	public Animation<TextureRegion> synthJumpAnimation;
	/** The synth genre fall animation for the player */
	public Animation<TextureRegion> synthFallAnimation;
	/** The synth genre death animation for the player */
	public Animation<TextureRegion> synthDeathAnimation;

	/** The jazz genre idle animation for the player */
	public Animation<TextureRegion> jazzIdleAnimation;
	/** The jazz genre walking animation for the player */
	public Animation<TextureRegion> jazzWalkAnimation;
	/** The jazz genre jumping animation for the player */
	public Animation<TextureRegion> jazzJumpAnimation;
	/** The jazz genre fall animation for the player */
	public Animation<TextureRegion> jazzFallAnimation;
	/** The jazz genre death animation for the player */
	public Animation<TextureRegion> jazzDeathAnimation;

	/** The transform animation for the player */
	public Animation<TextureRegion> transformAnimation;

	/** The player's current animation */
	public Animation<TextureRegion> animation;
	/** The elapsed time for animationUpdate */
	private float stateTime = 0;
	/** A flag to check if the player's animation is jumping */
	private boolean animationIsJumping = false;
	/** A flag to check if the player's animation is dying */
	private boolean animationIsDying = false;

	Genre genre;

	boolean spawning = true;

	/**
	 * Returns left/right movement of this character.
	 * 
	 * This is the result of input times dude force.
	 *
	 * @return left/right movement of this character.
	 */
	public float getMovement() {
		return movement;
	}
	
	/**
	 * Sets left/right movement of this character.
	 * 
	 * This is the result of input times dude force.
	 *
	 * @param value left/right movement of this character.
	 */
	public void setMovement(float value) {
		movement = value; 
		// Change facing if appropriate
		if (movement < 0) {
			faceRight = false;
		} else if (movement > 0) {
			faceRight = true;
		}
	}

	/**
	 * Returns true if the player is actively walking.
	 *
	 * @return true if the player is actively walking.
	 */
	public boolean isWalking() {
		return isWalking;
	}

	/**
	 * Sets whether the player is actively walking.
	 *
	 * @param value whether the player is actively walking.
	 */
	public void setWalking(boolean value) {
		isWalking = value;
	}

	/**
	 * Returns true if the dude is actively jumping.
	 *
	 * @return true if the dude is actively jumping.
	 */
	public boolean isJumping() {
		return isJumping && isGrounded && jumpCooldown <= 0;
	}
	
	/**
	 * Sets whether the dude is actively jumping.
	 *
	 * @param value whether the dude is actively jumping.
	 */
	public void setJumping(boolean value) {
		isJumping = value; 
	}

	/**
	 * Returns the name of the ground sensor
	 *
	 * This is used by ContactListener
	 *
	 * @return the name of the ground sensor
	 */
	public String getSensorName() {
		return sensorName;
	}


	/**
	 * Sets whether the dude is on the ground.
	 *
	 * @param value whether the dude is on the ground.
	 */
	public void setGrounded(boolean value) {
		isGrounded = value; 
	}

	/**
	 * Returns how much force to apply to get the dude moving
	 *
	 * Multiply this by the input to get the movement value.
	 *
	 * @return how much force to apply to get the dude moving
	 */
	public float getForce() {
		return force;
	}

	/**
	 * Returns ow hard the brakes are applied to get a dude to stop moving
	 *
	 * @return ow hard the brakes are applied to get a dude to stop moving
	 */
	public float getDamping() {
		return damping;
	}
	
	/**
	 * Returns the upper limit on dude left-right movement.  
	 *
	 * This does NOT apply to vertical movement.
	 *
	 * @return the upper limit on dude left-right movement.  
	 */
	public float getMaxSpeed() {
		return maxspeed;
	}

	/** The scale of the enemy */
	public float playerScale;

	/**
	 * Returns true if this character is facing right
	 *
	 * @return true if this character is facing right
	 */
	public boolean isFacingRight() {
		return faceRight;
	}

	/** how much the player should be displaced from their current position at any given moment.
	 * Generally called by moving platforms to shift the player so they 'stick' to said platforms
	 * */
	private Vector2 displacement;

	/** Whether the genre switch ability is on cooldown*/
	boolean genreSwitchCooldown = false;

	public void setDisplace(Vector2 displace){
		displacement = displace;
	}
	/**
	 * Creates a new dude avatar with the given physics data
	 *
	 * The size is expressed in physics units NOT pixels.  In order for 
	 * drawing to work properly, you MUST set the drawScale. The drawScale 
	 * converts the physics units to pixels.
	 *
	 * @param data  	The physics constants for this player
	 * @param width		The object width in physics units
	 * @param height	The object height in physics units
	 * @param startX	The starting x position of the player
	 * @param startY	The starting y position of the player
	 * @param playerScale1	The scale of the player
	 */
	public Player(JsonValue data, float startX, float startY, float width, float height, float playerScale1, Genre genre) {
		// The shrink factors fit the image to a tigher hitbox
		super(startX, startY,
				width*data.get("shrink").getFloat( 0 ),
				height*data.get("shrink").getFloat( 1 ));
        setDensity(data.getFloat("density", 0));
		setFriction(data.getFloat("friction", 0));  /// HE WILL STICK TO WALLS IF YOU FORGET
		setFixedRotation(true);

		playerScale = playerScale1;
		maxspeed = (genre == Genre.SYNTH) ? data.get("max_speed").getFloat("synth")
				: data.get("max_speed").getFloat("jazz");

		damping = data.getFloat("damping", 0);
		force = data.getFloat("force", 0);
		jumpForceSynth = data.getFloat( "synth_jump_force", 0 );
		jumpForceJazz = data.getFloat( "jazz_jump_force", 0 );
		jumpLimit = data.getInt( "jump_cool", 0 );
		displacement = new Vector2(0,0);
		this.data = data;

		// Gameplay attributes
		isWalking = false;
		isGrounded = false;
		isJumping = false;
		faceRight = true;

		jumpForce = (genre == Genre.SYNTH) ? jumpForceSynth : jumpForceJazz;
		this.genre = genre;

		jumpCooldown = 0;
		setType(Type.Player);
		sensorName = "SynthGroundSensor";
	}

	/**
	 * Creates the physics Body(s) for this object, adding them to the world.
	 *
	 * This method overrides the base method to keep your ship from spinning.
	 *
	 * @param world Box2D world to store body
	 *
	 * @return true if object allocation succeeded
	 */
	public boolean activatePhysics(World world) {
		// create the box from our superclass
		if (!super.activatePhysics(world)) {
			return false;
		}

		// Ground Sensor
		// -------------
		// We only allow the dude to jump when he's on the ground. 
		// Double jumping is not allowed.
		//
		// To determine whether or not the dude is on the ground, 
		// we create a thin sensor under his feet, which reports 
		// collisions with the world but has no collision response.
		Vector2 sensorCenter = new Vector2(0, -getHeight() / 2);
		FixtureDef sensorDef = new FixtureDef();
		sensorDef.density = data.getFloat("density",0);
		sensorDef.isSensor = true;
		sensorShape = new PolygonShape();
		JsonValue sensorjv = data.get("sensor");
		sensorShape.setAsBox(sensorjv.getFloat("shrink",0)*getWidth()/2.0f,
								 sensorjv.getFloat("height",0), sensorCenter, 0.0f);
		sensorDef.shape = sensorShape;

		// Ground sensor to represent our feet
		Fixture sensorFixture = body.createFixture( sensorDef );
		sensorFixture.setUserData(getSensorName());
		
		return true;
	}
	

	/**
	 * Applies the force to the body of this dude
	 *
	 * This method should be called after the force attribute is set.
	 */
	public void applyForce() {
		if (!isActive()) {
			return;
		}
		
		// Don't want to be moving. Damp out player motion
		if (getMovement() == 0f) {
			forceCache.set(-getDamping()*getVX(),0);
			body.applyForce(forceCache,getPosition(),true);
		}

		if(Math.abs(getMovement() + getVX()) != Math.abs(getMovement()) + Math.abs(getVX())){
			setVX(0);
		}
		forceCache.set(getMovement(),0);
		body.applyForce(forceCache,getPosition(),true);
		// Velocity too high, clamp it
		if (Math.abs(getVX()) >= getMaxSpeed()) {
			setVX(Math.signum(getVX())*getMaxSpeed());
		}


		// Jump!
		if (isJumping()) {
			setVY(getVY() + jumpForce);
		}
	}
	
	/**
	 * Updates the object's physics state and animation based on the player's movement state (NOT GAME LOGIC).
	 *
	 * We use this method to reset cooldowns.
	 *
	 * @param dt	Number of seconds since last animation frame
	 */
	public void update(float dt) {
		if(spawning){
			if(playerAnimFinished()){
				spawning = false;
			}
			else{
				return;
			}
		}
		// Process actions in object model
		setPosition(getPosition().x+ dt*displacement.x, getPosition().y+ dt*displacement.y);

		if(!isDying){
			setWalking(InputController.getInstance().getHorizontal() != 0 && !isJumping);
			setMovement(InputController.getInstance().getHorizontal() * getForce());
			setJumping(InputController.getInstance().didPrimary());
			applyForce();
		}
		// Apply cooldowns
		if (isJumping()) {
			animationIsJumping = true;
			jumpCooldown = jumpLimit;
		} else {
			jumpCooldown = Math.max(0, jumpCooldown - 1);
		}
		animationUpdate();
		setRestitution(0.0f);
		super.update(dt);
	}

	/**
	 * Draws the physics object.
	 *
	 * @param canvas Drawing context
	 */
	public void draw(GameCanvas canvas) {
		float effect = faceRight ? 1.0f : -1.0f;
		TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
		canvas.draw(currentFrame, Color.WHITE,origin.x,origin.y,getX()*drawScale.x,getY()*drawScale.y,getAngle(),
				playerScale*effect,playerScale);
	}
	
	/**
	 * Draws the outline of the physics body.
	 *
	 * This method can be helpful for understanding issues with collisions.
	 *
	 * @param canvas Drawing context
	 */
	public void drawDebug(GameCanvas canvas) {
		super.drawDebug(canvas);
		canvas.drawPhysics(sensorShape,Color.RED,getX(),getY(),getAngle(),drawScale.x,drawScale.y);
	}

	@Override
	public void genreUpdate(Genre genre) {
		if (this.genre != genre) {
			this.genre = genre;
			if (genre == Genre.SYNTH) {
				maxspeed = synthSpeed;
				jumpForce = jumpForceSynth;
			} else {
				maxspeed = jazzSpeed;
				jumpForce = jumpForceJazz;
			}
			//genreSwitchCooldown = true;
		}
	}

	/**
	 * Updates the animation based on the physics state.
	 */
	private void animationUpdate() {
		if (!playerAnimFinished()) {
			return;
		}

		if (isDying && !animationIsDying) {
			switch (genre) {
				case SYNTH:
					setAnimation(synthDeathAnimation);
					break;
				case JAZZ:
					setAnimation(jazzDeathAnimation);
					break;
			}
			animationIsDying = true;
		} else if (isJumping && !animationIsDying && isGrounded) {
			animationIsJumping = true;
			switch (genre) {
				case SYNTH:
					setAnimation(synthJumpAnimation);
					break;
				case JAZZ:
					setAnimation(jazzJumpAnimation);
					break;
			}
		}

		if (animationIsDying) {
			if (jazzDeathAnimation.isAnimationFinished(stateTime) || synthDeathAnimation.isAnimationFinished(stateTime)) {
				if (!GameController.getInstance().isFailure()){
					GameController.getInstance().setFailure(true);
				}
				animationIsDying = false;
			}
		} else {
			if (animationIsJumping) {
				if (animation.isAnimationFinished(stateTime)) {
					animationIsJumping = false;
					switch (genre) {
						case SYNTH:
							setAnimation(synthFallAnimation);
							break;
						case JAZZ:
							setAnimation(jazzFallAnimation);
							break;
					}
				}
			} else if (!isGrounded) {
				switch (genre) {
					case SYNTH:
						setAnimation(synthFallAnimation);
						break;
					case JAZZ:
						setAnimation(jazzFallAnimation);
						break;
				}
			} else if (isWalking()) {
				switch (genre) {
					case SYNTH:
						setAnimation(synthWalkAnimation);
						break;
					case JAZZ:
						setAnimation(jazzWalkAnimation);
						break;
				}
			} else {
				switch (genre) {
					case SYNTH:
						setAnimation(synthIdleAnimation);
						break;
					case JAZZ:
						setAnimation(jazzIdleAnimation);
						break;
				}
			}
		}
	}

	public void setAnimation(Animation<TextureRegion> animation){
		this.animation = animation;
	}

	public void updateAnimationFrame(){
		if(isDying && playerAnimFinished()){
			return;
		}
		stateTime++;
	}
	public float getBeat() {return .5f;}

	public void beatAction(){genreSwitchCooldown = false; }


	public boolean getIsDying(){
		return isDying;
	}
	public void setDying(boolean isDying){
		this.isDying = isDying;
		if(isDying){
			stateTime = 0;
			animation = (genre == Genre.SYNTH ? synthDeathAnimation : jazzDeathAnimation);
			setVX(0);
			setVY(0);
		}

	}

	public boolean playerAnimFinished(){
		return animation.isAnimationFinished(stateTime);
	}
}