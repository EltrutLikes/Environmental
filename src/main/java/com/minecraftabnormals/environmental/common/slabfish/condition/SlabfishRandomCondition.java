package com.minecraftabnormals.environmental.common.slabfish.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

/**
 * <p>A {@link SlabfishCondition} that returns <code>true</code> if the player that bred two slabfish together has insomnia.</p>
 *
 * @author Ocelot
 */
public class SlabfishRandomCondition implements SlabfishCondition {
	private final float chance;

	private SlabfishRandomCondition(float chance) {
		this.chance = chance;
	}

	/**
	 * Creates a new {@link SlabfishRandomCondition} from the specified json.
	 *
	 * @param json    The json to deserialize
	 * @param context The context of the json deserialization
	 * @return A new slabfish condition from that json
	 */
	public static SlabfishCondition deserialize(JsonObject json, JsonDeserializationContext context) {
		if (!json.has("chance"))
			throw new JsonSyntaxException("'chance' must be present.");
		return new SlabfishRandomCondition(json.get("chance").getAsFloat());
	}

	@Override
	public boolean test(SlabfishConditionContext context) {
		return context.getRandom().nextFloat() <= this.chance;
	}
}
