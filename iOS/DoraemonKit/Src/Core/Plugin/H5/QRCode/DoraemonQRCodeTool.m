//
//  DoraemonQRCodeTool.m
//  DoraemonKit-DoraemonKit
//
//  Created by love on 2019/5/24.
//

#import "DoraemonQRCodeTool.h"
#import "DoraemonDefine.h"

@interface DoraemonQRCodeTool () <AVCaptureMetadataOutputObjectsDelegate,CAAnimationDelegate>

/** 二维码信息对象 */
@property (nonatomic,strong) AVCaptureSession *codeCaptureSession;

/** 显示采集图像的对象 */
@property (nonatomic,strong) AVCaptureVideoPreviewLayer *codeCaptureVideoPreviewLayer;

@property (nonatomic, strong) UIImageView *scanLineView;/**< 扫描条 */

@property (nonatomic, assign) CGFloat QRCodeWidth;

@property (nonatomic,strong) void(^ScanResultsBlock)(NSString *) ;

@end
@implementation DoraemonQRCodeTool
#pragma mark  单例宏

+ (instancetype)shared {
    static id object = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        object = [[self alloc] init];
    });
    return object;
}


#pragma mark  扫描线
- (UIImageView *)scanLineView
{
    if (!_scanLineView) {
        _scanLineView = [[UIImageView alloc] init];
        _scanLineView.image = [UIImage doraemon_imageNamed:@"doraemon_scan_line"];
        _scanLineView.backgroundColor = [UIColor clearColor];
    }
    return _scanLineView;
}

#pragma mark  二维码采集对象
- (AVCaptureSession *)codeCaptureSession
{
    if (!_codeCaptureSession) {
        // 二维码采集对象
        _codeCaptureSession = [[AVCaptureSession alloc] init];
    }
    return _codeCaptureSession;
}

#pragma mark  显示采集图像的对象
- (AVCaptureVideoPreviewLayer *)codeCaptureVideoPreviewLayer
{
    if (!_codeCaptureVideoPreviewLayer) {
        _codeCaptureVideoPreviewLayer  = [AVCaptureVideoPreviewLayer layerWithSession:self.codeCaptureSession];
    }
    return _codeCaptureVideoPreviewLayer;
}

//  =======================================二维码解析======================================================


#pragma mark  判断是否真机和授权
- (NSString *)authJudge {
    if ([[AVCaptureDevice devicesWithMediaType:AVMediaTypeVideo] count] <= 0) {
        return  @"没有相机";
    }
    
    if([AVCaptureDevice authorizationStatusForMediaType:AVMediaTypeVideo] == AVAuthorizationStatusDenied) {
        
        return @"没有权限";
    }
    return nil;
}

