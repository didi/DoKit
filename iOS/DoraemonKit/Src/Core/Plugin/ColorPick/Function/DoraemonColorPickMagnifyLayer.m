//
//  DoraemonColorPickMagnifyLayer.m
//  DoraemonKit
//
//  Created by wenquan on 2019/1/31.
//

#import "DoraemonColorPickMagnifyLayer.h"

#import <DoraemonKit/UIColor+Doraemon.h>

static CGFloat const kMagnifySize = 150; // 放大镜尺寸
static CGFloat const kRimThickness = 3.0; // 放大镜边缘的厚度
static NSInteger const kGridNum = 15; // 放大镜网格的数量
static NSInteger const kPixelSkip = 1; // 采集像素颜色时像素的间隔

@interface DoraemonColorPickMagnifyLayer ()

@property (nonatomic) struct CGPath *gridCirclePath;

@end

@implementation DoraemonColorPickMagnifyLayer

#pragma mark - Lifecycle

- (void)dealloc {
    if (_gridCirclePath) CGPathRelease(_gridCirclePath);
}

- (id)init {
    self = [super init];
    if (self) {
        self.bounds = CGRectMake(-kMagnifySize/2, -kMagnifySize/2, kMagnifySize, kMagnifySize);
        self.anchorPoint = CGPointMake(0.5, 1);
        
        UIImage *magnifyImage = [self magnifyImage];
        CALayer *magnifyLayer = [CALayer layer];
        magnifyLayer.bounds = self.bounds;
        magnifyLayer.position = CGPointMake(CGRectGetMidX(self.bounds), CGRectGetMidY(self.bounds));
        magnifyLayer.contents = (id)magnifyImage.CGImage;
        magnifyLayer.magnificationFilter = kCAFilterNearest;
        [self addSublayer:magnifyLayer];
    }
    return self;
}

#pragma mark - Override

- (void)drawInContext:(CGContextRef)ctx {
    // 对于内部的放大镜进行网格裁剪
    CGContextAddPath(ctx, self.gridCirclePath);
    CGContextClip(ctx);
    // 画网格
    [self drawGridInContext:ctx];
}

- (void)drawGridInContext:(CGContextRef)ctx {
    CGFloat gridSize = ceilf(kMagnifySize/kGridNum);
    
    // 由于锚点修改，这里需要偏移
    CGPoint currentPoint = self.targetPoint;
    currentPoint.x -= kGridNum*kPixelSkip/2;
    currentPoint.y -= kGridNum*kPixelSkip/2;
    NSInteger i,j;
    
    // 放大镜中画出网格，并使用当前点和周围点的颜色进行填充
    for (j=0; j<kGridNum; j++) {
        for (i=0; i<kGridNum; i++) {
            CGRect gridRect = CGRectMake(gridSize*i-kMagnifySize/2, gridSize*j-kMagnifySize/2, gridSize, gridSize);
            UIColor *gridColor = [UIColor clearColor];
            if (self.pointColorBlock) {
                NSString *pointColorHexString = self.pointColorBlock(currentPoint);
                gridColor = [UIColor doraemon_colorWithHexString:pointColorHexString];
            }
            CGContextSetFillColorWithColor(ctx, gridColor.CGColor);
            CGContextFillRect(ctx, gridRect);
            // 横向寻找下一个相邻点
            currentPoint.x += kPixelSkip;
        }
        // 一行绘制完毕，横向回归起始点，纵向寻找下一个点
        currentPoint.x -= kGridNum*kPixelSkip;
        currentPoint.y += kPixelSkip;
    }
}

#pragma mark - Private

- (UIImage *)magnifyImage {
    UIGraphicsBeginImageContextWithOptions(self.bounds.size, NO, 0);
    
    CGContextRef ctx = UIGraphicsGetCurrentContext();
    
    CGFloat size = kMagnifySize;
    CGContextTranslateCTM(ctx, size/2, size/2);
    
    // 绘制裁剪区域
    CGContextSaveGState(ctx);
    CGContextAddPath(ctx, self.gridCirclePath);
    CGContextClip(ctx);
    CGContextRestoreGState(ctx);
    
    // 绘制放大镜边缘
    CGContextSetLineWidth(ctx, kRimThickness);
    CGContextSetStrokeColorWithColor(ctx, [UIColor blackColor].CGColor);
    CGContextAddPath(ctx, self.gridCirclePath);
    CGContextStrokePath(ctx);
    
    // 绘制两条边缘线中间的内容
    CGContextSetLineWidth(ctx, kRimThickness-1);
    CGContextSetStrokeColorWithColor(ctx, [UIColor whiteColor].CGColor);
    CGContextAddPath(ctx, self.gridCirclePath);
    CGContextStrokePath(ctx);
    
    // 绘制中心的选择区域
    CGFloat gridWidth = ceilf(kMagnifySize/kGridNum);
    CGFloat xyOffset = -(gridWidth+1)/2;
    CGRect selectedRect = CGRectMake(xyOffset, xyOffset, gridWidth, gridWidth);
    CGContextAddRect(ctx, selectedRect);
    
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
    if (@available(iOS 13.0, *)) {
        UIColor *dyColor = [UIColor colorWithDynamicProvider:^UIColor * _Nonnull(UITraitCollection * _Nonnull trainCollection) {
            if ([trainCollection userInterfaceStyle] == UIUserInterfaceStyleLight) {
                return [UIColor blackColor];
            }
            else {
                return [UIColor whiteColor];
            }
        }];
        CGContextSetStrokeColorWithColor(ctx, dyColor.CGColor);
    } else {
#endif
        CGContextSetStrokeColorWithColor(ctx, [UIColor blackColor].CGColor);
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
    }
#endif
    CGContextSetLineWidth(ctx, 1.0);
    CGContextStrokePath(ctx);
    
    UIImage *image = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return image;
}

#pragma mark - Getter

- (struct CGPath *)gridCirclePath {
    if (_gridCirclePath == NULL) {
        CGMutablePathRef circlePath = CGPathCreateMutable();
        const CGFloat radius = kMagnifySize/2;
        CGPathAddArc(circlePath, nil, 0, 0, radius-kRimThickness/2, 0, 2*M_PI, YES);
        _gridCirclePath = circlePath;
    }
    return _gridCirclePath;
}

@end
