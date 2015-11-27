package de.howaner.FakeMobs.util;

import de.howaner.FakeMobs.merchant.Merchant;
import de.howaner.FakeMobs.merchant.MerchantOffer;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;

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
	
}
