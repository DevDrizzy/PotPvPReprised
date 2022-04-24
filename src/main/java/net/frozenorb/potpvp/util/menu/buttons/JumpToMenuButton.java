package net.frozenorb.potpvp.util.menu.buttons;

import net.frozenorb.potpvp.util.menu.Button;
import lombok.AllArgsConstructor;
import net.frozenorb.potpvp.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public class JumpToMenuButton extends Button {

	private Menu menu;
	private ItemStack itemStack;

	@Override
	public String getName(Player player) {
		return this.itemStack.getItemMeta().getDisplayName();
	}

	@Override
	public List<String> getDescription(Player player) {
		return this.itemStack.getItemMeta().getLore();
	}

	@Override
	public Material getMaterial(Player player) {
		return this.itemStack.getType();
	}

	@Override
	public byte getDamageValue(Player player) {
		return (byte)this.itemStack.getDurability();
	}

	@Override
	public void clicked(Player player,int i,ClickType clickType) {
		menu.openMenu(player);
	}

}
