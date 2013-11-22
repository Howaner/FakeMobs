package de.howaner.FakeMobs.util;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import de.howaner.FakeMobs.FakeMobsPlugin;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class FakeMob {
	private final int id;
	private String name = null;
	private Location loc;
	private int health = 20;
	private EntityType type;
	private boolean sitting = false;
	private boolean playerLook = false;
	private WrappedDataWatcher watcherCache = null;
	
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
		packet.getIntegers().write(3, (int) Math.floor((this.loc.getY() + 1D) * 32D)); //Y
		packet.getIntegers().write(4, (int) Math.floor(this.loc.getZ() * 32D)); //Z
		
		/*packet.getIntegers().write(5, 0) //Vector
				.write(6, 0)
				.write(7, 0);*/
		
		packet.getBytes().write(0, (byte)(int)(this.loc.getYaw() * 256.0F / 360.0F)); //Yaw
		packet.getBytes().write(1, (byte)(int)(this.loc.getPitch() * 256.0F / 360.0F)); //Pitch
		packet.getBytes().write(2, (byte)(int)(this.loc.getYaw() * 256.0F / 360.0F)); //Head
		
		WrappedDataWatcher watcher = this.getDefaultWatcher();
		if (this.name != null) {
			watcher.setObject(10, (String) this.name);
			watcher.setObject(11, (byte) 1);
		}
		if (this.sitting)
			watcher.setObject(16, (byte) 0x1);
		
		packet.getDataWatcherModifier().write(0, watcher);
		
		try {
			FakeMobsPlugin.getPlugin().getProtocolManager().sendServerPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendMetaPacket(Player player) {
		PacketContainer packet = new PacketContainer(Packets.Server.ENTITY_METADATA);
		
		WrappedDataWatcher watcher = this.getDefaultWatcher();
		if (this.name != null) {
			watcher.setObject(10, (String) this.name);
			watcher.setObject(11, (byte) 1);
		}
		if (this.sitting)
			watcher.setObject(16, (byte) 0x1);
		
		packet.getIntegers().write(0, this.getEntityId());
		packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
		
		try {
			FakeMobsPlugin.getPlugin().getProtocolManager().sendServerPacket(player, packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendLookPacket(Player player, Location point) {
		double xDiff = point.getX() - this.loc.getX();
		double yDiff = point.getY() - this.loc.getY();
		double zDiff = point.getZ() - this.loc.getZ();
		double DistanceXZ = Math.sqrt(xDiff * xDiff + zDiff * zDiff);
		double DistanceY = Math.sqrt(DistanceXZ * DistanceXZ + yDiff * yDiff);
		double newYaw = Math.acos(xDiff / DistanceXZ) * 180.0D / 3.141592653589793D;
		double newPitch = Math.acos(yDiff / DistanceY) * 180.0D / 3.141592653589793D - 90.0D;
		if (zDiff < 0.0D)
			newYaw += Math.abs(180.0D - newYaw) * 2.0D;
		double yaw = ((float)newYaw - 98.0D);
		
		this.sendLookPacket(player, yaw);
	}
	
	public void sendLookPacket(Player player, double yaw) {
		PacketContainer packet = new PacketContainer(Packets.Server.ENTITY_HEAD_ROTATION);
		packet.getIntegers().write(0, this.getEntityId());
		packet.getBytes().write(0, (byte)(int)(yaw * 256.0F / 360.0F));
		
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
		packet.getIntegers().write(2, (int) Math.floor((this.loc.getY() + 1D) * 32)); //Y
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
		if (this.watcherCache == null) {
			Entity entity = this.getWorld().spawnEntity(new Location(this.getWorld(), 0, 256, 0), type);
			this.watcherCache = WrappedDataWatcher.getEntityWatcher(entity).deepClone();
			entity.remove();
		}
		return this.watcherCache;
	}
	
	public List<Player> getNearbyPlayers() {
		return this.getNearbyPlayers(3D);
	}
	
	public List<Player> getNearbyPlayers(double radius) {
		List<Player> players = new ArrayList<Player>();
		
		for (Player player : Bukkit.getOnlinePlayers()) {
			Location l = player.getLocation();
			if (l.getWorld() == this.getWorld() &&
					Math.max(this.loc.getX(), l.getX()) - Math.min(this.loc.getX(), l.getX()) <= radius &&
					Math.max(this.loc.getY(), l.getY()) - Math.min(this.loc.getY(), l.getY()) <= radius &&
					Math.max(this.loc.getZ(), l.getZ()) - Math.min(this.loc.getZ(), l.getZ()) <= radius)
				players.add(player);
		}
		
		return players;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getCustomName() {
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
	
	public boolean isSitting() {
		return this.sitting;
	}
	
	public boolean isPlayerLook() {
		return this.playerLook;
	}
	
	public void setLocation(Location loc) {
		this.loc = loc;
	}
	
	public void setHealth(int health) {
		this.health = health;
	}
	
	public void setCustomName(String name) {
		if (name != null && name.length() > 16) name = name.substring(0, 16);
		this.name = name;
	}
	
	public void setSitting(boolean sitting) {
		if (this.type != EntityType.OCELOT && this.type != EntityType.WOLF) return;
		this.sitting = sitting;
	}
	
	public void setPlayerLook(boolean look) {
		if (this.playerLook == look) return;
		if (!look) {
			for (Player player : Bukkit.getOnlinePlayers())
				if (player.getWorld() == this.getWorld())
					this.sendLookPacket(player, this.getLocation().getYaw());
		}
		this.playerLook = look;
	}
	
	public void teleport(Location loc) {
		this.loc = loc;
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.getWorld() == this.getWorld())
				this.sendPositionPacket(player);
	}
	
	public void updateCustomName() {
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.getWorld() == this.getWorld())
				this.sendMetaPacket(player);
	}
	
}
