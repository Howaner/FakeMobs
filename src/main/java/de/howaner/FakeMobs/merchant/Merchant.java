package de.howaner.FakeMobs.merchant;

import de.howaner.FakeMobs.merchant.ReflectionUtils.NMSMerchantRecipe;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Merchant {
	private NMSMerchant h;
	private String title = null;
	
	public Merchant() {
		this.h = new NMSMerchant();
		this.h.proxy = Proxy.newProxyInstance(Bukkit.class.getClassLoader(), new Class[] { ReflectionUtils.getClassByName(ReflectionUtils.getNMSPackageName() + ".IMerchant") }, this.h);
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public List<MerchantOffer> getOffers() {
		List<MerchantOffer> offerList = new ArrayList<MerchantOffer>();
		for (Object recipe : (List) this.h.getOffers(null)) {
			offerList.add(new MerchantOffer(new NMSMerchantRecipe(recipe)));
		}
		return offerList;
	}
	
	public Merchant addOffer(MerchantOffer offer) {
		this.h.a(offer.getHandle().getMerchantRecipe());
		return this;
	}
	
	public Merchant addOffers(MerchantOffer[] offers) {
		for (MerchantOffer o : offers) {
			addOffer(o);
		}
		return this;
	}
	
	public Merchant setOffers(List<MerchantOffer> offers) {
		this.h.clearRecipes();
		for (MerchantOffer o : offers)
			this.addOffer(o);
		return this;
	}
	
	public boolean hasCustomer() {
		return this.h.b() != null;
	}
	
	public Player getCustomer() {
		return (Player)(this.h.b() == null ? null : this.h.getBukkitEntity());
	}
	
	public Merchant setCustomer(Player player) {
		this.h.a_(player == null ? null : ReflectionUtils.toEntityHuman(player));
		return this;
	}
	
	public void openTrading(Player player) {
		this.h.openTrading(ReflectionUtils.toEntityHuman(player), this.title);
	}
	
	protected NMSMerchant getHandle() {
		return this.h;
	}
	
	@Override
	public Merchant clone() {
		return new Merchant().setOffers(getOffers()).setCustomer(getCustomer());
	}
	
}
