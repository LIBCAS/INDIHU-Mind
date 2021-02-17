package core.file.mime;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MimeTypeRecognizer {

    private final Map<String, String> extensionMap = new HashMap<>();

    public MimeTypeRecognizer() {
        // MS Office
        extensionMap.put("doc", SpecificMimeTypes.DOC);
        extensionMap.put("dot", SpecificMimeTypes.DOT);
        extensionMap.put("docx", SpecificMimeTypes.DOCX);
        extensionMap.put("dotx", SpecificMimeTypes.DOTX);
        extensionMap.put("docm", SpecificMimeTypes.DOCM);
        extensionMap.put("dotm", SpecificMimeTypes.DOTM);
        extensionMap.put("xls", SpecificMimeTypes.XLS);
        extensionMap.put("xlt", SpecificMimeTypes.XLT);
        extensionMap.put("xla", SpecificMimeTypes.XLA);
        extensionMap.put("xlsx", SpecificMimeTypes.XLSX);
        extensionMap.put("xltx", SpecificMimeTypes.XLTX);
        extensionMap.put("xlsm", SpecificMimeTypes.XLSM);
        extensionMap.put("xltm", SpecificMimeTypes.XLTM);
        extensionMap.put("xlam", SpecificMimeTypes.XLAM);
        extensionMap.put("xlsb", SpecificMimeTypes.XLSB);
        extensionMap.put("ppt", SpecificMimeTypes.PPT);
        extensionMap.put("pot", SpecificMimeTypes.POT);
        extensionMap.put("pps", SpecificMimeTypes.PPS);
        extensionMap.put("ppa", SpecificMimeTypes.PPA);
        extensionMap.put("pptx", SpecificMimeTypes.PPTX);
        extensionMap.put("potx", SpecificMimeTypes.POTX);
        extensionMap.put("ppsx", SpecificMimeTypes.PPSX);
        extensionMap.put("ppam", SpecificMimeTypes.PPAM);
        extensionMap.put("pptm", SpecificMimeTypes.PPTM);
        extensionMap.put("potm", SpecificMimeTypes.POTM);
        extensionMap.put("ppsm", SpecificMimeTypes.PPSM);
        // Open Office
        extensionMap.put("odt", SpecificMimeTypes.ODT);
        extensionMap.put("ott", SpecificMimeTypes.OTT);
        extensionMap.put("oth", SpecificMimeTypes.OTH);
        extensionMap.put("odm", SpecificMimeTypes.ODM);
        extensionMap.put("odg", SpecificMimeTypes.ODG);
        extensionMap.put("otg", SpecificMimeTypes.OTG);
        extensionMap.put("odp", SpecificMimeTypes.ODP);
        extensionMap.put("otp", SpecificMimeTypes.OTP);
        extensionMap.put("ods", SpecificMimeTypes.ODS);
        extensionMap.put("ots", SpecificMimeTypes.OTS);
        extensionMap.put("odc", SpecificMimeTypes.ODC);
        extensionMap.put("odf", SpecificMimeTypes.ODF);
        extensionMap.put("odb", SpecificMimeTypes.ODB);
        extensionMap.put("odi", SpecificMimeTypes.ODI);
        extensionMap.put("oxt", SpecificMimeTypes.OXT);
        // Draft
        extensionMap.put("draft", "text/draft");
    }

    public String recognize(String extension) {
        if (extension == null) {
            return null;
        }
        return extensionMap.get(extension);
    }

    public String recognize(String extension, String mimeType) {
        String recognizeType = this.recognize(extension);
        if (recognizeType == null) {
            return mimeType;
        }
        return recognizeType;
    }

}