#pragma mark  二维码初始化
- (NSString *)QRCodeDeviceInitWithVC:(UIViewController *)VC
                     WithQRCodeWidth:(CGFloat)QRCodeWidth
                         ScanResults:(void  (^)(NSString *result))ScanResults;
{
    /*
     判断真机和授权
     */
    if ([self authJudge].length) {
        //        return [self authJudge];
    }
    if (!VC) {
        return @"Don't know is which UIViewController,参数VC为空";
    }
    
    if (QRCodeWidth < 40.f) {
        QRCodeWidth = [UIScreen mainScreen].bounds.size.width * 2 / 3;
    }
    
    if (QRCodeWidth > [UIScreen mainScreen].bounds.size.width) {
        QRCodeWidth = [UIScreen mainScreen].bounds.size.width;
    }
    
    self.QRCodeWidth = QRCodeWidth;
    
    self.ScanResultsBlock = ScanResults;
    //------------------------------------------------------ 创建输入流--------------------------------------------------------
    
    /**
     信息采集类型
     AVMediaTypeVideo 视频
     AVMediaTypeAudio 声音
     AVMediaTypeText   文本
     AVMediaTypeClosedCaption 隐藏字幕
     AVMediaTypeSubtitle  副标题
     AVMediaTypeTimecode  时间编码
     AVMediaTypeMetadata  元数据
     AVMediaTypeMuxed
     
     */
    
    // 信息采集设备
    AVCaptureDevice *codeCaptureDevice = [AVCaptureDevice defaultDeviceWithMediaType:AVMediaTypeVideo];
    
    
    NSError *error;
    
    AVCaptureDeviceInput *input = [AVCaptureDeviceInput deviceInputWithDevice:codeCaptureDevice error:&error];
    if (error) {
        NSLog(@"创建输入流error：%@",error);
        return  [NSString stringWithFormat:@"%@",error];
    }
    
    // 设置输入流
    if ([self.codeCaptureSession canAddInput:input]) {
        [self.codeCaptureSession addInput:input];
    }
    
    //------------------------------------------------------ 创建输入流--------------------------------------------------
    
    //------------------------------------------------------ 创建输出流--------------------------------------------------
    
    AVCaptureMetadataOutput *output = [[AVCaptureMetadataOutput alloc] init];
    
    // 设置输出流
    if ([self.codeCaptureSession canAddOutput:output]) {
        [self.codeCaptureSession addOutput:output];
        
        //设置扫码支持的编码格式(如下设置条形码和二维码兼容),设置输出类型
        NSMutableArray *a = [[NSMutableArray alloc] init];
        if ([output.availableMetadataObjectTypes containsObject:AVMetadataObjectTypeQRCode]) {
            [a addObject:AVMetadataObjectTypeQRCode];
        }
        if ([output.availableMetadataObjectTypes containsObject:AVMetadataObjectTypeEAN13Code]) {
            [a addObject:AVMetadataObjectTypeEAN13Code];
        }
        if ([output.availableMetadataObjectTypes containsObject:AVMetadataObjectTypeEAN8Code]) {
            [a addObject:AVMetadataObjectTypeEAN8Code];
        }
        if ([output.availableMetadataObjectTypes containsObject:AVMetadataObjectTypeCode128Code]) {
            [a addObject:AVMetadataObjectTypeCode128Code];
        }
        output.metadataObjectTypes=a;
        
    }
    
    //设置输出流代理，在代理中获取二维码的结果
    [output setMetadataObjectsDelegate:self queue:dispatch_get_main_queue()];
    
    /// 限制扫描区域
    // 1.获取屏幕的frame
    CGRect viewRect = VC.view.frame;
    // 2.获取扫描容器的frame
    CGRect containerRect = CGRectMake(0, (CGRectGetHeight(VC.view.frame) - CGRectGetWidth(VC.view.frame))/2.0, CGRectGetWidth(VC.view.frame), CGRectGetWidth(VC.view.frame));;
    
    CGFloat x = containerRect.origin.y / viewRect.size.height;
    CGFloat y = containerRect.origin.x / viewRect.size.width;
    CGFloat width = containerRect.size.height / viewRect.size.height;
    CGFloat height = containerRect.size.width / viewRect.size.width;
    
    // CGRect outRect = CGRectMake(x, y, width, height);
    // [_output rectForMetadataOutputRectOfInterest:outRect];
    output.rectOfInterest = CGRectMake(x, y, width, height);
    
    //    [output setRectOfInterest:CGRectMake(((SCREEN_HEIGHT - SCREEN_WIDTH)/2.0)/SCREEN_WIDTH,0,SCREEN_WIDTH/SCREEN_HEIGHT,1)];
    //    [output availableMetadataObjectTypes]; 获取输出支持类型
    
    //设置采样率，越高识别越精准，速度越慢,高质量采集率
    [self.codeCaptureSession setSessionPreset:AVCaptureSessionPresetHigh];
    
    //------------------------------------------------------ 创建输出流--------------------------------------------------
    
    // 采集图像显示
    // 可以保留纵横比，但填满可用的屏幕区域
    self.codeCaptureVideoPreviewLayer.videoGravity=AVLayerVideoGravityResizeAspectFill;
    self.codeCaptureVideoPreviewLayer.bounds = viewRect;
    self.codeCaptureVideoPreviewLayer.position = CGPointMake(VC.view.center.x, VC.view.center.y - 64);
    [VC.view.layer insertSublayer:self.codeCaptureVideoPreviewLayer atIndex:0];
    
    CGRect rect = CGRectMake(([UIScreen mainScreen].bounds.size.width - QRCodeWidth) / 2, ([UIScreen mainScreen].bounds.size.height - QRCodeWidth - 72)/2, QRCodeWidth, QRCodeWidth);
    
    
    _maskView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, [UIScreen mainScreen].bounds.size.width, [UIScreen mainScreen].bounds.size.height)];
    _maskView.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.6];
    [VC.view addSubview:_maskView];
    
    UIBezierPath *rectPath = [UIBezierPath bezierPathWithRect:[UIScreen mainScreen].bounds];
    [rectPath appendPath:[[UIBezierPath bezierPathWithRoundedRect:CGRectMake(([UIScreen mainScreen].bounds.size.width - QRCodeWidth) / 2, ([UIScreen mainScreen].bounds.size.height - QRCodeWidth - 72)/2, QRCodeWidth, QRCodeWidth) cornerRadius:1] bezierPathByReversingPath]];
    
    CAShapeLayer *shapeLayer = [CAShapeLayer layer];
    shapeLayer.path = rectPath.CGPath;
    
    _maskView.layer.mask = shapeLayer;
    
    UIImageView *scanAreaImageView = [[UIImageView alloc] initWithFrame:rect];
    scanAreaImageView.image = [UIImage imageNamed:@"扫描框"];
    scanAreaImageView.layer.borderColor = [UIColor clearColor].CGColor;
    scanAreaImageView.layer.cornerRadius = 2;
    scanAreaImageView.layer.borderWidth = 1;
    
    if (!scanAreaImageView.image) {
        scanAreaImageView.layer.borderColor = [UIColor brownColor].CGColor;
    }
    
    [VC.view addSubview:scanAreaImageView];
    
    self.scanLineView.frame = CGRectMake(1, 20, QRCodeWidth-2, 2);
    if (!self.scanLineView.image) {
        self.scanLineView.backgroundColor = [UIColor orangeColor];
    }
    
    [scanAreaImageView addSubview:self.scanLineView];
    
    return nil;
}

