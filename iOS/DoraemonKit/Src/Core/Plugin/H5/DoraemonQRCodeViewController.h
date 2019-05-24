//
//  DoraemonQRCodeViewController.h
//  AFNetworking
//
//  Created by love on 2019/5/22.
//

#import <Foundation/Foundation.h>
#import "DoraemonBaseViewController.h"
NS_ASSUME_NONNULL_BEGIN

@interface DoraemonQRCodeViewController : DoraemonBaseViewController
@property (nonatomic, copy) void(^QRCodeBlock)(NSString *QRCodeResult);
@end

NS_ASSUME_NONNULL_END
