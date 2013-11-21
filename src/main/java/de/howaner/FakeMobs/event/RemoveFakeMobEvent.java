package de.howaner.FakeMobs.event;

import de.howaner.FakeMobs.util.FakeMob;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RemoveFakeMobEvent extends Event {
	private final FakeMob mob;
	private static HandlerList handlers = new HandlerList();
	
	public RemoveFakeMobEvent(FakeMob mob) {
		this.mob = mob;
	}
	
	public FakeMob getMob() {
		return this.mob;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
	
}
