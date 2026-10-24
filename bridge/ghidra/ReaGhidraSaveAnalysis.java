import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import ghidra.app.util.headless.HeadlessScript;
import ghidra.framework.data.DomainObjectAdapterDB;

/** Pack the analyzed Program so a later session with the same profile imports it instead of analyzing again. */
public final class ReaGhidraSaveAnalysis extends HeadlessScript {
    @Override
    public void run() throws Exception {
        String[] arguments = getScriptArgs();
        if (arguments.length != 1 || arguments[0].isBlank()) {
            throw new IllegalArgumentException("ReaGhidraSaveAnalysis requires one output path argument");
        }
        if (analysisTimeoutOccurred()) {
            println("REA analysis cache skipped: auto-analysis did not complete");
            return;
        }
        if (!(currentProgram instanceof DomainObjectAdapterDB program)) {
            println("REA analysis cache skipped: the Program cannot be packed");
            return;
        }
        Path target = Path.of(arguments[0]);
        Path partial = target.resolveSibling(target.getFileName() + "." + ProcessHandle.current().pid() + ".partial");
        try {
            Files.createDirectories(target.getParent());
            // Packing refuses an open transaction, and GhidraScript wraps run() in one.
            end(true);
            try {
                program.saveToPackedFile(partial.toFile(), monitor);
            } finally {
                start();
            }
            Files.move(partial, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception failure) {
            Files.deleteIfExists(partial);
            println("REA analysis cache not written: " + failure.getMessage());
        }
    }
}
