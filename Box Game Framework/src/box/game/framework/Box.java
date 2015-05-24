package box.game.framework;

import java.util.*;
import java.lang.*;
import java.io.*;

public class Box {

    float width;
    float height;
    float boxAngle;
    Coordinates SW;
    Coordinates SE;
    Coordinates NW;
    Coordinates NE;
    Coordinates centerPoint;
    boolean isPerfectBox;

    public Box(float boxHeight, float boxWidth, float angle, Coordinates centerPnt) {
        height = boxHeight;
        width = boxWidth;
        centerPoint = centerPnt;

        //Make the angle safe for use
        if (angle > 360.0f) {
            boxAngle = Box.convertOverflowingAngle(angle);
        } else if (angle < 0.0f) {
            boxAngle = Box.convertNegativeAngle(angle);
        } else {
            boxAngle = angle;
        }

        //C:\Users\Duncan\Desktop\Box Game Framework\Comment Images\Box\Box const - 1.png
        float radialAngle = (float) (Math.atan((0.5f * width) / (0.5f * height)) * (180.0f / Math.PI));
        float radialAnglePos = Box.convertOverflowingAngle(boxAngle + radialAngle);
        float radialAngleNegative = Box.convertNegativeAngle(boxAngle - radialAngle);
        float radialDist = (0.5f * width) / (float) (Math.sin(Math.toRadians(radialAngle)));

        Coordinates genericPointOne = Box.findEndPointOfLine(radialDist, radialAnglePos, centerPoint);
        Coordinates genericPointTwo = Box.findEndPointOfLine(radialDist, Box.convertOverflowingAngle(radialAnglePos + 180), centerPoint);
        Coordinates genericPointThree = Box.findEndPointOfLine(radialDist, radialAngleNegative, centerPoint);
        Coordinates genericPointFour = Box.findEndPointOfLine(radialDist, Box.convertNegativeAngle(radialAngleNegative - 180), centerPoint);

        if ((boxAngle > 0 && boxAngle < 90) || (boxAngle > 270 && boxAngle < 360)) {
            NW = genericPointFour;
            SW = genericPointTwo;
            NE = genericPointOne;
            SE = genericPointThree;
        } 
        else if (((boxAngle > 90) && (boxAngle != 180) && (boxAngle < 270))) {
            NW = genericPointThree;
            SW = genericPointOne;
            NE = genericPointTwo;
            SE = genericPointFour;
        } 
        else {
            isPerfectBox = true;
            if (
                (Math.abs(boxAngle - 0) < BoxGameFramework.UNIVERSAL_EPSILON)
                || (Math.abs(boxAngle - 180) < BoxGameFramework.UNIVERSAL_EPSILON)
                || (Math.abs(boxAngle - 360) < BoxGameFramework.UNIVERSAL_EPSILON)
            ){
                NW = new Coordinates(centerPoint.getX() - 0.5f * height, centerPoint.getY() + 0.5f * width);
                SW = new Coordinates(centerPoint.getX() - 0.5f * height, centerPoint.getY() - 0.5f * width);
                NE = new Coordinates(centerPoint.getX() + 0.5f * height, centerPoint.getY() + 0.5f * width);
                SE = new Coordinates(centerPoint.getX() + 0.5f * height, centerPoint.getY() - 0.5f * width);
            } 
            else if (
                (Math.abs(boxAngle - 90) < BoxGameFramework.UNIVERSAL_EPSILON)
                || (Math.abs(boxAngle - 270) < BoxGameFramework.UNIVERSAL_EPSILON)
            ){
                NW = new Coordinates(centerPoint.getX() - 0.5f * width, centerPoint.getY() + 0.5f * height);
                SW = new Coordinates(centerPoint.getX() - 0.5f * width, centerPoint.getY() - 0.5f * height);
                NE = new Coordinates(centerPoint.getX() + 0.5f * width, centerPoint.getY() + 0.5f * height);
                SE = new Coordinates(centerPoint.getX() + 0.5f * width, centerPoint.getY() - 0.5f * height);
            } 
            else{
                //Throw exception here later
                System.out.println("Inside box class, inside Box constructor, someting gone wrong with the angle, returning..");
                return;
            }
        }
    }

    //Change this one to just accept four corners, then find the greatest/least x/ys, etc.?
    public Box(Coordinates NWpoint, Coordinates SWpoint, Coordinates NEpoint, Coordinates SEpoint) {

        //If the points given do not form a perfect box together
        if (
            !(Math.abs(NWpoint.getX() - SWpoint.getX()) < BoxGameFramework.UNIVERSAL_EPSILON)
            || !(Math.abs(NEpoint.getX() - SEpoint.getX()) < BoxGameFramework.UNIVERSAL_EPSILON)
            || !(Math.abs(NWpoint.getY() - NEpoint.getY()) < BoxGameFramework.UNIVERSAL_EPSILON)
            || !(Math.abs(SWpoint.getY() - SEpoint.getY()) < BoxGameFramework.UNIVERSAL_EPSILON)
        ){
            System.out.println("Inside box, inside alternate, perf box const with only corners, points given are not perfect, returning..");
            return;
        }

        isPerfectBox = true;
        NW = NWpoint;
        SW = SWpoint;
        NE = NEpoint;
        SE = SEpoint;
        boxAngle = 0;

    }
    
