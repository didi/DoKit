//
//  DoraemonJavaScriptManager.h
//  DoraemonKit
//
//  Created by carefree on 2022/5/11.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonJavaScriptManager : NSObject

+ (DoraemonJavaScriptManager *)shareInstance;

- (void)show;

- (void)evalJavaScript:(NSString *)script;

@end

NS_ASSUME_NONNULL_END
