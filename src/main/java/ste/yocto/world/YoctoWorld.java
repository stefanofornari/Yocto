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
                
        final int MAXW = getWidth()-1;
        final int MAXH = getHeight()-1;
        
        // the grid is 1-based for users
        --x; --y;
        
        final Yocto Y = map[y][x];
        
        int e = Y.e();
        
        if (x>0 && y>0) {
            e += Y.e(map[y-1][x-1]);
        }
        if (x>0) {
            e += Y.e(map[y][x-1]);
        }
        if (x>0 && y<MAXH) {
            e += Y.e(map[y+1][x-1]);
        }
        if (y>0) {
            e += Y.e(map[y-1][x]);
        }
        if (y<MAXH) {
            e += Y.e(map[y+1][x]);
        }
        if (x<MAXW && y>0) {
            e += Y.e(map[y-1][x+1]);
        }
        if (x<MAXW) {
            e += Y.e(map[y][x+1]);
        }
        if (x<MAXW && y<MAXH) {
            e += Y.e(map[y+1][x+1]);
        }
        
        return e;
    }
    
    public void evolve() {
        for (int y=1; y<=getHeight(); ++y) {
            for (int x=1; x<=getWidth(); ++x) {
                int e = getEnergy(x, y);
                if ((getYocto(x, y) != Yocto.NEUTRAL) && (e < 0)) {
                    moveToBestSpot(x, y, e);
                }
            }
        }
    }
    
    // ------------------------------------------------------- protected methods
    
    public Yocto getYocto(int x, int y) {
        return map[y-1][x-1];
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
    
    private void move(YoctoSpot from, YoctoSpot to) {
        if ((from.x != to.x) || (from.y != to.y)) {
            map[to.y-1][to.x-1] = map[from.y-1][from.x-1];
            map[from.y-1][from.x-1] = Yocto.NEUTRAL;
        }
    }
    
    private void moveToBestSpot(int x, int y, int e) {
        final int WIDTH  =  getWidth();
        final int HEIGHT = getHeight();
        
        int eMax;
        
        YoctoSpot from = new YoctoSpot(x, y);
        YoctoSpot to;
        
        eMax = e;
        
        //
        // North West
        //
            if ((x>1 && y>1) && (getYocto(x-1, y-1) == Yocto.NEUTRAL)) {
            to = new YoctoSpot(x-1, y-1);
            move(from, to);
            e = getEnergy(x-1, y-1);
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
        if ((y>1) && (getYocto(x, y-1) == Yocto.NEUTRAL)) {
            to = new YoctoSpot(x, y-1);
            move(from, to);
            e = getEnergy(x, y-1);
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
        if ((x<WIDTH && y>1) && (getYocto(x+1, y-1) == Yocto.NEUTRAL)) {
            to = new YoctoSpot(x+1, y-1);
            move(from, to);
            e = getEnergy(x+1, y-1);
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
        if ((x<WIDTH) && (getYocto(x+1, y) == Yocto.NEUTRAL)) {
            to = new YoctoSpot(x+1, y);
            move(from, to);
            e = getEnergy(x+1, y);
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
        if ((x<WIDTH && y<HEIGHT) && (getYocto(x+1, y+1) == Yocto.NEUTRAL)) {
            to = new YoctoSpot(x+1, y+1);
            move(from, to);
            e = getEnergy(x+1, y+1);
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
        if ((y<HEIGHT) && (getYocto(x, y+1) == Yocto.NEUTRAL)) {
            to = new YoctoSpot(x, y+1);
            move(from, to);
            e = getEnergy(x, y+1);
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
        if ((x>1 && y<WIDTH) && (getYocto(x-1, y+1) == Yocto.NEUTRAL)) {
            to = new YoctoSpot(x-1, y+1);
            move(from, to);
            e = getEnergy(x-1, y+1);
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
        if ((x>1) && (getYocto(x-1, y) == Yocto.NEUTRAL)) {
            to = new YoctoSpot(x-1, y);
            move(from, to);
            e = getEnergy(x-1, y);
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
