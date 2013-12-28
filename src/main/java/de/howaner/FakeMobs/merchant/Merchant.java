package de.howaner.FakeMobs.merchant;

import org.bukkit.craftbukkit.v1_6_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Merchant {
	private NMSMerchant h;
	private String title = null;
	
	public Merchant() {
		this.h = new NMSMerchant();
	}
	
	public String getTitle() {
		return this.title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public Merchant addOffer(MerchantOffer offer) {
		this.h.a(offer.getHandle());
		return this;
	}
	
	public Merchant addOffers(MerchantOffer[] offers) {
		for (MerchantOffer o : offers) {
			addOffer(o);
		}
		return this;
	}
	
	public Merchant setOffers(MerchantOfferList offers) {
		this.h.setRecipes(offers.getHandle());
		return this;
	}
	
	public MerchantOfferList getOffers() {
		return new MerchantOfferList(this.h.getOffers(null));
	}
	
	public boolean hasCustomer() {
		return this.h.m_() != null;
	}
	
	public Player getCustomer() {
		return (Player)(this.h.m_() == null ? null : this.h.m_().getBukkitEntity());
	}
	
	public Merchant setCustomer(Player player) {
		this.h.a_(player == null ? null : ((CraftPlayer)player).getHandle());
		return this;
	}
	
	public void openTrading(Player player) {
		this.h.openTrading(((CraftPlayer)player).getHandle(), this.title);
	}
	
	protected NMSMerchant getHandle() {
		return this.h;
	}
	
	@Override
	public Merchant clone() {
		return new Merchant().setOffers(getOffers()).setCustomer(getCustomer());
	}
	
}
