import { common } from '@kit.AbilityKit';
import { fileIo as fs, fileUri } from '@kit.CoreFileKit';
import { util } from '@kit.ArkTS';
import { DoKitFileUtil } from '../../util/DoKitFileUtil';
import { DoKitManager } from '../../core/DoKitManager';
import { SandboxFileItem, SandboxFileType } from './SandboxFileModels';

class SandboxRootEntry {
  label: string = '';
  path: string = '';

  constructor(label: string, path: string) {
    this.label = label;
    this.path = path;
  }
}

/**
 * 沙盒文件浏览辅助，对标 Android FileExplorerFragment + DoKitFileUtil
 */
export class FileExplorerHelper {
  private static readonly TEXT_SUFFIXES: string[] =
    ['txt', 'log', 'json', 'xml', 'html', 'htm', 'js', 'ts', 'ets', 'md', 'css', 'csv', 'properties'];
  private static readonly IMAGE_SUFFIXES: string[] = ['jpg', 'jpeg', 'png', 'gif', 'webp', 'bmp'];
  private static readonly DB_SUFFIXES: string[] = ['db', 'sqlite', 'sqlite3'];

  static getRootEntries(): SandboxFileItem[] {
    const context = DoKitManager.getContext();
    if (context === null) {
      return [];
    }
    const roots: SandboxRootEntry[] = FileExplorerHelper.buildRootEntries(context);
    const items: SandboxFileItem[] = [];
    for (let i = 0; i < roots.length; i++) {
      const root = roots[i];
      if (!FileExplorerHelper.pathExists(root.path)) {
        continue;
      }
      const item = new SandboxFileItem();
      item.name = root.label;
      item.path = root.path;
      item.rootLabel = root.label;
      item.isDirectory = true;
      item.fileType = SandboxFileType.DIRECTORY;
      items.push(item);
    }
    return items;
  }

  static listDirectory(dirPath: string): SandboxFileItem[] {
    if (!FileExplorerHelper.pathExists(dirPath)) {
      return [];
    }
    let names: string[] = [];
    try {
      names = fs.listFileSync(dirPath);
    } catch (_err) {
      return [];
    }
    const items: SandboxFileItem[] = [];
    for (let i = 0; i < names.length; i++) {
      const name = names[i];
      const fullPath = FileExplorerHelper.joinPath(dirPath, name);
      try {
        const stat = fs.statSync(fullPath);
        const item = new SandboxFileItem();
        item.name = name;
        item.path = fullPath;
        item.isDirectory = stat.isDirectory();
        item.size = stat.size;
        item.mtime = stat.mtime;
        item.fileType = FileExplorerHelper.resolveFileType(name, item.isDirectory);
        items.push(item);
      } catch (_err) {
        // skip unreadable entry
      }
    }
    return items;
  }

  static getDisplayTitle(path: string, isRoot: boolean): string {
    if (isRoot) {
      return '沙盒浏览';
    }
    const index = path.lastIndexOf('/');
    if (index < 0) {
      return path;
    }
    return path.substring(index + 1);
  }

  static getParentPath(path: string): string {
    const index = path.lastIndexOf('/');
    if (index <= 0) {
      return '';
    }
    return path.substring(0, index);
  }

  static isRootPath(path: string): boolean {
    const roots = FileExplorerHelper.getRootPathSet();
    return roots.has(path);
  }

