package Model.units;
import Model.gamefield.Unit;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public abstract class UnitContractTest<T extends Unit> {
    protected abstract T createUnit();

    @Test void activate_setsActiveTrue() {
        T u = createUnit(); u.activate(); assertTrue(u.isActive());
    }
    @Test void activate_triggersListener() {
        T u = createUnit(); boolean[] c={false};
        u.addUnitListener(x -> c[0]=true); u.activate(); assertTrue(c[0]);
    }
    @Test void activate_twice_listenerCalledOnce() {
        T u = createUnit(); int[] c={0};
        u.addUnitListener(x -> c[0]++); u.activate(); u.activate();
        assertEquals(1, c[0]);
    }
    @Test void deactivate_setsActiveFalse() {
        T u = createUnit(); u.activate(); u.deactivate(); assertFalse(u.isActive());
    }
    @Test void deactivate_triggersListener() {
        T u = createUnit(); u.activate(); boolean[] c={false};
        u.addUnitListener(x -> c[0]=true); u.deactivate(); assertTrue(c[0]);
    }
    @Test void deactivate_twice_listenerCalledOnce() {
        T u = createUnit(); u.activate(); int[] c={0};
        u.addUnitListener(x -> c[0]++); u.deactivate(); u.deactivate();
        assertEquals(1, c[0]);
    }
}