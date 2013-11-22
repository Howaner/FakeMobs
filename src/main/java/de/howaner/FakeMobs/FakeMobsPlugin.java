package de.howaner.FakeMobs;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import de.howaner.FakeMobs.command.FakeMobCommand;
import de.howaner.FakeMobs.event.RemoveFakeMobEvent;
import de.howaner.FakeMobs.event.SpawnFakeMobEvent;
import de.howaner.FakeMobs.listener.MobListener;
import de.howaner.FakeMobs.listener.ProtocolListener;
import de.howaner.FakeMobs.util.Cache;
import de.howaner.FakeMobs.util.FakeMob;
import de.howaner.FakeMobs.util.LookUpdate;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class FakeMobsPlugin extends JavaPlugin {
	public static Logger log;
	private static FakeMobsPlugin instance;
	private ProtocolManager pManager;
	private Map<Integer, FakeMob> mobs = new HashMap<Integer, FakeMob>();
	
	@Override
	public void onEnable() {
		instance = this;
		log = this.getLogger();
		this.pManager = ProtocolLibrary.getProtocolManager();
		this.loadMobsFile();
		
		this.getProtocolManager().addPacketListener(new ProtocolListener(this));
		Bukkit.getPluginManager().registerEvents(new MobListener(this), this);
		this.getCommand("FakeMob").setExecutor(new FakeMobCommand(this));
		
		for (FakeMob mob : this.getMobs())
			for (Player player : Bukkit.getOnlinePlayers())
				if (mob.getWorld() == player.getWorld())
					mob.sendSpawnPacket(player);
		
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new LookUpdate(this), 5L, 5L);
		
		log.info("Plugin enabled!");
	}
	
	@Override
	public void onDisable() {
		for (FakeMob mob : this.getMobs())
			for (Player player : Bukkit.getOnlinePlayers())
				if (mob.getWorld() == player.getWorld())
					mob.sendDestroyPacket(player);
		
		log.info("Plugin disabled!");
	}
	
	public boolean existsMob(int id) {
		return this.mobs.containsKey(id);
	}
	
	public FakeMob getMob(Location loc) {
		for (FakeMob mob : this.getMobs()) {
			if (mob.getLocation().getWorld() == loc.getWorld() &&
					mob.getLocation().getBlockX() == loc.getBlockX() &&
					mob.getLocation().getBlockY() == loc.getBlockY() &&
					mob.getLocation().getBlockZ() == loc.getBlockZ())
				return mob;
		}
		return null;
	}
	
	public boolean isMobOnLocation(Location loc) {
		return (this.getMob(loc) == null) ? false : true;
	}
	
	public FakeMob getMob(int id) {
		return this.mobs.get(id);
	}
	
	public void removeMob(int id) {
		FakeMob mob = this.mobs.get(id);
		if (mob == null) return;
		
		RemoveFakeMobEvent event = new RemoveFakeMobEvent(mob);
		Bukkit.getPluginManager().callEvent(event);
		
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.getWorld() == mob.getWorld())
				mob.sendDestroyPacket(player);
		
		Map<Player, FakeMob> selectedMap = new HashMap<Player, FakeMob>();
		selectedMap.putAll(Cache.selectedMobs);
		for (Entry<Player, FakeMob> e : selectedMap.entrySet()) {
			if (e.getValue() == mob)
				Cache.selectedMobs.remove(e.getKey());
		}
		
		this.mobs.remove(id);
		this.saveMobsFile();
	}
	
	public FakeMob spawnMob(Location loc, EntityType type) {
		if (!type.isAlive() || type == EntityType.PLAYER) return null;
		
		int id = this.getNewId();
		FakeMob mob = new FakeMob(id, loc, type);
		
		SpawnFakeMobEvent event = new SpawnFakeMobEvent(loc, type, mob);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) return null;
		
		for (Player player : Bukkit.getOnlinePlayers())
			if (player.getWorld() == mob.getWorld())
				mob.sendSpawnPacket(player);
		
		this.mobs.put(id, mob);
		this.saveMobsFile();
		return mob;
	}
	
	public int getNewId() {
		int id = -1;
		for (FakeMob mob : this.getMobs())
			if (mob.getId() > id)
				id = mob.getId();
		return id+1;
	}
	
	public List<FakeMob> getMobs() {
		List<FakeMob> mobList = new ArrayList<FakeMob>();
		mobList.addAll(this.mobs.values());
		return mobList;
	}
	
	public Map<Integer, FakeMob> getMobsMap() {
		return this.mobs;
	}
	
	public static FakeMobsPlugin getPlugin() {
		return instance;
	}
	
	public ProtocolManager getProtocolManager() {
		return this.pManager;
	}
	
	public void loadMobsFile() {
		this.mobs.clear();
		YamlConfiguration config = YamlConfiguration.loadConfiguration(new File("plugins/FakeMobs/mobs.yml"));
		
		for (String key : config.getKeys(false)) {
			ConfigurationSection section = config.getConfigurationSection(key);
			int id = Integer.parseInt(key);
			Location loc = new Location(Bukkit.getWorld(section.getString("World")),
					section.getDouble("X"),
					section.getDouble("Y"),
					section.getDouble("Z"),
					Float.parseFloat(section.getString("Yaw")),
					Float.parseFloat(section.getString("Pitch")));
			EntityType type = EntityType.valueOf(section.getString("Type").toUpperCase());
			FakeMob mob = new FakeMob(id, loc, type);
			if (section.contains("Name") && section.getString("Name").length() <= 16)
				mob.setCustomName(section.getString("Name"));
			mob.setSitting(section.getBoolean("Sitting"));
			mob.setPlayerLook(section.getBoolean("PlayerLook"));
			this.mobs.put(id, mob);
		}
		
		log.info("Loaded " + this.mobs.size() + " Mobs!");
	}
	
	public void saveMobsFile() {
		YamlConfiguration config = new YamlConfiguration();
		
		for (FakeMob mob : this.getMobs()) {
			ConfigurationSection section = config.createSection(String.valueOf(mob.getId()));
			section.set("World", mob.getWorld().getName());
			section.set("X", mob.getLocation().getX());
			section.set("Y", mob.getLocation().getY());
			section.set("Z", mob.getLocation().getZ());
			section.set("Yaw", mob.getLocation().getYaw());
			section.set("Pitch", mob.getLocation().getPitch());
			section.set("Type", mob.getType().name());
			if (mob.getCustomName() != null)
				section.set("Name", mob.getCustomName());
			section.set("Sitting", mob.isSitting());
			section.set("PlayerLook", mob.isPlayerLook());
		}
		
		try {
			config.save(new File("plugins/FakeMobs/mobs.yml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
