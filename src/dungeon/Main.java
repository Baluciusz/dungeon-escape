package dungeon;

import dungeon.controller.GameController;
import dungeon.factory.AbandonedCampEventCreator;
import dungeon.factory.EnemyEncounterEventCreator;
import dungeon.factory.EventSelector;
import dungeon.factory.ExplorationEventCreator;
import dungeon.factory.GameEventCreator;
import dungeon.factory.RestingPlaceEventCreator;
import dungeon.factory.TrapEventCreator;
import dungeon.factory.TreasureChestEventCreator;
import dungeon.ui.ConsoleUI;
import dungeon.ui.InputClosedException;

import java.util.List;
import java.util.Random;

/**
 * Entry point of the Dungeon Escape game.
 * <p>
 * This is the only place that knows every concrete event creator.
 * It wires the objects together and starts the game.
 */
public final class Main {

    private Main() {
        // Utility class: no instances.
    }

    public static void main(String[] args) {
        Random random = new Random();

        List<GameEventCreator> creators = List.of(
                new ExplorationEventCreator(),
                new EnemyEncounterEventCreator(),
                new TrapEventCreator(),
                new AbandonedCampEventCreator(),
                new TreasureChestEventCreator(),
                new RestingPlaceEventCreator());

        EventSelector eventSelector = new EventSelector(creators, random);
        ConsoleUI ui = new ConsoleUI(System.in, System.out);
        GameController controller = new GameController(ui, eventSelector, random);

        try {
            controller.run();
        } catch (InputClosedException e) {
            System.out.println();
            System.out.println("Input closed. Goodbye!");
        }
    }
}
