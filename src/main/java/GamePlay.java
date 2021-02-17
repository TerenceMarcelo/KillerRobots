/* Terence Marcelo 19163785
   SEC Assignment 1 2020
*/

import java.util.*;
import java.util.concurrent.*;
import java.lang.Thread;
import java.time.*;
import javafx.scene.control.*;
import javafx.application.Platform;

/* - The 2D Array is used to keep track of which squares are occupied
   - The SynchronousQueue is used to store a new robot until a new thread is
     available to take it.
   - The LinkedBlocking queue stores the missiles created from mouse clicks.
   - DogeAlive is used to keep track of if the game is still going.
   - The label is for displaying the score and the TextArea is for displaying
     the messages for when a robot is created, killed, etc.
*/
public class GamePlay
{
   private static ExecutorService es;
   private static SynchronousQueue<Robot> newRobot;
   private static LinkedBlockingQueue <Missile> missileHits;
   private static LinkedList<Robot> robots;
   private static int robotTotalCount;
   private static int score;
   private static boolean occupied [][];
   private static boolean dogeAlive;
   private static TextArea logger;
   private static Label label;
   private static Object mutex;

   public static void start(JFXArena arena, TextArea inLogger, Label inLabel)
   {
      if(robots == null)
      {
         robots = new LinkedList<Robot>();
         newRobot = new SynchronousQueue<Robot>();
         missileHits = new LinkedBlockingQueue<Missile>();
         robotTotalCount = 0;
         score = 0;
         es = Executors.newFixedThreadPool(34);
         occupied = new boolean[9][9];
         Arrays.fill(occupied[0], false);
         dogeAlive = true;
         logger = inLogger;
         label = inLabel;
         mutex = new Object();
      }

      /* This thread is for each robot and controls their movement/direction.*/
      Runnable robotThread = new Runnable()
      {
         @Override
         public void run()
         {
            Robot thisRobot;
            try
            {
               thisRobot = newRobot.take();
               robots.add(thisRobot);
               robotTotalCount++;
               while(dogeAlive && thisRobot.isAlive())
               {
                  Thread.sleep(thisRobot.getDelay());
                  thisRobot.setRobotX(Math.rint(thisRobot.getRobotX()));
                  thisRobot.setRobotY(Math.rint(thisRobot.getRobotY()));
                  int direction = 1 + (int)(Math.random() * ((4 - 1) + 1));
                  if(direction == 1 && thisRobot.getRobotX() < 8.0)
                  {
                     if(occupied[(int)thisRobot.getRobotX() + 1][
                                 (int)thisRobot.getRobotY()])
                     {
                        direction = 2;
                     }
                     else
                     {
                        int oldX = (int)thisRobot.getRobotX();
                        synchronized(mutex)
                        {
                           occupied[(int)thisRobot.getRobotX() + 1][
                                 (int)thisRobot.getRobotY()] = true;
                        }
                        for(int ii = 0; ii < 10; ii++)
                        {
                           /* Sleep thread for 50 miliseconds x10 moving a
                              bit each time since it should take 500
                              milliseconds to complete a movement*/
                           Thread.sleep(50);
                           thisRobot.setRobotX(thisRobot.getRobotX() + 0.1);
                        }
                        synchronized(mutex)
                        {
                           occupied[oldX][(int)thisRobot.getRobotY()] = false;
                        }
                     }
                  }
                  else if(direction == 2 && thisRobot.getRobotX() >= 1.0)
                  {
                     if(occupied[(int)thisRobot.getRobotX() - 1][
                                 (int)thisRobot.getRobotY()])
                     {
                        direction = 3;
                     }
                     else
                     {
                        int oldX = (int)thisRobot.getRobotX();
                        synchronized(mutex)
                        {
                           occupied[(int)thisRobot.getRobotX() - 1][
                                 (int)thisRobot.getRobotY()] = true;
                        }
                        for(int ii = 0; ii < 10; ii++)
                        {
                           Thread.sleep(50);
                           thisRobot.setRobotX(thisRobot.getRobotX() - 0.1);
                        }
                        synchronized(mutex)
                        {
                           occupied[oldX][(int)thisRobot.getRobotY()] = false;
                        }
                     }
                  }
                  else if(direction == 3 && thisRobot.getRobotY() < 8.0)
                  {
                     if(occupied[(int)thisRobot.getRobotX()][
                                 (int)thisRobot.getRobotY() + 1])
                     {
                        direction = 4;
                     }
                     else
                     {
                        int oldY = (int)thisRobot.getRobotY();
                        synchronized(mutex)
                        {
                           occupied[(int)thisRobot.getRobotX()][
                                 (int)thisRobot.getRobotY() + 1] = true;
                        }
                        for(int ii = 0; ii < 10; ii++)
                        {
                           Thread.sleep(50);
                           thisRobot.setRobotY(thisRobot.getRobotY() + 0.1);
                        }
                        synchronized(mutex)
                        {
                           occupied[(int)thisRobot.getRobotX()][oldY] = false;
                        }
                     }
                  }
                  else if(direction == 4 && thisRobot.getRobotY() >= 1.0)
                  {
                     if(!occupied[(int)thisRobot.getRobotX()][
                                  (int)thisRobot.getRobotY() - 1])
                     {
                        int oldY = (int)thisRobot.getRobotY();
                        synchronized(mutex)
                        {
                           occupied[(int)thisRobot.getRobotX()][
                                 (int)thisRobot.getRobotY() - 1] = true;
                        }
                        for(int ii = 0; ii < 10; ii++)
                        {
                           Thread.sleep(50);
                           thisRobot.setRobotY(thisRobot.getRobotY() - 0.1);
                        }
                        synchronized(mutex)
                        {
                           occupied[(int)thisRobot.getRobotX()][oldY] = false;
                        }
                     }
                  }
                  if((int)Math.rint(thisRobot.getRobotX()) == 4 &&
                     (int)Math.rint(thisRobot.getRobotY()) == 4)
                  {
                     /* As soon as a robot starts moving into (4,4) where doge
                        is, the game ends. */
                     dogeAlive = false;
                     logger.appendText("\n\n\nGAME OVER");
                     logger.appendText("\nFINAL SCORE: " +
                                       String.valueOf(score) + "\n");
                     arena.gameOver();
                     es.shutdown();
                  }
               }
            occupied[(int)thisRobot.getRobotX()][
                     (int)thisRobot.getRobotY()] = false;
            robots.remove(thisRobot);
            }catch(InterruptedException ie)
            {
               System.out.println("One of the robots was interrupted.");
            }
         }
      };


      Runnable robotCreationTimer = new Runnable()
      {
         @Override
         public void run()
         {
            try
            {
               Robot tempRobot;

               Instant before = Instant.now();
               while(dogeAlive)
               {
                  Instant after = Instant.now();
                  long delta = Duration.between(before, after).toMillis();

                  /* Creates a robot every 100 milliseconds. */
                  if(delta > 100)
                  {
                     /* I chose the delay to be random between 500 and 900*/
                     int delay = 500 + (int)(Math.random() * ((900 - 500) +1));
                     if(!occupied[0][0])
                     {
                        occupied[0][0] = true;
                        tempRobot = new Robot(robotTotalCount,delay,0.0,0.0);
                        es.execute(robotThread);
                        newRobot.put(tempRobot);
                        logger.appendText("\nNew Robot at (0,0)");
                     }
                     else if(!occupied[8][0])
                     {
                        occupied[8][0] = true;
                        tempRobot = new Robot(robotTotalCount,delay,8.0,0.0);
                        es.execute(robotThread);
                        newRobot.put(tempRobot);
                        logger.appendText("\nNew Robot at (8,0)");
                     }
                     else if(!occupied[0][8])
                     {
                        occupied[0][8] = true;
                        tempRobot = new Robot(robotTotalCount,delay,0.0,8.0);
                        es.execute(robotThread);
                        newRobot.put(tempRobot);
                        logger.appendText("\nNew Robot at (0,8)");
                     }
                     else if(!occupied[8][8])
                     {
                        occupied[8][8] = true;
                        tempRobot = new Robot(robotTotalCount,delay,8.0,8.0);
                        es.execute(robotThread);
                        newRobot.put(tempRobot);
                        logger.appendText("\nNew Robot at (8,8)");
                     }
                     before = Instant.now();
                  }
               }
            }catch(InterruptedException ie)
            {
               System.out.println("Robot creation interrupted.");
            }
         }
      };

      Runnable screenRefreshTimer = new Runnable()
      {
         @Override
         public void run()
         {
            Instant before = Instant.now();
            while(dogeAlive)
            {
               Instant after = Instant.now();
               long delta = Duration.between(before, after).toMillis();
               if(delta > 5)
               {
                  /*Display is refreshed every 5 milliseconds so 20 fps*/
                  arena.updateArena();
                  before = Instant.now();
               }
            }
         }
      };

      Runnable scoreTimer = new Runnable()
      {
         @Override
         public void run()
         {
            Instant before = Instant.now();
            while(dogeAlive)
            {
               Instant after = Instant.now();
               long delta = Duration.between(before, after).toMillis();
               if(delta > 1000)
               {
                  score = score + 10;
                  Platform.runLater(() ->
                  {
                     label.setText("Score: " + String.valueOf(score));
                  });
                  before = Instant.now();
               }
            }
         }
      };

      Runnable missileTimer = new Runnable()
      {
         @Override
         public void run()
         {
            try
            {
               boolean robotHit;
               while(dogeAlive)
               {
                  /* Iterates through list of robots to see if any of their
                     locations matches the one the user clicked on. */
                  Thread.sleep(100);
                  Missile thisMissile = missileHits.take();
                  Iterator iter = robots.iterator();
                  robotHit = false;
                  while(iter.hasNext())
                  {
                     Robot currentRobot = (Robot)iter.next();
                     if((int)currentRobot.getRobotX() == thisMissile.getX() &&
                        (int)currentRobot.getRobotY() == thisMissile.getY())
                     {
                        currentRobot.killRobot();
                        logger.appendText("\nRobot at (" + String.valueOf(
                        thisMissile.getX()) + "," + String.valueOf(
                                          thisMissile.getY()) + ") killed.");
                        robotHit = true;
                        score = score + 10;
                     }
                  }
                  if(!robotHit && dogeAlive)
                  {
                     logger.appendText("\nNo robots hit.");
                  }
               }
            }catch(InterruptedException ie)
            {
               System.out.println("Missile Launch Interrupted");
            }
         }
      };

      /* Start all the threads.*/
      es.execute(missileTimer);
      es.execute(screenRefreshTimer);
      es.execute(robotCreationTimer);
      es.execute(scoreTimer);
   }

   public static void launchMissile(int x, int y)
   {
      try
      {
         /* Adds mouse clicks to the synchronous queue*/
         Missile toLaunch = new Missile(x, y);
         missileHits.put(toLaunch);
      }catch(InterruptedException ie)
      {
         System.out.println("Missile Launch interrupted.");
      }
   }

   public static List<Robot> getRobots()
   {
      return Collections.unmodifiableList(robots);
   }

   public static boolean isDogeAlive()
   {
      return dogeAlive;
   }
}
