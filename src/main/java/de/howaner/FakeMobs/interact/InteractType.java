package de.howaner.FakeMobs.interact;

import java.util.HashMap;
import java.util.Map;

public enum InteractType {
	COMMAND,TEXT,ITEM,EXP;
	
	public static final Map<InteractType, Class<? extends InteractAction>> actions = new HashMap<InteractType, Class<? extends InteractAction>>();
	
	static {
		actions.put(InteractType.COMMAND, InteractCommand.class);
		actions.put(InteractType.TEXT, InteractText.class);
		actions.put(InteractType.ITEM, InteractItem.class);
		actions.put(InteractType.EXP, InteractExp.class);
	}
	
	public Class<? extends InteractAction> getActionClass() {
		return actions.get(this);
	}
	
	public static String getStringList() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < values().length; i++) {
			if (i != 0) builder.append(", ");
			builder.append(values()[i]);
		}
		return builder.toString();
	}
	
	public static InteractType getByName(String name) {
		try {
			return valueOf(name.toUpperCase());
		} catch (Exception e) {
			return null;
		}
	}
	
}