+ (CABasicAnimation *)moveYTime:(float)time fromY:(NSNumber *)fromY toY:(NSNumber *)toY rep:(int)rep
{
    CABasicAnimation *animationMove = [CABasicAnimation animationWithKeyPath:@"transform.translation.y"];
    [animationMove setFromValue:fromY];
    [animationMove setToValue:toY];
    animationMove.duration = time;
    //    animationMove.delegate = self;
    animationMove.repeatCount  = rep;
    animationMove.fillMode = kCAFillModeForwards;
    animationMove.removedOnCompletion = NO;
    animationMove.autoreverses = YES;
    animationMove.timingFunction = [CAMediaTimingFunction functionWithName:kCAMediaTimingFunctionEaseInEaseOut];
    return animationMove;
}


#pragma 去除扫码动画
- (void)removeAnimation{
    
    [self.scanLineView.layer removeAnimationForKey:@"LineAnimation"];
    self.scanLineView.hidden = YES;
}

#pragma 添加扫码动画
- (void)addAnimation{
    
    self.scanLineView.hidden = NO;
    CABasicAnimation *animation = [DoraemonQRCodeTool moveYTime:self.QRCodeWidth/250 * 2.5 fromY:[NSNumber numberWithFloat:-10] toY:[NSNumber numberWithFloat:self.QRCodeWidth-30] rep:OPEN_MAX];
    [self.scanLineView.layer addAnimation:animation forKey:@"LineAnimation"];
}

#pragma 监听扫码状态-修改扫描动画
- (void)observeValueForKeyPath:(NSString *)keyPath
                      ofObject:(id)object
                        change:(NSDictionary *)change
                       context:(void *)context{
    if ([object isKindOfClass:[AVCaptureSession class]]) {
        BOOL isRunning = ((AVCaptureSession *)object).isRunning;
        if (isRunning) {
            [self addAnimation];
        }else{
            [self removeAnimation];
        }
    }
}

