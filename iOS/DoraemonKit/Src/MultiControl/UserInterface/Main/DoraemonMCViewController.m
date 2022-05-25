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
#import <AFNetworking/AFNetworking.h>
#import <DoraemonKit/DKQRCodeScanViewController.h>
#import <DoraemonKit/DKMultiControlStreamManager.h>

@interface DoraemonMCViewController () <DoraemonQRScanDelegate, DKMultiControlStreamManagerStateListener>

@property (nonatomic, strong) DoraemonQRScanView *scanView;

/// 主机按钮
@property (nonatomic , strong) UIButton *masterDeviceBtn;
@property (nonatomic , strong) UIButton *masterCloseBtn;

/// 从机按钮
@property (nonatomic , strong) UIButton *assisDeviceBtn;

@property(nonatomic, nullable, weak) UIButton *webSocketButton;

@property(nonatomic, nullable, weak) UISwitch *masterSwitch;

@property (nonatomic , strong) UILabel *asssisTip;

@property (nonatomic , strong) UIButton *assisDisConnectBtn;

@property (nonatomic, strong) UIImageView *qrCodeImage;

@property (nonatomic, strong) UILabel *errorLabel;

@property (nonatomic, strong) UILabel *bottomTip;

@property (nonatomic, strong) UIActivityIndicatorView *loadingView;

@property (nonatomic , strong) UIImageView *banner;

@property (nonatomic , strong) UILabel *clientCountLabel;

- (void)webSocketButtonHandler:(nullable id)sender;

- (void)masterSwitchHandler:(nullable id)sender;

@end

@implementation DoraemonMCViewController

-(void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)wsDidiConnectHandle:(NSNotification *)notify {
    dispatch_async(dispatch_get_main_queue(), ^{
        self.clientCountLabel.text = [NSString stringWithFormat:@"当前连接从机数：%zd",[DoraemonMCServer connectCount]];
    });
}


- (void)viewDidLoad {
    [super viewDidLoad];
    [self refreshUI];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshUI) name:DoraemonMCClientStatusChanged object:nil];
}

- (UIActivityIndicatorView *)loadingView {
    if (!_loadingView) {
        _loadingView = [[UIActivityIndicatorView alloc] init];
        _loadingView.activityIndicatorViewStyle = UIActivityIndicatorViewStyleGray;
        _loadingView.center = CGPointMake(self.view.bounds.size.width/2.0, self.view.bounds.size.height/2.0);
        [self.view addSubview:_loadingView];
        _loadingView.hidden = YES;
    }
    return _loadingView;
}

- (void)startServer {
    [self.loadingView startAnimating];
    self.loadingView.hidden = NO;
    dispatch_async(dispatch_get_global_queue(0, 0), ^{
        NSError *error = nil;
        BOOL startServerSuccess = [DoraemonMCServer startServerWithError:&error];
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.loadingView stopAnimating];
            self.loadingView.hidden = YES;
            if(startServerSuccess) {
                [[DoraemonManager shareInstance] configEntryBtnBlingWithText:@"主" backColor:[UIColor doraemon_blue]];
                [DoraemonToastUtil showToastBlack:@"服务开启成功" inView:self.view];
                self.errorLabel.hidden = YES;
                self.qrCodeImage.hidden = NO;
                self.bottomTip.hidden = NO;
                [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(wsDidiConnectHandle:) name:@"com.didi.DoraemonMCServer.wsDidConnect" object:nil];
            }else {
                [[DoraemonManager shareInstance] configEntryBtnBlingWithText:nil backColor:nil];
                if (error) {
                    [DoraemonToastUtil showToastBlack:error.localizedDescription inView:self.view];
                }
                self.errorLabel.hidden = NO;
                self.qrCodeImage.hidden = YES;
                self.bottomTip.hidden = YES;
            }
            [self refreshUI];
        });

    });

}

- (DoraemonMCViewControllerWorkMode)workMode {
    if ([DoraemonMCClient isConnected]) {
        return DoraemonMCViewControllerWorkModeClient;
    }
    if ([DoraemonMCServer isOpen]) {
        return DoraemonMCViewControllerWorkModeServer;
    }
    return DoraemonMCViewControllerWorkModeNone;
}

