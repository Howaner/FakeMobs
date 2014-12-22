package de.howaner.FakeMobs.merchant;

import de.howaner.FakeMobs.merchant.ReflectionUtils.NMSMerchantRecipe;
import de.howaner.FakeMobs.merchant.ReflectionUtils.OBCCraftItemStack;
import org.bukkit.inventory.ItemStack;

public class MerchantOffer {
	private ItemStack[] items = new ItemStack[3];
	private int maxUses = Integer.MAX_VALUE;
	
	public MerchantOffer(ItemStack is1, ItemStack is2, ItemStack re) {
		this.items[0] = is1;
		this.items[1] = is2;
		this.items[2] = re;
	}
	
	public MerchantOffer(ItemStack is, ItemStack re) {
		this(is, null, re);
	}
	
	protected MerchantOffer(NMSMerchantRecipe handle) {
		this.items[0] = OBCCraftItemStack.asBukkitCopy(handle.getBuyItem1());
		this.items[1] = (handle.getBuyItem2() == null) ? null : OBCCraftItemStack.asBukkitCopy(handle.getBuyItem2());
		this.items[2] = OBCCraftItemStack.asBukkitCopy(handle.getBuyItem3());
	}
	
	protected NMSMerchantRecipe getHandle() {
		NMSMerchantRecipe nms;
		if (this.items[1] == null)
			nms = new NMSMerchantRecipe(OBCCraftItemStack.asNMSCopy(this.items[0]), OBCCraftItemStack.asNMSCopy(this.items[2]));
		else
			nms = new NMSMerchantRecipe(OBCCraftItemStack.asNMSCopy(this.items[0]), OBCCraftItemStack.asNMSCopy(this.items[1]), OBCCraftItemStack.asNMSCopy(this.items[2]));

		nms.setMaxUses(this.maxUses);
		return nms;
	}
	
	public ItemStack getFirstInput() {
		return this.items[0];
	}
	
	public ItemStack getSecondInput() {
		return this.items[1];
	}
	
	public ItemStack getOutput() {
		return this.items[2];
	}

	public int getMaxUses() {
		return this.maxUses;
	}

	public void setMaxUses(int maxUses) {
		this.maxUses = maxUses;
	}

	@Override
	public MerchantOffer clone() {
		MerchantOffer clone = new MerchantOffer(this.getFirstInput(), this.getSecondInput(), this.getOutput());
		clone.setMaxUses(this.getMaxUses());
		return clone;
	}
	
}
