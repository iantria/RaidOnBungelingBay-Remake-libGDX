package com.iantria.raidgame.entity;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iantria.raidgame.util.Constants;

public class SecretBase extends Entity {



    public SecretBase(String id, float scale, boolean isMovingObj, Vector2 position, float rotation, TextureRegion image) {
        super(id, scale, isMovingObj, position, rotation, image);
        startingPosition = new Vector2(position);
        init();
    }

    public void init() {
        type = EntityType.SECRET_BASE;
        vector1 = new Vector2(position);
        vector2 = new Vector2(position);
        vector3 = new Vector2(position);
        vector4 = new Vector2(position);
        updateVectorsForStationaryObjects();
    }

    public void reset() {
        this.position.x = Constants.gameMap.position.x + startingPosition.x;
        this.position.y = Constants.gameMap.position.y + startingPosition.y;
        updateVectorsForStationaryObjects();
    }

    public void update(float delta) {
        updateVectorsForStationaryObjects();
    }

    public void draw(Batch batch) {

//        batch.draw(image, vector1.x, vector1.y,57,11);
//        batch.draw(image, vector2.x, vector2.y,57,11);
//        batch.draw(image, vector3.x, vector3.y,57,11);
//        batch.draw(image, vector4.x, vector4.y,57,11);
    }
}
