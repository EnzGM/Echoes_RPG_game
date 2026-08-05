package physics;

import com.badlogic.gdx.physics.box2d.*;
import events.EventBus;
import events.EventType;


public class CollisionListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Object dataA = fixtureA.getBody().getUserData();
        Object dataB = fixtureB.getBody().getUserData();

        if (isAstronauta(dataA) && isObstacle(dataB) || isAstronauta(dataB) && isObstacle(dataA)) {
            EventBus.getInstance().publish(EventType.PLAYER_COLLIDED_OBSTACLE);
        }

        if (isAstronauta(dataA) && isWall(dataB) || isAstronauta(dataB) && isWall(dataA)) {
            EventBus.getInstance().publish(EventType.PLAYER_COLLIDED_WALL);
        }

        if (isAstronauta(dataA) && isPortal(dataB) || isAstronauta(dataB) && isPortal(dataA)) {
            EventBus.getInstance().publish(EventType.PORTAL_ENTERED);
        }
    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    // Helpers

    private boolean isAstronauta(Object data) {
        return data != null && data.toString().equalsIgnoreCase("ASTROANUTA");
    }

    private boolean isObstacle(Object data) {
        return data != null && data.toString().equalsIgnoreCase("OBSTACLE");
    }

    private boolean isWall(Object data) {
        return data != null && data.toString().equalsIgnoreCase("WALL");
    }
    private boolean isPortal(Object data) {
        return data != null && data.toString().equalsIgnoreCase("PORTAL");
    }
}


