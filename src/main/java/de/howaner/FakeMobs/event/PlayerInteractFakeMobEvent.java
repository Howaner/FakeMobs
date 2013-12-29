package de.howaner.FakeMobs.event;

import de.howaner.FakeMobs.util.FakeMob;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerInteractFakeMobEvent extends Event {
	private final Player player;
	private final FakeMob mob;
	private final Action action;
	private static HandlerList handlers = new HandlerList();
	
	public PlayerInteractFakeMobEvent(Player player, FakeMob mob, Action action) {
		this.player = player;
		this.mob = mob;
		this.action = action;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public FakeMob getMob() {
		return this.mob;
	}
	
	public Action getAction() {
		return this.action;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public static enum Action {
		LEFT_CLICK,RIGHT_CLICK;
	}
	
}