    public static Coordinates findEndPointOfLine(float lineLength, float lineAngle, Coordinates startPoint) {
        //Make the angle safe if it's needed
        if (lineAngle > 360) {
            lineAngle = Box.convertOverflowingAngle(lineAngle);
        }
        if (lineAngle < 0) {
            lineAngle = Box.convertNegativeAngle(lineAngle);
        }

        //Find the quadrant to determine the directional multipliers for x and y offset when they are added to startPoints' x and y's
        int angleQuadrant = (int) Math.abs((lineAngle - (lineAngle % 90)) / 90);
        //Basic trig here, find the dist of the legs of the triangle formed between the start and end point, tri drawn along 0 & 180's axis
        float xOffset = (float) Math.abs(Math.cos(lineAngle * (Math.PI / 180)) * lineLength);
        float yOffset = (float) Math.abs(Math.sin(lineAngle * (Math.PI / 180)) * lineLength);

        //Find the direction the x and y offsets need to be traveling, mutliply
        int angleMultX;
        int angleMultY;
        if (angleQuadrant == 0) {
            angleMultX = 1;
            angleMultY = 1;
        } 
        else if (angleQuadrant == 1) {
            angleMultX = -1;
            angleMultY = 1;
        } 
        else if (angleQuadrant == 2) {
            angleMultX = -1;
            angleMultY = -1;
        } 
        else if (angleQuadrant == 3) {
            angleMultX = 1;
            angleMultY = -1;
        }
        else{
            //Throw exception later
            System.out.println("Inside Box class, inside findEndPointOfLine, angleQuadrant is not 0-3, returning null...");
            return null;
        }

        //Just add on the x and y offsets to startPoint's x's and y's in the needed direction, endPoint found and created
        return new Coordinates(startPoint.getX() + xOffset * angleMultX, startPoint.getY() + yOffset * angleMultY);
    }

    public void rotateBoxByDeg(float degrees) {

    }
    
    public void translateBox(float xDist, float yDist) {
        //Pretty self-explanatory, add the xDist and yDist to all Coordinate values of this
        SW.setCoordinates(SW.getX()+xDist, SW.getY()+yDist);
        NW.setCoordinates(NW.getX()+xDist, NW.getY()+yDist);
        SE.setCoordinates(SE.getX()+xDist, SE.getX()+yDist);
        NE.setCoordinates(NE.getX()+xDist, NE.getY()+yDist);
        centerPoint.setCoordinates(centerPoint.getX()+xDist, centerPoint.getY()+yDist);
    }

    public static float convertOverflowingAngle(float angle) {
        if (angle > 360.0f) {
            angle = angle % 360.0f;
        } 
        return angle;
    }

    public static float convertNegativeAngle(float a) {
        float angle = a;
        //Just in case it's overflowing, check and convert it
        if (Math.abs(angle) * 1 != angle) {
            if (Math.abs(angle) > 360) {
                angle = Box.convertOverflowingAngle(Math.abs(angle));
            }
            //Then just subtract what's left over
            angle = 360 - Math.abs(angle);
        } 
        return angle;
    }

    public static Coordinates linesIntersectAtPoint(Coordinates startPoint, Coordinates endPoint, Coordinates startPointTwo, Coordinates endPointTwo) {
        
        //Opportunity to just set startX, startY, etc. to variables and use those instead, for performance. However, with the way that Java
        //optimizes for get/set methods should make it okay. Also my laziness may have something
        //to do with it as well.
        float intersectionX = 
              ((startPoint.getX() * endPoint.getY() - endPoint.getX() * startPoint.getY()) * (startPointTwo.getX() - endPointTwo.getX()) - ((startPoint.getX() - endPoint.getX()) * (startPointTwo.getX() * endPointTwo.getY() - startPointTwo.getY() * endPointTwo.getX())))
            / ((startPoint.getX() - endPoint.getX()) * (startPointTwo.getY() - endPointTwo.getY()) - (startPoint.getY() - endPoint.getY()) * (startPointTwo.getX() - endPointTwo.getX()));

        float intersectionY = 
              ((startPoint.getX() * endPoint.getY() - endPoint.getX() * startPoint.getY()) * (startPointTwo.getY() - endPointTwo.getY()) - ((startPoint.getY() - endPoint.getY()) * (startPointTwo.getX() * endPointTwo.getY() - startPointTwo.getY() * endPointTwo.getX())))
            / ((startPoint.getX() - endPoint.getX()) * (startPointTwo.getY() - endPointTwo.getY()) - (startPoint.getY() - endPoint.getY()) * (startPointTwo.getX() - endPointTwo.getX()));

        float greatestX;
        float leastX;
        float greatestY;
        float leastY;
        float greatestXTwo;
        float leastXTwo;
        float greatestYTwo;
        float leastYTwo;

        //Find the greatest/least x's of the line's two points
        if (startPoint.getX() > endPoint.getX()) {
            leastX = endPoint.getX();
            greatestX = startPoint.getX();
        } 
        else if (startPoint.getX() < endPoint.getX()) {
            leastX = startPoint.getX();
            greatestX = endPoint.getX();
        } 
        else {
            //Replace with exception throwing later; not relevant right now and there SHOULD be no way to access this
            System.out.println("Error inside linesIntersectAt() in Box class, returning null...");
            return null;
        }

        //Find the greatest/least y's of the line's two points
        if (startPoint.getY() > endPoint.getY()) {
            leastY = endPoint.getY();
            greatestY = startPoint.getY();
        } 
        else if (startPoint.getY() < endPoint.getY()) {
            leastY = startPoint.getY();
            greatestY = endPoint.getY();
        }
        else {
            System.out.println("Error inside linesIntersectAt() in Box class, returning null...");
            return null;
        }

        //Do the same for the second line's points
        if (startPointTwo.getX() > endPointTwo.getX()) {
            leastXTwo = endPoint.getX();
            greatestXTwo = startPoint.getX();
        }
        else if (startPointTwo.getX() < endPointTwo.getX()) {
            leastXTwo = startPoint.getX();
            greatestXTwo = endPoint.getX();
        }
        else {
            System.out.println("Error inside linesIntersectAt() in Box class, returning null...");
            return null;
        }

        if (startPointTwo.getY() > endPointTwo.getY()) {
            leastYTwo = endPoint.getY();
            greatestYTwo = startPoint.getY();
        } 
        else if (startPointTwo.getY() < endPointTwo.getY()) {
            leastYTwo = startPoint.getY();
            greatestYTwo = endPoint.getY();
        } 
        else {
            System.out.println("Error inside linesIntersectAt() in Box class, returning null...");
            return null;
        }

        //In case the intersection point isn't between the start and ending points of both lines, then see if the lines are
        if(
            !(
               ((leastX <= intersectionX && intersectionX <= greatestX) && (leastY <= intersectionY && intersectionY <= greatestY))
            && ((leastXTwo <= intersectionX && intersectionX <= greatestXTwo) && (leastYTwo <= intersectionY && intersectionY <= greatestYTwo))
            )
        ){
            Coordinates c = Box.perfLineIntersectsPerfLineAt(startPoint, endPoint, startPointTwo, endPointTwo);
            if (c != null) {
                return c;
            }

            return null;
        }

        return new Coordinates(intersectionX, intersectionY);
    }

