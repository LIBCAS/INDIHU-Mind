export const imageUploadCallBack = (file: Blob) =>
  new Promise((resolve, _reject) => {
    var reader = new FileReader();
    reader.readAsDataURL(file);
    const img = new Image();
    reader.onload = () => {
      const src =
        typeof reader.result === "string" ? reader.result : "undefined";
      img.src = src;
      resolve({
        data: {
          link: img.src,
        },
      });
    };
  });
