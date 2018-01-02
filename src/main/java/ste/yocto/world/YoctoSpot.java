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
package ste.yocto.world;

/**
 *
 */
public class YoctoSpot {
    public int x;
    public int y;
    
    public YoctoSpot(int x, int y) {
        this.x = x; this.y = y;
    }
    
    public YoctoSpot(YoctoSpot s) {
        this.x = s.x;
        this.y = s.y;
    }
}
