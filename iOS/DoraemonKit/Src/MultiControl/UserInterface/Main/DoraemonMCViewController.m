//
//  DoraemonMCViewController.m
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/12.
//

#import "DoraemonMCViewController.h"
#import "DoraemonQRScanView.h"
#import "DoraemonDefine.h"
#import "DoraemonMCServer.h"
#import "DoraemonMCClient.h"
#import "DoraemonAppInfoUtil.h"
#import "DoraemonManager.h"
#import "UIColor+Doraemon.h"
#import <DoraemonKit/DKQRCodeScanViewController.h>
#import <DoraemonKit/DKMultiControlStreamManager.h>

@interface DoraemonMCViewController () <DKMultiControlStreamManagerStateListener>

@property(nonatomic, nullable, weak) UIButton *webSocketButton;

@property(nonatomic, nullable, weak) UISwitch *masterSwitch;

@property(nullable, nonatomic, strong) UIImageView *banner;

- (void)webSocketButtonHandler:(nullable id)sender;

- (void)masterSwitchHandler:(nullable id)sender;

@end

@implementation DoraemonMCViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self refreshUI];
}

- (UIImageView *)banner {
    if (!_banner) {
        CGFloat marginTop = 100 ;
        UIImage *bannerImg =  [UIImage doraemon_xcassetImageNamed:@"dk_mc_banner"];
        CGFloat bannerHeight = 300 ;
        if (bannerImg.size.width) {
            bannerHeight = self.view.bounds.size.width * bannerImg.size.height/bannerImg.size.width;
        }
        _banner = [[UIImageView alloc] initWithFrame:CGRectMake(0, marginTop, self.view.bounds.size.width, bannerHeight)];
        _banner.image = bannerImg;
        _banner.contentMode = UIViewContentModeScaleAspectFit;
        [self.view addSubview:_banner];
    }
    
    return _banner;
}

- (void)refreshUI {
    self.title = @"一机多控";

    [self banner];
    CGFloat top = CGRectGetMaxY(self.banner.frame) + 30;
    UIButton *webSocketButton = [[UIButton alloc] initWithFrame:CGRectMake(40, top, UIApplication.sharedApplication.delegate.window.frame.size.width - 80, 40)];
    self.webSocketButton = webSocketButton;
    webSocketButton.layer.cornerRadius = 5;
    webSocketButton.backgroundColor = UIColor.doraemon_blue;
    [webSocketButton setTitleColor:UIColor.whiteColor forState:UIControlStateNormal];
    webSocketButton.titleLabel.font = [UIFont systemFontOfSize:18];
    [webSocketButton addTarget:self action:@selector(webSocketButtonHandler:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:webSocketButton];
    top += webSocketButton.frame.size.height;
    
    //主机选项
    top += 20;
    UILabel *masterLabel = [[UILabel alloc] initWithFrame:CGRectMake(40, top, 100, 30)];
    masterLabel.text = @"主机模式";
    masterLabel.textColor = UIColor.doraemon_black_2;
    [self.view addSubview:masterLabel];
    UISwitch *masterSwitch = [[UISwitch alloc] initWithFrame:CGRectMake(170, top, 60, 31)];
    [self.view addSubview:masterSwitch];
    self.masterSwitch = masterSwitch;
    [masterSwitch addTarget:self action:@selector(masterSwitchHandler:) forControlEvents:UIControlEventValueChanged];
    [DKMultiControlStreamManager.sharedInstance registerMultiControlStreamManagerStateListener:self];
}

#pragma mark - Event
- (void)masterSwitchHandler:(id)sender {
    if (((UISwitch *) sender).isOn) {
        [DKMultiControlStreamManager.sharedInstance changeToMaster];
    } else {
        [DKMultiControlStreamManager.sharedInstance changeToSlave];
    }
}

- (void)webSocketButtonHandler:(id)sender {
    if (DKMultiControlStreamManager.sharedInstance.state == DKMultiControlStreamManagerStateClosed) {
#if TARGET_OS_SIMULATOR
        UIAlertController *alertController = [UIAlertController alertControllerWithTitle:@"连接 DoKit Studio" message:@"请输入 ip 地址点击确定连接" preferredStyle:UIAlertControllerStyleAlert];
        __weak typeof(alertController) weakAlertController = alertController;
        [alertController addAction:[UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            typeof(weakAlertController) alertController = weakAlertController;
            NSString *ip = alertController.textFields.firstObject.text;
            if (!ip) {
                return;
            }
            NSURL *url = [NSURL URLWithString:ip];
            if (!url) {
                return;
            }
            [DKMultiControlStreamManager.sharedInstance enableMultiControlWithUrl:url];
        }]];
        [alertController addAction:[UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:nil]];
        [alertController addTextFieldWithConfigurationHandler:^(UITextField * _Nonnull textField) {
            textField.placeholder = @"请输入 ip 地址";
        }];
        [self presentViewController:alertController animated:YES completion:nil];
#else
        DKQRCodeScanViewController *qrCodeScanViewController = [[DKQRCodeScanViewController alloc] init];
        qrCodeScanViewController.completionBlock = ^(NSString *decodedString) {
            if (!decodedString) {
                return;
            }
            NSURL *url = [NSURL URLWithString:decodedString];
            if (!url) {
                return;
            }
            [DKMultiControlStreamManager.sharedInstance enableMultiControlWithUrl:url];
        };
        [self showViewController:qrCodeScanViewController sender:sender];
#endif
    } else {
        [DKMultiControlStreamManager.sharedInstance disableMultiControl];
    }
}

- (void)changeToState:(DKMultiControlStreamManagerState)state {
    switch (state) {
        case DKMultiControlStreamManagerStateClosed:
            [self.masterSwitch setOn:NO animated:YES];
            [self.webSocketButton setTitle:@"连接服务器" forState:UIControlStateNormal];
            break;
        case DKMultiControlStreamManagerStateSlave:
            [self.masterSwitch setOn:NO animated:YES];
            [self.webSocketButton setTitle:@"断开连接" forState:UIControlStateNormal];
            break;
        case DKMultiControlStreamManagerStateMaster:
            [self.masterSwitch setOn:YES animated:YES];
            [self.webSocketButton setTitle:@"断开连接" forState:UIControlStateNormal];
            break;
            
        default:
            break;
    }
}

- (BOOL)needBigTitleView{
    return YES;
}

@end
