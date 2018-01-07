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
import static ste.yocto.world.YoctoWorld.Yocto.ENERGY_ATTRACT;
import static ste.yocto.world.YoctoWorld.Yocto.ENERGY_REJECT;

/**
 *
 */
public class BugFreeYoctoWorldEnergy {
    
    private static final byte[][] WORLD_NOT_ADJACENT_A_MAP = new byte[][] {
        "          ".getBytes(),
        " + + +    ".getBytes(),
        "        + ".getBytes(),
        "     +    ".getBytes(),
        "  +     + ".getBytes(),
        "          ".getBytes(),
        " +   +    ".getBytes(),
        "       +  ".getBytes(),
        "    +     ".getBytes(),
        "          ".getBytes()
    };
    
    private static final byte[][] WORLD_NOT_ADJACENT_R_MAP = new byte[][] {
        "          ".getBytes(),
        " - - -    ".getBytes(),
        "        - ".getBytes(),
        "     -    ".getBytes(),
        "  -     - ".getBytes(),
        "          ".getBytes(),
        " -   -    ".getBytes(),
        "       -  ".getBytes(),
        "    -     ".getBytes(),
        "          ".getBytes()
    };
    
    private static final byte[][] WORLD_NOT_ADJACENT_F_MAP = new byte[][] {
        "          ".getBytes(),
        " = = =    ".getBytes(),
        "        = ".getBytes(),
        "     =    ".getBytes(),
        "  =     = ".getBytes(),
        "          ".getBytes(),
        " =   =    ".getBytes(),
        "       =  ".getBytes(),
        "    =     ".getBytes(),
        "          ".getBytes()
    };
    
    private static final byte[][] WORLD_NOT_ADJACENT_ARF_MAP = new byte[][] {
        "+ + =     ".getBytes(),
        "      -   ".getBytes(),
        "   +      ".getBytes(),
        "+      =  ".getBytes(),
        "          ".getBytes(),
        "- +   -   ".getBytes(),
        "          ".getBytes(),
        "    =   - ".getBytes(),
        " +    -   ".getBytes(),
        "          ".getBytes()
    };
    
    
    private static final byte[][] WORLD_ADJACENT_A_MAP = new byte[][] {
        "          ".getBytes(),
        " +++      ".getBytes(),
        "     +  + ".getBytes(),
        "     ++   ".getBytes(),
        "  ++    + ".getBytes(),
        "  ++    + ".getBytes(),
        " +    +++ ".getBytes(),
        "      +++ ".getBytes(),
        " + +  +++ ".getBytes(),
        "          ".getBytes()
    };
    
