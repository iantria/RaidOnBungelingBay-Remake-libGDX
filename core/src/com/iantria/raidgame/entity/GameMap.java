package com.iantria.raidgame.entity;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.iantria.raidgame.util.Constants;

public class GameMap extends Entity {

    private ShaderProgram shader;
    private Texture shaderTexture;
    private float shaderTime = 0f;

    public GameMap(String id, float scale, boolean isMovingObj, Vector2 position, float rotation, TextureRegion image) {
        super(id, scale, isMovingObj, position, rotation, image);

        this.position = position;
        this.direction = 0f;
        this.rotation = rotation;

        setVector1(position);
        setVector2(new Vector2(position.x, position.y - 2000));
        setVector3(new Vector2(position.x - 3200, position.y - 2000));
        setVector4(new Vector2(position.x - 3200, position.y));

        updateMapSegments();
    }


    public void updateMapSegments() {
        if (position.x > Constants.WINDOW_WIDTH/2f) position.x = -(Constants.MAP_WIDTH - Constants.WINDOW_WIDTH/2f) + (position.x-Constants.WINDOW_WIDTH/2f) ; // 400 3200 400
        if (position.x < -Constants.MAP_WIDTH) position.x = Constants.MAP_WIDTH + position.x ;

        if (position.y > Constants.WINDOW_HEIGHT/2f) position.y = -(Constants.MAP_HEIGHT - Constants.WINDOW_HEIGHT/2f) + (position.y - Constants.WINDOW_HEIGHT/2f);
        if (position.y < -Constants.MAP_HEIGHT) position.y = Constants.MAP_HEIGHT + position.y;

        if (position.x < (Constants.WINDOW_WIDTH) && position.x > 0) {
            temp2_x = -(Constants.MAP_WIDTH - position.x);
        } else if (position.x < -(Constants.MAP_WIDTH - Constants.WINDOW_WIDTH) && position.x > -Constants.MAP_WIDTH){
            temp2_x = Constants.MAP_WIDTH + position.x;
        } else {
            temp2_x = Constants.MAP_WIDTH + position.x;
        }
        temp2_y = position.y;

        if (position.y < (Constants.WINDOW_HEIGHT) && position.y > 0) {
            temp3_y = -(Constants.MAP_HEIGHT - position.y);
        } else if (position.y < -(Constants.MAP_HEIGHT - Constants.WINDOW_HEIGHT) && position.y > -Constants.MAP_HEIGHT){
            temp3_y = Constants.MAP_HEIGHT + position.y;
        } else {
            temp3_y = Constants.MAP_HEIGHT + position.y;
        }
        temp3_x = position.x;

        vector1.x = position.x;
        vector1.y = position.y;
        vector2.x = temp2_x;
        vector2.y = temp2_y;
        vector3.x = temp3_x;
        vector3.y = temp3_y;
        vector4.x = temp2_x;
        vector4.y = temp3_y;

    }

    public void draw(SpriteBatch batch){

        if (shader == null ){

            Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth()/40, Gdx.graphics.getHeight()/40, Pixmap.Format.RGBA8888 );
            shaderTexture = new Texture(pixmap);
            pixmap.dispose();
            shader = new ShaderProgram(batch.getShader().getVertexShaderSource(), Gdx.files.internal("shaders/ocean_water.frag").readString());
            ShaderProgram.pedantic = false;
            if (!shader.isCompiled()){
                System.out.println("Error compiling shader: " + shader.getLog());
            }
            //System.out.println("tex:" + shaderTexture.getWidth());
        }

        if (Constants.mapID == 1 || Constants.mapID == 2) {
            batch.draw(Constants.oceanTextureRegion, vector1.x, vector1.y, 3200, 2000);
            batch.draw(Constants.oceanTextureRegion, vector2.x, vector2.y, 3200, 2000);
            batch.draw(Constants.oceanTextureRegion, vector3.x, vector3.y, 3200, 2000);
            batch.draw(Constants.oceanTextureRegion, vector4.x, vector4.y, 3200, 2000);

            if (Constants.mapID == 1 ) {
                shaderTime += Gdx.graphics.getDeltaTime();
                batch.setShader(shader);
                shader.bind();
                shader.setUniformf("u_time", shaderTime);

                batch.draw(shaderTexture, vector1.x, vector1.y, 3200, 2000);
                batch.draw(shaderTexture, vector2.x, vector2.y, 3200, 2000);
                batch.draw(shaderTexture, vector3.x, vector3.y, 3200, 2000);
                batch.draw(shaderTexture, vector4.x, vector4.y, 3200, 2000);

                batch.setShader(null);
            }
        }

        batch.draw(image, vector1.x, vector1.y, 3200, 2000);
        batch.draw(image, vector2.x, vector2.y, 3200, 2000);
        batch.draw(image, vector3.x, vector3.y, 3200, 2000);
        batch.draw(image, vector4.x, vector4.y, 3200, 2000);

    }

    public void update(float delta, float helicopterDirection) {

        this.direction = helicopterDirection;

        Constants.map_dx = position.x;
        Constants.map_dy = position.y;

        float hip = delta * speed;

        position.x += hip * java.lang.Math.sin(java.lang.Math.toRadians(direction));
        position.y -= hip * java.lang.Math.cos(java.lang.Math.toRadians(direction));

        Constants.map_dx -= position.x;
        Constants.map_dy -= position.y;

        updateMapSegments();

//        System.out.println("Map direction:" + getDirection() + "   X:" + hip *java.lang.Math.sin(java.lang.Math.toRadians(direction)) + "    Y:" + hip *java.lang.Math.cos(java.lang.Math.toRadians(direction)));

    }


}
