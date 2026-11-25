package dungeon.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnemyTest {

    @Test
    void enemiesGetStrongerOnHigherFloors() {
        Enemy firstFloor = new Enemy(EnemyType.ORC, 1);
        Enemy fifthFloor = new Enemy(EnemyType.ORC, 5);

        assertEquals(EnemyType.ORC.getBaseWinChance(), firstFloor.getWinChance());
        assertEquals(EnemyType.ORC.getBaseWinChance() - 4 * Enemy.WIN_CHANCE_DROP_PER_FLOOR,
                fifthFloor.getWinChance());
        assertEquals(EnemyType.ORC.getBaseDamage() + 4 * Enemy.DAMAGE_INCREASE_PER_FLOOR,
                fifthFloor.getDamage());
    }

    @Test
    void weaponBonusIsAddedButCappedAt95Percent() {
        Enemy ogre = new Enemy(EnemyType.OGRE, 1); // 65% base

        assertEquals(75, ogre.getWinChanceWith(Weapon.RUSTY_SWORD_BONUS));
        assertEquals(85, ogre.getWinChanceWith(Weapon.STEEL_SWORD_BONUS));
        assertEquals(Enemy.MAX_WIN_CHANCE, ogre.getWinChanceWith(100));
    }

    @Test
    void failedEscapeCostsHalfTheFightDamage() {
        Enemy goblin = new Enemy(EnemyType.GOBLIN, 2);

        assertEquals(goblin.getDamage() / 2, goblin.getEscapeFailDamage());
    }

    @Test
    void floorMustBePositive() {
        assertThrows(IllegalArgumentException.class, () -> new Enemy(EnemyType.OGRE, 0));
    }
}
