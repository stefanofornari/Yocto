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
import ste.xtest.Constants;
import ste.yocto.world.YoctoWorld.Yocto;
import static ste.yocto.world.YoctoWorld.Yocto.ATTRACTOR;
import static ste.yocto.world.YoctoWorld.Yocto.FRIEND;
import static ste.yocto.world.YoctoWorld.Yocto.NEUTRAL;
import static ste.yocto.world.YoctoWorld.Yocto.REJECTOR;

/**
 *
 */
public class BugFreeYoctoWorldManipulation {
            
    // -------------------------------------------------------------------------
    // WORLD MANIPULATION
    // -------------------------------------------------------------------------
    
    @Test
    public void fluent_select_a_cell_selects_the_cell_ok() {
        YoctoWorld w = YoctoWorldFactory.random(5, 5);
        
        then(w.select(1,1)).isSameAs(w);
        then(w.select(2,3)).isSameAs(w);
        
        for (int y=1; y<=w.getHeight(); ++y) {
            for (int x=1; x<=w.getWidth(); ++x) {
                then(w.select(x,y).get()).isEqualTo(w.getYocto(x, y));
            }
        }
    }
    
    @Test
    public void fluent_select_ko() {
        YoctoWorld w = YoctoWorldFactory.random(3, 5);
        
        for (int I: Constants.NOT_POSITIVES_0_1_25_389_4567) {
            try {
                w.select(I,1);
                fail("missing argument validation");
            } catch (IllegalArgumentException x) {
                then(x).hasMessage("x must be a positive number less then " + w.getWidth()+ " (found " + I + ")");
            }
        }
        
        for (int I: Constants.NOT_POSITIVES_0_1_25_389_4567) {
            try {
                w.select(1,I);
                fail("missing argument validation");
            } catch (IllegalArgumentException x) {
                then(x).hasMessage("y must be a positive number less then " + w.getHeight()+ " (found " + I + ")");
            }
        }
        
        try {
            w.select(w.getWidth()+1,1);
            fail("missing argument validation");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("x must be a positive number less then " + w.getWidth()+ " (found " + (w.getWidth()+1) + ")");
        }
        
        try {
            w.select(1, w.getHeight()+11);
            fail("missing argument validation");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("y must be a positive number less then " + w.getHeight()+ " (found " + (w.getHeight()+11) + ")");
        }
    }
    
    @Test
    public void get_fails_if_no_spot_selected() {
        YoctoWorld w = YoctoWorldFactory.random(3, 5);
        
        try {
            w.get();
            fail("missing select first check");
        } catch (IllegalStateException x) {
            then(x).hasMessage("no yoctospot seleceted, first select one with select(x,y)");
        }
    }
    
    @Test
    public void set_fails_if_no_spot_selected() {
        YoctoWorld w = YoctoWorldFactory.random(3, 5);
        
        try {
            w.set(NEUTRAL);
            fail("missing select first check");
        } catch (IllegalStateException x) {
            then(x).hasMessage("no yoctospot seleceted, first select one with select(x,y)");
        }
    }
    
    @Test
    public void fluent_set_sets_the_selected_yocto_ok() {
        YoctoWorld w = YoctoWorldFactory.empty(5, 5);
        
        then(w.select(1,1).set(ATTRACTOR)).isSameAs(w);
        then(w.get()).isEqualTo(ATTRACTOR);
        then(w.select(3,5).set(FRIEND)).isSameAs(w);
        then(w.get()).isEqualTo(FRIEND);
    }
    
