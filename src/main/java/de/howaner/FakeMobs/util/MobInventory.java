package de.howaner.FakeMobs.util;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.events.PacketContainer;
import de.howaner.FakeMobs.FakeMobsPlugin;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MobInventory {
	private ItemStack itemInHand = new ItemStack(Material.AIR);
	private ItemStack boots = new ItemStack(Material.AIR);
	private ItemStack leggings = new ItemStack(Material.AIR);
	private ItemStack chestPlate = new ItemStack(Material.AIR);
	private ItemStack helmet = new ItemStack(Material.AIR);
	
	
	public ItemStack getItemInHand() {
		return this.itemInHand;
	}
	
	public ItemStack getBoots() {
		return this.boots;
	}
	
	public ItemStack getLeggings() {
		return this.leggings;
	}
	
	public ItemStack getChestPlate() {
		return this.chestPlate;
	}
	
	public ItemStack getHelmet() {
		return this.helmet;
	}
	
	public void setItemInHand(ItemStack itemInHand) {
		this.itemInHand = itemInHand;
		if (this.itemInHand == null)
			this.itemInHand = new ItemStack(Material.AIR);
	}
	
	public void setBoots(ItemStack boots) {
		this.boots = boots;
		if (this.boots == null)
			this.boots = new ItemStack(Material.AIR);
	}
	
	public void setLeggings(ItemStack leggings) {
		this.leggings = leggings;
		if (this.leggings == null)
			this.leggings = new ItemStack(Material.AIR);
	}
	
	public void setChestPlate(ItemStack chestPlate) {
		this.chestPlate = chestPlate;
		if (this.chestPlate == null)
			this.chestPlate = new ItemStack(Material.AIR);
	}
	
	public void setHelmet(ItemStack helmet) {
		this.helmet = helmet;
		if (this.helmet == null)
			this.helmet = new ItemStack(Material.AIR);
	}
	
	public ItemStack getStackBySlot(int slot) {
		switch (slot) {
			case 0: return this.getItemInHand();
			case 1: return this.getBoots();
			case 2: return this.getLeggings();
			case 3: return this.getChestPlate();
			case 4: return this.getHelmet();
		}
		return null;
	}
	
	public List<PacketContainer> createPackets(int entityId) {
		List<PacketContainer> packetList = new ArrayList<PacketContainer>();
		for (int i = 0; i <= 4; i++) {
			ItemStack stack = this.getStackBySlot(i);
			if (stack == null) continue;
			PacketContainer packet = FakeMobsPlugin.getPlugin().getProtocolManager().createPacket(Packets.Server.ENTITY_EQUIPMENT);
			packet.getIntegers().write(0, entityId);
			packet.getIntegers().write(1, i);
			packet.getItemModifier().write(0, stack);
			packetList.add(packet);
		}
		return packetList;
	}
	
	public boolean isEmpty() {
		return (
				(this.getItemInHand() == null || this.getItemInHand().getType() == Material.AIR) &&
				(this.getBoots() == null || this.getBoots().getType() == Material.AIR) &&
				(this.getLeggings() == null || this.getLeggings().getType() == Material.AIR) &&
				(this.getChestPlate() == null || this.getChestPlate().getType() == Material.AIR) &&
				(this.getHelmet() == null || this.getHelmet().getType() != Material.AIR)
		);
	}
	
	@Override
	public MobInventory clone() {
		MobInventory inv = new MobInventory();
		inv.setItemInHand(this.getItemInHand());
		inv.setBoots(this.getBoots());
		inv.setLeggings(this.getLeggings());
		inv.setChestPlate(this.getChestPlate());
		inv.setHelmet(this.getHelmet());
		return inv;
	}
	
}
