
package net.sf.freecol.common.model;

import java.util.Iterator;
import java.util.logging.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Represents an Indian settlement.
 */
public class IndianSettlement extends Settlement {
    private static final Logger logger = Logger.getLogger(IndianSettlement.class.getName());

    public static final String  COPYRIGHT = "Copyright (C) 2003-2004 The FreeCol Team";
    public static final String  LICENSE = "http://www.gnu.org/licenses/gpl.html";
    public static final String  REVISION = "$Revision$";

    public static final int INCA = 0;
    public static final int AZTEC = 1;
    public static final int ARAWAK = 2;
    public static final int CHEROKEE = 3;
    public static final int IROQUOIS = 4;
    public static final int SIOUX = 5;
    public static final int APACHE = 6;
    public static final int TUPI = 7;
    public static final int LAST_TRIBE = 7;
    public static final int CAMP = 0;
    public static final int VILLAGE = 1;
    public static final int CITY = 2;
    public static final int LAST_KIND = 2;

    private int food = 0;
    private int kind;
    private int tribe;
    private boolean isCapital;
    private UnitContainer unitContainer;





    /**
     * The constructor to use.
     *
     * @param game The <code>Game</code> in which this object belong.
     * @param tile The location of the <code>IndianSettlement</code>.
     * @param tribe Tribe of settlement
     * @param kind Kind of settlement
     * @param isCapital True if settlement is tribe's capital
     * @exception IllegalArgumentException if an invalid tribe or kind is given
     */
    public IndianSettlement(Game game, Player player, Tile tile, int tribe, int kind, boolean isCapital) {
        // TODO: Change 'null' to the indian AI-player:

        super(game, player, tile);

        unitContainer = new UnitContainer(game, this);

        if (tribe < 0 || tribe > LAST_TRIBE) {
            throw new IllegalArgumentException("Invalid tribe provided");
        }

        this.tribe = tribe;

        if (kind < 0 || kind > LAST_KIND) {
            throw new IllegalArgumentException("Invalid settlement kind provided");
        }

        this.kind = kind;
        this.isCapital = isCapital;
    }



    /**
    * Initiates a new <code>IndianSettlement</code> from an <code>Element</code>.
    *
    * @param game The <code>Game</code> in which this object belong.
    * @param element The <code>Element</code> (in a DOM-parsed XML-tree) that describes
    *                this object.
    */
    public IndianSettlement(Game game, Element element) {
        super(game, element);

        readFromXMLElement(element);
    }





    /**
    * Gets the kind of Indian settlement.
    */
    public int getKind() {
        return kind;
    }


    /**
    * Gets the tribe of the Indian settlement.
    */
    public int getTribe() {
        return tribe;
    }


    /**
     * Gets the radius of what the <code>Settlement</code> considers
     * as it's own land.  Cities dominate 2 tiles, other settlements 1 tile.
     *
     * @return Settlement radius
     */
    public int getRadius() {
        if (kind == CITY) {
            return 2;
        } else {
            return 1;
        }
    }


    public boolean isCapital() {
        return isCapital;
    }
    
    public void setCapital(boolean isCapital) {
        this.isCapital = isCapital;
    }
    /**
    * Adds a <code>Locatable</code> to this Location.
    *
    * @param locatable The code>Locatable</code> to add to this Location.
    */
    public void add(Locatable locatable) {
        if (locatable instanceof Unit) {
            unitContainer.addUnit((Unit) locatable);
        } else {
            logger.warning("Tried to add an unrecognized 'Locatable' to a IndianSettlement.");
        }
    }


    /**
    * Removes a code>Locatable</code> from this Location.
    *
    * @param locatable The <code>Locatable</code> to remove from this Location.
    */
    public void remove(Locatable locatable) {
        if (locatable instanceof Unit) {
            unitContainer.removeUnit((Unit) locatable);
        } else {
            logger.warning("Tried to remove an unrecognized 'Locatable' from a IndianSettlement.");
        }
    }


    /**
    * Returns the amount of Units at this Location.
    *
    * @return The amount of Units at this Location.
    */
    public int getUnitCount() {
        return unitContainer.getUnitCount();
    }


    public Iterator getUnitIterator() {
        return unitContainer.getUnitIterator();
    }
    
    

    public Unit getFirstUnit() {
        return unitContainer.getFirstUnit();
    }


