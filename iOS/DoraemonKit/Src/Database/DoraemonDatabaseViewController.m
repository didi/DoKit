//
//  DoraemonDatabaseViewController.m
//  DoraemonKit
//
//  Created by wentian on 2019/7/11.
//

#import "DoraemonDatabaseViewController.h"
#import "DoraemonDefine.h"
#import "DebugDatabaseManager.h"

@interface DoraemonDatabaseViewController ()

@property (nonatomic, strong) UIButton *startButton;
@property (nonatomic, strong) UILabel *tipLabel;
@property (nonatomic, strong) UILabel *stateLable;

@end

@implementation DoraemonDatabaseViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    
    self.title = @"DBView";
    
    _startButton = [UIButton buttonWithType:UIButtonTypeCustom];
    _startButton.frame = CGRectMake(15, self.bigTitleView.doraemon_bottom + 50, self.view.doraemon_width - 40, 50);
    _startButton.backgroundColor = [UIColor doraemon_colorWithHex:0x4889db];
    [_startButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    [_startButton addTarget:self action:@selector(startServer) forControlEvents:UIControlEventTouchUpInside];
    _startButton.layer.cornerRadius = 4;
    _startButton.layer.masksToBounds = YES;
    [self.view addSubview:_startButton];
    
    _tipLabel = [[UILabel alloc] initWithFrame:CGRectMake(_startButton.doraemon_left, _startButton.doraemon_bottom + 40, _startButton.doraemon_width, 150)];
    _tipLabel.textColor = [UIColor doraemon_colorWithHex:0x808080];
    _tipLabel.numberOfLines = 0;
    
    [self.view addSubview:_tipLabel];
    
    [self updateStateDesc];
}

- (void)startServer {
    
    if ([[DebugDatabaseManager shared] isRunning]) {
        [[DebugDatabaseManager shared] stop];
    }else {
        [[DebugDatabaseManager shared] startServerOnPort:9002];
    }
    
    [self updateStateDesc];
}

- (void)updateStateDesc {
    
    BOOL isrunning = [[DebugDatabaseManager shared] isRunning];
    
    [_startButton setTitle:isrunning ? DoraemonLocalizedString(@"关闭服务") : DoraemonLocalizedString(@"开启服务") forState:UIControlStateNormal];
    
    NSString *tips = @"";
    if (isrunning) {
        tips = [NSString stringWithFormat:@"%@：\n\n%@：\n\n%@", DoraemonLocalizedString(@"温馨提示"), DoraemonLocalizedString(@"你可以通过下面地址访问"), [DebugDatabaseManager shared].serverURL];
    }else {
        tips = [NSString stringWithFormat:@"%@：\n\n%@！\n%@!\n", DoraemonLocalizedString(@"温馨提示"), DoraemonLocalizedString(@"服务已关闭"), DoraemonLocalizedString(@"请保证当前手机和PC处在同一个局域网内")];
    }
    _tipLabel.text = tips;
}

- (BOOL)needBigTitleView{
    return YES;
}

@end
