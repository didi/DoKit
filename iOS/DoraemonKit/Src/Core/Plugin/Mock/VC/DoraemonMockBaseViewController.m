//
//  DoraemonMockBaseViewController.m
//  AFNetworking
//
//  Created by didi on 2019/11/7.
//

#import "DoraemonMockBaseViewController.h"
#import "DoraemonDefine.h"
#import "DoraemonMockManager.h"

@interface DoraemonMockBaseViewController ()<DoraemonMockSearchViewDelegate,DoraemonMockFilterButtonDelegate,DoraemonMockFilterBgroundDelegate>
@property (nonatomic, assign) CGFloat padding_left;

@end

@implementation DoraemonMockBaseViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"数据Mock");
    _padding_left = kDoraemonSizeFrom750_Landscape(32);
    _searchView = [[DoraemonMockSearchView alloc] initWithFrame:CGRectMake(_padding_left, self.bigTitleView.doraemon_bottom + _padding_left, self.view.doraemon_width-2* _padding_left, kDoraemonSizeFrom750_Landscape(100))];
    _searchView.delegate = self;
    [self.view addSubview:_searchView];
    
    _leftButton = [[DoraemonMockFilterButton alloc] initWithFrame:CGRectMake(0, _searchView.doraemon_bottom, self.view.doraemon_width/2, kDoraemonSizeFrom750_Landscape(126))];
    [_leftButton renderUIWithTitle:DoraemonLocalizedString(@"接口分组")];
    _leftButton.delegate = self;
    _leftButton.tag = 0;
    [self.view addSubview:_leftButton];

    _rightButton = [[DoraemonMockFilterButton alloc] initWithFrame:CGRectMake(_leftButton.doraemon_right, _searchView.doraemon_bottom, self.view.doraemon_width/2, kDoraemonSizeFrom750_Landscape(126))];
    [_rightButton renderUIWithTitle:DoraemonLocalizedString(@"开关状态")];
    _rightButton.delegate = self;
    _rightButton.tag = 1;
    [self.view addSubview:_rightButton];

    _listView = [[DoraemonMockFilterListView alloc] initWithFrame:CGRectMake(0, _leftButton.doraemon_bottom, self.view.doraemon_width, self.view.doraemon_height - _leftButton.doraemon_bottom)];
    _listView.delegate = self;
    [self.view addSubview:_listView];

    _sepeatorLine = [[UIView alloc] initWithFrame:CGRectMake(0, _leftButton.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750_Landscape(12))];
    _sepeatorLine.backgroundColor = [UIColor doraemon_bg];
    [self.view addSubview:_sepeatorLine];
}

- (BOOL)needBigTitleView{
    return YES;
}

- (void)showList:(DoraemonMockFilterButton *)filterBtn{
    _listView.tag = filterBtn.tag;
    _listView.selectedIndex = filterBtn.selectedItemIndex;
    [self.view addSubview:_listView];
    [_listView showList: [self getItemArray:filterBtn.tag]];
}

- (NSArray *)getItemArray:(NSInteger)tag{
    NSArray *dataArray;
    if (tag == 0) {
        dataArray = [DoraemonMockManager sharedInstance].groups;
    }else{
        dataArray = [DoraemonMockManager sharedInstance].states;
    }
    return dataArray;
}

- (void)closeList{
    if(_rightButton.down){
        _rightButton.selectedItemIndex = _listView.selectedIndex;
        [_rightButton setDropdown:NO];
    }else{
        _leftButton.selectedItemIndex = _listView.selectedIndex;
        [_leftButton setDropdown:NO];
    }
    [_listView closeList];
}

#pragma mark - DoraemonMockSearchViewDelegate
- (void)searchViewInputChange:(NSString *)text{
    
}

#pragma mark --DoraemonMockFilterBgroundDelegate
- (void)filterBgroundClick{
    [self closeList];
}

#pragma mark --DoraemonMockFilterBgroundDelegate
- (void)filterSelectedClick{
    [self closeList];
}

#pragma mark - DoraemonMockHalfButton
- (void)filterBtnClick:(id)sender{
    if(_rightButton.down&&_leftButton.down){
        if(sender==_rightButton){
            _leftButton.selectedItemIndex = _listView.selectedIndex;
            [self showList:_rightButton];
            [_leftButton setDropdown:NO];
            return;
        }
        _rightButton.selectedItemIndex = _listView.selectedIndex;
        [self showList:_leftButton];
        [_rightButton setDropdown:NO];
    }
    else if(_rightButton.down){
        [self showList:_rightButton];
    }
    else if(_leftButton.down){
        [self showList:_leftButton];
    }else{
        if(sender==_leftButton){
            _leftButton.selectedItemIndex = _listView.selectedIndex;
        }
        else{
            _rightButton.selectedItemIndex = _listView.selectedIndex;
        }
        [_listView closeList];
    }
}

@end
