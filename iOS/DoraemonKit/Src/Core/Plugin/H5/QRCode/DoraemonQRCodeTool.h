//
//  DoraemonQRCodeTool.h
//  DoraemonKit-DoraemonKit
//
//  Created by love on 2019/5/24.
//

#import <Foundation/Foundation.h>
#import <AVFoundation/AVFoundation.h>
#import <UIKit/UIKit.h>
NS_ASSUME_NONNULL_BEGIN

@interface DoraemonQRCodeTool : NSObject
/** 半透明的外圈view，以供扩展 */
@property (nonatomic,strong,readonly) UIView * maskView;

/**
 * 单例宏
 */
//FSingletonH(FQuickCreateQRCode)
+ (instancetype)shared;

/**是否存在延迟*/
@property (nonatomic,assign) BOOL isAddDelay;
//  =======================================二维码扫描======================================================

/**
 *  启动二维码扫描
 */
- (void)startScanning;

/**
 *  停止二维码扫描
 */
- (void)stopScanning;



/**
 *  二维码的初始化
 *
 *  QRCodeWidth  默认为屏幕宽度的2/3
 *
 *  如果返回值存在，则二维码实例化不成功，返回值为error
 *
 *  二维码扫描需要真机，模拟器不可以
 *
 *  初始化完成不会自动扫描，需要手动开启
 *
 *  当扫描到结果时不会自动停止，会继续扫描，需要手动来停止扫描
 */
- (NSString *)QRCodeDeviceInitWithVC:(UIViewController *)VC
                     WithQRCodeWidth:(CGFloat)QRCodeWidth
                         ScanResults:(void  (^)(NSString *result))ScanResults;

/*
 *  二维码扫描示例
 
 FQRCodeTool *tool = [FQRCodeTool sharedFQRCodeTool];
 
 NSString *error = [tool QRCodeDeviceInitWithVC:self WithQRCodeWidth:0 ScanResults:^(NSString *result) {
 
 NSLog(@"%@",result); // 在这里就可以添加弹出框。。。
 UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"扫描结果" message:result delegate:nil cancelButtonTitle:@"朕知道了" otherButtonTitles:nil, nil];
 [alert show];
 
 [tool stopScanning];
 }];
 
 if (error) {
 // 这里得到返回的错误，可以进行提示
 UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"错误提示" message:error delegate:nil cancelButtonTitle:@"朕知道了" otherButtonTitles:nil, nil];
 [alert show];
 }
 else
 {
 [tool startScanning];
 }
 
 // 扩展
 UIButton *button = [UIButton buttonWithType:UIButtonTypeSystem];
 button.frame = CGRectMake(100, 100, 50, 30);
 [button setTitle:@"跳转" forState:UIControlStateNormal];
 button.backgroundColor = [UIColor redColor];
 [button setTitleColor:[UIColor blackColor] forState:UIControlStateNormal];
 [tool.maskView addSubview:button];
 */






//  =======================================二维码解析======================================================
/**
 * 通过 UIImage 解析二维码
 */
+ (NSString *)parsingQRCodeStringFromUIImage:(UIImage *)image;

/**
 * 通过 CIImage 解析二维码
 */
+ (NSString *)parsingQRCodeStringFromCiImage:(CIImage *)image;









//  =======================================二维码生成======================================================
/**
 *   生成带小图片的二维码
 *
 *   二维码颜色为黑色，中心图片的宽度为二维码的1/5
 *  QRCodeContent  生成二维码的内容
 *  QRCodeWidth  生成二维码的宽高
 *  centerImage  生成二维码的中心图片
 */
+ (UIImage *)generateQRCodeWithQRCodeContent:(NSString *)QRCodeContent
                                 QRCodeWidth:(CGFloat)QRCodeWidth
                                 centerImage:(UIImage *)centerImage;

/**
 *  生成自定义颜色的二维码
 *
 *  QRCodeContent  生成二维码的内容
 *  QRCodeWidth  生成二维码的宽高
 *
 */
+ (UIImage *)generateQRCodeWithQRCodeContent:(NSString *)QRCodeContent
                                 QRCodeWidth:(CGFloat)QRCodeWidth
                                     withRed:(CGFloat)red
                                    andGreen:(CGFloat)green
                                     andBlue:(CGFloat)blue;



@end

NS_ASSUME_NONNULL_END
