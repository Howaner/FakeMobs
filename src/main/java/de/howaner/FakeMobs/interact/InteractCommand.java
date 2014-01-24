package de.howaner.FakeMobs.interact;

import de.howaner.FakeMobs.util.FakeMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class InteractCommand implements InteractAction {
	private String command;
	
	@Override
	public InteractType getType() {
		return InteractType.COMMAND;
	}
	
	@Override
	public int getArgsLength() {
		return -1;
	}
	
	@Override
	public String getUsageText() {
		return ChatColor.RED + "Usage: /fakemob interact add command <Command>";
	}
	
	@Override
	public void onSet(Player player, String value) {
		this.command = value;
		if (value.startsWith("s:"))
			player.sendMessage(ChatColor.GOLD + "Added Server Command Interact, Command: /" + value.substring(2, value.length()));
		else
			player.sendMessage(ChatColor.GOLD + "Added Command Interact, Command: /" + value);
	}
	
	@Override
	public void onInteract(Player player, FakeMob mob) {
		String cmd = this.command;
		cmd = cmd.replace("@p", player.getName());
		
		CommandSender sender = player;
		if (cmd.startsWith("s:")) {
			cmd = cmd.substring(2, cmd.length());
			sender = Bukkit.getConsoleSender();
		}
		
		Bukkit.dispatchCommand(sender, cmd);
	}
	
	@Override
	public String toString() {
		return this.command;
	}

	@Override
	public void loadFromConfig(ConfigurationSection section) {
		this.command = section.getString("Command");
	}

	@Override
	public void saveToConfig(ConfigurationSection section) {
		section.set("Command", this.command);
	}
	
}
