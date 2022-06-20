package net.flytre.flytre_lib.api.config.reference.fluid;

import net.minecraft.fluid.Fluid;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


sealed public interface ConfigFluid permits FluidReference, FluidTagReference {


    static Set<Fluid> values(Set<ConfigFluid> fluids, World world) {
        Set<Fluid> result = new HashSet<>();
        for (ConfigFluid fluid : fluids) {
            if (fluid instanceof FluidReference) {
                result.add(((FluidReference) fluid).getValue(world));
            } else {
                Set<Fluid> list = ((FluidTagReference) fluid).getValue(world);
                if (list != null)
                    result.addAll(list);
            }
        }
        return result;
    }

    /**
     * To be used! O(1) search time for fluids not in tags, or O(n) for fluids in tags / not present
     */
    static boolean contains(Set<ConfigFluid> fluids, Fluid fluid, World world) {
        if (fluids.contains(new FluidReference(fluid)))
            return true;
        return fluids.stream().anyMatch(i -> {
            if (!(i instanceof FluidTagReference))
                return false;
            Set<Fluid> list = ((FluidTagReference) i).getValue(world);
            return list != null && list.contains(fluid);
        });
    }

    static Set<ConfigFluid> of(Set<Fluid> values) {
        return values.stream().map(FluidReference::new).collect(Collectors.toSet());
    }
}
