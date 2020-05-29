//
//  UIColor+DoraemonHierarchy.h
//  DoraemonKit-DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIColor (DoraemonHierarchy)

- (NSString *)doraemon_HexString;

- (NSString *)doraemon_description;

- (NSString *_Nullable)doraemon_systemColorName;

@end

NS_ASSUME_NONNULL_END
