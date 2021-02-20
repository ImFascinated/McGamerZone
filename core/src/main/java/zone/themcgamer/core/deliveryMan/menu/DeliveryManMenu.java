package zone.themcgamer.core.deliveryMan.menu;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.entity.Player;
import zone.themcgamer.common.TimeUtils;
import zone.themcgamer.core.account.Account;
import zone.themcgamer.core.account.AccountManager;
import zone.themcgamer.core.common.ItemBuilder;
import zone.themcgamer.core.common.SkullTexture;
import zone.themcgamer.core.common.menu.Button;
import zone.themcgamer.core.common.menu.MenuType;
import zone.themcgamer.core.common.menu.UpdatableMenu;
import zone.themcgamer.core.deliveryMan.DeliveryManClient;
import zone.themcgamer.core.deliveryMan.DeliveryManManager;
import zone.themcgamer.core.deliveryMan.DeliveryManReward;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Nicholas
 */
public class DeliveryManMenu extends UpdatableMenu {
    private final DeliveryManManager deliveryManManager;

    public DeliveryManMenu(Player player, DeliveryManManager deliveryManManager) {
        super(player, DeliveryManManager.DELIVERY_MAN_NAME, 4, MenuType.CHEST, TimeUnit.SECONDS.toMillis(1L));
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
                .setName("§d§lNitro Reward")
                .addLoreLine("§7Claimable: §cNo")
                .toItemStack(), event -> player.sendMessage("nitro reward")));
    }

    @Override
    public void onUpdate() {
        Optional<Account> optionalAccount = AccountManager.fromCache(player.getUniqueId());
        if (optionalAccount.isEmpty())
            return;
        Optional<DeliveryManClient> optionalClient = deliveryManManager.lookup(player.getUniqueId());
        if (optionalClient.isEmpty())
            return;
        int slot = 2;
        for (DeliveryManReward reward : DeliveryManReward.values()) {
            boolean canClaim = (deliveryManManager.canClaim(player, reward) && optionalAccount.get().hasRank(reward.getRequiredRank()));
            ItemBuilder itemBuilder = new ItemBuilder(XMaterial.PLAYER_HEAD)
                    .setSkullOwner((canClaim ? reward.getRewardPackage().getIconTexture(player, optionalAccount.get()) : SkullTexture.COAL_BLOCK))
                    .setName((canClaim ? "§a" : "§c") + "§l" + reward.getDisplayName())
                    .addLoreLine("§7Claimable: " + (canClaim ? "§aYes" : "§cNo"));
            if (reward == DeliveryManReward.MONTHLY) {
                if (optionalAccount.get().hasRank(reward.getRequiredRank())) {
                    itemBuilder.addLoreLine("")
                            .addLoreLine("§7Your Rank: §b" + optionalAccount.get().getPrimaryRankName());
                } else {
                    itemBuilder.addLoreLine("")
                            .addLoreLine("§cOnly §bdonators §ccan claim a §b" + reward.getDisplayName() + "§c!");
                }
            }
            itemBuilder.addLoreLine("")
                    .addLoreLine("§7Rewards:")
                    .addLoreLine(" §bNone!"); // TODO: 2/19/2021 This is static at the moment due to the fact the reward base isn't done.
            if (!deliveryManManager.canClaim(player, reward)) {
                itemBuilder.addLoreLine("")
                        .addLoreLine("§7Next Delivery: §b" + TimeUtils.formatIntoDetailedString((optionalClient.get().getLastClaimedRewards().get(reward)
                                + reward.getClaimCooldown()) - System.currentTimeMillis(), true));
            }
            set(1, slot, new Button(itemBuilder.toItemStack(), event -> {
                if (!canClaim)
                    return;
                close();
                deliveryManManager.claimReward(player, reward);
            }));
            slot += 2;
        }
    }
}