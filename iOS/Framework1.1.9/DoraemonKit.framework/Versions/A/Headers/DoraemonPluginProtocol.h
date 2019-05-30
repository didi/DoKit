//
//  DoraemonPluginProtocol.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//

#import <Foundation/Foundation.h>

@protocol DoraemonPluginProtocol <NSObject>

@optional
- (void)pluginDidLoad;
- (void)pluginDidLoad:(NSDictionary *)itemData;

@end
