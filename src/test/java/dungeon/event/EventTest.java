package dungeon.event;

import dungeon.FixedRandom;
import dungeon.domain.Bread;
import dungeon.domain.Enemy;
import dungeon.domain.EnemyType;
import dungeon.domain.HealthPotion;
import dungeon.domain.Player;
import dungeon.domain.Weapon;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests event rules. FixedRandom decides each roll:
 * a low number (0) succeeds, a high number (99) fails.
 */
class EventTest {

    private static final int SUCCESS = 0;
    private static final int FAIL = 99;

    @Test
    void successfulDisarmCausesNoDamage() {
        Player player = new Player(100, 100);

        new TrapEvent(new FixedRandom(SUCCESS)).resolve(1, player);

        assertEquals(100, player.getHp());
    }

    @Test
    void failedDisarmCanHitADefectiveTrap() {
        Player player = new Player(100, 100);

        new TrapEvent(new FixedRandom(FAIL, SUCCESS)).resolve(1, player);

        assertEquals(100, player.getHp(), "defective trap: no damage");
    }

    @Test
    void failedDisarmOfAWorkingTrapCausesDamage() {
        Player player = new Player(100, 100);

        new TrapEvent(new FixedRandom(FAIL, FAIL)).resolve(1, player);

        assertEquals(100 - TrapEvent.DISARM_FAIL_DAMAGE, player.getHp());
    }

    @Test
    void wonFightIsReportedAsDefeatedEnemy() {
        Player player = new Player(100, 100);
        EnemyEncounterEvent fight = new EnemyEncounterEvent(new Enemy(EnemyType.OGRE, 1),
                new FixedRandom(SUCCESS, FAIL)); // win, no potion drop

        EventOutcome outcome = fight.resolve(1, player);

        assertTrue(outcome.isEnemyDefeated());
        assertEquals(100, player.getHp());
    }

    @Test
    void lostFightCostsEnemyDamage() {
        Player player = new Player(100, 100);
        Enemy orc = new Enemy(EnemyType.ORC, 3);

        EventOutcome outcome = new EnemyEncounterEvent(orc, new FixedRandom(FAIL)).resolve(1, player);

        assertFalse(outcome.isEnemyDefeated());
        assertEquals(100 - orc.getDamage(), player.getHp());
    }

    @Test
    void chestCanContainARustySword() {
        Player player = new Player(100, 100);

        new TreasureChestEvent(new FixedRandom(80)).resolve(1, player); // 75-91 = Rusty Sword

        assertEquals(1, player.getInventory().countOf(Weapon.RUSTY_SWORD));
    }

    @Test
    void chestGivesBreadInsteadOfAnAlreadyOwnedSword() {
        Player player = new Player(100, 100);
        player.addItem(Weapon.steelSword());

        new TreasureChestEvent(new FixedRandom(95)).resolve(1, player); // 92-99 = Steel Sword

        assertEquals(1, player.getInventory().countOf(Weapon.STEEL_SWORD));
        assertEquals(1, player.getInventory().countOf(Bread.NAME));
    }

    @Test
    void occupiedCampLeadsToAFollowUpEnemyEncounter() {
        Player player = new Player(100, 100);

        EventOutcome outcome = new AbandonedCampEvent(2, new FixedRandom(SUCCESS, 0)).resolve(1, player);

        assertTrue(outcome.hasFollowUp());
        assertTrue(outcome.getFollowUpEvent() instanceof EnemyEncounterEvent);
    }

    @Test
    void restingHealsUpToTheMaximum() {
        Player player = new Player(95, 100);

        new RestingPlaceEvent(new FixedRandom()).resolve(1, player);

        assertEquals(Player.MAX_HP, player.getHp());
    }

    @Test
    void invalidChoiceIsRejected() {
        GameEvent chest = new TreasureChestEvent(new FixedRandom());
        Player player = new Player(100, 100);

        assertThrows(IllegalArgumentException.class, () -> chest.resolve(3, player));
        assertThrows(IllegalArgumentException.class, () -> chest.resolve(0, player));
        assertFalse(player.getInventory().countOf(HealthPotion.NAME) > 0);
    }
}
