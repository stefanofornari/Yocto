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

import java.util.Random;

/**
 *
 */
public class YoctoWorld {
    
    public static char N = ' ';
    public static char A = '+';
    public static char R = '-';
    public static char F = '=';
        
    
    public enum Yocto {
        
        NEUTRAL   (N), 
        ATTRACTOR (A),
        REJECTOR  (R), 
        FRIEND    (F);
        
        private char y;
        
        public static int ENERGY_ATTRACT  = +1;
        public static int ENERGY_REJECT   = -1;
        public static int ENERGY_NEUTRAL  =  0;
        
        Yocto(char y) {
            this.y = y;
        }
        
        public int e(Yocto y) {
            if (this == NEUTRAL) {
                return ENERGY_NEUTRAL;
            }
            
            switch (y) {
                case ATTRACTOR: return ENERGY_ATTRACT;
                case REJECTOR: return ENERGY_REJECT;
                case FRIEND: return (this == REJECTOR) ? ENERGY_ATTRACT : ENERGY_NEUTRAL;
            }
            
            return 0;
        }
        
        public int e() {
            switch (this) {
                case ATTRACTOR: return ENERGY_ATTRACT;
                case REJECTOR: return ENERGY_REJECT;
            }
            
            return 0;
        }
        
        public static Yocto valueOf(char c) {
            if (c == N) return NEUTRAL;
            if (c == A) return ATTRACTOR;
            if (c == R) return REJECTOR;
            if (c == F) return FRIEND;
                
            throw new IllegalArgumentException(c + " is not a valid Yocto");
        }
        
        public char toChar() {
            switch(this) {
                case ATTRACTOR: return A;
                case REJECTOR: return R;
                case FRIEND: return F;
            }
            
            return  N;
        }
    };
    
    private Random RND = new Random();
    private YoctoSpot selected = null;
    private Yocto[][] map;
    
    public YoctoWorld(int width, int height) {
        
        if (width < 1) {
            throw new IllegalArgumentException("width must be a postive number (found " + width + ")");
        }
        
        if (height < 1) {
            throw new IllegalArgumentException("height must be a postive number (found " + height + ")");
        }
        
        this.map = new Yocto[height][width];
        
        for (int y=0; y<height; ++y) {
            for (int x=0; x<width; ++x) {
                this.map[y][x] = Yocto.NEUTRAL;
            }
        }
        
    }
    
    public YoctoWorld(Yocto[][] map) {
        if (map == null) {
            throw new IllegalArgumentException("map can not be null");
        }
        
        if (map.length == 0) {
            throw new IllegalArgumentException("column length must be a positive integer");
        }
        
        if (map[0].length == 0) {
            throw new IllegalArgumentException("row length must be a positive integer");
        }
        
        //
        // check that all rows have the same size as the first one
        //
        int i = 1;
        for (Yocto[] row: map) {
            if (row.length != map[0].length) {
                throw new IllegalArgumentException(
                    String.format(
                        "row length mistmatch in row #%d (%d instead of %d)",
                        i, row.length, map[0].length
                    )
                );
            }
            ++i;
        }
        
        //
        // all good...
        //
        this.map = map;
    }
    
    public int getWidth() {
        return map[0].length;
    }
    
    public int getHeight() {
        return map.length;
    }
    
    public YoctoWorld select(int x, int y) {
        checkCoordinates(x, y);
        
        selected = new YoctoSpot(x, y); 
        return this;
    }
    
    public Yocto get() {
        checkSelected();
        return getYocto(selected.x, selected.y);
    }
    
    public YoctoWorld set(Yocto y) {
        checkSelected();
        
        map[selected.y-1][selected.x-1] = y;
        
        return this;
    }
    
    /**
     * 
     * @param x
     * @param y
     * 
     * @return this YoctoWorld
     * 
     * @throws IllegalArgumentException if the given coordinates are invalid or
     *         the destination spot is not empty
     * @throws IllegalStetException if no spots are selected or the selected
     *         spot is NEUTRAL
     */
    public YoctoWorld moveTo(int x, int y) {
        checkSelected();
        checkCoordinates(x, y);
        
        if (get() == Yocto.NEUTRAL) {
            throw new IllegalStateException("the selected spot is empty, no yocto to move");
        }
        
        if (getYocto(x, y) != Yocto.NEUTRAL) {
            throw new IllegalArgumentException ("the destination spot is not empty, the selected yocto can not be moved there");
        }
        
        move(selected, new YoctoSpot(x, y));
        
        return this;
    }
    
