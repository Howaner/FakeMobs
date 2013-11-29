package de.howaner.FakeMobs.listener;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.GamePhase;
import de.howaner.FakeMobs.FakeMobsPlugin;
import de.howaner.FakeMobs.event.PlayerInteractFakeMobEvent;
import de.howaner.FakeMobs.util.FakeMob;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ProtocolListener implements PacketListener {
	private FakeMobsPlugin plugin;
	
	public ProtocolListener(FakeMobsPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onPacketSending(PacketEvent pe) {
		PacketContainer packet = pe.getPacket();
		final Player player = pe.getPlayer();
		
		if (packet.getID() == Packets.Server.MAP_CHUNK_BULK) {
			int[] chunksX = packet.getIntegerArrays().read(0);
			int[] chunksZ = packet.getIntegerArrays().read(1);
			for (int i = 0; i < chunksX.length; i++) {
				int chunkX = chunksX[i];
				int chunkZ = chunksZ[i];
				List<FakeMob> mobs = ProtocolListener.this.plugin.getMobsInChunk(player.getWorld(), chunkX, chunkZ);
				for (FakeMob mob : mobs)
					mob.sendSpawnPacket(player);
			}
		}
		
		if (packet.getID() == Packets.Server.MAP_CHUNK) {
			final int chunkX = packet.getIntegers().read(0);
			final int chunkZ = packet.getIntegers().read(1);
			
			/*System.out.println(player.getName());
			new Thread() {
				@Override
				public void run() {*/
					List<FakeMob> mobs = ProtocolListener.this.plugin.getMobsInChunk(player.getWorld(), chunkX, chunkZ);
					for (FakeMob mob : mobs)
						mob.sendSpawnPacket(player);
				/*}
			}.start();*/
		}
	}

	@Override
	public void onPacketReceiving(PacketEvent pe) {
		PacketContainer packet = pe.getPacket();
		Player player = pe.getPlayer();
		
		if (packet.getID() == Packets.Client.USE_ENTITY) {
			int id = packet.getIntegers().read(1) - 740;
			int actionId = packet.getIntegers().read(2);
			
			if (id < 0) return;
			FakeMob mob = this.plugin.getMob(id);
			if (mob == null || player.getWorld() != mob.getWorld()) return;
			
			if (player.isDead()) return;
			if (player.getWorld() != mob.getWorld() ||
					Math.max(player.getLocation().getX(), mob.getLocation().getX()) - Math.min(player.getLocation().getX(), mob.getLocation().getX()) > 4 ||
					Math.max(player.getLocation().getY(), mob.getLocation().getY()) - Math.min(player.getLocation().getY(), mob.getLocation().getY()) > 4 ||
					Math.max(player.getLocation().getZ(), mob.getLocation().getZ()) - Math.min(player.getLocation().getZ(), mob.getLocation().getZ()) > 4)
				return;
			
			PlayerInteractFakeMobEvent.Action action = (actionId == 0) ? PlayerInteractFakeMobEvent.Action.LEFT_CLICK : PlayerInteractFakeMobEvent.Action.RIGHT_CLICK;
			
			PlayerInteractFakeMobEvent event = new PlayerInteractFakeMobEvent(player, mob, action);
			Bukkit.getPluginManager().callEvent(event);
			if (!event.isCancelled())
				event.setCancelled(true);
		}
	}

	@Override
	public ListeningWhitelist getSendingWhitelist() {
		return new ListeningWhitelist(ListenerPriority.NORMAL, new Integer[] { Packets.Server.MAP_CHUNK }, GamePhase.BOTH, new ListenerOptions[0]);
	}

	@Override
	public ListeningWhitelist getReceivingWhitelist() {
		return new ListeningWhitelist(ListenerPriority.NORMAL, new Integer[] { Packets.Client.USE_ENTITY }, GamePhase.BOTH, ListenerOptions.INTERCEPT_INPUT_BUFFER);
	}
	
	@Override
	public Plugin getPlugin() {
		return this.plugin;
	}
	
}