- (UIButton *)masterDeviceBtn {
    if (!_masterDeviceBtn) {
        _masterDeviceBtn = [[UIButton alloc] initWithFrame:CGRectMake(self.view.bounds.size.width/2.0 - 50, CGRectGetMaxY(self.banner.frame) + 30, 100, 30)];
        _masterDeviceBtn.clipsToBounds = YES;
        _masterDeviceBtn.layer.cornerRadius = 5 ;
        _masterDeviceBtn.backgroundColor = [UIColor doraemon_blue];
        [_masterDeviceBtn setTitle:@"主机" forState:UIControlStateNormal];
        [_masterDeviceBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _masterDeviceBtn.titleLabel.font = [UIFont systemFontOfSize:18];
        [self.view addSubview:_masterDeviceBtn];
        [_masterDeviceBtn addTarget:self action:@selector(masterDeviceBtnClick) forControlEvents:UIControlEventTouchUpInside];
    }
    return _masterDeviceBtn;
}

- (UIButton *)assisDisConnectBtn {
    if (!_assisDisConnectBtn) {
        _assisDisConnectBtn = [[UIButton alloc] initWithFrame:CGRectMake(self.view.bounds.size.width/2.0 - 50, CGRectGetMaxY(self.asssisTip.frame) + 30, 100, 30)];
        _assisDisConnectBtn.clipsToBounds = YES;
        _assisDisConnectBtn.layer.cornerRadius = 5 ;
        _assisDisConnectBtn.backgroundColor = [UIColor doraemon_blue];
        [_assisDisConnectBtn setTitle:@"断开连接" forState:UIControlStateNormal];
        [_assisDisConnectBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _assisDisConnectBtn.titleLabel.font = [UIFont systemFontOfSize:18];
        [self.view addSubview:_assisDisConnectBtn];
        [_assisDisConnectBtn addTarget:self action:@selector(assisDisConnectBtnCLick) forControlEvents:UIControlEventTouchUpInside];
    }
    return _assisDisConnectBtn;
}


- (UIButton *)assisDeviceBtn {
    if (!_assisDeviceBtn) {
        _assisDeviceBtn = [[UIButton alloc] initWithFrame:CGRectMake(self.view.bounds.size.width/2.0 - 50, CGRectGetMaxY(self.masterDeviceBtn.frame) + 30, 100, 30)];
        _assisDeviceBtn.clipsToBounds = YES;
        _assisDeviceBtn.layer.cornerRadius = 5 ;
        _assisDeviceBtn.backgroundColor = [UIColor doraemon_blue];
        [_assisDeviceBtn setTitle:@"从机" forState:UIControlStateNormal];
        [_assisDeviceBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
        _assisDeviceBtn.titleLabel.font = [UIFont systemFontOfSize:18];
        [self.view addSubview:_assisDeviceBtn];
        [_assisDeviceBtn addTarget:self action:@selector(assisDeviceBtnClick) forControlEvents:UIControlEventTouchUpInside];
    }
    return _assisDeviceBtn;
}

- (void)masterDeviceBtnClick {
    [self startServer];
}


- (void)assisDeviceBtnClick {
#if TARGET_IPHONE_SIMULATOR  //模拟器
        UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"连接主机" message:@"请输入主机Ip地址,点击确定,连接主机" preferredStyle:UIAlertControllerStyleAlert];
        
        UIAlertAction *action = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            NSString *ip = [alert textFields].firstObject.text;
            NSString *url = [NSString stringWithFormat:@"http://%@:%zd/MyWs", ip , kDoraemonMCServerPort];
            [self dealUrl:url];
        }];
        UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:nil];
        [alert addAction:action];
        [alert addAction:cancelAction];
        [alert addTextFieldWithConfigurationHandler:^(UITextField * _Nonnull textField) {
            textField.placeholder = @"请输入主机ip地址";
        }];
        [self presentViewController:alert animated:YES completion:nil];
