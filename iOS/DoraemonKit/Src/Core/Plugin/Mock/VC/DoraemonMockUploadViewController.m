//
//  DoraemonMockUploadDataViewController.m
//  AFNetworking
//
//  Created by didi on 2019/11/7.
//

#import "DoraemonMockUploadViewController.h"
#import "DoraemonDefine.h"
#import "DoraemonMockUploadListView.h"
#import "DoraemonMockDataPreviewViewController.h"

@interface DoraemonMockUploadViewController ()<DoraemonMockUploadListViewDelegate>

@property (nonatomic, strong) DoraemonMockUploadListView *detailView;


@end

@implementation DoraemonMockUploadViewController

- (void)viewDidLoad{
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"上传模板");
    
    _detailView = [[DoraemonMockUploadListView alloc] initWithFrame:CGRectMake(0, self.sepeatorLine.doraemon_bottom, self.view.doraemon_width, self.view.doraemon_height - self.sepeatorLine.doraemon_bottom)];
    _detailView.delegate = self;
    [self.view addSubview:_detailView];
}

#pragma mark - DoraemonMockUploadListViewDelegate
- (void)previewClick:(NSString *)result{
    if (result && result.length>0) {
        DoraemonMockDataPreviewViewController *vc = [[DoraemonMockDataPreviewViewController alloc] init];
        vc.result = result;
        [self.navigationController pushViewController:vc animated:YES];
    }
}

@end
