/* Terence Marcelo 19163785
   SEC Assignment 1 2020
*/

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class App extends Application
{
   public static void main(String[] args)
   {
      launch();
   }

   @Override
   public void start(Stage stage)
   {
      Label label = new Label("Score: 0" );
      TextArea logger = new TextArea();
      stage.setTitle("Killer Robots");

      JFXArena arena = new JFXArena();
      GamePlay.start(arena, logger, label);

      arena.addListener((x, y) ->
      {
         System.out.println("Arena click at (" + x + "," + y + ")");
         GamePlay.launchMissile(x,y);
      });


      ToolBar toolbar = new ToolBar();
      toolbar.getItems().addAll(label);

      logger.appendText("Hit the robots before they reach doge!");

      SplitPane splitPane = new SplitPane();
      splitPane.getItems().addAll(arena, logger);
      arena.setMinWidth(300.0);

      BorderPane contentPane = new BorderPane();
      contentPane.setTop(toolbar);
      contentPane.setCenter(splitPane);

      Scene scene = new Scene(contentPane, 800, 800);
      stage.setScene(scene);
      stage.show();
   }
}
