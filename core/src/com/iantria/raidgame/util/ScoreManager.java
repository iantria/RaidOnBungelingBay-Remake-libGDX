package com.iantria.raidgame.util;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import java.util.LinkedList;

public class ScoreManager {

    public Network networkSaveScore;
    public Network networkGetHighScores;
    public Score myScore;

    /*
    +-------------------------+-------------+------+-----+-------------------+-------------------+
    | Field                   | Type        | Null | Key | Default           | Extra             |
    +-------------------------+-------------+------+-----+-------------------+-------------------+
    | id                      | int         | NO   | PRI | NULL              | auto_increment    |
    | is_player               | int         | YES  |     | NULL              |                   |
    | name                    | varchar(12) | YES  |     | NULL              |                   |
    | device_type             | varchar(45) | YES  |     | NULL              |                   |
    | android_version         | int         | YES  |     | NULL              |                   |
    | screen_size             | varchar(45) | YES  |     | NULL              |                   |
    | lowest_fps              | int         | YES  |     | NULL              |                   |
    | score                   | int         | YES  |     | NULL              |                   |
    | is_win                  | int         | YES  |     | NULL              |                   |
    | time_to_win             | int         | YES  |     | NULL              |                   |
    | is_carrier_lost         | int         | YES  |     | NULL              |                   |
    | heli_lost               | int         | YES  |     | NULL              |                   |
    | is_enemy_ship_completed | int         | YES  |     | NULL              |                   |
    | game_version            | varchar(20) | YES  |     | NULL              |                   |
    | season                  | int         | YES  |     | NULL              |                   |
    | game_date               | datetime    | YES  |     | CURRENT_TIMESTAMP | DEFAULT_GENERATED |
    +-------------------------+-------------+------+-----+-------------------+-------------------+
     */

    public ScoreManager() {
        myScore = new Score();
    }

    public void setGameValues(){
        myScore.screenSize = Gdx.graphics.getWidth() + "x" + Gdx.graphics.getHeight();
        myScore.lowestFPS = Constants.lowestFPS;
        myScore.score = Statistics.score;
        myScore.timeToWin = (int) Statistics.gameTime;
        myScore.helicoptersLost = Statistics.numberOfLivesLost;
        myScore.deviceType = Gdx.app.getType().name();
        myScore.season = Constants.SEASON;
        myScore.gameVersion = Constants.VERSION;

        if (Constants.isPlayer) {
            myScore.isPlayer = 1;
            myScore.name = "Human";
        }
        else {
            myScore.isPlayer = 0;
            myScore.name = "Demo_AI";
        }

        if (Gdx.app.getType() == Application.ApplicationType.Android)
            myScore.androidVersion = Gdx.app.getVersion();
        else
            myScore.androidVersion = -1;

        if (Statistics.youWon)
            myScore.isWin = 1;
        else
            myScore.isWin = 0;

        if (Statistics.carrierSurvived)
            myScore.isCarrierLost = 0;
        else
            myScore.isCarrierLost = 1;

        if (Statistics.enemyShipWasCompleted)
            myScore.isEnemyShipCompleted = 1;
        else
            myScore.isEnemyShipCompleted = 0;
    }

    public void saveScore(){
        networkSaveScore = new Network(Constants.NETWORK_SERVICES_SCORES_API, "service=1&data=" + toString());
    }

    public void retrieveHighScores(){
        networkGetHighScores = new Network(Constants.NETWORK_SERVICES_SCORES_API, "service=2&season=" + Constants.SEASON);
    }

    public String toString(){
        return myScore.isPlayer +";" + myScore.name + ";" + myScore.deviceType + ";" + myScore.androidVersion + ";" +myScore.screenSize + ";" +
                myScore.lowestFPS + ";" + myScore.score + ";" + myScore.isWin + ";" + myScore.timeToWin + ";" + myScore.isCarrierLost + ";" + myScore.helicoptersLost + ";" +
                myScore.isEnemyShipCompleted + ";" + myScore.gameVersion +";" + myScore.season;
    }

    public LinkedList<Score> getHighScoresList() {
        LinkedList<Score> scores = new LinkedList<Score>();
        String[] lines = networkGetHighScores.result.split("\n");

        for (int x = 0 ; x < lines.length ; x++){
            String[] parts = lines[x].split(";");
            Score score = new Score();
            score.isPlayer = convertToInt(parts[0]);
            score.name = parts[1];
            score.deviceType = parts[2];
            score.score = convertToInt(parts[3]);
            score.isWin = convertToInt(parts[4]);
            score.timeToWin = convertToInt(parts[5]);
            score.isCarrierLost = convertToInt(parts[6]);
            score.helicoptersLost = convertToInt(parts[7]);
            score.isEnemyShipCompleted = convertToInt(parts[8]);
            score.date = parts[9];
            scores.add(score);
        }
        return scores;
    }

    // Function to convert String to int
    public int convertToInt(String str) {
        int val = 0;
        try {
            val = Integer.parseInt(str);
        }
        catch (NumberFormatException e) {
            val = -1;
        }
        return val;
    }
}
