/*
 * redisync
 *
 * Copyright (C) 2013 art of coding UG (haftungsbeschränkt).
 *
 * Alle Rechte vorbehalten. Nutzung unterliegt Lizenzbedingungen.
 * All rights reserved. Use is subject to license terms.
 */

package eu.artofcoding.redisync.web;

import org.omnifaces.converter.SelectItemsConverter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.FacesConverter;
import java.io.Serializable;

@FacesConverter(forClass = ManagedDirectory.class)
public class ManagedDirectoryConverter extends SelectItemsConverter implements Serializable {

    private static final long serialVersionUID = 1L;

/*
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ManagedDirectory managedDirectory = new ManagedDirectory(value, null);
        System.out.println("convert " + value + " to Object " + managedDirectory);
        return managedDirectory;
    }
*/

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String stringValue = String.valueOf(value);
        ManagedDirectory managedDirectory = new ManagedDirectory(stringValue, null);
        System.out.printf("convert to String %s -> %s -> %s%n", value, managedDirectory, managedDirectory.getName());
        return managedDirectory.getName();
    }

}
