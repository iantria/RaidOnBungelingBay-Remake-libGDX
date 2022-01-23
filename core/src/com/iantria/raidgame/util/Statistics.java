package com.iantria.raidgame.util;

public class Statistics {

	//Stats
	public static int numberOfTimesHitByCruiseMissile;
	public static int numberOfTimesHitByAAGun;
	public static int numberOfTimesHitByFighter;
	public static int numberOfRanOutFuel;
	public static int numberOfBombsDropped;
	public static int numberOfBombsLanded;
	public static int numberOfCannonRoundsFired;
	public static int numberOfCannonRoundsLanded;
	public static int numberOfLandings;
	public static int numberOfFightersDestroyed;
	public static int numberOfBombersDestroyed;
	public static int numberOfAAGunsDestroyed;
	public static int numberOfCruiseMissilesDestroyed;
	public static int numberOfFactoriesDestroyed;
	
	public static int numberOfRadarsDestroyed;
	public static int numberOfBoatsDestroyed;
	public static int numberOfTanksDestroyed;
	public static int numberOfLivesLost;
	
	public static boolean carrierSurvived;
	public static boolean youWon;
	public static boolean enemyShipWasCompleted;
	public static int score;

	public static int amountOfDamageTaken;
	public static int amountOfDamageDealt;
	public static int amountOfCarrierDamageTaken;
	public static float gameTime;
	public static int amountOfFuelUsed;
	public static int numberOfTimesAAGunFired;
	public static int numberOfTimesFighterFired;
	public static int numberOfTimesCruiseMissileFired;		
	
	public static void resetScores() {
		numberOfTimesHitByAAGun = 0;
		numberOfTimesHitByFighter = 0;
		numberOfRanOutFuel = 0;
		numberOfTimesHitByCruiseMissile = 0;
		
		numberOfTimesAAGunFired = 0;
		numberOfTimesFighterFired = 0;
		numberOfTimesCruiseMissileFired = 0;		
		
		numberOfBombsDropped = 0;
		numberOfBombsLanded = 0;
		numberOfCannonRoundsFired = 0;
		numberOfCannonRoundsLanded = 0;

		numberOfLandings = 0;
		
		numberOfFactoriesDestroyed = 0;
		numberOfFightersDestroyed = 0;
		numberOfBombersDestroyed = 0;
		numberOfAAGunsDestroyed = 0;
		numberOfCruiseMissilesDestroyed = 0;
		
		numberOfBoatsDestroyed = 0;
		numberOfTanksDestroyed = 0;
		numberOfRadarsDestroyed = 0;
		numberOfLivesLost = 0;
		carrierSurvived = true;
		youWon = false;
		enemyShipWasCompleted = false;
		score = 0;

		amountOfDamageTaken = 0;
		amountOfDamageDealt = 0;
		amountOfCarrierDamageTaken = 0;
		amountOfFuelUsed = 0;
		gameTime = 0;
	}
	
}
