package com.iantria.raidgame.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.iantria.raidgame.util.Constants;

public class GameMap extends Entity {

    public GameMap(String id, float scale, boolean isMovingObj, Vector2 position, float rotation, TextureRegion image) {
        super(id, scale, isMovingObj, position, rotation, image);

        this.position = position;
        this.direction = 0f;
        this.rotation = rotation;

        vector1 = position;
        vector2 = new Vector2(position.x, position.y - 2000);
        vector3 = new Vector2(position.x - 3200, position.y - 2000);
        vector4 = new Vector2(position.x - 3200, position.y);
        updateMapSegments();
    }

    public void updateMapSegments() {

        if (position.x > Constants.WINDOW_WIDTH/2f) position.x = -(Constants.MAP_WIDTH - Constants.WINDOW_WIDTH/2f) + (position.x-Constants.WINDOW_WIDTH/2f) ;
        if (position.x < -Constants.MAP_WIDTH) position.x = Constants.MAP_WIDTH + position.x ;

        if (position.y > Constants.WINDOW_HEIGHT/2f) position.y = -(Constants.MAP_HEIGHT - Constants.WINDOW_HEIGHT/2f) + (position.y - Constants.WINDOW_HEIGHT/2f);
        if (position.y < -Constants.MAP_HEIGHT) position.y = Constants.MAP_HEIGHT + position.y;

        // Left Right Map temp1 and temp2
        if (position.x <= (Constants.MAP_WIDTH/2f) && position.x >= 0) { // if mapx is between 0..map_width/2
            //System.out.println("Move 2nd map left side");
            temp2_x = -(Constants.MAP_WIDTH - position.x);
        } else if (position.x < -(Constants.MAP_WIDTH/2f) && position.x > -Constants.MAP_WIDTH){  // if mapx is between -map_width/2..-map_width
            //System.out.println("Move 2nd map right side - 2nd if");
            temp2_x = Constants.MAP_WIDTH + position.x;
        } else if (position.x > (Constants.MAP_WIDTH/2f) && position.x < Constants.MAP_WIDTH){ // if mapx is between map_width/2..map_width
            //System.out.println("Move 2nd map right side - 3rd if");
            temp2_x = Constants.MAP_WIDTH + position.x;
        } else if (position.x < 0 && position.x > -Constants.MAP_WIDTH/2){ // if mapx is between 0..-map_width/2
            //System.out.println("Move 2nd map left side - 4th if");
            temp2_x =  -(Constants.MAP_WIDTH - position.x);;
        } else {
            //System.out.println("2nd map dont know");
            temp2_x = -Constants.MAP_WIDTH + position.x;
        }
        temp2_y = position.y;

        //Original
        //        if (position.x > Constants.WINDOW_WIDTH/2f) position.x = -(Constants.MAP_WIDTH - Constants.WINDOW_WIDTH/2f) + (position.x-Constants.WINDOW_WIDTH/2f) ;
//        if (position.x < -Constants.MAP_WIDTH) position.x = Constants.MAP_WIDTH + position.x ;
//
//        if (position.y > Constants.WINDOW_HEIGHT/2f) position.y = -(Constants.MAP_HEIGHT - Constants.WINDOW_HEIGHT/2f) + (position.y - Constants.WINDOW_HEIGHT/2f);
//        if (position.y < -Constants.MAP_HEIGHT) position.y = Constants.MAP_HEIGHT + position.y;
//
//        if (position.x < (Constants.WINDOW_WIDTH) && position.x > 0) {
//            temp2_x = -(Constants.MAP_WIDTH - position.x);
//        } else if (position.x < -(Constants.MAP_WIDTH - Constants.WINDOW_WIDTH) && position.x > -Constants.MAP_WIDTH){
//            temp2_x = Constants.MAP_WIDTH + position.x;
//        } else {
//            temp2_x = Constants.MAP_WIDTH + position.x;
//        }
//        temp2_y = position.y;

        //System.out.println("posy:" +position.y);
        if (position.y < (Constants.MAP_HEIGHT/2f) && position.y >= 0) { // if mapy between 0..map_height/2
            temp3_y = -(Constants.MAP_HEIGHT - position.y);
            //System.out.println("1ST if");
        } else if (position.y > (Constants.MAP_HEIGHT/2f) && position.y < Constants.MAP_HEIGHT) { // if mapy between map_height/2..map_height
            temp3_y = Constants.MAP_HEIGHT + position.y;
            //System.out.println("2ST if");
        } else if (position.y < -(Constants.MAP_HEIGHT/2f) && position.y > -Constants.MAP_HEIGHT) { // if mapy between -map_height/2..-map_height
            temp3_y = Constants.MAP_HEIGHT + position.y;
            //System.out.println("3ST if");
        } else if (position.y < 0 && position.y > -Constants.MAP_HEIGHT/2f) { // if mapy between 0..-map_height/2
            temp3_y = -(Constants.MAP_HEIGHT - position.y);
            //System.out.println("4ST if");
        } else {
            temp3_y = Constants.MAP_HEIGHT + position.y;
            //System.out.println("do not know");
        }

//        if (position.y < (Constants.WINDOW_HEIGHT) && position.y > 0) {
//            temp3_y = -(Constants.MAP_HEIGHT - position.y);
//        } else if (position.y < -(Constants.MAP_HEIGHT - Constants.WINDOW_HEIGHT) && position.y > -Constants.MAP_HEIGHT){
//            temp3_y = Constants.MAP_HEIGHT + position.y;
//        } else {
//            temp3_y = Constants.MAP_HEIGHT + position.y;
//        }

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
    }
}
