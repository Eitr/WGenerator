package net.buddat.wgenerator.util;

/**
 * Contains all default settings for a new map generation.
 */
public class Constants {

	public static final String version = "2.9.1";
	public static final String WINDOW_TITLE = "Map Generator for Wurm Unlimited";
	public static final String READY = "Ready";

	public static final int MAP_SIZE = 2048;
	public static final int MAP_HEIGHT = 4096;
	public static final int RESOLUTION = MAP_SIZE / 8;

	public static final int HEIGHTMAP_ITERATIONS = 10;
	public static final int MIN_EDGE = 64;
	public static final int BORDER_WEIGHT = 6;
	public static final int NORMALIZE_RATIO = 33;

	public static final int EROSION_ITERATIONS = 30;
	public static final int EROSION_MIN_SLOPE = 30;
	public static final int EROSION_MAX_SLOPE = 300;
	public static final int EROSION_MAX_SEDIMENT = 100;

	public static final int DIRT_DROP_COUNT = 40;
	public static final int MAX_DIRT_SLOPE = 40;
	public static final int MAX_DIRT_DIAG_SLOPE = 56;
	public static final int WATER_HEIGHT = 500;
	public static final double CLIFF_RATIO = 2.5;
	public static final int ROCK_WEIGHT = 3600;
	public static final boolean MORE_LAND = true;

	public static final int RIVER_DEPTH = 40;
	public static final int RIVER_WIDTH = 2;
	public static final int RIVER_SLOPE = 4;

	public static final int GRASS_ITERATIONS = 50;
	public static final int BIOME_RATE = 60;
	public static final int BIOME_MAX_SLOPE = 20;
	public static final int BIOME_SEEDS = 500;
	public static final int BIOME_SIZE = 60;
	public static final int BIOME_DENSITY = 1;
	public static final int BIOME_MIN_HEIGHT = 50;
	public static final int BIOME_MAX_HEIGHT = 50;
	public static final int BIOME_RANDOM_MIN = 50;
	public static final int BIOME_RANDOM_MAX = 75;
	public static final int BIOME_FLOWER_PERCENT = 30;
	public static final double ORE_IRON = 1.0, ORE_GOLD = 0.05, ORE_SILVER = 0.1, ORE_ZINC = 0.15, 
			ORE_COPPER = 0.2, ORE_LEAD = 0.2, ORE_TIN = 0.2, ORE_ADDY = 0.0, ORE_GLIMMER = 0.0, 
			ORE_MARBLE = 0.2, ORE_SLATE = 0.2, ORE_SANDSTONE = 0.2, ORE_ROCKSALT = 0.2;

	public static final int GRID_SIZE = 8;
	public static final int BIOME_SEED_LIMIT_MULTIPLIER = 10;

	public static enum VIEW_TYPE {ISO,TOPO,CAVE,HEIGHT,BIOMES};
	public static String CONFIG_DIRECTORY = "./config/";
	public static final int CPU_CORES = Runtime.getRuntime().availableProcessors();

}
