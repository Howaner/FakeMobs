package de.howaner.FakeMobs.listener;

import de.howaner.FakeMobs.event.PlayerInteractFakeMobEvent;
import de.howaner.FakeMobs.event.PlayerInteractFakeMobEvent.Action;
import de.howaner.FakeMobs.interact.InteractAction;
import de.howaner.FakeMobs.util.FakeMob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class InteractListener implements Listener {
	
	@EventHandler
	public void onInteractFakeMobEvent(PlayerInteractFakeMobEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK) return;
		Player player = event.getPlayer();
		FakeMob mob = event.getMob();
		
		for (InteractAction action : mob.getInteractActions())
			action.onInteract(player, mob);
	}
	
}
