package de.howaner.FakeMobs.util;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Cache {
	
	public static Map<Player, FakeMob> selectedMobs = new HashMap<Player, FakeMob>();
	
	public static ItemStack generateItemStack(String name) {
		if (name.equalsIgnoreCase("none")) return new ItemStack(Material.AIR);
		
		short data = 0;
		if (name.contains(":")) {
			data = Short.parseShort(name.split(":")[1]);
			name = name.split(":")[0];
		}
		
		Material mat = Material.matchMaterial(name);
		if (mat == null) return null;
		
		ItemStack item = new ItemStack(mat, 1, data);
		return item;
	}
	
}
