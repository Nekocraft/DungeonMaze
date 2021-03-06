package com.timvisee.DungeonMaze.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import com.timvisee.DungeonMaze.DungeonMaze;

public class DMWorldManager {
	public static DungeonMaze plugin;

	public DMWorldManager(DungeonMaze instance) {
		DMWorldManager.plugin = instance;
	}

	// DM worlds
	private static List<String> worlds = new ArrayList<String>();
	private static List<String> preloadWorlds = new ArrayList<String>();
	
	/**
	 * Refresh the list with Dungeon Maze worlds
	 */
	public static void refresh() {
		// Load the list from the config
		List<String> w = plugin.getConfig().getStringList("worlds");
		
			if (getMultiverseCore() != null) {
				for (World world : Bukkit.getWorlds()) {
					MultiverseCore mv = getMultiverseCore();
					MultiverseWorld mvWorld = mv.getMVWorldManager().getMVWorld(world);
					
					if ((mvWorld.getGenerator().contains("dungeonmaze") || mvWorld.getGenerator().contains("DungeonMaze")) && !w.contains(world.getName()))
						w.add(world.getName());
				}
			} else
				DungeonMaze.log.severe("[DungeonMaze] Ajouts des mondes impossibles");
		
		worlds = w;

		// Load the list from the config
		List<String> pw = plugin.getConfig().getStringList("preloadWorlds");
		if(pw != null)
			preloadWorlds = pw;
		
		// Put all the DM worlds into the bukkit.yml file
		if (getMultiverseCore() == null) {
			FileConfiguration bukkitConfig = plugin.getConfigFromPath(new File("bukkit.yml"));
			if(bukkitConfig != null) {
				System.out.println("Editing bukkit.yml file...");
				for(String entry : w)
					bukkitConfig.set("worlds." + w + ".generator", entry);
				try {
					bukkitConfig.save(new File("bukkit.yml"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("Editing finished!");
			}
		}
	}
	
	/**
	 * Get all DM worlds
	 * @return all DM worlds
	 */
	public static List<String> getDMWorlds() {
		return DMWorldManager.worlds;
	}
	
	/**
	 * Get all loaded DM worlds
	 * @return
	 */
	public static List<String> getLoadedDMWorlds() {
		List<String> loadedWorlds = new ArrayList<String>();
		refresh();
		for(String entry : DMWorldManager.worlds) {
			World w = plugin.getServer().getWorld(entry);
			if(w != null)
				loadedWorlds.add(entry);
		}
		
		return loadedWorlds;
	}
	
	/**
	 * Get all preload worlds of DM
	 * @return all preload worlds
	 */
	public static List<String> getPreloadWorlds() {
		return preloadWorlds;
	}
	
	/**
	 * Check if a world is a DM world
	 * @param w the world name
	 * @return true if the world is a DM world
	 */
	public static boolean isDMWorld(String w) {
		return getDMWorlds().contains(w);
	}
	
	/**
	 * Check if a world is a loaded DM world
	 * @param w the world name
	 * @return true if the world is a loaded DM world
	 */
	public static boolean isLoadedDMWorld(String w) {
		return getLoadedDMWorlds().contains(w);
	}
	
	/**
	 * Preload all 'preload' DM worlds
	 */
	public static void preloadWorlds() {
		if (preloadWorlds != null) {
			for(String w : preloadWorlds) {
					WorldCreator newWorld = new WorldCreator(w);
					newWorld.generator(plugin.getDMWorldGenerator());
					if (Bukkit.getWorld(w) != null)
						newWorld.createWorld();


			}
		}
	}
	
	public static MultiverseCore getMultiverseCore() {
        MultiverseCore mv = (MultiverseCore) plugin.getServer().getPluginManager().getPlugin("Multiverse-Core");
 
        if (mv != null) return mv;
        else return null;
    }
}
