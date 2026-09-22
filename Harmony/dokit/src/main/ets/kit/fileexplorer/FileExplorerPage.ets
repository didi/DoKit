import promptAction from '@ohos.promptAction';
import { BundleKey } from '../../constant/BundleKey';
import { DoKitManager } from '../../core/DoKitManager';
import { DoKitBundle, FragmentCloseHandler } from '../../core/FragmentRenderer';
import { DoKitFileUtil } from '../../util/DoKitFileUtil';
import { FileExplorerHelper } from './FileExplorerHelper';
import { FileExplorerNavigator } from './FileExplorerNavigator';
import { SandboxFileItem } from './SandboxFileModels';

/** 对标 Android FileExplorerFragment */
export class FileExplorerFragment {
  static readonly CLASS_NAME: string = 'FileExplorerFragment';
}

/**
 * 沙盒浏览页，对标 Android FileExplorerFragment
 */
@Component
export struct FileExplorerPage {
  /** 初始目录，对标 Android BundleKey.DIR_KEY */
  initialPath: string = '';
  onClose: () => void = () => {};
  @State private pageTitle: string = '沙盒浏览';
  @State private fileItems: SandboxFileItem[] = [];
  private currentPath: string = '';
  @State private showTextPreview: boolean = false;
  @State private showImagePreview: boolean = false;
  @State private previewTitle: string = '';
  @State private previewText: string = '';
  @State private previewImageUri: string = '';
  @State private showVideoPreview: boolean = false;
  @State private previewVideoUri: string = '';
  @State private showChooseDialog: boolean = false;
  @State private chooseIsFile: boolean = false;
  private choosePath: string = '';
  /** 对标 Android mCurDir == null 时的根列表路径；有 DIR_KEY 时是该目录的子项 */
  private rootPaths: string[] = [];
  private dirKeyPath: string = '';

  aboutToAppear(): void {
    let path = this.initialPath;
    if (path.length === 0) {
      path = FileExplorerNavigator.consumeInitialPath();
    }
    if (path.length > 0 && FileExplorerHelper.pathExists(path)) {
      this.dirKeyPath = path;
      const children = FileExplorerHelper.listDirectory(path);
      this.rootPaths = [];
      for (let i = 0; i < children.length; i++) {
        this.rootPaths.push(children[i].path);
      }
      this.showRootList();
      return;
    }
    const roots = FileExplorerHelper.getRootEntries();
    this.rootPaths = [];
    for (let i = 0; i < roots.length; i++) {
      this.rootPaths.push(roots[i].path);
    }
    this.showRootList();
  }

  private showRootList(): void {
    this.currentPath = '';
    this.pageTitle = '沙盒浏览';
    if (this.dirKeyPath.length > 0) {
      this.fileItems = FileExplorerHelper.listDirectory(this.dirKeyPath);
      return;
    }
    this.fileItems = FileExplorerHelper.getRootEntries();
  }

  private loadDirectory(path: string): void {
    this.currentPath = path;
    this.pageTitle = FileExplorerHelper.getDisplayTitle(path, false);
    this.fileItems = FileExplorerHelper.listDirectory(path);
  }

  private closePreview(): void {
    this.showTextPreview = false;
    this.showImagePreview = false;
    this.showVideoPreview = false;
    this.previewTitle = '';
    this.previewText = '';
    this.previewImageUri = '';
    this.previewVideoUri = '';
  }

  private handleBack(): void {
    if (this.showChooseDialog) {
      this.showChooseDialog = false;
      return;
    }
    if (this.showTextPreview || this.showImagePreview || this.showVideoPreview) {
      this.closePreview();
      return;
    }
    if (this.currentPath.length === 0) {
      this.onClose();
      return;
    }
    if (this.rootPaths.indexOf(this.currentPath) >= 0) {
      this.showRootList();
      return;
    }
    const parentPath = FileExplorerHelper.getParentPath(this.currentPath);
    if (parentPath.length === 0) {
      this.showRootList();
      return;
    }
    this.loadDirectory(parentPath);
  }

  private parentDirName(path: string): string {
    const slash = path.lastIndexOf('/');
    if (slash <= 0) {
      return '';
    }
    const parent = path.substring(0, slash);
    const parentSlash = parent.lastIndexOf('/');
    if (parentSlash < 0) {
      return parent;
    }
    return parent.substring(parentSlash + 1);
  }

