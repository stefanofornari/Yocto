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

import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.TilePane;
import javafx.stage.Stage;
import ste.yocto.world.YoctoWorld;
import ste.yocto.world.YoctoWorldFactory;

/**
 *
 * @author ste
 */
public class YoctoLab extends Application {
    
    public static final int WIDTH = 5;
    public static final int HEIGHT = 5;
    
    private static Parent root;
    
    @FXML
    private TilePane worldPane;
    
    public static final YoctoWorld WORLD = YoctoWorldFactory.empty(WIDTH, HEIGHT);
    
    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(root, 300, 600);
        
        scene.getStylesheets().add(
            getClass().getResource("/ste/yocto/ui/css/stylesheet.css").toExternalForm()
        );
        
        stage.setTitle("YoctoWorld Laboratory");
        stage.setScene(scene);
        stage.show();
    }
    
    public void stop() {
        Platform.exit();
        System.exit(0);
    }
    
    @FXML
    public void action() {
        Task updateViewTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (!isCancelled()) {
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
                }
                return null;
            }
        };
        
        Thread t = new Thread(updateViewTask);
        t.setDaemon(false);
        t.start();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        root = FXMLLoader.load(YoctoLab.class.getResource("/ste/yocto/ui/YoctoLab.fxml"));
        launch(args);
    }
    
    // --------------------------------------------------------- private methods
        
    private void printWorld(YoctoWorld w) {
        System.out.print("\n");
        
        for(int y=1; y<=w.getHeight(); ++y) {
            for(int x=1; x<=w.getWidth(); ++x) {
                System.out.print(w.select(x,y).get().toChar());
            }
            System.out.print('\t');
            for(int x=1; x<=w.getWidth(); ++x) {
                System.out.print(w.getEnergy(x,y));
            }
            System.out.print('\n');
        }
    }
    
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
    
}
