//
//  DoraemonHierarchyHelper.h
//  DoraemonKit-DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@class DoraemonHierarchyWindow;

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonHierarchyHelper : NSObject

+ (instancetype _Nonnull)shared;

// DoraemonHierarchyWindow isn't a shared instance, so we need a object onwer it when show, and free it when hide.
@property (nonatomic, strong, nullable) DoraemonHierarchyWindow *window;

@property (nonatomic, assign) BOOL isHierarchyIgnorePrivateClass;

- (NSArray <UIWindow *>*)allWindows;

- (NSArray <UIWindow *>*)allWindowsIgnorePrefix:(NSString *_Nullable)prefix;

@end

NS_ASSUME_NONNULL_END
