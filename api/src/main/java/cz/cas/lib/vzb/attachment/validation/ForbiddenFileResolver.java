package cz.cas.lib.vzb.attachment.validation;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.io.FilenameUtils;

import java.util.Set;

import static core.util.Utils.asSet;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ForbiddenFileResolver {

    /**
     * Set of extensions for checking for potentially dangerous extensions
     *
     * @implNote Set as a data structure for a quick retrieval (time-complexity)
     */
    public static final Set<String> FORBIDDEN_EXTENSIONS = asSet(
            "application", "bat", "bin", "cmd", "com", "cpl", "dll", "exe", "gadget", "hta", "inf", "ins", "inx",
            "isu", "jar", "job", "js", "jse", "lnk", "msc", "msh", "msh1", "msh1xml", "msh2", "msh2xml", "mshxml",
            "msi", "msi", "msp", "msp", "mst", "paf", "pif", "ps1", "ps1xml", "ps2", "ps2xml", "psc1", "psc2",
            "py", "pyc", "pyw", "reg", "scf", "scr", "sct", "shb", "shs", "u3p", "vb", "vbe", "vbs", "vbscript",
            "ws", "wsc", "wsf", "wsh"
    );

    public static boolean isFileExtensionForbidden(@NonNull String originalFilename) {
        String extension = FilenameUtils.getExtension(originalFilename).toLowerCase();
        if (extension.isEmpty()) return true;
        return FORBIDDEN_EXTENSIONS.contains(extension);
    }

}