  /**
   * 对标 Android TextDetailFragment.FileReadTask：流式按块读，异常由调用方捕获。
   * 不一次 new ArrayBuffer(整文件)，避免 apk 等大文件 OOM。
   */
  static readTextPreview(path: string): string {
    const CHUNK_SIZE = 64 * 1024;
    // 平台限制：整文件进字符串仍会 OOM，最多读入前 1MB 供预览（安卓是按行往 RecyclerView 追加）
    const MAX_READ = 1024 * 1024;
    let file: fs.File | undefined = undefined;
    try {
      const stat = fs.statSync(path);
      if (stat.size <= 0) {
        return '';
      }
      file = fs.openSync(path, fs.OpenMode.READ_ONLY);
      const decoder = util.TextDecoder.create('utf-8', { ignoreBOM: true });
      const buffer = new ArrayBuffer(CHUNK_SIZE);
      let text = '';
      let totalRead = 0;
      const limit = Math.min(Number(stat.size), MAX_READ);
      while (totalRead < limit) {
        const readLen = fs.readSync(file.fd, buffer);
        if (readLen <= 0) {
          break;
        }
        const sliceLen = Math.min(readLen, limit - totalRead);
        const stream = totalRead + sliceLen < limit;
        text += decoder.decodeToString(new Uint8Array(buffer, 0, sliceLen), { stream: stream });
        totalRead += sliceLen;
        if (readLen < CHUNK_SIZE) {
          break;
        }
      }
      return text;
    } finally {
      if (file !== undefined) {
        fs.closeSync(file.fd);
      }
    }
  }

  static getFileUri(path: string): string {
    return fileUri.getUriFromPath(path);
  }

  static resolveIconResource(item: SandboxFileItem): Resource {
    if (item.isDirectory) {
      return $r('app.media.dk_dir_icon');
    }
    const suffix = DoKitFileUtil.getSuffix(item.name);
    if (suffix === DoKitFileUtil.JPG) {
      return $r('app.media.dk_jpg_icon');
    }
    if (suffix === DoKitFileUtil.TXT) {
      return $r('app.media.dk_txt_icon');
    }
    if (suffix === DoKitFileUtil.DB) {
      return $r('app.media.dk_file_db');
    }
    return $r('app.media.dk_file_icon');
  }

  private static buildRootEntries(context: common.UIAbilityContext): SandboxRootEntry[] {
    const entries: SandboxRootEntry[] = [];
    const parentPath = FileExplorerHelper.getParentPath(context.filesDir);
    if (parentPath.length > 0) {
      entries.push(new SandboxRootEntry(FileExplorerHelper.getDisplayTitle(parentPath, false), parentPath));
    }
    if (context.cacheDir.length > 0) {
      entries.push(new SandboxRootEntry(FileExplorerHelper.getDisplayTitle(context.cacheDir, false), context.cacheDir));
    }
    if (context.filesDir.length > 0) {
      entries.push(new SandboxRootEntry(FileExplorerHelper.getDisplayTitle(context.filesDir, false), context.filesDir));
    }
    return entries;
  }

  private static getRootPathSet(): Set<string> {
    const set: Set<string> = new Set<string>();
    const roots = FileExplorerHelper.getRootEntries();
    for (let i = 0; i < roots.length; i++) {
      set.add(roots[i].path);
    }
    return set;
  }

  static pathExists(path: string): boolean {
    try {
      fs.accessSync(path);
      return true;
    } catch (_err) {
      return false;
    }
  }

  private static joinPath(base: string, name: string): string {
    if (base.endsWith('/')) {
      return `${base}${name}`;
    }
    return `${base}/${name}`;
  }

  private static resolveFileType(name: string, isDirectory: boolean): SandboxFileType {
    if (isDirectory) {
      return SandboxFileType.DIRECTORY;
    }
    const suffix = FileExplorerHelper.getSuffix(name);
    if (FileExplorerHelper.DB_SUFFIXES.indexOf(suffix) >= 0) {
      return SandboxFileType.DATABASE;
    }
    if (FileExplorerHelper.IMAGE_SUFFIXES.indexOf(suffix) >= 0) {
      return SandboxFileType.IMAGE;
    }
    if (FileExplorerHelper.TEXT_SUFFIXES.indexOf(suffix) >= 0) {
      return SandboxFileType.TEXT;
    }
    return SandboxFileType.OTHER;
  }

  private static getSuffix(name: string): string {
    const index = name.lastIndexOf('.');
    if (index < 0 || index >= name.length - 1) {
      return '';
    }
    return name.substring(index + 1).toLowerCase();
  }
}
