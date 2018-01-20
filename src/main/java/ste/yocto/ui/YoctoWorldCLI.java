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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.validators.PositiveInteger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import ste.yocto.world.YoctoWorld;
import ste.yocto.world.YoctoWorldFactory;

/**
 *
 * @author ste
 */
class YoctoWorldCLI {
    public static String OPT_HELP       =       "--help";    
    public static String OPT_FILE       =       "--file";
    public static String OPT_ITERATIONS = "--iterations";
    public static String OPT_NOT_RANDOM = "--not-random";
    
    private static YoctoWorld WORLD = null;
    
    public static void main(String... args) {
        YoctoWorldCLI.CommonOptions options = new YoctoWorldCLI.CommonOptions();
        JCommander jc = JCommander.newBuilder()
            .addObject(options)
            .build();
        
        jc.setProgramName(YoctoWorldCLI.class.getName());
        
        if (args.length == 0) {
            jc.usage();
            return;
        }
        
        try {
            jc.parse(args);
        } catch (ParameterException x) {
            System.out.println("\nInvalid arguments: " + x.getMessage() + "\n");
            jc.usage();
            return;
        }
        
        if (options.help) {
            jc.usage();
            return;
        }
        
        try {
            WORLD = YoctoWorldFactory.fromFile(options.file);
        } catch (IOException x) {
            System.out.println("error: " + new File(options.file).getAbsolutePath() + " invalid or not found");
            return;
        }
        
        if (options.notRandom) {
            WORLD.setRandom(null);
        }
        
        for (long i=0; i<options.iterations; ++i) {
            WORLD.evolve();
            try {
                writeWorldToFile(options.file);
            } catch (IOException x) {
                //
                // TODO: erro handling
                //
            }
        }
    }
    
    public static YoctoWorld getWorld() {
        return WORLD;
    }
    
    private static void writeWorldToFile(String file) throws IOException {
        FileWriter w = new FileWriter(file);
        for (int y=1; y<=WORLD.getHeight(); ++y) {
            if (y>1) {
                w.append("\n");
            }
            for (int x=1; x<=WORLD.getWidth(); ++x) {
                w.append(WORLD.getYocto(x, y).toChar());
            }
        }
        
        w.close();
    }
    
    // --------------------------------------------------------- CommandList
    
    private static class CommonOptions {
        @Parameter(names = "--help", help = true)
        public boolean help;
        
        @Parameter(names = "--not-random")
        public boolean notRandom;
        
        @Parameter(names = "--file")
        public String file;
        
        @Parameter(names = "--iterations", validateWith = PositiveInteger.class)
        public long iterations;
    }
}
