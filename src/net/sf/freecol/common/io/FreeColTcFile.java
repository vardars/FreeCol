/**
 *  Copyright (C) 2002-2015   The FreeCol Team
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

package net.sf.freecol.common.io;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import java.util.Set;

import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.resources.ResourceMapping;
import static net.sf.freecol.common.util.CollectionUtils.*;


/**
 * A Total Conversion (TC).  Rules are TCs.
 */
public class FreeColTcFile extends FreeColModFile {

    private static final Set<String> FILE_ENDINGS
        = makeUnmodifiableSet(".ftc", ".zip");


    /**
     * Opens the given file for reading.
     *
     * @param file The file to load.
     * @throws IOException if thrown while opening the file.
     */
    public FreeColTcFile(final File file) throws IOException {
        super(file);
    }

    /**
     * Opens the file with the given name for reading.
     *
     * @param id The identifier of the TC to load.
     * @throws IOException if thrown while opening the file.
     */
    public FreeColTcFile(final String id) throws IOException {
        super(new File(FreeColDirectories.getRulesDirectory(), id));
    }


    /**
     * Gets the Specification of this TC.
     *
     * @return The <code>Specification</code> in this TC, or null if
     *     none present.
     * @exception IOException if an error occurs reading the specification.
     */
    public Specification getSpecification() throws IOException {
        Specification specification;
        try (InputStream si = getSpecificationInputStream()) {
            if (si == null) return null;
            specification = new Specification(si);
        }
        return specification;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResourceMapping getResourceMapping() {
        ResourceMapping result = new ResourceMapping();
        try {
            if (getParent() != null) {
                final FreeColTcFile parentTcData = new FreeColTcFile(getParent());
                result.addAll(parentTcData.getResourceMapping());
            }
            // Add the local data *after* the parent data so that the local
            // values can override (eventual call is Map.putAll).
            // Note that FreeColDataFile.getResourceMapping logs the load,
            // and thus the log messages will appear to be in the reverse
            // order, which mislead me until looking at the code.
            result.addAll(super.getResourceMapping());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * File endings that are supported for this type of data file.
     *
     * @return A set: ".ftc" and ".zip".
     */
    protected static Set<String> getFileEndings() {
        return FILE_ENDINGS;
    }
}
