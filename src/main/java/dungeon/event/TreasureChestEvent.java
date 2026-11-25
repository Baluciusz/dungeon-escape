package dungeon.event;

import dungeon.domain.HealthPotion;
import dungeon.domain.Player;

import java.util.List;
import java.util.Random;

/**
 * A treasure chest that may contain a Health Potion.
 */
public class TreasureChestEvent extends GameEvent {

    public static final int POTION_CHANCE = 50;

    public TreasureChestEvent(Random random) {
        super(random);
    }

    @Override
    public String getTitle() {
        return "YOU FIND A TREASURE CHEST.";
    }

    @Override
    public String getArt() {
        return """
                     ______________
                    /\\  ________  /\\
                   /__\\/________\\/__\\
                   |   |   []   |   |
                   |___|________|___|
                """;
    }

    @Override
    public List<String> getOptions(Player player) {
        return List.of("Open the chest", "Leave it closed");
    }

    @Override
    protected EventOutcome handleChoice(int choice, Player player) {
        if (choice == 2) {
            return EventOutcome.of("You leave the chest untouched.");
        }
        if (chance(POTION_CHANCE)) {
            player.addItem(new HealthPotion());
            return EventOutcome.of("Inside you find a Health Potion!");
        }
        return EventOutcome.of("The chest is empty. Someone got here first.");
    }
}
