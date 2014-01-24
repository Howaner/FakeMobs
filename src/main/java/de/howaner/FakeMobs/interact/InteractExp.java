package de.howaner.FakeMobs.interact;

import de.howaner.FakeMobs.util.FakeMob;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class InteractExp implements InteractAction {
	private float exp;
	
	@Override
	public InteractType getType() {
		return InteractType.EXP;
	}

	@Override
	public int getArgsLength() {
		return 1;
	}
	
	@Override
	public String getUsageText() {
		return ChatColor.RED + "Usage: /fakemob interact add exp <Exp>";
	}

	@Override
	public void onSet(Player player, String value) {
		try {
			this.exp = Float.parseFloat(value);
			player.sendMessage("Added Exp Interact, Exp: " + this.exp);
		} catch (Exception e) {
			player.sendMessage(ChatColor.RED + value + " isn't a valid Exp amount!");
		}
	}
	
	@Override
	public void onInteract(Player player, FakeMob mob) {
		player.setExp(player.getExp() + this.exp);
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.exp);
	}

	@Override
	public void loadFromConfig(ConfigurationSection section) {
		this.exp = Float.parseFloat(section.getString("Exp"));
	}

	@Override
	public void saveToConfig(ConfigurationSection section) {
		section.set("Exp", String.valueOf(this.exp));
	}
	
}
