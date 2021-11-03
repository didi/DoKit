//
//  DoraemonQRCodeViewController.h
//  DoraemonKit
//
//  Created by love on 2019/5/22.
//

#import <Foundation/Foundation.h>
#import "DoraemonBaseViewController.h"

@interface DoraemonQRCodeViewController : DoraemonBaseViewController
@property (nonatomic, copy) void(^QRCodeBlock)(NSString *QRCodeResult);
@end

