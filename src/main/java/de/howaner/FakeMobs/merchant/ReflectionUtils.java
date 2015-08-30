package de.howaner.FakeMobs.merchant;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ReflectionUtils {
	
	public static class OBCCraftItemStack {
		
		public static Class getOBCClass() {
			return ReflectionUtils.getClassByName(ReflectionUtils.getOBCPackageName() + ".inventory.CraftItemStack");
		}
		
		public static ItemStack asBukkitCopy(Object nmsItemStack) {
			try {
				Method m = getOBCClass().getDeclaredMethod("asBukkitCopy", ReflectionUtils.getClassByName(ReflectionUtils.getNMSPackageName() + ".ItemStack"));
				m.setAccessible(true);
				return (ItemStack) m.invoke(null, nmsItemStack);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public static Object asNMSCopy(ItemStack stack) {
			try {
				Method m = getOBCClass().getDeclaredMethod("asNMSCopy", ItemStack.class);
				m.setAccessible(true);
				return m.invoke(null, stack);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
	}
	
	public static class NMSMerchantRecipeList {
		private Object handle;
		
		public static Class getNMSClass() {
			return ReflectionUtils.getClassByName(ReflectionUtils.getNMSPackageName() + ".MerchantRecipeList");
		}
		
		public NMSMerchantRecipeList() {
			try {
				this.handle = getNMSClass().newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public NMSMerchantRecipeList(Object handle) {
			this.handle = handle;
		}
		
		public Object getHandle() {
			return this.handle;
		}
		
		public void clear() {
			try {
				Method m = ArrayList.class.getDeclaredMethod("clear");
				m.setAccessible(true);
				m.invoke(this.handle);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void add(NMSMerchantRecipe recipe) {
			try {
				Method m = ArrayList.class.getDeclaredMethod("add", Object.class);
				//Method m = getNMSClass().getDeclaredMethod("add", NMSMerchantRecipe.getNMSClass());
				m.setAccessible(true);
				m.invoke(this.handle, recipe.getMerchantRecipe());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public List<NMSMerchantRecipe> getRecipes() {
			List<NMSMerchantRecipe> recipeList = new ArrayList<NMSMerchantRecipe>();
			for (Object obj : (List) handle) {
				recipeList.add(new NMSMerchantRecipe(obj));
			}
			return recipeList;
		}
	}
	
	public static class NMSMerchantRecipe {
		private Object merchantRecipe;
		
		public NMSMerchantRecipe(Object merchantRecipe) {
			this.merchantRecipe = merchantRecipe;
		}
		
		public NMSMerchantRecipe(Object item1, Object item3) {
			this(item1, null, item3);
		}
		
		public NMSMerchantRecipe(Object item1, Object item2, Object item3) {
			try {
				Class isClass = ReflectionUtils.getClassByName(ReflectionUtils.getNMSPackageName() + ".ItemStack");
				this.merchantRecipe = getNMSClass().getDeclaredConstructor(isClass, isClass, isClass).newInstance(item1, item2, item3);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public static Class getNMSClass() {
			return ReflectionUtils.getClassByName(ReflectionUtils.getNMSPackageName() + ".MerchantRecipe");
		}
		
		public Object getBuyItem1() {
			try {
				Method m = getNMSClass().getDeclaredMethod("getBuyItem1");
				m.setAccessible(true);
				return m.invoke(this.merchantRecipe);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public Object getBuyItem2() {
			try {
				Method m = getNMSClass().getDeclaredMethod("getBuyItem2");
				m.setAccessible(true);
				return m.invoke(this.merchantRecipe);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public Object getBuyItem3() {
			try {
				Method m = getNMSClass().getDeclaredMethod("getBuyItem3");
				m.setAccessible(true);
				return m.invoke(this.merchantRecipe);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		public int getMaxUses() {
			try {
				Field field = getNMSClass().getDeclaredField("maxUses");
				field.setAccessible(true);
				return (int) field.getByte(this.merchantRecipe);
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}

		public void setMaxUses(int maxUses) {
			try {
				Field field = getNMSClass().getDeclaredField("maxUses");
				field.setAccessible(true);
				field.set(this.merchantRecipe, maxUses);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public Object getMerchantRecipe() {
			return this.merchantRecipe;
		}
		
	}

	public static class PlayerInfoAction {
		public static Object UPDATE_GAME_MODE    = getNMSAction("UPDATE_GAME_MODE");
		public static Object ADD_PLAYER          = getNMSAction("ADD_PLAYER");
		public static Object UPDATE_DISPLAY_NAME = getNMSAction("UPDATE_DISPLAY_NAME");
		public static Object REMOVE_PLAYER       = getNMSAction("REMOVE_PLAYER");
		private static Class nmsClass;

		// Return: EnumPlayerInfoAction
		private static Object getNMSAction(String name) {
			try {
				Field field = getNMSClass().getDeclaredField(name);
				field.setAccessible(true);
				return field.get(null);
			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}

		public static Class getNMSClass() {
			if (nmsClass == null) {
				nmsClass = getClassByName(getNMSPackageName() + ".PacketPlayOutPlayerInfo$EnumPlayerInfoAction"); // Spigot 1.8.3
				if (nmsClass == null) {
					// Spigot 1.8.0
					nmsClass = getClassByName(getNMSPackageName() + ".EnumPlayerInfoAction");
				}
			}
			return nmsClass;
		}
	}

	// Return: EnumGamemode
	public static Object createNMSGameMode(GameMode gameMode) {
		Class c = getClassByName(getNMSPackageName() + ".EnumGamemode");  // Spigot 1.8.0
		if (c == null) {
			// Spigot 1.8.3
			c = getClassByName(getNMSPackageName() + ".WorldSettings$EnumGamemode");
		}

		try {
			Method method = c.getDeclaredMethod("getById", int.class);
			method.setAccessible(true);
			return method.invoke(null, gameMode.getValue());
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static Object createPlayerInfoData(Object gameProfile, GameMode gameMode, int ping, String nickName) {
		boolean is_1_8_3 = true;
		Class playerInfoDataClass = getClassByName(getNMSPackageName() + ".PacketPlayOutPlayerInfo$PlayerInfoData");

		if (playerInfoDataClass == null) {
			// Spigot 1.8
			playerInfoDataClass = getClassByName(getNMSPackageName() + ".PlayerInfoData");
			is_1_8_3 = false;
		}

		Object nmsGameMode = createNMSGameMode(gameMode);

		try {
			Constructor constructor = playerInfoDataClass.getDeclaredConstructor(
					getClassByName(getNMSPackageName() + ".PacketPlayOutPlayerInfo"),
					getClassByName("com.mojang.authlib.GameProfile"),
					int.class,
					nmsGameMode.getClass(),
					getClassByName(getNMSPackageName() + ".IChatBaseComponent")
			);
			constructor.setAccessible(true);
			return constructor.newInstance(null, gameProfile, ping, nmsGameMode, createNMSTextComponent(nickName));
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static Object fillProfileProperties(Object gameProfile) {
		Class serverClass = getClassByName(getNMSPackageName() + ".MinecraftServer");
		Class sessionServiceClass = getClassByName("com.mojang.authlib.minecraft.MinecraftSessionService");

		try {
			Object minecraftServer;
			{
				Method method = serverClass.getDeclaredMethod("getServer");
				method.setAccessible(true);
				minecraftServer = method.invoke(null);
			}

			Object sessionService;
			{
				String methodName;
				if (existsMethod(serverClass, "aC", sessionServiceClass))
					methodName = "aC"; //1.8.3
				else
					methodName = "aD"; //1.8.8
				Method method = serverClass.getDeclaredMethod(methodName);
				method.setAccessible(true);
				sessionService = method.invoke(minecraftServer);
			}

			Object result;
			{
				Method method = sessionServiceClass.getDeclaredMethod("fillProfileProperties", gameProfile.getClass(), boolean.class);
				method.setAccessible(true);
				result = method.invoke(sessionService, gameProfile, true);
			}

			return result;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private static boolean existsMethod(Class clazz, String methodName, Class returnClass) {
		for (Method method : clazz.getDeclaredMethods()) {
			if (method.getName().equals(methodName) && method.getGenericReturnType() == returnClass) {
				return true;
			}
		}
		return false;
	}

	/** Return: GameProfile */
	public static Object searchUUID(String playerName) {
		Class serverClass = getClassByName(getNMSPackageName() + ".MinecraftServer");
		Class userCacheClass = getClassByName(getNMSPackageName() + ".UserCache");

		try {
			Object minecraftServer;
			{
				Method method = serverClass.getDeclaredMethod("getServer");
				method.setAccessible(true);
				minecraftServer = method.invoke(null);
			}

			Object userCache;
			{
				Method method = serverClass.getDeclaredMethod("getUserCache");
				method.setAccessible(true);
				userCache = method.invoke(minecraftServer);
			}

			{
				Method method = userCacheClass.getDeclaredMethod("getProfile", String.class);
				method.setAccessible(true);
				return method.invoke(userCache, playerName);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static Object createNMSTextComponent(String text) {
		if (text == null || text.isEmpty()) {
			return null;
		}

		Class c = getClassByName(getNMSPackageName() + ".ChatComponentText");
		try {
			Constructor constructor = c.getDeclaredConstructor(String.class);
			return constructor.newInstance(text);
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	public static Object toEntityHuman(Player player) {
		try {
			Class c = getClassByName(getOBCPackageName() + ".entity.CraftPlayer");
			Method m = c.getDeclaredMethod("getHandle");
			m.setAccessible(true);
			return m.invoke(player);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static Class getClassByName(String name) {
		try {
			return Class.forName(name);
		} catch (Exception e) {
			// Class not found
			return null;
		}
	}
	
	public static Object getField(Class c, Object obj, String key) throws Exception {
		Field field = c.getDeclaredField(key);
		field.setAccessible(true);
		return field.get(obj);
	}
	
	public static void replaceField(Class c, Object obj, String key, Object value) throws Exception {
		Field field = c.getDeclaredField(key);
		field.setAccessible(true);
		field.set(obj, value);
	}
	
	/**
	 * Get the Package from net.minecraft
	 * Example: net.minecraft.v1_6_R3
	 */
	public static String getNMSPackageName() {
		return "net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}
	
	/**
	 * Get the Package from org.bukkit.craftbukkit
	 * Example: org.bukkit.craftbukkit.v1_6_R3
	 */
	public static String getOBCPackageName() {
		return "org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}
	
}
