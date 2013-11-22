package de.howaner.FakeMobs.util;

import de.howaner.FakeMobs.FakeMobsPlugin;
import java.util.List;
import org.bukkit.entity.Player;

public class LookUpdate implements Runnable {
	private FakeMobsPlugin plugin;
	
	public LookUpdate(FakeMobsPlugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		for (FakeMob mob : this.plugin.getMobs()) {
			if (!mob.isPlayerLook()) return;
			List<Player> players = mob.getNearbyPlayers(6D);
			for (Player p : players)
				mob.sendLookPacket(p, p.getLocation());
		}
	}
	
}