    public boolean hasCollidedWithBox(Box comparingBox) {
        /*
         First check is for line interesctions; lines drawn between each of the four corner points of the boxes respectively; all paired
         segments between this' corners are checked for intersection with each of comparing's paired segments between its corners; this
         will generally (as long as most boxes are similarly sized, which they probably will be in-game most of the time) return true if
         they are colliding immediately, without having to go through the most intensive anyPointsAreInBox() checks
         */
        Coordinates[] lineSegmentsOfCornersOfThis = {this.getSW(), this.getSE(), this.getNW(), this.getSW(), this.getSE(), this.getNE(), this.getNW(), this.getNE()};
        Coordinates[] lineSegmentsOfCornersOfComparing = {comparingBox.getSW(), comparingBox.getSE(), comparingBox.getNW(), comparingBox.getSW(), comparingBox.getSE(), comparingBox.getNE(), comparingBox.getNW(), comparingBox.getNE()};

        for (int i = 0; i < 7; i += 2) {
            for (int j = 0; j < 7; j += 2) {
                if (Box.linesIntersectAtPoint(lineSegmentsOfCornersOfThis[i], lineSegmentsOfCornersOfThis[i + 1], lineSegmentsOfCornersOfComparing[j], lineSegmentsOfCornersOfComparing[j + 1]) != null) {
                    return true;
                }
            }
        }

        //Second check is to see if any of the points of either box is in the other box respectively, in case one box is small/large
        //enough to hide within/encase the other
        if (this.anyPointsAreInBox(comparingBox.getCorners()) || comparingBox.anyPointsAreInBox(this.getCorners())) {
            return true;
        }

        return false;
    }
    
    // ******* START ANY/IS METHODS ******** //
    public boolean anyPointsAreInBox(Coordinates[] testPoints) {
        //Box is perfect, no need for the following
        if (isPerfectBox) {
            if (this.anyPointsAreInPerfectBox(testPoints)) {
                return true;
            } 
            else {
                return false;
            }
        }

        float adjustedAngle;
        if (boxAngle > 180) {
            adjustedAngle = boxAngle - 180;
        } 
        else {
            adjustedAngle = boxAngle;
        }

        Box perfectBox = this.findPerfectBoxAroundBox();

        //Corresponding points mean the second, intermediary point needed to form a right triangle between two corner points
        Coordinates correspondingPointSWNW;
        Coordinates correspondingPointSWSE;
        Coordinates correspondingPointNWNE;
        Coordinates correspondingPointSENE;
        if (adjustedAngle < 90) {
            correspondingPointSWNW = perfectBox.getSW();
            correspondingPointSWSE = perfectBox.getSE();
            correspondingPointNWNE = perfectBox.getNW();
            correspondingPointSENE = perfectBox.getNE();
        } 
        else {
            correspondingPointSWNW = perfectBox.getNW();
            correspondingPointSWSE = perfectBox.getSW();
            correspondingPointNWNE = perfectBox.getNE();
            correspondingPointSENE = perfectBox.getSE();
        }

        Coordinates[] cornerPairsArrOne = {SW, SE, NW, SW};
        Coordinates[] correspondingPointsArr = {correspondingPointSWNW, correspondingPointSENE, correspondingPointNWNE, correspondingPointSWSE};
        Coordinates[] cornerPairsArrTwo = {NW, NE, NE, SE};

        //Remove all points that are outside the perfectBox drawn around this, these are the possible points that could be in this
        Coordinates[] currentUnremovedPoints = perfectBox.removePointsOutsidePerfectBox(testPoints);
        
        //Remove all the points in the given right triangles formed around this
        for (int i = 0; i < 4; i++) {
            currentUnremovedPoints = Box.removeAllPointsInRightTriangle(cornerPairsArrOne[i], correspondingPointsArr[i], cornerPairsArrTwo[i], currentUnremovedPoints);
            
            //If all the points have been removed, and this loop is still executing, then there can be no points in this
            if (currentUnremovedPoints.length == 0) {
                return false;
            }
        }
        
        //If there are still some unremoved points, and we have established that: they are all in the perfect box around this, and not
        //in any of the right triangles formed around this, then at least one point must have been within this
        return true;
    }
    