    public int getEnergy(int x, int y) {
        checkCoordinates(x, y);
                
        final Yocto Y = getYocto(x, y);
        
        //
        // if coordinates overlay an edge, continue on the other edge (the world 
        // is a spheroid)
        //
        
        return Y.e()            +
               Y.e(getNW(x, y)) +
               Y.e( getN(x, y)) +
               Y.e(getNE(x, y)) +
               Y.e( getE(x, y)) +
               Y.e(getSE(x, y)) +
               Y.e( getS(x, y)) +
               Y.e( getW(x, y)) +
               Y.e(getSW(x, y)) ;
    }
    
    public void evolve() {
        for (int y=1; y<=getHeight(); ++y) {
            for (int x=1; x<=getWidth(); ++x) {
                if (getYocto(x, y) != Yocto.NEUTRAL) {
                    int e = getEnergy(x, y);
                    if (e<0) {
                        moveToBestSpot(x, y, e);
                    }
                }
            }
        }
    }
    
    // ------------------------------------------------------- protected methods
    
    //
    // TODO: check coordinates
    //
    public Yocto getYocto(int x, int y) {
        return map[y-1][x-1];
    }
    
    public Yocto getYocto(YoctoSpot spot) {
        return getYocto(spot.x, spot.y);
    }
    
    // --------------------------------------------------------- private methods
    
    /**
     * 
     * @throws IllegalStateException if no spot has been selected
     */
    private void checkSelected() throws IllegalStateException {
        if (selected == null) {
            throw new IllegalStateException("no yoctospot seleceted, first select one with select(x,y)");
        }
    }
    
    /**
     * 
     * @param x
     * @param y
     * 
     * @throws IllegalArgumentException if one of the given coordinates is invalid
     */
    private void checkCoordinates(int x, int y) throws IllegalArgumentException {
        if ((x <= 0) || (x > getWidth())) {
            throw new IllegalArgumentException("x must be a positive number less then " + getWidth() + " (found " + x + ")");
        }
        if ((y <= 0) || (y > getHeight())) {
            throw new IllegalArgumentException("y must be a positive number less then " + getHeight() + " (found " + y + ")");
        }
    }
    
    private YoctoSpot getNWSpot(int x, int y) {
        return new YoctoSpot(
            (x == 1) ? getWidth() : x-1, (y == 1) ? getHeight() : y-1
        );
    }
    
    private Yocto getNW(int x, int y) {
        return getYocto(getNWSpot(x, y));
    }
    
    private YoctoSpot getNSpot(int x, int y) {
        return new YoctoSpot(
            x, (y == 1) ? getHeight() : y-1
        );
    }
    
    private Yocto getN(int x, int y) {
        return getYocto(getNSpot(x, y));
    }
    
    private YoctoSpot getNESpot(int x, int y) {
        return new YoctoSpot(
            (x == getWidth()) ? 1 : x+1, (y == 1) ? getHeight() : y-1
        );
    }
    
    private Yocto getNE(int x, int y) {
        return getYocto(getNESpot(x, y));
    }

    
    private YoctoSpot getESpot(int x, int y) {
        return new YoctoSpot(
            (x == getWidth()) ? 1 : x+1, y
        );
    }
    
    private Yocto getE(int x, int y) {
        return getYocto(getESpot(x, y));
    }
    
    private YoctoSpot getSESpot(int x, int y) {
        return new YoctoSpot(
            (x == getWidth()) ? 1 : x+1, (y == getHeight()) ? 1 : y+1
        );
    }
    
    private Yocto getSE(int x, int y) {
        return getYocto(getSESpot(x, y));
    }
    
    private YoctoSpot getSSpot(int x, int y) {
        return new YoctoSpot(
            x, (y == getHeight()) ? 1 : y+1
        );
    }
    
    private Yocto getS(int x, int y) {
        return getYocto(getSSpot(x, y));
    }
    
    private YoctoSpot getSWSpot(int x, int y) {
        return new YoctoSpot(
            (x == 1) ? getWidth() : x-1, (y == getHeight()) ? 1 : y+1
        );
    }
    
    private Yocto getSW(int x, int y) {
        return getYocto(getSWSpot(x, y));
    }
        
    private YoctoSpot getWSpot(int x, int y) {
        return new YoctoSpot(
            (x == 1) ? getWidth() : x-1, y
        );
    }
    
    private Yocto getW(int x, int y) {
        return getYocto(getWSpot(x, y));
    }
    
