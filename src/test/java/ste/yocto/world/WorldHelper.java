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
package ste.yocto.world;

import static org.assertj.core.api.BDDAssertions.then;

/**
 *
 * @author ste
 */
public class WorldHelper {
    public static void thenWorldIsEqualTo(YoctoWorld w1, YoctoWorld w2) {
        for (int y=1; y<=w1.getHeight(); ++y) {
            for (int x=1; x<=w1.getWidth(); ++x) {
                System.out.println(x + "," + y);
                then(w1.getYocto(x, y)).isEqualTo(w2.getYocto(x, y));
            }
            System.out.print('\n');
        }
    }
    
    public static void printWorld(YoctoWorld w) {
        System.out.println("\n+----------+");
        for (int y=1; y<=w.getHeight(); ++y) {
            for (int x=1; x<=w.getWidth(); ++x) {
                System.out.print(w.getYocto(x, y).toChar() + " ");
            }
            System.out.print('\t');
            for (int x=1; x<=w.getWidth(); ++x) {
                System.out.print(w.getEnergy(x, y) + " ");
            }
            System.out.print('\n');
        }
        System.out.println("\n+----------+");
    }
}
