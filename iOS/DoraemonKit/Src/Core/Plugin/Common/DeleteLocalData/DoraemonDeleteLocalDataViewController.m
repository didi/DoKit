//
//  DoraemonDeleteLocalDataViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/11/22.
//

#import "DoraemonDeleteLocalDataViewController.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonUtil.h"
#import "DoraemonCellButton.h"
#import "DoraemonDefine.h"

@interface DoraemonDeleteLocalDataViewController ()<DoraemonCellButtonDelegate>

@property (nonatomic, strong) DoraemonCellButton *cellBtn;

@end

@implementation DoraemonDeleteLocalDataViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"清除本地数据");
    
    _cellBtn = [[DoraemonCellButton alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750_Landscape(104))];
    [_cellBtn renderUIWithTitle:DoraemonLocalizedString(@"清除本地数据")];
    [_cellBtn renderUIWithRightContent:[self getHomeDirFileSize]];
    _cellBtn.delegate = self;
    [_cellBtn needDownLine];
    [self.view addSubview:_cellBtn];
}

- (BOOL)needBigTitleView{
    return YES;
}

- (void)cellBtnClick:(id)sender{
    [self deleteFile];
}

- (void)deleteFile{
    
    __weak typeof(self) weakSelf = self;
    UIAlertController * alertController = [UIAlertController alertControllerWithTitle:DoraemonLocalizedString(@"提示") message:DoraemonLocalizedString(@"确定要删除本地数据") preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:DoraemonLocalizedString(@"取消") style:UIAlertActionStyleCancel handler:nil];
    UIAlertAction *okAction = [UIAlertAction actionWithTitle:DoraemonLocalizedString(@"确定") style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [weakSelf.cellBtn renderUIWithRightContent:DoraemonLocalizedString(@"正在清理中")];
        [DoraemonUtil clearLocalDatas];
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [weakSelf.cellBtn renderUIWithRightContent:[self getHomeDirFileSize]];
        });
    }];
    [alertController addAction:cancelAction];
    [alertController addAction:okAction];
    [self presentViewController:alertController animated:YES completion:nil];
}

- (NSString *)getHomeDirFileSize{
    // 获取沙盒主目录路径
    NSString *homeDir = NSHomeDirectory();
    
    DoraemonUtil *util = [[DoraemonUtil alloc] init];
    [util getFileSizeWithPath:homeDir];
    NSInteger fileSize = util.fileSize;
    NSString *fileSizeString = [NSByteCountFormatter stringFromByteCount:fileSize countStyle: NSByteCountFormatterCountStyleFile];
    return fileSizeString;
}


@end
