//
//  DoraemonMockBaseViewController.h
//  DoraemonKit
//
//  Created by didi on 2019/11/7.
//

#import "DoraemonBaseViewController.h"
#import "DoraemonMockFilterListView.h"
#import "DoraemonMockFilterButton.h"
#import "DoraemonMockSearchView.h"

@interface DoraemonMockBaseViewController : DoraemonBaseViewController

@property (nonatomic, strong) DoraemonMockSearchView *searchView;
@property (nonatomic, strong) UIView *sepeatorLine;
@property (nonatomic, strong) DoraemonMockFilterListView *listView;
@property (nonatomic, strong) DoraemonMockFilterButton *leftButton;
@property (nonatomic, strong) DoraemonMockFilterButton *rightButton;

- (void)filterSelectedClick;

@end

