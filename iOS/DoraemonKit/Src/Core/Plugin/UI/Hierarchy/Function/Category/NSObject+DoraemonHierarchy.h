//
//  NSObject+DoraemonHierarchy.h
//  DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

FOUNDATION_EXPORT NSNotificationName _Nonnull const DoraemonHierarchyChangeNotificationName;

@class DoraemonHierarchyCategoryModel;

NS_ASSUME_NONNULL_BEGIN

@interface NSObject (DoraemonHierarchy)

- (NSArray <DoraemonHierarchyCategoryModel *>*)doraemon_hierarchyCategoryModels;

- (void)doraemon_showIntAlertAndAutomicSetWithKeyPath:(NSString *)keyPath;

- (void)doraemon_showFrameAlertAndAutomicSetWithKeyPath:(NSString *)keyPath;

- (void)doraemon_showColorAlertAndAutomicSetWithKeyPath:(NSString *)keyPath;

- (void)doraemon_showFontAlertAndAutomicSetWithKeyPath:(NSString *)keyPath;

- (UIColor *)doraemon_hashColor;

@end

@interface UIView (DoraemonHierarchy)

- (NSArray <DoraemonHierarchyCategoryModel *>*)doraemon_sizeHierarchyCategoryModels;

@end

NS_ASSUME_NONNULL_END
