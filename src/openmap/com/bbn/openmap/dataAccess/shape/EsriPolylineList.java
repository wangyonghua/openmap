// **********************************************************************
// 
// <copyright>
// 
//  BBN Technologies, a Verizon Company
//  10 Moulton Street
//  Cambridge, MA 02138
//  (617) 873-8000
// 
//  Copyright (C) BBNT Solutions LLC. All rights reserved.
// 
// </copyright>
// **********************************************************************
// 
// $Source: /cvs/distapps/openmap/src/openmap/com/bbn/openmap/dataAccess/shape/EsriPolylineList.java,v $
// $RCSfile: EsriPolylineList.java,v $
// $Revision: 1.1.1.1 $
// $Date: 2003/02/14 21:35:48 $
// $Author: dietrick $
// 
// **********************************************************************


package com.bbn.openmap.dataAccess.shape;

import com.bbn.openmap.omGraphics.*;
import com.bbn.openmap.util.Debug;

/**
 * An EsriGraphicList ensures that only EsriPolygons are added to its list.
 * @author Doug Van Auken 
 * @author Don Dietrick
 */
public class EsriPolylineList extends EsriGraphicList {

    /**
     * Over-ride the add( ) method to trap for inconsistent shape
     * geometry.  If you are adding a OMGraphic that is not a list,
     * make sure this list is a sub-list containing multiple geometry
     * parts.  Only add another list to a top level EsriGraphicList.
     * @param shape the non-null OMGraphic to add 
     */
    public void add(OMGraphic shape) {
	try {

	    if (shape instanceof OMPoly) {
		shape = EsriPolyline.convert((OMPoly)shape);
	    } else if (shape instanceof OMLine) {
		shape = EsriPolyline.convert(EsriPolylineList.convert((OMLine)shape));
	    }

	    if (shape instanceof OMGraphicList) {
		OMGraphicList list = (OMGraphicList)shape;
		EsriGraphic graphic = (EsriGraphic)list.getOMGraphicAt(0);
		
		if (graphic instanceof EsriPolyline ||
		    graphic instanceof EsriPolylineList) {
		    graphics.add(shape);
		    addExtents(((EsriGraphicList)shape).getExtents());
		} else if (graphic instanceof OMGraphic) {
		    // Try recursively...
		    add((OMGraphic)graphic);
		} else {
		    Debug.message("esri", "EsriPolylineList.add()- graphic list isn't EsriPolylineList, can't add.");
		}
	    } else if (shape instanceof EsriPolyline) {
		graphics.add(shape);
		addExtents(((EsriPolyline)shape).getExtents());
	    } else {
		Debug.message("esri", "EsriPolylineList.add()- graphic isn't EsriPolyline, can't add.");
	    }
	} catch (ClassCastException cce) {
	}
    }

    /**
     * Get the list type in ESRI type number form - 3.
     */
    public int getType() {
	return SHAPE_TYPE_POLYLINE;
    }

    /**
     * Construct an EsriPolylineList.
     */
    public EsriPolylineList() {
	super();
    }
    
    /**
     * Construct an EsriPolylineList with an initial capacity. 
     *
     * @param initialCapacity the initial capacity of the list 
     */
    public EsriPolylineList(int initialCapacity) {
	super(initialCapacity);
    }

    /**
     * Construct an EsriPolylineList with an initial capacity and
     * a standard increment value.
     *
     * @param initialCapacity the initial capacity of the list 
     * @param capacityIncrement the capacityIncrement for resizing 
     * @deprecated - capacityIncrement doesn't do anything.
     */
    public EsriPolylineList(int initialCapacity, int capacityIncrement) {
	super(initialCapacity);
    }

    public static OMPoly convert(OMLine omLine) {
	if (omLine.getRenderType() == OMGraphic.RENDERTYPE_LATLON) {
	    OMPoly poly = new OMPoly(omLine.getLL(), 
				     omLine.DECIMAL_DEGREES, 
				     omLine.getLineType());
	    DrawingAttributes da = new DrawingAttributes();
	    da.setFrom(omLine);
	    da.setTo(poly);
	    return poly;
	} else return null;
    }

}
