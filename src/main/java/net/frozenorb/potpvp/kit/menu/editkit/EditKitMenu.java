package net.frozenorb.potpvp.kit.menu.editkit;

import com.google.common.base.Preconditions;
import net.frozenorb.potpvp.PotPvPRP;
import net.frozenorb.potpvp.kit.Kit;
import net.frozenorb.potpvp.kit.menu.kits.KitDeleteButton;
import net.frozenorb.potpvp.kit.menu.kits.KitRenameButton;
import net.frozenorb.potpvp.util.InventoryUtils;
import net.frozenorb.potpvp.util.menu.Button;
import net.frozenorb.potpvp.util.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public final class EditKitMenu extends Menu {

    private static final int EDITOR_X_OFFSET = 2;
    private static final int EDITOR_Y_OFFSET = 2;

    private final Kit kit;

    public EditKitMenu(Kit kit) {
        setNoncancellingInventory(true);
        setUpdateAfterClick(false);
        setPlaceholder(true);

        this.kit = Preconditions.checkNotNull(kit, "kit");
    }

    @Override
    public void onOpen(Player player) {
        player.getInventory().setContents(kit.getInventoryContents());

        Bukkit.getScheduler().runTaskLater(PotPvPRP.getInstance(), player::updateInventory, 1L);
    }

    @Override
    public void onClose(Player player) {
        InventoryUtils.resetInventoryDelayed(player);
    }

    @Override
    public String getTitle(Player player) {
        return "Editing " + kit.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        // Top row
        buttons.put(getSlot(2, 0), new KitRenameButton(kit));
        buttons.put(getSlot(4, 0), new KitDeleteButton(kit.getType(), kit.getSlot()));
        buttons.put(getSlot(6, 0), new CancelKitEditButton(kit.getType()));

        // Bottom row
        buttons.put(getSlot(2, 4), new SaveButton(kit));
        buttons.put(getSlot(4, 4), new ClearInventoryButton());
        buttons.put(getSlot(6, 4), new LoadDefaultKitButton(kit.getType()));


        if (kit.getType().isEditorSpawnAllowed()) {
            int x = 0;
            int y = 0;

            for (ItemStack editorItem : kit.getType().getEditorItems()) {
                if (editorItem != null) {
                    if (editorItem.getType() != Material.AIR) {
                        buttons.put(getSlot(x + EDITOR_X_OFFSET, y + EDITOR_Y_OFFSET), new TakeItemButton(editorItem));
                    }
                }

                x++;

                if (x > 7) {
                    x = 0;
                    y++;

                    if (y >= 3) {
                        break;
                    }
                }
            }
        }

        return buttons;
    }

}