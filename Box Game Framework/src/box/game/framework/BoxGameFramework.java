//Author: Duncan

package box.game.framework;

public class BoxGameFramework {
    
    //Used when comparing equality among floats
    public static final float UNIVERSAL_EPSILON = 0.000000000000000001f;
    
    public static void main(String[] args) {
        Box a = new Box(4, 2, 230, new Coordinates(50, 50));
        a.printCornersForTesting();
        a.printCorners();
    }
}
