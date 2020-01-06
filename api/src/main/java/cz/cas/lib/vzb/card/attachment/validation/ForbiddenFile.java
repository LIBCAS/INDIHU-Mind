package cz.cas.lib.vzb.card.attachment.validation;

import java.util.HashSet;
import java.util.Set;

import static core.util.Utils.asSet;

public final class ForbiddenFile {
    private ForbiddenFile() {
        throw new UnsupportedOperationException("ForbiddenFile class is meant to be static");
    }

    /**
     * Hashset of extensions for checking for potentially dangerous extensions
     * Hashset as a datastructure for a time-complexity
     */
    public static final Set<String> extensions = new HashSet<>(asSet(
            "application", "bat", "bin", "cmd", "com", "cpl", "dll", "exe", "gadget", "hta", "inf", "ins", "inx",
            "isu", "jar", "job", "js", "jse", "lnk", "msc", "msh", "msh1", "msh1xml", "msh2", "msh2xml", "mshxml",
            "msi", "msi", "msp", "msp", "mst", "paf", "pif", "ps1", "ps1xml", "ps2", "ps2xml", "psc1", "psc2",
            "py", "pyc", "pyw", "reg", "scf", "scr", "sct", "shb", "shs", "u3p", "vb", "vbe", "vbs", "vbscript",
            "ws", "wsc", "wsf", "wsh"
    ));
}