/* Terence Marcelo 19163785
   SEC Assignment 1 2020
*/

import javafx.scene.canvas.*;
import javafx.geometry.VPos;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;

/*A JavaFX GUI element that displays a grid on which you can draw images,
   text and lines.*/
public class JFXArena extends Pane
{
   /* Represents the image to draw. You can modify this to introduce
      multiple images.
   */
   private static final String IMAGE_FILE = "robot.png";
   private static final String DOGE_FILE = "doge.png";
   private static final String DEADDOGE_FILE = "deadDoge.jpg";
   private Image robot;
   private Image doge;
   private Image deadDoge;

   /* The following values are arbitrary, and you may need to
      modify them according to the requirements of your application.
   */
   private int gridWidth = 9;
   private int gridHeight = 9;

   //private BlockingQueue<Robot> robotQueue = new LinkedBlockingQueue<>();
   //private LinkedList<Robot> robots = new LinkedList<Robot>();

   private double gridSquareSize; // Auto-calculated
   private Canvas canvas; // Used to provide a 'drawing surface'.

   private List<ArenaListener> listeners = null;
   private boolean gameOver = false;

   /* Creates a new arena object, loading the robot image and
      initialising a drawing surface.
   */
   public JFXArena()
   {
      /* Here's how you get an Image object from an image file
         (which you provide in the 'resources/' directory).
      */
      InputStream is = getClass().getClassLoader().getResourceAsStream(
                                                                  IMAGE_FILE);
      if(is == null)
      {
         throw new AssertionError("Cannot find image file " + IMAGE_FILE);
      }
      robot = new Image(is);

      is = getClass().getClassLoader().getResourceAsStream(DOGE_FILE);
      if(is == null)
      {
         throw new AssertionError("Cannot find image file " + DOGE_FILE);
      }
      doge = new Image(is);

      is = getClass().getClassLoader().getResourceAsStream(DEADDOGE_FILE);
      if(is == null)
      {
         throw new AssertionError("Cannot find image file " + DEADDOGE_FILE);
      }
      deadDoge = new Image(is);

      canvas = new Canvas();
      canvas.widthProperty().bind(widthProperty());
      canvas.heightProperty().bind(heightProperty());
      getChildren().add(canvas);
   }

   /* Threading class calls this a few times every second
      while the game is still ongoing */
   public void updateArena()
   {
      requestLayout();
   }

   /* Threading class calls this when the game is over to call
      requestLayout() one last time to display the dead doge image */
   public void gameOver()
   {
      gameOver = true;
      requestLayout();
   }

   /* Adds a callback for when the user clicks on a grid square within the
      arena.
      The callback (of type ArenaListener) receives the grid (x,y) coordinates
      as parameters to the
      'squareClicked()' method.
   */
   public void addListener(ArenaListener newListener)
   {
       if(listeners == null)
       {
           listeners = new LinkedList<>();
           setOnMouseClicked(event ->
           {
               int gridX = (int)(event.getX() / gridSquareSize);
               int gridY = (int)(event.getY() / gridSquareSize);

               if(gridX < gridWidth && gridY < gridHeight)
               {
                   for(ArenaListener listener : listeners)
                   {
                       listener.squareClicked(gridX, gridY);
                   }
               }
           });
       }
       listeners.add(newListener);
   }


