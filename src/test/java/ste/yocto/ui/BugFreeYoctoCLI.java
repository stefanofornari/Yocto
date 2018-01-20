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
import org.junit.Test;
import ste.xtest.cli.BugFreeCLI;
import static ste.yocto.ui.YoctoWorldCLI.OPT_FILE;
import static ste.yocto.ui.YoctoWorldCLI.OPT_HELP;
import static ste.yocto.ui.YoctoWorldCLI.OPT_ITERATIONS;
import static ste.yocto.ui.YoctoWorldCLI.OPT_NOT_RANDOM;
import ste.yocto.world.WorldHelper;
import ste.yocto.world.YoctoWorld;
import static ste.yocto.world.YoctoWorld.Yocto.FRIEND;
import static ste.yocto.world.YoctoWorld.Yocto.REJECTOR;
import ste.yocto.world.YoctoWorldFactory;

/**
 *
 */
public class BugFreeYoctoCLI extends BugFreeCLI {

    @Before
    public void before() throws Exception {
        STDOUT.clearLog();
    }

    @Test
    public void show_syntax_if_invalid_command() throws Exception {
        final String[][] ARGS = new String[][]{
            new String[]{},
            new String[]{"invalid"},
            new String[]{""}
        };

        for (String[] A : ARGS) {
            STDOUT.clearLog();
            YoctoWorldCLI.main(A);
            if (A.length > 0) {
                then(STDOUT.getLog()).contains("Invalid arguments").contains("Usage:");
            } else {
                then(STDOUT.getLog()).contains("Usage:");
            }

        }
    }

    @Test
    public void show_help_if_command_is_help() throws Exception {
        YoctoWorldCLI.main(OPT_HELP);
        then(STDOUT.getLog()).contains("Usage:");
    }
    
    @Test
    public void read_world_from_file_ok() throws Exception {
        YoctoWorldCLI.main(OPT_FILE, "src/test/worlds/small1.yw", OPT_ITERATIONS, "0");
        
        YoctoWorld w = YoctoWorldCLI.getWorld();
        then(w.getHeight()).isEqualTo(3);
        then(w.getWidth()).isEqualTo(3);
        then(w.getYocto(3, 3)).isEqualTo(REJECTOR);
        
        YoctoWorldCLI.main(OPT_FILE, "src/test/worlds/small2.yw", OPT_ITERATIONS, "0");
        
        w = YoctoWorldCLI.getWorld();
        then(w.getHeight()).isEqualTo(4);
        then(w.getWidth()).isEqualTo(2);
        then(w.getYocto(2, 1)).isEqualTo(FRIEND);
    }
    
    @Test
    public void read_world_from_file_ko() throws Exception {
        YoctoWorldCLI.main(OPT_FILE, "doesnotexist.yw", OPT_ITERATIONS, "0");
        then(STDOUT.getLog())
            .contains("error:")
            .contains(new File("doesnotexist.yw").getAbsolutePath() + " invalid or not found");
    }
    