  private openText(item: SandboxFileItem): void {
    try {
      this.previewTitle = item.name;
      this.previewText = FileExplorerHelper.readTextPreview(item.path);
      this.showTextPreview = true;
    } catch (_err) {
      promptAction.showToast({ message: '文件读取失败', duration: 1500 });
    }
  }

  /** 对标 Android FileExplorerFragment.onViewClick */
  private onItemClick(item: SandboxFileItem): void {
    if (item.isDirectory) {
      this.loadDirectory(item.path);
      return;
    }
    if (DoKitFileUtil.isImage(item.name)) {
      this.previewTitle = item.name;
      this.previewImageUri = FileExplorerHelper.getFileUri(item.path);
      this.showImagePreview = true;
      return;
    }
    if (DoKitFileUtil.isDB(item.name)) {
      promptAction.showToast({ message: '数据库预览开发中', duration: 1500 });
      return;
    }
    if (DoKitFileUtil.isVideo(item.name)) {
      this.previewTitle = item.name;
      this.previewVideoUri = FileExplorerHelper.getFileUri(item.path);
      this.showVideoPreview = true;
      return;
    }
    if (DoKitFileUtil.isSp(item.name, this.parentDirName(item.path))) {
      this.openText(item);
      return;
    }
    this.openText(item);
  }

  /** 对标 Android FileExplorerChooseDialog：文件可分享，文件和目录可删除 */
  private onItemLongPress(item: SandboxFileItem): void {
    this.choosePath = item.path;
    this.chooseIsFile = !item.isDirectory;
    this.showChooseDialog = true;
  }

  private shareChooseFile(): void {
    this.showChooseDialog = false;
    const context = DoKitManager.getContext();
    if (context === null || this.choosePath.length === 0) {
      return;
    }
    DoKitFileUtil.systemShare(context, this.choosePath);
  }

  private deleteChoosePath(): void {
    const path = this.choosePath;
    this.showChooseDialog = false;
    if (path.length === 0) {
      return;
    }
    DoKitFileUtil.deleteDirectory(path);
    if (this.currentPath.length === 0) {
      return;
    }
    this.loadDirectory(this.currentPath);
  }

  @Builder
  private buildFileRow(item: SandboxFileItem) {
    Row() {
      Image(FileExplorerHelper.resolveIconResource(item))
        .width(36)
        .height(36)
        .objectFit(ImageFit.Contain)
        .margin({ right: 12 })

      Text(item.name)
        .fontSize(15)
        .fontColor('#333333')
        .maxLines(1)
        .textOverflow({ overflow: TextOverflow.Ellipsis })
        .layoutWeight(1)

      if (item.isDirectory) {
        Image($r('app.media.dk_more_icon'))
          .width(16)
          .height(16)
          .objectFit(ImageFit.Contain)
      }
    }
    .width('100%')
    .height(56)
    .padding({ left: 16, right: 16 })
    .onClick(() => {
      this.onItemClick(item);
    })
    .gesture(
      LongPressGesture({ repeat: false })
        .onAction(() => {
          this.onItemLongPress(item);
        })
    )
  }

  @Builder
  private buildFileList() {
    List() {
      ForEach(this.fileItems, (item: SandboxFileItem) => {
        ListItem() {
          Column() {
            this.buildFileRow(item)
            Divider().color('#EEEEEE').margin({ left: 16, right: 16 })
          }
        }
      }, (item: SandboxFileItem) => item.path)
    }
    .width('100%')
    .layoutWeight(1)
    .divider({ strokeWidth: 0 })
  }

