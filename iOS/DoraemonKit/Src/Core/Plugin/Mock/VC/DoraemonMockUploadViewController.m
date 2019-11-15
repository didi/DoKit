//
//  DoraemonMockUploadDataViewController.m
//  AFNetworking
//
//  Created by didi on 2019/11/7.
//

#import "DoraemonMockUploadViewController.h"
#import "DoraemonDefine.h"
#import "DoraemonMockUploadListView.h"

@interface DoraemonMockUploadViewController ()

@property (nonatomic, strong) DoraemonMockUploadListView *detailView;


@end

@implementation DoraemonMockUploadViewController

- (void)viewDidLoad{
    [super viewDidLoad];
    
    _detailView = [[DoraemonMockUploadListView alloc] initWithFrame:CGRectMake(0, self.sepeatorLine.doraemon_bottom, self.view.doraemon_width, self.view.doraemon_height - self.sepeatorLine.doraemon_bottom)];
    [self.view addSubview:_detailView];
}

@end
