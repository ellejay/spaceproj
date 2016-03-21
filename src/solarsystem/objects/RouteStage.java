package solarsystem.objects;

/**
 * Object class for storing information about stage on the journey route.
 * @author Laura McGhie
 */
public class RouteStage {

    private String body;
    private double apoapsis;
    private double periapsis;

    /**
     * Default constructor for a stage. Takes a body, and an apoapsis and periapsis to define the orbit around it.
     * @param name - The name of the body we are currently orbiting
     * @param apoapsis - the furthest distance from the body on the orbital path
     * @param periapsis - the closest distance to the body on the orbital path
     */
    public RouteStage(String name, double apoapsis, double periapsis) {
        this.body = name;
        this.apoapsis = apoapsis;
        this.periapsis = periapsis;
    }

    /**
     * Return the name of the body we are orbiting
     * @return String body name
     */
    public String getBody() {
        return body;
    }

    /**
     * Return the furthest distance from the body at the focus on the orbital path
     * @return double apoapsis
     */
    public double getApoapsis() {
        return apoapsis;
    }

    /**
     * Return the closest distance to the body at the focus on the orbital path
     * @return double periapsis
     */
    public double getPeriapsis() {
        return periapsis;
    }

    /**
     * Return a string with data about this orbit, including the body and orbit parameters
     * @return info string
     */
    public String getInfo() {
        return body + " " + apoapsis + "km / " + periapsis + "km";
    }

    /**
     * Boolean to indicate if this orbit is a landed position on the surface of a planet, or an orbit around it
     * @return boolean landed
     */
    public boolean isLanded() {
        return apoapsis == 0 && periapsis == 0;
    }
}
