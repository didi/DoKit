//
//  PieChart.m
//  ccccc1111111
//
//  Created by 0xd on 2019/9/25.
//  Copyright © 2019 000. All rights reserved.
//

#import "PieChart.h"

@implementation PieChart

- (instancetype)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        self.itemDescriptionFont =  [UIFont systemFontOfSize:9];
        self.itemDescriptionTextColor = [UIColor blackColor];
        self.innerCircleRadiusRatio = 0.3;
        self.contentInset = UIEdgeInsetsMake(10, 10, 10, 10);
    }
    return self;
}

- (void)itemsChanged {
    [super itemsChanged];
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
    for (ChartDataItem *item in self.items) {
        sum += item.value;
    }
    CGPoint chartCenter = CGPointMake(self.center.x, CGRectGetHeight(self.bounds) - outerCircleRadius - self.contentInset.bottom);
    __block CGFloat lastestEndAngle = -M_PI_2;
    [self.items enumerateObjectsUsingBlock:^(ChartDataItem * _Nonnull item, NSUInteger idx, BOOL * _Nonnull stop) {
        CGFloat currentAngle = item.value / sum * M_PI * 2;
        CGFloat startAngle = lastestEndAngle;

        CGFloat endAngle = startAngle + currentAngle;
        
        CAShapeLayer *shapeLayer = [self shapeLayerRadius:outerCircleRadius
                                        innerCircleRadius:innerCircleRadius
                                                   center:chartCenter fillColor:item.color
                                               startAngle:startAngle endAngle:endAngle
                                                    value:item.value];
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
                             value:(double)value {

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
    textLayer.string = [self.vauleFormatter stringFromNumber:[NSNumber numberWithDouble:value]];
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
