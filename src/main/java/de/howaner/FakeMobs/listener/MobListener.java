package de.howaner.FakeMobs.listener;

import de.howaner.FakeMobs.FakeMobsPlugin;
import de.howaner.FakeMobs.adjuster.MyWorldAccess;
import de.howaner.FakeMobs.event.PlayerInteractFakeMobEvent;
import de.howaner.FakeMobs.event.PlayerInteractFakeMobEvent.Action;
import de.howaner.FakeMobs.util.Cache;
import de.howaner.FakeMobs.util.FakeMob;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldUnloadEvent;

public class MobListener implements Listener {
	private FakeMobsPlugin plugin;
	
	public MobListener(FakeMobsPlugin plugin) {
		this.plugin = plugin;
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onWorldInit(WorldInitEvent event) {
		MyWorldAccess.registerWorldAccess(event.getWorld());
	}

	@EventHandler (priority = EventPriority.HIGHEST)
	public void onWorldUnload(WorldUnloadEvent event) {
		MyWorldAccess.unregisterWorldAccess(event.getWorld());
	}

	@EventHandler
	public void onSelectMob(PlayerInteractFakeMobEvent event) {
		Player player = event.getPlayer();
		FakeMob mob = event.getMob();
		if (Cache.selectedMobs.containsKey(player) && Cache.selectedMobs.get(player) == null) {
			Cache.selectedMobs.put(player, mob);
			player.sendMessage(ChatColor.GREEN + "Mob " + ChatColor.GRAY + "#" + mob.getId() + ChatColor.GREEN + " selected!");
			return;
		}
		
		if (event.getAction() == Action.RIGHT_CLICK && mob.haveShop())
			mob.getShop().openShop(player, (mob.getCustomName() != null && !mob.getCustomName().isEmpty()) ? mob.getCustomName() : null);
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		final Player player = event.getPlayer();

		Bukkit.getScheduler().runTaskLater(FakeMobsPlugin.getPlugin(), new Runnable() {
			@Override
			public void run() {
				MobListener.this.plugin.updatePlayerView(player);
			}
		}, 5L);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (event.getFrom().getWorld() == event.getTo().getWorld() &&
				event.getFrom().getBlockX() == event.getTo().getBlockX() &&
				event.getFrom().getBlockY() == event.getTo().getBlockY() &&
				event.getFrom().getBlockZ() == event.getTo().getBlockZ())
			return;

		Player player = event.getPlayer();
		if (player.getHealth() <= 0.0D) return;  // player is dead

		Chunk oldChunk = event.getFrom().getChunk();
		Chunk newChunk = event.getTo().getChunk();

		// Only when the player moves a complete chunk:
		if (oldChunk.getWorld() != newChunk.getWorld() || oldChunk.getX() != newChunk.getX() || oldChunk.getZ() != newChunk.getZ()) {
			this.plugin.updatePlayerView(player);
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		this.plugin.updatePlayerView(player);
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		this.plugin.updatePlayerView(player);
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
		for (FakeMob mob : this.plugin.getMobs())
			mob.unloadPlayer(player);
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();

		Bukkit.getScheduler().runTaskLater(FakeMobsPlugin.getPlugin(), new Runnable() {
			@Override
			public void run() {
				MobListener.this.plugin.updatePlayerView(player);
			}
		}, 5L);
	}

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();

		for (FakeMob mob : this.plugin.getMobs())
			mob.unloadPlayer(player);
	}
	
}