#pragma 启动二维码扫描

- (void)startScanning
{
    //[[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleLightContent animated:YES];
    
    //开始动画
    [self addAnimation];
    
    [self.codeCaptureSession addObserver:self forKeyPath:@"running" options:NSKeyValueObservingOptionNew context:nil];
    
    if (self.isAddDelay) {
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(3.f * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            
            // 开始采集
            [self.codeCaptureSession startRunning];
        });
    } else {
        // 开始采集
        [self.codeCaptureSession startRunning];
        
    }
}

#pragma 停止二维码扫描
- (void)stopScanning
{
    [self.codeCaptureSession removeObserver:self forKeyPath:@"running" context:nil];
    
    //[[UIApplication sharedApplication] setStatusBarStyle:UIStatusBarStyleDefault animated:YES];
    
    [self removeAnimation];
    
    [self.codeCaptureSession stopRunning];
}

#pragma mark AVCaptureMetadataOutputObjectsDelegate
- (void)captureOutput:(AVCaptureOutput *)captureOutput didOutputMetadataObjects:(NSArray *)metadataObjects fromConnection:(AVCaptureConnection *)connection {
    
    if (metadataObjects.count > 0) {
        //NSLog(@"%@",[[metadataObjects objectAtIndex:0] stringValue]);
        
        self.ScanResultsBlock([[metadataObjects objectAtIndex:0] stringValue]);
    }
}




#pragma mark  - 解析图片二维码

+ (NSString *)parsingQRCodeStringFromUIImage:(UIImage *)image
{
    return [self stringFromCiImage:[[CIImage alloc]initWithCGImage:image.CGImage]];
}

+ (NSString *)parsingQRCodeStringFromCiImage:(CIImage *)image
{
    return [self stringFromCiImage:image];
}


+ (NSString *)stringFromCiImage:(CIImage *)ciimage {
    
    NSString *content = @"" ;
    
    if (!ciimage) {
        return content;
    }
    
    CIDetector *detector = [CIDetector detectorOfType:CIDetectorTypeQRCode
                                              context:[CIContext contextWithOptions:nil]
                                              options:@{CIDetectorAccuracy:CIDetectorAccuracyHigh}];
    
    NSArray *features = [detector featuresInImage:ciimage];
    
    if (features.count) {
        
        for (CIFeature *feature in features) {
            
            if ([feature isKindOfClass:[CIQRCodeFeature class]]) {
                content = ((CIQRCodeFeature *)feature).messageString;
                break;
            }
        }
    } else {
        
        //[self addAlertViewControllerWithMessage:@"未正常解析二维码图片, 请确保iphone5/5c以上的设备"];
        
        NSLog(@"未正常解析二维码图片, 请确保iphone5/5c以上的设备");
    }
    return content;
}



// ===============================二维码的生成===============================
#pragma mark - 二维码的生成

/**
 *   获取带小图片的二维码
 *
 *   二维码颜色为黑色，中心图片的宽度为二维码的1/5
 *  QRCodeContent  生成二维码的内容
 *  QRCodeWidth  生成二维码的宽高
 *  centerImage  生成二维码的中心图片
 */
+ (UIImage *)generateQRCodeWithQRCodeContent:(NSString *)QRCodeContent
                                 QRCodeWidth:(CGFloat)QRCodeWidth
                                 centerImage:(UIImage *)centerImage
{
    CIImage *ciImage = [self createQRForString:QRCodeContent];
    
    UIImage *uiImage =  [self createNoninterpolatedUIImageFormCIImage:ciImage withSize:QRCodeWidth];
    
    if (centerImage) {
        return [self createImageBigImage:uiImage smallImage:centerImage sizeWH:uiImage.size.width * 0.2f];
    }
    
    return uiImage;
}

/**
 *  获取自定义颜色的二维码
 *
 *  QRCodeContent  生成二维码的内容
 *  QRCodeWidth  生成二维码的宽高
 *
 */
