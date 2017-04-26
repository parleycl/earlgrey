package geos.model;

import geos.annotations.Model;
import geos.annotations.ModelField;
import geos.core.ModelCore;
import geos.types.CENTROID;
import geos.types.GEOM;

@Model(name = "Cartografia_regional", tableName = "SPATIAL_DATA_REGIONAL", version = 1)
public class CartografiaRegional extends ModelCore{
	@ModelField
	public String REGION;
	@ModelField
	public String C_REG;
	@ModelField
	public GEOM GEOMREGIONAL;
	@ModelField
	public CENTROID CENTROIDE;
} 