    public boolean pointIsInPerfectBox(Coordinates testPoint) {
        if (!isPerfectBox) {
            System.out.println("Inside box class, inside pointIsInPerfectBox, box calling is not perfect, returning false");
            return false;
        }

        if (
            testPoint.getX() > this.getNW().getX()
            && testPoint.getX() < this.getNE().getX()
            && testPoint.getY() > this.getNW().getY()
            && testPoint.getY() > this.getSW().getY()
        ) {
            return true;
        } 
        return false;
    }

    public boolean anyPointsAreInPerfectBox(Coordinates[] testPoints) {
        if (!isPerfectBox) {
            System.out.println("Inside box class, inside anyPointsAreInPerfectBox(testPoints), box calling is not perfect, returning false");
            return false;
        }

        for (int i = 0; i < testPoints.length; i++) {
            if (
                testPoints[i].getX() > NW.getX()
                && testPoints[i].getX() < NE.getX()
                && testPoints[i].getY() < NW.getY()
                && testPoints[i].getY() > SW.getY()
            ) {
                return true;
            }
        }
        return false;
    }

    public static boolean anyPointsAreInPerfectBox(Coordinates[] testPoints, Coordinates cornerPointOne, Coordinates cornerPointTwo) {
        float greatestX;
        float greatestY;
        float leastX;
        float leastY;
        
        //Reason I'm not sticking to my usual if/else if/else structure is because this is going to be called A LOT by the
        //removePointsInRightTriangle method, and form takes a backseat to function in this sort of case; same reason I'm not including
        //a perfect box check at the beginning
        if (cornerPointOne.getX() >= cornerPointTwo.getX()) {
            greatestX = cornerPointOne.getX();
            leastX = cornerPointTwo.getX();
        } 
        else {
            greatestX = cornerPointTwo.getX();
            leastX = cornerPointOne.getX();
        }

        if (cornerPointOne.getY() >= cornerPointTwo.getY()) {
            greatestY = cornerPointOne.getY();
            leastY = cornerPointTwo.getY();
        } 
        else {
            greatestY = cornerPointTwo.getY();
            leastY = cornerPointOne.getY();
        }

        //Also not using an enhanced for loop here for the same performance-related reasons, as its skin over an Iterator
        //means a performance loss, since we're only iterating over a basic array of primtive floats instead of a proper collection,
        //at least that's what the more smart people online have collectively informed me
        for (int i = 0; i < testPoints.length; i++) {
            if (
            testPoints[i].getX() >= leastX
            && testPoints[i].getX() <= greatestX
            && testPoints[i].getY() >= leastY
            && testPoints[i].getY() <= greatestY
            ){
                return true;
            }
        }
        return false;
    }
    
    //******* START REMOVE METHODS ****** //
    public Coordinates[] removePointsInPerfectBox(Coordinates[] testPoints) {
        if(!isPerfectBox){
            return null;
        }
        
        //May change this to NW.getX(), etc. later
        float greatestX = this.findGreatestXOfBox();
        float greatestY = this.findGreatestYOfBox();
        float leastX = this.findLeastXOfBox();
        float leastY = this.findLeastYOfBox();
        
        //Loop through all the test points and add those that aren't in this to the list of points outside of this
        ArrayList<Coordinates> pointsOutsideBoxList = new ArrayList<Coordinates>();
        for (Coordinates c : testPoints) {
            if (
                !(c.getX() <= greatestX
                && c.getX() >= leastX
                && c.getY() <= greatestY
                && c.getY() >= leastY)
            ){
                pointsOutsideBoxList.add(c);
            } 
        }

        //Convert the points outside list back into a regular array
        Coordinates[] pointsOutsideBoxArr = new Coordinates[pointsOutsideBoxList.size()];
        for (int i = 0; i < pointsOutsideBoxArr.length; i++) {
            pointsOutsideBoxArr[i] = pointsOutsideBoxList.get(i);
        }

        return pointsOutsideBoxArr;
    }

    public Coordinates[] removePointsOutsidePerfectBox(Coordinates[] testPoints) {
        if(isPerfectBox){
            return null;
        }
        
        float greatestX = this.findGreatestXOfBox();
        float greatestY = this.findGreatestYOfBox();
        float leastX = this.findLeastXOfBox();
        float leastY = this.findLeastYOfBox();
        
        //Run through all the test points and add those 
        ArrayList<Coordinates> pointsInBoxList = new ArrayList<Coordinates>();
        for (Coordinates c : testPoints) {
            if(
                c.getX() <= greatestX
                && c.getX() >= leastX
                && c.getY() <= greatestY
                && c.getY() >= leastY
            ){
                pointsInBoxList.add(c);
            } 
        }

        Coordinates[] pointsInBoxArr = new Coordinates[pointsInBoxList.size()];
        for (int i = 0; i < pointsInBoxArr.length; i++) {
            pointsInBoxArr[i] = pointsInBoxList.get(i);
        }

        return pointsInBoxArr;
    }

