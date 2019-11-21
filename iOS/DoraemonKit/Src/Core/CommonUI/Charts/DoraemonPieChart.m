//
//  PieChart.m
//  DoraemonKit
//
//  Created by 0xd on 2019/9/25.
//  Copyright © 2019 000. All rights reserved.
//

#import "DoraemonPieChart.h"

@implementation DoraemonPieChart

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
        self.itemDescriptionFont =  [UIFont systemFontOfSize:11];
        self.itemDescriptionTextColor = [UIColor whiteColor];
        self.innerCircleRadiusRatio = 0.3;
        self.contentInset = UIEdgeInsetsMake(10, 10, 10, 10);
    }
    return self;
}

- (void)display {
    [super display];
    [self setNeedsDisplay];
}

- (void)drawRect:(CGRect)rect {
    CGFloat chartFrameY = self.contentInset.top + self.titleLabel.frame.size.height;
    CGFloat chartFrameWidht = self.frame.size.width - self.contentInset.left - self.contentInset.right;
    CGFloat chartFrameHeight = self.frame.size.height - self.titleLabel.frame.size.height - self.contentInset.top - self.contentInset.bottom;
    CGRect chartFrame = CGRectMake(self.contentInset.left, chartFrameY, chartFrameWidht, chartFrameHeight);
    CGFloat outerCircleRadius = MIN(chartFrame.size.width, chartFrame.size.height) / 2;
    CGFloat innerCircleRadius = outerCircleRadius * self.innerCircleRadiusRatio;
    
    double sum = 0;
    for (DoraemonChartDataItem *item in self.items) {
        sum += item.value;
    }
    CGPoint chartCenter = CGPointMake(self.center.x, CGRectGetHeight(self.bounds) - outerCircleRadius - self.contentInset.bottom);
    __block CGFloat lastestEndAngle = -M_PI_2;
    [self.items enumerateObjectsUsingBlock:^(DoraemonChartDataItem * _Nonnull item, NSUInteger idx, BOOL * _Nonnull stop) {
        CGFloat currentAngle = item.value / sum * M_PI * 2;
        CGFloat startAngle = lastestEndAngle;

        CGFloat endAngle = startAngle + currentAngle;
        
        CAShapeLayer *shapeLayer = [self shapeLayerRadius:outerCircleRadius
                                        innerCircleRadius:innerCircleRadius
                                                   center:chartCenter
                                                fillColor:item.color
                                               startAngle:startAngle
                                                 endAngle:endAngle
                                                    name:item.name
                                                  precent:item.value / sum * 100];
        [self.layer addSublayer:shapeLayer];
        lastestEndAngle = endAngle;
    }];
    [self addInnerCircleWithRadius:innerCircleRadius center:chartCenter];
}

- (CAShapeLayer *)shapeLayerRadius:(CGFloat)radius
                 innerCircleRadius:(CGFloat)innerCircleRadius
                            center:(CGPoint)center
                         fillColor:(UIColor *)fillColor
                        startAngle:(CGFloat)startAngle
                          endAngle:(CGFloat)endAngle
                             name:(NSString *)name
                           precent:(CGFloat)precent {

    UIBezierPath *path = [UIBezierPath bezierPath];
    [path moveToPoint:center];
    [path addArcWithCenter:center radius:radius startAngle:startAngle endAngle:endAngle clockwise:YES];
    CAShapeLayer *shapeLayer = [CAShapeLayer layer];
    shapeLayer.path = path.CGPath;
    shapeLayer.fillColor = fillColor.CGColor;
    
    CATextLayer *textLayer = [CATextLayer layer];
    textLayer.frame = CGRectMake(0, 0, FLT_MAX, FLT_MAX);
    textLayer.font = (__bridge CFTypeRef _Nullable)self.itemDescriptionFont;
    textLayer.fontSize = self.itemDescriptionFont.pointSize;
    textLayer.foregroundColor = (__bridge CGColorRef _Nullable)self.itemDescriptionTextColor;
    textLayer.alignmentMode = kCAAlignmentCenter;
    NSString *precentText = [[self.vauleFormatter stringFromNumber:[NSNumber numberWithDouble:precent]] stringByAppendingString:@"%"];
    textLayer.string = [precentText stringByAppendingFormat:@"\n%@",name];
    textLayer.contentsScale = [UIScreen mainScreen].scale;
    // 计算文字中心点
    CGFloat textCenterAngle = startAngle + (endAngle - startAngle) / 2;
    CGSize textLayerSize = textLayer.preferredFrameSize;
    CGFloat textLayerOriginX = cos(textCenterAngle) * (radius + innerCircleRadius) / 2 + center.x - textLayerSize.width / 2;
    CGFloat textLayerOriginy = sin(textCenterAngle) * (radius + innerCircleRadius) / 2 + center.y - textLayerSize.height / 2;
    [shapeLayer addSublayer:textLayer];
    
    textLayer.frame = CGRectMake(textLayerOriginX, textLayerOriginy, textLayerSize.width, textLayerSize.height);
    return shapeLayer;
}

- (void)addInnerCircleWithRadius:(CGFloat)radius
                          center:(CGPoint)center {
    CAShapeLayer *shape = [CAShapeLayer layer];
    UIBezierPath *path = [UIBezierPath bezierPathWithArcCenter:center radius:radius startAngle:0 endAngle:(2 * M_PI) clockwise:YES];
    [path moveToPoint:center];
    shape.path = path.CGPath;
    shape.fillColor = [UIColor whiteColor].CGColor;
    [self.layer addSublayer:shape];
}

@end
