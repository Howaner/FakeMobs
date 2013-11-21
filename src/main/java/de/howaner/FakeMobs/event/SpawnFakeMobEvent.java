package de.howaner.FakeMobs.event;

import de.howaner.FakeMobs.util.FakeMob;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SpawnFakeMobEvent extends Event implements Cancellable {
	private final Location loc;
	private final EntityType type;
	private final FakeMob mob;
	private boolean cancelled = false;
	private static HandlerList handlers = new HandlerList();
	
	public SpawnFakeMobEvent(Location loc, EntityType type, FakeMob mob) {
		this.loc = loc;
		this.type = type;
		this.mob = mob;
	}
	
	public Location getLocation() {
		return this.loc;
	}
	
	public EntityType getType() {
		return this.type;
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
	
	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
}
