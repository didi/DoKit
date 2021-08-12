//
//  DoraemonSanboxDetailViewController.m
//  DoraemonKit
//
//  Created by yixiang on 2018/6/20.
//

#import <AVKit/AVKit.h>
#import <AVFoundation/AVFoundation.h>
#import "DoraemonSanboxDetailViewController.h"
#import "DoraemonToastUtil.h"
#import "UIView+Doraemon.h"
#import "Doraemoni18NUtil.h"
#import <QuickLook/QuickLook.h>
#import "DoraemonDBManager.h"
#import "DoraemonDBTableViewController.h"
#import "DoraemonManager.h"

@interface DoraemonSanboxDetailViewController ()<QLPreviewControllerDelegate,QLPreviewControllerDataSource,UITableViewDelegate,UITableViewDataSource>

@property (nonatomic, strong) UIImageView *imageView;
@property (nonatomic, strong) UITextView *textView;
@property (nonatomic, copy) NSArray *tableNameArray;
@property (nonatomic, strong) UITableView *dbTableNameTableView;

@end

@implementation DoraemonSanboxDetailViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"文件预览");
    
    if (self.filePath.length > 0) {
        NSString *path = self.filePath;
        if ([path hasSuffix:@".strings"] || [path hasSuffix:@".plist"]) {
            // 文本文件
            [self setContent:[[NSDictionary dictionaryWithContentsOfFile:path] description]];
        } else if ([path hasSuffix:@".DB"] || [path hasSuffix:@".db"] || [path hasSuffix:@".sqlite"] || [path hasSuffix:@".SQLITE"] || [self isSQLiteFile:self.filePath]) {
            // 数据库文件
            self.title = DoraemonLocalizedString(@"数据库预览");
            [self browseDBTable];
        } else if ([[path lowercaseString] hasSuffix:@".webp"]) {
            // webp文件
            DoraemonWebpHandleBlock block = [DoraemonManager shareInstance].webpHandleBlock;
            if (block) {
                UIImage *img = [DoraemonManager shareInstance].webpHandleBlock(path);
                [self setOriginalImage:img];
            } else {
                [self setContent:@"webp need implement webpHandleBlock in DoraemonManager"];
            }
        } else {
            // 其他文件 尝试使用 QLPreviewController 进行打开
            QLPreviewController *previewController = [[QLPreviewController alloc] init];
            previewController.delegate = self;
            previewController.dataSource = self;
            [self presentViewController:previewController animated:YES completion:nil];
        }
    } else {
        [DoraemonToastUtil showToast:DoraemonLocalizedString(@"文件不存在") inView:self.view];
    }
}

- (void)setContent:(NSString *)text {
    _textView = [[UITextView alloc] initWithFrame:self.view.bounds];
    _textView.font = [UIFont systemFontOfSize:12.0f];
    _textView.textColor = [UIColor blackColor];
    _textView.textAlignment = NSTextAlignmentLeft;
    _textView.editable = NO;
    _textView.dataDetectorTypes = UIDataDetectorTypeLink;
    _textView.scrollEnabled = YES;
    _textView.backgroundColor = [UIColor whiteColor];
    _textView.layer.borderColor = [UIColor grayColor].CGColor;
    _textView.layer.borderWidth = 2.0f;
    _textView.text = text;
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
    if (@available(iOS 13.0, *)) {
        _textView.textColor = [UIColor labelColor];
        _textView.backgroundColor = [UIColor systemBackgroundColor];
    } else {
#endif
        _textView.textColor = [UIColor blackColor];
        _textView.backgroundColor = [UIColor whiteColor];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
    }
#endif
    [self.view addSubview:_textView];
}

- (void)setOriginalImage:(UIImage *)originalImage{
    if (!originalImage) {
        return;
    }
    CGFloat viewWidth = self.view.doraemon_width;
    CGFloat viewHeight = self.view.doraemon_height;
    CGFloat imageWidth = originalImage.size.width;
    CGFloat imageHeight = originalImage.size.height;
    BOOL isPortrait = imageHeight / viewHeight > imageWidth / viewWidth;
    CGFloat scaledImageWidth, scaledImageHeight;
    CGFloat x,y;
    CGFloat imageScale;
    if (isPortrait) {//图片竖屏分量比较大
        imageScale = imageHeight / viewHeight;
        scaledImageHeight = viewHeight;
        scaledImageWidth = imageWidth / imageScale;
        x = (viewWidth - scaledImageWidth) / 2;
        y = 0;
    }else{//图片横屏分量比较大
        imageScale = imageWidth / viewWidth;
        scaledImageWidth = viewWidth;
        scaledImageHeight = imageHeight / imageScale;
        x = 0;
        y = (viewHeight - scaledImageHeight) / 2;
    }
    _imageView = [[UIImageView alloc] initWithFrame:CGRectMake(x, y, scaledImageWidth, scaledImageHeight)];
    _imageView.image = originalImage;
    _imageView.userInteractionEnabled = YES;
    [self.view addSubview:_imageView];
}

//浏览数据库中所有数据表
- (void)browseDBTable{
    [DoraemonDBManager shareManager].dbPath = self.filePath;
    self.tableNameArray = [[DoraemonDBManager shareManager] tablesAtDB];
    self.dbTableNameTableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, self.view.doraemon_width, self.view.doraemon_height) style:UITableViewStylePlain];
//    self.dbTableNameTableView.backgroundColor = [UIColor whiteColor];
    self.dbTableNameTableView.delegate = self;
    self.dbTableNameTableView.dataSource = self;
    [self.view addSubview:self.dbTableNameTableView];
}

#pragma mark - UITableViewDelegate,UITableViewDataSource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.tableNameArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    static NSString *identifer = @"db_table_name";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifer];
    if (!cell) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifer];
    }
    cell.textLabel.text = self.tableNameArray[indexPath.row];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    [tableView deselectRowAtIndexPath:indexPath animated:YES];
    NSString *tableName = [self.tableNameArray objectAtIndex:indexPath.row];
    [DoraemonDBManager shareManager].tableName = tableName;
    
    DoraemonDBTableViewController *vc = [[DoraemonDBTableViewController alloc] init];
    [self.navigationController pushViewController:vc animated:YES];
}


#pragma mark - QLPreviewControllerDataSource, QLPreviewControllerDelegate
- (NSInteger)numberOfPreviewItemsInPreviewController:(QLPreviewController *)controller{
    return 1;
}
- (id)previewController:(QLPreviewController *)controller previewItemAtIndex:(NSInteger)index{
    return [NSURL fileURLWithPath:self.filePath];
}
- (void)previewControllerDidDismiss:(QLPreviewController *)controller {
    [self leftNavBackClick:nil];
}

#pragma mark - Private Methods
- (BOOL)isSQLiteFile:(NSString *)file {
    NSFileHandle *fileHandle = [NSFileHandle fileHandleForReadingAtPath:file];
    if (!fileHandle) {
        return NO;
    }
    NSData *data = [fileHandle readDataOfLength:16];
    NSString *str = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    [fileHandle closeFile];
    if ([str isEqual:@"SQLite format 3\0"]) {
        return YES;
    } else {
        return NO;
    }
}

@end
