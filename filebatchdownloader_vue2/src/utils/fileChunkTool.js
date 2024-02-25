// FileUtilsPlugin.js

let selectedDirectory = null; // 全局变量保存目录句柄

// 请求用户选择文件夹
async function pickDirectory() {
  selectedDirectory = selectedDirectory || (await window.showDirectoryPicker());
  return selectedDirectory;
}

// 在所选文件夹内创建新文件
async function createFile(directoryHandle, fileName, content) {
  const fileHandle = await directoryHandle.getFileHandle(fileName, { create: true });
  const writable = await fileHandle.createWritable();
  await writable.write(content);
  await writable.close();
}

// 递归创建文件夹
async function createFolder(directoryHandle, folderPath) {
  const parts = folderPath.split('/');
  let currentHandle = directoryHandle;

  for (const part of parts) {
    if (part !== '') {
      currentHandle = await currentHandle.getDirectoryHandle(part, { create: true });
    }
  }

  return currentHandle;
}

// 在指定路径的文件夹内创建新文件（带数据）
async function createFileByPathWithData(directoryHandle, filePath, base64Data) {
  const arrayBuffer = base64ToArrayBuffer(base64Data);
  const parts = filePath.split('/');
  const fileName = parts.pop(); // 获取路径中的文件名
  const folderPath = parts.join('/'); // 获取路径中的文件夹路径

  const folderHandle = await createFolder(directoryHandle, folderPath);
  const fileHandle = await folderHandle.getFileHandle(fileName, { create: true });

  const writable = await fileHandle.createWritable();
  await writable.write(arrayBuffer);
  await writable.close();
}

function base64ToArrayBuffer(base64) {
  const binaryString = atob(base64);
  const length = binaryString.length;
  const arrayBuffer = new ArrayBuffer(length);
  const uint8Array = new Uint8Array(arrayBuffer);

  for (let i = 0; i < length; i++) {
    uint8Array[i] = binaryString.charCodeAt(i);
  }

  return arrayBuffer;
}

// 导出插件
export default {
  install(Vue) {
    Vue.prototype.$fileUtils = {
      pickDirectory,
      createFile,
      createFolder,
      createFileByPathWithData,
    };
  },
};
