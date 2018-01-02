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
import org.junit.Test;
import static ste.yocto.world.YoctoWorld.A;
import static ste.yocto.world.YoctoWorld.F;
import static ste.yocto.world.YoctoWorld.N;
import static ste.yocto.world.YoctoWorld.R;
import static ste.yocto.world.YoctoWorld.Yocto.*;


/**
 *
 */
public class BugFreeYocto {
    
    @Test
    public void toChar_returns_a_single_char_representation() {
        then(NEUTRAL.toChar()).isEqualTo(N);
        then(ATTRACTOR.toChar()).isEqualTo(A);
        then(REJECTOR.toChar()).isEqualTo(R);
        then(FRIEND.toChar()).isEqualTo(F);
    }
    
    @Test
    public void e_returns_yocto_self_energy() {
        then(NEUTRAL.e()).isEqualTo(0);
        then(ATTRACTOR.e()).isEqualTo(1);
        then(REJECTOR.e()).isEqualTo(-1);
        then(FRIEND.e()).isEqualTo(0);
    }
    
    @Test
    public void rejector_evergy_from_interaction() {
        then(REJECTOR.e(FRIEND)).isEqualTo(ENERGY_ATTRACT);
        then(REJECTOR.e(ATTRACTOR)).isEqualTo(ENERGY_ATTRACT);
        then(REJECTOR.e(REJECTOR)).isEqualTo(ENERGY_REJECT);
        then(REJECTOR.e(NEUTRAL)).isEqualTo(ENERGY_NEUTRAL);
    }
    
    @Test
    public void attractor_evergy_from_interaction() {
        then(ATTRACTOR.e(FRIEND)).isEqualTo(ENERGY_NEUTRAL);
        then(ATTRACTOR.e(ATTRACTOR)).isEqualTo(ENERGY_ATTRACT);
        then(ATTRACTOR.e(REJECTOR)).isEqualTo(ENERGY_REJECT);
        then(ATTRACTOR.e(NEUTRAL)).isEqualTo(ENERGY_NEUTRAL);
    }
    
    @Test
    public void friend_evergy_from_interaction() {
        then(FRIEND.e(FRIEND)).isEqualTo(ENERGY_NEUTRAL);
        then(FRIEND.e(ATTRACTOR)).isEqualTo(ENERGY_ATTRACT);
        then(FRIEND.e(REJECTOR)).isEqualTo(ENERGY_REJECT);
        then(FRIEND.e(NEUTRAL)).isEqualTo(ENERGY_NEUTRAL);
    }
    
    @Test
    public void neutral_does_not_have_interactions() {
        then(NEUTRAL.e(FRIEND)).isEqualTo(ENERGY_NEUTRAL);
        then(NEUTRAL.e(ATTRACTOR)).isEqualTo(ENERGY_NEUTRAL);
        then(NEUTRAL.e(REJECTOR)).isEqualTo(ENERGY_NEUTRAL);
        then(NEUTRAL.e(NEUTRAL)).isEqualTo(ENERGY_NEUTRAL);
    }
    
}
