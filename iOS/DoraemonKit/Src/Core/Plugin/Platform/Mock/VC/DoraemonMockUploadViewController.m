//
//  DoraemonMockUploadDataViewController.m
//  DoraemonKit
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
    self.title = DoraemonLocalizedString(@"上传模版");
    
    self.searchView.textField.text = [DoraemonMockManager sharedInstance].uploadSearchText;
    
    _detailView = [[DoraemonMockUploadListView alloc] initWithFrame:CGRectMake(0, self.sepeatorLine.doraemon_bottom, self.view.doraemon_width, self.view.doraemon_height - self.sepeatorLine.doraemon_bottom)];
    _detailView.delegate = self;
    [self.view addSubview:_detailView];
    
    NSString *leftTitle = [DoraemonMockManager sharedInstance].uploadGroup;
    if ([leftTitle isEqualToString:@"所有"]) {
        leftTitle = @"接口分组";
    }
    [self.leftButton renderUIWithTitle:leftTitle];
    
    NSString *rightTitle = [DoraemonMockManager sharedInstance].uploadState;
    if ([rightTitle isEqualToString:@"所有"]) {
        rightTitle = @"开关状态";
    }
    [self.rightButton renderUIWithTitle:rightTitle];
}

#pragma mark - DoraemonMockUploadListViewDelegate
- (void)previewClick:(DoraemonMockUpLoadModel *)uploadModel{
    NSString *result = uploadModel.result;
    if (result && result.length>0) {
        DoraemonMockDataPreviewViewController *vc = [[DoraemonMockDataPreviewViewController alloc] init];
        vc.upLoadModel = uploadModel;
        [self.navigationController pushViewController:vc animated:YES];
    }else{
        [DoraemonToastUtil showToastBlack:@"数据预览为空" inView:self.view];
    }
}

#pragma mark --DoraemonMockFilterBgroundDelegate
- (void)filterSelectedClick{
    if(self.rightButton.down){
        NSString *rightTitle = [DoraemonMockManager sharedInstance].states[self.listView.selectedIndex];
        [DoraemonMockManager sharedInstance].uploadState = rightTitle;
        if ([rightTitle isEqualToString:@"所有"]) {
            rightTitle = @"开关状态";
        }
        [self.rightButton renderUIWithTitle:rightTitle];
    }else{
        NSString *leftTitle = [DoraemonMockManager sharedInstance].groups[self.listView.selectedIndex];
        [DoraemonMockManager sharedInstance].uploadGroup = leftTitle;
        if ([leftTitle isEqualToString:@"所有"]) {
            leftTitle = @"接口分组";
        }
        [self.leftButton renderUIWithTitle:leftTitle];
    }
    
    [super filterSelectedClick];
    
    [_detailView reloadUI];
}

#pragma mark - DoraemonMockSearchViewDelegate
- (void)searchViewInputChange:(NSString *)text{
    [DoraemonMockManager sharedInstance].uploadSearchText = text;
    [self.detailView reloadUI];
}

@end
