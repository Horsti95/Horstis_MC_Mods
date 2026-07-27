package com.horsti.spawnguard;

import com.google.gson.JsonObject;

/** A single protected sphere: a dimension, a centre, a radius and who created it. */
public record Zone(String dimension, int x, int y, int z, int radius, String owner) {

	public boolean contains(String dim, double px, double py, double pz) {
		if (!dimension.equals(dim)) {
			return false;
		}
		double dx = px - (x + 0.5);
		double dy = py - (y + 0.5);
		double dz = pz - (z + 0.5);
		return dx * dx + dy * dy + dz * dz <= (double) radius * radius;
	}

	public JsonObject toJson() {
		JsonObject obj = new JsonObject();
		obj.addProperty("dimension", dimension);
		obj.addProperty("x", x);
		obj.addProperty("y", y);
		obj.addProperty("z", z);
		obj.addProperty("radius", radius);
		obj.addProperty("owner", owner);
		return obj;
	}

	public static Zone fromJson(JsonObject obj) {
		return new Zone(
			obj.get("dimension").getAsString(),
			obj.get("x").getAsInt(),
			obj.get("y").getAsInt(),
			obj.get("z").getAsInt(),
			obj.get("radius").getAsInt(),
			obj.has("owner") ? obj.get("owner").getAsString() : "?");
	}

	/** Short dimension label for chat: "overworld" instead of "minecraft:overworld". */
	public String shortDimension() {
		int colon = dimension.indexOf(':');
		return colon < 0 ? dimension : dimension.substring(colon + 1);
	}
}