    public static Coordinates[] removePointsInPerfectBox(Coordinates[] testPoints, Coordinates cornerPointOne, Coordinates cornerPointTwo) {
        float greatestX;
        float greatestY;
        float leastX;
        float leastY;
        if (cornerPointOne.getX() > cornerPointTwo.getX()) {
            greatestX = cornerPointOne.getX();
            leastX = cornerPointTwo.getX();
        } else {
            greatestX = cornerPointTwo.getX();
            leastX = cornerPointOne.getX();
        }

        if (cornerPointOne.getY() > cornerPointTwo.getY()) {
            greatestY = cornerPointOne.getY();
            leastY = cornerPointTwo.getY();
        } else {
            greatestY = cornerPointTwo.getY();
            leastY = cornerPointOne.getY();
        }

        ArrayList<Coordinates> pointsNotInBoxList = new ArrayList<>();
        for (Coordinates c : testPoints) {
            if (!(c.getX() >= leastX
                    && c.getX() <= greatestX
                    && c.getY() >= leastY
                    && c.getY() <= greatestY)) {
                pointsNotInBoxList.add(c);
            } else {
                //System.out.println("Point removed.");
                //Box.printCoordinates(c);

            }
        }

        //Copy over list of points inside to be returned
        Coordinates[] pointsNotInBoxArr = new Coordinates[pointsNotInBoxList.size()];
        for (int i = 0; i < pointsNotInBoxList.size(); i++) {
            pointsNotInBoxArr[i] = pointsNotInBoxList.get(i);
        }

        return pointsNotInBoxArr;

    }

    public static Coordinates[] removeAllPointsInRightTriangle(Coordinates startPoint, Coordinates ninetyDegreePoint, Coordinates endPoint, Coordinates[] testPoints) {
        //The directional multiplier for all the points that will be based along the hypotenuse of the right triangle
        int angleMultX = startPoint.getX() > endPoint.getX() ? -1 : 1;
        int angleMultY = startPoint.getY() > endPoint.getY() ? -1 : 1;

        //The directional multipliers for all the points that will be based along the legs of the right triangle
        int innerMultX;
        int innerMultY;
        //If 90 deg point is at startPoint's x level and endPoint's y level
        if (   
            Math.abs(startPoint.getX() - ninetyDegreePoint.getX()) < BoxGameFramework.UNIVERSAL_EPSILON
            && Math.abs(endPoint.getY() - ninetyDegreePoint.getY()) < BoxGameFramework.UNIVERSAL_EPSILON
        ){
            //In these cases, innerMult's are set to the multipliers of the quadrant diagnolly adjacent to angleMult's (ex. ang quad = I, inner's quad would be 3)
            if (angleMultX == 1 && angleMultY == 1) {
                innerMultX = -1;
                innerMultY = 1;
            } 
            else if (angleMultX == 1 && angleMultY == -1) {
                innerMultX = -1;
                innerMultY = -1;
            } 
            else if (angleMultX == -1 && angleMultY == -1) {
                innerMultX = 1;
                innerMultY = -1;
            } 
            else if(angleMultX == -1 && angleMultY == 1){
                innerMultX = -1;
                innerMultY = -1;
            }
            else{
                System.out.println("inside removeAllPointsInsideRightTriangle in Box class; multipliers not set, lines nto equal, returning null..");
                return null;
            }
        }
        //Otherwise see if the 90 deg point is at startPoint's y level and endpoint's x level
        else if (
            Math.abs(startPoint.getY() - ninetyDegreePoint.getY()) < BoxGameFramework.UNIVERSAL_EPSILON
            && Math.abs(endPoint.getX() - ninetyDegreePoint.getX()) < BoxGameFramework.UNIVERSAL_EPSILON
        ){
            if (angleMultX == 1 && angleMultY == 1) {
                innerMultX = 1;
                innerMultY = -1;
            } 
            else if (angleMultX == 1 && angleMultY == -1) {
                innerMultX = 1;
                innerMultY = 1;
            } 
            else if (angleMultX == -1 && angleMultY == -1) {
                innerMultX = -1;
                innerMultY = 1;
            }
            else if(angleMultX == -1 && angleMultY == 1){
                innerMultX = 1;
                innerMultY = 1;
            }
            else{
                System.out.println("inside removeAllPointsInsideRightTriangle in Box class; multipliers not set, lines nto equal, returning null..");
                return null; 
            }
        } 
        //If not, something has gone wrong, return (later will be exception thrown)
        else{
            System.out.println("inside removeAllPointsInsideRightTriangle in Box class; multipliers not set, lines nto equal, returning null..");
            return null;
        }

        float xDist = Math.abs(startPoint.getX() - endPoint.getX());
        float yDist = Math.abs(startPoint.getY() - endPoint.getY());
        
        float currentMult = 1;
        int currentBoxNum;
        float currentXDist;
        float currentYDist;
        Coordinates currentHypotenusePoint = new Coordinates();
        Coordinates currentInnerPoint = new Coordinates();
        Coordinates[] currentRemovedPointsArr = testPoints;

        //Start loop for how many times the right triangle is to be split up into smaller perfect boxes and checked
        //This is a lot more intuitive to demonstrate visualy, I think: 
        //  C:\Users\Duncan\Desktop\Box Game Framework\Comment Images\Box\removeAllPointsInRightTriangle - 1.png
        for (int i = 1; i < 5; i++) {
            /*
            currentMult is continually halved; result is an exponential-style decay (ex. 1 -> 0.5 -> .25 -> .125, etc.) to the power specified
            in the for loop declaration.
            
            In this case to 5, which seems ike a decent stopping point for precision vs performance, though may eventually
            paramaterize it depending on the maximum/minimum size of the box (ex. much smaller box may be detected as colliding with a 
            massively larger one because the area not covered by the triangle box is still much larger than that smaller box, IDK
            */
            currentMult /= 2;
            
            //The number of current boxes is equal to the currentMult^i-1, at one less exponent that currentMult is at
            currentBoxNum = (int) (1.0f / (currentMult * 2));
            
            //Scale the x and y Dist down further with currentMult
            currentXDist = xDist * currentMult;
            currentYDist = yDist * currentMult;
            
            //The point along along the hypotenuse of the right triangle, used to form an abstract perfect box representing a small area within the r triangle with currentInnerPoint
            currentHypotenusePoint.setCoordinates(startPoint.getX() + currentXDist * angleMultX, startPoint.getY() + currentYDist * angleMultY);
            //The point along the legs of the right triangle
            currentInnerPoint.setCoordinates(currentHypotenusePoint.getX() + currentXDist * innerMultX, currentHypotenusePoint.getY() + currentYDist * innerMultY);

            //Start the loop
            for (int j = 0; j < currentBoxNum; j++) {
                //Use the perfect box formed by hypo and inner points to remove all test points in it, which covers an area of this right tri
                currentRemovedPointsArr = Box.removePointsInPerfectBox(currentRemovedPointsArr, currentHypotenusePoint, currentInnerPoint);
                if (currentRemovedPointsArr.length == 0) {
                    return currentRemovedPointsArr;
                }
                
                //Move the points down along the hypotenuse/leg of the right triangle
                //Hypo is multiplied by 2 which prevents redudant checking (it would check an area already covered by the previous boxes)
                currentHypotenusePoint.setCoordinates(currentHypotenusePoint.getX() + 2 * currentXDist * angleMultX, currentHypotenusePoint.getY() + 2 * currentYDist * angleMultY);
                currentInnerPoint.setCoordinates(currentHypotenusePoint.getX() + currentXDist * innerMultX, currentHypotenusePoint.getY() + currentYDist * innerMultY);
            }
        }

        return currentRemovedPointsArr;
    }
    
