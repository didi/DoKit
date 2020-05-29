//
//  DoraemonKitDemoi18Util.m
//  DoraemonKitDemo
//
//  Created by didi on 2020/4/4.
//  Copyright © 2020 yixiang. All rights reserved.
//

#import "DoraemonKitDemoi18Util.h"

@implementation DoraemonKitDemoi18Util

+ (NSString *)localizedString:(NSString *)key {
    
    NSString *language = [[NSLocale preferredLanguages] firstObject];
    if (language.length == 0) {
        return key;
    }
    NSString *fileNamePrefix = @"zh-Hans";
    if([language hasPrefix:@"en"]) {
        fileNamePrefix = @"en";
    }
    
    NSString *path = [[NSBundle mainBundle] pathForResource:fileNamePrefix ofType:@"lproj"];
    NSBundle *bundle = [NSBundle bundleWithPath:path];
    NSString *localizedString = [bundle localizedStringForKey:key value:nil table:@"DoraemonKitDemo"];
    if (!localizedString) {
        localizedString = key;
    }
    return localizedString;
}

@end
