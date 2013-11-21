package de.howaner.FakeMobs.listener;

import de.howaner.FakeMobs.FakeMobsPlugin;
import de.howaner.FakeMobs.event.PlayerInteractFakeMobEvent;
import de.howaner.FakeMobs.util.Cache;
import de.howaner.FakeMobs.util.FakeMob;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class MobListener implements Listener {
	private FakeMobsPlugin plugin;
	
	public MobListener(FakeMobsPlugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onSelectMob(PlayerInteractFakeMobEvent event) {
		if (event.isCancelled() || event.getAction() != PlayerInteractFakeMobEvent.Action.RIGHT_CLICK) return;
		Player player = event.getPlayer();
		FakeMob mob = event.getMob();
		if (Cache.selectedMobs.containsKey(player) && Cache.selectedMobs.get(player) == null) {
			Cache.selectedMobs.put(player, mob);
			player.sendMessage(ChatColor.GREEN + "Mob " + ChatColor.GRAY + "#" + mob.getId() + ChatColor.GREEN + " selected!");
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		for (FakeMob mob : this.plugin.getMobs())
			if (mob.getWorld() == player.getWorld())
				mob.sendSpawnPacket(player);
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		for (FakeMob mob : this.plugin.getMobs())
			if (mob.getWorld() == player.getWorld())
				mob.sendSpawnPacket(player);
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Location loc = event.getBlock().getLocation();
		if (this.plugin.isMobOnLocation(loc))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (Cache.selectedMobs.containsKey(player))
			Cache.selectedMobs.remove(player);
	}
	
}