    @Test
    public void fluent_moveTo_moves_slected_yocto() {
        final Yocto[][] MAP = new Yocto[][] {
            new Yocto[] { NEUTRAL, ATTRACTOR, FRIEND },
            new Yocto[] { REJECTOR, FRIEND, REJECTOR }
        };
        YoctoWorld w = new YoctoWorld(MAP);
        
        then(w.select(1,2).moveTo(1,1)).isSameAs(w);
        then(w.select(1,1).get()).isEqualTo(REJECTOR);
        then(w.select(2,1).get()).isEqualTo(ATTRACTOR);
        then(w.select(3,1).get()).isEqualTo(FRIEND);
        then(w.select(1,2).get()).isEqualTo(NEUTRAL);
        then(w.select(2,2).get()).isEqualTo(FRIEND);
        then(w.select(3,2).get()).isEqualTo(REJECTOR);
        
        
        then(w.select(3,2).moveTo(1,2)).isSameAs(w);
        then(w.select(1,1).get()).isEqualTo(REJECTOR);
        then(w.select(2,1).get()).isEqualTo(ATTRACTOR);
        then(w.select(3,1).get()).isEqualTo(FRIEND);
        then(w.select(1,2).get()).isEqualTo(REJECTOR);
        then(w.select(2,2).get()).isEqualTo(FRIEND);
        then(w.select(3,2).get()).isEqualTo(NEUTRAL);
    }
    
    @Test
    public void moveTo_can_not_move_a_NEUTRAL_spot() {
        final Yocto[][] MAP = new Yocto[][] {
            new Yocto[] { NEUTRAL, ATTRACTOR, FRIEND },
            new Yocto[] { REJECTOR, FRIEND, REJECTOR }
        };
        YoctoWorld w = new YoctoWorld(MAP);
        
        try {
            w.select(1,1).moveTo(2, 2);
            fail("missing state validation");
        } catch (IllegalStateException x) {
            then(x).hasMessage("the selected spot is empty, no yocto to move");
        }
    }
    
    @Test
    public void moveTo_can_not_move_to_a_not_NEUTRAL_spot() {
        final Yocto[][] MAP = new Yocto[][] {
            new Yocto[] { NEUTRAL, ATTRACTOR, FRIEND },
            new Yocto[] { REJECTOR, FRIEND, REJECTOR }
        };
        YoctoWorld w = new YoctoWorld(MAP);
        
        try {
            w.select(1,2).moveTo(2, 2);
            fail("missing state validation");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("the destination spot is not empty, the selected yocto can not be moved there");
        }
    }
    
    @Test
    public void moveTo_fails_if_no_spot_selected() {
        YoctoWorld w = YoctoWorldFactory.random(3, 5);
        
        try {
            w.moveTo(0, 0);
            fail("missing select first check");
        } catch (IllegalStateException x) {
            then(x).hasMessage("no yoctospot seleceted, first select one with select(x,y)");
        }
    }
    
    @Test
    public void moveTo_wrong_coordinates_ko() {
        YoctoWorld w = YoctoWorldFactory.random(3, 5);
        
        for (int I: Constants.NOT_POSITIVES_0_1_25_389_4567) {
            try {
                w.select(1,1).moveTo(I, 1);
                fail("missing argument validation");
            } catch (IllegalArgumentException x) {
                then(x).hasMessage("x must be a positive number less then " + w.getWidth()+ " (found " + I + ")");
            }
        }
        
        for (int I: Constants.NOT_POSITIVES_0_1_25_389_4567) {
            try {
                w.select(1,1).moveTo(1,I);
                fail("missing argument validation");
            } catch (IllegalArgumentException x) {
                then(x).hasMessage("y must be a positive number less then " + w.getHeight()+ " (found " + I + ")");
            }
        }
        
        try {
            w.select(1,1).moveTo(w.getWidth()+1,1);
            fail("missing argument validation");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("x must be a positive number less then " + w.getWidth()+ " (found " + (w.getWidth()+1) + ")");
        }
        
        try {
            w.select(1,1).moveTo(1, w.getHeight()+11);
            fail("missing argument validation");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("y must be a positive number less then " + w.getHeight()+ " (found " + (w.getHeight()+11) + ")");
        }
    }
    
    // --------------------------------------------------------- private methods
}