    @Test
    public void write_evolution_into_the_file() throws Exception {
        File newFile = File.createTempFile("yocto", "yw");
        File worldFile = new File("src/test/worlds/small3.yw");
        
        FileUtils.copyFile(worldFile, newFile);
        YoctoWorldCLI.main(OPT_FILE, newFile.getAbsolutePath(), OPT_ITERATIONS, "1", OPT_NOT_RANDOM);
        
        YoctoWorld w = YoctoWorldCLI.getWorld();
        WorldHelper.printWorld(w);
        
        WorldHelper.thenWorldIsEqualTo(
            YoctoWorldFactory.fromFile(newFile.getAbsolutePath()),    
            YoctoWorldFactory.fromStrings(
                new String[] {
                    "   ", "   ", "  +", "  -"
                }
        ));
    }
    
/*
    @Test
    public void insert_default_number_of_default_coupons() throws Exception {
        CouponManager cm = new CouponManager();

        CouponCLI.main(CMD_CREATE);

        List<Coupon> coupons = cm.getAll();
        then(coupons).hasSize(CouponCLI.DEFAULT_HOW_MANY);
        String output = STDOUT.getLog();
        Instant validUntil = Instant.now().plus(CouponCLI.DEFAULT_VALIDITY, DAYS);
        for (Coupon c : coupons) {
            then(c.getQuantity()).isEqualTo(CouponCLI.DEFAULT_QUANTITY);
            then(output).contains(String.format("%s,%d,%tF\n", c.getId(), c.getQuantity(), new Date(validUntil.toEpochMilli())));
        }
    }

    @Test
    public void show_coupons_with_no_coupons() throws Exception {
        CouponCLI.main(CMD_LIST);

        then(STDOUT.getLog()).isEqualTo("\nFound 0 coupons\n\n");
    }

    @Test
    public void show_coupons_with_coupons() throws Exception {
        Coupon C1 = givenCoupon("coupon-0001", 100, new Date(), false);

        CouponCLI.main(CMD_LIST);

        then(STDOUT.getLog()).isEqualTo(
                "\nFound 1 coupons\n\n"
                + String.format(
                        "%s for %d valid until %tF used on %tF",
                        C1.getId(), C1.getQuantity(), C1.getValidUntil(), C1.getUsedOn()
                )
                + "\n"
        );

        Coupon C2 = givenCoupon("coupon-0002", 200, new Date(System.currentTimeMillis() + 1000l * 60 * 60 * 24 * 365), true);

        STDOUT.clearLog();
        CouponCLI.main(CMD_LIST);
        then(STDOUT.getLog()).isEqualTo(
                "\nFound 2 coupons\n\n"
                + String.format(
                        "%s for %d valid until %tF used on %tF",
                        C1.getId(), C1.getQuantity(), C1.getValidUntil(), C1.getUsedOn()
                )
                + "\n"
                + String.format(
                        "%s for %d valid until %tF used on %tF",
                        C2.getId(), C2.getQuantity(), C2.getValidUntil(), C2.getUsedOn()
                )
                + "\n"
        );
    }

    @Test
    public void generate_random_ids() throws Exception {
        final ArrayRandomStub R = new ArrayRandomStub(
                new int[]{
                    0x4e3b, 0x5484, 0x6a0e, 0x11e7, 0xac13, 0x0f23, 0x3562, 0xfedd
                }
        );
        PrivateAccess.setStaticValue(CouponCLI.class, "R", R);

        then(CouponCLI.generateCouponId()).isEqualTo("3b84-0ee7-1323");
        then(CouponCLI.generateCouponId()).isEqualTo("62dd-3b84-0ee7");
    }

    @Test
    public void error_if_funambol_home_not_set() throws Exception {
        EXIT.expectSystemExit();

        System.getProperties().remove("funambol.home");
        STDERR.enableLog();

        CouponCLI.main("list");

        then(STDERR.getLog()).contains(
                "no funambol.home set; use -Dfunambol.home=<your onemediahub home"
        );
    }

    @Test
    public void create_n_coupons_ok() throws Exception {
        CouponManager cm = new CouponManager();

        CouponCLI.main(CMD_CREATE, "--n", "0");

        List<Coupon> coupons = cm.getAll();
        then(coupons).isEmpty();

        CouponCLI.main(CMD_CREATE, "--n", "10");
        coupons = cm.getAll();
        then(coupons).hasSize(10);
        String output = STDOUT.getLog();
        Instant validUntil = Instant.now().plus(CouponCLI.DEFAULT_VALIDITY, DAYS);
        for (Coupon c : coupons) {
            then(c.getQuantity()).isEqualTo(CouponCLI.DEFAULT_QUANTITY);
            then(output).contains(String.format("%s,%d,%tF\n", c.getId(), c.getQuantity(), new Date(validUntil.toEpochMilli())));
        }

    }

    @Test
    public void create_width_n_coupons_ko() throws Exception {
        CouponManager cm = new CouponManager();

        CouponCLI.main(CMD_CREATE, "--n", "-1");
        then(cm.getAll()).isEmpty();
        then(STDOUT.getLog()).contains("Usage:");

    }

    @Test
    public void create_with_validity_ok() throws Exception {
        CouponManager cm = new CouponManager();

        final Instant IN_15_DAYS = Instant.now().plus(15, ChronoUnit.DAYS);

        CouponCLI.main(CMD_CREATE, "--validity", "15");

        List<Coupon> coupons = cm.getAll();
        then(coupons).hasSize(CouponCLI.DEFAULT_HOW_MANY);
        for (Coupon c : coupons) {
            then(c.getValidUntil()).isEqualToIgnoringHours(new Date(IN_15_DAYS.toEpochMilli()));
            then(STDOUT.getLog()).contains(String.format("%s,%d,%tF\n", c.getId(), c.getQuantity(), c.getValidUntil()));
        }
    }

    @Test
    public void create_width_validity_ko() throws Exception {
        CouponManager cm = new CouponManager();

        CouponCLI.main(CMD_CREATE, "--validity", "-1");
        then(cm.getAll()).isEmpty();
        then(STDOUT.getLog()).contains("Usage:");

    }

    @Test
    public void create_with_days_ok() throws Exception {
        CouponManager cm = new CouponManager();

        CouponCLI.main(CMD_CREATE, "--days", "25");

        List<Coupon> coupons = cm.getAll();
        then(coupons).hasSize(CouponCLI.DEFAULT_HOW_MANY);
        for (Coupon c : coupons) {
            then(c.getQuantity()).isEqualTo(25);
            then(STDOUT.getLog()).contains(String.format("%s,%d,%tF\n", c.getId(), c.getQuantity(), c.getValidUntil()));
        }
    }

    @Test
    public void create_width_days_ko() throws Exception {
        CouponManager cm = new CouponManager();

        for (int i : new int[]{-1, 0}) {
            CouponCLI.main(CMD_CREATE, "--days", String.valueOf(i));
            then(cm.getAll()).isEmpty();
            then(STDOUT.getLog()).contains("Usage:");
        }

    }
*/

    // --------------------------------------------------------- private methods    
}