    public Unit getLastUnit() {
        return unitContainer.getLastUnit();
    }


    /**
    * Gets the <code>Unit</code> that is currently defending this <code>IndianSettlement</code>.
    * @param attacker The target that would be attacking this <code>IndianSettlement</code>.
    * @return The <code>Unit</code> that has been choosen to defend this <code>IndianSettlement</code>.
    */
    public Unit getDefendingUnit(Unit attacker) {
        Iterator unitIterator = getUnitIterator();

        Unit defender = null;
        if (unitIterator.hasNext()) {
            defender = (Unit) unitIterator.next();
        } else {
            return null;
        }

        while (unitIterator.hasNext()) {
            Unit nextUnit = (Unit) unitIterator.next();

            if (nextUnit.getDefensePower(attacker) > defender.getDefensePower(attacker)) {
                defender = nextUnit;
            }
        }

        return defender;
    }




    public boolean contains(Locatable locatable) {
        if (locatable instanceof Unit) {
            return unitContainer.contains((Unit) locatable);
        } else {
            return false;
        }
    }

    
    public boolean canAdd(Locatable locatable) {
        return true;
    }


    public void newTurn() {
        int workers = unitContainer.getUnitCount();
        for (int direction=0; direction<8 && workers > 0; direction++) {
            if (getGame().getMap().getNeighbourOrNull(direction, getTile()) != null &&
                    (getGame().getMap().getNeighbourOrNull(direction, getTile()).getOwner() == null
                    || getGame().getMap().getNeighbourOrNull(direction, getTile()).getOwner() == this)) {
                food += 5;
                workers--;
            }
        }
    
        // TODO: Create a unit if food>=300, but not if a maximum number of units is reaced.
    }
    
    
    public void dispose() {
        unitContainer.dispose();
        getTile().setSettlement(null);
        super.dispose();
    }
    

    /**
    * Make a XML-representation of this object.
    *
    * @param document The document to use when creating new componenets.
    * @return The DOM-element ("Document Object Model") made to represent this "IndianSettlement".
    */
    public Element toXMLElement(Player player, Document document, boolean showAll, boolean toSavedGame) {
        Element indianSettlementElement = document.createElement(getXMLElementTagName());

        indianSettlementElement.setAttribute("ID", getID());
        indianSettlementElement.setAttribute("tile", tile.getID());
        indianSettlementElement.setAttribute("owner", owner.getID());
        indianSettlementElement.setAttribute("tribe", Integer.toString(tribe));
        indianSettlementElement.setAttribute("kind", Integer.toString(kind));
        indianSettlementElement.setAttribute("isCapital", Boolean.toString(isCapital));
        indianSettlementElement.setAttribute("food", Integer.toString(food));

        indianSettlementElement.appendChild(unitContainer.toXMLElement(player, document, showAll, toSavedGame));

        return indianSettlementElement;
    }


    /**
    * Initialize this object from an XML-representation of this object.
    *
    * @param indianSettlementElement The DOM-element ("Document Object Model") made to represent this "IndianSettlement".
    */
    public void readFromXMLElement(Element indianSettlementElement) {
        setID(indianSettlementElement.getAttribute("ID"));

        tile = (Tile) getGame().getFreeColGameObject(indianSettlementElement.getAttribute("tile"));
        owner = (Player)getGame().getFreeColGameObject(indianSettlementElement.getAttribute("owner"));
        tribe = Integer.parseInt(indianSettlementElement.getAttribute("tribe"));
        kind = Integer.parseInt(indianSettlementElement.getAttribute("kind"));
        isCapital = (new Boolean(indianSettlementElement.getAttribute("isCapital"))).booleanValue();
        
        if (indianSettlementElement.hasAttribute("food")) {
            food = Integer.parseInt(indianSettlementElement.getAttribute("food"));
        } else {
            food = 0;
        }

        Element unitContainerElement = getChildElement(indianSettlementElement, UnitContainer.getXMLElementTagName());
        if (unitContainer != null) {
            unitContainer.readFromXMLElement(unitContainerElement);
        } else {
            unitContainer = new UnitContainer(getGame(), this, unitContainerElement);
        }
    }


    /**
    * Returns the tag name of the root element representing this object.
    * @return "indianSettlement".
    */
    public static String getXMLElementTagName() {
        return "indianSettlement";
    }
}
