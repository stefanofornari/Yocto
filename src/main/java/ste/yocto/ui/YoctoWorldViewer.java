/*
 * Copyright (C) 2017 Stefano Fornari
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package ste.yocto.ui;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.validators.PositiveInteger;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import ste.yocto.world.YoctoWorld;

/**
 *
 */
public class YoctoWorldViewer extends Application
                      implements Initializable {
    
    public static String OPT_HELP = "--help";    
    public static String OPT_FILE = "--file";
    
    public static final String PARAM_WORLD = "world";
    
    private static Parent root;
    
    private Task updateViewTask;
    
    @FXML
    private GridPane worldPane;
    
    @FXML
    private Button playButton;
    
    @FXML
    private Button pauseButton;
    
    public static YoctoWorld WORLD = null;
    
    @Override
    public void start(Stage stage) throws IOException {
        YoctoWorldViewer.CommonOptions options = new YoctoWorldViewer.CommonOptions();
        JCommander jc = JCommander.newBuilder()
            .addObject(options)
            .build();
        
        jc.setProgramName(YoctoWorldCLI.class.getName());
        
        Parameters args = getParameters();
        if (args.getNamed().isEmpty()) {
            jc.usage();
            Platform.exit();
            return;
        }
        
        
        System.out.println("PARAMETERS: " + args.getNamed());
        System.out.println("PARAMETERS: " + args.getUnnamed());
        System.out.println("PARAMETERS: " + args.getRaw());
        
        /*
        String worldFile = params.getNamed().get(PARAM_WORLD);
        if (worldFile != null) {
            WORLD = YoctoWorldFactory.fromFile(worldFile);
        } else {
            WORLD = YoctoWorldFactory.empty(WIDTH, HEIGHT);
        }
        
        root = FXMLLoader.load(YoctoWorldViewer.class.getResource("/ste/yocto/ui/YoctoLab.fxml"));
        Scene scene = new Scene(root);
        
        scene.getStylesheets().add(
            getClass().getResource("/ste/yocto/ui/css/stylesheet.css").toExternalForm()
        );
        
        stage.setTitle("YoctoWorld Laboratory");
        stage.setScene(scene);
        stage.show();
        */
    }
    
    @Override
    public void stop() {
        Platform.exit();
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (int y=1; y<=WORLD.getHeight(); ++y) {
            for (int x=1; x<=WORLD.getWidth(); ++x) {
                YoctoTile tile = new YoctoTile();
                worldPane.add(tile, x-1, y-1);
                tile.yoctoProperty().set(WORLD.getYocto(x, y));
            }
        }
    }
    
    @FXML
    public void pause() {
        updateViewTask.cancel(true);
        playButton.setDisable(false);
        pauseButton.setDisable(true);
    }
    
    @FXML
    public void play() {
        updateViewTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (!isCancelled()) {
                    /*
                    printWorld(WORLD);
                    WORLD.evolve();
                    printWorld(WORLD);
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            showWorld(WORLD);
                        }
                    });
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException x) {
                        return null;
                    }
                    */
                }
                return null;
            }
        };
        
        Thread t = new Thread(updateViewTask);
        t.setDaemon(false);
        t.start();
        
        playButton.setDisable(true);
        pauseButton.setDisable(false);
    }
    
    
    
    // -------------------------------------------------------------------- main
    
    /**
     * @param args the command line arguments
     */
    public static void main(String... args) throws Exception {
        launch(args);
    }
    
    // --------------------------------------------------------- private methods
        
    
    private void showWorld(YoctoWorld w) {
        List<Node> nodes = worldPane.getChildren();
        
        for(int y=1; y<=w.getHeight(); ++y) {
            for(int x=1; x<=w.getWidth(); ++x) {
                YoctoTile tile = (YoctoTile)nodes.get((y-1)*w.getWidth() + (x-1));
                tile.yoctoProperty().set(w.getYocto(x, y));
                tile.energyProperty().set(w.getEnergy(x, y));
            }
        }
    }
    
    // ----------------------------------------------------------- CommonOptions
    
    private static class CommonOptions {
        @Parameter(names = "--help", help = true)
        public boolean help;
        
        @Parameter(names = "--file")
        public String file;
    }
    
}
