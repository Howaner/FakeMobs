package de.howaner.FakeMobs.interact;

import de.howaner.FakeMobs.util.FakeMob;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InteractItem implements InteractAction {
	private ItemStack item;
	
	@Override
	public InteractType getType() {
		return InteractType.ITEM;
	}

	@Override
	public int getArgsLength() {
		return 1;
	}
	
	@Override
	public String getUsageText() {
		return ChatColor.RED + "Usage: /fakemob interact add item <Material:Data;Amount>";
	}

	@Override
	public void onSet(Player player, String value) {
		short data = 0;
		int amount = 1;
		
		if (value.contains(";")) {
			try {
				amount = Integer.parseInt(value.split(";")[1]);
				value = value.split(";")[0];
			} catch (Exception e) {
				player.sendMessage(ChatColor.RED + value.split(";")[1] + " isn't a valid amount!");
				return;
			}
		}
		if (value.contains(":")) {
			try {
				data = Short.parseShort(value.split(":")[1]);
				value = value.split(":")[0];
			} catch (Exception e) {
				player.sendMessage(ChatColor.RED + value.split(":")[1] + " isn't a valid item data!");
				return;
			}
		}
		
		Material mat = Material.matchMaterial(value);
		if (mat == null) {
			player.sendMessage(ChatColor.RED + "Item " + value + " not exists!");
			return;
		}
		
		this.item = new ItemStack(mat, amount, data);
		player.sendMessage(ChatColor.GOLD + "Added Item Interact, Item: " + this.toString());
	}
	
	@Override
	public void onInteract(Player player, FakeMob mob) {
		player.getInventory().addItem(this.item.clone());
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.item.getType().name());
		if (this.item.getData().getData() != (byte)0)
			builder.append(":").append(this.item.getData().getData());
		if (this.item.getAmount() != 1)
			builder.append(";").append(this.item.getAmount());
		
		return builder.toString();
	}
	
	@Override
	public void loadFromConfig(ConfigurationSection section) {
		this.item = section.getItemStack("Item");
	}

	@Override
	public void saveToConfig(ConfigurationSection section) {
		section.set("Item", this.item);
	}
	
}
