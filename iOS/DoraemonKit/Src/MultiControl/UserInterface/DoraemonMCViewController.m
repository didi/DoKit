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

@interface DoraemonMCViewController ()<DoraemonQRScanDelegate>

@property (nonatomic, assign) DoraemonMCViewControllerType type;

@property (nonatomic, strong) DoraemonQRScanView *scanView;

@property (nonatomic, strong) UIImageView *qrCodeImage;

@property (nonatomic, strong) UILabel *errorLabel;

@property (nonatomic, strong) UILabel *bottomTip;

@property (nonatomic, strong) UIActivityIndicatorView *loadingView;
@end

@implementation DoraemonMCViewController

+ (instancetype)instanceWithType:(DoraemonMCViewControllerType)type {
    DoraemonMCViewController *instance = [[DoraemonMCViewController alloc] init];
    instance.type = type;
    return instance;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [self setupUI];
    if ([self isServer]) {
        [self startServer];
    }
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
                [DoraemonToastUtil showToastBlack:@"服务开启成功" inView:self.view];
                self.errorLabel.hidden = YES;
                self.qrCodeImage.hidden = NO;
                self.bottomTip.hidden = NO;
            }else {
                if (error) {
                    [DoraemonToastUtil showToastBlack:error.localizedDescription inView:self.view];
                }
                self.errorLabel.hidden = NO;
                self.qrCodeImage.hidden = YES;
                self.bottomTip.hidden = YES;
            }
        });

    });

}

- (BOOL)isServer {
    return DoraemonMCViewControllerTypeServer == self.type;
}

- (void)setupUI {
    self.title = [self isServer] ? @"主机服务二维码" : @"连接主机" ;
    if ([self isServer]) {
        self.qrCodeImage = [[UIImageView alloc] initWithFrame:CGRectMake(self.view.bounds.size.width/2.0 - 150, 100, 300, 300)];
        [self.view addSubview:self.qrCodeImage];
        NSString *url = [NSString stringWithFormat:@"http://%@:%zd/MyWs",[DoraemonAppInfoUtil getIPAddress:YES] , kDoraemonMCServerPort];
        self.qrCodeImage.image = [self.class QRCodeFromString:url size:300];
        
        self.bottomTip = [[UILabel alloc] initWithFrame:CGRectMake(30, CGRectGetMaxY(self.qrCodeImage.frame) + 50, self.view.bounds.size.width - 60, 100)];
        self.bottomTip.font = [UIFont systemFontOfSize:15];
        self.bottomTip.numberOfLines = 0 ;
        self.bottomTip.textColor = [UIColor doraemon_blue];
        self.bottomTip.textAlignment = NSTextAlignmentCenter;
        
        [self.view addSubview:self.bottomTip];
        self.bottomTip.text = [NSString stringWithFormat: @"请用其他手机的一机多控功能扫描以上二维码,连接该机器\n连接地址:%@",url];
    }
}


- (UILabel *)errorLabel {
    if (!_errorLabel) {
        _errorLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 100, self.view.bounds.size.width, 30)];
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

- (void)viewWillAppear:(BOOL)animated{
    [super viewWillAppear:animated];
    
    if (![self isServer]) {
        
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

}

- (void)viewWillDisappear:(BOOL)animated{
    [super viewWillDisappear:animated];
    if (![self isServer]) {
        [self removeScanView];
    }
}

- (void)removeScanView{
    if (self.scanView) {
        [self.scanView stopScanning];
        [self.scanView removeFromSuperview];
        self.scanView = nil;
    }
}

- (void)dealUrl:(NSString *)URL{
    [self leftNavBackClick:nil];
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
