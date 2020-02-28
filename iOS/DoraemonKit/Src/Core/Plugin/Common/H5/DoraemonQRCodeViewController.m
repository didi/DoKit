//
//  DoraemonQRCodeViewController.m
//  AFNetworking
//
//  Created by love on 2019/5/22.
//

#import "DoraemonQRCodeViewController.h"
#import "DoraemonQRCodeTool.h"
#import "DoraemonDefaultWebViewController.h"

#define WEAKSELF(weakSelf)  __weak __typeof(&*self)weakSelf = self;

@interface DoraemonQRCodeViewController ()
@property (nonatomic,strong) DoraemonQRCodeTool *qrcode;
@end
@implementation DoraemonQRCodeViewController

- (void)createCode {
    WEAKSELF(weakSelf)
    self.qrcode = [DoraemonQRCodeTool shared];
    [self.qrcode QRCodeDeviceInitWithVC:self WithQRCodeWidth:0 ScanResults:^(NSString *result) {
        [weakSelf.qrcode stopScanning];
        [weakSelf dismissViewControllerAnimated:YES completion:^{
            if (weakSelf.QRCodeBlock) {
                weakSelf.QRCodeBlock(result);
            }
        }];
    }];
}

- (void)leftNavBackClick:(id)clickView {
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)viewDidLoad {
    [super viewDidLoad];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
    if (@available(iOS 13.0, *)) {
        self.view.backgroundColor = [UIColor systemBackgroundColor];
    } else {
#endif
       self.view.backgroundColor = [UIColor whiteColor];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
    }
#endif
    self.title = @"二维码扫描";

    [AVCaptureDevice requestAccessForMediaType:AVMediaTypeVideo completionHandler:^(BOOL granted) {
        
        dispatch_async(dispatch_get_main_queue(), ^{
            if (granted) {
                NSLog(@"用户允许");
                [self createCode];

                [self.qrcode startScanning];
            }else{
                //用户拒绝
                NSLog(@"用户拒绝");
            }
        });
    }];
}

@end
