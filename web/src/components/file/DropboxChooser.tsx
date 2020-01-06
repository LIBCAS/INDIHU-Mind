import React, { Component } from "react";
import loadScript from "load-script";
import Button from "@material-ui/core/Button";

import { Dropbox } from "../../components/icons/Dropbox";

// https://www.npmjs.com/package/react-dropbox-chooser
const DROPBOX_SDK_URL = "https://www.dropbox.com/static/api/2/dropins.js";
const SCRIPT_ID = "dropboxjs";

let scriptLoadingStarted = false;

interface DropboxChooserProps {
  appKey: string;
  success: Function;
  cancel?: Function;
  linkType?: "preview" | "direct";
  multiselect?: boolean;
  extensions?: string[];
  disabled?: boolean;
}

// read more
// https://www.dropbox.com/developers/chooser
export class DropboxChooser extends Component<DropboxChooserProps> {
  static defaultProps = {
    cancel: () => {},
    linkType: "preview",
    multiselect: false,
    disabled: false
  };

  constructor(props: DropboxChooserProps) {
    super(props);
    this.onChoose = this.onChoose.bind(this);
  }

  componentDidMount() {
    if (!this.isDropboxReady() && !scriptLoadingStarted) {
      scriptLoadingStarted = true;
      loadScript(DROPBOX_SDK_URL, {
        attrs: {
          id: SCRIPT_ID,
          "data-app-key": this.props.appKey
        }
      });
    }
  }

  isDropboxReady() {
    // @ts-ignore
    return !!window.Dropbox;
  }

  onChoose() {
    if (!this.isDropboxReady() || this.props.disabled) {
      return null;
    }

    const { success, cancel, linkType, multiselect, extensions } = this.props;
    // @ts-ignore
    window.Dropbox.choose({
      success,
      cancel,
      linkType,
      multiselect,
      extensions
    });
  }

  render() {
    return (
      <Button type="button" onClick={this.onChoose}>
        Dropbox <Dropbox style={{ marginLeft: "5px", height: "30px" }} />
      </Button>
    );
  }
}
