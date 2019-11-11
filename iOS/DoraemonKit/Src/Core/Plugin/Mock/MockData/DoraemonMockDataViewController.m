//
//  DoraemonMockDataViewController.m
//  AFNetworking
//
//  Created by didi on 2019/11/5.
//

#import "DoraemonMockDataViewController.h"
#import "DoraemonMockDetailListView.h"
#import "DoraemonDefine.h"


@interface DoraemonMockDataViewController()

@property (nonatomic, strong) DoraemonMockDetailListView *detailView;
@property (nonatomic, assign) CGFloat padding_left;

@end

@implementation DoraemonMockDataViewController

- (void)viewDidLoad{
    [super viewDidLoad];
    
    _detailView = [[DoraemonMockDetailListView alloc] initWithFrame:CGRectMake(0, self.sepeatorLine.doraemon_bottom, self.view.doraemon_width, self.view.doraemon_height - self.sepeatorLine.doraemon_bottom)];
    [self.view addSubview:_detailView];
}


@end
