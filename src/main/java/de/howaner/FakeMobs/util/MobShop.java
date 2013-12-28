package de.howaner.FakeMobs.util;

import de.howaner.FakeMobs.merchant.Merchant;
import de.howaner.FakeMobs.merchant.MerchantOffer;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MobShop {
	private List<MerchantOffer> items = new ArrayList<MerchantOffer>();
	
	public void openShop(Player player) {
		this.openShop(player, null);
	}
	
	public void openShop(Player player, String title) {
		Merchant merchant = new Merchant();
		for (MerchantOffer offer : items)
			merchant.addOffer(offer);
		merchant.setTitle(title);
		merchant.openTrading(player);
	}
	
	public void clear() {
		this.items.clear();
	}
	
	public void addItems(MerchantOffer... items) {
		for (MerchantOffer item : items)
			this.items.add(item);
	}
	
	public void addItem(MerchantOffer item) {
		this.items.add(item);
	}
	
	public MerchantOffer getItem(int id) {
		return this.items.get(id);
	}
	
	public List<MerchantOffer> getItems() {
		return this.items;
	}
	
	public void removeItem(MerchantOffer offer) {
		this.items.remove(offer);
	}
	
	public void removeItem(int id) {
		this.items.remove(id);
	}
	
	public static ItemStack toItemStack(String name) {
		return toItemStack(name, null);
	}
	
	public static ItemStack toItemStack(String name, Player player) {
		if (name == null || name.isEmpty()) return null;
		
		if (name.equalsIgnoreCase("hand") && player != null)
			return player.getItemInHand().clone();
		
		Material mat;
		short data = 0;
		int amount = 1;
		String itemName = null;
		if (name.contains("/")) {
			itemName = name.split("/")[1];
			name = name.split("/")[0];
		}
		if (name.contains(";")) {
			try {
				amount = Integer.parseInt(name.split(";")[1]);
				name = name.split(";")[0];
			} catch (Exception e) { return null; }
		}
		if (name.contains(":")) {
			try {
				data = Short.parseShort(name.split(":")[1]);
				name = name.split(":")[0];
			} catch (Exception e) { return null; }
		}
		mat = Material.matchMaterial(name);
		if (mat == null) return null;
		ItemStack stack = new ItemStack(mat, amount, data);
		if (itemName != null) {
			itemName = itemName.replace("_", " ");
			itemName = ChatColor.translateAlternateColorCodes('&', itemName);
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(itemName);
			stack.setItemMeta(meta);
		}
		return stack;
	}
	
}