  @Builder
  private buildTextPreview() {
    Column() {
      Row() {
        Text('←')
          .fontSize(22)
          .fontColor('#333333')
          .padding({ left: 12, right: 12, top: 8, bottom: 8 })
          .onClick(() => {
            this.closePreview();
          })

        Text(this.previewTitle)
          .fontSize(16)
          .fontColor('#111111')
          .layoutWeight(1)
          .maxLines(1)
          .textOverflow({ overflow: TextOverflow.Ellipsis })
      }
      .width('100%')
      .height(48)
      .backgroundColor(Color.White)

      Scroll() {
        Text(this.previewText)
          .fontSize(13)
          .fontColor('#333333')
          .width('100%')
          .padding(16)
      }
      .layoutWeight(1)
      .scrollBar(BarState.Auto)
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#F5F6F8')
  }

  @Builder
  private buildImagePreview() {
    Column() {
      Row() {
        Text('←')
          .fontSize(22)
          .fontColor('#333333')
          .padding({ left: 12, right: 12, top: 8, bottom: 8 })
          .onClick(() => {
            this.closePreview();
          })

        Text(this.previewTitle)
          .fontSize(16)
          .fontColor('#111111')
          .layoutWeight(1)
          .maxLines(1)
          .textOverflow({ overflow: TextOverflow.Ellipsis })
      }
      .width('100%')
      .height(48)
      .backgroundColor(Color.White)

      Scroll() {
        Image(this.previewImageUri)
          .width('100%')
          .objectFit(ImageFit.Contain)
          .margin(16)
      }
      .layoutWeight(1)
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#000000')
  }

  @Builder
  private buildVideoPreview() {
    Column() {
      Row() {
        Text('←')
          .fontSize(22)
          .fontColor('#FFFFFF')
          .padding({ left: 12, right: 12, top: 8, bottom: 8 })
          .onClick(() => {
            this.closePreview();
          })

        Text(this.previewTitle)
          .fontSize(16)
          .fontColor('#FFFFFF')
          .layoutWeight(1)
          .maxLines(1)
          .textOverflow({ overflow: TextOverflow.Ellipsis })
      }
      .width('100%')
      .height(48)

      Video({ src: this.previewVideoUri })
        .width('100%')
        .height(240)
        .controls(true)
    }
    .width('100%')
    .height('100%')
    .backgroundColor('#000000')
  }

  @Builder
  private buildChooseDialog() {
    Stack() {
      Column()
        .width('100%')
        .height('100%')
        .backgroundColor('#66000000')
        .onClick(() => {
          this.showChooseDialog = false;
        })

      Column() {
        if (this.chooseIsFile) {
          Text('分享')
            .fontSize(16)
            .fontColor('#333333')
            .width('100%')
            .height(48)
            .textAlign(TextAlign.Center)
            .onClick(() => {
              this.shareChooseFile();
            })
          Divider().color('#EEEEEE')
        }
        Text('删除')
          .fontSize(16)
          .fontColor('#FF3B30')
          .width('100%')
          .height(48)
          .textAlign(TextAlign.Center)
          .onClick(() => {
            this.deleteChoosePath();
          })
      }
      .width('80%')
      .backgroundColor(Color.White)
      .borderRadius(8)
    }
    .width('100%')
    .height('100%')
  }

  build() {
    Stack() {
      Column() {
      if (this.showTextPreview) {
        this.buildTextPreview()
      } else if (this.showImagePreview) {
        this.buildImagePreview()
      } else if (this.showVideoPreview) {
        this.buildVideoPreview()
      } else {
        Row() {
          Text('←')
            .fontSize(22)
            .fontColor('#333333')
            .padding({ left: 12, right: 12, top: 8, bottom: 8 })
            .onClick(() => {
              this.handleBack();
            })

          Text(this.pageTitle)
            .fontSize(18)
            .fontWeight(FontWeight.Medium)
            .fontColor('#111111')
            .layoutWeight(1)
            .textAlign(TextAlign.Center)
            .maxLines(1)
            .textOverflow({ overflow: TextOverflow.Ellipsis })

          Text('✕')
            .fontSize(20)
            .fontColor('#666666')
            .padding(8)
            .onClick(() => {
              this.onClose();
            })
        }
        .width('100%')
        .height(56)
        .padding({ right: 8 })
        .backgroundColor(Color.White)

        if (this.fileItems.length === 0) {
          Column() {
            Text('目录为空')
              .fontSize(14)
              .fontColor('#999999')
          }
          .width('100%')
          .layoutWeight(1)
          .justifyContent(FlexAlign.Center)
        } else {
          this.buildFileList()
        }
      }
      }
      .width('100%')
      .height('100%')
      .backgroundColor(Color.White)

      if (this.showChooseDialog) {
        this.buildChooseDialog()
      }
    }
    .width('100%')
    .height('100%')
  }
}

@Builder
export function renderFileExplorerFragment(bundle: DoKitBundle, onClose: FragmentCloseHandler) {
  FileExplorerPage({
    initialPath: String(bundle[BundleKey.DIR_KEY] ?? ''),
    onClose
  });
}