    // ****** START FIND METHODS ****** //    
    public Box findPerfectBoxAroundBox() {
        return new Box(new Coordinates(this.findLeastXOfBox(), this.findGreatestYOfBox()), new Coordinates(this.findLeastXOfBox(), this.findLeastYOfBox()), new Coordinates(this.findGreatestXOfBox(), this.findGreatestYOfBox()), new Coordinates(this.findGreatestXOfBox(), this.findLeastYOfBox()));
    }
    
    public static float findGreatestFloat(float[] testArr) {
        //Later throw an exception?
        if(testArr.length == 0){
            System.out.println("Inside Box class, inside findGreatestFloat, testArr length is 0, returning -1");
            return -1;
        }
        
        //Convert the test array into a list
        ArrayList<Float> testFloatList = new ArrayList<Float>();
        for (float testFloat : testArr) {
            testFloatList.add(testFloat);
        }

        //Just set it so that currentTestFloat will be initialized; it's actually impossible for it to not be, with the empty array catch above
        //but Java doesn't know that :(
        float currentTestFloat = -1;
        float currentTestingListSize = testFloatList.size();
        
        outerLoop:
        for(int i = 0; i < currentTestingListSize; i++){
            //Set currentTestFloat
            currentTestFloat = testFloatList.get(i);
            //Remove it from the list as preparation for comparing it with other elements
            testFloatList.remove(testFloatList.get(i));
            currentTestingListSize--;
            
            //Run through each remaining element on the list and, unless currentTestFLoat is smaller than any other elements in then, return it
            for(float testingAgainst : testFloatList){
                if(testingAgainst > currentTestFloat){
                    i--;
                    continue outerLoop;
                }
            }
            break;
        }
        return currentTestFloat;
    }

    public static float findLeastFloat(float[] testArr) {
        //Later throw an exception?
        if(testArr.length == 0){
            System.out.println("Inside Box class, inside findLeastFloat, testArr length is 0, returning -1");
            return -1;
        }
        
        //Convert the test array into a list
        ArrayList<Float> testFloatList = new ArrayList<Float>();
        for (float testFloat : testArr) {
            testFloatList.add(testFloat);
        }

        //Just set it so that currentTestFloat will be initialized; it's actually impossible for it to not be, with the empty array catch above
        //but Java doesn't know that :(
        float currentTestFloat = -1;
        float currentTestingListSize = testFloatList.size();
        
        outerLoop:
        for(int i = 0; i < currentTestingListSize; i++){
            //Set currentTestFloat
            currentTestFloat = testFloatList.get(i);
            //Remove it from the list as preparation for comparing it with other elements
            testFloatList.remove(testFloatList.get(i));
            currentTestingListSize--;
            
            //Run through each remaining element on the list and, unless currentTestFLoat is greater than any other elements in then, return it
            for(float testingAgainst : testFloatList){
                if(testingAgainst < currentTestFloat){
                    i--;
                    continue outerLoop;
                }
            }
            break;
        }
        return currentTestFloat;
    }
    
    public float findGreatestXOfBox() {
        float[] cornerXs = {SW.getX(), NW.getX(), SE.getX(), NE.getX()};
        return Box.findGreatestFloat(cornerXs);
    }

    public float findLeastXOfBox() {
        float[] cornerXs = {SW.getX(), NW.getX(), SE.getX(), NE.getX()};
        return Box.findLeastFloat(cornerXs);
    }

    public float findGreatestYOfBox() {
        float[] cornerYs = {SW.getY(), NW.getY(), SE.getY(), NE.getY()};
        return Box.findGreatestFloat(cornerYs);
    }

    public float findLeastYOfBox() {
        float[] cornerYs = {SW.getY(), NW.getY(), SE.getY(), NE.getY()};
        return Box.findLeastFloat(cornerYs);
    }
    
    // ****** START GET METHODS ******* //
    public float getWidth() {
        return width;
    }
    
    public float getHeight() {
        return height;
    }

    public float getBoxAngle() {
        return boxAngle;
    }
    
    public Coordinates getCenterPoint() {
        return centerPoint;
    }

