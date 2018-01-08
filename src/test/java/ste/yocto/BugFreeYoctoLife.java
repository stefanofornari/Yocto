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
package ste.yocto;

import ste.yocto.world.YoctoWorldFactory;
import static org.assertj.core.api.BDDAssertions.then;
import ste.yocto.world.YoctoWorld;
import org.junit.Test;
import ste.xtest.math.ArrayRandomStub;
import ste.xtest.reflect.PrivateAccess;
import static ste.yocto.world.YoctoWorld.Yocto.ATTRACTOR;
import static ste.yocto.world.YoctoWorld.Yocto.FRIEND;
import static ste.yocto.world.YoctoWorld.Yocto.NEUTRAL;
import static ste.yocto.world.YoctoWorld.Yocto.REJECTOR;

/**
 *
 * @author ste
 */
public class BugFreeYoctoLife {
    
    private static final String[] MAP1 = new String[] {
        "+- =  =-++",
        "-+=+=-+++=",
        "  =++  ++=",
        "= += -== =",
        "+=-= --+--",
        " += =- =--",
        "+=+== +==+",
        "+ --- - -=",
        " =+--=+ =-",
        " = = - +  "
    };

    private static final String[] MAP2 = new String[] {
        "+- =  =-++",
        "-+=+=-+++=",
        "  =++=-++=",
        "= +=-  ==-",
        "+=-=  --  ",
        " +===-= --",
        "+=+--++= +",
        "++-  =-=-=",
        "=== - +  -",
        "  -  -=+= "
    };
    
    private static final String[] MAP3 = new String[] {
        "+- =  =-++",
        "-+=+=-+++=",
        "  =++=-++=",
        "= +=- -==-",
        "+=-==  -- ",
        " +== -== +",
        "+=+--++ -=",
        "++-  =- - ",
        "==   -+=- ",
        "= -  -=+= "
    };
    
    @Test
    public void random_move_if_same_energy_a() throws Exception {
        YoctoWorld W = YoctoWorldFactory.fromStrings(new String[] {
            "-  ",
            " + ",
            "  -"
        });
        
        ArrayRandomStub R = new ArrayRandomStub(new int[] {1, 0});
        PrivateAccess.setInstanceValue(W, "RND", R);
        
        W.evolve();
        then(W.getYocto(1, 3)).isEqualTo(ATTRACTOR);
        then(W.getYocto(2, 2)).isEqualTo(NEUTRAL);
        
        W = YoctoWorldFactory.fromStrings(new String[] {
            "-  ",
            " + ",
            "  -"
        });
        PrivateAccess.setInstanceValue(W, "RND", R);
        
        W.evolve();
        then(W.getYocto(3, 1)).isEqualTo(ATTRACTOR);
        then(W.getYocto(2, 2)).isEqualTo(NEUTRAL);
    }
    
    @Test
    public void random_move_if_same_energy_f() throws Exception {
        final YoctoWorld W = YoctoWorldFactory.fromStrings(new String[] {
            "   ",
            " = ",
            "  -"
        });
        
        ArrayRandomStub R = new ArrayRandomStub(new int[] {1, 0, 0, 0, 0, 0});
        PrivateAccess.setInstanceValue(W, "RND", R);
        
        W.evolve();
        then(W.getYocto(2, 1)).isEqualTo(FRIEND);
        then(W.getYocto(2, 2)).isEqualTo(REJECTOR);        
    }
    
    @Test
    public void random_move_if_same_energy_r() throws Exception {
        final YoctoWorld W = YoctoWorldFactory.fromStrings(new String[] {
            "   ",
            " - ",
            "   "
        });
        
        ArrayRandomStub R = new ArrayRandomStub(new int[] {1, 1, 0, 1, 0, 0, 1, 1});
        PrivateAccess.setInstanceValue(W, "RND", R);
        
        W.evolve();
        then(W.getYocto(1, 2)).isEqualTo(REJECTOR);
        then(W.getYocto(2, 2)).isEqualTo(NEUTRAL);
        
        W.evolve();
        then(W.getYocto(1, 3)).isEqualTo(REJECTOR);
        then(W.getYocto(1, 2)).isEqualTo(NEUTRAL);
        
    }
    
    @Test
    public void move_across_boundaries_r() throws Exception {
        final YoctoWorld W = YoctoWorldFactory.fromStrings(new String[] {
            "-  ",
            "   ",
            "   "
        });
        
        ArrayRandomStub R = new ArrayRandomStub(new int[] {
            //
            // do not make it move more than once (it will be computed twice, 
            // once in its original postion, and once in its new position
            //
            1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0
        });
        PrivateAccess.setInstanceValue(W, "RND", R);
        
        W.evolve();
        then(W.getYocto(3, 3)).isEqualTo(REJECTOR);
        then(W.getYocto(1, 1)).isEqualTo(NEUTRAL);
        
        W.select(3, 3).moveTo(1, 1); W.evolve();
        then(W.getYocto(1, 3)).isEqualTo(REJECTOR);
        then(W.getYocto(1, 1)).isEqualTo(NEUTRAL);
        
        W.select(1, 3).moveTo(1, 1); W.evolve();
        then(W.getYocto(2, 3)).isEqualTo(REJECTOR);
        then(W.getYocto(1, 1)).isEqualTo(NEUTRAL);
        
        W.select(2, 3).moveTo(1, 1); W.evolve();
        then(W.getYocto(3, 2)).isEqualTo(REJECTOR);
        then(W.getYocto(1, 1)).isEqualTo(NEUTRAL);
        
        W.select(3, 2).moveTo(1, 1); W.evolve();
        then(W.getYocto(3, 1)).isEqualTo(REJECTOR);
        then(W.getYocto(1, 1)).isEqualTo(NEUTRAL);
    }
    
    
    @Test
    public void bring_yocto_to_life() throws Exception {
        final YoctoWorld W1 = YoctoWorldFactory.fromStrings(MAP1);
        final YoctoWorld W2 = YoctoWorldFactory.fromStrings(MAP2);
        final YoctoWorld W3 = YoctoWorldFactory.fromStrings(MAP3);
        
        ArrayRandomStub R = new ArrayRandomStub(new int[] {1, 1, 0, 1, 0, 0, 1, 0});
        PrivateAccess.setInstanceValue(W1, "RND", R);
        
        printWorld(W1);
        
        W1.evolve();
        printWorld(W1);
        thenWorldIsEqualTo(W1, W2);
        
        W1.evolve();
        printWorld(W1);
        thenWorldIsEqualTo(W1, W3);
        
        printWorld(W1);
    }
    
    // -------------------------------------------------------------------------

    private void printWorld(YoctoWorld w) {
        System.out.println("\n+----------+");
        for (int y=1; y<=w.getHeight(); ++y) {
            for (int x=1; x<=w.getWidth(); ++x) {
                System.out.print(w.getEnergy(x, y) + " ");
            }
            System.out.print('\n');
        }
        System.out.println("\n+----------+");
    }
    
    private void thenWorldIsEqualTo(YoctoWorld w1, YoctoWorld w2) {
        for (int y=1; y<=w1.getHeight(); ++y) {
            for (int x=1; x<=w1.getWidth(); ++x) {
                System.out.println(x + "," + y);
                then(w1.getYocto(x, y)).isEqualTo(w2.getYocto(x, y));
            }
            System.out.print('\n');
        }
    }
    
    
}
