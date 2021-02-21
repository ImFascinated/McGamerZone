package zone.themcgamer.core.deliveryMan;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import org.bukkit.entity.Player;
import zone.themcgamer.common.TimeUtils;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.common.ItemBuilder;
import zone.themcgamer.core.common.SkullTexture;
import zone.themcgamer.core.common.Style;
import zone.themcgamer.core.common.menu.Button;
import zone.themcgamer.core.common.menu.MenuType;
import zone.themcgamer.core.common.menu.UpdatableMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Nicholas
 */
public class DeliveryManMenu extends UpdatableMenu {
    private final DeliveryManManager deliveryManManager;

    public DeliveryManMenu(Player player, DeliveryManManager deliveryManManager) {
        super(player, "§c§lDelivery §r" + DeliveryManManager.DELIVERY_MAN_NAME + " Reward's", 4, MenuType.CHEST, TimeUnit.SECONDS.toMillis(1L));
        this.deliveryManManager = deliveryManManager;
    }

    @Override
    protected void onOpen() {
        // TODO: 2/19/2021 Make these rewards actually work - Nick
        set(2, 3, new Button(new ItemBuilder(XMaterial.PLAYER_HEAD)
                .setSkullOwner(SkullTexture.EMERALD_BLOCK)
                .setName("§b§lVoting Rewards")
                .addLoreLine("§7Click to view your §b0 §7unclaimed")
                .addLoreLine("§7voting rewards!")
                .toItemStack(), event -> player.sendMessage("voting rewards")));
        set(2, 5, new Button(new ItemBuilder(XMaterial.PLAYER_HEAD)
                .setSkullOwner(SkullTexture.DISCORD)
                .setName("§d§lNitro Rewards")
                .addLoreLine("§7Click to view your §d0 §7unclaimed")
                .addLoreLine("§7nitro boosting rewards!")
                .toItemStack(), event -> player.sendMessage("nitro rewards")));
    }

    @Override
    public void onUpdate() {
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (optionalAccount.isEmpty())
            return;
        Account account = optionalAccount.get();
        Optional<DeliveryManClient> optionalClient = deliveryManManager.lookup(player.getUniqueId());
        if (optionalClient.isEmpty())
            return;
        DeliveryManClient deliveryManClient = optionalClient.get();
        int slot = 2;
        for (DeliveryManReward reward : DeliveryManReward.values()) {
            boolean canClaim = (deliveryManClient.canClaim(reward) && account.hasRank(reward.getRequiredRank()));
            String[] rewardNames = reward.getRewardPackage().getRewardNames(player, account);

            List<String> lore = new ArrayList<>();
            lore.add((canClaim ? "§7You haven't claimed this reward!" : "&7You already claimed this reward!"));
            lore.add("");
            if (reward == DeliveryManReward.MONTHLY) {
                if (account.hasRank(reward.getRequiredRank()))
                    lore.add("§7Your Rank: §b" + account.getPrimaryRankName());
                else lore.add("§7Only §f"  + reward.getRequiredRank().getPrefix() + " §7can claim this reward!");
                lore.add("");
            }

            lore.add("§6&lRewards:");
            if (rewardNames.length < 1)
                lore.add(" §b▪ &7None");
            else lore.addAll(Arrays.asList(rewardNames));

            if (!deliveryManClient.canClaim(reward)) {
                lore.add("");
                lore.add("§7Next Delivery: §b" + TimeUtils.formatIntoDetailedString((deliveryManClient.getLastClaim(reward)
                        + reward.getClaimCooldown()) - System.currentTimeMillis(), true));
            }
            ItemBuilder itemBuilder = new ItemBuilder(canClaim ? XMaterial.PLAYER_HEAD : XMaterial.MINECART);
            if (canClaim)
                itemBuilder.setSkullOwner(reward.getRewardPackage().getIconTexture(player, account));
            itemBuilder.setName((canClaim ? "§a" : "§c") + "§l" + reward.getDisplayName());
            itemBuilder.setLore(lore);
            set(1, slot, new Button(itemBuilder.toItemStack(), event -> {
                if (!canClaim) {
                    player.sendMessage(Style.main(DeliveryManManager.DELIVERY_MAN_NAME, "You can not claim this reward right now!"));
                    player.playSound(player.getLocation(), XSound.ENTITY_ENDERMITE_AMBIENT.parseSound(), 0.9f, 1f);
                    return;
                }
                close();
                deliveryManManager.claimReward(player, reward);
            }));
            slot+= 2;
        }
    }
}