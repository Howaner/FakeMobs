package de.howaner.FakeMobs.util;

import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import org.bukkit.entity.EntityType;

public class DataWatchCreator {

	public static WrappedDataWatcher createDefaultWatcher(FakeMob mob) {
		WrappedDataWatcher data = new WrappedDataWatcher();
		addEntityDefaults(data, mob.getType());

		//Custom Name:
		if (mob.getCustomName() != null && !mob.getCustomName().isEmpty()) {
			data.setObject(11, (byte) 1);
			data.setObject(3, (byte) 1);
			data.setObject(10, mob.getCustomName());
			data.setObject(2, mob.getCustomName());
		}

		//Sitting:
		if (mob.isSitting()) {
			if (mob.getType() == EntityType.PLAYER) {
				data.setObject(0, (byte) 2);
			} else {
				data.setObject(16, (byte) 0x1);
			}
		}

		return data;
	}

	public static void addEntityDefaults(WrappedDataWatcher watcher, EntityType type) {
		// Add EntityLiving defaults:
		watcher.setObject(7, 0); //Potion effect color
		watcher.setObject(8, (byte) 0); //Is potion effect active?
		watcher.setObject(9, (byte) 0); //Number of Arrows
		watcher.setObject(6, (float) 1.0f); //Health

		// Add EntityInsentient defaults:
		if (type != EntityType.PLAYER) {
			watcher.setObject(11, (byte) 0); //Custom Name Visible (Minecraft 1.7)
			watcher.setObject(10, ""); //Custom Name (Minecraft 1.7)
			watcher.setObject(3, (byte) 0); //Custom Name Visible (Minecraft 1.8)
			watcher.setObject(2, ""); //Custom Name (Minecraft 1.8)
		}

		switch (type) {
			case BAT:
				watcher.setObject(16, (byte) 0); //Is Hanging?
				break;
			case BLAZE:
				watcher.setObject(16, (byte) 0); //On fire
				break;
			case SPIDER:
			case CAVE_SPIDER:
				watcher.setObject(16, (byte) 0); //In climbing?
				break;
			case CHICKEN:
				break;
			case CREEPER:
				watcher.setObject(16, (byte) -1); //1 = Fuse, -1 = idle
				watcher.setObject(17, (byte) 0); //Is Powered
				break;
			case MUSHROOM_COW:
			case COW:
				break;
			case ENDERMAN:
				watcher.setObject(16, (short) 0); //Carried Block
				watcher.setObject(17, (byte) 0); //Carried Block Data
				watcher.setObject(18, (byte) 0); //Is screaming?
				break;
			case ENDER_DRAGON:
				break;
			case GHAST:
				watcher.setObject(16, (byte) 0); //Is attacking?
				break;
			case GIANT:
				break;
			case HORSE:
				watcher.setObject(16, 0);
				watcher.setObject(19, (byte) 0); //Type: Horse
				watcher.setObject(20, 0); //Color: White
				watcher.setObject(21, ""); //Owner Name
				watcher.setObject(22, 0); //No Armor
				break;
			case IRON_GOLEM:
				watcher.setObject(16, (byte) 0); //Is the iron golem from a player created?
				break;
			case SLIME:
			case MAGMA_CUBE:
				watcher.setObject(16, (byte) 1); //Slime size 1
				break;
			case OCELOT:
				watcher.setObject(18, (byte) 0); //Ocelot Type
				break;
			case PIG:
				watcher.setObject(16, (byte) 0); //Has saddle ?
				break;
			case PIG_ZOMBIE:
			case ZOMBIE:
				watcher.setObject(12, (byte) 0); //Is child?
				watcher.setObject(13, (byte) 0); //Is villager?
				watcher.setObject(14, (byte) 0); //Is converting?
				break;
			case PLAYER:
				watcher.setObject(0, (byte) 0); //Player state (Normal, not crouched)
				watcher.setObject(10, (byte) 0); //Skin flags
				watcher.setObject(16, (byte) 0); //0x02 = Hide cape
				watcher.setObject(17, 0.0f); //Absorption hearts
				watcher.setObject(18, 0); //Score
				break;
			case SHEEP:
				watcher.setObject(16, (byte) 0); //Color
				break;
			case SILVERFISH:
				break;
			case SKELETON:
				watcher.setObject(13, (byte) 0); //Type. 0 = Normal, 1 = Wither
				break;
			case SNOWMAN:
				break;
			case VILLAGER:
				watcher.setObject(16, 0); //Type
				break;
			case WITCH:
				watcher.setObject(21, (byte) 0); //Is agressive?
				break;
			case WITHER:
				watcher.setObject(17, 0);
				watcher.setObject(18, 0);
				watcher.setObject(19, 0);
				watcher.setObject(20, 0);
				break;
			case WOLF:
				watcher.setObject(18, 20.0f); //Health
				watcher.setObject(19, (byte) 0); //Begging
				watcher.setObject(20, (byte) 14); //Collar color
				break;
		}
	}

}
