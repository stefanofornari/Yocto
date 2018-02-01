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

import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import ste.xtest.cli.BugFreeCLI;

/**
 *
 */
public class BugFreeYoctoWorldViewer extends BugFreeCLI {
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Before
    public void before() throws Exception {
        STDOUT.clearLog();
    }
    
    @Test(timeout=750)
    public void static_invocation() throws Exception {
        YoctoWorldViewer.main("something");
        then(STDOUT.getLog()).contains("Usage:");
    }
/*
    @Test
    public void show_syntax_if_invalid_command() throws Exception {
        final String[][] ARGS = new String[][]{
            new String[]{},
            new String[]{"invalid"},
            new String[]{""}
        };

        for (String[] A : ARGS) {
            STDOUT.clearLog();
            new YoctoWorldCLI().launch(A);
            if (A.length > 0) {
                then(STDOUT.getLog()).contains("Invalid arguments").contains("Usage:");
            } else {
                then(STDOUT.getLog()).contains("Usage:");
            }

        }
    }

    @Test
    public void show_help_if_command_is_help() throws Exception {
        new YoctoWorldCLI().launch(OPT_HELP);
        then(STDOUT.getLog()).contains("Usage:");
    }
    
    @Test
    public void read_world_from_file_ok() throws Exception {
        YoctoWorldCLI cli = new YoctoWorldCLI();
        cli.launch(OPT_FILE, "src/test/worlds/small1.yw", OPT_ITERATIONS, "0");
        
        YoctoWorld w = cli.getWorld();
        then(w.getHeight()).isEqualTo(3);
        then(w.getWidth()).isEqualTo(3);
        then(w.getYocto(3, 3)).isEqualTo(REJECTOR);
        
        cli = new YoctoWorldCLI();
        cli.launch(OPT_FILE, "src/test/worlds/small2.yw", OPT_ITERATIONS, "0");
        
        w = cli.getWorld();
        then(w.getHeight()).isEqualTo(4);
        then(w.getWidth()).isEqualTo(2);
        then(w.getYocto(2, 1)).isEqualTo(FRIEND);
    }
    
    @Test
    public void read_world_from_file_ko() throws Exception {
        new YoctoWorldCLI().launch(OPT_FILE, "doesnotexist.yw", OPT_ITERATIONS, "0");
        then(STDOUT.getLog())
            .contains("error:")
            .contains(new File("doesnotexist.yw").getAbsolutePath() + " invalid or not found");
    }
    
    @Test
    public void write_evolution_into_the_file() throws Exception {
        File newFile = File.createTempFile("yocto", "yw");
        File worldFile = new File("src/test/worlds/small3.yw");
        
        FileUtils.copyFile(worldFile, newFile);
        YoctoWorldCLI cli = new YoctoWorldCLI();
        cli.launch(OPT_FILE, newFile.getAbsolutePath(), OPT_ITERATIONS, "1", OPT_NOT_RANDOM);
        
        YoctoWorld w = cli.getWorld();
        WorldHelper.printWorld(w);
        
        WorldHelper.thenWorldIsEqualTo(
            YoctoWorldFactory.fromFile(newFile.getAbsolutePath()),    
            YoctoWorldFactory.fromStrings(
                new String[] {
                    "   ", " = ", " - ", "   "
                }
        ));
        
        cli = new YoctoWorldCLI();
        cli.launch(OPT_FILE, newFile.getAbsolutePath(), OPT_ITERATIONS, "1", OPT_NOT_RANDOM);
        
        w = cli.getWorld();
        WorldHelper.printWorld(w);
        
        WorldHelper.thenWorldIsEqualTo(
            YoctoWorldFactory.fromFile(newFile.getAbsolutePath()),    
            YoctoWorldFactory.fromStrings(
                new String[] {
                    "=  ", "-  ", "   ", "   "
                }
        ));
    }
    
    
    @Test
    public void run_forever_if_forever_argument_is_given() throws Exception {
        File newFile = File.createTempFile("yocto", "yw");
        File worldFile = new File("src/test/worlds/small3.yw");
        
        FileUtils.copyFile(worldFile, newFile);
        
        YoctoWorldCLI cli = new YoctoWorldCLI();
        new Thread() {
            @Override
            public void run() {
                try {
                    cli.launch(OPT_FILE, newFile.getAbsolutePath(), OPT_FOREVER);
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        }.start();
        
        int i=0;
        while (i<20) {
            if (cli.getWorld() == null) {
                Thread.sleep(100);
            } else {
                break;
            }
            ++i;
        }
        then(i).isLessThan(20);
        
        YoctoWorld w = cli.getWorld();
        long prevModified = w.getLastEvolutionTimestamp();
        long lastModified = -1;
        while(i<10) {
            Thread.sleep(50);
            lastModified = w.getLastEvolutionTimestamp();
            then(lastModified).isGreaterThan(prevModified);
            prevModified = lastModified;
            ++i;
        }
    }
    
    @Test(timeout=300)
    public void iterations_and_forever_cannot_be_together() throws Exception {
        File newFile = File.createTempFile("yocto", "yw");
        File worldFile = new File("src/test/worlds/small3.yw");
        FileUtils.copyFile(worldFile, newFile);
        
        new YoctoWorldCLI().launch(OPT_FILE, newFile.getAbsolutePath(), OPT_ITERATIONS, "0", OPT_FOREVER);
        then(STDOUT.getLog())
            .contains("Invalid arguments:")
            .contains("--iteration and --forever can not be provided together.")
            .contains("Usage:");
    }
*/
}
