export interface FileProps {
  id: string;
  name: string;
  type: string;
  link: string;
  ordinalNumber: number;
  providerId?: string;
  providerType?: "DROPBOX" | "GOOGLE_DRIVE" | "LOCAL";
  // if providerType LOCAL, then file has content
  content?: File;
}

export interface FileDropboxProps {
  bytes: number;
  icon: string;
  id: string;
  isDir: boolean;
  link: string;
  name: string;
}

export interface FileGoogleDriveProps {
  description: string;
  embedUrl: string;
  iconUrl: string;
  id: string;
  lastEditedUtc: number;
  mimeType: string;
  name: string;
  parentId: string;
  serviceId: string;
  sizeBytes: number;
  type: string;
  url: string;
}

export interface FileGoogleDriveActionProps {
  action: string;
  docs: FileGoogleDriveProps[];
  viewToken: any[];
}
