package de.howaner.FakeMobs.util;

import java.lang.reflect.Method;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemParser {
	
	public static ItemStack generateItemStack(String[] input) throws Exception {
		if (input[0].equalsIgnoreCase("none")) return new ItemStack(Material.AIR);
		
		// Material
		Material material = Material.matchMaterial(input[0]);
		if (material == null)
			throw new Exception("Unknown item type");
		ItemStack item = new ItemStack(material);
		
		// Amount
		if (input.length >= 2) {
			Integer amount = toInt(input[1], 1, 64);
			if (amount == null)
				throw new Exception("Invalid item amount");
			
			amount = Math.max(Math.min(64, amount), 1);
			item.setAmount(amount);
		}
		
		// Meta
		if (input.length >= 3) {
			Integer meta = toInt(input[2], 0, 2147483647);
			if (meta == null)
				throw new Exception("Invalid meta");
			
			item.setDurability((short)(int)meta);
		}
		
		// NBT Data
		if (input.length >= 4) {
			StringBuilder builder = new StringBuilder();
			for (int i = 3; i < input.length; i++) {
				if (i != 3) builder.append(" ");
				builder.append(input[i]);
			}
			
			try {
				Class MojangsonParserClass = Class.forName("net.minecraft.server." + getBukkitVersion() + ".MojangsonParser");
				Method parseMethod = MojangsonParserClass.getDeclaredMethod("parse", String.class);
				parseMethod.setAccessible(true);
				Object NBTBase = parseMethod.invoke(null, builder.toString());
				
				if (NBTBase.getClass() != Class.forName("net.minecraft.server." + getBukkitVersion() + ".NBTTagCompound")) {
					throw new Exception("Invalid nbt tag: Not a valid tag");
				}
				item = addNBTToItemStack(item, NBTBase);
			} catch (Exception e) {
				throw new Exception("Invalid nbt tag: " + e.getMessage());
			}
		}
		
		return item;
	}
	
	public static Integer toInt(String obj, int min, int max) {
		try {
			int number = Integer.parseInt(obj);
			
			number = Math.max(Math.min(max, number), min);
			return number;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static ItemStack addNBTToItemStack(ItemStack stack, Object nbt) {
		try {
			Class CraftItemStackClass = Class.forName("org.bukkit.craftbukkit." + getBukkitVersion() + ".inventory.CraftItemStack");
			Class NMSItemStackClass = Class.forName("net.minecraft.server." + getBukkitVersion() + ".ItemStack");
			Class NBTTagCompoundClass = Class.forName("net.minecraft.server." + getBukkitVersion() + ".NBTTagCompound");
			
			Method asNMSCopy = CraftItemStackClass.getDeclaredMethod("asNMSCopy", ItemStack.class);
			asNMSCopy.setAccessible(true);
			Object nmsStack = asNMSCopy.invoke(null, stack);
			
			Method setTag = NMSItemStackClass.getDeclaredMethod("setTag", NBTTagCompoundClass);
			setTag.setAccessible(true);
			setTag.invoke(nmsStack, nbt);
			
			Method asBukkitCopy = CraftItemStackClass.getDeclaredMethod("asBukkitCopy", NMSItemStackClass);
			asBukkitCopy.setAccessible(true);
			return (ItemStack) asBukkitCopy.invoke(null, nmsStack);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String getBukkitVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}

}
