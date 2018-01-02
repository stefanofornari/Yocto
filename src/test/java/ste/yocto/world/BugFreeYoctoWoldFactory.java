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

import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Test;
import ste.xtest.math.ArrayRandomStub;
import ste.xtest.reflect.PrivateAccess;
import ste.yocto.world.YoctoWorld.Yocto;
import static ste.yocto.world.YoctoWorld.Yocto.NEUTRAL;

/**
 *
 */
public class BugFreeYoctoWoldFactory {
    
    @Test
    public void create_empty_world_ok() {
        YoctoWorld w = YoctoWorldFactory.empty(1, 1);
        
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
    public void create_world_from_map_ok() {
        final byte[][] MAP = new byte[][] {
            new byte[] { ' ', '+', '=' },
            new byte[] { '-', '=', '-' },
            new byte[] { '+', ' ', ' ' }
        };
        
        YoctoWorld w = YoctoWorldFactory.fromBytes(MAP);
        thenWorldEqualsTo(w, MAP);
    }
    
    @Test
    public void create_world_with_random_map() throws Exception {
        //
        // 0 -> NEUTRAL
        // 1 -> ATTRACTOR
        // 2 -> REJECTOR
        // 3 -> FRIENd
        //
        final ArrayRandomStub R =
            new ArrayRandomStub(new int[] { 0, 1, 2, 3, 3, 1, 2, 0, 2, 1, 1, 0, 0, 3, 0, 1, 1, 3, 3 });
        
        final byte[][] MAP1 = new byte[][] {
            new byte[] { ' ', '+', '-' },
            new byte[] { '=', '=', '+' },
            new byte[] { '-', ' ', '-' }
        };
        final byte[][] MAP2 = new byte[][] {
            new byte[] { '+', '+', ' ', ' ', '=' },
            new byte[] { ' ', '+', '+', '=', '=' }
        };
        
        PrivateAccess.setStaticValue(YoctoWorldFactory.class, "RND", R);
        thenWorldEqualsTo(YoctoWorldFactory.random(3, 3), MAP1);
        thenWorldEqualsTo(YoctoWorldFactory.random(5, 2), MAP2);
        
    }
    
    @Test
    public void create_world_with_string_map() throws Exception {
        final String[] MAP1 = new String[] {
            "+- -  --++",
            "-+-+--+++-",
            "  -++  ++-",
            "- +- --- -",
            "+--- --+--",
            " +- -- ---",
            "+-+-- +--+",
            "+ --- - --",
            " -+---+ --",
            " - - - +  "
        };
        
        final String[] MAP2 = new String[] {
            "--+ ++ ---",
            "-- - ---- ",
            "++ --++-- ",
            " +- +-  + ",
            "- - +-----",
            "+- + -+ --",
            "- -  +-  -",
            "-+---+-+- ",
            "+ --- -+ -",
            "+ + +-+-++"
        };
        
        YoctoWorld w = YoctoWorldFactory.fromStrings(MAP1);
        for (int y=1; y<= MAP1.length; ++y) {
            byte[] row = MAP1[y-1].getBytes();
            for (int x=1; x<=row.length; ++x) {
                then(w.getYocto(x, y)).isEqualTo(Yocto.valueOf((char)row[x-1]));
            }
        }
        
    }
    
    // --------------------------------------------------------- private methods
    
    private void thenWorldEqualsTo(YoctoWorld w, byte[][] map) {
        for (int y=1; y<map.length; ++y) {
            for (int x=1; x<=map[0].length; ++x) {
                then(w.getYocto(x, y)).isEqualTo(Yocto.valueOf((char)map[y-1][x-1]));
            }
        }
    }
            
}
