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

import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import org.apache.commons.io.IOUtils;
import ste.yocto.world.YoctoWorld.Yocto;

/**
 * TODO: argument validation
 */
public class YoctoWorldFactory {
    
    private static Random RND = new Random();
    
    public static YoctoWorld empty(int width, int height) {
        return new YoctoWorld(width, height);
    }
    
    public static YoctoWorld random(int width, int height) {
        final Yocto[] VALUES = Yocto.values();
        
        Yocto[][] map = new Yocto[height][width];
        
        for (int y=0; y<height; ++y) {
            for (int x=0; x<width; ++x) {
                map[y][x] = VALUES[RND.nextInt(4)];
            }
        }
        
        return new YoctoWorld(map);
    }
    
    public static YoctoWorld fromBytes(byte[][] bytes) {
        Yocto[][] map = new Yocto[bytes.length][bytes[0].length];
        
        for (int y=0; y<bytes.length; ++y) {
            for (int x=0; x<bytes[0].length; ++x) {
                map[y][x] = Yocto.valueOf((char)bytes[y][x]);
            }
        }
        
        return new YoctoWorld(map);
    }
    
    public static YoctoWorld fromStrings(String[] rows) {
        Yocto[][] map = new Yocto[rows.length][rows[0].length()];
        
        for (int y=0; y<map.length; ++y) {
            byte[] row = rows[y].getBytes();
            for (int x=0; x<row.length; ++x) {
                map[y][x] = Yocto.valueOf((char)row[x]);
            }
        }
        
        return new YoctoWorld(map);
    }
    
    public static YoctoWorld fromFile(String file) throws IOException {
        String content = IOUtils.toString(new FileReader(file));
        
        return YoctoWorldFactory.fromStrings(
             content.split("\n")
        );
    }
    
    
}
