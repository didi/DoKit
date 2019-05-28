//
//  DoraemonH5ViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/5/4.
//

#import "DoraemonH5ViewController.h"
#import "UIView+Doraemon.h"
#import "DoraemonToastUtil.h"
#import "DoraemonDefine.h"
#import "Doraemoni18NUtil.h"
#import "UITextView+Placeholder.h"
#import "DoraemonDefaultWebViewController.h"
#import "DoraemonManager.h"
#import "DoraemonQRCodeViewController.h"
#import "DoraemonCacheManager.h"

@interface DoraemonH5ViewController () <UITableViewDelegate, UITableViewDataSource>

@property (nonatomic, strong) UITextView *h5UrlTextView;
@property (nonatomic, strong) UIView *lineView;
@property (nonatomic, strong) UIButton *jumpBtn;
/// 扫码跳转
@property (nonatomic, strong) UIButton *scanJumpBtn;
@property (nonatomic, strong) UITableView *tableView;
@property (nonatomic, strong) NSArray *dataSource;
@end

@implementation DoraemonH5ViewController

- (UITableView *)tableView {
    if (!_tableView) {
        _tableView = [[UITableView alloc] init];
//        _tableView.backgroundColor = [UIColor orangeColor];
        _tableView.delegate = self;
        _tableView.dataSource = self;
        [_tableView registerClass:[UITableViewCell class] forCellReuseIdentifier:@"cell"];
//        _tableView.tableFooterView = [UIView new];
        [self.view addSubview:_tableView];
    }
    return _tableView;
}

- (UIButton *)scanJumpBtn {
    if (!_scanJumpBtn) {
        _scanJumpBtn = [UIButton buttonWithType:(UIButtonTypeCustom)];
        [_scanJumpBtn setBackgroundImage:[UIImage doraemon_imageNamed:@"doraemon_scan"] forState:(UIControlStateNormal)];
        [_scanJumpBtn addTarget:self action:@selector(clickScan) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:_scanJumpBtn];
    }
    return _scanJumpBtn;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    self.title = DoraemonLocalizedString(@"H5任意门");
    
    _h5UrlTextView = [[UITextView alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom + kDoraemonSizeFrom750(32), self.view.doraemon_width, kDoraemonSizeFrom750(358))];
    _h5UrlTextView.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(32)];
    _h5UrlTextView.placeholder = DoraemonLocalizedString(@"请输入网址");
    [self.view addSubview:_h5UrlTextView];
    _h5UrlTextView.keyboardType = UIKeyboardTypeURL;
    _h5UrlTextView.autocorrectionType = UITextAutocorrectionTypeNo;
    _h5UrlTextView.keyboardAppearance = UIKeyboardAppearanceDark;
    _h5UrlTextView.autocapitalizationType = UITextAutocapitalizationTypeNone;
//    _h5UrlTextView.backgroundColor = [UIColor purpleColor];
    _lineView = [[UIView alloc] initWithFrame:CGRectMake(0, _h5UrlTextView.doraemon_bottom, self.view.doraemon_width, kDoraemonSizeFrom750(1))];
    _lineView.backgroundColor = [UIColor doraemon_colorWithHex:0x000000 andAlpha:0.1];
    [self.view addSubview:_lineView];
    
    _jumpBtn = [[UIButton alloc] initWithFrame:CGRectMake(kDoraemonSizeFrom750(30), self.view.doraemon_height-kDoraemonSizeFrom750(30 + 100), self.view.doraemon_width-2*kDoraemonSizeFrom750(30), kDoraemonSizeFrom750(100))];
    _jumpBtn.backgroundColor = [UIColor doraemon_colorWithHexString:@"#337CC4"];
    [_jumpBtn setTitle:DoraemonLocalizedString(@"点击跳转") forState:UIControlStateNormal];
    [_jumpBtn addTarget:self action:@selector(jump) forControlEvents:UIControlEventTouchUpInside];
    _jumpBtn.layer.cornerRadius = kDoraemonSizeFrom750(8);
    [self.view addSubview:_jumpBtn];
    
    self.scanJumpBtn.frame = CGRectMake(self.view.doraemon_width - kDoraemonSizeFrom750(38.6 + 33.2), _lineView.doraemon_top - kDoraemonSizeFrom750(38.6 + 33.2), kDoraemonSizeFrom750(38.6), kDoraemonSizeFrom750(38.6));
    
    self.tableView.frame = CGRectMake(0, _lineView.doraemon_bottom + kDoraemonSizeFrom750(32), self.view.doraemon_width, _jumpBtn.doraemon_top - _lineView.doraemon_bottom - kDoraemonSizeFrom750(32));
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    self.dataSource = [[DoraemonCacheManager sharedInstance] h5historicalRecord];
    [self.tableView reloadData];
}

- (BOOL)needBigTitleView{
    return YES;
}

