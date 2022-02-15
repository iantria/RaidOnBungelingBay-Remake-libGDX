package com.iantria.raidgame.util;

public class Score {

    public int id;
    public int isPlayer;
    public String name;
    public String deviceType;
    public int androidVersion;
    public String screenSize;
    public int lowestFPS;
    public int score;
    public int isWin;
    public int timeToWin;
    public int isCarrierLost;
    public int helicoptersLost;
    public int isEnemyShipCompleted;
    public String gameVersion;
    public int season;
    public String date;

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

}
