package de.howaner.FakeMobs.merchant;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
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
				Method m = this.getNMSClass().getDeclaredMethod("getBuyItem3");
				m.setAccessible(true);
				return m.invoke(this.merchantRecipe);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		
		public Object getMerchantRecipe() {
			return this.merchantRecipe;
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
			e.printStackTrace();
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
