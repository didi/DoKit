//
//  DoraemonMockUploadDataViewController.m
//  AFNetworking
//
//  Created by didi on 2019/11/7.
//

#import "DoraemonMockUploadDataViewController.h"

#import "DoraemonMockDetailListView.h"
#import "DoraemonDefine.h"

@interface DoraemonMockUploadDataViewController ()

@property (nonatomic, strong) DoraemonMockDetailListView *detailView;


@end

@implementation DoraemonMockUploadDataViewController

- (void)viewDidLoad{
    [super viewDidLoad];
    
    _detailView = [[DoraemonMockDetailListView alloc] initWithFrame:CGRectMake(0, self.sepeatorLine.doraemon_bottom, self.view.doraemon_width, self.view.doraemon_height - self.sepeatorLine.doraemon_bottom)];
    [self.view addSubview:_detailView];
}

@end
