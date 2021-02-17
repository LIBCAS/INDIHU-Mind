package cz.cas.lib.indihumind.document;

public enum AttachmentFileProviderType {

    DROPBOX,
    GOOGLE_DRIVE,

    /** File uploaded from user's personal computer. **/
    LOCAL,

    /** File downloaded from user-provided URL **/
    URL

}
