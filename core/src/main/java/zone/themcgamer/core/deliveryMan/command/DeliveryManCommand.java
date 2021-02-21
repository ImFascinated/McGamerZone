package zone.themcgamer.core.deliveryMan.command;

import zone.themcgamer.core.command.Command;
import zone.themcgamer.core.command.CommandProvider;
import zone.themcgamer.core.deliveryMan.DeliveryManManager;
import zone.themcgamer.core.deliveryMan.DeliveryManMenu;

/**
 * @author Nicholas
 */
public class DeliveryManCommand {
    private final DeliveryManManager deliveryManManager;

    public DeliveryManCommand(DeliveryManManager deliveryManManager) {
        this.deliveryManManager = deliveryManManager;
    }

    @Command(name = "deliveryman", description = "Open the Delivery Man menu", playersOnly = true)
    public void onCommand(CommandProvider command) {
        new DeliveryManMenu(command.getPlayer(), deliveryManManager).open();
    }
}