   /* This method is called in order to redraw the screen, either because
      the user is manipulating the window, OR because you've called
      'requestLayout()'.
      You will need to modify the last part of this method; specifically
      the sequence of calls to the other 'draw...()' methods. You shouldn't
      need to modify anything else about it.
   */
   @Override
   public void layoutChildren()
   {
      super.layoutChildren();
      GraphicsContext gfx = canvas.getGraphicsContext2D();
      gfx.clearRect(0.0, 0.0, canvas.getWidth(), canvas.getHeight());

      gridSquareSize = Math.min(
           getWidth() / (double) gridWidth,
           getHeight() / (double) gridHeight);

      double arenaPixelWidth = gridWidth * gridSquareSize;
      double arenaPixelHeight = gridHeight * gridSquareSize;


      gfx.setStroke(Color.DARKGREY);
      gfx.strokeRect(0.0, 0.0, arenaPixelWidth - 1.0, arenaPixelHeight - 1.0);
      for(int gridX = 1; gridX < gridWidth; gridX++)
      {
         double x = (double) gridX * gridSquareSize;
         gfx.strokeLine(x, 0.0, x, arenaPixelHeight);
      }

      for(int gridY = 1; gridY < gridHeight; gridY++)
      {
         double y = (double) gridY * gridSquareSize;
         gfx.strokeLine(0.0, y, arenaPixelWidth, y);
      }

      if(!gameOver)
      {
         drawImage(gfx, doge, 4.0, 4.0);
         drawLabel(gfx, "Protect Doge", 4.0, 4.0);

         List<Robot> robotList = (List<Robot>)GamePlay.getRobots();
         Iterator iter = robotList.iterator();
         while(iter.hasNext())
         {
            Robot toDraw = (Robot)iter.next();
            drawImage(gfx, robot, toDraw.getRobotX(), toDraw.getRobotY());
            drawLabel(gfx, String.valueOf(toDraw.getId()), toDraw.getRobotX(),
                                                         toDraw.getRobotY());
         }
      }
      else
      {
         drawImage(gfx, deadDoge, 4.0, 4.0);
         drawLabel(gfx, "RIP Doge", 4.0, 4.0);
      }
    }

   /* Draw an image in a specific grid location. Only call this from
      within layoutChildren().
      You shouldn't need to modify this method.
   */
   private void drawImage(GraphicsContext gfx, Image image, double gridX,
                                                                 double gridY)
   {
      double x = (gridX + 0.5) * gridSquareSize;
      double y = (gridY + 0.5) * gridSquareSize;
      double fullSizePixelWidth = robot.getWidth();
      double fullSizePixelHeight = robot.getHeight();

      double displayedPixelWidth, displayedPixelHeight;
      if(fullSizePixelWidth > fullSizePixelHeight)
      {
          displayedPixelWidth = gridSquareSize;
          displayedPixelHeight = gridSquareSize * fullSizePixelHeight /
                                                         fullSizePixelWidth;
      }
      else
      {
          displayedPixelHeight = gridSquareSize;
          displayedPixelWidth = gridSquareSize * fullSizePixelWidth /
                                                         fullSizePixelHeight;
       }

      gfx.drawImage(image, x - displayedPixelWidth / 2.0,
                     y - displayedPixelHeight / 2.0, displayedPixelWidth,
                                                      displayedPixelHeight);
   }


   /* Only call this from within layoutChildren().
      You shouldn't need to modify this method.
   */
   private void drawLabel(GraphicsContext gfx, String label, double gridX,
                                                               double gridY)
   {
        gfx.setTextAlign(TextAlignment.CENTER);
        gfx.setTextBaseline(VPos.TOP);
        gfx.setStroke(Color.BLUE);
        gfx.strokeText(label, (gridX + 0.5) * gridSquareSize, (gridY + 1.0)
                                                            * gridSquareSize);
   }

   /*You shouldn't need to modify this method.
   */
   private void drawLine(GraphicsContext gfx, double gridX1, double gridY1,
                                               double gridX2, double gridY2)
   {
      gfx.setStroke(Color.RED);
      final double radius = 0.5;
      double angle = Math.atan2(gridY2 - gridY1, gridX2 - gridX1);
      double clippedGridX1 = gridX1 + Math.cos(angle) * radius;
      double clippedGridY1 = gridY1 + Math.sin(angle) * radius;

      gfx.strokeLine((clippedGridX1 + 0.5) * gridSquareSize,
                     (clippedGridY1 + 0.5) * gridSquareSize,
                     (gridX2 + 0.5) * gridSquareSize,
                     (gridY2 + 0.5) * gridSquareSize);
   }


}
