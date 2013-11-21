package de.howaner.FakeMobs.util;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import de.howaner.FakeMobs.FakeMobsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class FakeMob {
	private final int id;
	private String name;
	private Location loc;
	private int health = 20;
	private EntityType type;
	
	public FakeMob(int id, Location loc, EntityType type) {
		this.id = id;
		this.loc = loc;
		this.type = type;
	}
	
	public int getEntityId() {
		return 740 + this.id;
	}
	
	public void sendSpawnPacket(Player player) {
		PacketContainer packet = new PacketContainer(Packets.Server.MOB_SPAWN);
		
		packet.getIntegers().write(0, this.getEntityId());
		packet.getIntegers().write(1, (int) this.type.getTypeId()); //Id
		packet.getIntegers().write(2, (int) Math.floor(this.loc.getX() * 32D)); //X
		packet.getIntegers().write(3, (int) Math.floor(this.loc.getY() * 32D)); //Y
		packet.getIntegers().write(4, (int) Math.floor(this.loc.getZ() * 32D)); //Z
		
		/*packet.getIntegers().write(5, 0) //Vector
				.write(6, 0)
				.write(7, 0);*/
		
		packet.getBytes().write(0, (byte)(int)(this.loc.getYaw() * 256.0F / 360.0F)); //Yaw
		packet.getBytes().write(1, (byte)(int)(this.loc.getPitch() * 256.0F / 360.0F)); //Pitch
		packet.getBytes().write(2, (byte)(int)(this.loc.getPitch() * 256.0F / 360.0F)); //Head
		
		packet.getDataWatcherModifier().write(0, this.getDefaultWatcher());
		
		try {
			FakeMobsPlugin.getPlugin().getProtocolManager().sendServerPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendPositionPacket(Player player) {
		PacketContainer packet = new PacketContainer(Packets.Server.ENTITY_TELEPORT);
		
		packet.getIntegers().write(0, this.getEntityId()); //Id
		packet.getIntegers().write(1, (int) Math.floor(this.loc.getX() * 32)); //X
		packet.getIntegers().write(2, (int) Math.floor(this.loc.getY() * 32)); //Y
		packet.getIntegers().write(3, (int) Math.floor(this.loc.getZ() * 32)); //Z
		
		packet.getBytes().write(0, (byte)(int)(this.loc.getYaw() * 256.0F / 360.0F)); //Yaw
		packet.getBytes().write(1, (byte)(int)(this.loc.getPitch() * 256.0F / 360.0F)); //Pitch
		
		try {
			FakeMobsPlugin.getPlugin().getProtocolManager().sendServerPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendDestroyPacket(Player player) {
		PacketContainer packet = new PacketContainer(Packets.Server.DESTROY_ENTITY);
		packet.getIntegerArrays().write(0, new int[] { this.getEntityId() });
		
		try {
			FakeMobsPlugin.getPlugin().getProtocolManager().sendServerPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public WrappedDataWatcher getDefaultWatcher() {
		Entity entity = this.getWorld().spawnEntity(new Location(this.getWorld(), 0, 256, 0), type);
		WrappedDataWatcher watcher = WrappedDataWatcher.getEntityWatcher(entity).deepClone();
		
		entity.remove();
		return watcher;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Location getLocation() {
		return this.loc;
	}
	
	public World getWorld() {
		return this.loc.getWorld();
	}
	
	public int getHealth() {
		return this.health;
	}
	
	public EntityType getType() {
		return this.type;
	}
	
	public void setLocation(Location loc) {
		this.loc = loc;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void teleport(Location loc) {
		this.loc = loc;
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.getWorld() == this.getWorld())
				this.sendPositionPacket(player);
	}
	
}
