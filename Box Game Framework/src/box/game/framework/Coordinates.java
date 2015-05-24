package box.game.framework;

public class Coordinates {
    float x;
    float y;

    public Coordinates(float xCoord, float yCoord) {
        x = xCoord;
        y = yCoord;
    }
    
    public Coordinates() {
        //Empty const
    }
    
    public boolean doCoordinatesMatch(Coordinates comparingPoint){
        if(comparingPoint == null || this == null){
            return false;
        }
        if(
            Math.abs(this.getX()-comparingPoint.getX()) < BoxGameFramework.UNIVERSAL_EPSILON &&
            Math.abs(this.getY()-comparingPoint.getY()) < BoxGameFramework.UNIVERSAL_EPSILON
        ){
            return true;
	}
		
	return false;
        
    }
    
    public float findAngleToPoint(Coordinates c){
        
        if(this.doCoordinatesMatch(c)){
            return 0;
        }
        
        //If the subtracted x is negative, c.getX() is bigger than this.getX(), or if they're equal
        int xDirectionalMult;
        int yDirectionalMult;
        
        //If this x is lesser than c.getX() (ie the difference is a negative number)
        if( Math.abs(this.getX()-c.getX()) != (this.getX()-c.getX()) ){
            xDirectionalMult = 1;
        }
        else if( Math.abs(this.getX()-c.getX()) == (this.getX()-c.getX())){
            xDirectionalMult = -1;
        }
        else{
            System.out.println("error inside Cooridnates find angle");
            xDirectionalMult = 2;
        }
        
        //If this y is lesser than c.gety() (ie the difference is a negative number)
        if( Math.abs(this.getY()-c.getY()) != (this.getY()-c.getY()) ){
            yDirectionalMult = 1;
        }
        else if( Math.abs(this.getY()-c.getY()) == (this.getY()-c.getY()) ){
            yDirectionalMult = -1;
        }
        else{
            System.out.println("error inside Coordinates find angle");
            yDirectionalMult = 2;
        }

        //Depends on which quadrant that c is in related to this
        float addedAngle;
        float angleToBox;
        
        //Angle is drawn from the last axis with basic trig to find a and b -> C:\Users\Duncan\Desktop\Box Game Framework\Comment Images\Coordinates
        //Then add the necessary amount depending on the quadrant
        if(xDirectionalMult == 1 && yDirectionalMult == 1){
            addedAngle = 0;
            angleToBox = (float) Math.toDegrees(Math.acos(Math.abs(this.getX()-c.getX()) / this.findDistanceToPoint(c)));
        }
        else if(xDirectionalMult == -1 && yDirectionalMult == 1){
            addedAngle = 90;
            angleToBox = (float) Math.toDegrees(Math.asin(Math.abs(this.getX()-c.getX()) / this.findDistanceToPoint(c)));
        }
        else if(xDirectionalMult == -1 && yDirectionalMult == -1){
            addedAngle = 180;
            angleToBox = (float) Math.toDegrees(Math.acos(Math.abs(this.getX()-c.getX()) / this.findDistanceToPoint(c)));
        }
        else if(xDirectionalMult == 1 && yDirectionalMult == -1){
            addedAngle = 270;
            angleToBox = (float) Math.toDegrees(Math.asin(Math.abs(this.getX()-c.getX()) / this.findDistanceToPoint(c)));
        }
        else{
            addedAngle = -1;
            System.out.println("inside coordinates, findangle to point, something gone wrong");
            return -1;
        }
        
       return angleToBox+addedAngle;
    }
    
    public float findDistanceToPoint(Coordinates c){
        //Just stock distance formula here, ya know a squared + b squared, = c squared, basic trig stuff
        return ((float) Math.sqrt( Math.pow(Math.abs(this.getX()-c.getX()), 2) + Math.pow(Math.abs(this.getY()-c.getY()), 2) ));
    }
    
    public void setX(float xcoord) {
        x = xcoord;
    }

    public void setY(float ycoord) {
        y = ycoord;
    }
    
    public void setCoordinates(float xcoord, float ycoord) {
        x = xcoord;
        y = ycoord;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
    
    //Self explanatory; may remove these two later
    public void printCoordinates(){
        System.out.println("X: " + x + " - Y: " + y);
    }
    
    public void printCoordinatesForTesting(){
        System.out.println(x + ", " + y);
    }
}