+ (UIImage *)generateQRCodeWithQRCodeContent:(NSString *)QRCodeContent
                                 QRCodeWidth:(CGFloat)QRCodeWidth
                                     withRed:(CGFloat)red
                                    andGreen:(CGFloat)green
                                     andBlue:(CGFloat)blue
{
    CIImage *ciImage = [self createQRForString:QRCodeContent];
    
    UIImage *uiImage =  [self createNoninterpolatedUIImageFormCIImage:ciImage withSize:QRCodeWidth];
    
    return  [self imageBlackToTransparent:uiImage withRed:red andGreen:green andBlue:blue];
}


#pragma mark  二维码颜色填充

void ProviderReleaseData (void *info, const void *data, size_t size){
    free((void*)data);
}

/**
 
 生成的二维码是黑白的，所以还要对二维码进行颜色填充，并转换为透明背景，使用遍历图片像素来更改图片颜色，因为使用的是CGContext，速度非常快
 
 */

+ (UIImage*)imageBlackToTransparent:(UIImage*)image withRed:(CGFloat)red andGreen:(CGFloat)green andBlue:(CGFloat)blue{
    
    const int imageWidth = image.size.width;
    const int imageHeight = image.size.height;
    
    size_t      bytesPerRow = imageWidth * 4;
    
    uint32_t* rgbImageBuf = (uint32_t*)malloc(bytesPerRow * imageHeight);
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    CGContextRef context = CGBitmapContextCreate(rgbImageBuf, imageWidth, imageHeight, 8, bytesPerRow, colorSpace,
                                                 kCGBitmapByteOrder32Little | kCGImageAlphaNoneSkipLast);
    CGContextDrawImage(context, CGRectMake(0, 0, imageWidth, imageHeight), image.CGImage);
    // 遍历像素
    int pixelNum = imageWidth * imageHeight;
    uint32_t* pCurPtr = rgbImageBuf;
    for (int i = 0; i <pixelNum; i++, pCurPtr++){
        if ((*pCurPtr & 0xFFFFFF00) < 0x99999900)    // 将白色变成透明
        {
            // 改成下面的代码，会将图片转成想要的颜色
            uint8_t* ptr = (uint8_t*)pCurPtr;
            ptr[3] = red; //0~255
            ptr[2] = green;
            ptr[1] = blue;
        }
        else
        {
            uint8_t* ptr = (uint8_t*)pCurPtr;
            ptr[0] = 0;
        }
    }
    // 输出图片
    CGDataProviderRef dataProvider = CGDataProviderCreateWithData(NULL, rgbImageBuf, bytesPerRow * imageHeight, ProviderReleaseData);
    CGImageRef imageRef = CGImageCreate(imageWidth, imageHeight, 8, 32, bytesPerRow, colorSpace,
                                        kCGImageAlphaLast | kCGBitmapByteOrder32Little, dataProvider,
                                        NULL, true, kCGRenderingIntentDefault);
    CGDataProviderRelease(dataProvider);
    UIImage* resultUIImage = [UIImage imageWithCGImage:imageRef];
    // 清理空间
    CGImageRelease(imageRef);
    CGContextRelease(context);
    CGColorSpaceRelease(colorSpace);
    return resultUIImage;
}

#pragma mark  生成二维码图片

/**
 *  根据两个图片,合成一个大图片
 *
 *  @param bigImage   大图的背景图片
 *  @param smallImage 小图标(居中)
 *  @param sizeWH     小图标的尺寸
 *
 *  @return 合成后的图片
 */
+ (UIImage *)createImageBigImage:(UIImage *)bigImage smallImage:(UIImage *)smallImage sizeWH:(CGFloat)sizeWH
{
    CGSize size = bigImage.size;
    
    // 1.开启一个图形山下文
    UIGraphicsBeginImageContext(size);
    
    // 2.绘制大图片
    [bigImage drawInRect:CGRectMake(0, 0, size.width, size.height)];
    
    // 3.绘制小图片
    CGFloat x = (size.width - sizeWH) * 0.5;
    CGFloat y = (size.height - sizeWH) *0.5;
    [smallImage drawInRect:CGRectMake(x, y, sizeWH, sizeWH)];
    
    // 4.取出合成图片
    UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
    
    // 5.关闭图形上下文
    UIGraphicsEndImageContext();
    
    return newImage;
}

