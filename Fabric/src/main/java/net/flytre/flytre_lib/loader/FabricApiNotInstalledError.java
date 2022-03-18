package net.flytre.flytre_lib.loader;

class FabricApiNotInstalledError extends RuntimeException {

    FabricApiNotInstalledError() {
        super("Error: Fabric API must be installed to do this");
    }
}