    private void move(YoctoSpot from, YoctoSpot to) {
        if ((from.x != to.x) || (from.y != to.y)) {
            map[to.y-1][to.x-1] = map[from.y-1][from.x-1];
            map[from.y-1][from.x-1] = Yocto.NEUTRAL;
        }
    }
    
    private void moveToBestSpot(int x, int y, int e) {
        int eMax = e;
        
        YoctoSpot from = new YoctoSpot(x, y);
        YoctoSpot to;
        
        //
        // North West
        //
        to = getNWSpot(x,y);
        if (getYocto(to) == Yocto.NEUTRAL) {
            move(from, to);
            e = getEnergy(to.x, to.y);
            if (e == eMax) {
                // same energy, randomly stay or move back 
                if (RND.nextBoolean()) {
                    from = to;
                } else {
                    move(to, from); 
                }
            } else if (e > eMax) {
                eMax = e;
                from = to;
            } else {
                move(to, from); // less energy, bring it back...
            }
        }
        
        //
        // North
        //
        to = getNSpot(x,y);
        if (getYocto(to) == Yocto.NEUTRAL) {
            move(from, to);
            e = getEnergy(to.x, to.y);
            if (e == eMax) {
                // same energy, randomly stay or move back 
                if (RND.nextBoolean()) {
                    from = to;
                } else {
                    move(to, from); 
                }
            } else if (e > eMax) {
                eMax = e;
                from = to;
            } else {
                move(to, from); // less energy, bring it back...
            }
        }
        
        //
        // North East
        //
        to = getNESpot(x, y);
        if (getYocto(to) == Yocto.NEUTRAL) {
            move(from, to);
            e = getEnergy(to.x, to.y);
            if (e == eMax) {
                // same energy, randomly stay or move back 
                if (RND.nextBoolean()) {
                    from = to;
                } else {
                    move(to, from);
                }
            } else if (e > eMax) {
                eMax = e;
                from = to;
            } else {
                move(to, from); // less energy, bring it back...
            }
        }
        
        //
        // East
        //
        to = getESpot(x, y);
        if (getYocto(to) == Yocto.NEUTRAL) {
            move(from, to);
            e = getEnergy(to.x, to.y);
            if (e == eMax) {
                // same energy, randomly stay or move back 
                if (RND.nextBoolean()) {
                    from = to;
                } else {
                    move(to, from); 
                }
            } else if (e > eMax) {
                eMax = e;
                from = to;
            } else {
                move(to, from); // less energy, bring it back...
            }
        }
        
        //
        // South East
        //
        to = getSWSpot(x, y);
        if (getYocto(to) == Yocto.NEUTRAL) {
            move(from, to);
            e = getEnergy(to.x, to.y);
            if (e == eMax) {
                // same energy, randomly stay or move back 
                if (RND.nextBoolean()) {
                    from = to;
                } else {
                    move(to, from); 
                }
            } else if (e > eMax) {
                eMax = e;
                from = to;
            } else {
                move(to, from); // less energy, bring it back...
            }
        }
        
        //
        // South
        //
        to = getSSpot(x, y);
        if (getYocto(to) == Yocto.NEUTRAL) {
            move(from, to);
            e = getEnergy(to.x, to.y);
            if (e == eMax) {
                // same energy, randomly stay or move back 
                if (RND.nextBoolean()) {
                    from = to;
                } else {
                    move(to, from); 
                }
            } else if (e > eMax) {
                eMax = e;
                from = to;
            } else {
                move(to, from); // less energy, bring it back...
            }
        }
        
        //
        // South West
        //
        to = getSWSpot(x, y);
        if (getYocto(to) == Yocto.NEUTRAL) {
            move(from, to);
            e = getEnergy(to.x, to.y);
            if (e == eMax) {
                // same energy, randomly stay or move back 
                if (RND.nextBoolean()) {
                    from = to;
                } else {
                    move(to, from); 
                }
            } else if (e > eMax) {
                eMax = e;
                from = to;
            } else {
                move(to, from); // less energy, bring it back...
            }
        }
        
        //
        // West
        //
        to = getWSpot(x, y);
        if (getYocto(to) == Yocto.NEUTRAL) {
            move(from, to);
            e = getEnergy(to.x, to.y);
            if (e == eMax) {
                // same energy, randomly stay or move back 
                if (RND.nextBoolean()) {
                    from = to;
                } else {
                    move(to, from); 
                }
            } else if (e > eMax) {
                eMax = e;
                from = to;
            } else {
                move(to, from); // less energy, bring it back...
            }
        }
    }
}
