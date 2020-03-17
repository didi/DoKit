//
//  DoraemonHierarchyFormatterTool.h
//  DoraemonKit-DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//
#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonHierarchyFormatterTool : NSObject

/**
 Format date use style.

 @param date Date.
 @return Format string.
 */
+ (NSString *_Nullable)stringFromDate:(NSDate *)date;

/**
 Get date use formatted string use style

 @param string Formatted string.
 @return Date.
 */
+ (NSDate *_Nullable)dateFromString:(NSString *)string;

/**
 Format a CGFloat value with maximumFractionDigits = 2.

 @param number NSNumber.
 @return Format string.
 */
+ (NSString *)formatNumber:(NSNumber *)number;

/**
 Format print frame with maximumFractionDigits = 2.
 
 @param frame CGRect.
 @return Format string.
 */
+ (NSString *)stringFromFrame:(CGRect)frame;

@end

NS_ASSUME_NONNULL_END