#pragma mark  根据指定的CIImage对象来转换成指定大小的UIImage对象
/**
 *  根据指定的CIImage对象来转换成指定大小的UIImage对象
 *
 *  @param image 要转换的CIImage对象
 *  @param size  指定的大小
 *
 *  @return 生成好的UIImage对象
 
 生成的二维码是一个CIImage，我们直接转换成UIImage的话大小不好控制，所以使用下面方法返回需要大小的UIImage
 */

+ (UIImage *)createNoninterpolatedUIImageFormCIImage:(CIImage *)image
                                            withSize:(CGFloat)size
{
    CGRect extent = CGRectIntegral(image.extent);
    
    CGFloat scale = MIN(size/CGRectGetWidth(extent), size/CGRectGetHeight(extent));
    
    // 创建bitmap
    size_t with = CGRectGetWidth(extent)*scale;
    size_t height = CGRectGetHeight(extent)*scale;
    
    CGColorSpaceRef cs = CGColorSpaceCreateDeviceGray();
    
    CGContextRef bitmap = CGBitmapContextCreate(nil, with, height, 8, 0, cs, (CGBitmapInfo)kCGImageAlphaNone);
    
    CIContext *context = [CIContext contextWithOptions:nil];
    
    CGImageRef bitmapImage = [context createCGImage:image fromRect:extent];
    
    CGContextSetInterpolationQuality(bitmap, kCGInterpolationNone);
    
    CGContextScaleCTM(bitmap, scale, scale);
    
    CGContextDrawImage(bitmap, extent, bitmapImage);
    
    
    // 保存bitmap到图片
    CGImageRef scaledImage = CGBitmapContextCreateImage(bitmap);
    CGContextRelease(bitmap);
    CGImageRelease(bitmapImage);
    
    return [UIImage imageWithCGImage:scaledImage];
    
}


#pragma mark  根据指定的字符串来生成二维码的CIImage对象
/**
 *  根据指定的字符串来生成二维码的CIImage对象
 *
 *  @param QRString 要用来生成二维码的字符串
 *
 *  @return 生成好的二维码图片，是一个CIImage对象
 */
+ (CIImage *)createQRForString:(NSString *)QRString
{
    
    if (!QRString.length) {
        QRString = @"没有给该二维码添加内容";
    }
    
    // 获取支持的二维码和条码生成器
    /* CIAztecCodeGenerator,
     CICheckerboardGenerator,
     CICode128BarcodeGenerator,
     CIConstantColorGenerator,
     CILenticularHaloGenerator,
     CIPDF417BarcodeGenerator,
     CIQRCodeGenerator,
     CIRandomGenerator,
     CIStarShineGenerator,
     CIStripesGenerator,
     CISunbeamsGenerator*/
    NSLog(@"%@",[CIFilter filterNamesInCategory:kCICategoryGenerator]);
    
    // 实例化二维码滤镜
    CIFilter *QRFilter = [CIFilter filterWithName:@"CIQRCodeGenerator"];
    
    // 恢复滤镜的默认属性
    [QRFilter setDefaults]; // 可以省略
    
    // 设置二维码的内容
    [QRFilter setValue:[QRString dataUsingEncoding:NSUTF8StringEncoding] forKey:@"inputMessage"];
    
    // 设置二维码的纠错级别
    /*
     * ：L级可纠正约7%错误、
     M级别可纠正约15%错误、
     Q级别可纠正约25%错误、
     H级别可纠正约30%错误。
     **/
    [QRFilter setValue:@"H" forKey:@"inputCorrectionLevel"];
    
    return QRFilter.outputImage;
}

@end