#else      //真机
        DoraemonQRScanView *scaner = [[DoraemonQRScanView alloc] initWithFrame:CGRectMake(0, self.bigTitleView.doraemon_bottom, self.view.doraemon_width, self.view.doraemon_height-self.bigTitleView.doraemon_bottom)];
        scaner.delegate = self;
        scaner.showScanLine = YES;
        scaner.showBorderLine = YES;
        scaner.showCornerLine = YES;
        scaner.scanRect = CGRectMake(scaner.doraemon_width/2-kDoraemonSizeFrom750(480)/2, kDoraemonSizeFrom750(195), kDoraemonSizeFrom750(480), kDoraemonSizeFrom750(480));
        [self.view addSubview:scaner];
        self.scanView = scaner;
        [scaner startScanning];
#endif
}

- (void)assisDisConnectBtnCLick {
    [DoraemonMCClient disConnect];
    [self refreshUI];
}


- (void)masterCloseBtnClick {
    [DoraemonMCServer close];
    [[DoraemonManager shareInstance] configEntryBtnBlingWithText:nil backColor:nil];
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
    
    switch ([self workMode]) {
        case DoraemonMCViewControllerWorkModeClient:
        {
            self.masterDeviceBtn.hidden = YES;
            self.qrCodeImage.hidden = YES;
            self.bottomTip.hidden = YES;
            self.errorLabel.hidden = YES;
            self.assisDisConnectBtn.hidden = NO;
            self.clientCountLabel.hidden = YES;
            self.masterCloseBtn.hidden = YES;
            self.assisDeviceBtn.hidden = YES;
            if (self.asssisTip == nil) {
                self.asssisTip = [[UILabel alloc] initWithFrame:CGRectMake(30, CGRectGetMaxY(self.banner.frame) + 20, self.view.bounds.size.width - 60, 100)];
                self.asssisTip.font = [UIFont systemFontOfSize:15];
                self.asssisTip.numberOfLines = 0 ;
                self.asssisTip.textColor = [UIColor doraemon_blue];
                self.asssisTip.textAlignment = NSTextAlignmentCenter;
                self.asssisTip.text = @"已连接至主机";
                [self.view addSubview:self.asssisTip];
            }
            
            _assisDisConnectBtn.frame = CGRectMake(self.view.bounds.size.width/2.0 - 50, CGRectGetMaxY(self.asssisTip.frame) + 30, 100, 30);

            self.asssisTip.hidden = NO;

            [self.webSocketButton removeFromSuperview];
            [self.masterSwitch removeFromSuperview];
            [DKMultiControlStreamManager.sharedInstance unregisterWithListener:self];
            [DKMultiControlStreamManager.sharedInstance disableMultiControl];

            break;
        }
        case DoraemonMCViewControllerWorkModeServer:
        {
            if (self.qrCodeImage == nil) {
                self.qrCodeImage = [[UIImageView alloc] initWithFrame:CGRectMake(self.view.bounds.size.width/2.0 - 100, CGRectGetMaxY(self.banner.frame) + 20, 200, 200)];
                [self.view addSubview:self.qrCodeImage];


                self.masterCloseBtn = [[UIButton alloc] initWithFrame:CGRectMake(self.view.bounds.size.width/2.0 - 50, CGRectGetMaxY(self.qrCodeImage.frame) + 30, 100, 30)];
                self.masterCloseBtn.clipsToBounds = YES;
                self.masterCloseBtn.layer.cornerRadius = 5 ;
                self.masterCloseBtn.backgroundColor = [UIColor doraemon_blue];
                [self.masterCloseBtn setTitle:@"关闭服务" forState:UIControlStateNormal];
                [self.masterCloseBtn setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
                self.masterCloseBtn.titleLabel.font = [UIFont systemFontOfSize:18];
                [self.view addSubview:self.masterCloseBtn];
                [self.masterCloseBtn addTarget:self action:@selector(masterCloseBtnClick) forControlEvents:UIControlEventTouchUpInside];
                
                
                self.bottomTip = [[UILabel alloc] initWithFrame:CGRectMake(30, CGRectGetMaxY(self.masterCloseBtn.frame) + 20, self.view.bounds.size.width - 60, 100)];
                self.bottomTip.font = [UIFont systemFontOfSize:15];
                self.bottomTip.numberOfLines = 0 ;
                self.bottomTip.textColor = [UIColor doraemon_blue];
                self.bottomTip.textAlignment = NSTextAlignmentCenter;
                self.clientCountLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(self.bottomTip.frame) + 20, self.view.bounds.size.width, 30)];
                self.clientCountLabel.textColor = [UIColor doraemon_blue];
                self.clientCountLabel.textAlignment = NSTextAlignmentCenter;
                [self.view addSubview:self.clientCountLabel];
                [self.view addSubview:self.bottomTip];
            }
            NSString *url = [NSString stringWithFormat:@"http://%@:%zd/MyWs",[DoraemonAppInfoUtil getIPAddress:YES] , kDoraemonMCServerPort];
            self.qrCodeImage.image = [self.class QRCodeFromString:url size:300];
            self.bottomTip.text = [NSString stringWithFormat: @"请用其他手机的一机多控功能扫描以上二维码,连接该机器\n连接地址:%@",url];
            self.masterDeviceBtn.hidden = YES;
            self.assisDeviceBtn.hidden = YES;
            self.qrCodeImage.hidden = NO;
            self.bottomTip.hidden = NO;
            self.errorLabel.hidden = YES;
            self.clientCountLabel.hidden = NO;
            self.clientCountLabel.frame = CGRectMake(0, CGRectGetMaxY(self.bottomTip.frame) + 20, self.view.bounds.size.width, 30);
            self.clientCountLabel.text = [NSString stringWithFormat:@"当前连接从机数：%zd",[DoraemonMCServer connectCount]];
            self.assisDisConnectBtn.hidden = YES;
            self.masterCloseBtn.hidden = NO;
            self.asssisTip.hidden = YES;

            [self.webSocketButton removeFromSuperview];
            [self.masterSwitch removeFromSuperview];
            [DKMultiControlStreamManager.sharedInstance unregisterWithListener:self];
            [DKMultiControlStreamManager.sharedInstance disableMultiControl];

            break;
        }
        case DoraemonMCViewControllerWorkModeNone:
        {
            self.masterDeviceBtn.hidden = NO;
            self.assisDeviceBtn.hidden = NO;
            self.assisDisConnectBtn.hidden = YES;
            self.qrCodeImage.hidden = YES;
            self.bottomTip.hidden = YES;
            self.errorLabel.hidden = YES;
            self.clientCountLabel.hidden = YES;
            self.masterCloseBtn.hidden = YES;
            self.asssisTip.hidden = YES;

            UIButton *webSocketButton = [[UIButton alloc] initWithFrame:CGRectMake(self.view.bounds.size.width / 2.0 - 50, CGRectGetMaxY(self.assisDeviceBtn.frame) + 30, 100, 30)];
            self.webSocketButton = webSocketButton;
            webSocketButton.layer.cornerRadius = 5;
            webSocketButton.backgroundColor = UIColor.doraemon_blue;
//            [webSocketButton setTitle:@"流式传输" forState:UIControlStateNormal];
            [webSocketButton setTitleColor:UIColor.whiteColor forState:UIControlStateNormal];
            webSocketButton.titleLabel.font = [UIFont systemFontOfSize:18];
            [webSocketButton addTarget:self action:@selector(webSocketButtonHandler:) forControlEvents:UIControlEventTouchUpInside];
            [self.view addSubview:webSocketButton];
            UISwitch *masterSwitch = [[UISwitch alloc] initWithFrame:CGRectMake(self.view.bounds.size.width / 2 - 49 / 2, CGRectGetMaxY(webSocketButton.frame) + 30, 49, 31)];
            [self.view addSubview:masterSwitch];
            self.masterSwitch = masterSwitch;
            [masterSwitch addTarget:self action:@selector(masterSwitchHandler:) forControlEvents:UIControlEventValueChanged];
            [DKMultiControlStreamManager.sharedInstance registerMultiControlStreamManagerStateListener:self];

            break;
        }
        default:
            break;
    }
}

