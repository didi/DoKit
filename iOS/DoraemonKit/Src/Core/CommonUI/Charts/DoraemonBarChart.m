//
//  BarChart.m
//  DoraemonKit
//
//  Created by 0xd on 2019/9/11.
//  Copyright © 2019 000. All rights reserved.
//

#import "DoraemonBarChart.h"

@implementation DoraemonBarChart

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        if (@available(iOS 13.0, *)) {
            self.backgroundColor = [UIColor colorWithDynamicProvider:^UIColor * _Nonnull(UITraitCollection * _Nonnull traitCollection) {
                if (traitCollection.userInterfaceStyle == UIUserInterfaceStyleDark) {
                    return [UIColor secondarySystemBackgroundColor];
                } else {
                    return [UIColor whiteColor];
                }
            }];
        } else {
#endif
            self.backgroundColor = [UIColor whiteColor];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        }
#endif
        self.xAxis = [[DoraemonXAxis alloc] init];
        self.yAxis = [[DoraemonYAxis alloc] init];
        self.yAxis.labelCount = 5;
        self.vauleFormatter = self.vauleFormatter;
        self.barsSpacingRatio = 1;
    }
    return self;
}

- (void)display {
    [super display];
    NSMutableArray<NSNumber *> *yAxisValues = [NSMutableArray arrayWithCapacity:self.items.count];
    for (DoraemonChartDataItem *entry in self.items) {
        [yAxisValues addObject: [NSNumber numberWithDouble:entry.value]];
    }
    self.yAxis.values = [yAxisValues copy];
    [self.yAxis update];
    [self setNeedsDisplay];
}

- (void)drawRect:(CGRect)rect {
    CGContextRef context = UIGraphicsGetCurrentContext();
    [self drawXAxisWithContext:context];
    [self drawYAxisWithContext:context];
    [self drawBarsWithContext:context];
}

- (void) drawXAxisWithContext:(CGContextRef)context {
    if (!self.xAxis) {
        return;
    }
    CGContextSaveGState(context);
    CGContextSetStrokeColorWithColor(context, self.xAxis.axisLineColor.CGColor);
    CGContextSetLineWidth(context, self.xAxis.axisLineWidth);
    CGFloat y = CGRectGetMaxY(self.bounds) - self.contentInset.bottom;
    CGContextMoveToPoint(context, self.contentInset.left, y);
    CGContextAddLineToPoint(context, CGRectGetMaxX(self.bounds) - self.contentInset.right, y);
    CGContextStrokePath(context);
    CGContextRestoreGState(context);
}

- (void)drawYAxisWithContext:(CGContextRef)context {
    if (!self.yAxis) {
        return;
    }
    CGContextSaveGState(context);
    CGFloat labelHeight = self.yAxis.labelFont.lineHeight;
    CGFloat yAxisHeight = self.bounds.size.height - self.contentInset.top - self.contentInset.bottom;
    
    CGContextSetStrokeColorWithColor(context, self.yAxis.axisLineColor.CGColor);
    CGContextSetLineWidth(context, self.yAxis.axisLineWidth);
    CGContextMoveToPoint(context, self.contentInset.left, self.contentInset.top);
    CGContextAddLineToPoint(context, self.contentInset.left, self.contentInset.top + yAxisHeight);
    CGContextStrokePath(context);
    
   
    CGFloat spacing = (yAxisHeight - self.yAxis.marginTop) / (self.yAxis.labels.count - 1);

    NSArray<NSString *> *labels = [self.yAxis.labels.reverseObjectEnumerator allObjects];
    // Render Grid Lines
    for (NSUInteger i = 1; i < labels.count; i++) {
        CGFloat dashes[] = {2,2};
        CGFloat y = self.contentInset.top + self.yAxis.marginTop + spacing * (i - 1);
        CGContextMoveToPoint(context, self.contentInset.left, y);
        CGContextAddLineToPoint(context, self.bounds.size.width - self.contentInset.right, y);
        CGContextSetLineDash(context, 0.0, dashes, 2);
        CGContextStrokePath(context);
    }
    
    // draw x axis value labels
    for (NSUInteger i = 0; i < labels.count; i++) {
        CGFloat y = self.contentInset.top + self.yAxis.marginTop + spacing * i;
        NSString *text = labels[i];
        UIColor *attColor = [UIColor blackColor];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        if (@available(iOS 13.0, *)) {
            attColor = [UIColor labelColor];
        }
#endif
        NSDictionary *attributes = @{NSFontAttributeName: self.yAxis.axisLabelFont, NSForegroundColorAttributeName: attColor};
        CGSize size = [text sizeWithAttributes:attributes];
        
        CGFloat labelX = MAX(self.contentInset.left - size.width, 0);
        CGRect rect = CGRectMake(labelX - 10, y - labelHeight / 2, self.contentInset.left - 1, labelHeight);
         
        [labels[i] drawInRect:rect withAttributes:attributes];
    }
    
    CGContextRestoreGState(context);
}

- (void)drawBarsWithContext:(CGContextRef)context {
    if (!self.items || self.items.count == 0) {
        return;
    }
    CGFloat barWidth = (self.bounds.size.width - self.contentInset.left - self.contentInset.right) / (self.items.count * (1 + self.barsSpacingRatio) + self.barsSpacingRatio);
    
    for (NSUInteger i = 0; i < self.items.count; i++) {
        DoraemonChartDataItem *item = self.items[i];
        CGFloat yAxisHeight = self.bounds.size.height - self.contentInset.top - self.contentInset.bottom - self.yAxis.marginTop;
        CGFloat height = item.value / self.yAxis.maxY * yAxisHeight;
        CGFloat x = barWidth * i + self.contentInset.left + (barWidth * self.barsSpacingRatio) * (i + 1);
        CGFloat y = self.bounds.size.height - height - self.contentInset.bottom;
        CAShapeLayer *shapeLayer = [[CAShapeLayer alloc] init];
        shapeLayer.frame = CGRectMake(x, y, barWidth, height);
        shapeLayer.backgroundColor = item.color.CGColor;
        [self.layer addSublayer:shapeLayer];
        
        NSString *value = [self.vauleFormatter stringFromNumber: [NSNumber numberWithDouble:item.value]];
        UIColor *attColor = [UIColor blackColor];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        if (@available(iOS 13.0, *)) {
            attColor = [UIColor labelColor];
        }
#endif
        NSDictionary *attributes = @{NSFontAttributeName: self.yAxis.axisLabelFont, NSForegroundColorAttributeName: attColor};
        CGSize valueLabelSize = [value sizeWithAttributes:attributes];
        CGFloat valueLabelOffSetX = (barWidth - valueLabelSize.width) / 2;
        CGRect valueRect = CGRectMake(x + valueLabelOffSetX, y - valueLabelSize.height - 5, valueLabelSize.width, valueLabelSize.height);
        [value drawInRect:valueRect withAttributes:attributes];
        
        CGSize descLabelSize = [item.name sizeWithAttributes:attributes];
        CGRect descRect = CGRectMake(x + valueLabelOffSetX, CGRectGetMaxY(shapeLayer.frame) + 10, descLabelSize.width, descLabelSize.height);
        [item.name drawInRect:descRect withAttributes:attributes];
    }
}

@end
