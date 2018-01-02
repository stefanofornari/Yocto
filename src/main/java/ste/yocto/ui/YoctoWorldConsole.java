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
package ste.yocto.ui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import ste.yocto.world.YoctoWorld;
import ste.yocto.world.YoctoWorldFactory;

/**
 */
public class YoctoWorldConsole {
    public static void main(String... args) throws Exception {
        
        BufferedReader r = new BufferedReader(new FileReader(args[0]));
        
        List<String> lines = new ArrayList<>();
        
        String line;
        while((line = r.readLine()) != null) {
            lines.add(line);
        }
        r.close();
        
        String[] data = new String[lines.size()];
        lines.toArray(data);
        
        //int size = Integer.parseInt(args[0]);
        //YoctoWorld w = YoctoWorldFactory.random(size, size);
        YoctoWorld w = YoctoWorldFactory.fromStrings(data);
        
        while (true) {
            printWorld(w);
            w.evolve();
            try {
                Thread.sleep(500);
            } catch (InterruptedException x) {}
        }
    }
    
    public static void printWorld(YoctoWorld w) {
        System.out.print("\033\143");
        //System.out.print("\n");
        
        for(int y=1; y<=w.getHeight(); ++y) {
            for(int x=1; x<=w.getWidth(); ++x) {
                System.out.print(w.select(x,y).get().toChar());
            }
            System.out.print('\t');
            for(int x=1; x<=w.getWidth(); ++x) {
                System.out.print(w.getEnergy(x,y));
            }
            System.out.print('\n');
        }
    }
}