- (void)masterSwitchHandler:(id)sender {
    if (((UISwitch *) sender).isOn) {
        [DKMultiControlStreamManager.sharedInstance changeToMaster];
    } else {
        [DKMultiControlStreamManager.sharedInstance changeToSlave];
    }
}

- (void)webSocketButtonHandler:(id)sender {
    if (DKMultiControlStreamManager.sharedInstance.state == DKMultiControlStreamManagerStateClosed) {
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
    } else {
        [DKMultiControlStreamManager.sharedInstance disableMultiControl];
    }
}

- (void)changeToState:(DKMultiControlStreamManagerState)state {
    switch (state) {
        case DKMultiControlStreamManagerStateClosed:
            [self.masterSwitch setOn:NO animated:YES];
            [self.webSocketButton setTitle:@"流式传输" forState:UIControlStateNormal];
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

- (UILabel *)errorLabel {
    if (!_errorLabel) {
        _errorLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, CGRectGetMaxY(self.banner.frame) + 20, self.view.bounds.size.width, 30)];
        _errorLabel.font = [UIFont systemFontOfSize:15];
        _errorLabel.textAlignment = NSTextAlignmentCenter;
        _errorLabel.text = @"服务开启失败,点击重试";
        _errorLabel.textColor = [UIColor doraemon_blue];
        UITapGestureRecognizer *tapGes = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(errorLabelDidTap)];
        _errorLabel.userInteractionEnabled = YES;
        [_errorLabel addGestureRecognizer:tapGes];
        [self.view addSubview:_errorLabel];
        _errorLabel.hidden = YES;
    }
    return _errorLabel;
}