    public Coordinates getNW() {
        return NW;
    }

    public Coordinates getSW() {
        return SW;
    }

    public Coordinates getNE() {
        return NE;
    }

    public Coordinates getSE() {
        return SE;
    }

    public Coordinates[] getCorners() {
        Coordinates[] corners = {SW, NW, SE, NE};
        return corners;
    }
    
    public boolean getIsPerfectBox() {
        return isPerfectBox;
    }    
    
    ///////////////// ********* LINE OF GREAT FUCKERY, DO NOT PASS BEYOND HERE BROTHERS ***************** //////////////////////////////
    
    //Even I can't understand what I did here, and I wrote it :( ; just use doesPerfLineIntersectPerfLine(), not even gonna try to
    //go through and cleanup/comment on this monstrosity, just gonna pretend it doesn't exist
    public static Coordinates perfLineIntersectsPerfLineAt(Coordinates perfLineStartOne, Coordinates perfLineEndOne, Coordinates perfLineStartTwo, Coordinates perfLineEndTwo) {
        
        float regularDimLineOne;
        float regularDimLineTwo;
        float leastDimOfLineOne = 0;
        float greatestDimOfLineOne = 0;
        float leastDimOfLineTwo = 0;
        float greatestDimOfLineTwo = 0;

        boolean oneXWise = false;
        boolean twoXWise = false;

        //Find start line's dims
        if (Math.abs(perfLineStartOne.getX() - perfLineEndOne.getX()) < BoxGameFramework.UNIVERSAL_EPSILON) {
            oneXWise = true;
            regularDimLineOne = perfLineStartOne.getX();
            //Find the least and greatest of the varying dimension of the line
            if (perfLineStartOne.getY() >= perfLineEndOne.getY()) {
                leastDimOfLineOne = perfLineEndOne.getY();
                greatestDimOfLineOne = perfLineStartOne.getY();
            } else if (perfLineStartOne.getY() <= perfLineEndOne.getY()) {
                leastDimOfLineOne = perfLineStartOne.getY();
                greatestDimOfLineOne = perfLineEndOne.getY();
            } else {
                //Equal
            }
        } else if (Math.abs(perfLineStartOne.getY() - perfLineEndOne.getY()) < BoxGameFramework.UNIVERSAL_EPSILON) {
            oneXWise = false;
            regularDimLineOne = perfLineStartOne.getY();
            //Find the greatest and least of the varying dimensions of the line
            if (perfLineStartOne.getX() >= perfLineEndOne.getX()) {
                leastDimOfLineOne = perfLineEndOne.getX();
                greatestDimOfLineOne = perfLineStartOne.getX();
            } else if (perfLineStartOne.getX() <= perfLineEndOne.getX()) {
                leastDimOfLineOne = perfLineStartOne.getX();
                greatestDimOfLineOne = perfLineEndOne.getX();
            } else {
                //Equal?
            }
        } else {
            //Line one is not a perfect line, waddafuck
            //perfLineEndOne.printCoordinates();
            //perfLineStartOne.printCoordinates();
            //System.out.println("Line one is not perfect. Returning null.");
            return null;
        }

        //Find second line's dims
        if (Math.abs(perfLineStartTwo.getX() - perfLineEndTwo.getX()) < BoxGameFramework.UNIVERSAL_EPSILON) {
            twoXWise = true;
            regularDimLineTwo = perfLineStartTwo.getX();
            //Find varying dimension least and greatest, etc.
            if (perfLineStartTwo.getY() >= perfLineEndTwo.getY()) {
                leastDimOfLineTwo = perfLineEndTwo.getY();
                greatestDimOfLineTwo = perfLineStartTwo.getY();
            } else if (perfLineStartTwo.getY() <= perfLineEndTwo.getY()) {
                leastDimOfLineTwo = perfLineStartTwo.getY();
                greatestDimOfLineTwo = perfLineEndTwo.getY();
            } else {
                //Equal?
            }
        } else if (Math.abs(perfLineStartTwo.getY() - perfLineEndTwo.getY()) < BoxGameFramework.UNIVERSAL_EPSILON) {
            twoXWise = false;
            regularDimLineTwo = perfLineStartTwo.getY();
            if (perfLineStartTwo.getX() >= perfLineEndTwo.getX()) {
                leastDimOfLineTwo = perfLineEndTwo.getX();
                greatestDimOfLineTwo = perfLineStartTwo.getX();
            } else if (perfLineStartTwo.getX() <= perfLineEndTwo.getX()) {
                leastDimOfLineTwo = perfLineStartTwo.getX();
                greatestDimOfLineTwo = perfLineEndTwo.getX();
            } else {
                //Equal?
            }
        } else {
            //Line two is not perfect, waddafuck
            //System.out.println("Line two is not perfect. Returning null.");
            return null;
        }

        //System.out.println("oneXWise: " + oneXWise + ", twoXWise: " + twoXWise);
        //Start comparing and finding points
        if (!oneXWise && twoXWise) {
            if ((leastDimOfLineOne <= regularDimLineTwo && regularDimLineTwo <= greatestDimOfLineOne)
                    && (leastDimOfLineTwo <= regularDimLineOne && regularDimLineOne <= greatestDimOfLineTwo)) {
                return new Coordinates(perfLineStartTwo.getX(), perfLineStartOne.getY());
            } else {
                return null;
            }
        } else if (oneXWise && !twoXWise) {
            if ((leastDimOfLineOne <= regularDimLineTwo && regularDimLineTwo <= greatestDimOfLineOne)
                    && (leastDimOfLineTwo <= regularDimLineOne && regularDimLineOne <= greatestDimOfLineTwo)) {
                return new Coordinates(perfLineStartOne.getX(), perfLineStartTwo.getY());
            } else {
                return null;
            }
        } else if ((oneXWise && twoXWise)
                || (!oneXWise && !twoXWise)) {
            /*
             ALL OF THE BELOW CODE IS APPLIED ONLY WHEN ONEXWISE && TWOXWISE || !ONEXWISE && !TWOXWISE, KEEP CALM AND CARRY ON WITH OTHERS
             */
            //Check if regular dims are the same
            if (!(Math.abs(regularDimLineOne - regularDimLineTwo) < BoxGameFramework.UNIVERSAL_EPSILON)) {
                //System.out.println("RegularDims are not the same");
                return null;
            }
            //Check if either are within the other's bounds
            // System.out.println("leastDimlineOne: " + leastDimOfLineOne + ", greatestDimLineOne: "+ greatestDimOfLineOne);
            // System.out.println("leastDimLineTw: " + leastDimOfLineTwo + ", greatestDimineTwo: " + greatestDimOfLineTwo);
            //System.out.println("reguarllineone: " + regularDimLineOne + ", regualrtwo "+ regularDimLineTwo);

            //REDOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
            if (!((leastDimOfLineOne <= leastDimOfLineTwo && leastDimOfLineTwo <= greatestDimOfLineOne)
                    || (leastDimOfLineOne <= greatestDimOfLineTwo && greatestDimOfLineTwo <= greatestDimOfLineOne)
                    || (leastDimOfLineTwo <= leastDimOfLineOne && leastDimOfLineOne <= greatestDimOfLineTwo)
                    || (leastDimOfLineTwo <= greatestDimOfLineOne && greatestDimOfLineOne <= greatestDimOfLineTwo))) {
                return null;
            }

            //Only 2 possibilites here
            //Check if line one's points are both inside lineTwo
            if ((leastDimOfLineTwo <= leastDimOfLineOne && greatestDimOfLineOne <= greatestDimOfLineTwo)
                    && (leastDimOfLineTwo <= greatestDimOfLineOne && greatestDimOfLineOne <= greatestDimOfLineTwo)) {
                float distOne = perfLineStartOne.findDistanceToPoint(perfLineStartTwo);
                float distTwo = perfLineEndOne.findDistanceToPoint(perfLineStartTwo);

                //Find the point of perfLineOne that is closest to perfLineStartOne
                //If the start point is farther away from perfLineTwo's start
                if (distOne > distTwo) {
                    return perfLineEndOne;
                } //If the end point is farther away from perfLineTwo's start
                else if (distTwo > distOne) {
                    return perfLineStartOne;
                } //If the distance is equal, then perfLine's points are right on top of each other
                else {
                    return perfLineStartOne;
                }
            } //At least one point, if not two, are inside lineOne
            else {
                //Find which points of lineTwo are within line One's bounds
                Coordinates[] lineTwoPoints = {perfLineStartTwo, perfLineEndTwo};
                ArrayList<Coordinates> pointsBetweenLineOne = new ArrayList<Coordinates>(2);
                int pointsCount = 0;
                for (int i = 0; i < 2; i++) {
                    if (twoXWise) {
                        if (leastDimOfLineOne <= lineTwoPoints[i].getY() && lineTwoPoints[i].getY() <= greatestDimOfLineOne) {
                            pointsBetweenLineOne.add(lineTwoPoints[i]);
                            pointsCount++;
                        }
                    } else if (!twoXWise) {
                        if (leastDimOfLineOne <= lineTwoPoints[i].getX() && lineTwoPoints[i].getX() <= greatestDimOfLineOne) {
                            pointsBetweenLineOne.add(lineTwoPoints[i]);
                            pointsCount++;
                        }
                    } else {
                        return null;
                    }
                }

                if (pointsBetweenLineOne.size() == 1) {
                    return pointsBetweenLineOne.get(0);
                } //Both points of lineTwo are between lineOne
                else if (pointsBetweenLineOne.size() == 2) {
                    float distOne = perfLineStartTwo.findDistanceToPoint(perfLineStartOne);
                    float distTwo = perfLineEndTwo.findDistanceToPoint(perfLineStartOne);

                        //Find the point of perfLineTwo that is closest to perfLineStartOne
                    //If startPoit of line two is farther away than endPoint
                    if (distOne > distTwo) {
                        return perfLineEndTwo;
                    } //If the end point is farther away from perfLineOne's start
                    //If end point of lineTwo is farthere away than start point
                    else if (distTwo > distOne) {
                        return perfLineStartTwo;
                    } //If the distance is equal, then perfLine's points are right on top of each other
                    else {
                        return perfLineStartTwo;
                    }
                } else {
                    return null;
                }
            }
        }
        //System.out.println("Shouldn't happen: perfLineIntersectsPerfLineAt: returning null");
        return null;
    }

    //******* START PRINT METHODS ******* //
    public void printCorners() {
        System.out.println("NW: (" + NW.getX() + ", " + NW.getY() + ")");
        System.out.println("SW: (" + SW.getX() + ", " + SW.getY() + ")");
        System.out.println("NE: (" + NE.getX() + ", " + NE.getY() + ")");
        System.out.println("SE: (" + SE.getX() + ", " + SE.getY() + ")");
    }

    public void printCornersForTesting() {
        System.out.println(NW.getX() + "," + NW.getY());
        System.out.println(SW.getX() + "," + SW.getY());
        System.out.println(NE.getX() + "," + NE.getY());
        System.out.println(SE.getX() + "," + SE.getY());
    }
}
