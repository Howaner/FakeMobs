package de.howaner.FakeMobs.interact;

import de.howaner.FakeMobs.util.FakeMob;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class InteractText implements InteractAction {
	private String text;
	
	@Override
	public InteractType getType() {
		return InteractType.TEXT;
	}
	
	@Override
	public int getArgsLength() {
		return -1;
	}
	
	@Override
	public String getUsageText() {
		return ChatColor.RED + "Usage: /fakemob interact add text <Text>";
	}

	@Override
	public void onSet(Player player, String value) {
		this.text = value;
		player.sendMessage(ChatColor.GOLD + "Added Text Interact, Text: " + value);
	}
	
	@Override
	public void onInteract(Player player, FakeMob mob) {
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.text));
	}

	@Override
	public void loadFromConfig(ConfigurationSection section) {
		this.text = section.getString("Text");
	}

	@Override
	public void saveToConfig(ConfigurationSection section) {
		section.set("Text", this.text);
	}
	
}