- (void)errorLabelDidTap {
    [self startServer];
}


- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    [self removeScanView];
}

- (void)removeScanView{
    if (self.scanView) {
        [self.scanView stopScanning];
        [self.scanView removeFromSuperview];
        self.scanView = nil;
    }
}

- (void)dealUrl:(NSString *)URL{
    [DoraemonMCClient connectWithUrl:URL];
}

- (BOOL)needBigTitleView{
    return YES;
}

#pragma mark -- DoraemonQRScanDelegate
- (void)scanView:(DoraemonQRScanView *)scanView pickUpMessage:(NSString *)message{
    if(message.length>0){
        [self dealUrl:message];
    }
}

- (void)scanView:(DoraemonQRScanView *)scanView aroundBrightness:(NSString *)brightnessValue{
    
}


/**
*  根据字符串生成二维码图片
*
*  @param code 二维码code
*  @param size 生成图片大小
*
*  @return image
*/
+ (UIImage *)QRCodeFromString:(NSString *)code size:(CGFloat)size{
    //创建CIFilter 指定filter的名称为CIQRCodeGenerator
    CIFilter *filter = [CIFilter filterWithName:@"CIQRCodeGenerator"];
    //指定二维码的inputMessage,即你要生成二维码的字符串
    [filter setValue:[code dataUsingEncoding:NSUTF8StringEncoding] forKey:@"inputMessage"];
    //输出CIImage
    CIImage *ciImage = [filter outputImage];
    //对CIImage进行处理
    return [self createfNonInterpolatedImageFromCIImage:ciImage withSize:size];
}

/**
 *  对CIQRCodeGenerator 生成的CIImage对象进行不插值放大或缩小处理
 *
 *  @param iamge 原CIImage对象
 *  @param size  处理后的图片大小
 *
 *  @return image
 */
+ (UIImage *) createfNonInterpolatedImageFromCIImage:(CIImage *)iamge withSize:(CGFloat)size{
    CGRect extent = iamge.extent;
    CGFloat scale = MIN(size/CGRectGetWidth(extent), size/CGRectGetHeight(extent));
    size_t with = scale * CGRectGetWidth(extent);
    size_t height = scale * CGRectGetHeight(extent);
    
    UIGraphicsBeginImageContext(CGSizeMake(with, height));
    CGContextRef bitmapContextRef = UIGraphicsGetCurrentContext();
    
    CIContext *context = [CIContext contextWithOptions:nil];
    //通过CIContext 将CIImage生成CGImageRef
    CGImageRef bitmapImage = [context createCGImage:iamge fromRect:extent];
    //在对二维码放大或缩小处理时,禁止插值
    CGContextSetInterpolationQuality(bitmapContextRef, kCGInterpolationNone);
    //对二维码进行缩放
    CGContextScaleCTM(bitmapContextRef, scale, scale);
    //将二维码绘制到图片上下文
    CGContextDrawImage(bitmapContextRef, extent, bitmapImage);
    //获得上下文中二维码
    UIImage *retVal =  UIGraphicsGetImageFromCurrentImageContext();
    CGImageRelease(bitmapImage);
    CGContextRelease(bitmapContextRef);
    return retVal;
}
@end
