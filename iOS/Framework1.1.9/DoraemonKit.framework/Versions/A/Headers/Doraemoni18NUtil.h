//
//  Doraemoni18NUtil.h
//  DoraemonKit
//
//  Created by xgb on 2018/11/14.
//

#import <Foundation/Foundation.h>

#define DoraemonLocalizedString(key)   [Doraemoni18NUtil localizedString:key]

@interface Doraemoni18NUtil : NSObject

+ (NSString *)localizedString:(NSString *)key;

@end

