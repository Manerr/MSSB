package fr.maner.mssb.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemFactory {

	protected ItemStack item;

	protected int amount = 1;
	protected String name;
	protected boolean unbreakable = false;
	
	private Double attackDamage;

	protected List<String> lores = new ArrayList<String>();
	protected Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
	protected Set<ItemFlag> itemFlags = new HashSet<ItemFlag>();

	public ItemFactory(Material material) {
		this.item = new ItemStack(material);
	}

	public ItemFactory(ItemStack itemStack) {
		this.item = itemStack;
		this.amount = item.getAmount();

		ItemMeta itemMeta = item.getItemMeta();
		this.unbreakable = itemMeta.isUnbreakable();
		this.enchantments = itemMeta.getEnchants();
		this.itemFlags = itemMeta.getItemFlags();

		if (!itemMeta.getDisplayName().isEmpty())
			this.name = itemMeta.getDisplayName();
		if (itemMeta.getLore() != null)
			this.lores = itemMeta.getLore();
	}

	public ItemFactory setAmount(int amount) {
		this.amount = amount;
		return this;
	}

	public ItemFactory setName(String name) {
		this.name = name;
		return this;
	}

	public ItemFactory setUnbreakable(boolean unbreakable) {
		this.unbreakable = unbreakable;
		return this;
	}

	public ItemFactory addLore(String... lore) {
		this.lores.addAll(Arrays.asList(lore));
		return this;
	}

	public ItemFactory setAttackDamage(double attackDamage) {
		this.attackDamage = attackDamage;
		return this;
	}

	public ItemFactory setLore(String... lore) {
		this.lores.clear();
		this.addLore(lore);
		return this;
	}

	public ItemFactory addEnchantment(Enchantment enchant, int power) {
		this.enchantments.put(enchant, power);
		return this;
	}

	public ItemFactory addItemFlags(ItemFlag... itemFlags) {
		this.itemFlags.addAll(Arrays.asList(itemFlags));
		return this;
	}

	public ItemStack build() {
		item.setAmount(amount);
		item.addUnsafeEnchantments(enchantments);

		ItemMeta itemMeta = item.getItemMeta();

		if (name != null)
			itemMeta.setDisplayName(convert(name));

		if (attackDamage != null) {
			itemMeta.removeAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE);
			itemMeta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(),
					"attackDamage", attackDamage, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
		}

		itemFlags.forEach(itemMeta::addItemFlags);

		itemMeta.setLore(lores.stream().map(lore -> convert(lore)).collect(Collectors.toList()));
		itemMeta.setUnbreakable(unbreakable);

		this.item.setItemMeta(itemMeta);
		return item;
	}

	private String convert(String s) {
		return ChatColor.translateAlternateColorCodes('&', s);
	}
}
