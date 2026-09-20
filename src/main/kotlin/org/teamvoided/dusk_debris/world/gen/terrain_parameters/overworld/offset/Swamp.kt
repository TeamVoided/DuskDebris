package org.teamvoided.dusk_debris.world.gen.terrain_parameters.overworld.offset

import net.minecraft.util.CubicSpline
import net.minecraft.util.ToFloatFunction
import org.teamvoided.dusk_debris.util.world_helper.add
import org.teamvoided.dusk_debris.util.world_helper.calculateSlope
import org.teamvoided.dusk_debris.world.gen.terrain_parameters.OverworldTerrainCreator
import org.teamvoided.dusk_debris.world.gen.terrain_parameters.overworld.Offset
import org.teamvoided.dusk_debris.world.gen.terrain_parameters.overworld.offset.Flats.PlatType

object Swamp {
    /** SWAMP TYPE NOTES
     *
     * Bog, land of lakes, lump fens in here
     *
     * Flats, entirely flat with little variation
     *
     * River, has a river dent in it with peaks being slightly elevated
     *
     * Towers, weird 10 block tall cliff islands thing (need new noise?)
     *
     **/
    enum class SwampType(val min: Float, val max: Float = min) {
        Bog(-0.5f),
        Flat(-0.3f, 0f),
        River(0.2f, 0.5f),
        Towers(1f);
    }

    //fun <C, I : ToFloatFunction<C>> createSwamps(
    //    contNumber: Float,
    //    data: OverworldTerrainCreator.TerrainParametersData<C, I>
    //): CubicSpline<C, I> {
    //    val flats1 = flats(50, 77, data)
//
    //    val spline = CubicSpline.builder(1f, data.amplifier)
    //        .add(PlatType.Flats.max, flats1)
    //    return spline.build()
    //}


}