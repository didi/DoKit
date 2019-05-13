//
//  Doraemoni18NUtil.m
//  DoraemonKit
//
//  Created by xgb on 2018/11/14.
//

#import "Doraemoni18NUtil.h"

@implementation Doraemoni18NUtil

+ (NSString *)localizedString:(NSString *)key {
    
    NSString *language = [[NSLocale preferredLanguages] firstObject];
    if (language.length == 0) {
        return key;
    }
    NSString *fileNamePrefix = @"zh-Hans";
    if([language hasPrefix:@"en"]) {
        fileNamePrefix = @"en";
    }
    NSBundle *tmp = [NSBundle bundleWithPath:[[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.bundle/", @"DoraemonKit"]]];
    NSString *path = [tmp pathForResource:fileNamePrefix ofType:@"lproj"];
    NSBundle *bundle = [NSBundle bundleWithPath:path];
    NSString *localizedString = [bundle localizedStringForKey:key value:nil table:@"Doraemon"];
    if (!localizedString) {
        localizedString = key;
    }
    return localizedString;
}

@end
