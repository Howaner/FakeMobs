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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ProtocolListener implements PacketListener {
	private FakeMobsPlugin plugin;
	
	public ProtocolListener(FakeMobsPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onPacketSending(PacketEvent pe) { }

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
		return null;
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
