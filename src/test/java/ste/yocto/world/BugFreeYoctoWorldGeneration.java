/*
 * Copyright (C) 2018 Stefano Fornari.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Stefano Fornari.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * STEFANO FORNARI MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY
 * OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. STEFANO FORNARI SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */
package ste.yocto.world;

import static org.assertj.core.api.AssertionsForClassTypes.fail;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import ste.yocto.world.YoctoWorld.Yocto;
import static ste.yocto.world.YoctoWorld.Yocto.ATTRACTOR;
import static ste.yocto.world.YoctoWorld.Yocto.FRIEND;
import static ste.yocto.world.YoctoWorld.Yocto.NEUTRAL;
import static ste.yocto.world.YoctoWorld.Yocto.REJECTOR;

/**
 *
 */
public class BugFreeYoctoWorldGeneration {
        
    // -------------------------------------------------------------------------
    // WORLD GENERATION
    // -------------------------------------------------------------------------
    
    @Test
    public void create_empty_world_ok() {
        YoctoWorld w = new YoctoWorld(1, 1);
        
        then(w).hasFieldOrPropertyWithValue("height", 1)
               .hasFieldOrPropertyWithValue("width", 1);
        then(w.getEnergy(1,1)).isZero();
        
        w = YoctoWorldFactory.empty(15, 20);
        then(w).hasFieldOrPropertyWithValue("height", 20)
               .hasFieldOrPropertyWithValue("width", 15);
        for (int y=1; y<=w.getHeight(); ++y) {
            for (int x=1; x<=w.getWidth(); ++x) {
                then(w.getYocto(x, y)).isEqualTo(NEUTRAL);
            }
        }
    }
    
    @Test
    public void create_empty_world_ko() {
        //
        // wrong width
        //
        for (int i: new int[] {0, -1, -1234}) {
            try {
                new YoctoWorld(i, 10);
                fail("missing argument validation");
            } catch (IllegalArgumentException x) {
                then(x).hasMessage("width must be a postive number (found " + i + ")");
            }
        }
        
        //
        // wrong height
        //
        for (int i: new int[] {0, -1, -1234}) {
            try {
                new YoctoWorld(10, i);
                fail("missing argument validation");
            } catch (IllegalArgumentException x) {
                then(x).hasMessage("height must be a postive number (found " + i + ")");
            }
        }
    }
    
    @Test
    public void create_world_from_map_ok() {
        final Yocto[][] MAP = new Yocto[][] {
            new Yocto[] { NEUTRAL, ATTRACTOR, FRIEND },
            new Yocto[] { REJECTOR, FRIEND, REJECTOR },
            new Yocto[] { ATTRACTOR, NEUTRAL, NEUTRAL }
        };
        
        YoctoWorld w = new YoctoWorld(MAP);
        for (int y=1; y<MAP.length; ++y) {
            for (int x=1; x<=MAP[0].length; ++x) {
                then(w.getYocto(x, y)).isEqualTo(MAP[y-1][x-1]);
            }
        }
    }
    
    @Test
    public void create_world_from_map_ko() {
        try {
            new YoctoWorld(null);
            fail("missing argument validation");
        } catch(IllegalArgumentException x) {
            then(x).hasMessage("map can not be null");
        }
    }
    
    @Test
    public void create_world_from_wrong_map_ko() {
        final Yocto[][] MAP = new Yocto[][] {
            new Yocto[] { NEUTRAL, ATTRACTOR, FRIEND },
            new Yocto[] { REJECTOR, FRIEND, REJECTOR },
            new Yocto[] { ATTRACTOR, NEUTRAL }
        };
        
        //
        // rows with different sizes
        //
        try {
            new YoctoWorld(MAP);
            fail("missing argument validation");
        } catch(IllegalArgumentException x) {
            then(x).hasMessage("row length mistmatch in row #3 (2 instead of 3)");
        }
        
        MAP[1] = new Yocto[] {NEUTRAL};
        try {
            new YoctoWorld(MAP);
            fail("missing argument validation");
        } catch(IllegalArgumentException x) {
            then(x).hasMessage("row length mistmatch in row #2 (1 instead of 3)");
        }
        
        //
        // Zero-size rows/clumns
        // 
        MAP[0] = new Yocto[0];
        try {
            new YoctoWorld(MAP);
            fail("missing argument validation");
        } catch(IllegalArgumentException x) {
            then(x).hasMessage("row length must be a positive integer");
        }
        
        try {
            new YoctoWorld(new Yocto[0][0]);
            fail("missing argument validation");
        } catch(IllegalArgumentException x) {
            then(x).hasMessage("column length must be a positive integer");
        }
    }
    
    // --------------------------------------------------------- private methods
    
}
