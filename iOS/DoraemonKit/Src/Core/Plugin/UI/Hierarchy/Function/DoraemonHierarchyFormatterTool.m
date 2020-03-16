//
//  DoraemonHierarchyFormatterTool.m
//  DoraemonKit-DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//
#import <UIKit/UIKit.h>
#import "DoraemonHierarchyFormatterTool.h"

static DoraemonHierarchyFormatterTool *_instance = nil;

@interface DoraemonHierarchyFormatterTool ()

@property (nonatomic, strong) NSDateFormatter *formatter;

@property (nonatomic, strong) NSNumberFormatter *numberFormatter;

@end

@implementation DoraemonHierarchyFormatterTool

#pragma mark - Public
+ (NSString *)stringFromDate:(NSDate *)date {
    return [[self shared] stringFromDate:date];
}

+ (NSDate *)dateFromString:(NSString *)string {
    return [[self shared] dateFromString:string];
}

+ (NSString *)formatNumber:(NSNumber *)number {
    return [[self shared] formatNumber:number];
}

+ (NSString *)stringFromFrame:(CGRect)frame {
    return [NSString stringWithFormat:@"{{%@, %@}, {%@, %@}}",[self formatNumber:@(frame.origin.x)],[self formatNumber:@(frame.origin.y)],[self formatNumber:@(frame.size.width)],[self formatNumber:@(frame.size.height)]];
}

#pragma mark - Primary
+ (instancetype)shared {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _instance = [[DoraemonHierarchyFormatterTool alloc] init];
    });
    return _instance;
}

- (NSString *)stringFromDate:(NSDate *)date {
    if (!date) {
        return nil;
    }
    return [self.formatter stringFromDate:date];
}

- (NSDate *)dateFromString:(NSString *)string {
    if (!string) {
        return nil;
    }
    return [self.formatter dateFromString:string];
}

- (NSString *)formatNumber:(NSNumber *)number {
    return [self.numberFormatter stringFromNumber:number];
}

#pragma mark - Getters and setters
- (NSDateFormatter *)formatter {
    if (!_formatter) {
        _formatter = [[NSDateFormatter alloc] init];
        _formatter.dateFormat = @"yyyy-MM-dd HH:mm:ss";
    }
    return _formatter;
}

- (NSNumberFormatter *)numberFormatter {
    if (!_numberFormatter) {
        _numberFormatter = [[NSNumberFormatter alloc] init];
        _numberFormatter.numberStyle = NSNumberFormatterDecimalStyle;
        _numberFormatter.maximumFractionDigits = 2;
        _numberFormatter.usesGroupingSeparator = NO;
    }
    return _numberFormatter;
}

@end
