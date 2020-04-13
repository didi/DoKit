//
//  DoraemonStartTimeProfilerViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by didi on 2020/4/13.
//

#import "DoraemonStartTimeProfilerViewController.h"
#import "DoraemonDefine.h"
#import "DoraemonHealthManager.h"

@interface DoraemonStartTimeProfilerViewController ()

@property (nonatomic, strong) UITextView *contentLabel;

@end

@implementation DoraemonStartTimeProfilerViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"启动耗时");
    [self setRightNavTitle:DoraemonLocalizedString(@"导出")];
    
    NSString *costDetail = [DoraemonHealthManager sharedInstance].costDetail;
    
    _contentLabel = [[UITextView alloc] initWithFrame:self.view.bounds];
    _contentLabel.textColor = [UIColor doraemon_black_2];
    _contentLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(16)];
    _contentLabel.text = costDetail;
    
    [self.view addSubview:_contentLabel];
}

- (void)rightNavTitleClick:(id)clickView{
    [self export:_contentLabel.text];
}

- (void)export:(NSString *)text {
    NSString *cachesDir = [NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES) firstObject];
    NSString *tempDir = [cachesDir stringByAppendingPathComponent:@"DoKitTemp"];
    NSFileManager *fileManager = [NSFileManager defaultManager];
    BOOL isDir = NO;
    BOOL existed = [fileManager fileExistsAtPath:tempDir isDirectory:&isDir];
    if(!(isDir && existed)){
        [fileManager createDirectoryAtPath:tempDir withIntermediateDirectories:YES attributes:nil error:nil];
    }
    NSString *path = [tempDir stringByAppendingPathComponent:@"startCostDetail.txt"];
    BOOL writeSuccess = [text writeToFile:path atomically:YES encoding:NSUTF8StringEncoding error:nil];
    if (writeSuccess) {
        [DoraemonUtil shareFileWithPath:path formVC:self];
    }
}

@end
