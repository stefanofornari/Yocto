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

import java.io.File;
import org.apache.commons.io.FileUtils;
import static org.assertj.core.api.BDDAssertions.then;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import ste.xtest.cli.BugFreeCLI;
import static ste.yocto.ui.YoctoWorldViewer.OPT_FILE;
import static ste.yocto.ui.YoctoWorldViewer.OPT_HELP;
import ste.yocto.world.YoctoWorld;
import static ste.yocto.world.YoctoWorld.Yocto.ATTRACTOR;
import static ste.yocto.world.YoctoWorld.Yocto.FRIEND;

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

    @Test(timeout=750)
    public void show_syntax_if_invalid_command() throws Exception {
        final String[][] ARGS = new String[][]{
            new String[]{},
            new String[]{"invalid"},
            new String[]{""}
        };

        for (String[] A : ARGS) {
            STDOUT.clearLog();
            new YoctoWorldViewer()._main(A);
            if (A.length > 0) {
                then(STDOUT.getLog()).contains("Invalid arguments").contains("Usage:");
            } else {
                then(STDOUT.getLog()).contains("Usage:");
            }

        }
    }

    @Test(timeout=750)
    public void show_help_if_command_is_help() throws Exception {
        new YoctoWorldViewer()._main(OPT_HELP);
        then(STDOUT.getLog()).contains("Usage:");
    }

    
    @Test
    public void read_world_from_file_ok() throws Exception {
        final YoctoWorldViewer viewer = new YoctoWorldViewer();
        
        File newFile = File.createTempFile("yocto", "yw");
        File worldFile = new File("src/test/worlds/small3.yw");
        
        FileUtils.copyFile(worldFile, newFile);
        
        new Thread() {
            @Override
            public void run() {
                try {
                    viewer._main(OPT_FILE, newFile.getAbsolutePath());
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        }.start();
        
        int i=0;
        while (i<20) {
            if (viewer.getWorld() == null) {
                Thread.sleep(100);
            } else {
                break;
            }
            ++i;
        }
        then(i).isLessThan(20);
        
        YoctoWorld w = viewer.getWorld();
        then(w.getHeight()).isEqualTo(4);
        then(w.getWidth()).isEqualTo(3);
        then(w.getYocto(3, 3)).isEqualTo(FRIEND);
        
        //
        // write a new world
        //
        worldFile = new File("src/test/worlds/small1.yw");
        FileUtils.copyFile(worldFile, newFile);
        
        //
        // The viewer shall read the file every 1 seconds
        //
        Thread.sleep(1500);
        
        w = viewer.getWorld();
        then(w.getHeight()).isEqualTo(3);
        then(w.getWidth()).isEqualTo(3);
        then(w.getYocto(1, 1)).isEqualTo(ATTRACTOR);
    }

    @Test(timeout=750)
    public void read_world_from_file_ko() throws Exception {
        new YoctoWorldViewer()._main(OPT_FILE, "doesnotexist.yw");
        then(STDOUT.getLog())
            .contains("error:")
            .contains(new File("doesnotexist.yw").getAbsolutePath() + " invalid or not found");
    }
}
