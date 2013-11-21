package de.howaner.FakeMobs.command;

import de.howaner.FakeMobs.FakeMobsPlugin;
import de.howaner.FakeMobs.util.Cache;
import de.howaner.FakeMobs.util.FakeMob;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class FakeMobCommand implements CommandExecutor {
	private FakeMobsPlugin plugin;
	
	public FakeMobCommand(FakeMobsPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You are not a Player!");
			return true;
		}
		Player player = (Player) sender;
		if (args.length == 0) return false;
		if (args[0].equalsIgnoreCase("create")) {
			if (args.length != 2) return false;
			if (!player.hasPermission("FakeMobs.create")) {
				player.sendMessage(ChatColor.RED + "No permission!");
				return true;
			}
			Location loc = player.getLocation();
			if (this.plugin.isMobOnLocation(loc)) {
				player.sendMessage(ChatColor.RED + "Here is already a Mob!");
				return true;
			}
			EntityType type;
			try {
				type = EntityType.valueOf(args[1].toUpperCase());
			} catch (Exception e) {
				player.sendMessage(ChatColor.RED + args[1] + " is not a Entity!");
				StringBuilder entityBuilder = new StringBuilder();
				for (int i = 0; i < EntityType.values().length; i++) {
					if (EntityType.values()[i] == null || EntityType.values()[i].getName() == null) continue;
					if (i != 0) entityBuilder.append(", ");
					entityBuilder.append(EntityType.values()[i].getName());
				}
				player.sendMessage(ChatColor.GOLD + "Avaible Entitys: " + ChatColor.WHITE + entityBuilder.toString());
				return true;
			}
			if (!type.isAlive()) {
				player.sendMessage(ChatColor.RED + "This entity is not alive!");
				return true;
			}
			FakeMob mob = this.plugin.spawnMob(loc, type);
			if (mob == null) {
				player.sendMessage(ChatColor.RED + "A error occured while creating the Mob!");
				return true;
			}
			player.sendMessage(ChatColor.GREEN + "Created Mob with ID " + ChatColor.GRAY + "#" + mob.getId());
			return true;
		} else if (args[0].equalsIgnoreCase("select")) {
			if (args.length != 1) return false;
			if (!player.hasPermission("FakeMobs.select")) {
				player.sendMessage(ChatColor.RED + "No permission!");
				return true;
			}
			if (Cache.selectedMobs.containsKey(player) && Cache.selectedMobs.get(player) == null) {
				Cache.selectedMobs.remove(player);
				player.sendMessage(ChatColor.GOLD + "Selection cancelled!");
			} else {
				Cache.selectedMobs.put(player, null);
				player.sendMessage(ChatColor.GREEN + "Click on the FakeMob!");
			}
			return true;
		} else if (args[0].equalsIgnoreCase("teleport")) {
			if (args.length != 1) return false;
			if (!player.hasPermission("FakeMobs.teleport")) {
				player.sendMessage(ChatColor.RED + "No permission!");
				return true;
			}
			FakeMob mob = Cache.selectedMobs.get(player);
			if (mob == null) {
				player.sendMessage(ChatColor.RED + "You haven't a Selection!");
				return true;
			}
			if (player.getLocation().getBlock().getType() != Material.AIR) {
				player.sendMessage(ChatColor.RED + "You standing in a Block!");
				return true;
			}
			mob.teleport(player.getLocation());
			player.sendMessage(ChatColor.GREEN + "Teleported Mob " + ChatColor.GRAY + "#" + mob.getId() + ChatColor.GREEN + "!");
			return true;
		} else if (args[0].equalsIgnoreCase("remove")) {
			if (args.length != 1) return false;
			if (!player.hasPermission("FakeMobs.remove")) {
				player.sendMessage(ChatColor.RED + "No permission!");
				return true;
			}
			FakeMob mob = Cache.selectedMobs.get(player);
			if (mob == null) {
				player.sendMessage(ChatColor.RED + "You haven't a Selection!");
				return true;
			}
			this.plugin.removeMob(mob.getId());
			player.sendMessage(ChatColor.GREEN + "Mob " + ChatColor.GRAY + "#" + mob.getId() + ChatColor.GREEN + " removed!");
			return true;
		} else
			return false;
	}
	
}
