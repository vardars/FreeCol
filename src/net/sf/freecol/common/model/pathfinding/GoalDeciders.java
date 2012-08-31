/**
 *  Copyright (C) 2002-2012   The FreeCol Team
 *
 *  This file is part of FreeCol.
 *
 *  FreeCol is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  FreeCol is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with FreeCol.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.freecol.common.model.pathfinding;

import net.sf.freecol.common.model.Europe;
import net.sf.freecol.common.model.Location;
import net.sf.freecol.common.model.Map;
import net.sf.freecol.common.model.PathNode;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Settlement;
import net.sf.freecol.common.model.Tile;
import net.sf.freecol.common.model.Unit;


/**
 * Handy library of GoalDeciders.
 */
public final class GoalDeciders {


    /**
     * A Goal Decider to find the `closest' settlement owned by the
     * searching unit player, with connected ports weighted double.
     */
    private static GoalDecider ourClosestSettlementGoalDecider
        = new GoalDecider() {
                private PathNode bestPath = null;
                private float bestValue = 0.0f;

                public PathNode getGoal() { return bestPath; }
                public boolean hasSubGoals() { return true; }
                public boolean check(Unit u, PathNode path) {
                    Location loc = path.getLastNode().getLocation();
                    Settlement settlement = loc.getSettlement();
                    if (settlement != null && settlement.getOwner().owns(u)) {
                        float value = ((settlement.isConnectedPort()) ? 2.0f
                            : 1.0f) / (path.getTotalTurns() + 1);
                        if (bestValue < value) {
                            bestValue = value;
                            bestPath = path;
                            return true;
                        }
                    }
                    return false;
                }
            };


    /**
     * Gets a composite goal decider composed of two or more individual
     * goal deciders.  The first one dominates the second etc.
     *
     * @param gds A series (two minimum) of <code>GoalDecider</code>s
     *     to compose.
     * @return A new <code>GoalDecider</code> composed of the argument
     *     goal deciders.
     */
    public static GoalDecider getComposedGoalDecider(final GoalDecider... gds) {
        if (gds.length < 2) {
            throw new IllegalArgumentException("Short GoalDecider list");
        }

        return new GoalDecider() {
            private GoalDecider[] goalDeciders = gds;

            public PathNode getGoal() {
                for (int i = 0; i < goalDeciders.length; i++) {
                    PathNode path = goalDeciders[i].getGoal();
                    if (path != null) return path;
                }
                return null;
            }
            public boolean hasSubGoals() { return true; }
            public boolean check(Unit u, PathNode path) {
                boolean ret = false;
                for (int i = goalDeciders.length-1; i >= 0; i--) {
                    ret = goalDeciders[i].check(u, path);
                }
                return ret;
            }
        };
    }

    /**
     * Accessor for ourClosestSettlementGoalDecider.
     *
     * @returns ourClosestSettlementGoalDecider.
     */
    public static GoalDecider getOurClosestSettlementGoalDecider() {
        return ourClosestSettlementGoalDecider;
    }
}