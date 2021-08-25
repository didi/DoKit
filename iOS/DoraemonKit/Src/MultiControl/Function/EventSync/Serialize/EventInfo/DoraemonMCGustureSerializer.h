//
//  DoraemonMCGustureSerializer.h
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/13.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMCGustureSerializer : NSObject

+ (NSDictionary *)dictFromGusture:(UIGestureRecognizer *)gusture;

+ (void)syncInfoToGusture:(UIGestureRecognizer *)gusture withDict:(NSDictionary *)dict;

@end

NS_ASSUME_NONNULL_END
