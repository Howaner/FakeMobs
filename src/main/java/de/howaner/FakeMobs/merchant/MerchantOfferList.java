package de.howaner.FakeMobs.merchant;

import java.util.ArrayList;
import net.minecraft.server.v1_6_R3.MerchantRecipe;
import net.minecraft.server.v1_6_R3.MerchantRecipeList;

public class MerchantOfferList extends ArrayList<MerchantOffer> {
	
	public MerchantOfferList(MerchantRecipeList handle) {
		for (Object recipe : handle)
			this.add(new MerchantOffer((MerchantRecipe)recipe));
	}
	
	public MerchantRecipeList getHandle() {
		MerchantRecipeList list = new MerchantRecipeList();
		for (MerchantOffer o : this) {
			list.add(o.getHandle());
		}
		return list;
	}
	
}
