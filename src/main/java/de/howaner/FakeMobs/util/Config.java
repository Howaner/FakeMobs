package de.howaner.FakeMobs.util;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {
	public static File configFile = new File("plugins/FakeMobs/config.yml");
	public static int SEE_RADIUS = 60;
	
	public static void load() {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
		SEE_RADIUS = config.getInt("Mobs.Radius");
	}
	
	public static void save() {
		YamlConfiguration config = new YamlConfiguration();
		config.set("Mobs.Radius", SEE_RADIUS);
		
		try {
			config.save(configFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
