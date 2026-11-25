package dungeon.event;

import dungeon.domain.Player;

import java.util.List;
import java.util.Random;

/**
 * A quiet place where the player can recover some HP.
 */
public class RestingPlaceEvent extends GameEvent {

    public static final int REST_HEAL = 10;

    public RestingPlaceEvent(Random random) {
        super(random);
    }

    @Override
    public String getTitle() {
        return "YOU FIND A QUIET RESTING PLACE.";
    }

    @Override
    public String getArt() {
        return """
                         z
                       z
                     ( )
                    (   )
                   __)_(__
                  /_______\\
                """;
    }

    @Override
    public List<String> getOptions(Player player) {
        return List.of("Rest (+" + REST_HEAL + " HP)", "Move on");
    }

    @Override
    protected EventOutcome handleChoice(int choice, Player player) {
        if (choice == 2) {
            return EventOutcome.of("You decide not to waste time and move on.");
        }
        int restored = player.heal(REST_HEAL);
        return EventOutcome.of("You rest by the fire. HP +" + restored + ".");
    }
}