    private static final int[][] WORLD_ADJACENT_A_ENERGY = new int[][] {
        new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        new int[] { 0, 2, 3, 2, 0, 0, 0, 0, 0, 0 },
        new int[] { 0, 0, 0, 0, 0, 3, 0, 0, 1, 0 },
        new int[] { 0, 0, 0, 0, 0, 3, 3, 0, 0, 0 },
        new int[] { 0, 0, 4, 4, 0, 0, 0, 0, 2, 0 },
        new int[] { 0, 0, 5, 4, 0, 0, 0, 0, 4, 0 },
        new int[] { 0, 2, 0, 0, 0, 0, 4, 7, 5, 0 },
        new int[] { 0, 0, 0, 0, 0, 0, 6, 9, 6, 0 },
        new int[] { 0, 1, 0, 1, 0, 0, 4, 6, 4, 0 },
        new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }
    };
    
    private static final byte[][] WORLD_ADJACENT_BORDER_A_MAP = new byte[][] {
        "+++ +++  +".getBytes(),
        "++++      ".getBytes(),
        "     +  + ".getBytes(),
        "+    ++  +".getBytes(),
        "+       + ".getBytes(),
        "  ++    + ".getBytes(),
        " +    +++ ".getBytes(),
        "        ++".getBytes(),
        " + +  +++ ".getBytes(),
        "++++++++++".getBytes()
    };
    
    private static final int[][] WORLD_ADJACENT_BORDER_A_ENERGY = new int[][] {
        new int[] { 8, 9, 8, 0, 6, 6, 5, 0, 0, 6 },
        new int[] { 5, 6, 5, 4, 0, 0, 0, 0, 0, 0 },
        new int[] { 0, 0, 0, 0, 0, 3, 0, 0, 2, 0 },
        new int[] { 3, 0, 0, 0, 0, 3, 3, 0, 0, 5 },
        new int[] { 3, 0, 0, 0, 0, 0, 0, 0, 3, 0 },
        new int[] { 0, 0, 3, 2, 0, 0, 0, 0, 4, 0 },
        new int[] { 0, 2, 0, 0, 0, 0, 2, 5, 5, 0 },
        new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 6, 4 },
        new int[] { 0, 4, 0, 4, 0, 0, 5, 7, 7, 0 },
        new int[] { 7, 7, 7, 6, 6, 7, 7, 7, 6, 6 }
    };
    
    private static final byte[][] WORLD_ADJACENT_BORDER_AR_MAP = new byte[][] {
        " -     ---".getBytes(),
        " +++      ".getBytes(),
        "     +  + ".getBytes(),
        "    -++ - ".getBytes(),
        "- +     + ".getBytes(),
        "- ++     +".getBytes(),
        "        - ".getBytes(),
        " +++  +   ".getBytes(),
        " +-+  +-+ ".getBytes(),
        " +++      ".getBytes()
    };
    
    private static final int[][] WORLD_ADJACENT_BORDER_AR_ENERGY = new int[][] {
        new int[] {  0,  3,  0,  0,  0,  0,  0, -2, -3, -2 },
        new int[] {  0,  1,  2,  2,  0,  0,  0,  0,  0,  0 },
        new int[] {  0,  0,  0,  0,  0,  2,  0,  0,  0,  0 },
        new int[] {  0,  0,  0,  0,  1,  2,  3,  0,  1,  0 },
        new int[] { -1,  0,  3,  0,  0,  0,  0,  0,  1,  0 },
        new int[] { -1,  0,  3,  3,  0,  0,  0,  0,  0, -1 },
        new int[] {  0,  0,  0,  0,  0,  0,  0,  0,  0,  0 },
        new int[] {  0,  2,  4,  2,  0,  0,  1,  0,  0,  0 },
        new int[] {  0,  4,  7,  4,  0,  0,  1,  2,  0,  0 },
        new int[] {  0,  1,  3,  2,  0,  0,  0,  0,  0,  0 }
    };
    
    private static final byte[][] WORLD_ADJACENT_BORDER_ARF_MAP = new byte[][] {
        "=-     =+- -     ---".getBytes(),
        " +++       -  +++   ".getBytes(),
        "  =  +  +  -    =-=-".getBytes(),
        "    -+= -  -     +  ".getBytes(),
        "- +     =  -   + -=-".getBytes(),
    };
    
    private static final int[][] WORLD_ADJACENT_BORDER_ARF_ENERGY = new int[][] {
        new int[] { -3,  2,  0,  0,  0,  0,  0,  1,  0,  1,  0, -3,  0,  0,  0,  0,  0, -1, -4, -2 },
        new int[] {  0,  1,  2,  2,  0,  0,  0,  0,  0,  0,  0, -3,  0,  0,  2,  3,  0,  0,  0,  0 },
        new int[] {  0,  0,  3,  0,  0,  1,  0,  0,  0,  0,  0, -3,  0,  0,  0,  0,  2,  3, -1,  0 },
        new int[] {  0,  0,  0,  0,  1,  1,  2,  0,  1,  0,  0, -3,  0,  0,  0,  0,  0, -1,  0,  0 },
        new int[] { -3,  0,  0,  0,  0,  0,  0,  0, -1,  0,  0, -3,  0,  0,  0,  1,  0, -1, -4, -2 }
    };
    
    // -------------------------------------------------------------------------
    // ENERGY
    // -------------------------------------------------------------------------
    
    @Test
    public void empty_world() {
        final YoctoWorld[] WORLDS = new YoctoWorld[] {
            new YoctoWorld(10, 10), new YoctoWorld(25, 10), new YoctoWorld(100, 1)
        };
        
        for (YoctoWorld W: WORLDS) {
            for (int y=1; y<=W.getHeight(); ++y) {
                for (int x=1; x<=W.getWidth(); ++x) {
                    then(W.getEnergy(x, y)).isZero();
                }
            }
        }
    }
    
    @Test
    public void only_not_adjacent_A_yoctos() {
        final YoctoWorld W = givenWorld(WORLD_NOT_ADJACENT_A_MAP);
        for (int y=1; y<=W.getHeight(); ++y) {
            for (int x=1; x<=W.getWidth(); ++x) {
                System.out.println("(" + x + "," + y + ")");
                then(W.getEnergy(x, y)).isEqualTo((W.getYocto(x, y) == YoctoWorld.Yocto.ATTRACTOR) ? ENERGY_ATTRACT : 0
                );
            }
        }
    }
    
    @Test
    public void only_not_adjacent_R_yoctos() {
        final YoctoWorld W = givenWorld(WORLD_NOT_ADJACENT_R_MAP);
        for (int y=1; y<=W.getHeight(); ++y) {
            for (int x=1; x<=W.getWidth(); ++x) {
                System.out.println("(" + x + "," + y + ")");
                then(W.getEnergy(x, y)).isEqualTo((W.getYocto(x, y) == YoctoWorld.Yocto.REJECTOR) ? ENERGY_REJECT : 0
                );
            }
        }
    }
    
    @Test
    public void only_not_adjacent_F_yoctos() {
        final YoctoWorld W = givenWorld(WORLD_NOT_ADJACENT_F_MAP);
        for (int y=1; y<=W.getHeight(); ++y) {
            for (int x=1; x<=W.getWidth(); ++x) {
                System.out.println("(" + x + "," + y + ")");
                then(W.getEnergy(x, y)).isZero();
            }
        }
    }
    
    @Test
    public void only_not_adjacent_ARF_yoctos() {
        final YoctoWorld W = givenWorld(WORLD_NOT_ADJACENT_ARF_MAP);
        
        for (int y=1; y<=W.getHeight(); ++y) {
            for (int x=1; x<=W.getWidth(); ++x) {
                then(W.getEnergy(x, y)).isEqualTo(W.getYocto(x, y).e());
            }
        }
    }
    
    @Test
    public void only_a_with_adjacents() {
        final YoctoWorld W = givenWorld(WORLD_ADJACENT_A_MAP);
        
        for (int y=1; y<=WORLD_ADJACENT_A_MAP.length; ++y) {
            for (int x=1; x<=WORLD_ADJACENT_A_MAP[0].length; ++x) {
                System.out.println("(" + x + "," + y + ")");
                then(W.getEnergy(x, y)).isEqualTo(WORLD_ADJACENT_A_ENERGY[y-1][x-1]);
            }
        }
    }
    
    @Test
    public void energy_with_adjacents_and_border() {
        final YoctoWorld[] WORLDS = new YoctoWorld[] {
            givenWorld(WORLD_ADJACENT_BORDER_A_MAP),
            givenWorld(WORLD_ADJACENT_BORDER_AR_MAP),
            givenWorld(WORLD_ADJACENT_BORDER_ARF_MAP)
        };
        
        final int[][][] ENERGY = new int[][][] {
            WORLD_ADJACENT_BORDER_A_ENERGY,
            WORLD_ADJACENT_BORDER_AR_ENERGY,
            WORLD_ADJACENT_BORDER_ARF_ENERGY
        };
        
        for (int i=0; i<WORLDS.length; ++i) {
            System.out.println("-" + i + "-");
            for (int y=1; y<=WORLDS[i].getHeight(); ++y) {
                System.out.println("--");
                for (int x=1; x<=WORLDS[i].getWidth(); ++x) {
                    System.out.println(i + ": (" + x + "," + y + ")");
                    then(WORLDS[i].getEnergy(x, y)).isEqualTo(ENERGY[i][y-1][x-1]);
                }
            }
        }
    }
    
    @Test
    public void energy_with_wrong_coordinates_throws_error() {
        YoctoWorld w = YoctoWorldFactory.random(3, 5);
        
        for (int I: Constants.NOT_POSITIVES_0_1_25_389_4567) {
            try {
                w.getEnergy(I,1);
                fail("missing argument validation");
            } catch (IllegalArgumentException x) {
                then(x).hasMessage("x must be a positive number less then " + w.getWidth()+ " (found " + I + ")");
            }
        }
        
        for (int I: Constants.NOT_POSITIVES_0_1_25_389_4567) {
            try {
                w.getEnergy(1,I);
                fail("missing argument validation");
            } catch (IllegalArgumentException x) {
                then(x).hasMessage("y must be a positive number less then " + w.getHeight()+ " (found " + I + ")");
            }
        }
        
        try {
            w.getEnergy(w.getWidth()+1,1);
            fail("missing argument validation");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("x must be a positive number less then " + w.getWidth()+ " (found " + (w.getWidth()+1) + ")");
        }
        
        try {
            w.getEnergy(1, w.getHeight()+11);
            fail("missing argument validation");
        } catch (IllegalArgumentException x) {
            then(x).hasMessage("y must be a positive number less then " + w.getHeight()+ " (found " + (w.getHeight()+11) + ")");
        }
    }

    // --------------------------------------------------------- private methods
    
    private YoctoWorld givenWorld(byte[][] map) {
        return YoctoWorldFactory.fromBytes(map);
    }
    
}
