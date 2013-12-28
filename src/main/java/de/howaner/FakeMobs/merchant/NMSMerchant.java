package de.howaner.FakeMobs.merchant;

import net.minecraft.server.v1_6_R3.EntityHuman;
import net.minecraft.server.v1_6_R3.EntityPlayer;
import net.minecraft.server.v1_6_R3.IMerchant;
import net.minecraft.server.v1_6_R3.ItemStack;
import net.minecraft.server.v1_6_R3.MerchantRecipe;
import net.minecraft.server.v1_6_R3.MerchantRecipeList;

public class NMSMerchant implements IMerchant {
	private MerchantRecipeList o = new MerchantRecipeList();
	private transient EntityHuman c;
	
	@Override
	public void a_(EntityHuman player) {
		this.c = player;
	}

	@Override
	public EntityHuman m_() {
		return this.c;
	}

	@Override
	public MerchantRecipeList getOffers(EntityHuman player) {
		return this.o;
	}

	@Override
	public void a(MerchantRecipe recipe) {
		this.o.add(recipe);
	}

	@Override
	public void a_(ItemStack is) {
		
	}
	
	/* Other Methods */
	public void setRecipes(MerchantRecipeList recipes) {
		this.o = recipes;
	}
	
	public void openTrading(EntityPlayer player, String title) {
		this.c = player;
		this.c.openTrade(this, title);
	}
	
}
