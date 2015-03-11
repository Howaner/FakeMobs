package de.howaner.FakeMobs.util;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.events.PacketContainer;
import de.howaner.FakeMobs.FakeMobsPlugin;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MobInventory {
	// 0 - Item in Hand,  1 = Boots, 2 = Leggings, 3 = ChestPlate, 4 = Helmet
	private ItemStack[] items = new ItemStack[5];

	public ItemStack getItemInHand() {
		return this.items[0];
	}
	
	public ItemStack getBoots() {
		return this.items[1];
	}
	
	public ItemStack getLeggings() {
		return this.items[2];
	}
	
	public ItemStack getChestPlate() {
		return this.items[3];
	}
	
	public ItemStack getHelmet() {
		return this.items[4];
	}
	
	public void setItemInHand(ItemStack item) {
		this.setSlot(0, item);
	}
	
	public void setBoots(ItemStack item) {
		this.setSlot(1, item);
	}
	
	public void setLeggings(ItemStack item) {
		this.setSlot(2, item);
	}
	
	public void setChestPlate(ItemStack item) {
		this.setSlot(3, item);
	}
	
	public void setHelmet(ItemStack item) {
		this.setSlot(4, item);
	}
	
	public ItemStack getSlot(int slot) {
		if (slot < 0 || slot >= this.items.length) {
			return null;
		}

		return this.items[slot];
	}

	public void setSlot(int slot, ItemStack item) {
		if (item != null && item.getType() == Material.AIR) {
			item = null;
		}

		if (slot < 0 || slot >= this.items.length) {
			return;
		}

		this.items[slot] = item;
	}
	
	public List<PacketContainer> createPackets(int entityId) {
		List<PacketContainer> packetList = new ArrayList<PacketContainer>();
		for (int i = 0; i < 5; i++) {
			ItemStack stack = this.getSlot(i);

			PacketContainer packet = FakeMobsPlugin.getPlugin().getProtocolManager().createPacket(Packets.Server.ENTITY_EQUIPMENT);
			packet.getIntegers().write(0, entityId);
			packet.getIntegers().write(1, i);
			packet.getItemModifier().write(0, stack);

			packetList.add(packet);
		}
		return packetList;
	}
	
	public boolean isEmpty() {
		for (ItemStack item : this.items) {
			if (item != null && item.getType() != Material.AIR) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public MobInventory clone() {
		MobInventory inv = new MobInventory();
		for (int i = 0; i < 5; i++) {
			inv.setSlot(i, this.getSlot(i));
		}
		return inv;
	}
	
}
