package de.howaner.FakeMobs.command;

import de.howaner.FakeMobs.FakeMobsPlugin;
import de.howaner.FakeMobs.interact.InteractAction;
import de.howaner.FakeMobs.interact.InteractType;
import de.howaner.FakeMobs.merchant.MerchantOffer;
import de.howaner.FakeMobs.util.Cache;
import de.howaner.FakeMobs.util.FakeMob;
import de.howaner.FakeMobs.util.MobShop;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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
				boolean komma = false;
				for (int i = 0; i < EntityType.values().length; i++) {
					EntityType t = EntityType.values()[i];
					if (t == null || !t.isAlive() || t.getName() == null) continue;
					if (komma) entityBuilder.append(", ");
					entityBuilder.append(t.name());
					komma = true;
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
				player.sendMessage(ChatColor.RED + "A error occurred while creating the Mob!");
				return true;
			}
			player.sendMessage(ChatColor.GREEN + "Created Mob with ID " + ChatColor.GRAY + "#" + mob.getId());
			return true;
		} else if (args[0].equalsIgnoreCase("select")) {
			if (args.length != 1 && args.length != 2) return false;
			if (!player.hasPermission("FakeMobs.select")) {
				player.sendMessage(ChatColor.RED + "No permission!");
				return true;
			}
			if (args.length == 2) {
				int id;
				try {
					id = Integer.valueOf(args[1]);
				} catch (Exception e) {
					player.sendMessage(ChatColor.RED + "Please enter a valid Id!");
					return true;
				}
				FakeMob mob = this.plugin.getMob(id);
				if (mob == null) {
					player.sendMessage(ChatColor.RED + "A Mob with ID " + ChatColor.GRAY + "#" + id + ChatColor.RED + " don't exists!");
					return true;
				}
				Cache.selectedMobs.put(player, mob);
				player.sendMessage(ChatColor.GREEN + "Mob " + ChatColor.GRAY + "#" + id + ChatColor.GREEN + " selected!");
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
		} else if (args[0].equalsIgnoreCase("name")) {
			if (args.length < 2) return false;
			if (!player.hasPermission("FakeMobs.name")) {
				player.sendMessage(ChatColor.RED + "No permission!");
				return true;
			}
			FakeMob mob = Cache.selectedMobs.get(player);
			if (mob == null) {
				player.sendMessage(ChatColor.RED + "You haven't a Selection!");
				return true;
			}
			StringBuilder textBuilder = new StringBuilder();
			for (int i = 1; i < args.length; i++) {
				if (i != 1) textBuilder.append(" ");
				textBuilder.append(args[i]);
			}
			String text = ChatColor.translateAlternateColorCodes('&', textBuilder.toString());
			if (text.length() > 32) {
				player.sendMessage(ChatColor.RED + "Name too long!");
				return true;
			}
			if (text.equalsIgnoreCase("none")) {
				mob.setCustomName(null);
				player.sendMessage(ChatColor.GREEN + "Mob Name deleted!");
			} else {
				mob.setCustomName(text);
				player.sendMessage(ChatColor.GREEN + "Mob Name set to " + ChatColor.GRAY + text + ChatColor.GREEN + "!");
			}
			mob.updateCustomName();
			this.plugin.saveMobsFile();
			return true;
		} else if (args[0].equalsIgnoreCase("sitting")) {
			if (args.length < 1) return false;
			if (!player.hasPermission("FakeMobs.sitting")) {
				player.sendMessage(ChatColor.RED + "No permission!");
				return true;
			}
			FakeMob mob = Cache.selectedMobs.get(player);
			if (mob == null) {
				player.sendMessage(ChatColor.RED + "You haven't a Selection!");
				return true;
			}
			if (mob.getType() != EntityType.OCELOT && mob.getType() != EntityType.WOLF && mob.getType() != EntityType.PLAYER) {
				player.sendMessage(ChatColor.RED + "Only pets and players can sit!");
				return true;
			}
			mob.setSitting(!mob.isSitting());
			mob.updateCustomName();
			this.plugin.saveMobsFile();
			player.sendMessage(ChatColor.GREEN + "Sitting Status changed: " + ChatColor.GRAY + ((mob.isSitting()) ? "on" : "off"));
			return true;
		} else if (args[0].equalsIgnoreCase("look")) {
			if (args.length < 1) return false;
			if (!player.hasPermission("FakeMobs.look")) {
				player.sendMessage(ChatColor.RED + "No permission!");
				return true;
			}
			FakeMob mob = Cache.selectedMobs.get(player);
			if (mob == null) {
				player.sendMessage(ChatColor.RED + "You haven't a Selection!");
				return true;
			}
			mob.setPlayerLook(!mob.isPlayerLook());
			if (mob.isPlayerLook())
				mob.sendLookPacket(player, player.getLocation());
			this.plugin.saveMobsFile();
			player.sendMessage(ChatColor.GREEN + "Player Look: " + ChatColor.GRAY + ((mob.isPlayerLook()) ? "on" : "off"));
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
			this.plugin.saveMobsFile();
			player.sendMessage(ChatColor.GREEN + "Teleported Mob " + ChatColor.GRAY + "#" + mob.getId() + ChatColor.GREEN + "!");
			return true;
		} else if (args[0].equalsIgnoreCase("inv")) {
			if (args.length != 3) return false;
			if (!player.hasPermission("FakeMobs.inv")) {
				player.sendMessage(ChatColor.RED + "No permission!");
				return true;
			}
			FakeMob mob = Cache.selectedMobs.get(player);
			if (mob == null) {
				player.sendMessage(ChatColor.RED + "You haven't a Selection!");
				return true;
			}
			ItemStack item = Cache.generateItemStack(args[2]);
			if (item == null) {
				player.sendMessage(ChatColor.RED + "This Item doesn't exists!");
				return true;
			}
			if (args[1].equalsIgnoreCase("hand")) {
				mob.getInventory().setItemInHand(item);
				player.sendMessage(ChatColor.GOLD + "Setted Item in Hand to " + item.getType().name() + "!");
			} else if (args[1].equalsIgnoreCase("boots")) {
				mob.getInventory().setBoots(item);
				player.sendMessage(ChatColor.GOLD + "Setted Boots to " + item.getType().name() + "!");
			} else if (args[1].equalsIgnoreCase("leggings")) {
				mob.getInventory().setLeggings(item);
				player.sendMessage(ChatColor.GOLD + "Setted Leggings to " + item.getType().name() + "!");
			} else if (args[1].equalsIgnoreCase("chestplate")) {
				mob.getInventory().setChestPlate(item);
				player.sendMessage(ChatColor.GOLD + "Setted ChestPlate to " + item.getType().name() + "!");
			} else if (args[1].equalsIgnoreCase("helmet")) {
				mob.getInventory().setHelmet(item);
				player.sendMessage(ChatColor.GOLD + "Setted Helmet to " + item.getType().name() + "!");
			} else
				return false;
			mob.updateInventory();
			this.plugin.saveMobsFile();
			return true;
		} else if (args[0].equalsIgnoreCase("shop")) {
			if (args.length < 2) return false;
			if (!player.hasPermission("FakeMobs.shop")) {
				player.sendMessage(ChatColor.RED + "No permission!");
				return true;
			}
			FakeMob mob = Cache.selectedMobs.get(player);
			if (mob == null) {
				player.sendMessage(ChatColor.RED + "You haven't a Selection!");
				return true;
			}
			if (args[1].equalsIgnoreCase("enable")) {
				if (args.length != 2) return false;
				if (mob.haveShop()) {
					player.sendMessage(ChatColor.RED + "This Mob have already a Shop!");
					return true;
				}
				mob.setShop(new MobShop());
				this.plugin.saveMobsFile();
				player.sendMessage(ChatColor.GREEN + "Villager Shop enabled!");
				return true;
			} else if (args[1].equalsIgnoreCase("disable")) {
				if (args.length != 2) return false;
				if (!mob.haveShop()) {
					player.sendMessage(ChatColor.RED + "This Mob haven't a Shop!");
					return true;
				}
				mob.setShop(null);
				this.plugin.saveMobsFile();
				player.sendMessage(ChatColor.GREEN + "Villager Shop removed!");
				return true;
			} else if (args[1].equalsIgnoreCase("addItem")) {
				if (args.length != 4 && args.length != 5) return false;
				if (!mob.haveShop()) {
					player.sendMessage(ChatColor.RED + "This Mob haven't a Shop!");
					return true;
				}
				ItemStack item1 = null;
				ItemStack item2 = null;
				ItemStack result = null;
				
				item1 = MobShop.toItemStack(args[2], player);
				if (item1 == null) {
					player.sendMessage(args[2] + " isn't a Item!");
					return true;
				}
				
				if (args.length == 4) {
					result = MobShop.toItemStack(args[3], player);
					if (result == null) {
						player.sendMessage(args[3] + " isn't a Item!");
						return true;
					}
				} else if (args.length == 5) {
					item2 = MobShop.toItemStack(args[3], player);
					if (item2 == null) {
						player.sendMessage(args[3] + " isn't a Item!");
						return true;
					}
					result = MobShop.toItemStack(args[4], player);
					if (result == null) {
						player.sendMessage(args[4] + " isn't a Item!");
						return true;
					}
				} else
					return false;
				
				MerchantOffer offer = new MerchantOffer(item1, item2, result);
				mob.getShop().addItem(offer);
				this.plugin.saveMobsFile();
				player.sendMessage(ChatColor.GREEN + "Item added!");
				return true;
			} else if (args[1].equalsIgnoreCase("removeItem")) {
				if (args.length != 3) return false;
				if (!mob.haveShop()) {
					player.sendMessage(ChatColor.RED + "This Mob haven't a Shop!");
					return true;
				}
				int id;
				try {
					id = Integer.parseInt(args[2]);
				} catch (Exception e) {
					player.sendMessage(ChatColor.RED + args[2] + " isn't a Id!");
					return true;
				}
				if (mob.getShop().getItem(id) == null) {
					player.sendMessage(ChatColor.RED + "Item " + ChatColor.GRAY + "#" + id + ChatColor.RED + " dont't exists!");
					return true;
				}
				mob.getShop().removeItem(id);
				this.plugin.saveMobsFile();
				player.sendMessage(ChatColor.GOLD + "Item " + ChatColor.GRAY + "#" + id + ChatColor.GOLD + " was removed!");
				return true;
			} else if (args[1].equalsIgnoreCase("clear")) {
				if (args.length != 2) return false;
				if (!mob.haveShop()) {
					player.sendMessage(ChatColor.RED + "This Mob haven't a Shop!");
					return true;
				}
				if (mob.getShop().getItems().isEmpty()) {
					player.sendMessage(ChatColor.RED + "This Shop has no Items!");
					return true;
				}
				mob.getShop().clear();
				this.plugin.saveMobsFile();
				player.sendMessage(ChatColor.GREEN + "Shop cleared!");
				return true;
			} else if (args[1].equalsIgnoreCase("items")) {
				if (args.length != 2) return false;
				if (!mob.haveShop()) {
					player.sendMessage(ChatColor.RED + "This Mob haven't a Shop!");
					return true;
				}
				if (mob.getShop().getItems().isEmpty()) {
					player.sendMessage(ChatColor.RED + "This Shop has no Items!");
					return true;
				}
				player.sendMessage(ChatColor.GOLD + "Items (" + mob.getShop().getItems().size() + ")");
				for (int i = 0; i < mob.getShop().getItems().size(); i++) {
					MerchantOffer offer = mob.getShop().getItems().get(i);
					StringBuilder builder = new StringBuilder();
					builder.append(ChatColor.GRAY);
					builder.append(i);
					builder.append(". ");
					builder.append(ChatColor.WHITE);
					//Item 1
					builder.append("Item 1: ");
					builder.append(offer.getFirstInput().getType().name());
					builder.append(" (");
					builder.append(offer.getFirstInput().getAmount());
					builder.append(")");
					//Item 2
					builder.append(", Item 2: ");
					if (offer.getSecondInput() != null) {
						builder.append(offer.getSecondInput().getType().name());
						builder.append(" (");
						builder.append(offer.getSecondInput().getAmount());
						builder.append(")");
					} else
						builder.append("none");
					//Output
					builder.append(", Output: ");
					builder.append(offer.getOutput().getType().name());
					builder.append(" (");
					builder.append(offer.getOutput().getAmount());
					builder.append(")");
					
					player.sendMessage(builder.toString());
				}
				return true;
			} else
				return false;
		} else if (args[0].equalsIgnoreCase("interact")) {
			if (args.length < 2)
				return false;
			if (!player.hasPermission("FakeMobs.interact")) {
				player.sendMessage(ChatColor.RED + "No permission!");
				return true;
			}
			FakeMob mob = Cache.selectedMobs.get(player);
			if (mob == null) {
				player.sendMessage(ChatColor.RED + "You haven't a Selection!");
				return true;
			}
			if (args[1].equalsIgnoreCase("add")) {
				if (args.length < 4)
					return false;
				InteractType type = InteractType.getByName(args[2]);
				if (type == null) {
					player.sendMessage(ChatColor.RED + "Interact type " + args[2] + " can't found!");
					player.sendMessage(ChatColor.GREEN + "Available Types: " + ChatColor.WHITE + InteractType.getStringList());
					return true;
				}
				InteractAction action;
				try {
					action = type.getActionClass().newInstance();
				} catch (Exception e) {
					e.printStackTrace();
					return true;
				}
				if (action.getArgsLength() != -1 && (args.length - 3) != action.getArgsLength()) {
					player.sendMessage(action.getUsageText());
				}
				
				StringBuilder argsBuilder = new StringBuilder();
				for (int i = 3; i < args.length; i++) {
					if (i != 3) argsBuilder.append(" ");
					argsBuilder.append(args[i]);
				}
				
				action.onSet(player, argsBuilder.toString());
				mob.addInteractAction(action);
				this.plugin.saveMobsFile();
				return true;
			} else if (args[1].equalsIgnoreCase("remove")) {
				if (args.length != 3)
					return false;
				int id;
				try {
					id = Integer.parseInt(args[2]);
				} catch (Exception e) {
					player.sendMessage(ChatColor.RED + args[2] + " isn't a valid ID!");
					return true;
				}
				InteractAction action = mob.getInteractActions().get(id);
				if (action == null) {
					player.sendMessage(ChatColor.RED + "The Mob haven't a Mob Action with ID #" + id + "!");
					return true;
				}
				
				mob.removeInteractAction(action);
				player.sendMessage(ChatColor.GOLD + "Mob Action with ID #" + id + " removed!");
				this.plugin.saveMobsFile();
				return true;
			} else if (args[1].equalsIgnoreCase("list")) {
				if (args.length != 2)
					return false;
				if (mob.getInteractActions().isEmpty()) {
					player.sendMessage(ChatColor.GOLD + "Registered Mob Actions: " + ChatColor.WHITE + "None");
				} else {
					player.sendMessage(ChatColor.GOLD + "Registered Mob Actions:");
					for (int i = 0; i < mob.getInteractActions().size(); i++) {
						InteractAction action = mob.getInteractActions().get(i);
						StringBuilder builder = new StringBuilder();
						
						builder.append(ChatColor.GRAY).append("#").append(i).append(": ")
								.append(ChatColor.GOLD).append("Type: ").append(ChatColor.WHITE).append(action.getType().name())
								.append(", ")
								.append(ChatColor.GOLD).append("Value: ").append(ChatColor.WHITE).append(action.toString());
						
						player.sendMessage(builder.toString());
					}
				}
				player.sendMessage(ChatColor.GOLD + "Available Interact Actions: " + ChatColor.WHITE + InteractType.getStringList());
				return true;
			} else if (args[1].equalsIgnoreCase("clear")) {
				if (args.length != 2)
					return false;
				if (mob.getInteractActions().isEmpty()) {
					player.sendMessage(ChatColor.RED + "The Mob haven't Interact Actions!");
					return true;
				}
				mob.clearInteractAction();
				this.plugin.saveMobsFile();
				player.sendMessage(ChatColor.GOLD + "All Interact Actions removed!");
				return true;
			} else
				return false;
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
		} else if (args[0].equalsIgnoreCase("help")) {
			if (args.length != 1) return false;
			if (!player.hasPermission("FakeMobs.help")) {
				player.sendMessage(ChatColor.RED + "No permission!");
				return true;
			}
			player.sendMessage(ChatColor.GOLD + "Help for " + ChatColor.GRAY + "/FakeMob");
			player.sendMessage(ChatColor.GRAY + "/FakeMob create <Type> " + ChatColor.RED + "-- " + ChatColor.WHITE + "Spawn a Fakemob");
			player.sendMessage(ChatColor.GRAY + "/FakeMob select [id] " + ChatColor.RED + "-- " + ChatColor.WHITE + "Select a Fakemob");
			player.sendMessage(ChatColor.GRAY + "/FakeMob name <Name/none> " + ChatColor.RED + "-- " + ChatColor.WHITE + "Give the Fakemob a name");
			player.sendMessage(ChatColor.GRAY + "/FakeMob sitting " + ChatColor.RED + "-- " + ChatColor.WHITE + "Change the Sitting state of a pet and players (Wolf/Ocelot/Player)");
			player.sendMessage(ChatColor.GRAY + "/FakeMob look " + ChatColor.RED + "-- " + ChatColor.WHITE + "Enable/Disable the Players Look");
			player.sendMessage(ChatColor.GRAY + "/FakeMob teleport " + ChatColor.RED + "-- " + ChatColor.WHITE + "Teleport a Fakemob to you");
			player.sendMessage(ChatColor.GRAY + "/FakeMob inv <hand/boots/leggings/chestplate/helmet> <Item> " + ChatColor.RED + "-- " + ChatColor.WHITE + "Set the Inventory of a Fakemob. Use none to delete.");
			player.sendMessage(ChatColor.GRAY + "/FakeMob shop enable " + ChatColor.RED + "-- " + ChatColor.WHITE + "Enable the Shop");
			player.sendMessage(ChatColor.GRAY + "/FakeMob shop disable " + ChatColor.RED + "-- " + ChatColor.WHITE + "Disable the Shop");
			player.sendMessage(ChatColor.GRAY + "/FakeMob shop addItem <Item 1> [Item 2] <Output> " + ChatColor.RED + "-- " + ChatColor.WHITE + "Add a Item to the Shop");
			player.sendMessage(ChatColor.GRAY + "/FakeMob shop removeItem <id> " + ChatColor.RED + "-- " + ChatColor.WHITE + "Remove a Item with this Id (you can see Id's in /Fakemob shop items");
			player.sendMessage(ChatColor.GRAY + "/FakeMob shop clear " + ChatColor.RED + "-- " + ChatColor.WHITE + "Remove all Items from the Shop");
			player.sendMessage(ChatColor.GRAY + "/FakeMob shop items " + ChatColor.RED + "-- " + ChatColor.WHITE + "Display all Items in the Shop");
			player.sendMessage(ChatColor.GRAY + "/FakeMob interact add <Type> <Value> " + ChatColor.RED + "-- " + ChatColor.WHITE + "Add Interact Action to the Mob");
			player.sendMessage(ChatColor.GRAY + "/FakeMob interact remove <ID> " + ChatColor.RED + "-- " + ChatColor.WHITE + "Remove Interact Action");
			player.sendMessage(ChatColor.GRAY + "/FakeMob interact list " + ChatColor.RED + "-- " + ChatColor.WHITE + "List all Interact Actions from the Mob");
			player.sendMessage(ChatColor.GRAY + "/FakeMob interact clear " + ChatColor.RED + "-- " + ChatColor.WHITE + "Remove all Interact Actions from the Mob");
			player.sendMessage(ChatColor.GRAY + "/FakeMob remove " + ChatColor.RED + "-- " + ChatColor.WHITE + "Remove a Fakemob");
			return true;
		} else
			return false;
	}
	
}
