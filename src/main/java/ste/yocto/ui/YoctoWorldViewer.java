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
import com.beust.jcommander.ParameterException;
import java.io.File;
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
import ste.yocto.world.YoctoWorldFactory;

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
    
    private File worldFile = null;
    private YoctoWorld WORLD = null;
    
    @Override
    public void start(Stage stage) throws IOException {
        Parameters params = getParameters();
        System.out.println("PARAMETERS: " + params.getNamed());
        System.out.println("PARAMETERS: " + params.getUnnamed());
        System.out.println("PARAMETERS: " + params.getRaw());
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
                    Platform.runLater(new Runnable() {
                        @Override public void run() {
                            showWorld(WORLD);
                        }
                    });
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException x) {
                        break;
                    }

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
    
    public YoctoWorld getWorld() {
        return WORLD;
    }
    
    // ------------------------------------------------------- protected methods
    
    protected void _main(String... args) {
        YoctoWorldViewer.CommonOptions options = new YoctoWorldViewer.CommonOptions();
        JCommander jc = JCommander.newBuilder()
            .addObject(options)
            .build();
        
        jc.setProgramName(YoctoWorldCLI.class.getName());
        
        try {
            jc.parse(args);
        } catch (ParameterException x) {
            System.out.println("\nInvalid arguments: " + x.getMessage() + "\n");
            jc.usage();
            Platform.exit();
            return;
        }
        
        if (options.help) {
            jc.usage();
            Platform.exit();
            return;
        }
        
        new Thread() {
            @Override
            public void run() {
                try {
                     Application.launch(YoctoWorldViewer.class, args);
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        }.start();
        
        worldFile = options.file;
        if (!worldFile.exists()) {
            System.out.println("\nerror: " + worldFile.getAbsolutePath() + " invalid or not found\n");
            Platform.exit();
            return;
        }
        
        while(true) {
            try {
                WORLD = YoctoWorldFactory.fromFile(worldFile.getAbsolutePath());
                Thread.sleep(1000);
            } catch (Exception x) {
                //
                // TODO: error handling
                //
                x.printStackTrace();
                break;
            }
        }
    }
    
    
    // -------------------------------------------------------------------- main
    
    /**
     * @param args the command line arguments
     */
    public static void main(String... args) throws Exception {
        new YoctoWorldViewer()._main(args);
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
        
        @Parameter(names = "--file", description = "file write/read the yocto world to/from", required=true)
        public File file;
    }
    
}
