/*
 * Copyright (C) 2018 Stefano Fornari
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
    
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.StringPropertyBase;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import static ste.yocto.ui.YoctoLab.WORLD;
import ste.yocto.world.YoctoWorld.Yocto;
import static ste.yocto.world.YoctoWorld.Yocto.*;

/**
 *
 */
public class YoctoTile extends TilePane 
                       implements Initializable {
    
    public static final String CSS_CLASS_YOCTO_TILE = "yocto-tile";
    public static final String CSS_CLASS_YOCTO_TILE_HIGH = "yocto-tile-high";
    
    private YoctoProperty yoctoProperty;
    private EnergyProperty energyProperty;
    
    @FXML
    private Label energyLabel;
    
    @FXML
    private Label yoctoLabel;
    
    @FXML
    private Label noteLabel;
    
    public YoctoTile() {
        FXMLLoader fxmlLoader = new FXMLLoader(
            getClass().getResource("/ste/yocto/ui/YoctoTile.fxml")
        );
        
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        
        yoctoProperty = new YoctoProperty(this);
        energyProperty = new EnergyProperty(this);
        
        try {
           fxmlLoader.load();
        } catch (IOException exception) {
           throw new RuntimeException(exception);
        }
    }
    
    @FXML
    protected void onClick() {
        System.out.println("clicked " + this);
        
        switch(yoctoProperty.getYocto()) {
            case NEUTRAL  : yoctoProperty.set(ATTRACTOR) ; break;
            case ATTRACTOR: yoctoProperty.set(REJECTOR)  ; break;
            case REJECTOR : yoctoProperty.set(FRIEND)    ; break;
            default       : yoctoProperty.set(NEUTRAL)   ; break;
        }
        
        List<Node> nodes = getParent().getChildrenUnmodifiable();
        
        int pos = 0;
        for (Node n: nodes) {
            if (n == this) {
                break;
            }
            ++pos;    
        }
        
        int posy = Math.floorDiv(pos, WORLD.getWidth())+1;
        int posx = Math.floorMod(pos, WORLD.getWidth())+1;
        
        WORLD.select(posx, posy).set(yoctoProperty.getYocto());
        
        //
        // TODO: move this logic up one level
        //
        for (int y=1; y<=WORLD.getHeight(); ++y) {
            for (int x=1; x<=WORLD.getWidth(); ++x) {
                YoctoTile t = (YoctoTile)nodes.get((y-1)*YoctoLab.WIDTH+(x-1));
                t.energyProperty.set(WORLD.getEnergy(x, y));
            }
        }
    }
    
    @FXML
    protected void mouseIn() {
        getStyleClass().add(CSS_CLASS_YOCTO_TILE_HIGH);
    }
    
    @FXML
    protected void mouseOut() {
        getStyleClass().remove(CSS_CLASS_YOCTO_TILE_HIGH);
    }
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        energyLabel.setText("0");
        noteLabel.setText("");
        yoctoLabel.setText(String.valueOf(Yocto.NEUTRAL.toChar()));
        
        yoctoLabel.textProperty().bind(yoctoProperty());
        energyLabel.textProperty().bind(energyProperty().asString());
    }
    
    public YoctoProperty yoctoProperty() {
        return yoctoProperty;
    }
    
    public EnergyProperty energyProperty() {
        return energyProperty;
    }
    
    // ----------------------------------------------------------- YoctoProperty
    
    protected class YoctoProperty extends StringPropertyBase {
        private Object owner;
        private Yocto yocto;
        
        protected YoctoProperty(YoctoTile owner) {
            this.owner = owner;
            this.yocto = NEUTRAL;
        }
        
        @Override
        public Object getBean() {
            return owner;
        }

        @Override
        public String getName() {
            return "yocto";
        }
        
        public void set(Yocto y) {
            ObservableList<String> styles = getStyleClass();
            
            styles.removeAll(CSS_CLASS_YOCTO_TILE + "-" + yocto.toString().toLowerCase());
            
            yocto = y;
            setValue(String.valueOf(y.toChar()));
            
            styles.add(CSS_CLASS_YOCTO_TILE + "-" + yocto.toString().toLowerCase());
        }
        
        public Yocto getYocto() {
            return yocto;
        }
        
    };

    protected class EnergyProperty extends IntegerPropertyBase {
        private Object owner;
        
        protected EnergyProperty(Object owner) {
            this.owner = owner;
        }
        
        @Override
        public Object getBean() {
            return owner;
        }

        @Override
        public String getName() {
            return "energy";
        }
    };

}
