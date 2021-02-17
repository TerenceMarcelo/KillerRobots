/* Terence Marcelo 19163785
   SEC Assignment 1 2020
*/

import java.util.*;

public class Robot
   {
      private int id;
      private double robotX, robotY;
      private long delay;
      private boolean alive;

      public Robot(int inId, long inDelay, double inRobotX, double inRobotY)
      {
         id = inId;
         delay = inDelay;
         robotX = inRobotX;
         robotY = inRobotY;
         alive = true;
      }

      public long getDelay()
      {
         return delay;
      }

      public int getId()
      {
         return id;
      }

      public boolean isAlive()
      {
         return alive;
      }

      public void setRobotX(double inRobotX)
      {
         robotX = inRobotX;
      }

      public void setRobotY(double inRobotY)
      {
         robotY = inRobotY;
      }

      public void killRobot()
      {
         alive = false;
      }

      public double getRobotX()
      {
         return robotX;
      }

      public double getRobotY()
      {
         return robotY;
      }
   }
