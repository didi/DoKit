//
//  DoraemonMockDataViewController.m
//  AFNetworking
//
//  Created by didi on 2019/11/5.
//

#import "DoraemonMockAPIViewController.h"
#import "DoraemonDefine.h"
#import "DoraemonMockApiListView.h"


@interface DoraemonMockAPIViewController()

@property (nonatomic, strong) DoraemonMockApiListView *detailView;
@property (nonatomic, assign) CGFloat padding_left;

@end

@implementation DoraemonMockAPIViewController

- (void)viewDidLoad{
    [super viewDidLoad];
    
    _detailView = [[DoraemonMockApiListView alloc] initWithFrame:CGRectMake(0, self.sepeatorLine.doraemon_bottom, self.view.doraemon_width, self.view.doraemon_height - self.sepeatorLine.doraemon_bottom)];
    [self.view addSubview:_detailView];
}


@end