- (void)clickScan {
    
    if ([DoraemonAppInfoUtil isSimulator]) {
        [DoraemonToastUtil showToast:DoraemonLocalizedString(@"模拟器不支持扫码功能") inView:self.view];
        return;
    }
    
    DoraemonQRCodeViewController *vc = [[DoraemonQRCodeViewController alloc] init];
    __weak typeof(self) weakSelf = self;
    vc.QRCodeBlock = ^(NSString * _Nonnull QRCodeResult) {
        weakSelf.h5UrlTextView.text = QRCodeResult;
        [weakSelf jump];
    };
    [self presentViewController:[[UINavigationController alloc] initWithRootViewController:vc] animated:YES completion:nil];
}

- (void)jump{
    if (_h5UrlTextView.text.length==0) {
        [DoraemonToastUtil showToast:DoraemonLocalizedString(@"h5链接不能为空") inView:self.view];
        return;
    }
    
    if (![NSURL URLWithString:_h5UrlTextView.text]) {
        [DoraemonToastUtil showToast:DoraemonLocalizedString(@"h5链接有误") inView:self.view];
        return;
    }
    
    NSString *h5Url = _h5UrlTextView.text;
    if ([DoraemonManager shareInstance].h5DoorBlock) {
        [self leftNavBackClick:nil];
        [DoraemonManager shareInstance].h5DoorBlock(h5Url);
    }else{
        [[DoraemonCacheManager sharedInstance] saveH5historicalRecordWithText:h5Url];
        
        DoraemonDefaultWebViewController *vc = [[DoraemonDefaultWebViewController alloc] init];
        vc.h5Url = [self urlCorrectionWithURL:h5Url];
        [self.navigationController pushViewController:vc animated:YES];
    }
}
#pragma mark - tableView

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.dataSource.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"cell"];

    if (self.dataSource.count > indexPath.row) {
        cell.textLabel.text = self.dataSource[indexPath.row];
    } else {
        cell.textLabel.text = @"默认值";
    }
    cell.textLabel.textColor = [UIColor doraemon_colorWithHex:0x333333 andAlpha:1];
    cell.textLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(30)];
    cell.imageView.image = [UIImage doraemon_imageNamed:@"doraemon_search"];
    return cell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    if (self.dataSource.count > indexPath.row) {
        _h5UrlTextView.text = self.dataSource[indexPath.row];
        [self jump];
    }
}

- (CGFloat)tableView:(UITableView *)tableView heightForFooterInSection:(NSInteger)section {
    return kDoraemonSizeFrom750(40 + 33);
}

- (UIView *)tableView:(UITableView *)tableView viewForFooterInSection:(NSInteger)section {
    UIView *footerView = [[UIView alloc] init];
    footerView.frame = CGRectMake(0, 0, self.view.doraemon_width, kDoraemonSizeFrom750(40 + 33));
//    footerView.backgroundColor = [UIColor redColor];
    
    UIButton *clearButton = [UIButton buttonWithType:(UIButtonTypeCustom)];
    clearButton.frame = CGRectMake((self.view.doraemon_width - kDoraemonSizeFrom750(300))/2, kDoraemonSizeFrom750(40), kDoraemonSizeFrom750(300), kDoraemonSizeFrom750(33));
//    clearButton.backgroundColor = [UIColor orangeColor];
    [clearButton setTitle:DoraemonLocalizedString(@"清除搜索历史") forState:(UIControlStateNormal)];
    [clearButton setTitleColor:[UIColor doraemon_colorWithHex:0x999999 andAlpha:1] forState:UIControlStateNormal];
    clearButton.titleLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(24)];
    [clearButton addTarget:self action:@selector(clearRecord) forControlEvents:(UIControlEventTouchUpInside)];
    [footerView addSubview:clearButton];
    
    return footerView;
}

/// 清除记录
- (void)clearRecord {
    [[DoraemonCacheManager sharedInstance] clearAllH5historicalRecord];
    self.dataSource = [[DoraemonCacheManager sharedInstance] h5historicalRecord];
    [self.tableView reloadData];
}

/// 地址修正
- (NSString *)urlCorrectionWithURL:(NSString *)URL {
    if (!URL || URL.length <= 0) { return URL; }
    
    if (![URL hasPrefix:@"http://"] && ![URL hasPrefix:@"https://"]) {
        return [NSString stringWithFormat:@"https://%@",URL];
    }
    
    if ([URL hasPrefix:@":"]) {
        return [NSString stringWithFormat:@"https%@",URL];
    }
    
    if ([URL hasPrefix:@"//"]) {
        return [NSString stringWithFormat:@"https:%@",URL];
    }
    
    if ([URL hasPrefix:@"/"]) {
        return [NSString stringWithFormat:@"https:/%@",URL];
    }
    
    return URL;
}

@end
