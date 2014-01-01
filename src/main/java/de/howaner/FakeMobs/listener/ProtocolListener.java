package de.howaner.FakeMobs.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.ListeningWhitelist;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import de.howaner.FakeMobs.FakeMobsPlugin;
import de.howaner.FakeMobs.event.PlayerInteractFakeMobEvent;
import de.howaner.FakeMobs.event.PlayerInteractFakeMobEvent.Action;
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
		
		if (packet.getType() == PacketType.Play.Client.USE_ENTITY) {
			int id = ((packet.getIntegers().size() > 1) ? packet.getIntegers().read(1) : packet.getIntegers().read(0)) - 740;
			
			if (id < 0) return;
			FakeMob mob = this.plugin.getMob(id);
			if (mob == null || player.getWorld() != mob.getWorld()) return;
			
			if (player.isDead()) return;
			//Standard is 4. But I use 50 for better selection!
			if (player.getWorld() != mob.getWorld() ||
					Math.max(player.getLocation().getX(), mob.getLocation().getX()) - Math.min(player.getLocation().getX(), mob.getLocation().getX()) > 50 ||
					Math.max(player.getLocation().getY(), mob.getLocation().getY()) - Math.min(player.getLocation().getY(), mob.getLocation().getY()) > 50 ||
					Math.max(player.getLocation().getZ(), mob.getLocation().getZ()) - Math.min(player.getLocation().getZ(), mob.getLocation().getZ()) > 50)
				return;
			
			Action action;
			try {
				action = (packet.getEntityUseActions().read(0) == EntityUseAction.ATTACK) ? Action.LEFT_CLICK : Action.RIGHT_CLICK;
			} catch (Exception e) {
				action = (packet.getIntegers().read(2) == 0) ? Action.RIGHT_CLICK : Action.LEFT_CLICK;
			}
			
			PlayerInteractFakeMobEvent event = new PlayerInteractFakeMobEvent(player, mob, action);
			Bukkit.getPluginManager().callEvent(event);
			pe.setCancelled(true);
		}
	}

	@Override
	public ListeningWhitelist getSendingWhitelist() {
		return ListeningWhitelist.EMPTY_WHITELIST;
	}

	@Override
	public ListeningWhitelist getReceivingWhitelist() {
		return ListeningWhitelist.newBuilder().
				priority(ListenerPriority.NORMAL).
				types(PacketType.Play.Client.USE_ENTITY).
				gamePhase(GamePhase.PLAYING).
				options(new ListenerOptions[0]).
				build();
		//return new ListeningWhitelist(ListenerPriority.NORMAL, new Integer[] { Packets.Client.USE_ENTITY }, GamePhase.BOTH, ListenerOptions.INTERCEPT_INPUT_BUFFER);
	}
	
	@Override
	public Plugin getPlugin() {
		return this.plugin;
	}
	
}
