import promptAction from '@ohos.promptAction';
import { FileExplorerHelper } from './FileExplorerHelper';
import { SandboxFileItem, SandboxFileType } from './SandboxFileModels';

/**
 * 沙盒浏览页，对标 Android FileExplorerFragment
 */
@Component
export struct FileExplorerPage {
  onClose: () => void = () => {};
  @State private isRoot: boolean = true;
  @State private currentPath: string = '';
  @State private pageTitle: string = '沙盒浏览';
  @State private fileItems: SandboxFileItem[] = [];
  @State private showTextPreview: boolean = false;
  @State private showImagePreview: boolean = false;
  @State private previewTitle: string = '';
  @State private previewText: string = '';
  @State private previewImageUri: string = '';

  aboutToAppear(): void {
    this.loadRoot();
  }

  private loadRoot(): void {
    this.isRoot = true;
    this.currentPath = '';
    this.pageTitle = '沙盒浏览';
    this.fileItems = FileExplorerHelper.getRootEntries();
  }

  private loadDirectory(path: string): void {
    this.isRoot = false;
    this.currentPath = path;
    this.pageTitle = FileExplorerHelper.getDisplayTitle(path, false);
    this.fileItems = FileExplorerHelper.listDirectory(path);
  }

  private closePreview(): void {
    this.showTextPreview = false;
    this.showImagePreview = false;
    this.previewTitle = '';
    this.previewText = '';
    this.previewImageUri = '';
  }

  private handleBack(): void {
    if (this.showTextPreview || this.showImagePreview) {
      this.closePreview();
      return;
    }
    if (this.isRoot) {
      this.onClose();
      return;
    }
    if (FileExplorerHelper.isRootPath(this.currentPath)) {
      this.loadRoot();
      return;
    }
    const parentPath = FileExplorerHelper.getParentPath(this.currentPath);
    if (parentPath.length === 0 || FileExplorerHelper.isRootPath(parentPath)) {
      this.loadRoot();
      return;
    }
    this.loadDirectory(parentPath);
  }

  private onItemClick(item: SandboxFileItem): void {
    if (item.isDirectory) {
      this.loadDirectory(item.path);
      return;
    }
    if (item.fileType === SandboxFileType.TEXT) {
      try {
        this.previewTitle = item.name;
        this.previewText = FileExplorerHelper.readTextPreview(item.path);
        this.showTextPreview = true;
      } catch (_err) {
        promptAction.showToast({ message: '文件读取失败', duration: 1500 });
      }
      return;
    }
    if (item.fileType === SandboxFileType.IMAGE) {
      this.previewTitle = item.name;
      this.previewImageUri = FileExplorerHelper.getFileUri(item.path);
      this.showImagePreview = true;
      return;
    }
    if (item.fileType === SandboxFileType.DATABASE) {
      promptAction.showToast({ message: '数据库预览开发中', duration: 1500 });
      return;
    }
    promptAction.showToast({ message: '暂不支持预览该文件类型', duration: 1500 });
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

  build() {
    Column() {
      if (this.showTextPreview) {
        this.buildTextPreview()
      } else if (this.showImagePreview) {
        this.buildImagePreview()
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
  }
}
