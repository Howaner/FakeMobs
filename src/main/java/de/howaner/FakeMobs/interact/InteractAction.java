package de.howaner.FakeMobs.interact;

import de.howaner.FakeMobs.util.FakeMob;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public interface InteractAction {
	
	public InteractType getType();
	
	public int getArgsLength();
	
	public String getUsageText();
	
	public void onSet(Player player, String value);
	
	public void onInteract(Player player, FakeMob mob);
	
	@Override
	public String toString();
	
	public void loadFromConfig(ConfigurationSection section);
	
	public void saveToConfig(ConfigurationSection section);
	
}
